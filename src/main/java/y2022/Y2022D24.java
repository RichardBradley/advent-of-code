package y2022;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;
import lombok.Value;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2022D24 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        List<String> input = Resources.readLines(Resources.getResource("y2022/Y2022D24.txt"), StandardCharsets.UTF_8);

        // 1
        assertThat(part1(example)).isEqualTo(18);
        System.out.println("Example 1 OK");
        System.out.println(part1(input));

        // 2
        assertThat(part2(example)).isEqualTo(54);
        System.out.println(part2(input));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    @Value
    private static class SearchState implements Comparable<SearchState> {
        Point location;
        int time;
        private final int searchHeuristicCost;

        public SearchState(Point location, int time, World world) {
            this.location = location;
            this.time = time;

            this.searchHeuristicCost = time + minMovesToFinish(location, world.end);
        }

        private int minMovesToFinish(Point location, Point end) {
            // To be admissable, must not over-estimate the dist to finish
            return Math.abs(location.x - end.x) + Math.abs(location.y - end.y);
        }

        @Override
        public int compareTo(SearchState that) {
            return Integer.compare(this.searchHeuristicCost, that.searchHeuristicCost);
        }

        public void visitPossibleMoves(World world, Consumer<SearchState> visitor) {
            int nextTime = time + 1;
            for (Point dir : dirs) {
                Point nextLoc = new Point(location.x + dir.x, location.y + dir.y);
                if (world.isClear(nextLoc, nextTime)) {
                    visitor.accept(new SearchState(nextLoc, nextTime, world));
                }
            }
            if (world.isClear(location, nextTime)) {
                visitor.accept(new SearchState(location, nextTime, world));
            }
        }
    }

    static Point[] dirs = new Point[]{
            new Point(0, -1),
            new Point(1, 0),
            new Point(0, 1),
            new Point(-1, 0)
    };

    private static int part1(List<String> input) {
        World world = new World(input);
        return minTime(world, 0, world.start, world.end);
    }

    private static int part2(List<String> input) {
        World world = new World(input);
        int tFirstAtEnd = minTime(world, 0, world.start, world.end);
        int tReturnToStart = minTime(world, tFirstAtEnd, world.end, world.start);
        return minTime(world, tReturnToStart, world.start, world.end);
    }

    private static int minTime(World world, int time, Point start, Point end) {
        // A*
        PriorityQueue<SearchState> queue = new PriorityQueue<>();
        Set<SearchState> visited = new HashSet<>();
        queue.add(new SearchState(start, time, world));
        visited.add(queue.peek());

        while (true) {
            SearchState state = queue.poll();
            checkNotNull(state);

            if (state.location.equals(end)) {
                return state.time;
            }

            state.visitPossibleMoves(world, next -> {
                if (visited.add(next)) {
                    queue.add(next);
                }
            });
        }
    }

    @Value
    private static class World {
        int width;
        int height;
        Point start;
        Point end;
        Set<Point> northMovingBlizzardStarts = new HashSet<>();
        Set<Point> eastMovingBlizzardStarts = new HashSet<>();
        Set<Point> southMovingBlizzardStarts = new HashSet<>();
        Set<Point> westMovingBlizzardStarts = new HashSet<>();

        public World(List<String> input) {
            width = input.get(0).length();
            checkState(input.stream().allMatch(line -> line.length() == width));
            start = new Point(1, 0);
            checkState(input.get(start.y).charAt(start.x) == '.');
            height = input.size();
            end = new Point(width - 2, height - 1);
            checkState(input.get(end.y).charAt(end.x) == '.');

            for (int y = 0; y < height; y++) {
                String line = input.get(y);
                for (int x = 0; x < width; x++) {
                    switch (line.charAt(x)) {
                        case '#':
                        case '.':
                            continue;
                        case '^':
                            northMovingBlizzardStarts.add(new Point(x, y));
                            break;
                        case '>':
                            eastMovingBlizzardStarts.add(new Point(x, y));
                            break;
                        case '<':
                            westMovingBlizzardStarts.add(new Point(x, y));
                            break;
                        case 'v':
                            southMovingBlizzardStarts.add(new Point(x, y));
                            break;
                        default:
                            throw new IllegalArgumentException("for: " + line.charAt(x));
                    }
                }
            }
        }

        boolean isClear(Point p, int time) {
            if (p.x <= 0 || p.x >= width - 1 || p.y <= 0 || p.y >= height - 1) {
                return start.equals(p) || end.equals(p);
            }
            // translate position back to start and compare to bliz starts
            int correspondingStartY = wrapY(p.y + time);
            if (northMovingBlizzardStarts.contains(new Point(p.x, correspondingStartY))) {
                return false;
            }
            if (southMovingBlizzardStarts.contains(new Point(p.x, wrapY(p.y - time)))) {
                return false;
            }
            if (eastMovingBlizzardStarts.contains(new Point(wrapX(p.x - time), p.y))) {
                return false;
            }
            if (westMovingBlizzardStarts.contains(new Point(wrapX(p.x + time), p.y))) {
                return false;
            }
            return true;
        }

        private int wrapY(int y) {
            return mod(y - 1, height - 2) + 1;
        }

        private int wrapX(int x) {
            return mod(x - 1, width - 2) + 1;
        }
    }

    private static int mod(int a, int m) {
        int ret = a % m;
        if (ret < 0) {
            ret += m;
            checkState(ret > 0);
        }
        return ret;
    }

    private static List<String> example = List.of(
            "#.######",
            "#>>.<^<#",
            "#.<..<<#",
            "#>v.><>#",
            "#<^v^^>#",
            "######.#");
}

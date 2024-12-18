package y2024;

import com.google.common.base.Stopwatch;
import lombok.Value;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static aoc.Common.loadInputFromResources;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2024D18 {

    public static void main(String[] args) throws Exception {
        List<String> input = loadInputFromResources();
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(part1(input, 1024)).isEqualTo(318);

        // 2
        assertThat(part2(input)).isEqualTo("56,29");

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static long part1(List<String> input, int time) {
        Set<Point> walls = new HashSet<>();
        Pattern p = Pattern.compile("(\\d+),(\\d+)");
        for (int i = 0; i < time; i++) {
            Matcher m = p.matcher(input.get(i));
            checkState(m.matches());
            walls.add(new Point(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2))));
        }

        Point start = new Point(0, 0);
        int width = 71;
        int height = 71;
        Point end = new Point(width - 1, height - 1);

        // Dijkstra:
        Map<Point, Integer> minCostToPoint = new HashMap<>();
        PriorityQueue<State> queue = new PriorityQueue<>();
        queue.add(new State(0, start));

        Set<Point> pointsOnBestPath = new HashSet<>();

        State current;
        while (null != (current = queue.poll())) {
            if (current.loc.equals(end)) {
                return current.cost;
            }

            for (State next : current.nextSteps(walls, width, height)) {
                minCostToPoint.compute(next.getLoc(), (k, oldDist) -> {
                    if (oldDist == null || oldDist > next.cost) {
                        queue.add(next);
                        return next.cost;
                    }
                    return oldDist;
                });
            }
        }
        throw new IllegalStateException();
    }

    @Value
    static class State implements Comparable<State> {
        int cost;
        Point loc;

        @Override
        public int compareTo(State that) {
            return Integer.compare(this.cost, that.cost);
        }

        public List<State> nextSteps(Set<Point> walls, int width, int height) {
            List<State> acc = new ArrayList<>(4);
            for (Point dir : dirs) {
                Point next = add(loc, dir);
                if (next.x >= 0 && next.x < width && next.y >= 0 && next.y < height) {
                    if (!walls.contains(next)) {
                        acc.add(new State(cost + 1, next));
                    }
                }
            }
            return acc;
        }
    }

    static Point[] dirs = new Point[]{
            new Point(0, -1), // N
            new Point(1, 0), // E
            new Point(0, 1), // S
            new Point(-1, 0) // W
    };

    private static Point add(Point a, Point b) {
        return new Point(a.x + b.x, a.y + b.y);
    }

    private static String part2(List<String> input) {
        for (int t = 0; ; t++) {
            try {
                part1(input, t);
            } catch (Exception e) {
                return input.get(t - 1);
            }
        }
    }
}

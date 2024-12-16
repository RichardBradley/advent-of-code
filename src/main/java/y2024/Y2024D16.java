package y2024;

import com.google.common.base.Stopwatch;
import lombok.Value;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static aoc.Common.loadInputFromResources;
import static com.google.common.truth.Truth.assertThat;

public class Y2024D16 {

    public static void main(String[] args) throws Exception {
        List<String> input = loadInputFromResources();
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(run(example, false)).isEqualTo(7036);
        assertThat(run(input, false)).isEqualTo(99488);

        // 2
        assertThat(run(example, true)).isEqualTo(45);
        assertThat(run(input, true)).isEqualTo(516);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    @Value
    static class PathLoc {
        Point position;
        int dir;
    }

    static Point[] dirs = new Point[]{
            new Point(0, -1), // N
            new Point(1, 0), // E
            new Point(0, 1), // S
            new Point(-1, 0) // W
    };

    @Value
    static class State implements Comparable<State> {
        int cost;
        PathLoc loc;

        @Override
        public int compareTo(State that) {
            return Integer.compare(this.cost, that.cost);
        }

        public List<State> nextSteps(List<String> input) {
            List<State> acc = new ArrayList<>(3);
            // continue forward
            {
                Point next = add(loc.position, dirs[loc.dir]);
                char nextC = get(input, next);
                if (nextC == 'E' || nextC == '.') {
                    acc.add(new State(cost + 1, new PathLoc(next, loc.dir)));
                }
            }
            // turn left, right
            acc.add(new State(cost + 1000, new PathLoc(loc.position, (loc.dir + 1) % 4)));
            acc.add(new State(cost + 1000, new PathLoc(loc.position, (loc.dir + 3) % 4)));
            return acc;
        }
    }

    private static char get(List<String> map, Point p) {
        return map.get(p.y).charAt(p.x);
    }

    private static Point add(Point a, Point b) {
        return new Point(a.x + b.x, a.y + b.y);
    }

    private static long run(List<String> input, boolean isPart2) {
        Point start = find(input, 'S');
        Point end = find(input, 'E');

        // Dijkstra:
        Map<PathLoc, Integer> minCostToPoint = new HashMap<>();
        PriorityQueue<State> queue = new PriorityQueue<>();
        queue.add(new State(0, new PathLoc(start, 1)));

        Set<Point> pointsOnBestPath = new HashSet<>();

        State current;
        while (null != (current = queue.poll())) {
            if (current.loc.position.equals(end)) {
                if (!isPart2) {
                    return current.cost;
                } else {
                    traceToStart(minCostToPoint, current, pointsOnBestPath);
                }
            }

            for (State next : current.nextSteps(input)) {
                minCostToPoint.compute(next.getLoc(), (k, oldDist) -> {
                    if (oldDist == null || oldDist > next.cost) {
                        queue.add(next);
                        return next.cost;
                    }
                    return oldDist;
                });
            }
        }

        if (isPart2) {
            return pointsOnBestPath.size();
        }
        throw new IllegalStateException();
    }

    private static void traceToStart(
            Map<PathLoc, Integer> minCostToPoint,
            State curr,
            Set<Point> pointsOnBestPath) {

        pointsOnBestPath.add(curr.loc.position);
        // continue forward
        {
            State prev = new State(
                    curr.cost - 1,
                    new PathLoc(
                            add(curr.loc.position, dirs[(curr.loc.dir + 2) % 4]),
                            curr.loc.dir));
            if (Objects.equals(minCostToPoint.get(prev.loc), prev.cost)) {
                traceToStart(minCostToPoint, prev, pointsOnBestPath);
            }
        }
        // turn left, right
        {
            State prev = new State(
                    curr.cost - 1000,
                    new PathLoc(
                            curr.loc.position,
                            (curr.loc.dir + 1) % 4));
            if (Objects.equals(minCostToPoint.get(prev.loc), prev.cost)) {
                traceToStart(minCostToPoint, prev, pointsOnBestPath);
            }
        }
        {
            State prev = new State(
                    curr.cost - 1000,
                    new PathLoc(
                            curr.loc.position,
                            (curr.loc.dir + 3) % 4));
            if (Objects.equals(minCostToPoint.get(prev.loc), prev.cost)) {
                traceToStart(minCostToPoint, prev, pointsOnBestPath);
            }
        }
    }

    private static Point find(List<String> input, char target) {
        for (int y = 0; y < input.size(); y++) {
            int x = input.get(y).indexOf(target);
            if (x >= 0) {
                return new Point(x, y);
            }
        }
        throw new IllegalArgumentException();
    }

    static List<String> example = List.of(
            "###############",
            "#.......#....E#",
            "#.#.###.#.###.#",
            "#.....#.#...#.#",
            "#.###.#####.#.#",
            "#.#.#.......#.#",
            "#.#.#####.###.#",
            "#...........#.#",
            "###.#.#####.#.#",
            "#...#.....#.#.#",
            "#.#.#.###.#.#.#",
            "#.....#...#.#.#",
            "#.###.#.#.#.#.#",
            "#S..#.....#...#",
            "###############");
}

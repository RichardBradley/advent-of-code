package y2023;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;
import lombok.Value;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

public class Y2023D17 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            List<String> input = Resources.readLines(Resources.getResource("y2023/Y2023D17.txt"), StandardCharsets.UTF_8);

            // 1
            assertThat(findLeastCostPath(example, false)).isEqualTo(102);
            assertThat(findLeastCostPath(input, false)).isEqualTo(1128);

            // 2
            assertThat(findLeastCostPath(example, true)).isEqualTo(94);
            assertThat(findLeastCostPath(input, true)).isEqualTo(1268);

        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    enum Dirs {
        N, E, S, W;

        public Point advance(Point p) {
            switch (this) {
                case N:
                    return new Point(p.x, p.y - 1);
                case E:
                    return new Point(p.x + 1, p.y);
                case S:
                    return new Point(p.x, p.y + 1);
                case W:
                    return new Point(p.x - 1, p.y);
                default:
                    throw new IllegalStateException();
            }
        }

        public Dirs turnRight() {
            switch (this) {
                case N:
                    return E;
                case E:
                    return S;
                case S:
                    return W;
                case W:
                    return N;
                default:
                    throw new IllegalStateException();
            }
        }

        public Dirs turnLeft() {
            switch (this) {
                case N:
                    return W;
                case E:
                    return N;
                case S:
                    return E;
                case W:
                    return S;
                default:
                    throw new IllegalStateException();
            }
        }
    }

    @Value
    static class PathLoc {
        Point position;
        Dirs dir;
        int stepsInThisDir; // 1 at first
    }

    @Value
    static class State implements Comparable<State> {
        int cost;
        PathLoc loc;

        public Iterable<State> nextSteps(List<String> map, boolean isPart2) {
            // Part2:
            // Once an ultra crucible starts moving in a direction, it needs to move a minimum of
            // four blocks in that direction before it can turn (or even before it can stop at the end).
            // However, it will eventually start to get wobbly: an ultra crucible can move a
            // maximum of ten consecutive blocks without turning.

            List<State> acc = new ArrayList<>(3);
            // could turn left:
            if (!isPart2 || loc.stepsInThisDir >= 4) {
                Dirs nextDir = loc.dir.turnLeft();
                Point nextPos = nextDir.advance(loc.position);
                int nextCost = getMapCostAt(map, nextPos);
                if (nextCost != -1) {
                    acc.add(new State(cost + nextCost, new PathLoc(nextPos, nextDir, 1)));
                }
            }
            // could turn right:
            if (!isPart2 || loc.stepsInThisDir >= 4) {
                Dirs nextDir = loc.dir.turnRight();
                Point nextPos = nextDir.advance(loc.position);
                int nextCost = getMapCostAt(map, nextPos);
                if (nextCost != -1) {
                    acc.add(new State(cost + nextCost, new PathLoc(nextPos, nextDir, 1)));
                }
            }
            // could go straight, if allowed
            if (loc.stepsInThisDir < (isPart2 ? 10 : 3)) {
                Point nextPos = loc.dir.advance(loc.position);
                int nextCost = getMapCostAt(map, nextPos);
                if (nextCost != -1) {
                    acc.add(new State(cost + nextCost, new PathLoc(nextPos, loc.dir, loc.stepsInThisDir + 1)));
                }
            }
            return acc;
        }

        @Override
        public int compareTo(State that) {
            return Integer.compare(this.cost, that.cost);
        }
    }

    static int getMapCostAt(List<String> map, Point pos) {
        if (pos.y < 0 || pos.y >= map.size()) {
            return -1;
        }
        String row = map.get(pos.y);
        if (pos.x < 0 || pos.x >= row.length()) {
            return -1;
        }
        return row.charAt(pos.x) - '0';
    }

    private static long findLeastCostPath(List<String> input, boolean isPart2) {
        // Dijkstra:
        Map<PathLoc, Integer> minCostToPoint = new HashMap<>();
        PriorityQueue<State> queue = new PriorityQueue<>();
        queue.add(new State(0, new PathLoc(new Point(0, 0), Dirs.E, 0)));
        queue.add(new State(0, new PathLoc(new Point(0, 0), Dirs.S, 0)));

        // Safest to exhaust the queue, as there are multiple valid end states due to the dir
        State current;
        while (null != (current = queue.poll())) {
            for (State next : current.nextSteps(input, isPart2)) {
                minCostToPoint.compute(next.getLoc(), (k, oldDist) -> {
                    if (oldDist == null || oldDist > next.cost) {
                        queue.add(next);
                        return next.cost;
                    }
                    return oldDist;
                });
            }
        }

        Point target = new Point(
                input.get(0).length() - 1,
                input.size() - 1);

        return minCostToPoint.entrySet().stream()
                .filter(e -> e.getKey().position.equals(target))
                .filter(e -> {
                    if (isPart2) {
                        // it needs to move a minimum of four blocks in that
                        // direction before it can turn (or even before it can stop at the end)
                        return e.getKey().stepsInThisDir >= 4;
                    } else {
                        return true;
                    }
                })
                .mapToInt(e -> e.getValue())
                .min()
                .getAsInt();
    }

    static List<String> example = List.of(
            "2413432311323",
            "3215453535623",
            "3255245654254",
            "3446585845452",
            "4546657867536",
            "1438598798454",
            "4457876987766",
            "3637877979653",
            "4654967986887",
            "4564679986453",
            "1224686865563",
            "2546548887735",
            "4322674655533"
    );
}

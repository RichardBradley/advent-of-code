package y2024;

import com.google.common.base.Stopwatch;
import lombok.Value;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static aoc.Common.loadInputFromResources;
import static com.google.common.truth.Truth.assertThat;

public class Y2024D20 {

    public static void main(String[] args) throws Exception {
        List<String> input = loadInputFromResources();
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(countCheats(example, 1, 2)).isEqualTo(44);
        assertThat(countCheats(input, 100, 2)).isEqualTo(1409);

        // 2
        assertThat(countCheats(example, 50, 20)).isEqualTo(285);
        assertThat(countCheats(input, 100, 20)).isEqualTo(1012821);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static long countCheats(List<String> input, int minSavingVal, int maxCheatDuration) {

        Point start = find(input, 'S');
        int width = input.size();
        int height = input.get(0).length();

        // Dijkstra:
        Map<Point, Integer> minCostToPoint = new HashMap<>();
        PriorityQueue<State> queue = new PriorityQueue<>();
        queue.add(new State(0, start));
        minCostToPoint.put(start, 0);

        State current;
        while (null != (current = queue.poll())) {
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

        int cheatCount = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Point from = new Point(x, y);
                char fromC = get(input, from);
                if (fromC == '.' || fromC == 'S') {
                    for (int dx = -maxCheatDuration; dx <= maxCheatDuration; dx++) {
                        int maxDy = maxCheatDuration - Math.abs(dx);
                        for (int dy = -maxDy; dy <= maxDy; dy++) {
                            if (dx == 0 && dy == 0) {
                                continue;
                            }

                            int cheatDuration = Math.abs(dx) + Math.abs(dy);
                            Point to = new Point(x + dx, y + dy);
                            char toC = get(input, to);
                            if (toC == '.' || toC == 'E') {
                                Integer endCheatCost = minCostToPoint.get(to);
                                Integer startCheatCost = minCostToPoint.get(from);
                                if (endCheatCost != null && startCheatCost != null) {
                                    int saving = endCheatCost - startCheatCost - cheatDuration;
                                    if (saving >= minSavingVal) {
                                        cheatCount++;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return cheatCount;
    }

    private static Point find(List<String> input, char c) {
        for (int y = 0; y < input.size(); y++) {
            String line = input.get(y);
            int x = line.indexOf(c);
            if (x >= 0) {
                return new Point(x, y);
            }
        }
        throw new IllegalArgumentException();
    }

    @Value
    static class State implements Comparable<State> {
        int cost;
        Point loc;

        @Override
        public int compareTo(State that) {
            return Integer.compare(this.cost, that.cost);
        }

        public List<State> nextSteps(List<String> map) {
            List<State> acc = new ArrayList<>(4);
            for (Point dir : dirs) {
                Point next = add(loc, dir);
                char nextC = get(map, next);
                if (nextC == '.' || nextC == 'E') {
                    acc.add(new State(cost + 1, next));
                }
            }
            return acc;
        }
    }

    private static char get(List<String> map, Point p) {
        if (p.y >= 0 && p.y < map.size()) {
            String line = map.get(p.y);
            if (p.x >= 0 && p.x < line.length()) {
                return line.charAt(p.x);
            }
        }
        return '\0';
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

    static List<String> example = List.of(
            "###############",
            "#...#...#.....#",
            "#.#.#.#.#.###.#",
            "#S#...#.#.#...#",
            "#######.#.#.###",
            "#######.#.#...#",
            "#######.#.###.#",
            "###..E#...#...#",
            "###.#######.###",
            "#...###...#...#",
            "#.#####.#.###.#",
            "#.#...#.#.#...#",
            "#.#.#.#.#.#.###",
            "#...#...#...###",
            "###############");
}

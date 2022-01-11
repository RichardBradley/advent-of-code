package y2019;

import com.google.common.base.Stopwatch;
import lombok.Value;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.System.out;

public class Y2019D24 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            // 1
            assertThat(part1(example1)).isEqualTo(2129920);
            assertThat(part1(input)).isEqualTo(2130474);

            // 2
            assertThat(new MapPoint(3, 3, 0).getAdjacent()).hasSize(4);
            assertThat(new MapPoint(1, 1, 1).getAdjacent()).hasSize(4);
            assertThat(new MapPoint(3, 2, 0).getAdjacent()).hasSize(8);
            assertThat(new MapPoint(3, 2, 0).getAdjacent()).hasSize(8);
            assertThat(part2(example1, 10)).isEqualTo(99);
            assertThat(part2(input, 200)).isEqualTo(1923);
        } finally {
            out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private static int part2(String initialState, int steps) {
        Set<MapPoint> state = new HashSet<>();
        {
            int y = 0;
            for (String line : initialState.split("\n")) {
                for (int x = 0; x < line.length(); x++) {
                    if (line.charAt(x) == '#') {
                        state.add(new MapPoint(x, y, 0));
                    }
                }
                y++;
            }
        }
        int minDepth = 0;
        int maxDepth = 0;

        for (int step = 0; step < steps; step++) {
            Set<MapPoint> next = new HashSet<>();
            for (int depth = minDepth - 1; depth <= maxDepth + 1; depth++) {
                for (int y = 0; y < 5; y++) {
                    for (int x = 0; x < 5; x++) {
                        if (x == 2 && y == 2) {
                            continue; // centre square is not a real square
                        }
                        MapPoint curr = new MapPoint(x, y, depth);
                        int adjacentCount = 0;
                        for (MapPoint adj : curr.getAdjacent()) {
                            if (state.contains(adj)) {
                                adjacentCount++;
                            }
                        }

                        if (state.contains(curr)) {
                            // A bug dies (becoming an empty space) unless there is exactly one bug adjacent to it
                            if (adjacentCount == 1) {
                                next.add(curr);
                                minDepth = Math.min(minDepth, depth);
                                maxDepth = Math.max(maxDepth, depth);
                            }
                        } else {
                            // An empty space becomes infested with a bug if exactly one or two bugs are adjacent to it.
                            if (adjacentCount == 1 || adjacentCount == 2) {
                                next.add(curr);
                                minDepth = Math.min(minDepth, depth);
                                maxDepth = Math.max(maxDepth, depth);
                            }
                        }
                    }
                }
            }
            state = next;
        }

        return state.size();
    }

    @Value
    static class MapPoint {
        int x;
        int y;
        int depth;

        public List<MapPoint> getAdjacent() {
            List<MapPoint> acc = new ArrayList<>();
            // left:
            if (x == 0) {
                acc.add(new MapPoint(1, 2, depth - 1));
            } else if (x == 3 && y == 2) {
                acc.add(new MapPoint(4, 0, depth + 1));
                acc.add(new MapPoint(4, 1, depth + 1));
                acc.add(new MapPoint(4, 2, depth + 1));
                acc.add(new MapPoint(4, 3, depth + 1));
                acc.add(new MapPoint(4, 4, depth + 1));
            } else {
                acc.add(new MapPoint(x - 1, y, depth));
            }
            // right:
            if (x == 4) {
                acc.add(new MapPoint(3, 2, depth - 1));
            } else if (x == 1 && y == 2) {
                acc.add(new MapPoint(0, 0, depth + 1));
                acc.add(new MapPoint(0, 1, depth + 1));
                acc.add(new MapPoint(0, 2, depth + 1));
                acc.add(new MapPoint(0, 3, depth + 1));
                acc.add(new MapPoint(0, 4, depth + 1));
            } else {
                acc.add(new MapPoint(x + 1, y, depth));
            }
            // up:
            if (y == 0) {
                acc.add(new MapPoint(2, 1, depth - 1));
            } else if (y == 3 && x == 2) {
                acc.add(new MapPoint(0, 4, depth + 1));
                acc.add(new MapPoint(1, 4, depth + 1));
                acc.add(new MapPoint(2, 4, depth + 1));
                acc.add(new MapPoint(3, 4, depth + 1));
                acc.add(new MapPoint(4, 4, depth + 1));
            } else {
                acc.add(new MapPoint(x, y - 1, depth));
            }
            // down:
            if (y == 4) {
                acc.add(new MapPoint(2, 3, depth - 1));
            } else if (y == 1 && x == 2) {
                acc.add(new MapPoint(0, 0, depth + 1));
                acc.add(new MapPoint(1, 0, depth + 1));
                acc.add(new MapPoint(2, 0, depth + 1));
                acc.add(new MapPoint(3, 0, depth + 1));
                acc.add(new MapPoint(4, 0, depth + 1));
            } else {
                acc.add(new MapPoint(x, y + 1, depth));
            }

            return acc;
        }
    }

    private static int part1(String initialState) {
        Set<Point> state = new HashSet<>();
        {
            int y = 0;
            for (String line : initialState.split("\n")) {
                for (int x = 0; x < line.length(); x++) {
                    if (line.charAt(x) == '#') {
                        state.add(new Point(x, y));
                    }
                }
                y++;
            }
        }
        Set<Set<Point>> seenStates = new HashSet<>();
        seenStates.add(state);
        while (true) {
            state = step(state);
            if (!seenStates.add(state)) {
                return getBiodiversity(state);
            }
        }
    }

    private static int getBiodiversity(Set<Point> state) {
        int val = 1;
        int acc = 0;
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                if (state.contains(new Point(x, y))) {
                    acc += val;
                }

                val <<= 1;
            }
        }
        return acc;
    }

    static Point[] dirs = new Point[]{
            new Point(0, -1),
            new Point(1, 0),
            new Point(0, 1),
            new Point(-1, 0)
    };

    private static Set<Point> step(Set<Point> state) {
        Set<Point> next = new HashSet<>();
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                int adjacentCount = 0;
                for (Point dir : dirs) {
                    if (state.contains(new Point(x + dir.x, y + dir.y))) {
                        adjacentCount++;
                    }
                }

                Point curr = new Point(x, y);
                if (state.contains(curr)) {
                    // A bug dies (becoming an empty space) unless there is exactly one bug adjacent to it
                    if (adjacentCount == 1) {
                        next.add(curr);
                    }
                } else {
                    // An empty space becomes infested with a bug if exactly one or two bugs are adjacent to it.
                    if (adjacentCount == 1 || adjacentCount == 2) {
                        next.add(curr);
                    }
                }
            }
        }

        return next;
    }

    private static String example1 = "....#\n" +
            "#..#.\n" +
            "#..##\n" +
            "..#..\n" +
            "#....";

    private static String input = "..#..\n" +
            "##..#\n" +
            "##...\n" +
            "#####\n" +
            ".#.##";
}

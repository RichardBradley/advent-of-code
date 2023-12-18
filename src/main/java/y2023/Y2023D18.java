package y2023;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;
import lombok.Value;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Queue;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;
import static java.lang.Math.addExact;
import static java.lang.Math.multiplyExact;

public class Y2023D18 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            List<String> input = Resources.readLines(Resources.getResource("y2023/Y2023D18.txt"), StandardCharsets.UTF_8);

            // 1
            assertThat(new Part1().part1(example)).isEqualTo(62);
            assertThat(new Part1().part1(input)).isEqualTo(36725);

            // 2
            assertThat(part2part1(example)).isEqualTo(62);
            assertThat(part2(example)).isEqualTo(952408144115L);
            assertThat(part2(input)).isEqualTo(97874103749720L);

        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    static class Part1 {

        Set<Point> dugPoints = new HashSet<>();
        Set<Point> interiorPoints = new HashSet<>();
        Set<Point> exteriorPoints = new HashSet<>();
        int minX = 0, maxX = 0, minY = 0, maxY = 0;

        private long part1(List<String> input) {
            Point currLoc = new Point(0, 0);
            dugPoints.add(currLoc);
            Pattern inputPatt = Pattern.compile("([UDLR]) (\\d+) \\(#([0-9a-f]+)\\)");
            for (String line : input) {
                Matcher m = inputPatt.matcher(line);
                checkState(m.matches());
                int dist = Integer.parseInt(m.group(2));
                Point delta;
                switch (m.group(1)) {
                    case "U":
                        delta = new Point(0, -1);
                        break;
                    case "D":
                        delta = new Point(0, 1);
                        break;
                    case "R":
                        delta = new Point(1, 0);
                        break;
                    case "L":
                        delta = new Point(-1, 0);
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
                for (int i = 0; i < dist; i++) {
                    currLoc = new Point(currLoc.x + delta.x, currLoc.y + delta.y);
                    dugPoints.add(currLoc);
                }

                minX = Math.min(minX, currLoc.x);
                maxX = Math.max(maxX, currLoc.x);
                minY = Math.min(minY, currLoc.y);
                maxY = Math.max(maxY, currLoc.y);
            }

            // dig out the interior
            for (int y = minY; y <= maxY; y++) {
                for (int x = minX; x <= maxX; x++) {
                    Point p = new Point(x, y);
                    if (!dugPoints.contains(p)) {
                        floodFillInterior(p);
                    }
                }
            }

            // printMap();

            return dugPoints.size() + interiorPoints.size();
        }

        private void printMap() {
            for (int y = minY; y <= maxY; y++) {
                for (int x = minX; x <= maxX; x++) {
                    Point p = new Point(x, y);
                    if (x == 0 && y == 0) {
                        System.out.print('O');
                    } else if (dugPoints.contains(p)) {
                        System.out.print('#');
                    } else if (interiorPoints.contains(p)) {
                        System.out.print('i');
                    } else {
                        System.out.print('.');
                    }
                }
                System.out.println();
            }
        }

        private void floodFillInterior(Point start) {
            Set<Point> visited = new HashSet<>();
            Queue<Point> visitQueue = new ArrayDeque<>();
            visitQueue.add(start);
            Point p;
            while (null != (p = visitQueue.poll())) {
                if (exteriorPoints.contains(p)
                        || p.x < minX || p.x > maxX || p.y < minY || p.y > maxY) {
                    // exterior
                    exteriorPoints.addAll(visited);
                    return;
                }
                if (dugPoints.contains(p)) {
                    // flood stops, but may be interior or exterior
                    continue;
                }
                if (interiorPoints.contains(p)) {
                    // we already flood filled this zone
                    checkState(visited.isEmpty());
                    return;
                }
                if (visited.add(p)) {
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dy = -1; dy <= 1; dy++) {
                            if (!(dx == 0 && dy == 0)) {
                                visitQueue.add(new Point(p.x + dx, p.y + dy));
                            }
                        }
                    }
                }
            }

            // If got here, then we have a new region
            interiorPoints.addAll(visited);
        }
    }

    @Value
    static class LongPoint {
        long x;
        long y;
    }

    // Do the part1 q with the part2 code
    static long part2part1(List<String> input) {
        Pattern inputPatt = Pattern.compile("([UDLR]) (\\d+) \\(#([0-9a-f]+)([0-3])\\)");
        List<String> newInput = input.stream().map(line -> {
            Matcher m = inputPatt.matcher(line);
            checkState(m.matches());
            long dist = Long.parseLong(m.group(2));
            String dir;
            switch (m.group(1)) {
                case "U":
                    dir = "3";
                    break;
                case "R":
                    dir = "0";
                    break;
                case "D":
                    dir = "1";
                    break;
                case "L":
                    dir = "2";
                    break;
                default:
                    throw new IllegalArgumentException();
            }
            return String.format("U 0 (#%s%s)", Long.toString(dist, 16), dir);
        }).collect(Collectors.toList());
        return part2(newInput);
    }

    static long part2(List<String> input) {
        LongPoint currLoc = new LongPoint(0, 0);
        List<LongPoint> vertices = new ArrayList<>();
        vertices.add(currLoc);
        long pointsInBoundary = 0;
        Pattern inputPatt = Pattern.compile("([UDLR]) (\\d+) \\(#([0-9a-f]+)([0-3])\\)");
        for (String line : input) {
            Matcher m = inputPatt.matcher(line);
            checkState(m.matches());
            long dist = Long.parseLong(m.group(3), 16);
            pointsInBoundary += dist;
            switch (m.group(4)) {
                case "0": // R
                    currLoc = new LongPoint(currLoc.x + dist, currLoc.y);
                    break;
                case "1":// D
                    currLoc = new LongPoint(currLoc.x, currLoc.y + dist);
                    break;
                case "2":// L
                    currLoc = new LongPoint(currLoc.x - dist, currLoc.y);
                    break;
                case "3": // U
                    currLoc = new LongPoint(currLoc.x, currLoc.y - dist);
                    break;
                default:
                    throw new IllegalArgumentException();
            }
            vertices.add(currLoc);
        }

        // Get the area via https://en.wikipedia.org/wiki/Shoelace_formula
        // https://stackoverflow.com/a/451482/8261
        assertThat(vertices.get(0)).isEqualTo(vertices.get(vertices.size() - 1));
        long area = 0;
        for (int i = 0; i < vertices.size() - 1; i++) {
            LongPoint curr = vertices.get(i);
            LongPoint next = vertices.get(i + 1);
            area = addExact(area, multiplyExact(curr.y + curr.y, (curr.x - next.x)));
        }
        assertThat(area % 2).isEqualTo(0);
        area /= 2;

        // Then Pick's theorem
        // https://en.wikipedia.org/wiki/Pick%27s_theorem
        // A = i + b/2 - 1
        // We have A, b, want (i+b)
        // i = A - b/2 + 1
        assertThat(pointsInBoundary % 2).isEqualTo(0);
        long pointsInInterior = area - pointsInBoundary / 2 + 1;

        return pointsInInterior + pointsInBoundary;
    }

    static List<String> example = List.of(
            "R 6 (#70c710)",
            "D 5 (#0dc571)",
            "L 2 (#5713f0)",
            "D 2 (#d2c081)",
            "R 2 (#59c680)",
            "D 2 (#411b91)",
            "L 5 (#8ceee2)",
            "U 2 (#caa173)",
            "L 1 (#1b58a2)",
            "U 2 (#caa171)",
            "R 2 (#7807d2)",
            "U 3 (#a77fa3)",
            "L 2 (#015232)",
            "U 2 (#7a21e3)"
    );
}

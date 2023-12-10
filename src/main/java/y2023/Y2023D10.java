package y2023;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2023D10 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            List<String> input = Resources.readLines(Resources.getResource("y2023/Y2023D10.txt"), StandardCharsets.UTF_8);

            // 1
            assertThat(part1(example)).isEqualTo(8);
            assertThat(part1(input)).isEqualTo(6649);

            // 2
            assertThat(part2(example2)).isEqualTo(4);
            assertThat(part2(example3)).isEqualTo(10);
            assertThat(part2(input)).isEqualTo(601);

        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private static long part1(List<String> input) {
        Map<Point, Integer> distances = traceLoop(input);
        return distances.values().stream().mapToInt(i -> i).max().getAsInt();
    }

    private static long part2(List<String> input) {
        Map<Point, Integer> distances = traceLoop(input);

        // What letter is 'S' for connections?
        char s = findS(input, distances);

        // How many tiles are enclosed by the loop?
        // point is in loop if odd number of crossings to an edge
        int containedPointCount = 0;
        Set<Point> containedPoints = new HashSet<>();
        int width = input.get(0).length();
        for (int y = 0; y < input.size(); y++) {
            for (int x = 0; x < width; x++) {
                // Need to track as we cross ambiguous sections, if it is a crossing or a near miss
                // When going from right to left:
                // incl:  L-7   F-J
                // excl:  L-J   F-7
                //
                // Move the point inspected down by 1/4, so:
                // incl: F 7
                // excl: L J
                //
                // F-7
                // | |
                // L-J

                Point p = new Point(x, y);
                if (!distances.containsKey(p)) {
                    int crossingCount = 0;
                    for (int xx = x - 1; xx >= 0; xx--) {
                        Point pp = new Point(xx, y);
                        char cc = get(input, pp, s);
                        if (distances.containsKey(pp)) {
                            if ('|' == cc || 'F' == cc || '7' == cc) {
                                crossingCount++;
                            }
                        }
                    }
                    if (crossingCount % 2 == 1) {
                        containedPointCount++;
                        containedPoints.add(p);
                    }
                }
            }
        }

        return containedPointCount;
    }

    private static char findS(List<String> input, Map<Point, Integer> distances) {
        int startY = IntStream.range(0, input.size()).filter(i -> input.get(i).indexOf('S') >= 0).findFirst().getAsInt();
        int startX = input.get(startY).indexOf('S');

        boolean n = distances.getOrDefault(new Point(startX, startY - 1), 0) == 1;
        boolean s = distances.getOrDefault(new Point(startX, startY + 1), 0) == 1;
        boolean e = distances.getOrDefault(new Point(startX + 1, startY), 0) == 1;
        boolean w = distances.getOrDefault(new Point(startX - 1, startY), 0) == 1;

        if (n && s && !e && !w) {
            return '|';
        } else if (n && !s && !e && w) {
            return 'J';
        } else if (!n && e && s && !w) {
            return 'F';
        } else if (!n && !e && s && w) {
            return '7';
        } else {
            printMap(input, Collections.emptySet(), distances);
            throw new IllegalArgumentException(String.format("n %s e %s s %s w %s", n, e, s, w));
        }
    }

    private static void printMap(List<String> input, Set<Point> containedPoints, Map<Point, Integer> distances) {
        for (int y = 0; y < input.size(); y++) {
            String line = input.get(y);
            for (int x = 0; x < line.length(); x++) {
                Point p = new Point(x, y);
                if (containedPoints.contains(p)) {
                    System.out.print('I');
                } else if (distances.containsKey(p)) {
                    System.out.print(get(input, p));
                } else {
                    System.out.print('.');
                }
            }
            System.out.println();
        }
    }

    private static Map<Point, Integer> traceLoop(List<String> input) {
        int startY = IntStream.range(0, input.size()).filter(i -> input.get(i).indexOf('S') >= 0).findFirst().getAsInt();
        int startX = input.get(startY).indexOf('S');

        Map<Point, Integer> distances = new HashMap<>();
        List<Point> nextNeighs = List.of(new Point(startX, startY));
        for (int nextDist = 0; ; nextDist++) {
            List<Point> nextNextNeigh = new ArrayList<>();
            for (Point nextNeigh : nextNeighs) {
                if (!distances.containsKey(nextNeigh)) {
                    distances.put(nextNeigh, nextDist);
                    char curr = input.get(nextNeigh.y).charAt(nextNeigh.x);
                    // go N ?
                    {
                        Point nextP = new Point(nextNeigh.x, nextNeigh.y - 1);
                        char next = get(input, nextP);
                        if ((curr == 'S' || curr == '|' || curr == 'L' || curr == 'J')
                                && (next == '|' || next == '7' || next == 'F')) {
                            nextNextNeigh.add(nextP);
                        }
                    }
                    // go S ?
                    {
                        Point nextP = new Point(nextNeigh.x, nextNeigh.y + 1);
                        char next = get(input, nextP);
                        if ((curr == 'S' || curr == '|' || curr == 'F' || curr == '7')
                                && (next == '|' || next == 'L' || next == 'J')) {
                            nextNextNeigh.add(nextP);
                        }
                    }
                    // go E ?
                    {
                        Point nextP = new Point(nextNeigh.x + 1, nextNeigh.y);
                        char next = get(input, nextP);
                        if ((curr == 'S' || curr == '-' || curr == 'F' || curr == 'L')
                                && (next == '-' || next == '7' || next == 'J')) {
                            nextNextNeigh.add(nextP);
                        }
                    }
                    // go W ?
                    {
                        Point nextP = new Point(nextNeigh.x - 1, nextNeigh.y);
                        char next = get(input, nextP);
                        if ((curr == 'S' || curr == '-' || curr == '7' || curr == 'J')
                                && (next == '-' || next == 'F' || next == 'L')) {
                            nextNextNeigh.add(nextP);
                        }
                    }
                }
            }

            if (nextNextNeigh.isEmpty()) {
                return distances;
            } else {
                nextNeighs = nextNextNeigh;
            }
        }
    }

    private static char get(List<String> input, Point p, char sReplacement) {
        char c = get(input, p);
        return c == 'S' ? sReplacement : c;
    }

    private static char get(List<String> input, Point p) {
        if (p.y < 0 || p.y >= input.size()) {
            return '.';
        }
        String line = input.get(p.y);
        if (p.x < 0 || p.x >= line.length()) {
            return '.';
        }
        char c = line.charAt(p.x);
        return c;
    }

    static List<String> example = List.of(
            "..F7.",
            ".FJ|.",
            "SJ.L7",
            "|F--J",
            "LJ..."
    );

    static List<String> example2 = List.of(
            "...........",
            ".S-------7.",
            ".|F-----7|.",
            ".||.....||.",
            ".||.....||.",
            ".|L-7.F-J|.",
            ".|..|.|..|.",
            ".L--J.L--J.",
            "...........");
    static List<String> example3 = List.of(
            "FF7FSF7F7F7F7F7F---7",
            "L|LJ||||||||||||F--J",
            "FL-7LJLJ||||||LJL-77",
            "F--JF--7||LJLJ7F7FJ-",
            "L---JF-JLJ.||-FJLJJ7",
            "|F|F-JF---7F7-L7L|7|",
            "|FFJF7L7F-JF7|JL---7",
            "7-L-JL7||F7|L7F-7F7|",
            "L.L7LFJ|||||FJL7||LJ",
            "L7JLJL-JLJLJL--JLJ.L");

}

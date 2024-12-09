package y2024;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static aoc.Common.loadInputFromResources;
import static com.google.common.truth.Truth.assertThat;

public class Y2024D08 {

    public static void main(String[] args) throws Exception {
        List<String> input = loadInputFromResources();
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(part1(example)).isEqualTo(14);
        assertThat(part1(input)).isEqualTo(376);

        // 2
        assertThat(part2(example2)).isEqualTo(9);
        assertThat(part2(example)).isEqualTo(34);
        assertThat(part2(input)).isEqualTo(1352);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static long part1(List<String> input) {

        Multimap<Character, Point> transmittersByFreq = Multimaps.newSetMultimap(new HashMap<>(), HashSet::new);
        int height = input.size();
        int width = input.get(0).length();
        for (int y = 0; y < height; y++) {
            String line = input.get(y);
            for (int x = 0; x < width; x++) {
                char c = line.charAt(x);
                if (c != '.') {
                    Point p = new Point(x, y);
                    transmittersByFreq.put(c, p);
                }
            }
        }

        // p is an antinode if an antinode occurs at any point that is perfectly in line
        // with two antennas of the same frequency - but only when one of the antennas
        // is twice as far away as the other.
        int antinodeCount = 0;
        for (int y = 0; y < height; y++) {
            pointScan:
            for (int x = 0; x < width; x++) {
                Point p = new Point(x, y);
                for (Character freq : transmittersByFreq.keySet()) {
                    Collection<Point> transmitters = transmittersByFreq.get(freq);
                    for (Point transmitter : transmitters) {
                        if (!transmitter.equals(p)) {
                            Point pToT = minus(transmitter, p);
                            Point possibleSecondT = plus(p, mul(pToT, 2));
                            if (transmitters.contains(possibleSecondT)) {
                                antinodeCount++;
                                continue pointScan;
                            }
                        }
                    }
                }
            }
        }

        return antinodeCount;
    }

    private static Point minus(Point a, Point b) {
        return new Point(a.x - b.x, a.y - b.y);
    }

    private static Point plus(Point a, Point b) {
        return new Point(a.x + b.x, a.y + b.y);
    }

    private static Point mul(Point a, int x) {
        return new Point(a.x * x, a.y * x);
    }

    private static long part2(List<String> input) {

        Multimap<Character, Point> transmittersByFreq = Multimaps.newListMultimap(new HashMap<>(), ArrayList::new);
        int height = input.size();
        int width = input.get(0).length();
        for (int y = 0; y < height; y++) {
            String line = input.get(y);
            for (int x = 0; x < width; x++) {
                char c = line.charAt(x);
                if (c != '.') {
                    Point p = new Point(x, y);
                    transmittersByFreq.put(c, p);
                }
            }
        }

        // an antinode occurs at any grid position exactly in
        // line with at least two antennas of the same frequency, regardless of distance.
        Set<Point> antiNodes = new HashSet<>();
        for (Character freq : transmittersByFreq.keySet()) {
            List<Point> transmitters = (List<Point>) transmittersByFreq.get(freq);
            for (int i = 0; i < transmitters.size(); i++) {
                Point t1 = transmitters.get(i);
                for (int j = 0; j < transmitters.size(); j++) {
                    if (i != j) {
                        Point t2 = transmitters.get(j);
                        Point d = minus(t2, t1);
                        Point antiNode = t2;
                        do {
                            antiNodes.add(antiNode);
                            antiNode = plus(antiNode, d);
                        } while (inBounds(antiNode, width, height));
                    }
                }
            }
        }

        return antiNodes.size();
    }

    private static boolean inBounds(Point p, int width, int height) {
        return p.x >= 0 && p.x < width && p.y >= 0 && p.y < height;
    }

    static List<String> example = List.of(
            "............",
            "........0...",
            ".....0......",
            ".......0....",
            "....0.......",
            "......A.....",
            "............",
            "............",
            "........A...",
            ".........A..",
            "............",
            "............");

    static List<String> example2 = List.of(
            "T.........",
            "...T......",
            ".T........",
            "..........",
            "..........",
            "..........",
            "..........",
            "..........",
            "..........",
            "..........");
}

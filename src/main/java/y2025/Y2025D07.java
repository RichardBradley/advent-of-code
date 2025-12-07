package y2025;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static aoc.Common.loadInputFromResources;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2025D07 {

    public static void main(String[] args) throws Exception {
        List<String> input = loadInputFromResources();
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(part1(example)).isEqualTo(21);
        assertThat(part1(input)).isEqualTo(1622);

        // 2
        assertThat(part2(example)).isEqualTo(40);
        assertThat(part2(input)).isEqualTo(10357305916520L);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static long part1(List<String> input) {
        int startX = input.get(0).indexOf('S');
        Set<Integer> beams = new HashSet<>();
        beams.add(startX);
        int splitCount = 0;

        for (int y = 0; y < input.size(); y++) {
            Set<Integer> nextBeams = new HashSet<>();
            String line = input.get(y);
            for (int x = 0; x < line.length(); x++) {
                if (beams.contains(x)) {
                    char c = line.charAt(x);
                    if (c == '^') {
                        splitCount++;
                        if (x > 0) {
                            nextBeams.add(x - 1);
                        }
                        if (x < line.length() - 1) {
                            nextBeams.add(x + 1);
                        }
                    } else if (c == '.' || c == 'S') {
                        nextBeams.add(x);
                    } else {
                        throw new IllegalArgumentException("Unexpected char: " + c);
                    }
                }
            }
            beams = nextBeams;
        }
        return splitCount;
    }

    private static long part2(List<String> input) {
        Map<Point, Long> pathCountFromPoint = new HashMap<>();
        return getPathCountFrom(new Point(input.get(0).indexOf('S'), 0), input, pathCountFromPoint);
    }

    private static long getPathCountFrom(Point p, List<String> map, Map<Point, Long> pathCountFromPoint) {
        if (p.y >= map.size()) {
            return 1;
        }
        Long ret = pathCountFromPoint.get(p);
        if (ret != null) {
            return ret;
        }

        char c = map.get(p.y).charAt(p.x);
        if (c == '.' || c == 'S') {
            ret = getPathCountFrom(new Point(p.x, p.y + 1), map, pathCountFromPoint);
        } else if (c == '^') {
            ret = 0L;
            if (p.x > 0) {
                Point left = new Point(p.x - 1, p.y);
                ret += getPathCountFrom(left, map, pathCountFromPoint);
            }
            if (p.x < map.get(0).length() - 1) {
                Point right = new Point(p.x + 1, p.y);
                ret += getPathCountFrom(right, map, pathCountFromPoint);
            }
        } else {
            throw new IllegalArgumentException("Unexpected char: " + c);
        }
        pathCountFromPoint.put(p, ret);
        return ret;
    }

    static List<String> example = List.of(
            ".......S.......",
            "...............",
            ".......^.......",
            "...............",
            "......^.^......",
            "...............",
            ".....^.^.^.....",
            "...............",
            "....^.^...^....",
            "...............",
            "...^.^...^.^...",
            "...............",
            "..^...^.....^..",
            "...............",
            ".^.^.^.^.^...^.",
            "...............");
}

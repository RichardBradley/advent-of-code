package y2024;

import com.google.common.base.Stopwatch;
import lombok.Data;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static aoc.Common.loadInputFromResources;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2024D12 {

    public static void main(String[] args) throws Exception {
        List<String> input = loadInputFromResources();
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(score(exampleOX, false)).isEqualTo(16 + 36 * 21);
        assertThat(score(example, false)).isEqualTo(1930);
        assertThat(score(input, false)).isEqualTo(1370258);

        // 2
        assertThat(score(exampleAB, true)).isEqualTo(368);
        assertThat(score(input, true)).isEqualTo(805814);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static long score(List<String> input, boolean part2) {
        Set<Point> visited = new HashSet<>();
        int totalPrice = 0;
        for (int y = 0; y < input.size(); y++) {
            String line = input.get(y);
            for (int x = 0; x < line.length(); x++) {
                Point p = new Point(x, y);
                if (visited.add(p)) {
                    char c = get(input, p);
                    RegionSize regionSize = new RegionSize();
                    HashSet<Point> region = new HashSet<>();
                    regionSize.area = 1;
                    region.add(p);

                    fillRegion(input, p, c, regionSize, region);

                    visited.addAll(region);

                    if (part2) {
                        totalPrice += regionSize.area * countSides(regionSize);
                    } else {
                        totalPrice += regionSize.area * regionSize.perimeter;
                    }
                }
            }
        }
        return totalPrice;
    }

    @Data
    static class RegionSize {
        int area = 0;
        int perimeter = 0;
        Map<Point, Set<Point>> pointsInsideFencesByDir = new HashMap<>();
    }

    static Point[] dirs = new Point[]{
            new Point(0, -1),
            new Point(1, 0),
            new Point(0, 1),
            new Point(-1, 0)
    };

    private static void fillRegion(List<String> map, Point p, char c, RegionSize regionSize, HashSet<Point> region) {
        for (Point dir : dirs) {
            Point next = add(p, dir);
            if (c == get(map, next)) {
                if (region.add(next)) {
                    regionSize.area++;
                    fillRegion(map, next, c, regionSize, region);
                }
            } else {
                regionSize.perimeter++;
                Set<Point> m = regionSize.pointsInsideFencesByDir.computeIfAbsent(
                        dir,
                        k -> new HashSet<>());
                checkState(m.add(p));
            }
        }
    }

    private static Point add(Point a, Point b) {
        return new Point(a.x + b.x, a.y + b.y);
    }

    private static char get(List<String> input, Point p) {
        if (p.y >= 0 && p.y < input.size()) {
            String line = input.get(p.y);
            if (p.x >= 0 && p.x < input.size()) {
                return line.charAt(p.x);
            }
        }
        return ' ';
    }

    private static int countSides(RegionSize regionSize) {
        Comparator<Point> vSort = Comparator.comparing(Point::getX).thenComparing(Point::getY);
        Comparator<Point> hSort = Comparator.comparing(Point::getY).thenComparing(Point::getX);

        int sidesCount = 0;
        for (int dirIdx = 0; dirIdx < dirs.length; dirIdx++) {
            Point dir = dirs[dirIdx];

            Set<Point> pointsInsideFence = regionSize.pointsInsideFencesByDir.get(dir);

            boolean isHorizFence = dirIdx % 2 == 0;
            TreeSet<Point> pSorted = new TreeSet<>(isHorizFence ? hSort : vSort);
            pSorted.addAll(pointsInsideFence);

            Point prev = new Point(-10, -10);
            for (Point next : pSorted) {
                boolean isSameLine = isHorizFence ? prev.y == next.y : prev.x == next.x;
                if (!isSameLine || dist(prev, next) > 1) {
                    sidesCount++;
                }
                prev = next;
            }
        }
        return sidesCount;
    }

    private static int dist(Point a, Point b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    static List<String> exampleOX = List.of(
            "OOOOO",
            "OXOXO",
            "OOOOO",
            "OXOXO",
            "OOOOO");
    static List<String> example = List.of(
            "RRRRIICCFF",
            "RRRRIICCCF",
            "VVRRRCCFFF",
            "VVRCCCJFFF",
            "VVVVCJJCFE",
            "VVIVCCJJEE",
            "VVIIICJJEE",
            "MIIIIIJJEE",
            "MIIISIJEEE",
            "MMMISSJEEE");
    static List<String> exampleAB = List.of(
            "AAAAAA",
            "AAABBA",
            "AAABBA",
            "ABBAAA",
            "ABBAAA",
            "AAAAAA"
    );
}

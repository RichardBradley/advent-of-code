package y2021;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Queue;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

public class Y2021D09 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            List<String> input = Resources.readLines(Resources.getResource("y2021/Y2021D09.txt"), StandardCharsets.UTF_8);

            // 1
            assertThat(part1(example)).isEqualTo(15);
            assertThat(part1(input)).isEqualTo(486);

            // 2
            assertThat(part2(example)).isEqualTo(1134);
            assertThat(part2(input)).isEqualTo(1059300);

        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    static Point[] dirs = new Point[]{
            new Point(-1, 0),
            new Point(0, -1),
            new Point(1, 0),
            new Point(0, 1)
    };

    private static long part1(List<String> input) {
        return findLowestPoints(input).stream()
                .mapToInt(p -> 1 + (input.get(p.y).charAt(p.x) - '0'))
                .sum();
    }

    private static List<Point> findLowestPoints(List<String> input) {
        int height = input.size();
        int width = input.get(0).length();
        List<Point> acc = new ArrayList<>();

        for (int y = 0; y < height; y++) {
            String row = input.get(y);
            for (int x = 0; x < width; x++) {
                boolean isLowest = true;
                char curr = row.charAt(x);

                for (Point dir : dirs) {
                    int dx = x + dir.x;
                    if (dx >= 0 && dx < width) {
                        int dy = y + dir.y;
                        if (dy >= 0 && dy < height) {
                            isLowest &= (curr < input.get(dy).charAt(dx));
                        }
                    }
                }

                if (isLowest) {
                    acc.add(new Point(x, y));
                }
            }
        }
        return acc;
    }

    private static long part2(List<String> input) {
        int height = input.size();
        int width = input.get(0).length();

        List<Point> lowestPoints = findLowestPoints(input);
        List<Set<Point>> basins = new ArrayList<>();

        for (Point lowestPoint : lowestPoints) {
            Set<Point> basinMembers = new HashSet<>();
            basins.add(basinMembers);
            basinMembers.add(lowestPoint);
            Queue<Point> fillQueue = new ArrayDeque<>();
            fillQueue.add(lowestPoint);

            while (!fillQueue.isEmpty()) {
                Point prev = fillQueue.poll();
                char prevC = input.get(prev.y).charAt(prev.x);
                for (Point dir : dirs) {
                    Point next = new Point(prev.x + dir.x, prev.y + dir.y);
                    if (next.x >= 0 && next.x < width && next.y >= 0 && next.y < height) {
                        char nextC = input.get(next.y).charAt(next.x);
                        if (nextC != '9') {
                            if (nextC > prevC) {
                                basinMembers.add(next);
                                fillQueue.add(next);
                            }
                        }
                    }
                }
            }
        }

        return basins.stream()
                .sorted(Comparator.comparing((Set<Point> b) -> b.size()).reversed())
                .limit(3)
                .mapToInt(b -> b.size())
                .reduce(1, (a, b) -> a * b);
    }

    private static List<String> example = List.of(
            "2199943210",
            "3987894921",
            "9856789892",
            "8767896789",
            "9899965678"
    );

}

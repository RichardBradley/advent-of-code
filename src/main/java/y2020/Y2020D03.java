package y2020;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Y2020D03 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        List<String> input = Resources.readLines(Resources.getResource("y2020/Y2020D03.txt"), StandardCharsets.UTF_8);
        part1(input);
        part2(input);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static void part2(List<String> input) {
        Point[] slopes = new Point[]{
                new Point(1, 1),
                new Point(3, 1),
                new Point(5, 1),
                new Point(7, 1),
                new Point(1, 2)
        };
        int width = input.get(0).length();
        long total = 1;
        for (Point slope : slopes) {
            int treeCount = 0;
            int x = 0;
            for (int y = 0; y < input.size(); y += slope.y) {
                if (input.get(y).charAt(x % width) == '#') {
                    treeCount++;
                }
                x += slope.x;
            }
            total *= treeCount;
        }
        System.out.println("total = " + total);
    }

    private static void part1(List<String> input) {
        int x = 0;
        int width = input.get(0).length();
        int count = 0;
        for (int y = 0; y < input.size(); y++) {
            if (input.get(y).charAt(x % width) == '#') {
                count++;
            }
            x += 3;
        }
        System.out.println("count = " + count);
    }
}

package y2021;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2021D13 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            List<String> input = Resources.readLines(Resources.getResource("y2021/Y2021D13.txt"), StandardCharsets.UTF_8);

            // 1
            assertThat(eval(example, true)).isEqualTo(17);
            assertThat(eval(input, true)).isEqualTo(729);

            // 2
            eval(input, false);

        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private static long eval(List<String> input, boolean part1) {
        Set<Point> dots = new HashSet<>();
        Pattern foldPatt = Pattern.compile("fold along ([xy])=(\\d+)");
        boolean firstSection = true;
        for (String line : input) {
            if (firstSection) {
                if ("".equals(line)) {
                    firstSection = false;
                } else {
                    String[] parts = line.split(",");
                    checkState(parts.length == 2);
                    checkState(dots.add(new Point(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]))));
                }
            } else {
                Matcher m = foldPatt.matcher(line);
                checkState(m.matches());
                if ("x".equals(m.group(1))) {
                    // fold left:
                    int foldX = Integer.parseInt(m.group(2));
                    Set<Point> dots2 = new HashSet<>();
                    for (Point dot : dots) {
                        if (dot.x > foldX) {
                            dots2.add(new Point(2 * foldX - dot.x, dot.y));
                        } else {
                            dots2.add(dot);
                        }
                    }
                    dots = dots2;
                } else {
                    // fold up:
                    int foldY = Integer.parseInt(m.group(2));
                    Set<Point> dots2 = new HashSet<>();
                    for (Point dot : dots) {
                        if (dot.y > foldY) {
                            dots2.add(new Point(dot.x, 2 * foldY - dot.y));
                        } else {
                            dots2.add(dot);
                        }
                    }
                    dots = dots2;
                }

                if (part1) {
                    return dots.size();
                }
            }
        }

        int maxX = dots.stream().mapToInt(d -> d.x).max().getAsInt();
        int maxY = dots.stream().mapToInt(d -> d.y).max().getAsInt();
        for (int y = 0; y <= maxY; y++) {
            for (int x = 0; x <= maxX; x++) {
                Point p = new Point(x, y);
                System.out.print(dots.contains(p) ? '#' : ' ');
            }
            System.out.println();
        }
        return -1;
    }

    private static List<String> example = List.of(
            "6,10",
            "0,14",
            "9,10",
            "0,3",
            "10,4",
            "4,11",
            "6,0",
            "6,12",
            "4,1",
            "0,13",
            "10,12",
            "3,4",
            "3,0",
            "8,4",
            "1,10",
            "2,14",
            "8,10",
            "9,0",
            "",
            "fold along y=7",
            "fold along x=5"
    );
}

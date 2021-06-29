package y2017;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

public class Y2017D19 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {

            List<String> input = Resources.readLines(Resources.getResource("y2017/Y2017D19.txt"), StandardCharsets.UTF_8);

            assertThat(followPath(example)).isEqualTo("ABCDEF 38");
            assertThat(followPath(input)).isEqualTo("UICRNSDOK 16064");
        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private static String followPath(List<String> map) {
        Point curr = new Point(map.get(0).indexOf('|'), 0);
        Point dir = new Point(0, 1);
        StringBuilder acc = new StringBuilder();
        int stepCount = 1;

        while (true) {
            char next = getNext(map, curr, dir);
            if (next == '|' || next == '-') {
                curr = add(curr, dir);
                stepCount++;
            } else if ('A' <= next && 'Z' >= next) {
                acc.append(next);
                curr = add(curr, dir);
                stepCount++;
            } else if (next == '+') {
                // corner
                stepCount++;
                char ahead = getNext(map, curr, add(dir, dir));
                assertThat(ahead).isEqualTo(' ');
                Point[] leftRight = getLeftAndRight(dir);
                char aheadLeft = getNext(map, curr, add(dir, leftRight[0]));
                char aheadRight = getNext(map, curr, add(dir, leftRight[1]));
                if (aheadLeft == ' ') {
                    assertIsPipeOrLetter(aheadRight);
                    curr = add(curr, dir);
                    dir = leftRight[1];
                } else {
                    assertThat(aheadRight).isEqualTo(' ');
                    assertIsPipeOrLetter(aheadLeft);
                    curr = add(curr, dir);
                    dir = leftRight[0];
                }
            } else if (next == ' ') {
                // end
                return acc + " " + stepCount;
            }
        }
    }

    private static void assertIsPipeOrLetter(char c) {
        if ('A' <= c && 'Z' >= c) {
            return;
        }
        assertThat(c).isAnyOf('|', '-');
    }

    private static Point[] getLeftAndRight(Point dir) {
        if (dir.x == 0) {
            return new Point[]{new Point(-1, 0), new Point(1, 0)};
        } else {
            return new Point[]{new Point(0, -1), new Point(0, 1)};
        }
    }

    private static Point add(Point a, Point b) {
        return new Point(a.x + b.x, a.y + b.y);
    }

    private static char getNext(List<String> map, Point curr, Point dir) {
        int y = curr.y + dir.y;
        if (y < 0 || y >= map.size()) {
            return ' ';
        }
        String row = map.get(y);
        int x = curr.x + dir.x;
        if (x < 0 || x >= row.length()) {
            return ' ';
        }
        return row.charAt(x);
    }

    static List<String> example = ImmutableList.of(
            "     |          ",
            "     |  +--+    ",
            "     A  |  C    ",
            " F---|----E|--+ ",
            "     |  |  |  D ",
            "     +B-+  +--+ "
    );
}


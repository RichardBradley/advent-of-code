package y2021;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;
import lombok.Value;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2021D05 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            List<String> input = Resources.readLines(Resources.getResource("y2021/Y2021D05.txt"), StandardCharsets.UTF_8);

            // 1
            assertThat(part1(example)).isEqualTo(5);
            assertThat(part1(input)).isEqualTo(6548);

            // 2
            assertThat(part2(example)).isEqualTo(12);
            assertThat(part2(input)).isEqualTo(-1);

        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private static long part1(List<String> input) {
        Map<Point, Integer> density = new HashMap<>();
        Pattern pat = Pattern.compile("(\\d+),(\\d+) -> (\\d+),(\\d+)");
        for (String line : input) {
            Matcher m = pat.matcher(line);
            checkArgument(m.matches());
            Point from = new Point(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
            Point to = new Point(Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4)));
            if (from.x == to.x) {
                int y1 = Math.min(from.y, to.y);
                int y2 = Math.max(from.y, to.y);
                for (int y = y1; y <= y2; y++) {
                    density.compute(new Point(from.x, y), (k, v) -> 1 + (null == v ? 0 : v));
                }
            } else if (from.y == to.y) {
                int x1 = Math.min(from.x, to.x);
                int x2 = Math.max(from.x, to.x);
                for (int x = x1; x <= x2; x++) {
                    density.compute(new Point(x, from.y), (k, v) -> 1 + (null == v ? 0 : v));
                }
            } else {
                // skip
            }
        }

        // the number of points where at least two lines overlap
        return density.values().stream().filter(x -> x >= 2).count();
    }


    private static long part2(List<String> input) {
        Map<Point, Integer> density = new HashMap<>();
        Pattern pat = Pattern.compile("(\\d+),(\\d+) -> (\\d+),(\\d+)");
        for (String line : input) {
            Matcher m = pat.matcher(line);
            checkArgument(m.matches());
            Point from = new Point(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
            Point to = new Point(Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4)));
            if (from.x == to.x) {
                int y1 = Math.min(from.y, to.y);
                int y2 = Math.max(from.y, to.y);
                for (int y = y1; y <= y2; y++) {
                    density.compute(new Point(from.x, y), (k, v) -> 1 + (null == v ? 0 : v));
                }
            } else if (from.y == to.y) {
                int x1 = Math.min(from.x, to.x);
                int x2 = Math.max(from.x, to.x);
                for (int x = x1; x <= x2; x++) {
                    density.compute(new Point(x, from.y), (k, v) -> 1 + (null == v ? 0 : v));
                }
            } else {
                // diagonal
                int len = Math.abs(from.x - to.x);
                checkState(len == Math.abs(from.y - to.y));
                checkState(len > 0);
                for (int i = 0; i <= len; i++) {
                    int x = range(from.x, to.x, i);
                    int y = range(from.y, to.y, i);
                    density.compute(new Point(x, y), (k, v) -> 1 + (null == v ? 0 : v));
                }
            }
        }

        // the number of points where at least two lines overlap
        return density.values().stream().filter(x -> x >= 2).count();
    }

    private static int range(int start, int end, int i) {
        if (start < end) {
            return start + i;
        } else {
            return start - i;
        }
    }

    private static List<String> example = List.of(
            "0,9 -> 5,9",
            "8,0 -> 0,8",
            "9,4 -> 3,4",
            "2,2 -> 2,1",
            "7,0 -> 7,4",
            "6,4 -> 2,0",
            "0,9 -> 2,9",
            "3,4 -> 1,4",
            "0,0 -> 8,8",
            "5,5 -> 8,2"
    );

    @Value
    private static class BingoInput {
        int[] calledNumbers;
        List<int[][]> boards; // boardIdx, y, x
    }

    @Value
    private static class Output {
        int firstWinScore;
        int lastWinScore;
    }
}

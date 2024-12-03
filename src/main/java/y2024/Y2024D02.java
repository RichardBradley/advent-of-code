package y2024;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static aoc.Common.loadInputFromResources;
import static com.google.common.truth.Truth.assertThat;

public class Y2024D02 {

    public static void main(String[] args) throws Exception {
        List<String> input = loadInputFromResources();
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(part1(example)).isEqualTo(2);
        assertThat(part1(input)).isEqualTo(407);

        // 2
        assertThat(part2(example)).isEqualTo(4);
        assertThat(part2(input)).isEqualTo(459);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static long part1(List<String> input) {
        int safeCount = 0;

        Splitter s = Splitter.on(' ');
        for (String line : input) {
            List<Integer> vals = s.splitToList(line).stream()
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            if (isSafe(vals)) {
                safeCount++;
            }
        }

        return safeCount;
    }

    private static long part2(List<String> input) {
        int safeCount = 0;

        Splitter s = Splitter.on(' ');
        lines:
        for (String line : input) {
            List<Integer> vals = s.splitToList(line).stream()
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            if (isSafe(vals)) {
                safeCount++;
            } else {
                for (int i = 0; i < vals.size(); i++) {
                    ArrayList<Integer> vals2 = new ArrayList<>(vals);
                    vals2.remove(i);
                    if (isSafe(vals2)) {
                        safeCount++;
                        continue lines;
                    }
                }
            }
        }

        return safeCount;
    }

    private static boolean isSafe(List<Integer> vals) {

        boolean isAsc;
        if (vals.get(0) < vals.get(1)) {
            isAsc = true;
        } else if (vals.get(0) > vals.get(1)) {
            isAsc = false;
        } else {
            return false;
        }

        for (int i = 0; i < vals.size() - 1; i++) {
            // The levels are either all increasing or all decreasing.
            int a = vals.get(i);
            int b = vals.get(i + 1);

            if (isAsc) {
                if (!(a < b)) {
                    return false;
                }
            } else {
                if (!(a > b)) {
                    return false;
                }
            }

            // Any two adjacent levels differ by at least one and at most three.
            int diff = Math.abs(a - b);
            if (diff < 1 || diff > 3) {
                return false;
            }
        }

        return true;
    }

    static List<String> example = List.of(
            "7 6 4 2 1",
            "1 2 7 8 9",
            "9 7 6 2 1",
            "1 3 2 4 5",
            "8 6 4 4 1",
            "1 3 6 7 9"
    );
}

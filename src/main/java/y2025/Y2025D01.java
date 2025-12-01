package y2025;

import com.google.common.base.Stopwatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static aoc.Common.loadInputFromResources;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2025D01 {

    public static void main(String[] args) throws Exception {
        List<String> input = loadInputFromResources();
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(part1(example)).isEqualTo(3);
        assertThat(part1(input)).isEqualTo(1011);

        // 2
        assertThat(part2(example)).isEqualTo(6);
        assertThat(part2(input)).isEqualTo(5937);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static long part1(List<String> input) {
        long pos = 50;
        int timesPointingToZero = 0;

        for (String line : input) {
            int n = Integer.parseInt(line.substring(1));
            if ('R' == line.charAt(0)) {
                pos = mod(pos + n, 100);
            } else {
                pos = mod(pos - n, 100);
            }
            if (pos == 0) {
                timesPointingToZero++;
            }
        }
        return timesPointingToZero;
    }

    private static long part2(List<String> input) {
        long pos = 50;
        int timesPointingToZero = 0;

        for (String line : input) {
            int n = Integer.parseInt(line.substring(1));

            // count extra rotations
            timesPointingToZero += n / 100;
            n %= 100;

            if ('R' == line.charAt(0)) {
                if ((pos + n) > 100) {
                    timesPointingToZero++;
                }
                pos = mod(pos + n, 100);
            } else {
                if (pos != 0 && (pos - n) < 0) {
                    timesPointingToZero++;
                }
                pos = mod(pos - n, 100);
            }
            if (pos == 0) {
                timesPointingToZero++;
            }
        }
        return timesPointingToZero;
    }

    private static long mod(long a, long m) {
        long ret = a % m;
        if (ret < 0) {
            ret += m;
        }
        return ret;
    }

    static List<String> example = List.of(
            "L68",
            "L30",
            "R48",
            "L5",
            "R60",
            "L55",
            "L1",
            "L99",
            "R14",
            "L82");
}

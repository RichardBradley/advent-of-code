package y2022;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2022D25 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        List<String> input = Resources.readLines(Resources.getResource("y2022/Y2022D25.txt"), StandardCharsets.UTF_8);

        // 1
        assertThat(parseSnafu("2=-01")).isEqualTo(976);
        assertThat(parseSnafu("1121-1110-1=0")).isEqualTo(314159265);
        assertThat(toSnafu(8)).isEqualTo("2=");
        assertThat(toSnafu(4890)).isEqualTo("2=-1=0");
        assertThat(part1(example)).isEqualTo("2=-1=0");
        System.out.println("Example 1 OK");
        System.out.println(part1(input));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static String part1(List<String> input) {
        long sum = input.stream().mapToLong(s -> parseSnafu(s)).sum();
        return toSnafu(sum);
    }

    private static long parseSnafu(String s) {
        long acc = 0;
        for (int i = 0; i < s.length(); i++) {
            acc *= 5;
            char c = s.charAt(i);
            if ('=' == c) {
                acc -= 2;
            } else if ('-' == c) {
                acc--;
            } else {
                checkState(Character.isDigit(c));
                acc += (c - '0');
            }
        }
        return acc;
    }

    private static String toSnafu(long i) {
        StringBuilder acc = new StringBuilder();
        while (i > 0) {
            long digit = i % 5;
            i = i / 5;

            if (digit == 3) {
                // take 5 from this digit, add to next
                acc.insert(0, "=");
                i++;
            } else if (digit == 4) {
                // take 5 from this digit, add to next
                acc.insert(0, "-");
                i++;
            } else {
                acc.insert(0, digit);
            }
        }
        return acc.toString();
    }


    private static List<String> example = List.of(
            "1=-0-2",
            "12111",
            "2=0=",
            "21",
            "2=01",
            "111",
            "20012",
            "112",
            "1=-1=",
            "1-12",
            "12",
            "1=",
            "122");
}

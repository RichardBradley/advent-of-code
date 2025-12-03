package y2025;

import com.google.common.base.Stopwatch;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static aoc.Common.loadInputFromResources;
import static com.google.common.truth.Truth.assertThat;

public class Y2025D03 {

    public static void main(String[] args) throws Exception {
        List<String> input = loadInputFromResources();
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(part1(example)).isEqualTo(357);
        assertThat(part1(input)).isEqualTo(17316);

        // 2
        assertThat(part2(example)).isEqualTo(3121910778619L);
        assertThat(part2(input)).isEqualTo(0);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static long part1(List<String> input) {
        long sum = 0;
        for (String line : input) {
            long max = 0;
            for (int i = 0; i < line.length(); i++) {
                for (int j = i + 1; j < line.length(); j++) {
                    long val = 10 * (line.charAt(i) - '0') + (line.charAt(j) - '0');
                    if (val > max) {
                        max = val;
                    }
                }
            }
            sum += max;
        }
        return sum;
    }

    private static long part2(List<String> input) {
        long lastReportTimeMillis = 0;

        long sum = 0;
        for (int lineIdx = 0; lineIdx < input.size(); lineIdx++) {
            String line = input.get(lineIdx);
            StringBuilder sb = new StringBuilder();
            long max = findMax(sb, line, 0, 12);
            sum += max;

            if (System.currentTimeMillis() - lastReportTimeMillis > 10000) {
                lastReportTimeMillis = System.currentTimeMillis();
                System.out.printf("%s line %s of %s, sum = %s\n", Instant.now(), lineIdx, input.size(), sum);
            }
        }
        return sum;
    }

    private static long findMax(StringBuilder acc, String line, int i, int digitsRemaining) {
        if (digitsRemaining == 0) {
            return Long.parseLong(acc.toString());
        }
        if (i >= line.length()) {
            return 0;
        }

        // the max will start with the highest digit left of len-digitsRemaining
        int nextDigitPos = -1;
        char nextDigitVal = '\0';
        for (int j = i; j < line.length() - digitsRemaining + 1; j++) {
            char c = line.charAt(j);
            if (c > nextDigitVal) {
                nextDigitVal = c;
                nextDigitPos = j;
            }
        }

        if (nextDigitPos == -1) {
            return 0;
        } else {
            acc.append(nextDigitVal);
            return findMax(acc, line, nextDigitPos + 1, digitsRemaining - 1);
        }
    }

    static List<String> example = List.of(
            "987654321111111",
            "811111111111119",
            "234234234234278",
            "818181911112111");
}

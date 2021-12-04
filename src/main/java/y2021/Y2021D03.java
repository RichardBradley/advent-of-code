package y2021;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;

public class Y2021D03 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            List<String> input = Resources.readLines(Resources.getResource("y2021/Y2021D03.txt"), StandardCharsets.UTF_8);

            // 1
            assertThat(part1(asList(example))).isEqualTo(198);
            assertThat(part1(input)).isEqualTo(841526);

            // 2
            assertThat(part2(asList(example))).isEqualTo(230);
            assertThat(part2(input)).isEqualTo(-1);

        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private static int part1(List<String> input) {
        int width = input.get(0).length();
        StringBuilder gamma = new StringBuilder();
        StringBuilder epsilon = new StringBuilder();
        for (int i = 0; i < width; i++) {
            int oneCount = 0;
            int zeroCount = 0;
            for (String line : input) {
                if (line.charAt(i) == '0') {
                    zeroCount++;
                } else if (line.charAt(i) == '1') {
                    oneCount++;
                } else {
                    throw new IllegalArgumentException();
                }
            }
            if (oneCount > zeroCount) {
                gamma.append('1');
                epsilon.append('0');
            } else if (oneCount < zeroCount) {
                gamma.append('0');
                epsilon.append('1');
            } else {
                throw new IllegalArgumentException();
            }
        }

        return Integer.parseInt(gamma.toString(), 2)
                * Integer.parseInt(epsilon.toString(), 2);
    }

    private static int part2(List<String> input) {
        int ox = findByBit(input, true);
        int co2 = findByBit(input, false);
        return ox * co2;
    }

    private static int findByBit(List<String> input, boolean mostCommon) {
        input = new ArrayList<>(input);
        int width = input.get(0).length();
        for (int i = 0; i < width; i++) {
            int oneCount = 0;
            int zeroCount = 0;
            for (String line : input) {
                if (line.charAt(i) == '0') {
                    zeroCount++;
                } else if (line.charAt(i) == '1') {
                    oneCount++;
                } else {
                    throw new IllegalArgumentException();
                }
            }

            char target;
            if (oneCount >= zeroCount) {
                target = mostCommon ? '1' : '0';
            } else {
                target = mostCommon ? '0' : '1';
            }

            for (int j = input.size() - 1; j >= 0; j--) {
                if (input.get(j).charAt(i) != target) {
                    input.remove(j);
                }
            }

            if (input.size() == 1) {
                return Integer.parseInt(input.get(0), 2);
            }
        }
        throw new IllegalArgumentException();
    }

    private static String[] example = new String[]{
            "00100",
            "11110",
            "10110",
            "10111",
            "10101",
            "01111",
            "00111",
            "11100",
            "10000",
            "11001",
            "00010",
            "01010"
    };
}

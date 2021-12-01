package y2021;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;

public class Y2021D01 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        List<Integer> input = Resources.readLines(Resources.getResource("y2021/Y2021D01.txt"), StandardCharsets.UTF_8)
                .stream().map(Integer::parseInt).collect(Collectors.toList());

        // 1
        assertThat(countAscending(input)).isEqualTo(1502);

        // 2
        assertThat(countAscendingThrees(input)).isEqualTo(1538);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static int countAscendingThrees(List<Integer> input) {
        int prev = Integer.MAX_VALUE;
        int countAscending = 0;
        for (int i = 0; i < input.size() - 2; i++) {
            int sum = input.get(i) + input.get(i + 1) + input.get(i + 2);
            if (sum > prev) {
                countAscending++;
            }
            prev = sum;
        }
        return countAscending;
    }

    private static int countAscending(List<Integer> input) {
        int prev = Integer.MAX_VALUE;
        int countAscending = 0;
        for (Integer i : input) {
            if (i > prev) {
                countAscending++;
            }
            prev = i;
        }
        return countAscending;
    }
}

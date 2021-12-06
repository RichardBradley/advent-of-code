package y2021;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Iterables;
import com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;

public class Y2021D06 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            String input = Iterables.getOnlyElement(Resources.readLines(Resources.getResource("y2021/Y2021D06.txt"), StandardCharsets.UTF_8));

            // 1
            assertThat(part1(example, 18)).isEqualTo(26);
            assertThat(part1(example, 80)).isEqualTo(5934);
            assertThat(part1(input, 80)).isEqualTo(374927);

            // 2
            assertThat(part1(input, 256)).isEqualTo(1687617803407L);

        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private static long part1(String input, int dayCount) {
        Map<Integer, Long> timerToCount = Arrays.stream(input.split(",")).map(i -> Integer.parseInt(i))
                .collect(Collectors.groupingBy(i -> i))
                .values().stream()
                .collect(Collectors.toMap(xs -> xs.get(0), x -> (long) x.size()));

        for (int i = 0; i < dayCount; i++) {
            Map<Integer, Long> timerToCountNext = new HashMap<>();
            for (Map.Entry<Integer, Long> entry : timerToCount.entrySet()) {
                int timer = entry.getKey();
                long count = entry.getValue();
                if (timer == 0) {
                    timerToCountNext.compute(6, (k, v) -> (v == null ? 0L : v) + count);
                    timerToCountNext.compute(8, (k, v) -> (v == null ? 0L : v) + count);
                } else {
                    timerToCountNext.compute(timer - 1, (k, v) -> (v == null ? 0L : v) + count);
                }
            }
            timerToCount = timerToCountNext;
        }

        return timerToCount.values().stream().mapToLong(i -> i).sum();
    }

    private static String example = "3,4,3,1,2";
}

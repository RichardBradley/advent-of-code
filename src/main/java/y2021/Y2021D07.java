package y2021;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Iterables;
import com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;

public class Y2021D07 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            String input = Iterables.getOnlyElement(Resources.readLines(Resources.getResource("y2021/Y2021D07.txt"), StandardCharsets.UTF_8));

            // 1
            assertThat(part1(example)).isEqualTo(37);
            assertThat(part1(input)).isEqualTo(356922);

            // 2
            assertThat(fuelCost(3)).isEqualTo(6);
            assertThat(fuelCost(11)).isEqualTo(66);
            assertThat(part2(example)).isEqualTo(168);
            assertThat(part2(input)).isEqualTo(100347031);

        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private static long part1(String input) {
        List<Integer> xs = Arrays.stream(input.split(",")).map(i -> Integer.parseInt(i)).collect(Collectors.toList());

        int min = xs.stream().mapToInt(x -> x).min().getAsInt();
        int max = xs.stream().mapToInt(x -> x).max().getAsInt();

        int minFuel = Integer.MAX_VALUE;

        for (int groupAt = min; groupAt <= max; groupAt++) {
            int fuelCost = 0;
            for (int x : xs) {
                fuelCost += Math.abs(x - groupAt);
            }
            minFuel = Math.min(fuelCost, minFuel);
        }

        return minFuel;
    }

    private static long part2(String input) {
        List<Integer> xs = Arrays.stream(input.split(",")).map(i -> Integer.parseInt(i)).collect(Collectors.toList());

        int min = xs.stream().mapToInt(x -> x).min().getAsInt();
        int max = xs.stream().mapToInt(x -> x).max().getAsInt();

        int minFuel = Integer.MAX_VALUE;

        for (int groupAt = min; groupAt <= max; groupAt++) {
            int fuelCost = 0;
            for (int x : xs) {
                fuelCost += fuelCost(Math.abs(x - groupAt));
            }
            minFuel = Math.min(fuelCost, minFuel);
        }

        return minFuel;
    }

    private static int fuelCost(int dist) {
        return dist * (dist + 1) / 2;
    }

    private static String example = "16,1,2,0,4,2,7,1,2,14";
}

package y2020;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class Y2020D01 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        int[] input = Resources.readLines(Resources.getResource("y2020/Y2020D01.txt"), StandardCharsets.UTF_8)
                .stream()
                .mapToInt(Integer::parseInt)
                .toArray();
        part1(input);
        part2(input);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static void part2(int[] input) {
        int target = 2020;
        for (int i = 0; i < input.length; i++) {
            int ii = input[i];
            for (int j = i + 1; j < input.length; j++) {
                int jj = input[j];
                for (int k = j + 1; k < input.length; k++) {
                    int kk = input[k];
                    if (ii + jj + kk == target) {
                        System.out.printf(
                                "Found: %s + %s + %s = %s , %s * %s * %s = %s\n",
                                ii, jj, kk, ii + jj + kk, ii, jj, kk, ii * jj * kk);
                    }
                }
            }
        }
    }

    private static void part1(int[] input) {
        int target = 2020;
        boolean[] seen = new boolean[target];
        for (int i : input) {
            seen[i] = true;
            int j = target - i;
            if (seen[j]) {
                System.out.printf(
                        "Found: %s + %s = %s , %s * %s = %s\n",
                        i, j, i + j, i, j, i * j);
            }
        }
    }

}

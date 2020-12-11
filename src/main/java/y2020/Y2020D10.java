package y2020;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;

public class Y2020D10 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        List<Integer> input = Resources.readLines(Resources.getResource("y2020/Y2020D10.txt"), StandardCharsets.UTF_8)
                .stream()
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        System.out.println("Part1, example 1:");
        part1(example1);
        System.out.println("Part1:");
        part1(input);
        System.out.println("Part2, example 1:");
        part2(example1);
        System.out.println("Part2:");
        part2(input);


        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static void part1(List<Integer> input) {

        // If you use every adapter in your bag at once, what
        // is the distribution of joltage differences between
        // the charging outlet, the adapters, and your device?

        // jolts always increase
        input.sort(Comparator.naturalOrder());
        int[] countsByStepSize = new int[4];
        int inputJolts = 0;
        for (int adapterJolts : input) {
            checkState(inputJolts < adapterJolts && (inputJolts + 3) >= adapterJolts);
            int stepSize = adapterJolts - inputJolts;
            countsByStepSize[stepSize]++;
            inputJolts = adapterJolts;
        }
        countsByStepSize[3]++;
        System.out.println("number of 1-jolt differences multiplied by the number of 3-jolt differences = " + (countsByStepSize[1] * countsByStepSize[3]));
    }

    private static void part2(List<Integer> input) {
        input.sort(Comparator.naturalOrder());

        // DP counting back from the end:
        int deviceJolts = input.get(input.size() - 1) + 3;
        long[] howManyWays = new long[input.size()];
        for (int i = input.size() - 1; i >= 0; i--) {
            // The number of different arrangements at i is the sum of the different arrangements
            // of each of the reachable next adapters
            long arrangementCount = 0;
            int adapterJolts = input.get(i);
            for (int j = 1; (j <= 3 && (i + j) <= input.size()); j++) {

                if (i + j >= input.size()) {
                    if (adapterJolts < deviceJolts && (adapterJolts + 3) >= deviceJolts) {
                        arrangementCount++;
                    }
                    break;
                } else {
                    int nextAdapterJolts = input.get(i + j);
                    if (adapterJolts < nextAdapterJolts && (adapterJolts + 3) >= nextAdapterJolts) {
                        arrangementCount += howManyWays[i + j];
                    } else {
                        break;
                    }
                }
            }
            howManyWays[i] = arrangementCount;
        }

        long countsFromZero = 0;
        for (int i = 0; i < 4; i++) {
            if (input.get(i) <= 3) {
                countsFromZero += howManyWays[i];
            }
        }
        System.out.println("total number = " + countsFromZero);
    }


    static List<Integer> example1 = Arrays.asList(
            16,
            10,
            15,
            5,
            1,
            11,
            7,
            19,
            6,
            12,
            4);
}

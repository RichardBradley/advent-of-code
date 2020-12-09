package y2020;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Y2020D09 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        List<Long> input = Resources.readLines(Resources.getResource("y2020/Y2020D09.txt"), StandardCharsets.UTF_8)
                .stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());

        long target = part1(input);
        part2(target, input);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static long part1(List<Long> input) {
        // What is the first number that is not the sum of two of the 25 numbers before it.
        for (int i = 25; i < input.size(); i++) {
            Long ii = input.get(i);
            if (!canSum(ii, input.subList(i - 25, i))) {
                System.out.println("First non sum is " + ii);
                return ii;
            }
        }
        throw new IllegalStateException();
    }

    private static void part2(long target, List<Long> input) {
        // find a contiguous set of at least two numbers in your list
        // which sum to the invalid number from step 1.
        List<Long> subList = findSublistBySum(target, input);

        // add together the smallest and largest number in this contiguous range;
        System.out.println("encryption weakness: " +
                (subList.stream().mapToLong(x -> x).min().getAsLong()
                        + subList.stream().mapToLong(x -> x).max().getAsLong()));
    }

    private static List<Long> findSublistBySum(long target, List<Long> input) {
        for (int i = 0; i < input.size(); i++) {
            long sum = input.get(i);
            for (int j = i + 1; sum < target; j++) {
                sum += input.get(j);
                if (sum == target) {
                    List<Long> subList = input.subList(i, j + 1);
                    System.out.println("Found sublist at " + i + ": " + subList);
                    return subList;
                }
            }
        }
        throw new IllegalStateException();
    }

    private static boolean canSum(Long target, List<Long> list) {
        for (int i = 0; i < list.size(); i++) {
            Long ii = list.get(i);
            for (int j = i + 1; j < list.size(); j++) {
                Long jj = list.get(j);
                if (ii + jj == target) {
                    return true;
                }
            }
        }
        return false;
    }
}

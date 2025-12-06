package y2025;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static aoc.Common.loadInputFromResources;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2025D06 {

    public static void main(String[] args) throws Exception {
        List<String> input = loadInputFromResources();
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(part1(example)).isEqualTo(4277556);
        assertThat(part1(input)).isEqualTo(7644505810277L);

        // 2
        assertThat(part2(example)).isEqualTo(3263827);
        assertThat(part2(input)).isEqualTo(12841228084455L);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static long part1(List<String> input) {
        List<List<Long>> numbers = input.stream()
                .limit(input.size() - 1)
                .map(line ->
                        Splitter.on(" ").trimResults()
                                .omitEmptyStrings()
                                .splitToList(line)
                                .stream()
                                .map(Long::parseLong)
                                .collect(Collectors.toList()))
                .collect(Collectors.toList());

        List<String> ops = Splitter.on(" ").trimResults()
                .omitEmptyStrings()
                .splitToList(input.get(input.size() - 1));

        int len = ops.size();
        numbers.forEach(line -> checkState(line.size() == len));

        long sum = 0;
        for (int x = 0; x < len; x++) {
            String op = ops.get(x);
            switch (op) {
                case "+":
                    sum += getCol(numbers, x)
                            .sum();
                    break;
                case "*":
                    sum += getCol(numbers, x)
                            .reduce(1, (a, b) -> a * b);
                    break;
                default:
                    throw new IllegalArgumentException("Op: " + op);
            }
        }
        return sum;
    }

    private static LongStream getCol(List<List<Long>> numbers, int x) {
        return numbers.stream()
                .mapToLong(line -> line.get(x));
    }

    private static long part2(List<String> input) {
        // pad the end to assist parsing below
        input = input.stream().map(line -> line + " ").collect(Collectors.toList());

        int len = input.get(0).length();
        input.forEach(line -> checkState(line.length() == len));
        int numHeight = input.size() - 1;

        List<Long> numbers = new ArrayList<>();
        StringBuilder buff = new StringBuilder();
        long sum = 0;
        int groupXStart = 0;
        for (int x = 0; x < len; x++) {
            buff.setLength(0);
            for (int y = 0; y < numHeight; y++) {
                buff.append(input.get(y).charAt(x));
            }
            String num = buff.toString().trim();
            if (num.isEmpty()) {
                char op = input.get(input.size() - 1).charAt(groupXStart);
                switch (op) {
                    case '+':
                        sum += numbers.stream().mapToLong(Long::longValue).sum();
                        break;
                    case '*':
                        sum += numbers.stream().mapToLong(Long::longValue).reduce(1, (a, b) -> a * b);
                        break;
                    default:
                        throw new IllegalArgumentException("Op: " + op);
                }
                numbers.clear();
                groupXStart = x + 1;
            } else {
                numbers.add(Long.parseLong(num));
            }
        }

        return sum;
    }

    static List<String> example = List.of(
            "123 328  51 64 ",
            " 45 64  387 23 ",
            "  6 98  215 314",
            "*   +   *   +  ");
}

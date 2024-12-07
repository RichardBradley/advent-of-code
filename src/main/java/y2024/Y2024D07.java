package y2024;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import lombok.Value;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static aoc.Common.loadInputFromResources;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2024D07 {

    public static void main(String[] args) throws Exception {
        List<String> input = loadInputFromResources();
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(part1(example)).isEqualTo(3749);
        assertThat(part1(input)).isEqualTo(5702958180383L);

        // 2
        assertThat(part2(example)).isEqualTo(11387);
        assertThat(part2(input)).isEqualTo(92612386119138L);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    @Value
    static class InputLine {
        long target;
        List<Long> equation;
    }

    private static long part1(List<String> input) {
        return input.stream()
                .map(Y2024D07::parse)
                .filter(x -> isSatisfiable(x, x.equation.get(0), 1))
                .mapToLong(x -> x.target)
                .sum();
    }

    private static boolean isSatisfiable(InputLine inputLine, long sumSoFar, int nextIdx) {
        if (nextIdx == inputLine.equation.size()) {
            return sumSoFar == inputLine.target;
        } else {
            return isSatisfiable(inputLine, sumSoFar + inputLine.equation.get(nextIdx), nextIdx + 1)
                    || isSatisfiable(inputLine, sumSoFar * inputLine.equation.get(nextIdx), nextIdx + 1);
        }
    }

    static Pattern inputPat = Pattern.compile("(\\d+): ([ \\d]+)");

    private static InputLine parse(String line) {
        Matcher m = inputPat.matcher(line);
        checkState(m.matches());
        long target = Long.parseLong(m.group(1));
        List<Long> equation = Splitter.on(' ')
                .splitToList(m.group(2))
                .stream().map(Long::parseLong)
                .collect(Collectors.toList());
        return new InputLine(target, equation);
    }

    private static long part2(List<String> input) {
        return input.stream()
                .map(x -> parse(x))
                .filter(x -> isSatisfiable2(x, x.equation.get(0), 1))
                .mapToLong(x -> x.target)
                .sum();
    }

    private static boolean isSatisfiable2(InputLine inputLine, long sumSoFar, int nextIdx) {
        if (nextIdx == inputLine.equation.size()) {
            return sumSoFar == inputLine.target;
        } else {
            return isSatisfiable2(inputLine, sumSoFar + inputLine.equation.get(nextIdx), nextIdx + 1)
                    || isSatisfiable2(inputLine, sumSoFar * inputLine.equation.get(nextIdx), nextIdx + 1)
                    || isSatisfiable2(inputLine, Long.parseLong(sumSoFar + "" + inputLine.equation.get(nextIdx)), nextIdx + 1);
        }
    }

    static List<String> example = List.of(
            "190: 10 19",
            "3267: 81 40 27",
            "83: 17 5",
            "156: 15 6",
            "7290: 6 8 6 15",
            "161011: 16 10 13",
            "192: 17 8 14",
            "21037: 9 7 18 13",
            "292: 11 6 16 20");
}

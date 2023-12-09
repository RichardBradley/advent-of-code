package y2023;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Iterables;
import com.google.common.io.Resources;
import org.apache.commons.math3.util.Pair;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.getLast;
import static com.google.common.truth.Truth.assertThat;
import static org.apache.commons.math3.util.ArithmeticUtils.lcm;

public class Y2023D09 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            List<String> input = Resources.readLines(Resources.getResource("y2023/Y2023D09.txt"), StandardCharsets.UTF_8);

            // 1
            assertThat(part1(example)).isEqualTo(114);
            assertThat(part1(input)).isEqualTo(1901217887);

            // 2
            assertThat(part2(example)).isEqualTo(2);
            assertThat(part2(input)).isEqualTo(0);

        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private static long part1(List<String> input) {
        // Analyze your OASIS report and extrapolate the next value for each history.
        // What is the sum of these extrapolated values?
        long acc = 0;
        for (String line : input) {
            List<Long> values = Splitter.on(" ").splitToList(line).stream().map(Long::parseLong).collect(Collectors.toList());
            extend(values);
            acc += values.get(values.size() - 1);
        }
        return acc;
    }

    private static void extend(List<Long> values) {
        List<Long> deltas = new ArrayList<>();
        boolean allDeltasAreZero = true;
        for (int i = 0; i < values.size() - 1; i++) {
            long diff = values.get(i + 1) - values.get(i);
            deltas.add(diff);
            if (diff != 0) {
                allDeltasAreZero = false;
            }
        }
        checkState(deltas.size() > 1);
        if (allDeltasAreZero) {
            values.add(getLast(values));
        } else {
            extend(deltas);
            values.add(getLast(values) + getLast(deltas));
        }
    }

    private static long part2(List<String> input) {
        // this time extrapolating the previous value for each history. What is the sum of these extrapolated values?
        return part1(input.stream()
                .map(line -> {
                    List<Long> values = Splitter.on(" ").splitToList(line).stream().map(Long::parseLong).collect(Collectors.toList());
                    Collections.reverse(values);
                    return values.stream().map(i -> Long.toString(i)).collect(Collectors.joining(" "));
                })
                .collect(Collectors.toList()));
    }

    static List<String> example = List.of(
            "0 3 6 9 12 15",
            "1 3 6 10 15 21",
            "10 13 16 21 30 45"
    );
}

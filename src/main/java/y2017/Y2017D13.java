package y2017;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2017D13 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        List<String> input = Resources.readLines(Resources.getResource("y2017/Y2017D13.txt"), StandardCharsets.UTF_8);

        assertThat(severity(example)).isEqualTo(24);
        assertThat(severity(input)).isEqualTo(1728);

        assertThat(bestDelay(example)).isEqualTo(10);
        assertThat(bestDelay(input)).isEqualTo(3946838);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static int bestDelay(List<String> input) {
        Map<Integer, Integer> rangesByDepth = parse(input);
        for (int delay = 0; delay < 100000000; delay++) {
            if (severity(rangesByDepth, delay) < 0) {
                return delay;
            }
        }
        return -1;
    }

    private static int severity(List<String> input) {
        Map<Integer, Integer> rangesByDepth = parse(input);
        return severity(rangesByDepth, 0);
    }

    private static int severity(Map<Integer, Integer> rangesByDepth, int initialdelay) {
        int severity = -1;
        for (Map.Entry<Integer, Integer> entry : rangesByDepth.entrySet()) {
            int depth = entry.getKey();
            int range = entry.getValue();

            int oscillationPeriod = range * 2 - 2;
            boolean isCaught = 0 == ((depth + initialdelay) % oscillationPeriod);

            if (isCaught) {
                severity = Math.max(0, severity) + depth * range;
            }
        }

        return severity;
    }

    private static Map<Integer, Integer> parse(List<String> input) {
        Map<Integer, Integer> rangesByDepth = new LinkedHashMap<>();
        Pattern linePat = Pattern.compile("(\\d+): (\\d+)");
        for (String line : input) {
            Matcher matcher = linePat.matcher(line);
            checkState(matcher.matches());
            int depth = Integer.parseInt(matcher.group(1));
            int range = Integer.parseInt(matcher.group(2));
            checkState(null == rangesByDepth.put(depth, range));
        }
        return rangesByDepth;
    }

    static List<String> example = ImmutableList.of(
            "0: 3",
            "1: 2",
            "4: 4",
            "6: 4");
}


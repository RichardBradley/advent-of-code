package y2023;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;
import lombok.Value;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2023D06 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        List<String> input = Resources.readLines(Resources.getResource("y2023/Y2023D06.txt"), StandardCharsets.UTF_8);

        // 1
        assertThat(part1(example)).isEqualTo(288);
        assertThat(part1(input)).isEqualTo(1731600);

        // 2
        assertThat(part2(example)).isEqualTo(71503);
        assertThat(part2(input)).isEqualTo(40087680);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static long part1(List<String> input) {
        // determine the number of ways you can beat the record in each race
        // What do you get if you multiply these numbers together?
        List<Long> times = Splitter.on(" ")
                .omitEmptyStrings()
                .splitToList(input.get(0).substring("Time:".length())).stream()
                .map(s -> Long.parseLong(s))
                .collect(Collectors.toList());
        List<Long> distances = Splitter.on(" ")
                .omitEmptyStrings()
                .splitToList(input.get(1).substring("Distance:".length())).stream()
                .map(s -> Long.parseLong(s))
                .collect(Collectors.toList());
        assertThat(times.size()).isEqualTo(distances.size());

        long acc = 1;
        for (int raceIdx = 0; raceIdx < times.size(); raceIdx++) {
            long raceTime = times.get(raceIdx);
            long recordDistance = distances.get(raceIdx);
            int winningCount = 0;

            for (int chargeTime = 1; chargeTime < raceTime; chargeTime++) {
                long remainingTime = raceTime - chargeTime;
                long distanceCovered = remainingTime * chargeTime;
                if (distanceCovered > recordDistance) {
                    winningCount++;
                }
            }

            acc *= winningCount;
        }

        return acc;
    }

    private static long part2(List<String> input) {
        // determine the number of ways you can beat the record in each race
        // What do you get if you multiply these numbers together?
        long raceTime = Long.parseLong(input.get(0).substring("Time:".length()).replaceAll(" ", ""));
        long recordDistance = Long.parseLong(input.get(1).substring("Distance:".length()).replaceAll(" ", ""));

        long winningCount = 0;

        for (int chargeTime = 1; chargeTime < raceTime; chargeTime++) {
            long remainingTime = raceTime - chargeTime;
            long distanceCovered = remainingTime * chargeTime;
            if (distanceCovered > recordDistance) {
                winningCount++;
            }
        }

        return winningCount;
    }

    static List<String> example = List.of(
            "Time:      7  15   30",
            "Distance:  9  40  200"
    );
}

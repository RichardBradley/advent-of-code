package y2022;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2022D04 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        List<String> input = Resources.readLines(Resources.getResource("y2022/Y2022D04.txt"), StandardCharsets.UTF_8);

        // 1
        assertThat(part1(example)).isEqualTo(2);
        System.out.println(part1(input));

        // 2
        assertThat(part2(example)).isEqualTo(4);
        System.out.println(part2(input));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static int part1(List<String> input) {
        // In how many assignment pairs does one range fully contain the other?
        int count = 0;
        Pattern p = Pattern.compile("([0-9]+)-([0-9]+),([0-9]+)-([0-9]+)");
        for (String line : input) {
            Matcher m = p.matcher(line);
            checkState(m.matches());
            int aFrom = Integer.parseInt(m.group(1));
            int aTo = Integer.parseInt(m.group(2));
            int bFrom = Integer.parseInt(m.group(3));
            int bTo = Integer.parseInt(m.group(4));
            if ((aFrom <= bFrom && aTo >= bTo)
                    || (bFrom <= aFrom && bTo >= aTo)) {
                count++;
            }
        }
        return count;
    }

    private static int part2(List<String> input) {
        // the number of pairs that overlap at all.
        int count = 0;
        Pattern p = Pattern.compile("([0-9]+)-([0-9]+),([0-9]+)-([0-9]+)");
        for (String line : input) {
            Matcher m = p.matcher(line);
            checkState(m.matches());
            int aFrom = Integer.parseInt(m.group(1));
            int aTo = Integer.parseInt(m.group(2));
            int bFrom = Integer.parseInt(m.group(3));
            int bTo = Integer.parseInt(m.group(4));
            if ((aTo >= bFrom && aFrom <= bTo)
                    || (bTo >= aFrom && bFrom <= aTo)) {
                count++;

            }
        }
        return count;
    }

    private static List<String> example = List.of(
            "2-4,6-8",
            "2-3,4-5",
            "5-7,7-9",
            "2-8,3-7",
            "6-6,4-6",
            "2-6,4-8");
}

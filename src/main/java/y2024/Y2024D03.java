package y2024;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.truth.Truth.assertThat;

public class Y2024D03 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        List<String> input = Resources.readLines(Resources.getResource("y2024/Y2024D03.txt"), StandardCharsets.UTF_8);

        // 1
        assertThat(part1(example)).isEqualTo(161);
        assertThat(part1(input)).isEqualTo(188116424);

        // 2
        assertThat(part2(example2)).isEqualTo(48);
        assertThat(part2(input)).isEqualTo(104245808);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static long part1(List<String> input) {
        Pattern p = Pattern.compile("mul\\((\\d{1,3}),(\\d{1,3})\\)");
        int acc = 0;
        for (String line : input) {
            Matcher m = p.matcher(line);
            while (m.find()) {
                int a = Integer.parseInt(m.group(1));
                int b = Integer.parseInt(m.group(2));
                acc += (a * b);
            }
        }

        return acc;
    }

    private static long part2(List<String> input) {
        Pattern p = Pattern.compile("mul\\((\\d{1,3}),(\\d{1,3})\\)|(do)\\(\\)|(don't)\\(\\)");
        int acc = 0;
        boolean enabled = true;
        for (String line : input) {
            Matcher m = p.matcher(line);
            while (m.find()) {
                if (null != m.group(1)) {
                    if (enabled) {
                        int a = Integer.parseInt(m.group(1));
                        int b = Integer.parseInt(m.group(2));
                        acc += (a * b);
                    }
                } else if (null != m.group(3)) {
                    enabled = true;
                } else if (null != m.group(4)) {
                    enabled = false;
                } else {
                    throw new IllegalStateException();
                }
            }
        }

        return acc;
    }

    static List<String> example = List.of(
            "xmul(2,4)%&mul[3,7]!@^do_not_mul(5,5)+mul(32,64]then(mul(11,8)mul(8,5))");
    static List<String> example2 = List.of(
            "xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))");
}

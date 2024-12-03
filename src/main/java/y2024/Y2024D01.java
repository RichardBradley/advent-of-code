package y2024;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;
import org.junit.Assert;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static aoc.Common.loadInputFromResources;
import static com.google.common.truth.Truth.assertThat;

public class Y2024D01 {

    public static void main(String[] args) throws Exception {
        List<String> input = loadInputFromResources();
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(part1(example)).isEqualTo(11);
        System.out.println(part1(input));

        // 2
        assertThat(part2(example)).isEqualTo(31);
        System.out.println(part2(input));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static long part1(List<String> input) {
        List<Integer> l1 = new ArrayList<>();
        List<Integer> l2 = new ArrayList<>();

        Pattern p = Pattern.compile("^(\\d+) +(\\d+)$");
        for (String line : input) {
            Matcher m = p.matcher(line);
            Assert.assertTrue(m.matches());

            l1.add(Integer.parseInt(m.group(1)));
            l2.add(Integer.parseInt(m.group(2)));
        }

        l1.sort(Comparator.naturalOrder());
        l2.sort(Comparator.naturalOrder());

        assertThat(l1.size()).isEqualTo(l2.size());

        long totalDist = 0;
        for (int i = 0; i < l1.size(); i++) {
            totalDist += Math.abs(l1.get(i) - l2.get(i));
        }
        return totalDist;
    }

    private static long part2(List<String> input) {
        List<Integer> l1 = new ArrayList<>();
        List<Integer> l2 = new ArrayList<>();

        Pattern p = Pattern.compile("^(\\d+) +(\\d+)$");
        for (String line : input) {
            Matcher m = p.matcher(line);
            Assert.assertTrue(m.matches());

            l1.add(Integer.parseInt(m.group(1)));
            l2.add(Integer.parseInt(m.group(2)));
        }

        assertThat(l1.size()).isEqualTo(l2.size());

        long acc = 0;
        for (int i = 0; i < l1.size(); i++) {
            int x = l1.get(i);
            long count = l2.stream().filter(y -> x == y).count();
            acc += x * count;
        }
        return acc;
    }

    static List<String> example = List.of(
            "3   4",
            "4   3",
            "2   5",
            "1   3",
            "3   9",
            "3   3"
    );
}

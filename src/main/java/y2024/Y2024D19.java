package y2024;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static aoc.Common.loadInputFromResources;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2024D19 {

    public static void main(String[] args) throws Exception {
        List<String> input = loadInputFromResources();
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(part1(example)).isEqualTo(6);
        assertThat(part1(input)).isEqualTo(258);

        // 2
        assertThat(part2(example)).isEqualTo(16);
        assertThat(part2(input)).isEqualTo(632423618484345L);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static long part1(List<String> input) {
        List<String> availableTowels = Splitter.on(", ").splitToList(input.get(0));
        checkState("".equals(input.get(1)));

        int possibleCount = 0;
        for (int i = 2; i < input.size(); i++) {
            String target = input.get(i);
            if (canMake(target, availableTowels, 0)) {
                possibleCount++;
            }
        }
        return possibleCount;
    }

    static boolean canMake(String target, List<String> availableTowels, int idx) {
        if (idx == target.length()) {
            return true;
        }
        for (String availableTowel : availableTowels) {
            if (target.startsWith(availableTowel, idx)) {
                if (canMake(target, availableTowels, idx + availableTowel.length())) {
                    return true;
                }
            }
        }
        return false;
    }

    private static long part2(List<String> input) {
        List<String> availableTowels = Splitter.on(", ").splitToList(input.get(0));
        checkState("".equals(input.get(1)));

        long count = 0;
        Map<String, Long> wayCountCache = new HashMap<>();
        for (int i = 2; i < input.size(); i++) {
            String target = input.get(i);
            count += wayCount(target, availableTowels, wayCountCache);
        }
        return count;
    }


    static long wayCount(String target, List<String> availableTowels, Map<String, Long> wayCountCache) {
        if ("".equals(target)) {
            return 1;
        }
        Long cached = wayCountCache.get(target);
        if (cached != null) {
            return cached;
        }

        long count = 0;
        for (String availableTowel : availableTowels) {
            if (target.startsWith(availableTowel)) {
                count += wayCount(target.substring(availableTowel.length()), availableTowels, wayCountCache);
            }
        }
        wayCountCache.put(target, count);
        return count;
    }

    static List<String> example = List.of(
            "r, wr, b, g, bwu, rb, gb, br",
            "",
            "brwrr",
            "bggr",
            "gbbr",
            "rrbgbr",
            "ubwu",
            "bwurrg",
            "brgr",
            "bbrgwb");
}

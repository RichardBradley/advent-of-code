package y2022;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Iterables;
import com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2022D03 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        List<String> input = Resources.readLines(Resources.getResource("y2022/Y2022D03.txt"), StandardCharsets.UTF_8);

        // 1
        assertThat(part1(example)).isEqualTo(157);
        System.out.println(part1(input));

        // 2
        assertThat(part2(example)).isEqualTo(70);
        System.out.println(part2(input));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static int part1(List<String> input) {
        int score = 0;
        for (String line : input) {
            checkState((line.length() % 2) == 0);
            int halfLen = line.length() / 2;
            firstLetterLoop:
            for (int i = 0; i < halfLen; i++) {
                char c = line.charAt(i);
                for (int j = halfLen; j < line.length(); j++) {
                    if (c == line.charAt(j)) {
                        score += priority(c);
                        break firstLetterLoop;
                    }
                }
            }
        }
        return score;
    }


    private static int part2(List<String> input) {
        int score = 0;
        checkState((input.size() % 3) == 0);
        for (int i = 0; i < input.size(); i += 3) {
            Set<Integer> items = input.get(i).chars().boxed().collect(Collectors.toSet());
            items.retainAll(input.get(i + 1).chars().boxed().collect(Collectors.toList()));
            items.retainAll(input.get(i + 2).chars().boxed().collect(Collectors.toList()));

            score += priority((char) (int) Iterables.getOnlyElement(items));
        }
        return score;
    }

    private static int priority(char c) {
        if (c <= 'Z') {
            return (c - 'A' + 27);
        }
        return c - 'a' + 1;
    }

    private static List<String> example = List.of(
            "vJrwpWtwJgWrhcsFMMfFFhFp",
            "jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL",
            "PmmdzqPrVvPwwTWBwg",
            "wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn",
            "ttgJtRGJQctTZtZT",
            "CrZsJsPPZsGzwwsLwLmpwMDw");
}

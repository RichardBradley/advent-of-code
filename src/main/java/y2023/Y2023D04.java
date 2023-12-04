package y2023;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2023D04 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        List<String> input = Resources.readLines(Resources.getResource("y2023/Y2023D04.txt"), StandardCharsets.UTF_8);

        // 1
        assertThat(part1(example)).isEqualTo(13);
        assertThat(part1(input)).isEqualTo(26914);

        // 2
        assertThat(part2(example)).isEqualTo(30);
        assertThat(part2(input)).isEqualTo(1);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static long part1(List<String> input) {
        // How many points are they worth in total?
        long sum = 0;
        Pattern p = Pattern.compile("Card +(\\d+): ([\\d ]+) \\| ([\\d ]+)");
        for (String line : input) {
            Matcher m = p.matcher(line);
            checkState(m.matches());
            List<Integer> winningNum = Splitter.on(" ")
                    .omitEmptyStrings()
                    .splitToList(m.group(2)).stream()
                    .map(s -> Integer.parseInt(s))
                    .collect(Collectors.toList());
            List<Integer> numHave = Splitter.on(" ")
                    .omitEmptyStrings()
                    .splitToList(m.group(3)).stream()
                    .map(s -> Integer.parseInt(s))
                    .collect(Collectors.toList());

            int cardValue = 0;
            for (Integer i : numHave) {
                if (winningNum.contains(i)) {
                    if (cardValue == 0) {
                        cardValue = 1;
                    } else {
                        cardValue *= 2;
                    }
                }
            }
            sum += cardValue;
        }

        return sum;
    }

    private static long part2(List<String> input) {
        Map<Integer, Integer> countsByCardIdx = new HashMap<>();
        long sum = 0;
        Pattern p = Pattern.compile("Card +(\\d+): ([\\d ]+) \\| ([\\d ]+)");
        for (int cardIdx = 0; cardIdx < input.size(); cardIdx++) {
            int currCardCount = countsByCardIdx.compute(cardIdx, (k, v) -> v == null ? 1 : v + 1);

            String line = input.get(cardIdx);
            Matcher m = p.matcher(line);
            checkState(m.matches());
            List<Integer> winningNum = Splitter.on(" ")
                    .omitEmptyStrings()
                    .splitToList(m.group(2)).stream()
                    .map(s -> Integer.parseInt(s))
                    .collect(Collectors.toList());
            List<Integer> numHave = Splitter.on(" ")
                    .omitEmptyStrings()
                    .splitToList(m.group(3)).stream()
                    .map(s -> Integer.parseInt(s))
                    .collect(Collectors.toList());

            int cardValue = 0;
            for (Integer i : numHave) {
                if (winningNum.contains(i)) {
                    cardValue++;
                }
            }

            for (int i = 1; i <= cardValue; i++) {
                countsByCardIdx.compute(
                        cardIdx + i,
                        (k, v) -> (v == null ? 0 : v) + currCardCount);
            }
        }

        return countsByCardIdx.values().stream().mapToInt(i -> i).sum();
    }

    static List<String> example = List.of(
            "Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53",
            "Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19",
            "Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1",
            "Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83",
            "Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36",
            "Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11"
    );
}

package y2023;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;
import lombok.Value;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;

public class Y2023D07 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            List<String> input = Resources.readLines(Resources.getResource("y2023/Y2023D07.txt"), StandardCharsets.UTF_8);

            // 1
            assertThat(part1(example)).isEqualTo(6440);
            assertThat(part1(input)).isEqualTo(241344943);

            // 2
            assertThat(new Hand("QJJQ2", 1, true).handType).isEqualTo(HandTypes.FOUR_OAK);
            assertThat(new Hand("JKKK2", 1, true)).isLessThan(new Hand("QQQQ2", 1, true));

            assertThat(part2(example)).isEqualTo(5905);
            assertThat(part2(input)).isEqualTo(243101568);

        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    static class HandTypes {
        static int HIGH_CARD = 1;
        static int ONE_PAIR = 2;
        static int TWO_PAIR = 3;
        static int THREE_OAK = 4;
        static int FULL_HOUSE = 5;
        static int FOUR_OAK = 6;
        static int FIVE_OAK = 7;
    }

    static int getHandType(String hand, boolean jIsWild) {
        Map<Character, Integer> charsToCount = hand.chars().boxed()
                .collect(Collectors.toMap(c -> (char) (int) c, c -> 1, (a, b) -> a + b));

        if (jIsWild) {
            Integer jCount = charsToCount.get('J');
            if (null != jCount) {
                charsToCount.remove('J');
                // add J to largest group
                Character largestGroup = charsToCount.entrySet().stream()
                        .sorted(Map.Entry.<Character, Integer>comparingByValue().reversed())
                        .findFirst()
                        .map(e -> e.getKey())
                        .orElse('J');

                charsToCount.compute(largestGroup, (k, v) -> (v == null ? 0 : v) + jCount);
            }
        }

        String countSig = charsToCount.values().stream().sorted().map(i -> Integer.toString(i)).collect(Collectors.joining());
        switch (countSig) {
            case "5":
                return HandTypes.FIVE_OAK;
            case "14":
                return HandTypes.FOUR_OAK;
            case "23":
                return HandTypes.FULL_HOUSE;
            case "113":
                return HandTypes.THREE_OAK;
            case "122":
                return HandTypes.TWO_PAIR;
            case "1112":
                return HandTypes.ONE_PAIR;
            case "11111":
                return HandTypes.HIGH_CARD;
            default:
                throw new IllegalArgumentException("unexpected: " + countSig);
        }
    }

    @Value
    static class Hand implements Comparable<Hand> {
        int handType;
        String cards;
        int bid;

        public Hand(String cards, int bid, boolean jIsWild) {
            this.bid = bid;
            this.cards = cards
                    .replaceAll("A", "E")
                    .replaceAll("K", "D")
                    .replaceAll("Q", "C")
                    .replaceAll("J", jIsWild ? "0" : "B")
                    .replaceAll("T", "A");
            handType = Y2023D07.getHandType(cards, jIsWild);
        }

        @Override
        public int compareTo(Hand o) {
            int c = Integer.compare(this.handType, o.handType);
            if (c != 0) {
                return c;
            }
            return cards.compareTo(o.cards);
        }
    }

    private static long part1(List<String> input) {
        return run(input, false);
    }

    private static long part2(List<String> input) {
        return run(input, true);
    }

    private static long run(List<String> input, boolean jIsWild) {
        long acc = 0;

        List<Hand> hands = input.stream()
                .map(s -> {
                    String[] ab = s.split(" ");
                    return new Hand(ab[0], Integer.parseInt(ab[1]), jIsWild);
                })
                .sorted()
                .collect(Collectors.toList());

        for (int i = 0; i < hands.size(); i++) {
            acc += (i + 1) * hands.get(i).bid;
        }

        return acc;
    }

    static List<String> example = List.of(
            "32T3K 765",
            "T55J5 684",
            "KK677 28",
            "KTJJT 220",
            "QQQJA 483"
    );
}

package y2015;

import com.google.common.collect.Collections2;
import org.apache.commons.math3.util.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2015D13 {
    public static void main(String[] args) throws Exception {

        // 1
        assertThat(bestScore(testInput)).isEqualTo(330);

        System.out.println(bestScore(input));

        // 2
        String[] input2 = new String[input.length + 1];
        System.arraycopy(input, 0, input2, 0, input.length);
        input2[input2.length - 1] = "I would gain 0 happiness units by sitting next to Alice.";
        System.out.println(bestScore(input2));
    }

    private static int bestScore(String[] input) {
        Set<String> names = new HashSet<>();
        Map<Pair<String, String>, Integer> pairScores = new HashMap<>();

        Pattern pattern = Pattern.compile("(\\w+) would (gain|lose) (\\d+) happiness units by sitting next to (\\w+).");
        for (String line : input) {
            Matcher matcher = pattern.matcher(line);
            checkState(matcher.matches());
            String name1 = matcher.group(1);
            boolean isPos = "gain".equals(matcher.group(2));
            int score = Integer.parseInt(matcher.group(3)) * (isPos ? 1 : -1);
            String name2 = matcher.group(4);

            names.add(name1);
            names.add(name2);
            checkState(null == pairScores.put(new Pair<>(name1, name2), score));
        }

        int size = names.size();

        return Collections2.permutations(names)
                .stream()
                .mapToInt(possibleSeating -> {
                    int score = 0;
                    for (int i = 0; i < size; i++) {
                        String person = possibleSeating.get(i);
                        String neighbour1 = possibleSeating.get((i - 1 + size) % size);
                        String neighbour2 = possibleSeating.get((i + 1) % size);

                        score += pairScores.getOrDefault(new Pair<>(person, neighbour1), 0);
                        score += pairScores.getOrDefault(new Pair<>(person, neighbour2), 0);
                    }
                    return score;
                })
                .max()
                .getAsInt();
    }

    private static Pair<String, String> getKey(String x, String y) {
        if (x.compareTo(y) > 0) {
            return new Pair<>(y, x);
        }
        return new Pair<>(x, y);
    }

    private static String[] testInput = new String[]{
            "Alice would gain 54 happiness units by sitting next to Bob.",
            "Alice would lose 79 happiness units by sitting next to Carol.",
            "Alice would lose 2 happiness units by sitting next to David.",
            "Bob would gain 83 happiness units by sitting next to Alice.",
            "Bob would lose 7 happiness units by sitting next to Carol.",
            "Bob would lose 63 happiness units by sitting next to David.",
            "Carol would lose 62 happiness units by sitting next to Alice.",
            "Carol would gain 60 happiness units by sitting next to Bob.",
            "Carol would gain 55 happiness units by sitting next to David.",
            "David would gain 46 happiness units by sitting next to Alice.",
            "David would lose 7 happiness units by sitting next to Bob.",
            "David would gain 41 happiness units by sitting next to Carol.",
    };

    private static String[] input = new String[]{
            "Alice would gain 54 happiness units by sitting next to Bob.",
            "Alice would lose 81 happiness units by sitting next to Carol.",
            "Alice would lose 42 happiness units by sitting next to David.",
            "Alice would gain 89 happiness units by sitting next to Eric.",
            "Alice would lose 89 happiness units by sitting next to Frank.",
            "Alice would gain 97 happiness units by sitting next to George.",
            "Alice would lose 94 happiness units by sitting next to Mallory.",
            "Bob would gain 3 happiness units by sitting next to Alice.",
            "Bob would lose 70 happiness units by sitting next to Carol.",
            "Bob would lose 31 happiness units by sitting next to David.",
            "Bob would gain 72 happiness units by sitting next to Eric.",
            "Bob would lose 25 happiness units by sitting next to Frank.",
            "Bob would lose 95 happiness units by sitting next to George.",
            "Bob would gain 11 happiness units by sitting next to Mallory.",
            "Carol would lose 83 happiness units by sitting next to Alice.",
            "Carol would gain 8 happiness units by sitting next to Bob.",
            "Carol would gain 35 happiness units by sitting next to David.",
            "Carol would gain 10 happiness units by sitting next to Eric.",
            "Carol would gain 61 happiness units by sitting next to Frank.",
            "Carol would gain 10 happiness units by sitting next to George.",
            "Carol would gain 29 happiness units by sitting next to Mallory.",
            "David would gain 67 happiness units by sitting next to Alice.",
            "David would gain 25 happiness units by sitting next to Bob.",
            "David would gain 48 happiness units by sitting next to Carol.",
            "David would lose 65 happiness units by sitting next to Eric.",
            "David would gain 8 happiness units by sitting next to Frank.",
            "David would gain 84 happiness units by sitting next to George.",
            "David would gain 9 happiness units by sitting next to Mallory.",
            "Eric would lose 51 happiness units by sitting next to Alice.",
            "Eric would lose 39 happiness units by sitting next to Bob.",
            "Eric would gain 84 happiness units by sitting next to Carol.",
            "Eric would lose 98 happiness units by sitting next to David.",
            "Eric would lose 20 happiness units by sitting next to Frank.",
            "Eric would lose 6 happiness units by sitting next to George.",
            "Eric would gain 60 happiness units by sitting next to Mallory.",
            "Frank would gain 51 happiness units by sitting next to Alice.",
            "Frank would gain 79 happiness units by sitting next to Bob.",
            "Frank would gain 88 happiness units by sitting next to Carol.",
            "Frank would gain 33 happiness units by sitting next to David.",
            "Frank would gain 43 happiness units by sitting next to Eric.",
            "Frank would gain 77 happiness units by sitting next to George.",
            "Frank would lose 3 happiness units by sitting next to Mallory.",
            "George would lose 14 happiness units by sitting next to Alice.",
            "George would lose 12 happiness units by sitting next to Bob.",
            "George would lose 52 happiness units by sitting next to Carol.",
            "George would gain 14 happiness units by sitting next to David.",
            "George would lose 62 happiness units by sitting next to Eric.",
            "George would lose 18 happiness units by sitting next to Frank.",
            "George would lose 17 happiness units by sitting next to Mallory.",
            "Mallory would lose 36 happiness units by sitting next to Alice.",
            "Mallory would gain 76 happiness units by sitting next to Bob.",
            "Mallory would lose 34 happiness units by sitting next to Carol.",
            "Mallory would gain 37 happiness units by sitting next to David.",
            "Mallory would gain 40 happiness units by sitting next to Eric.",
            "Mallory would gain 18 happiness units by sitting next to Frank.",
            "Mallory would gain 7 happiness units by sitting next to George.",
    };
}

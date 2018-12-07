package y2015;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2015D15 {
    public static void main(String[] args) throws Exception {

        // 1
        assertThat(bestScore(testInput, null)).isEqualTo(62842880);

        System.out.println(bestScore(input, null));

        // 2
        assertThat(bestScore(testInput, 500)).isEqualTo(57600000);

        System.out.println(bestScore(input, 500));
    }

    static int scoringFactors = 4;

    private static int bestScore(String[] input, Integer targetCalories) {
        int[][] ingredients = parse(input);
        int maxSpoons = 100;

        return maxScore(ingredients, 0, maxSpoons, new int[scoringFactors + 1], targetCalories);
    }

    private static int maxScore(int[][] ingredients, int nextIngredientIdx, int spoonsRemaining, int[] currentScore, Integer targetCalories) {
        if (nextIngredientIdx == ingredients.length) {
            if (targetCalories != null && targetCalories != currentScore[scoringFactors]) {
                return 0;
            }

            int score = 1;
            for (int i = 0; i < scoringFactors; i++) {
                if (currentScore[i] < 0) {
                    score = 0;
                } else {
                    score *= currentScore[i];
                }
            }
            return score;
        }

        int[] thisIng = ingredients[nextIngredientIdx];
        int[] newScore = new int[scoringFactors + 1];
        int maxScore = 0;
        for (int spoonsOfThisIng = 0; spoonsOfThisIng <= spoonsRemaining; spoonsOfThisIng++) {
            for (int i = 0; i < scoringFactors + 1; i++) {
                newScore[i] = currentScore[i] + spoonsOfThisIng * thisIng[i];
            }
            int score = maxScore(ingredients, nextIngredientIdx + 1, spoonsRemaining - spoonsOfThisIng, newScore, targetCalories);
            if (score > maxScore) {
                maxScore = score;
            }
        }
        return maxScore;
    }

    private static int[][] parse(String[] input) {
        int[][] acc = new int[input.length][];

        Pattern pattern = Pattern.compile("\\w+: capacity (-?\\d+), durability (-?\\d+), flavor (-?\\d+), texture (-?\\d+), calories (-?\\d+)");
        for (int i = 0; i < input.length; i++) {
            String line = input[i];
            Matcher matcher = pattern.matcher(line);
            checkState(matcher.matches());
            acc[i] = new int[]{
                    Integer.parseInt(matcher.group(1)),
                    Integer.parseInt(matcher.group(2)),
                    Integer.parseInt(matcher.group(3)),
                    Integer.parseInt(matcher.group(4)),
                    Integer.parseInt(matcher.group(5)),
            };
        }
        return acc;
    }

    private static String[] testInput = new String[]{
            "Butterscotch: capacity -1, durability -2, flavor 6, texture 3, calories 8",
            "Cinnamon: capacity 2, durability 3, flavor -2, texture -1, calories 3"

    };

    private static String[] input = new String[]{
            "Frosting: capacity 4, durability -2, flavor 0, texture 0, calories 5",
            "Candy: capacity 0, durability 5, flavor -1, texture 0, calories 8",
            "Butterscotch: capacity -1, durability 0, flavor 5, texture 0, calories 6",
            "Sugar: capacity 0, durability 0, flavor -2, texture 2, calories 1"
    };
}

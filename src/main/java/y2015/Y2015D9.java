package y2015;

import com.google.common.collect.Collections2;
import javafx.util.Pair;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2015D9 {
    public static void main(String[] args) throws Exception {

        // 1
        assertThat(minRoute(testInput)).isEqualTo(605);

        System.out.println(minRoute(input));

        // 2
        assertThat(maxRoute(testInput)).isEqualTo(982);

        System.out.println(maxRoute(input));
    }

    private static int minRoute(String[] input) {
        return possibleRouteLengths(input).min().getAsInt();
    }

    private static int maxRoute(String[] input) {
        return possibleRouteLengths(input).max().getAsInt();
    }

    private static IntStream possibleRouteLengths(String[] input) {
        Set<String> locations = new HashSet<>();
        Map<Pair<String, String>, Integer> distances = new HashMap<>();

        Pattern pattern = Pattern.compile("(\\w+) to (\\w+) = (\\d+)");
        for (String line : input) {
            Matcher matcher = pattern.matcher(line);
            checkState(matcher.matches());
            String loc1 = matcher.group(1);
            String loc2 = matcher.group(2);
            int dist = Integer.parseInt(matcher.group(3));

            locations.add(loc1);
            locations.add(loc2);
            distances.put(getKey(loc1, loc2), dist);
        }


        return Collections2.permutations(locations)
                .stream()
                .mapToInt(possibleRoute -> {
                    int dist = 0;
                    for (int i = 0; i < possibleRoute.size() - 1; i++) {
                        dist += distances.get(getKey(possibleRoute.get(i), possibleRoute.get(i + 1)));
                    }
                    return dist;
                });
    }

    private static Pair<String, String> getKey(String x, String y) {
        if (x.compareTo(y) > 0) {
            return new Pair<>(y, x);
        }
        return new Pair<>(x, y);
    }

    private static String[] testInput = new String[]{
            "London to Dublin = 464",
            "London to Belfast = 518",
            "Dublin to Belfast = 141"
    };

    private static String[] input = new String[]{
            "Tristram to AlphaCentauri = 34",
            "Tristram to Snowdin = 100",
            "Tristram to Tambi = 63",
            "Tristram to Faerun = 108",
            "Tristram to Norrath = 111",
            "Tristram to Straylight = 89",
            "Tristram to Arbre = 132",
            "AlphaCentauri to Snowdin = 4",
            "AlphaCentauri to Tambi = 79",
            "AlphaCentauri to Faerun = 44",
            "AlphaCentauri to Norrath = 147",
            "AlphaCentauri to Straylight = 133",
            "AlphaCentauri to Arbre = 74",
            "Snowdin to Tambi = 105",
            "Snowdin to Faerun = 95",
            "Snowdin to Norrath = 48",
            "Snowdin to Straylight = 88",
            "Snowdin to Arbre = 7",
            "Tambi to Faerun = 68",
            "Tambi to Norrath = 134",
            "Tambi to Straylight = 107",
            "Tambi to Arbre = 40",
            "Faerun to Norrath = 11",
            "Faerun to Straylight = 66",
            "Faerun to Arbre = 144",
            "Norrath to Straylight = 115",
            "Norrath to Arbre = 135",
            "Straylight to Arbre = 127",
    };
}

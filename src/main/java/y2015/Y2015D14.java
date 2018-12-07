package y2015;

import lombok.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2015D14 {
    public static void main(String[] args) throws Exception {

        // 1
        assertThat(bestDistAfter(testInput, 1000)).isEqualTo(1120);

        System.out.println(bestDistAfter(input, 2503));

        // 2
        assertThat(bestScoreAfter(testInput, 1000)).isEqualTo(689);

        System.out.println(bestScoreAfter(input, 2503));
    }

    private static int bestScoreAfter(String[] input, int seconds) {
        List<ReindeerStats> reindeers = parse(input);
        int reindeerCount = reindeers.size();

        int[] flyTimeLeft = new int[reindeerCount];
        int[] restTimeLeft = new int[reindeerCount];
        int[] distances = new int[reindeerCount];
        int[] scores = new int[reindeerCount];

        for (int i = 0; i < reindeerCount; i++) {
            flyTimeLeft[i] = reindeers.get(i).flyDurationSec;
        }

        for (int t = 0; t<seconds; t++) {
            for (int i = 0; i < reindeerCount; i++) {
                ReindeerStats reindeer = reindeers.get(i);
                if (flyTimeLeft[i] > 0) {
                    distances[i] += reindeer.flySpeedKms;

                    if (0 == --flyTimeLeft[i]) {
                        restTimeLeft[i] = reindeer.restDurationSec;
                    }
                } else if (restTimeLeft[i] > 0) {
                    if (0 == --restTimeLeft[i]) {
                        flyTimeLeft[i] = reindeer.flyDurationSec;
                    }
                }
            }

            int maxDist = -1;
            for (int i = 0; i < reindeerCount; i++) {
                if (distances[i] > maxDist) {
                    maxDist = distances[i];
                }
            }
            for (int i = 0; i < reindeerCount; i++) {
                if (distances[i] == maxDist) {
                    scores[i] ++;
                }
            }
        }

        return Arrays.stream(scores).max().getAsInt();
    }

    private static int bestDistAfter(String[] input, int seconds) {
        return parse(input).stream()
                .mapToInt(reindeer -> distAfterSeconds(reindeer, seconds))
                .max().getAsInt();
    }

    private static List<ReindeerStats> parse(String[] input) {
        List<ReindeerStats> reindeers = new ArrayList<>();

        Pattern pattern = Pattern.compile("\\w+ can fly (\\d+) km/s for (\\d+) seconds, but then must rest for (\\d+) seconds.");
        for (String line : input) {
            Matcher matcher = pattern.matcher(line);
            checkState(matcher.matches());
            reindeers.add(new ReindeerStats(
                    Integer.parseInt(matcher.group(1)),
                    Integer.parseInt(matcher.group(2)),
                    Integer.parseInt(matcher.group(3))));
        }

        return reindeers;
    }

    private static int distAfterSeconds(ReindeerStats reindeer, int seconds) {
        int t = 0;
        int dist = 0;
        while (true) {
            // Fly:
            if (seconds - t >= reindeer.flyDurationSec) {
                t += reindeer.flyDurationSec;
                dist += reindeer.flySpeedKms * reindeer.flyDurationSec;
            } else {
                return dist + (seconds - t) * reindeer.flySpeedKms;
            }
            // Rest:
            if (seconds - t >= reindeer.restDurationSec) {
                t += reindeer.restDurationSec;
            } else {
                return dist;
            }
        }
    }

    @Value
    private static class ReindeerStats {
        int flySpeedKms;
        int flyDurationSec;
        int restDurationSec;
    }

    private static String[] testInput = new String[]{
            "Comet can fly 14 km/s for 10 seconds, but then must rest for 127 seconds.",
            "Dancer can fly 16 km/s for 11 seconds, but then must rest for 162 seconds.",
    };

    private static String[] input = new String[]{
"Rudolph can fly 22 km/s for 8 seconds, but then must rest for 165 seconds.",
"Cupid can fly 8 km/s for 17 seconds, but then must rest for 114 seconds.",
"Prancer can fly 18 km/s for 6 seconds, but then must rest for 103 seconds.",
"Donner can fly 25 km/s for 6 seconds, but then must rest for 145 seconds.",
"Dasher can fly 11 km/s for 12 seconds, but then must rest for 125 seconds.",
"Comet can fly 21 km/s for 6 seconds, but then must rest for 121 seconds.",
"Blitzen can fly 18 km/s for 3 seconds, but then must rest for 50 seconds.",
"Vixen can fly 20 km/s for 4 seconds, but then must rest for 75 seconds.",
"Dancer can fly 7 km/s for 20 seconds, but then must rest for 119 seconds.",
    };
}

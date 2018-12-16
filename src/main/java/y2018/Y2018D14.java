package y2018;

import com.google.common.base.Stopwatch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

public class Y2018D14 {
    public static void main(String[] args) throws Exception {

        // 1
        assertThat(getNextTenScoresAfterSteps(9)).isEqualTo("5158916779");
        assertThat(getNextTenScoresAfterSteps(5)).isEqualTo("0124515891");
        assertThat(getNextTenScoresAfterSteps(18)).isEqualTo("9251071085");
        assertThat(getNextTenScoresAfterSteps(2018)).isEqualTo("5941429882");

        System.out.println(getNextTenScoresAfterSteps(327901));

        // 2
        assertThat(countRecipesUntil("51589")).isEqualTo(9);
        assertThat(countRecipesUntil("01245")).isEqualTo(5);
        assertThat(countRecipesUntil("92510")).isEqualTo(18);
        assertThat(countRecipesUntil("59414")).isEqualTo(2018);

        Stopwatch sw = Stopwatch.createStarted();
        System.out.println(countRecipesUntil("327901"));
        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static int countRecipesUntil(String pattern) {
        int firstElfCurrentIdx = 0;
        int secondElfCurrentIdx = 1;
        char[] scoreboard = new char[30000000];
        scoreboard[0] = '3';
        scoreboard[1] = '7';
        int scoreboardLen = 2;
        long lastReportTimeMillis = 0;

        while (true) {
            Integer firstElfCurrentScore = scoreboard[firstElfCurrentIdx] - '0';
            Integer secondElfCurrentScore = scoreboard[secondElfCurrentIdx] - '0';
            String newScore = Integer.toString(firstElfCurrentScore + secondElfCurrentScore);
            if (newScore.contains("90")) {
                System.out.println("qq here?");
            }
            outer:
            for (int i = 0; i < newScore.length(); i++) {
                scoreboard[scoreboardLen++] = newScore.charAt(i);
                if (scoreboardLen >= pattern.length()) {
                    for (int j = 0; j < pattern.length(); j++) {
                        if (scoreboard[scoreboardLen - pattern.length() + j] != pattern.charAt(j)) {
                            continue outer;
                        }
                    }
                    return scoreboardLen - pattern.length();
                }
            }

            firstElfCurrentIdx = (firstElfCurrentIdx + 1 + firstElfCurrentScore) % scoreboardLen;
            secondElfCurrentIdx = (secondElfCurrentIdx + 1 + secondElfCurrentScore) % scoreboardLen;

            if (System.currentTimeMillis() - lastReportTimeMillis > 10000) {
                lastReportTimeMillis = System.currentTimeMillis();
                System.out.println("scoreboard len = " + scoreboardLen);
            }
        }
    }

    private static String getNextTenScoresAfterSteps(int requiredCount) {
        int firstElfCurrentIdx = 0;
        int secondElfCurrentIdx = 1;
        List<Integer> scoreboard = new ArrayList<>();
        scoreboard.add(3);
        scoreboard.add(7);

        while (true) {
            Integer firstElfCurrentScore = scoreboard.get(firstElfCurrentIdx);
            Integer secondElfCurrentScore = scoreboard.get(secondElfCurrentIdx);
            String newScore = Integer.toString(firstElfCurrentScore + secondElfCurrentScore);
            for (int i = 0; i < newScore.length(); i++) {
                scoreboard.add(newScore.charAt(i) - '0');
            }

            firstElfCurrentIdx = (firstElfCurrentIdx + 1 + firstElfCurrentScore) % scoreboard.size();
            secondElfCurrentIdx = (secondElfCurrentIdx + 1 + secondElfCurrentScore) % scoreboard.size();

            if (scoreboard.size() >= requiredCount + 10) {
                StringBuilder acc = new StringBuilder();
                for (int i = requiredCount; i < requiredCount + 10; i++) {
                    acc.append(scoreboard.get(i));
                }
                return acc.toString();
            }
        }
    }

}

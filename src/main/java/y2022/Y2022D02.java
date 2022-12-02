package y2022;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2022D02 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        List<String> input = Resources.readLines(Resources.getResource("y2022/Y2022D02.txt"), StandardCharsets.UTF_8);

        // 1
        assertThat(part1(example)).isEqualTo(15);
        System.out.println(part1(input));

        // 2
        assertThat(part2(example)).isEqualTo(12);
        System.out.println(part2(input));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static int part1(List<String> input) {
        int score = 0;
        Pattern pattern = Pattern.compile("([A-C]) ([X-Z])");
        for (String line : input) {
            Matcher m = pattern.matcher(line);
            checkState(m.matches());
            int oppenentMove = m.group(1).charAt(0) - 'A';
            int myMove = m.group(2).charAt(0) - 'X';
            score += (myMove + 1);
            int diff = (myMove - oppenentMove + 3) % 3;
            switch (diff) {
                case 0:
                    // draw
                    score += 3;
                    break;
                case 1:
                    // win
                    score += 6;
                    break;
                case 2:
                    // lose
                    break;
                default:
                    throw new RuntimeException();
            }
        }
        return score;
    }

    private static int part2(List<String> input) {
        int score = 0;
        Pattern pattern = Pattern.compile("([A-C]) ([X-Z])");
        for (String line : input) {
            Matcher m = pattern.matcher(line);
            checkState(m.matches());
            int oppenentMove = m.group(1).charAt(0) - 'A';
            int targetResult = m.group(2).charAt(0) - 'X';
            int myMove;
            switch (targetResult) {
                case 0: // lose
                    myMove = (oppenentMove + 2) % 3;
                    break;
                case 1: // draw
                    myMove = oppenentMove;
                    break;
                case 2: // win
                    myMove = (oppenentMove + 1) % 3;
                    break;
                default:
                    throw new RuntimeException();

            }

            score += (myMove + 1);
            int diff = (myMove - oppenentMove + 3) % 3;
            switch (diff) {
                case 0:
                    // draw
                    score += 3;
                    break;
                case 1:
                    // win
                    score += 6;
                    break;
                case 2:
                    // lose
                    break;
                default:
                    throw new RuntimeException();
            }
        }
        return score;
    }

    private static List<String> example = List.of(
            "A Y",
            "B X",
            "C Z");
}

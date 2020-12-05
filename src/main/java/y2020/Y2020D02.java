package y2020;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;

public class Y2020D02 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        List<String> input = Resources.readLines(Resources.getResource("y2020/Y2020D02.txt"), StandardCharsets.UTF_8);
        part1(input);
        part2(input);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }


    private static void part1(List<String> input) {
        int validLineCount = 0;
        Pattern pattern = Pattern.compile("([0-9]+)-([0-9]+) ([a-z]): ([a-z]+)");
        for (String line : input) {
            Matcher matcher = pattern.matcher(line);
            checkState(matcher.matches());
            int minCount = Integer.parseInt(matcher.group(1));
            int maxCount = Integer.parseInt(matcher.group(2));
            char requiredLetter = matcher.group(3).charAt(0);
            String candidatePassword = matcher.group(4);

            long observedCount = candidatePassword.chars().filter(c -> c == requiredLetter).count();
            if (observedCount >= minCount && observedCount <= maxCount) {
                validLineCount++;
            }
        }
        System.out.println("validLineCount = " + validLineCount);
    }


    private static void part2(List<String> input) {
        int validLineCount = 0;
        Pattern pattern = Pattern.compile("([0-9]+)-([0-9]+) ([a-z]): ([a-z]+)");
        for (String line : input) {
            Matcher matcher = pattern.matcher(line);
            checkState(matcher.matches());
            int index1 = Integer.parseInt(matcher.group(1));
            int index2 = Integer.parseInt(matcher.group(2));
            char requiredLetter = matcher.group(3).charAt(0);
            String candidatePassword = matcher.group(4);

            boolean index1Match = candidatePassword.charAt(index1 - 1) == requiredLetter;
            boolean index2Match = candidatePassword.charAt(index2 - 1) == requiredLetter;
            if (index1Match ^ index2Match) {
                validLineCount++;
            }
        }
        System.out.println("validLineCount 2 = " + validLineCount);
    }
}

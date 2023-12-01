package y2023;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;
import org.junit.Assert;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Y2023D01 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        List<String> input = Resources.readLines(Resources.getResource("y2023/Y2023D01.txt"), StandardCharsets.UTF_8);

        // 1
        System.out.println(part1(example));
        System.out.println(part1(input));

        // 2
        System.out.println(part2(example));
        System.out.println(part2(input));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static long part1(List<String> input) {
        long sum = 0;
        Pattern firstDig = Pattern.compile(".*?(\\d).*");
        Pattern lastDig = Pattern.compile(".*(\\d).*");
        for (String line : input) {
            Matcher m1 = firstDig.matcher(line);
            Assert.assertTrue(m1.matches());
            Matcher m2 = lastDig.matcher(line);
            Assert.assertTrue(m2.matches());
            sum += Long.parseLong(m1.group(1) + m2.group(1));
        }

        return sum;
    }

    private static long part2(List<String> input) {
        long sum = 0;
        Pattern firstDig = Pattern.compile(".*?(\\d|one|two|three|four|five|six|seven|eight|nine).*");
        Pattern lastDig = Pattern.compile(".*(\\d|one|two|three|four|five|six|seven|eight|nine).*");
        for (String line : input) {
            Matcher m1 = firstDig.matcher(line);
            Assert.assertTrue(m1.matches());
            Matcher m2 = lastDig.matcher(line);
            Assert.assertTrue(m2.matches());
            sum += Long.parseLong(numberWordToDigit(m1.group(1)) + numberWordToDigit(m2.group(1)));
        }

        return sum;
    }

    private static String numberWordToDigit(String x) {
        switch (x) {
            case "one": return "1";
            case "two": return "2";
            case "three": return "3";
            case "four": return "4";
            case "five": return "5";
            case "six": return "6";
            case "seven": return "7";
            case "eight": return "8";
            case "nine": return "9";
            default: return x;
        }
    }

    static List<String> example = List.of(
            "1abc2",
            "pqr3stu8vwx",
            "a1b2c3d4e5f",
            "treb7uchet"
    );
}

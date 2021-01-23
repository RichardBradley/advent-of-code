package y2017;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;

public class Y2017D08 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        part1(Resources.readLines(Resources.getResource("y2017/Y2017D08.txt"), StandardCharsets.UTF_8));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static void part1(List<String> lines) {
        int maxSeen = Integer.MIN_VALUE;
        Pattern p = Pattern.compile("(\\w+) (inc|dec) ([\\d-]+) if (\\w+) (<|<=|>|>=|==|!=) ([\\d-]+)");
        Map<String, Integer> registers = new HashMap<>();
        for (String line : lines) {
            Matcher m = p.matcher(line);
            checkState(m.matches());
            String targetReg = m.group(1);
            int delta = Integer.parseInt(m.group(3)) * ("dec".equals(m.group(2)) ? -1 : 1);
            String lhsReg = m.group(4);
            int lhsVal = registers.getOrDefault(lhsReg, 0);
            int rhsVal = Integer.parseInt(m.group(6));
            boolean testPassed;
            switch (m.group(5)) {
                case "<":
                    testPassed = lhsVal < rhsVal;
                    break;
                case "<=":
                    testPassed = lhsVal <= rhsVal;
                    break;
                case ">":
                    testPassed = lhsVal > rhsVal;
                    break;
                case ">=":
                    testPassed = lhsVal >= rhsVal;
                    break;
                case "==":
                    testPassed = lhsVal == rhsVal;
                    break;
                case "!=":
                    testPassed = lhsVal != rhsVal;
                    break;
                default:
                    throw new IllegalArgumentException(m.group(5));
            }
            if (testPassed) {
                int prevVal = registers.getOrDefault(targetReg, 0);
                int newVal = prevVal + delta;
                maxSeen = Math.max(maxSeen, newVal);
                registers.put(targetReg, newVal);
            }
        }

        System.out.println("max = " + registers.values().stream().mapToInt(x -> x).max().getAsInt());
        System.out.println("maxSeen = " + maxSeen);
    }
}


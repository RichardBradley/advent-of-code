package y2024;

import com.google.common.base.Stopwatch;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static aoc.Common.loadInputFromResources;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2024D13 {

    public static void main(String[] args) throws Exception {
        List<String> input = loadInputFromResources();
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(part1(example)).isEqualTo(480);
        assertThat(part1(input)).isEqualTo(38714);

        // 2
        assertThat(part1Exact(example)).isEqualTo(480);
        assertThat(part2(input)).isEqualTo(74015623345775L);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    @Value
    static class Machine {
        long aX, aY, bX, bY;
        long targetX, targetY;
    }

    private static long part1(List<String> input) {
        List<Machine> machines = parse(input);

        return machines.stream().mapToLong(m -> minTokens(m)).sum();
    }

    private static long part1Exact(List<String> input) {
        List<Machine> machines = parse(input);

        return machines.stream().mapToLong(m -> minTokensExact(m)).sum();
    }

    private static long part2(List<String> input) {
        List<Machine> machines = parse(input).stream()
                .map(m -> new Machine(
                        m.aX, m.aY, m.bX, m.bY,
                        10000000000000L + m.targetX,
                        10000000000000L + m.targetY))
                .collect(Collectors.toList());

        return machines.stream().mapToLong(m -> minTokensExact(m)).sum();
    }

    /**
     * A * aX + B * bX = tX
     * A * aY + B * bY = tY
     * A = (tY - B*bY)/aY
     * (tY - B*bY)/aY*aX + B*bX = tX
     * tY/aY*aX - B*bY/aY*aX + B*bX = tX
     * B * (bX - bY/aY*aX) = tX - tY/aY*aX
     * B = (tX - tY/aY*aX) / (bX - bY/aY*aX)
     */
    private static long minTokensExact(Machine m) {

        // promote all to doubles here, to keep the below expr cleaner
        double aX = m.aX;
        double aY = m.aY;
        double bX = m.bX;
        double bY = m.bY;
        double tX = m.targetX;
        double tY = m.targetY;

        double B = (tX - tY / aY * aX) / (bX - bY / aY * aX);
        double A = (tY - B * bY) / aY;

        long b = Math.round(B);
        long a = Math.round(A);

        if (Math.abs(A - a) > 0.001 || Math.abs(B - b) > 0.001) {
            return 0; // no exact solution
        }

        assertThat(a * m.aX + b * m.bX).isEqualTo(m.targetX);
        assertThat(a * m.aY + b * m.bY).isEqualTo(m.targetY);

        return 3 * a + b;
    }

    private static long minTokens(Machine m) {
        long minTokens = Long.MAX_VALUE;
        for (int aPress = 1; aPress <= 100; aPress++) {
            long xAOnly = m.aX * aPress;
            long xRem = m.targetX - xAOnly;
            if (xRem >= 0) {
                long bPress = xRem / m.bX;
                long x = bPress * m.bX + xAOnly;
                if (x == m.targetX) {
                    long y = aPress * m.aY + bPress * m.bY;
                    if (y == m.targetY) {
                        minTokens = Math.min(minTokens, 3 * aPress + bPress);
                    }
                }
            } else {
                break;
            }
        }

        return minTokens == Long.MAX_VALUE ? 0 : minTokens;
    }

    private static List<Machine> parse(List<String> input) {
        Pattern p1 = Pattern.compile("Button (A|B): X\\+(\\d+), Y\\+(\\d+)");
        Pattern p2 = Pattern.compile("Prize: X=(\\d+), Y=(\\d+)");
        int idx = 0;
        List<Machine> acc = new ArrayList<>();

        while (true) {
            Matcher m1a = p1.matcher(input.get(idx++));
            checkState(m1a.matches());
            checkState("A".equals(m1a.group(1)));
            long aX = Long.parseLong(m1a.group(2));
            long aY = Long.parseLong(m1a.group(3));

            Matcher m1b = p1.matcher(input.get(idx++));
            checkState(m1b.matches());
            checkState("B".equals(m1b.group(1)));
            long bX = Long.parseLong(m1b.group(2));
            long bY = Long.parseLong(m1b.group(3));

            Matcher m2 = p2.matcher(input.get(idx++));
            checkState(m2.matches());
            long targetX = Long.parseLong(m2.group(1));
            long targetY = Long.parseLong(m2.group(2));

            acc.add(new Machine(aX, aY, bX, bY, targetX, targetY));

            if (idx < input.size()) {
                checkState("".equals(input.get(idx++)));
            } else {
                break;
            }
        }

        return acc;
    }

    static List<String> example = List.of(
            "Button A: X+94, Y+34",
            "Button B: X+22, Y+67",
            "Prize: X=8400, Y=5400",
            "",
            "Button A: X+26, Y+66",
            "Button B: X+67, Y+21",
            "Prize: X=12748, Y=12176",
            "",
            "Button A: X+17, Y+86",
            "Button B: X+84, Y+37",
            "Prize: X=7870, Y=6450",
            "",
            "Button A: X+69, Y+23",
            "Button B: X+27, Y+71",
            "Prize: X=18641, Y=10279");
}

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

public class Y2022D10 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        List<String> input = Resources.readLines(Resources.getResource("y2022/Y2022D10.txt"), StandardCharsets.UTF_8);

        assertThat(part1(example)).isEqualTo(13140);
        System.out.println(part1(input));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    /**
     * Find the signal strength during the 20th, 60th, 100th, 140th, 180th,
     * and 220th cycles. What is the sum of these six signal strengths?
     */
    private static long part1(List<String> input) {
        Pattern p = Pattern.compile("(noop|addx (-?[0-9]+))");
        long signalStrengthSum = 0;
        int xRegister = 1;
        Integer awaitingAddXVal = null;
        int inputIdx = 0;

        StringBuilder screen = new StringBuilder();

        for (int cycle = 1; cycle <= 240; cycle++) {

            int screenXPos = (cycle - 1) % 40;
            if (Math.abs(screenXPos - xRegister) <= 1) {
                screen.append('#');
            } else {
                screen.append('.');
            }
            if (screenXPos == 39) {
                screen.append("\n");
            }

            if (((cycle - 20) % 40) == 0) {
                long signalStrength = cycle * xRegister;
                signalStrengthSum += signalStrength;
            }

            if (awaitingAddXVal != null) {
                xRegister += awaitingAddXVal;
                awaitingAddXVal = null;
            } else {
                Matcher m = p.matcher(input.get(inputIdx++));
                checkState(m.matches());
                if ("noop".equals(m.group(1))) {
                    // noop
                } else {
                    awaitingAddXVal = Integer.parseInt(m.group(2));
                }
            }
        }

        System.out.println(screen);
        System.out.println();
        return signalStrengthSum;
    }

    private static List<String> example = List.of(
            "addx 15",
            "addx -11",
            "addx 6",
            "addx -3",
            "addx 5",
            "addx -1",
            "addx -8",
            "addx 13",
            "addx 4",
            "noop",
            "addx -1",
            "addx 5",
            "addx -1",
            "addx 5",
            "addx -1",
            "addx 5",
            "addx -1",
            "addx 5",
            "addx -1",
            "addx -35",
            "addx 1",
            "addx 24",
            "addx -19",
            "addx 1",
            "addx 16",
            "addx -11",
            "noop",
            "noop",
            "addx 21",
            "addx -15",
            "noop",
            "noop",
            "addx -3",
            "addx 9",
            "addx 1",
            "addx -3",
            "addx 8",
            "addx 1",
            "addx 5",
            "noop",
            "noop",
            "noop",
            "noop",
            "noop",
            "addx -36",
            "noop",
            "addx 1",
            "addx 7",
            "noop",
            "noop",
            "noop",
            "addx 2",
            "addx 6",
            "noop",
            "noop",
            "noop",
            "noop",
            "noop",
            "addx 1",
            "noop",
            "noop",
            "addx 7",
            "addx 1",
            "noop",
            "addx -13",
            "addx 13",
            "addx 7",
            "noop",
            "addx 1",
            "addx -33",
            "noop",
            "noop",
            "noop",
            "addx 2",
            "noop",
            "noop",
            "noop",
            "addx 8",
            "noop",
            "addx -1",
            "addx 2",
            "addx 1",
            "noop",
            "addx 17",
            "addx -9",
            "addx 1",
            "addx 1",
            "addx -3",
            "addx 11",
            "noop",
            "noop",
            "addx 1",
            "noop",
            "addx 1",
            "noop",
            "noop",
            "addx -13",
            "addx -19",
            "addx 1",
            "addx 3",
            "addx 26",
            "addx -30",
            "addx 12",
            "addx -1",
            "addx 3",
            "addx 1",
            "noop",
            "noop",
            "noop",
            "addx -9",
            "addx 18",
            "addx 1",
            "addx 2",
            "noop",
            "noop",
            "addx 9",
            "noop",
            "noop",
            "noop",
            "addx -1",
            "addx 2",
            "addx -37",
            "addx 1",
            "addx 3",
            "noop",
            "addx 15",
            "addx -21",
            "addx 22",
            "addx -6",
            "addx 1",
            "noop",
            "addx 2",
            "addx 1",
            "noop",
            "addx -10",
            "noop",
            "noop",
            "addx 20",
            "addx 1",
            "addx 2",
            "addx 2",
            "addx -6",
            "addx -11",
            "noop",
            "noop",
            "noop");
}

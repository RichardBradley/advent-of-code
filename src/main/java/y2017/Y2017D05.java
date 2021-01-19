package y2017;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class Y2017D05 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        int[] instructions = Resources.readLines(Resources.getResource("y2017/Y2017D05.txt"), StandardCharsets.UTF_8).stream()
                .mapToInt(Integer::parseInt)
                .toArray();
        System.out.println(countSteps(instructions.clone()));
        System.out.println(countSteps2(instructions.clone()));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static int countSteps(int[] instructions) {
        int pc = 0;
        for (int stepCount = 0; ; stepCount++) {
            if (pc < 0 || pc >= instructions.length) {
                return stepCount;
            }
            pc += (instructions[pc]++);
        }
    }

    private static int countSteps2(int[] instructions) {
        int pc = 0;
        for (int stepCount = 0; ; stepCount++) {
            if (pc < 0 || pc >= instructions.length) {
                return stepCount;
            }
            int offset = instructions[pc];
            if (offset >= 3) {
                instructions[pc]--;
            } else {
                instructions[pc]++;
            }
            pc += offset;
        }
    }
}

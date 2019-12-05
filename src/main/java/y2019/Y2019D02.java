package y2019;

import com.google.common.base.Stopwatch;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

public class Y2019D02 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        System.out.println(eval(input, 12, 2));

        // 2
        System.out.println(findTargetOutput(input, 19690720));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static int findTargetOutput(int[] program, int target) {
        for (int noun = 0; ; noun++) {
            for (int verb = 0; verb < program.length; verb++) {
                System.out.printf("Eval noun = %s verb = %s", noun, verb);
                int out = eval(program, noun, verb);
                System.out.printf(" out = %s\n", out);
                if (out == target) {
                    return 100 * noun + verb;
                }
            }
        }
    }

    private static int eval(int[] program, int noun, int verb) {
        program = program.clone();
        program[1] = noun;
        program[2] = verb;
        int pc = 0;
        while (true) {
            switch (program[pc]) {
                case 99:
                    return program[0];
                case 1:
                    // add
                    program[program[pc + 3]] = program[program[pc + 1]] + program[program[pc + 2]];
                    break;
                case 2:
                    // mul
                    program[program[pc + 3]] = program[program[pc + 1]] * program[program[pc + 2]];
                    break;
                default:
                    throw new IllegalArgumentException();
            }
            pc += 4;
        }
    }

    static int[] input = new int[]{
            1, 0, 0, 3, 1, 1, 2, 3, 1, 3, 4, 3, 1, 5, 0, 3, 2, 6, 1, 19, 1, 5, 19, 23, 1, 13, 23, 27, 1, 6, 27, 31, 2, 31, 13, 35, 1, 9, 35, 39, 2, 39, 13, 43, 1, 43, 10, 47, 1, 47, 13, 51, 2, 13, 51, 55, 1, 55, 9, 59, 1, 59, 5, 63, 1, 6, 63, 67, 1, 13, 67, 71, 2, 71, 10, 75, 1, 6, 75, 79, 1, 79, 10, 83, 1, 5, 83, 87, 2, 10, 87, 91, 1, 6, 91, 95, 1, 9, 95, 99, 1, 99, 9, 103, 2, 103, 10, 107, 1, 5, 107, 111, 1, 9, 111, 115, 2, 13, 115, 119, 1, 119, 10, 123, 1, 123, 10, 127, 2, 127, 10, 131, 1, 5, 131, 135, 1, 10, 135, 139, 1, 139, 2, 143, 1, 6, 143, 0, 99, 2, 14, 0, 0
    };
}

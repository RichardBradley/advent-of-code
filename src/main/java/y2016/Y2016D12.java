package y2016;

import com.google.common.base.Stopwatch;
import lombok.Value;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.truth.Truth.assertThat;

public class Y2016D12 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(eval(parse(exampleInput), new int[4])[0]).isEqualTo(42);
        System.out.println("example ok");

        System.out.println(eval(parse(input), new int[4])[0]);

        // 2
        int[] registers = new int[4];
        registers[2] = 1;
        System.out.println(eval(parse(input), registers)[0]);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    @Value
    static class Instruction {
        OP op;
        int arg1;
        boolean arg1IsChar;
        int arg2;
        boolean arg2IsChar;
    }

    enum OP {
        CPY,
        INC,
        DEC,
        JNZ
    }

    static int[] eval(Instruction[] program, int[] registers) {
        int pc = 0;
        while (true) {
            if (pc < 0 || pc >= program.length) {
                return registers;
            }
            Instruction instruction = program[pc];
            switch (instruction.op) {
                case CPY:
                    int from = instruction.arg1IsChar
                            ? registers[instruction.arg1]
                            : instruction.arg1;
                    if (instruction.arg2IsChar) {
                        registers[instruction.arg2] = from;
                    } else {
                        throw new IllegalArgumentException();
                    }
                    break;
                case INC:
                    checkArgument(instruction.arg1IsChar);
                    registers[instruction.arg1]++;
                    break;
                case DEC:
                    checkArgument(instruction.arg1IsChar);
                    registers[instruction.arg1]--;
                    break;
                case JNZ:
                    int tested = instruction.arg1IsChar
                            ? registers[instruction.arg1]
                            : instruction.arg1;
                    checkArgument(!instruction.arg2IsChar);
                    if (tested != 0) {
                        pc += instruction.arg2;
                        continue;
                    }
                    break;
                default:
                    throw new IllegalArgumentException();
            }

            pc++;
        }
    }

    static Instruction[] parse(String[] input) {
        Instruction[] acc = new Instruction[input.length];
        Pattern pattern = Pattern.compile("(\\w+) (?:([a-d])|([0-9-]+)) ?(?:([a-d])|([0-9-]+))?");
        for (int i = 0; i < input.length; i++) {
            Matcher matcher = pattern.matcher(input[i]);
            checkArgument(matcher.matches());
            OP op = OP.valueOf(matcher.group(1).toUpperCase());

            int arg1;
            boolean arg1IsChar = matcher.group(2) != null;
            if (arg1IsChar) {
                arg1 = matcher.group(2).charAt(0) - 'a';
            } else {
                arg1 = Integer.parseInt(matcher.group(3));
            }
            boolean arg2IsChar = matcher.group(4) != null;
            int arg2;
            if (arg2IsChar) {
                arg2 = matcher.group(4).charAt(0) - 'a';
            } else {
                arg2 = matcher.group(5) != null
                        ? Integer.parseInt(matcher.group(5))
                        : 0;
            }

            acc[i] = new Instruction(op, arg1, arg1IsChar, arg2, arg2IsChar);
        }
        return acc;
    }

    static String[] exampleInput = new String[]{
            "cpy 41 a",
            "inc a",
            "inc a",
            "dec a",
            "jnz a 2",
            "dec a"
    };

    static String[] input = new String[]{
            "cpy 1 a",
            "cpy 1 b",
            "cpy 26 d",
            "jnz c 2",
            "jnz 1 5",
            "cpy 7 c",
            "inc d",
            "dec c",
            "jnz c -2",
            "cpy a c",
            "inc a",
            "dec b",
            "jnz b -2",
            "cpy c b",
            "dec d",
            "jnz d -6",
            "cpy 16 c",
            "cpy 17 d",
            "inc a",
            "dec d",
            "jnz d -2",
            "dec c",
            "jnz c -5",
    };
}

package y2016;

import com.google.common.base.Stopwatch;
import lombok.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.truth.Truth.assertThat;
import static java.util.stream.Collectors.toList;
import static y2016.Y2016D23.OP.*;

public class Y2016D23 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(eval(parse(exampleInput), new int[4])[0]).isEqualTo(3);
        System.out.println("example ok");

        int[] registers = new int[4];
        registers[0] = 7;
        System.out.println(eval(parse(input), registers)[0]);

        // 2
        registers = new int[4];
        registers[2] = 12;
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

        public Instruction toggle() {
            //For one-argument instructions, inc becomes dec, and all other one-argument instructions become inc.
            //
            //For two-argument instructions, jnz becomes cpy, and all other two-instructions become jnz.
            //
            //The arguments of a toggled instruction are not affected.
            switch (op) {
                case INC:
                    return new Instruction(DEC, arg1, arg1IsChar, arg2, arg2IsChar);
                case DEC:
                case TGL:
                    return new Instruction(INC, arg1, arg1IsChar, arg2, arg2IsChar);
                case JNZ:
                    return new Instruction(CPY, arg1, arg1IsChar, arg2, arg2IsChar);
                case CPY:
                    return new Instruction(JNZ, arg1, arg1IsChar, arg2, arg2IsChar);
                default:
                    throw new IllegalArgumentException(op.toString());
            }
        }
    }

    enum OP {
        CPY, // 2 arg
        INC, // 1 arg
        DEC, // 1 arg
        JNZ, // 2 arg
        TGL  // 1 arg
    }

    static int[] eval(Instruction[] program, int[] registers) {
        int opCount = 0;
        int pc = 0;
        while (true) {
            if (pc < 0 || pc >= program.length) {
                return registers;
            }
            if ((++opCount % 1000000) == 0) {
                System.out.printf("Op %s pc = %s registers = %s\n",
                        opCount, pc, Arrays.stream(registers).mapToObj(Integer::toString).collect(toList()));
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
                    if (tested != 0) {
                        int offset = instruction.arg2IsChar
                                ? registers[instruction.arg2]
                                : instruction.arg2;

                        pc += offset;
                        continue;
                    }
                    break;
                case TGL:

                    // tgl x toggles the instruction x away (pointing
                    // at instructions like jnz does: positive
                    // means forward; negative means backward):
                    //
                    //For one-argument instructions, inc becomes dec, and all other one-argument instructions become inc.
                    //
                    //For two-argument instructions, jnz becomes cpy, and all other two-instructions become jnz.
                    //
                    //The arguments of a toggled instruction are not affected.
                    //If an attempt is made to toggle an instruction outside the program, nothing happens.
                    //If toggling produces an invalid instruction (like cpy 1 2) and an attempt is later made to execute that instruction, skip it instead.
                    //If tgl toggles itself (for example, if a is 0, tgl a would target itself and become inc a), the resulting instruction is not executed until the next time it is reached.
                    int argVal = instruction.arg1IsChar
                            ? registers[instruction.arg1]
                            : instruction.arg1;
                    int targetedInstrIdx = pc + argVal;
                    if (targetedInstrIdx >= 0 && targetedInstrIdx < program.length) {
                        program[targetedInstrIdx] = program[targetedInstrIdx].toggle();
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
            "cpy 2 a",
            "tgl a",
            "tgl a",
            "tgl a",
            "cpy 1 a",
            "dec a",
            "dec a"
    };

    static String[] input = new String[]{
            "cpy a b",
            "dec b",
            "cpy a d",
            "cpy 0 a",
            "cpy b c",
            "inc a",
            "dec c",
            "jnz c -2",
            "dec d",
            "jnz d -5",
            "dec b",
            "cpy b c",
            "cpy c d",
            "dec d",
            "inc c",
            "jnz d -2",
            "tgl c",
            "cpy -16 c",
            "jnz 1 c",
            "cpy 72 c",
            "jnz 77 d",
            "inc a",
            "inc d",
            "jnz d -2",
            "inc c",
            "jnz c -5",
    };
}

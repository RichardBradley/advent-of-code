package y2016;

import com.google.common.base.Stopwatch;
import lombok.Value;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.truth.Truth.assertThat;
import static java.util.stream.Collectors.toList;

public class Y2016D25 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        assertThat(part1(input)).isEqualTo(-1);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static int part1(String[] input) {
        Instruction[] instructions = parse(input);
        for (int a = 0; ; a++) {
            int[] registers = new int[4];
            registers[0] = a;
            List<Integer> output = eval(instructions, registers);

            if (output.size() > 1) {
                boolean isValid = true;
                for (int i = 0; i < output.size(); i++) {
                    isValid &= (i % 2) == output.get(i);
                }
                if (isValid) {
                    return a;
                }
            }
        }
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
        CPY, // 2 arg
        INC, // 1 arg
        DEC, // 1 arg
        JNZ, // 2 arg
        OUT // 1 arg
    }

    static List<Integer> eval(Instruction[] program, int[] registers) {
        int opCount = 0;
        int pc = 0;
        Set<String> seenStates = new HashSet<>();
        List<Integer> output = new ArrayList<>();
        while (true) {
            String state = String.format("%s %s,%s,%s,%s", pc, registers[0], registers[1], registers[2], registers[3]);
            if (!seenStates.add(state)) {
                return output;
            }

            if (pc < 0 || pc >= program.length) {
                // terminated, but we're looking for a loop
                return Collections.emptyList();
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
                case OUT:
                    output.add(instruction.arg1IsChar
                            ? registers[instruction.arg1]
                            : instruction.arg1);
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

    static String[] input = new String[]{
            "cpy a d",
            "cpy 7 c",
            "cpy 362 b",
            "inc d",
            "dec b",
            "jnz b -2",
            "dec c",
            "jnz c -5",
            "cpy d a",
            "jnz 0 0",
            "cpy a b",
            "cpy 0 a",
            "cpy 2 c",
            "jnz b 2",
            "jnz 1 6",
            "dec b",
            "dec c",
            "jnz c -4",
            "inc a",
            "jnz 1 -7",
            "cpy 2 b",
            "jnz c 2",
            "jnz 1 4",
            "dec b",
            "dec c",
            "jnz 1 -4",
            "jnz 0 0",
            "out b",
            "jnz a -19",
            "jnz 1 -21",
    };
}

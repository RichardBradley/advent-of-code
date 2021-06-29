package y2017;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import lombok.Value;

import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2017D18 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {

            List<String> input = Resources.readLines(Resources.getResource("y2017/Y2017D18.txt"), StandardCharsets.UTF_8);


            assertThat(part1(example)).isEqualTo(4);
            assertThat(part1(input)).isEqualTo(1187);

            assertThat(part2(input)).isEqualTo(5969);
        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private static long part2(List<String> programStr) {
        Instruction[] program = programStr.stream().map(s -> parse(s)).collect(Collectors.toList()).toArray(new Instruction[0]);
        int[] sendCounts = new int[2];
        Queue<Long>[] inputQueues = new Queue[2];
        int[] pcs = new int[2];
        long[][] registers = new long[2][];
        for (int i = 0; i < 2; i++) {
            registers[i] = new long[26];
            registers[i]['p' - 'a'] = i;
            inputQueues[i] = new ArrayDeque<>();
        }

        while (true) {
            boolean progressMade = false;
            programIdLoop:
            for (int programId = 0; programId < 2; programId++) {

                pcLoop:
                while (true) {
                    Instruction instruction = program[pcs[programId]];
                    switch (instruction.op) {
                        case "snd": {
                            int otherProgramId = 1 - programId;
                            inputQueues[otherProgramId].add(registers[programId][((Character) instruction.arg1) - 'a']);
                            sendCounts[programId]++;
                            break;
                        }
                        case "set":
                            registers[programId][((Character) instruction.arg1) - 'a'] = instruction.arg2Val(registers[programId]);
                            break;
                        case "add":
                            registers[programId][((Character) instruction.arg1) - 'a'] += instruction.arg2Val(registers[programId]);
                            break;
                        case "mul":
                            registers[programId][((Character) instruction.arg1) - 'a'] *= instruction.arg2Val(registers[programId]);
                            break;
                        case "mod": {
                            int r = ((Character) instruction.arg1) - 'a';
                            registers[programId][r] = mod(registers[programId][r], instruction.arg2Val(registers[programId]));
                            break;
                        }
                        case "rcv": {
                            Long input = inputQueues[programId].poll();
                            if (input == null) {
                                continue programIdLoop; // next program; progressMade may be false
                            } else {
                                registers[programId][((Character) instruction.arg1) - 'a'] = input;
                            }
                            break;
                        }
                        case "jgz": {
                            long val = instruction.arg1Val(registers[programId]);
                            if (val > 0) {
                                pcs[programId] += instruction.arg2Val(registers[programId]);
                                continue pcLoop;
                            }
                            break;
                        }
                        default:
                            throw new IllegalArgumentException(instruction.op);
                    }

                    progressMade = true;
                    pcs[programId]++;
                }
            }

            if (!progressMade) {
                return sendCounts[1];
            }
        }
    }

    private static long part1(List<String> programStr) {
        Instruction[] program = programStr.stream().map(s -> parse(s)).collect(Collectors.toList()).toArray(new Instruction[0]);

        int pc = 0;
        long[] registers = new long[26];
        long lastFreqSent = -1;
        while (true) {
            Instruction instruction = program[pc];
            switch (instruction.op) {
                case "snd":
                    lastFreqSent = registers[((Character) instruction.arg1) - 'a'];
                    break;
                case "set":
                    registers[((Character) instruction.arg1) - 'a'] = instruction.arg2Val(registers);
                    break;
                case "add":
                    registers[((Character) instruction.arg1) - 'a'] += instruction.arg2Val(registers);
                    break;
                case "mul":
                    registers[((Character) instruction.arg1) - 'a'] *= instruction.arg2Val(registers);
                    break;
                case "mod": {
                    int r = ((Character) instruction.arg1) - 'a';
                    registers[r] = mod(registers[r], instruction.arg2Val(registers));
                    break;
                }
                case "rcv": {
                    long val = registers[((Character) instruction.arg1) - 'a'];
                    if (val != 0) {
                        return lastFreqSent;
                    }
                    break;
                }
                case "jgz": {
                    long val = registers[((Character) instruction.arg1) - 'a'];
                    if (val > 0) {
                        pc += instruction.arg2Val(registers);
                        continue;
                    }
                    break;
                }
                default:
                    throw new IllegalArgumentException(instruction.op);
            }

            pc++;
        }
    }

    private static long mod(long a, long m) {
        long ret = a % m;
        if (ret < 0) {
            ret += m;
            checkState(ret > 0);
        }
        return ret;
    }

    private static Pattern instrPat = Pattern.compile(
            "(\\w+) ((?<a1a>[a-z])|(?<a1n>[\\d-]+))( ((?<a2a>[a-z])|(?<a2n>[\\d-]+)))?");

    private static Instruction parse(String s) {
        Matcher matcher = instrPat.matcher(s);
        checkState(matcher.matches());
        String op = matcher.group(1);
        Object arg1;
        if (null != matcher.group("a1a")) {
            arg1 = matcher.group("a1a").charAt(0);
        } else {
            arg1 = Long.parseLong(matcher.group("a1n"));
        }
        Object arg2;
        if (null != matcher.group("a2a")) {
            arg2 = matcher.group("a2a").charAt(0);
        } else if (null != matcher.group("a2n")) {
            arg2 = Long.parseLong(matcher.group("a2n"));
        } else {
            arg2 = null;
        }
        return new Instruction(op, arg1, arg2);
    }

    @Value
    private static class Instruction {
        String op;
        Object arg1;
        Object arg2;

        long arg1Val(long[] registers) {
            checkNotNull(arg1);
            if (arg1 instanceof Long) {
                return (long) arg1;
            } else {
                char r = (Character) arg1;
                return registers[r - 'a'];
            }
        }

        long arg2Val(long[] registers) {
            checkNotNull(arg2);
            if (arg2 instanceof Long) {
                return (long) arg2;
            } else {
                char r = (Character) arg2;
                return registers[r - 'a'];
            }
        }
    }

    static List<String> example = ImmutableList.of(
            "set a 1",
            "add a 2",
            "mul a a",
            "mod a 5",
            "snd a",
            "set a 0",
            "rcv a",
            "jgz a -1",
            "set a 1",
            "jgz a -2"
    );
}


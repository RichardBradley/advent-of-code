package y2017;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;
import com.google.common.math.IntMath;
import lombok.Value;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2017D23 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {

            List<String> input = Resources.readLines(Resources.getResource("y2017/Y2017D23.txt"), StandardCharsets.UTF_8);

            assertThat(part1(input)).isEqualTo(3969);

            assertThat(part2()).isEqualTo(917);

        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    static long part2() {
        long b, c, f, g, h;

        b = 65;
        h = 0;

        b *= 100;
        b += 100000;
        c = b;
        c += 17000;

        do {
            f = IntMath.isPrime(Math.toIntExact(b)) ? 0 : 1;

            if (f != 0) {
                h++;
            }
            g = b;
            g -= c;
            if (g != 0) {
                b += 17;
            }
        } while (g != 0);

        return h;
    }

    private static long part1(List<String> programStr) {
        Instruction[] program = programStr.stream().map(s -> parse(s)).collect(Collectors.toList()).toArray(new Instruction[0]);

        int pc = 0;
        long[] registers = new long[26];
        long mulInstructionCount = 0;
        while (true) {
            if (pc < 0 || pc >= program.length) {
                break;
            }
            Instruction instruction = program[pc];
            switch (instruction.op) {
                case "set": {
                    long y = instruction.arg2Val(registers);
                    registers[((Character) instruction.arg1) - 'a'] = y;
                    break;
                }
                case "sub": {
                    long y = instruction.arg2Val(registers);
                    registers[((Character) instruction.arg1) - 'a'] -= y;
                    break;
                }
                case "mul": {
                    mulInstructionCount++;
                    long y = instruction.arg2Val(registers);
                    registers[((Character) instruction.arg1) - 'a'] *= y;
                    break;
                }
                case "jnz": {
                    long x = instruction.arg1Val(registers);
                    if (x != 0) {
                        long y = instruction.arg2Val(registers);
                        pc += y;
                        continue;
                    }
                    break;
                }
                default:
                    throw new IllegalArgumentException(instruction.op);
            }

            pc++;
        }

        return mulInstructionCount;
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
}


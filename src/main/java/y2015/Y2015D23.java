package y2015;

import com.google.common.base.Stopwatch;
import lombok.Value;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;
import static java.util.stream.Collectors.toList;

public class Y2015D23 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(run(parse(example), new int[2])).isEqualTo("[2, 0]");
        System.out.println("########");

        System.out.println(run(parse(input), new int[2]));

        // 2
        System.out.println("########");
        System.out.println(run(parse(input), new int[]{1, 0}));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");

    }

    static String run(List<Instr> programme, int[] registers) {
        int ip = 0;
        while (true) {
            if (ip >= programme.size()) {
                return Arrays.toString(registers);
            }
            Instr instr = programme.get(ip);

            System.out.println(ip + " " + Arrays.toString(registers) + " " + instr);

            switch (instr.op) {
                case hlf:
                    registers[instr.register] /= 2;
                    ip++;
                    continue;
                case tpl:
                    registers[instr.register] *= 3;
                    ip++;
                    continue;
                case inc:
                    registers[instr.register]++;
                    ip++;
                    continue;
                case jmp:
                    ip += instr.arg;
                    continue;
                case jie:
                    if (registers[instr.register] % 2 == 0) {
                        ip += instr.arg;
                    } else {
                        ip++;
                    }
                    continue;
                case jio:
                    if (registers[instr.register] == 1) {
                        ip += instr.arg;
                    } else {
                        ip++;
                    }
                    continue;
                default:
                    throw new IllegalStateException();
            }
        }
    }

    static enum Op {
        // hlf r sets register r to half its current value, then continues with the next instruction.
        hlf,
        //tpl r sets register r to triple its current value, then continues with the next instruction.
        tpl,
        //inc r increments register r, adding 1 to it, then continues with the next instruction.
        inc,
        //jmp offset is a jump; it continues with the instruction offset away relative to itself.
        jmp,
        //jie r, offset is like jmp, but only jumps if register r is even ("jump if even").
        jie,
        //jio r, offset is like jmp, but only jumps if register r is 1 ("jump if one", not odd).
        jio;
    }

    @Value
    static class Instr {
        Op op;
        int register;
        Integer arg;
    }

    static List<Instr> parse(String input) {
        Pattern pattern = Pattern.compile("(\\w{3}) ([ab])?,? ?([\\d+-]+)?");

        return Arrays.stream(input.split("\n"))
                .map(line -> {
                    Matcher matcher = pattern.matcher(line);
                    checkState(matcher.matches(), "for " + line);

                    int register = -1;
                    if (matcher.group(2) != null) {
                        register = ("a".equals(matcher.group(2))) ? 0 : 1;
                    }

                    Integer arg = matcher.group(3) == null ? null : Integer.parseInt(matcher.group(3));

                    return new Instr(
                            Op.valueOf(matcher.group(1)),
                            register,
                            arg);
                })
                .collect(toList());
    }

    static String example = "inc a\n" +
            "jio a, +2\n" +
            "tpl a\n" +
            "inc a";

    static String input = "jio a, +18\n" +
            "inc a\n" +
            "tpl a\n" +
            "inc a\n" +
            "tpl a\n" +
            "tpl a\n" +
            "tpl a\n" +
            "inc a\n" +
            "tpl a\n" +
            "inc a\n" +
            "tpl a\n" +
            "inc a\n" +
            "inc a\n" +
            "tpl a\n" +
            "tpl a\n" +
            "tpl a\n" +
            "inc a\n" +
            "jmp +22\n" + // 17
            "tpl a\n" +
            "inc a\n" +
            "tpl a\n" +
            "inc a\n" +
            "inc a\n" +
            "tpl a\n" +
            "inc a\n" +
            "tpl a\n" +
            "inc a\n" +
            "inc a\n" +
            "tpl a\n" +
            "tpl a\n" +
            "inc a\n" +
            "inc a\n" +
            "tpl a\n" +
            "inc a\n" +
            "inc a\n" +
            "tpl a\n" +
            "inc a\n" +
            "inc a\n" +
            "tpl a\n" +
            "jio a, +8\n" + // 39
            "inc b\n" +
            "jie a, +4\n" +
            "tpl a\n" +
            "inc a\n" +
            "jmp +2\n" +
            "hlf a\n" +
            "jmp -7";
}

package y2020;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;
import lombok.Value;
import lombok.experimental.Wither;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;

public class Y2020D08 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        List<Instr> code = parseCode(Resources.readLines(Resources.getResource("y2020/Y2020D08.txt"), StandardCharsets.UTF_8));

        // part1
        exec(code, false);
        System.out.println("######");
        part2(code);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static void part2(List<Instr> code) {
        for (int i = 0; i < code.size(); i++) {
            Instr origInstr = code.get(i);
            if (origInstr.op == Op.jmp) {
                code.set(i, origInstr.withOp(Op.nop));
                exec(code, false);
                code.set(i, origInstr);
            } else if (origInstr.op == Op.nop) {
                code.set(i, origInstr.withOp(Op.jmp));
                exec(code, false);
                code.set(i, origInstr);
            }
        }
    }

    enum Op {
        acc,
        jmp,
        nop;
    }

    @Value
    @Wither
    private static class Instr {
        Op op;
        long arg;
    }

    private static boolean exec(List<Instr> code, boolean log) {
        // Immediately before any instruction is executed a second time, what value is in the accumulator?
        Set<Integer> executedInstructions = new HashSet<>();
        long accumulator = 0;
        int pc = 0;
        while (true) {
            if (!executedInstructions.add(pc)) {
                // We are about to execute `pc` for the second time
                System.out.printf("Accumulator before %s runs a second time was %s\n",
                        pc, accumulator);
                return false;
            }
            if (pc == code.size()) {
                System.out.println("Terminated, accumulator = " + accumulator);
                return true;
            }
            Instr instr = code.get(pc);
            switch (instr.op) {
                case acc:
                    accumulator += instr.arg;
                    pc++;
                    break;
                case jmp:
                    pc += instr.arg;
                    break;
                case nop:
                    pc++;
                    break;
            }
            if (log) {
                System.out.printf(
                        "pc=%s %s %s    acc=%s\n",
                        pc,
                        instr.op,
                        instr.arg,
                        accumulator);
            }
        }
    }

    private static List<Instr> parseCode(List<String> lines) {
        Pattern p = Pattern.compile("(acc|jmp|nop) \\+?(-?[0-9]+)");
        return lines.stream().map(line -> {
                    Matcher m = p.matcher(line);
                    checkState(m.matches());
                    return new Instr(Op.valueOf(m.group(1)), Long.parseLong(m.group(2)));
                })
                .collect(Collectors.toList());
    }
}

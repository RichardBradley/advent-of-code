package y2018;

import com.google.common.base.Stopwatch;
import com.google.common.primitives.Ints;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static y2018.Y2018D16.eval;

public class Y2018D21 {
    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        // 1

//        input.printListing();

        System.out.println(run(input, new long[6]));

//        // 2
//        long[] registers2 = new long[6];
//        registers2[0] = 1;
//        System.out.println(run(input, registers2)[0]);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static long run(Programme programme, long[] registers) throws Exception {
//        FileWriter file = new FileWriter("log.txt");
        List<HistoryEntry> history = new ArrayList<>();
        long stepCount = 0;

        for (int ipc = 0; ipc < programme.instructions.size(); ipc++) {
            Y2018D16.OpcodeWithArgs instruction = programme.instructions.get(ipc);
            registers[programme.ipcIdx] = ipc;

            // Fast-forward the loop from 18 -> 25
            //   18, 19: c = 256 * (e + 1)
            //   20:  if (c > b) GOTO 23 else { e = e + 1; GOTO 18 }
            //
            if (ipc == 18) {
                long b = registers[1];
                long eAtEndOfLoop = b / 256;
                long loopsSkipped = Math.max(0, eAtEndOfLoop - registers[4]);
                if (loopsSkipped > 0) {
                    stepCount += (113794 - 113787) * loopsSkipped;
                    registers[4] = eAtEndOfLoop;
                }

                // 113787	ip=18	c = e + 1           	[0, 7969776, 12115, 8571075, 12114, 18]
            }

//            HistoryEntry newHistory = new HistoryEntry(ipc, registers);
//            skipFowardsIfPossible(history, newHistory, programme);
//            history.add(newHistory);
//            registers = registers.clone();

            eval(instruction, registers);

            String logLine = String.format(
                    "%s\tip=%s\t%-20s\t[%s, %s, %s, %s, %s, %s]",
                    ++stepCount,
                    ipc,
                    instruction.toHumanReadable(programme.ipcIdx),
                    registers[0], registers[1], registers[2], registers[3], registers[4], registers[5]);
            System.out.println(logLine);
//            file.write(logLine);
//            file.write("\n");
//            file.flush();

            ipc = Ints.checkedCast(registers[programme.ipcIdx]);
        }

        return stepCount;
    }

    @AllArgsConstructor
    static class HistoryEntry {
        long ipc;
        long[] registers;
    }

    @AllArgsConstructor
    static class Programme {
        int ipcIdx;
        List<Y2018D16.OpcodeWithArgs> instructions;

        public void printListing() {
            for (int i = 0; i < instructions.size(); i++) {
                Y2018D16.OpcodeWithArgs instruction = instructions.get(i);
                System.out.println(String.format(
                        "%s\t%s %7s %8s %3s\t\t%s",
                        i,
                        instruction.op, instruction.a, instruction.b, instruction.c,
                        instruction.toHumanReadable(ipcIdx)));
            }
        }
    }

    static Programme parse(String... specs) {
        checkArgument(specs[0].startsWith("#ip "));
        int ipIdx = Integer.parseInt(specs[0].substring("#ip ".length()));

        List<Y2018D16.OpcodeWithArgs> instructions = Arrays.stream(specs)
                .skip(1)
                .map(spec -> {
                    String[] xs = spec.split(" ");
                    checkArgument(xs.length == 4);
                    Y2018D16.Opcode opcode = Y2018D16.Opcode.valueOf(xs[0]);
                    int a = Integer.parseInt(xs[1]);
                    int b = Integer.parseInt(xs[2]);
                    int c = Integer.parseInt(xs[3]);
                    return new Y2018D16.OpcodeWithArgs(opcode, a, b, c);
                })
                .collect(Collectors.toList());

        return new Programme(ipIdx, instructions);
    }

    private static Programme input = parse(
            "#ip 5",
            "seti 123 0 3",
            "bani 3 456 3",
            "eqri 3 72 3",
            "addr 3 5 5",
            "seti 0 0 5",
            "seti 0 9 3",
            "bori 3 65536 1",
            "seti 9450265 6 3",
            "bani 1 255 4",
            "addr 3 4 3",
            "bani 3 16777215 3",
            "muli 3 65899 3",
            "bani 3 16777215 3",
            "gtir 256 1 4",
            "addr 4 5 5",
            "addi 5 1 5",
            "seti 27 1 5",
            "seti 0 9 4",
            "addi 4 1 2",
            "muli 2 256 2",
            "gtrr 2 1 2",
            "addr 2 5 5",
            "addi 5 1 5",
            "seti 25 7 5",
            "addi 4 1 4",
            "seti 17 5 5",
            "setr 4 6 1",
            "seti 7 8 5",
            "eqrr 3 0 4",
            "addr 4 5 5",
            "seti 5 8 5");
}
package y2018;

import com.google.common.base.Stopwatch;
import com.google.common.primitives.Ints;
import lombok.AllArgsConstructor;
import scala.Int;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;
import static y2018.Y2018D16.eval;

public class Y2018D19 {
    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        // 1
//        assertThat(run(testInput, new int[6])[0]).isEqualTo(6);
//
//        System.out.println(run(input, new int[6])[0]);

        // 2
        long[] registers2 = new long[6];
        registers2[0] = 1;
        System.out.println(run(input, registers2)[0]);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static long[] run(Programme programme, long[] registers) throws Exception {
        FileWriter file = new FileWriter("log.txt");
        List<HistoryEntry> history = new ArrayList<>();
        int stepCount = 0;

        for (int ipc = 0; ipc < programme.instructions.size(); ipc++) {
            Y2018D16.OpcodeWithArgs instruction = programme.instructions.get(ipc);
            registers[programme.ipIdx] = ipc;

            // skipFowardsIfPossible:
            if (ipc == 9) {

            }


//            HistoryEntry newHistory = new HistoryEntry(ipc, registers);
//            skipFowardsIfPossible(history, newHistory, programme);
//            history.add(newHistory);
//            registers = registers.clone();

            eval(instruction, registers);

            String logLine = String.format(
                    "%s ip=%s %s %s %s %s [%s, %s, %s, %s, %s, %s]",
                    ++stepCount,
                    ipc, instruction.op, instruction.a, instruction.b, instruction.c,
                    registers[0], registers[1], registers[2], registers[3], registers[4], registers[5]);
            System.out.println(logLine);
            file.write(logLine);
            file.write("\n");
            file.flush();

            ipc = Ints.checkedCast(registers[programme.ipIdx]);
        }

        return registers;
    }

    static boolean hasSkipped = false;

    private static void skipFowardsIfPossible(List<HistoryEntry> history, HistoryEntry newHistory, Programme programme) {
        for (int oldStep = history.size() - 1; oldStep >= 0; oldStep--) {
            HistoryEntry historyEntry = history.get(oldStep);
            if (historyEntry.ipc == newHistory.ipc) {
                int loopLen = history.size() - oldStep;
                System.out.println("Found loop around ipc=" + newHistory.ipc + " of length " + loopLen);

                int[] deltas = new int[6];
                for (int i = 0; i < 6; i++) {
                    deltas[i] = newHistory.registers[i] - historyEntry.registers[i];
                }

                System.out.println("Register changes: " + Arrays.toString(deltas));

                if (hasSkipped) return;

//                /// qq DELTA at each stage in the loop...
//
                int maxSkipsBeforeBranchChange = Integer.MAX_VALUE;
                for (int stepInLoop = oldStep; stepInLoop < history.size(); stepInLoop++) {
                    HistoryEntry entryInLoop = history.get(stepInLoop);
                    Y2018D16.OpcodeWithArgs instr = programme.instructions.get(entryInLoop.ipc);
                    maxSkipsBeforeBranchChange = Math.min(
                            maxSkipsBeforeBranchChange,
                            getMaxSkipsBeforeBranchChange(instr, entryInLoop.registers, deltas));
                }

//                int suggested = newHistory.registers[4] - newHistory.registers[1];
//                maxSkipsBeforeBranchChange = 0; // newHistory.registers[4] - newHistory.registers[1];

                if (maxSkipsBeforeBranchChange > 0) {
                    System.out.println("Skipping " + (maxSkipsBeforeBranchChange * loopLen) + " steps using observed loop");
                    for (int i = 0; i < 6; i++) {
                        newHistory.registers[i] += maxSkipsBeforeBranchChange * deltas[i];
                    }
                    history.clear();
                    hasSkipped = true;
                    return;
                }
            }
        }
    }

    private static int getMaxSkipsBeforeBranchChange(Y2018D16.OpcodeWithArgs instr, int[] registers, int[] deltas) {
        switch (instr.op) {
            case addr: // (add register) stores into register C the result of adding register A and register B.
            case addi: // (add immediate) stores into register C the result of adding register A and value B.
            case mulr: // (multiply register) stores into register C the result of multiplying register A and register B.
            case muli: // (multiply immediate) stores into register C the result of multiplying register A and value B.
            case banr: // (bitwise AND register) stores into register C the result of the bitwise AND of register A and register B.
            case bani: // (bitwise AND immediate) stores into register C the result of the bitwise AND of register A and value B.
            case borr: // (bitwise OR register) stores into register C the result of the bitwise OR of register A and register B.
            case bori: // (bitwise OR immediate) stores into register C the result of the bitwise OR of register A and value B.
            case setr: // (set register) copies the contents of register A into register C. (Input B is ignored.)
            case seti: // (set immediate) stores value A into register C. (Input B is ignored.)
                return Integer.MAX_VALUE; // not conditional
            case eqrr:
                // rC = (rA == rB ? 1 : 0)
                // qq
//                checkState(deltas[instr.a] == 0);
//                checkState(deltas[instr.b] == 0);
                return Integer.MAX_VALUE;
            case gtrr:
                // rC = (rA > rB ? 1 : 0)
                checkState(registers[instr.a] < registers[instr.b]);
                int diff = registers[instr.b] - registers[instr.a];
                checkState(deltas[instr.b] == 0);
                int skipsBeforeBranchChange = (diff / deltas[instr.a]) - 1;
                checkState(skipsBeforeBranchChange > 0);
                return skipsBeforeBranchChange;


            default:
                throw new IllegalArgumentException(instr.op.toString());
        }
    }

    @AllArgsConstructor
    static class HistoryEntry {
        int ipc;
        int[] registers;
    }

    @AllArgsConstructor
    static class Programme {
        int ipIdx;
        List<Y2018D16.OpcodeWithArgs> instructions;
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

    static Programme testInput = parse(
            "#ip 0",
            "seti 5 0 1",
            "seti 6 0 2",
            "addi 0 1 0",
            "addr 1 2 3",
            "setr 1 0 0",
            "seti 8 0 4",
            "seti 9 0 5");

    private static Programme input = parse(
            "#ip 2",
            "addi 2 16 2",
            "seti 1 4 3",
            "seti 1 5 1",
            "mulr 3 1 5",
            "eqrr 5 4 5",
            "addr 5 2 2",
            "addi 2 1 2",
            "addr 3 0 0",
            "addi 1 1 1",
            "gtrr 1 4 5",
            "addr 2 5 2",
            "seti 2 9 2",
            "addi 3 1 3",
            "gtrr 3 4 5",
            "addr 5 2 2",
            "seti 1 6 2",
            "mulr 2 2 2",
            "addi 4 2 4",
            "mulr 4 4 4",
            "mulr 2 4 4",
            "muli 4 11 4",
            "addi 5 7 5",
            "mulr 5 2 5",
            "addi 5 4 5",
            "addr 4 5 4",
            "addr 2 0 2",
            "seti 0 1 2",
            "setr 2 1 5",
            "mulr 5 2 5",
            "addr 2 5 5",
            "mulr 2 5 5",
            "muli 5 14 5",
            "mulr 5 2 5",
            "addr 4 5 4",
            "seti 0 6 0",
            "seti 0 6 2");
}
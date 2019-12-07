package y2019;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import lombok.Value;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;
import static y2019.Y2019D07.OP.*;

public class Y2019D07 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(findMaxOutput(example1)).isEqualTo(43210);
        System.out.println("example OK");
        System.out.println(findMaxOutput(input));

        // 2
        assertThat(findMaxOutputWithFeedback(example21)).isEqualTo(139629729);
        System.out.println("example OK");
        System.out.println(findMaxOutputWithFeedback(input));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static int findMaxOutputWithFeedback(int[] program) {
        int maxOutput = Integer.MIN_VALUE;
        settingLoop:
        for (List<Integer> ampSettings : Collections2.permutations(ImmutableList.of(5, 6, 7, 8, 9))) {
            // System.out.println("ampSettings = " + ampSettings);
            ProgramState[] amps = new ProgramState[5];
            for (int i = 0; i < amps.length; i++) {
                amps[i] = new ProgramState(program);
            }

            Queue<Integer>[] inputBuffs = (Queue<Integer>[]) new Queue<?>[amps.length];
            for (int i = 0; i < amps.length; i++) {
                inputBuffs[i] = new LinkedList<>();
                inputBuffs[i].add(ampSettings.get(i));
            }
            inputBuffs[0].add(0);

            // pump the amps in reverse order, until we deadlock or finish
            boolean progressMade;
            int lastFinalOutput = Integer.MIN_VALUE;
            do {
                progressMade = false;
                for (int currAmpIdx = amps.length - 1; currAmpIdx >= 0; currAmpIdx--) {
                    EvalResult next = evalPartial(amps[currAmpIdx], inputBuffs[currAmpIdx]);
                    if (next instanceof Terminated) {
                        if (currAmpIdx == 4) {
                            if (maxOutput < lastFinalOutput) {
                                maxOutput = lastFinalOutput;
                            }
                            continue settingLoop;
                        }
                    } else if (next instanceof Ouput) {
                        progressMade = true;
                        int outputVal = ((Ouput) next).outputVal;
                        if (currAmpIdx == 4) {
                            inputBuffs[0].add(outputVal);
                            lastFinalOutput = outputVal;
                        } else {
                            inputBuffs[currAmpIdx + 1].add(outputVal);
                        }
                    } else if (next instanceof BlockedOnInput) {
                        // continue;
                    } else {
                        throw new IllegalStateException();
                    }
                }
            } while (progressMade);
            System.out.println("Deadlock for " + ampSettings);
        }

        return maxOutput;
    }

    interface EvalResult {
    }

    static class BlockedOnInput implements EvalResult {
    }

    @Value
    static class Ouput implements EvalResult {
        int outputVal;
    }

    static class Terminated implements EvalResult {
    }

    private static int findMaxOutput(int[] program) {
        int maxOutput = Integer.MIN_VALUE;
        for (List<Integer> ampSettings : Collections2.permutations(ImmutableList.of(0, 1, 2, 3, 4))) {
            // System.out.println("ampSettings = " + ampSettings);
            int chainInput = 0;
            for (int ampSetting : ampSettings) {
                chainInput = eval(program, new LinkedList<>(ImmutableList.of(ampSetting, chainInput)));
            }
            // System.out.println("output = " + chainInput);
            if (maxOutput < chainInput) {
                maxOutput = chainInput;
            }
        }

        return maxOutput;
    }

    private static int eval(int[] program, Queue<Integer> inputs) {
        ProgramState state = new ProgramState(program);
        EvalResult evalResult = evalPartial(state, inputs);
        checkState(evalResult instanceof Ouput);
        int output = ((Ouput) evalResult).outputVal;
        evalResult = evalPartial(state, inputs);
        checkState(evalResult instanceof Terminated);
        return output;
    }

    private static EvalResult evalPartial(ProgramState state, Queue<Integer> inputs) {
        int[] program = state.program;
        outer:
        while (true) {
            Instruction i = parseInstr(program, state.pc);
            // System.out.printf("pc = %s, i = %s\n", pc, i);

            switch (i.opcode) {
                case END:
                    return new Terminated();
                case ADD: {
                    checkState(i.size == 4);
                    int arg1 = i.param1IsImmediate
                            ? i.param1
                            : program[i.param1];
                    int arg2 = i.param2IsImmediate
                            ? i.param2
                            : program[i.param2];
                    int result = arg1 + arg2;
                    checkState(!i.param3IsImmediate);
                    program[i.param3] = result;
                    break;
                }
                case MUL: {
                    checkState(i.size == 4);
                    int arg1 = i.param1IsImmediate
                            ? i.param1
                            : program[i.param1];
                    int arg2 = i.param2IsImmediate
                            ? i.param2
                            : program[i.param2];
                    int result = arg1 * arg2;
                    checkState(!i.param3IsImmediate);
                    program[i.param3] = result;
                    break;
                }
                case JIT: {
                    checkState(i.size == 3);
                    int arg1 = i.param1IsImmediate
                            ? i.param1
                            : program[i.param1];
                    int arg2 = i.param2IsImmediate
                            ? i.param2
                            : program[i.param2];
                    if (arg1 != 0) {
                        state.pc = arg2;
                        continue outer;
                    }
                    break;
                }
                case JIF: {
                    checkState(i.size == 3);
                    int arg1 = i.param1IsImmediate
                            ? i.param1
                            : program[i.param1];
                    int arg2 = i.param2IsImmediate
                            ? i.param2
                            : program[i.param2];
                    if (arg1 == 0) {
                        state.pc = arg2;
                        continue outer;
                    }
                    break;
                }
                case LT: {
                    checkState(i.size == 4);
                    int arg1 = i.param1IsImmediate
                            ? i.param1
                            : program[i.param1];
                    int arg2 = i.param2IsImmediate
                            ? i.param2
                            : program[i.param2];
                    program[i.param3] = (arg1 < arg2)
                            ? 1
                            : 0;
                    break;
                }
                case EQ: {
                    checkState(i.size == 4);
                    int arg1 = i.param1IsImmediate
                            ? i.param1
                            : program[i.param1];
                    int arg2 = i.param2IsImmediate
                            ? i.param2
                            : program[i.param2];
                    program[i.param3] = (arg1 == arg2)
                            ? 1
                            : 0;
                    break;
                }
                case INPUT:
                    checkState(i.size == 2);
                    checkState(!i.param1IsImmediate);
                    if (inputs.isEmpty()) {
                        // block for input:
                        return new BlockedOnInput();
                    } else {
                        program[i.param1] = inputs.poll();
                    }
                    break;
                case OUTPUT:
                    checkState(i.size == 2);
                    // block for output:
                    int output = i.param1IsImmediate
                            ? i.param1
                            : program[i.param1];
                    state.pc += i.size;
                    return new Ouput(output);
                default:
                    throw new IllegalArgumentException();
            }
            state.pc += i.size;
        }
    }

    private static Instruction parseInstr(int[] program, int pc) {
        // ABCDE as per docs
        int opcodeAndArgsSpec = program[pc];
        int DE = opcodeAndArgsSpec % 100;
        int C = (opcodeAndArgsSpec / 100) % 10;
        checkArgument(C == 0 || C == 1);
        int B = (opcodeAndArgsSpec / 1000) % 10;
        checkArgument(B == 0 || B == 1);
        int A = (opcodeAndArgsSpec / 10000) % 10;
        checkArgument(A == 0 || A == 1);

        OP opcode = OP.fromInt(DE);

        switch (opcode) {
            case ADD:
            case MUL:
            case LT:
            case EQ:
                return new Instruction(
                        4,
                        opcode,
                        program[pc + 1],
                        C == 1,
                        program[pc + 2],
                        B == 1,
                        program[pc + 3],
                        A == 1);
            case JIT:
            case JIF:
                checkArgument(A == 0);
                return new Instruction(
                        3,
                        opcode,
                        program[pc + 1],
                        C == 1,
                        program[pc + 2],
                        B == 1,
                        0,
                        false);

            case INPUT:
            case OUTPUT:
                checkArgument(B == 0);
                checkArgument(A == 0);
                return new Instruction(
                        2,
                        DE == 3 ? INPUT : OUTPUT,
                        program[pc + 1],
                        C == 1,
                        0,
                        false,
                        0,
                        false);
            case END:
                checkArgument(C == 0);
                checkArgument(B == 0);
                checkArgument(A == 0);
                return new Instruction(
                        1,
                        END,
                        0,
                        false,
                        0,
                        false,
                        0,
                        false);
            default:
                throw new IllegalArgumentException();
        }
    }

    enum OP {
        ADD, // 1
        MUL, // 2
        INPUT, // 3
        OUTPUT, // 4
        JIT, // 5
        JIF, // 6
        LT, // 7
        EQ, // 8
        END; // 99

        public static OP fromInt(int i) {
            switch (i) {
                case 1:
                    return ADD;
                case 2:
                    return MUL;
                case 3:
                    return INPUT;
                case 4:
                    return OUTPUT;
                case 5:
                    return JIT;
                case 6:
                    return JIF;
                case 7:
                    return LT;
                case 8:
                    return EQ;
                case 99:
                    return END;
                default:
                    throw new IllegalArgumentException("val: " + i);
            }
        }
    }

    @Value
    static class Instruction {
        int size;
        OP opcode;
        int param1;
        boolean param1IsImmediate;
        int param2;
        boolean param2IsImmediate;
        int param3;
        boolean param3IsImmediate;
    }

    static class ProgramState {
        int[] program;
        int pc = 0;

        public ProgramState(int[] program) {
            this.program = program.clone();
        }
    }

    static int[] example1 = new int[]{
            3, 15, 3, 16, 1002, 16, 10, 16, 1, 16, 15, 15, 4, 15, 99, 0, 0
    };

    static int[] example21 = new int[]{
            3, 26, 1001, 26, -4, 26, 3, 27, 1002, 27, 2, 27, 1, 27, 26,
            27, 4, 27, 1001, 28, -1, 28, 1005, 28, 6, 99, 0, 0, 5
    };

    static int[] example22 = new int[]{
            3, 52, 1001, 52, -5, 52, 3, 53, 1, 52, 56, 54, 1007, 54, 5, 55, 1005, 55, 26, 1001, 54,
            -5, 54, 1105, 1, 12, 1, 53, 54, 53, 1008, 54, 0, 55, 1001, 55, 1, 55, 2, 53, 55, 53, 4,
            53, 1001, 56, -1, 56, 1005, 56, 6, 99, 0, 0, 0, 0, 10
    };

    static int[] input = new int[]{
            3, 8, 1001, 8, 10, 8, 105, 1, 0, 0, 21, 42, 67, 84, 109, 122, 203, 284, 365, 446, 99999, 3, 9, 1002, 9, 3, 9, 1001, 9, 5, 9, 102, 4, 9, 9, 1001, 9, 3, 9, 4, 9, 99, 3, 9, 1001, 9, 5, 9, 1002, 9, 3, 9, 1001, 9, 4, 9, 102, 3, 9, 9, 101, 3, 9, 9, 4, 9, 99, 3, 9, 101, 5, 9, 9, 1002, 9, 3, 9, 101, 5, 9, 9, 4, 9, 99, 3, 9, 102, 5, 9, 9, 101, 5, 9, 9, 102, 3, 9, 9, 101, 3, 9, 9, 102, 2, 9, 9, 4, 9, 99, 3, 9, 101, 2, 9, 9, 1002, 9, 3, 9, 4, 9, 99, 3, 9, 101, 2, 9, 9, 4, 9, 3, 9, 101, 1, 9, 9, 4, 9, 3, 9, 101, 1, 9, 9, 4, 9, 3, 9, 1001, 9, 1, 9, 4, 9, 3, 9, 101, 1, 9, 9, 4, 9, 3, 9, 1002, 9, 2, 9, 4, 9, 3, 9, 1002, 9, 2, 9, 4, 9, 3, 9, 1001, 9, 2, 9, 4, 9, 3, 9, 101, 1, 9, 9, 4, 9, 3, 9, 1002, 9, 2, 9, 4, 9, 99, 3, 9, 1001, 9, 1, 9, 4, 9, 3, 9, 101, 2, 9, 9, 4, 9, 3, 9, 102, 2, 9, 9, 4, 9, 3, 9, 101, 1, 9, 9, 4, 9, 3, 9, 102, 2, 9, 9, 4, 9, 3, 9, 1001, 9, 1, 9, 4, 9, 3, 9, 101, 1, 9, 9, 4, 9, 3, 9, 1002, 9, 2, 9, 4, 9, 3, 9, 101, 2, 9, 9, 4, 9, 3, 9, 1002, 9, 2, 9, 4, 9, 99, 3, 9, 101, 2, 9, 9, 4, 9, 3, 9, 101, 2, 9, 9, 4, 9, 3, 9, 101, 2, 9, 9, 4, 9, 3, 9, 101, 1, 9, 9, 4, 9, 3, 9, 101, 1, 9, 9, 4, 9, 3, 9, 102, 2, 9, 9, 4, 9, 3, 9, 1002, 9, 2, 9, 4, 9, 3, 9, 1002, 9, 2, 9, 4, 9, 3, 9, 101, 2, 9, 9, 4, 9, 3, 9, 1001, 9, 1, 9, 4, 9, 99, 3, 9, 1001, 9, 1, 9, 4, 9, 3, 9, 101, 1, 9, 9, 4, 9, 3, 9, 102, 2, 9, 9, 4, 9, 3, 9, 1002, 9, 2, 9, 4, 9, 3, 9, 1001, 9, 2, 9, 4, 9, 3, 9, 1001, 9, 1, 9, 4, 9, 3, 9, 1001, 9, 2, 9, 4, 9, 3, 9, 1002, 9, 2, 9, 4, 9, 3, 9, 1002, 9, 2, 9, 4, 9, 3, 9, 102, 2, 9, 9, 4, 9, 99, 3, 9, 102, 2, 9, 9, 4, 9, 3, 9, 1002, 9, 2, 9, 4, 9, 3, 9, 101, 2, 9, 9, 4, 9, 3, 9, 101, 2, 9, 9, 4, 9, 3, 9, 101, 1, 9, 9, 4, 9, 3, 9, 1002, 9, 2, 9, 4, 9, 3, 9, 101, 1, 9, 9, 4, 9, 3, 9, 1001, 9, 2, 9, 4, 9, 3, 9, 102, 2, 9, 9, 4, 9, 3, 9, 101, 1, 9, 9, 4, 9, 99
    };
}

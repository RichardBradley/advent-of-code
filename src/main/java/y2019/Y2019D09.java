package y2019;

import com.google.common.base.Stopwatch;
import lombok.Value;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

public class Y2019D09 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        // 1
//       System.out.println("example 1");
//        evalMultiOutput(example1, 1);
//        System.out.println("example 2");
//        evalMultiOutput(example2, 1);
//        System.out.println("example 3");
//        evalMultiOutput(example3, 1);
        System.out.println("part 1");
        evalMultiOutput(input, 1);

        // 2
        System.out.println("part 2");
        evalMultiOutput(input, 2);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    interface EvalResult {
    }

    static class BlockedOnInput implements EvalResult {
    }

    @Value
    static class Ouput implements EvalResult {
        BigInteger outputVal;
    }

    static class Terminated implements EvalResult {
    }

    private static void evalMultiOutput(BigInteger[] program, int input) {
        ProgramState state = new ProgramState(program);
        EvalResult evalResult;
        Queue<BigInteger> inputs = new LinkedList<>();
        inputs.add(BigInteger.valueOf(input));

        while (true) {
            evalResult = evalPartial(state, inputs);
            if (evalResult instanceof Ouput) {
                System.out.println(((Ouput) evalResult).outputVal);
            } else {
                checkState(evalResult instanceof Terminated);
                return;
            }
        }
    }

    private static EvalResult evalPartial(ProgramState state, Queue<BigInteger> inputs) {
        BigInteger[] program = state.program;
        outer:
        while (true) {
            Instruction i = parseInstr(program, state.pc);
            // System.out.printf("pc = %s, i = %s\n", pc, i);

            switch (i.opcode) {
                case END:
                    return new Terminated();
                case ADD: {
                    checkState(i.size == 4);
                    BigInteger arg1 = state.evalParam(i.param1, i.param1mode);
                    BigInteger arg2 = state.evalParam(i.param2, i.param2mode);
                    BigInteger result = arg1.add(arg2);
                    state.writeToParam(i.param3, i.param3mode, result);
                    break;
                }
                case MUL: {
                    checkState(i.size == 4);
                    BigInteger arg1 = state.evalParam(i.param1, i.param1mode);
                    BigInteger arg2 = state.evalParam(i.param2, i.param2mode);
                    BigInteger result = arg1.multiply(arg2);
                    state.writeToParam(i.param3, i.param3mode, result);
                    break;
                }
                case JIT: {
                    checkState(i.size == 3);
                    BigInteger arg1 = state.evalParam(i.param1, i.param1mode);
                    BigInteger arg2 = state.evalParam(i.param2, i.param2mode);
                    if (!arg1.equals(BigInteger.ZERO)) {
                        state.pc = arg2.intValueExact();
                        continue outer;
                    }
                    break;
                }
                case JIF: {
                    checkState(i.size == 3);
                    BigInteger arg1 = state.evalParam(i.param1, i.param1mode);
                    BigInteger arg2 = state.evalParam(i.param2, i.param2mode);
                    if (arg1.equals(BigInteger.ZERO)) {
                        state.pc = arg2.intValueExact();
                        continue outer;
                    }
                    break;
                }
                case LT: {
                    checkState(i.size == 4);
                    BigInteger arg1 = state.evalParam(i.param1, i.param1mode);
                    BigInteger arg2 = state.evalParam(i.param2, i.param2mode);
                    BigInteger result = (arg1.compareTo(arg2) < 0)
                            ? BigInteger.ONE
                            : BigInteger.ZERO;
                    state.writeToParam(i.param3, i.param3mode, result);
                    break;
                }
                case EQ: {
                    checkState(i.size == 4);
                    BigInteger arg1 = state.evalParam(i.param1, i.param1mode);
                    BigInteger arg2 = state.evalParam(i.param2, i.param2mode);
                    BigInteger result = (arg1.compareTo(arg2) == 0)
                            ? BigInteger.ONE
                            : BigInteger.ZERO;
                    state.writeToParam(i.param3, i.param3mode, result);
                    break;
                }
                case INPUT:
                    checkState(i.size == 2);
                    if (inputs.isEmpty()) {
                        // block for input:
                        return new BlockedOnInput();
                    } else {
                        state.writeToParam(i.param1, i.param1mode, inputs.poll());
                    }
                    break;
                case OUTPUT:
                    checkState(i.size == 2);
                    // block for output:
                    BigInteger output = state.evalParam(i.param1, i.param1mode);
                    state.pc += i.size;
                    return new Ouput(output);
                case REL_BASE_OFFSET:
                    checkState(i.size == 2);
                    state.relativeBase = state.relativeBase.add(state.evalParam(i.param1, i.param1mode));
                    break;
                default:
                    throw new IllegalArgumentException();
            }
            state.pc += i.size;
        }
    }

    private static Instruction parseInstr(BigInteger[] program, int pc) {
        // ABCDE as per docs
        int opcodeAndArgsSpec = program[pc].intValueExact();
        int DE = opcodeAndArgsSpec % 100;
        ParamMode C = ParamMode.fromInt((opcodeAndArgsSpec / 100) % 10);
        ParamMode B = ParamMode.fromInt((opcodeAndArgsSpec / 1000) % 10);
        ParamMode A = ParamMode.fromInt((opcodeAndArgsSpec / 10000) % 10);

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
                        C,
                        program[pc + 2],
                        B,
                        program[pc + 3],
                        A);
            case JIT:
            case JIF:
                checkArgument(A == ParamMode.POSITION);
                return new Instruction(
                        3,
                        opcode,
                        program[pc + 1],
                        C,
                        program[pc + 2],
                        B,
                        null,
                        null);

            case INPUT:
            case OUTPUT:
            case REL_BASE_OFFSET:
                checkArgument(B == ParamMode.POSITION);
                checkArgument(A == ParamMode.POSITION);
                return new Instruction(
                        2,
                        opcode,
                        program[pc + 1],
                        C,
                        null,
                        null,
                        null,
                        null);
            case END:
                checkArgument(C == ParamMode.POSITION);
                checkArgument(B == ParamMode.POSITION);
                checkArgument(A == ParamMode.POSITION);
                return new Instruction(
                        1,
                        opcode,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
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
        REL_BASE_OFFSET, // 9
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
                case 9:
                    return REL_BASE_OFFSET;
                case 99:
                    return END;
                default:
                    throw new IllegalArgumentException("val: " + i);
            }
        }
    }

    enum ParamMode {
        IMMEDIATE,
        POSITION,
        RELATIVE;

        public static ParamMode fromInt(int i) {
            switch (i) {
                case 0:
                    return POSITION;
                case 1:
                    return IMMEDIATE;
                case 2:
                    return RELATIVE;
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    @Value
    static class Instruction {
        int size;
        OP opcode;
        BigInteger param1;
        ParamMode param1mode;
        BigInteger param2;
        ParamMode param2mode;
        BigInteger param3;
        ParamMode param3mode;
    }

    static class ProgramState {
        BigInteger[] program;
        BigInteger programLen;
        Map<BigInteger, BigInteger> extraMem = new HashMap<>();
        int pc = 0;
        BigInteger relativeBase = BigInteger.ZERO;

        public ProgramState(BigInteger[] program) {
            this.program = program.clone();
            this.programLen = BigInteger.valueOf(program.length);
        }

        BigInteger evalParam(BigInteger param, ParamMode mode) {
            switch (mode) {
                case IMMEDIATE:
                    return param;
                case POSITION:
                    return readMem(param);
                case RELATIVE:
                    return readMem(relativeBase.add(param));
                default:
                    throw new IllegalArgumentException();
            }
        }

        BigInteger readMem(BigInteger address) {
            checkArgument(address.compareTo(BigInteger.ZERO) >= 0);
            if (address.compareTo(programLen) < 0) {
                return program[address.intValueExact()];
            }
            return extraMem.getOrDefault(address, BigInteger.ZERO);
        }

        void writeMem(BigInteger address, BigInteger val) {
            checkArgument(address.compareTo(BigInteger.ZERO) >= 0);
            if (address.compareTo(programLen) < 0) {
                program[address.intValueExact()] = val;
            }
             extraMem.put(address, val);
        }

        public void writeToParam(BigInteger param, ParamMode mode, BigInteger val) {
            switch (mode) {
                case IMMEDIATE:
                    throw new IllegalArgumentException();
                case POSITION:
                    writeMem(param, val);
                    break;
                case RELATIVE:
                    writeMem(relativeBase.add(param), val);
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    private static BigInteger[] parse(String spec)  {
        return Arrays.stream(spec.split(" *, *"))
                .map(BigInteger::new)
                .toArray(BigInteger[]::new);
    }

    static BigInteger[] example1 = parse("109,1,204,-1,1001,100,1,100,1008,100,16,101,1006,101,0,99");
    static BigInteger[] example2 = parse("1102,34915192,34915192,7,4,7,99,0");
    static BigInteger[] example3 = parse("104,1125899906842624,99");

    static BigInteger[] input = parse("1102, 34463338, 34463338, 63, 1007, 63, 34463338, 63, 1005, 63, 53, 1101, 3, 0, 1000, 109, 988, 209, 12, 9, 1000, 209, 6, 209, 3, 203, 0, 1008, 1000, 1, 63, 1005, 63, 65, 1008, 1000, 2, 63, 1005, 63, 904, 1008, 1000, 0, 63, 1005, 63, 58, 4, 25, 104, 0, 99, 4, 0, 104, 0, 99, 4, 17, 104, 0, 99, 0, 0, 1102, 1, 550, 1027, 1101, 0, 0, 1020, 1101, 30, 0, 1004, 1101, 0, 22, 1014, 1102, 1, 36, 1009, 1101, 37, 0, 1007, 1102, 25, 1, 1010, 1102, 1, 33, 1012, 1102, 282, 1, 1029, 1102, 1, 488, 1025, 1101, 0, 31, 1019, 1101, 0, 21, 1008, 1101, 0, 35, 1015, 1101, 664, 0, 1023, 1102, 26, 1, 1001, 1101, 28, 0, 1016, 1102, 29, 1, 1005, 1102, 1, 24, 1002, 1101, 20, 0, 1018, 1101, 27, 0, 1013, 1101, 38, 0, 1017, 1102, 1, 1, 1021, 1102, 1, 557, 1026, 1102, 1, 39, 1000, 1101, 23, 0, 1006, 1101, 493, 0, 1024, 1102, 1, 291, 1028, 1101, 671, 0, 1022, 1101, 0, 34, 1003, 1101, 0, 32, 1011, 109, 10, 21108, 40, 40, 8, 1005, 1018, 199, 4, 187, 1105, 1, 203, 1001, 64, 1, 64, 1002, 64, 2, 64, 109, -14, 2108, 30, 8, 63, 1005, 63, 225, 4, 209, 1001, 64, 1, 64, 1105, 1, 225, 1002, 64, 2, 64, 109, 3, 2102, 1, 4, 63, 1008, 63, 34, 63, 1005, 63, 251, 4, 231, 1001, 64, 1, 64, 1106, 0, 251, 1002, 64, 2, 64, 109, 12, 2107, 22, -5, 63, 1005, 63, 269, 4, 257, 1105, 1, 273, 1001, 64, 1, 64, 1002, 64, 2, 64, 109, 20, 2106, 0, -3, 4, 279, 1001, 64, 1, 64, 1106, 0, 291, 1002, 64, 2, 64, 109, -16, 21108, 41, 40, -3, 1005, 1012, 311, 1001, 64, 1, 64, 1105, 1, 313, 4, 297, 1002, 64, 2, 64, 109, -13, 2101, 0, 2, 63, 1008, 63, 30, 63, 1005, 63, 335, 4, 319, 1105, 1, 339, 1001, 64, 1, 64, 1002, 64, 2, 64, 109, -3, 2102, 1, 4, 63, 1008, 63, 35, 63, 1005, 63, 359, 1106, 0, 365, 4, 345, 1001, 64, 1, 64, 1002, 64, 2, 64, 109, 15, 1205, 6, 377, 1105, 1, 383, 4, 371, 1001, 64, 1, 64, 1002, 64, 2, 64, 109, 5, 21102, 42, 1, -2, 1008, 1017, 39, 63, 1005, 63, 403, 1106, 0, 409, 4, 389, 1001, 64, 1, 64, 1002, 64, 2, 64, 109, -17, 21107, 43, 44, 10, 1005, 1012, 431, 4, 415, 1001, 64, 1, 64, 1106, 0, 431, 1002, 64, 2, 64, 109, 14, 21107, 44, 43, -4, 1005, 1012, 451, 1001, 64, 1, 64, 1106, 0, 453, 4, 437, 1002, 64, 2, 64, 109, 1, 21102, 45, 1, -3, 1008, 1014, 45, 63, 1005, 63, 479, 4, 459, 1001, 64, 1, 64, 1105, 1, 479, 1002, 64, 2, 64, 109, 7, 2105, 1, 0, 4, 485, 1106, 0, 497, 1001, 64, 1, 64, 1002, 64, 2, 64, 109, 5, 1206, -8, 513, 1001, 64, 1, 64, 1106, 0, 515, 4, 503, 1002, 64, 2, 64, 109, -33, 2101, 0, 7, 63, 1008, 63, 32, 63, 1005, 63, 535, 1106, 0, 541, 4, 521, 1001, 64, 1, 64, 1002, 64, 2, 64, 109, 23, 2106, 0, 8, 1001, 64, 1, 64, 1106, 0, 559, 4, 547, 1002, 64, 2, 64, 109, -1, 21101, 46, 0, -5, 1008, 1013, 46, 63, 1005, 63, 585, 4, 565, 1001, 64, 1, 64, 1105, 1, 585, 1002, 64, 2, 64, 109, -4, 21101, 47, 0, 2, 1008, 1016, 44, 63, 1005, 63, 605, 1105, 1, 611, 4, 591, 1001, 64, 1, 64, 1002, 64, 2, 64, 109, -18, 1207, 4, 38, 63, 1005, 63, 627, 1106, 0, 633, 4, 617, 1001, 64, 1, 64, 1002, 64, 2, 64, 109, 5, 2107, 22, 7, 63, 1005, 63, 649, 1106, 0, 655, 4, 639, 1001, 64, 1, 64, 1002, 64, 2, 64, 109, 12, 2105, 1, 10, 1001, 64, 1, 64, 1106, 0, 673, 4, 661, 1002, 64, 2, 64, 109, -10, 1208, 6, 33, 63, 1005, 63, 693, 1001, 64, 1, 64, 1106, 0, 695, 4, 679, 1002, 64, 2, 64, 109, -7, 2108, 35, 7, 63, 1005, 63, 715, 1001, 64, 1, 64, 1106, 0, 717, 4, 701, 1002, 64, 2, 64, 109, 6, 1208, 5, 37, 63, 1005, 63, 735, 4, 723, 1106, 0, 739, 1001, 64, 1, 64, 1002, 64, 2, 64, 109, -4, 1202, 5, 1, 63, 1008, 63, 34, 63, 1005, 63, 765, 4, 745, 1001, 64, 1, 64, 1105, 1, 765, 1002, 64, 2, 64, 109, 29, 1206, -7, 783, 4, 771, 1001, 64, 1, 64, 1105, 1, 783, 1002, 64, 2, 64, 109, -28, 1201, 6, 0, 63, 1008, 63, 29, 63, 1005, 63, 809, 4, 789, 1001, 64, 1, 64, 1106, 0, 809, 1002, 64, 2, 64, 109, 5, 1202, 2, 1, 63, 1008, 63, 20, 63, 1005, 63, 829, 1106, 0, 835, 4, 815, 1001, 64, 1, 64, 1002, 64, 2, 64, 109, -1, 1201, 6, 0, 63, 1008, 63, 35, 63, 1005, 63, 859, 1001, 64, 1, 64, 1105, 1, 861, 4, 841, 1002, 64, 2, 64, 109, 2, 1207, -3, 25, 63, 1005, 63, 879, 4, 867, 1105, 1, 883, 1001, 64, 1, 64, 1002, 64, 2, 64, 109, 13, 1205, 3, 901, 4, 889, 1001, 64, 1, 64, 1106, 0, 901, 4, 64, 99, 21101, 0, 27, 1, 21101, 915, 0, 0, 1106, 0, 922, 21201, 1, 22987, 1, 204, 1, 99, 109, 3, 1207, -2, 3, 63, 1005, 63, 964, 21201, -2, -1, 1, 21101, 0, 942, 0, 1106, 0, 922, 22101, 0, 1, -1, 21201, -2, -3, 1, 21101, 0, 957, 0, 1106, 0, 922, 22201, 1, -1, -2, 1105, 1, 968, 21202, -2, 1, -2, 109, -3, 2105, 1, 0");
}

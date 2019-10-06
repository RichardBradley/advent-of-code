package y2018;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Iterables;
import lombok.AllArgsConstructor;

import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2018D16 {
    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(possibleMatchingOpCodes(parseSampleOps(
                "Before: [3, 2, 1, 1]",
                "9 2 1 2",
                "After:  [3, 2, 2, 1]").get(0)).size()).isEqualTo(3);

        System.out.println("how many samples in your puzzle input behave like three or more opcodes?");
        List<SampleOp> sampleOps = parseSampleOps(Y2018D16input.sampleOps);
        System.out.println(sampleOps.stream()
                .filter(x -> possibleMatchingOpCodes(x).size() >= 3)
                .count());

        // 2
        Map<Integer, Opcode> opcodeMappings = determineOpcodes(sampleOps);
        long[] registers = eval(opcodeMappings, Y2018D16input.testProgram);
        System.out.println("What value is contained in register 0 after executing the test program?");
        System.out.println(registers[0]);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static long[] eval(Map<Integer, Opcode> opcodeMappings, String[] instructions) {
        long[] registers = new long[16];
        for (String instruction : instructions) {
            String[] items = instruction.split(" ");
            checkState(items.length == 4);
            OpcodeWithArgs opcodeWithArgs = new OpcodeWithArgs(
                    opcodeMappings.get(Integer.parseInt(items[0])),
                    Integer.parseInt(items[1]),
                    Integer.parseInt(items[2]),
                    Integer.parseInt(items[3]));
            eval(opcodeWithArgs, registers);
        }
        return registers;
    }

    private static Map<Integer, Opcode> determineOpcodes(List<SampleOp> sampleOps) {
        Map<Integer, Set<Opcode>> possibleMappings = new HashMap<>();
        for (int i = 0; i < 16; i++) {
            possibleMappings.put(i, EnumSet.allOf(Opcode.class));
        }

        for (SampleOp sampleOp : sampleOps) {
            Set<Opcode> possibleOps = possibleMatchingOpCodes(sampleOp);
            possibleMappings.get(sampleOp.op[0]).retainAll(possibleOps);
        }

        Map<Integer, Opcode> acc = new HashMap<>();
        while (!possibleMappings.isEmpty()) {
            boolean changed = false;
            for (Iterator<Map.Entry<Integer, Set<Opcode>>> iterator = possibleMappings.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<Integer, Set<Opcode>> entry = iterator.next();

                if (entry.getValue().size() == 1) {
                    changed = true;
                    Opcode opcode = Iterables.getOnlyElement(entry.getValue());
                    acc.put(entry.getKey(), opcode);
                    iterator.remove();
                    for (Set<Opcode> opcodes : possibleMappings.values()) {
                        opcodes.remove(opcode);
                    }
                }
            }

            checkState(changed);
        }

        checkState(acc.size() == 16);

        return acc;
    }

    static Set<Opcode> possibleMatchingOpCodes(SampleOp sampleOp) {
        Set<Opcode> matching = EnumSet.noneOf(Opcode.class);
        OpcodeWithArgs opcodeWithArgs = new OpcodeWithArgs(
                null,
                sampleOp.op[1],
                sampleOp.op[2],
                sampleOp.op[3]);
        for (Opcode op : Opcode.values()) {
            long[] registers = sampleOp.beforeRegisters.clone();
            opcodeWithArgs.op = op;
            eval(opcodeWithArgs, registers);
            if (Arrays.equals(registers, sampleOp.afterRegisters)) {
                matching.add(op);
            }
        }
        return matching;
    }

    @AllArgsConstructor
    static class OpcodeWithArgs {
        Opcode op;
        int a;
        int b;
        int c;

        public String toHumanReadable(int ipcIdx) {
            String[] registerNames = new String[]{"a", "b", "c", "d", "e", "f"};
            registerNames[ipcIdx] = "IPC";

            switch (op) {
                case addr: // (add register) stores into register C the result of adding register A and register B.
                    return (registerNames[c] + " = " + registerNames[a] + " + " + registerNames[b]);
                case addi: // (add immediate) stores into register C the result of adding register A and value B.
                    return (registerNames[c] + " = " + registerNames[a] + " + " + b);
                case mulr: // (multiply register) stores into register C the result of multiplying register A and register B.
                    return (registerNames[c] + " = " + registerNames[a] + " * " + registerNames[b]);
                case muli: // (multiply immediate) stores into register C the result of multiplying register A and value B.
                    return (registerNames[c] + " = " + registerNames[a] + " * " + b);
                case banr: // (bitwise AND register) stores into register C the result of the bitwise AND of register A and register B.
                    return (registerNames[c] + " = " + registerNames[a] + " & " + registerNames[b]);
                case bani: // (bitwise AND immediate) stores into register C the result of the bitwise AND of register A and value B.
                    return (registerNames[c] + " = " + registerNames[a] + " & " + b);
                case borr: // (bitwise OR register) stores into register C the result of the bitwise OR of register A and register B.
                    return (registerNames[c] + " = " + registerNames[a] + " | " + registerNames[b]);
                case bori: // (bitwise OR immediate) stores into register C the result of the bitwise OR of register A and value B.
                    return (registerNames[c] + " = " + registerNames[a] + " | " + b);
                case setr: // (set register) copies the contents of register A into register C. (Input B is ignored.)
                    return (registerNames[c] + " = " + registerNames[a]);
                case seti: // (set immediate) stores value A into register C. (Input B is ignored.)
                    return (registerNames[c] + " = " + a);
                case gtir: // (greater-than immediate/register) sets register C to 1 if value A is greater than register B. Otherwise, register C is set to 0.
                    return (registerNames[c] + " = (" + a + " > " + registerNames[b] + " ? 1 : 0)");
                case gtri: // (greater-than register/immediate) sets register C to 1 if register A is greater than value B. Otherwise, register C is set to 0.
                    return (registerNames[c] + " = (" + registerNames[a] + " > " + b + " ? 1 : 0)");
                case gtrr: // (greater-than register/register) sets register C to 1 if register A is greater than register B. Otherwise, register C is set to 0.
                    return (registerNames[c] + " = (" + registerNames[a] + " > " + registerNames[b] + " ? 1 : 0)");
                case eqir: // (equal immediate/register) sets register C to 1 if value A is equal to register B. Otherwise, register C is set to 0.
                    return (registerNames[c] + " = (" + a + " == " + registerNames[b] + " ? 1 : 0)");
                case eqri: // (equal register/immediate) sets register C to 1 if register A is equal to value B. Otherwise, register C is set to 0.
                    return (registerNames[c] + " = (" + registerNames[a] + " == " + b + " ? 1 : 0)");
                case eqrr: // (equal register/register) sets register C to 1 if register A is equal to register B. Otherwise, register C is set to 0.
                    return (registerNames[c] + " = (" + registerNames[a] + " == " + registerNames[b] + " ? 1 : 0)");
                default:
                    throw new IllegalArgumentException("" + op);
            }
        }
    }

    enum Opcode {
        addr, // (add register) stores into register C the result of adding register A and register B.
        addi, // (add immediate) stores into register C the result of adding register A and value B.
        mulr, // (multiply register) stores into register C the result of multiplying register A and register B.
        muli, // (multiply immediate) stores into register C the result of multiplying register A and value B.
        banr, // (bitwise AND register) stores into register C the result of the bitwise AND of register A and register B.
        bani, // (bitwise AND immediate) stores into register C the result of the bitwise AND of register A and value B.
        borr, // (bitwise OR register) stores into register C the result of the bitwise OR of register A and register B.
        bori, // (bitwise OR immediate) stores into register C the result of the bitwise OR of register A and value B.
        setr, // (set register) copies the contents of register A into register C. (Input B is ignored.)
        seti, // (set immediate) stores value A into register C. (Input B is ignored.)
        gtir, // (greater-than immediate/register) sets register C to 1 if value A is greater than register B. Otherwise, register C is set to 0.
        gtri, // (greater-than register/immediate) sets register C to 1 if register A is greater than value B. Otherwise, register C is set to 0.
        gtrr, // (greater-than register/register) sets register C to 1 if register A is greater than register B. Otherwise, register C is set to 0.
        eqir, // (equal immediate/register) sets register C to 1 if value A is equal to register B. Otherwise, register C is set to 0.
        eqri,// (equal register/immediate) sets register C to 1 if register A is equal to value B. Otherwise, register C is set to 0.
        eqrr; // (equal register/register) sets register C to 1 if register A is equal to register B. Otherwise, register C is set to 0.
    }

    static void eval(OpcodeWithArgs o, long[] registers) {
        switch (o.op) {
            case addr:
                registers[o.c] = registers[o.a] + registers[o.b];
                break;
            case addi:
                registers[o.c] = registers[o.a] + o.b;
                break;
            case mulr:
                registers[o.c] = registers[o.a] * registers[o.b];
                break;
            case muli:
                registers[o.c] = registers[o.a] * o.b;
                break;
            case banr:
                registers[o.c] = registers[o.a] & registers[o.b];
                break;
            case bani:
                registers[o.c] = registers[o.a] & o.b;
                break;
            case borr:
                registers[o.c] = registers[o.a] | registers[o.b];
                break;
            case bori:
                registers[o.c] = registers[o.a] | o.b;
                break;
            case setr:
                registers[o.c] = registers[o.a];
                break;
            case seti:
                registers[o.c] = o.a;
                break;
            case gtir: // (greater-than immediate/register) sets register C to 1 if value A is greater than register B. Otherwise, register C is set to 0.
                registers[o.c] = o.a > registers[o.b]
                        ? 1
                        : 0;
                break;
            case gtri: // (greater-than register/immediate) sets register C to 1 if register A is greater than value B. Otherwise, register C is set to 0.
                registers[o.c] = registers[o.a] > o.b
                        ? 1
                        : 0;
                break;
            case gtrr: // (greater-than register/register) sets register C to 1 if register A is greater than register B. Otherwise, register C is set to 0.
                registers[o.c] = registers[o.a] > registers[o.b]
                        ? 1
                        : 0;
                break;
            case eqir: // (equal immediate/register) sets register C to 1 if value A is equal to register B. Otherwise, register C is set to 0.
                registers[o.c] = o.a == registers[o.b]
                        ? 1
                        : 0;
                break;
            case eqri: // (equal register/immediate) sets register C to 1 if register A is equal to value B. Otherwise, register C is set to 0.
                registers[o.c] = registers[o.a] == o.b
                        ? 1
                        : 0;
                break;
            case eqrr: // (equal register/register) sets register C to 1 if register A is equal to register B. Otherwise, register C is set to 0.
                registers[o.c] = registers[o.a] == registers[o.b]
                        ? 1
                        : 0;
                break;
            default:
                throw new IllegalArgumentException(o.op + "");
        }
    }


    @AllArgsConstructor
    static class SampleOp {
        long[] beforeRegisters;
        int[] op;
        long[] afterRegisters;
    }

    static List<SampleOp> parseSampleOps(String... input) {
        List<SampleOp> acc = new ArrayList<>();
        Pattern line1 = Pattern.compile("Before: *\\[(\\d), (\\d), (\\d), (\\d)]");
        Pattern line2 = Pattern.compile("(\\d+) (\\d+) (\\d+) (\\d+)");
        Pattern line3 = Pattern.compile("After: *\\[(\\d), (\\d), (\\d), (\\d)]");

        for (int i = 0; i < input.length; i += 4) {
            Matcher m1 = line1.matcher(input[i]);
            Matcher m2 = line2.matcher(input[i + 1]);
            Matcher m3 = line3.matcher(input[i + 2]);
            checkState(m1.matches());
            checkState(m2.matches());
            checkState(m3.matches());
            checkState((i + 3 >= input.length) || input[i + 3].isEmpty());
            acc.add(new SampleOp(
                    new long[]{Integer.parseInt(m1.group(1)), Integer.parseInt(m1.group(2)), Integer.parseInt(m1.group(3)), Integer.parseInt(m1.group(4)),},
                    new int[]{Integer.parseInt(m2.group(1)), Integer.parseInt(m2.group(2)), Integer.parseInt(m2.group(3)), Integer.parseInt(m2.group(4)),},
                    new long[]{Integer.parseInt(m3.group(1)), Integer.parseInt(m3.group(2)), Integer.parseInt(m3.group(3)), Integer.parseInt(m3.group(4)),}
            ));
        }
        return acc;
    }
}

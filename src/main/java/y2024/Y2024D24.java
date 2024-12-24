package y2024;

import com.google.common.base.Stopwatch;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static aoc.Common.loadInputFromResources;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2024D24 {

    public static void main(String[] args) throws Exception {
        List<String> input = loadInputFromResources();
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(part1(example)).isEqualTo(2024);
        assertThat(part1(input)).isEqualTo(58740594706150L);

        // 2
        assertThat(part2(input)).isEqualTo("cvh,dbb,hbk,kvn,tfn,z14,z18,z23");

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static long part1(List<String> input) {
        Map<String, Boolean> wireStates = new HashMap<>();
        int lineIdx = 0;
        Pattern initPatt = Pattern.compile("(\\w\\d+): (1|0)");
        for (; ; lineIdx++) {
            String line = input.get(lineIdx);
            if (line.isEmpty()) {
                lineIdx++;
                break;
            }
            Matcher m = initPatt.matcher(line);
            checkState(m.matches());
            checkState(null == wireStates.put(m.group(1), "1".equals(m.group(2))));
        }

        List<Gate> gates = new ArrayList<>();
        Pattern gatePatt = Pattern.compile("(\\w+) (AND|OR|XOR) (\\w+) -> (\\w+)");
        for (; lineIdx < input.size(); lineIdx++) {
            Matcher m = gatePatt.matcher(input.get(lineIdx));
            checkState(m.matches());
            gates.add(new Gate(m.group(1), m.group(3), m.group(4), m.group(2)));
        }

        Map<String, Gate> gatesByOutput = new HashMap<>();
        for (Gate gate : gates) {
            checkState(null == gatesByOutput.put(gate.getOut(), gate));
        }

        int bitCount = gates.stream()
                .map(g -> g.out)
                .filter(s -> s.startsWith("z"))
                .mapToInt(s -> Integer.parseInt(s.substring(1)))
                .max().getAsInt() + 1;

        long z = 0;
        for (int i = bitCount - 1; i >= 0; i--) {
            z <<= 1;
            z |= (getWire("z" + f02(i), wireStates, gatesByOutput)) ? 1 : 0;
        }
        return z;
    }

    private static String f02(int i) {
        if (i < 10) {
            return "0" + i;
        }
        return "" + i;
    }

    private static boolean getWire(String name, Map<String, Boolean> wireStates, Map<String, Gate> gatesByOutput) {
        char c = name.charAt(0);
        if (c == 'x' || c == 'y') {
            return wireStates.get(name);
        }
        Gate gate = gatesByOutput.get(name);
        checkNotNull(gate);
        boolean inA = getWire(gate.inA, wireStates, gatesByOutput);
        boolean inB = getWire(gate.inB, wireStates, gatesByOutput);
        switch (gate.op) {
            case "AND":
                return inA && inB;
            case "OR":
                return inA || inB;
            case "XOR":
                return inA != inB;
            default:
                throw new IllegalArgumentException();
        }
    }

    private static void runCircuits(List<Gate> gates, Map<String, Boolean> wireStates) {
        boolean changesMade = false;
        while (!gates.isEmpty()) {
            Iterator<Gate> it = gates.iterator();
            while (it.hasNext()) {
                Gate gate = it.next();
                Boolean inA = wireStates.get(gate.inA);
                Boolean inB = wireStates.get(gate.inB);
                if (inA != null && inB != null) {
                    boolean out;
                    switch (gate.op) {
                        case "AND":
                            out = inA && inB;
                            break;
                        case "OR":
                            out = inA || inB;
                            break;
                        case "XOR":
                            out = inA != inB;
                            break;
                        default:
                            throw new IllegalArgumentException();
                    }

                    checkState(null == wireStates.put(gate.out, out));
                    changesMade = true;
                    it.remove();
                }
            }

            checkState(changesMade);
        }
    }

    @Data
    @AllArgsConstructor
    static class Gate {
        String inA, inB, out, op;
    }

    private static String part2(List<String> input) {
        List<Gate> gates = new ArrayList<>();
        Pattern gatePatt = Pattern.compile("(\\w+) (AND|OR|XOR) (\\w+) -> (\\w+)");
        int lineIdx = input.indexOf("") + 1;
        for (; lineIdx < input.size(); lineIdx++) {
            Matcher m = gatePatt.matcher(input.get(lineIdx));
            checkState(m.matches());
            gates.add(new Gate(m.group(1), m.group(3), m.group(4), m.group(2)));
        }

        Map<String, Gate> gatesByOutput = new HashMap<>();
        for (Gate gate : gates) {
            checkState(null == gatesByOutput.put(gate.getOut(), gate));
        }

        int bitCount = gates.stream()
                .map(g -> g.out)
                .filter(s -> s.startsWith("z"))
                .mapToInt(s -> Integer.parseInt(s.substring(1)))
                .max().getAsInt() + 1;

        Set<Integer> swappedIdx = new HashSet<>();

        // test the restricted adder
        nBitsLoop:
        for (int nBits = 1; nBits <= bitCount; nBits++) {
            System.out.printf("Testing adder with nBits = %s, swaps = %s\n", nBits, swappedIdx);
            if (adderOk(gatesByOutput, nBits)) {
                System.out.printf("Adder ok\n");
                continue;
            } else {
                // Swap 1 pair of output wires
                for (int g1 = 0; g1 < gates.size(); g1++) {
                    if (swappedIdx.contains(g1)) continue;
                    for (int g2 = g1 + 1; g2 < gates.size(); g2++) {
                        if (swappedIdx.contains(g2)) continue;

                        swappedIdx.add(g1);
                        swappedIdx.add(g2);
                        Gate gate1 = gates.get(g1);
                        Gate gate2 = gates.get(g2);
                        String tmp = gate1.out;
                        gate1.out = gate2.out;
                        gate2.out = tmp;
                        gatesByOutput.put(gate1.out, gate1);
                        gatesByOutput.put(gate2.out, gate2);

                        if (adderOk(gatesByOutput, nBits)) {
                            if (swappedIdx.size() == 8) {
                                checkState(adderOk(gatesByOutput, bitCount - 1));
                                return swappedIdx.stream()
                                        .map(i -> gates.get(i).out)
                                        .sorted()
                                        .collect(Collectors.joining(","));
                            }
                            continue nBitsLoop;
                        } else {
                            swappedIdx.remove(g1);
                            swappedIdx.remove(g2);
                            tmp = gate1.out;
                            gate1.out = gate2.out;
                            gate2.out = tmp;
                            gatesByOutput.put(gate1.out, gate1);
                            gatesByOutput.put(gate2.out, gate2);
                        }
                    }
                }

                throw new IllegalStateException("no good swap");
            }
        }
        throw new IllegalStateException("not found");
    }

    static Random rnd = new Random();

    private static boolean adderOk(Map<String, Gate> gatesByOutput, int nBits) {
        long bound = 1L << nBits;
        for (int testCount = 0; testCount < 100; testCount++) {
            long inputX = rnd.nextLong() & (bound - 1);
            long inputY = rnd.nextLong() & (bound - 1);
            long expectedZ = (inputX + inputY) & (bound - 1);

            Map<String, Boolean> wireStates = new HashMap<>();
            for (int n = 0; n < nBits; n++) {
                wireStates.put("x" + f02(n), 0 != (1 & (inputX >> n)));
                wireStates.put("y" + f02(n), 0 != (1 & (inputY >> n)));
            }

            long z = 0;
            try {
                for (int i = nBits - 1; i >= 0; i--) {
                    z <<= 1;
                    z |= (getWire("z" + f02(i), wireStates, gatesByOutput)) ? 1 : 0;
                }
            } catch (StackOverflowError | Exception e) {
                return false;
            }

            if (expectedZ != z) {
                return false;
            }

        }
        return true;
    }

    static List<String> example = List.of(
            "x00: 1",
            "x01: 0",
            "x02: 1",
            "x03: 1",
            "x04: 0",
            "y00: 1",
            "y01: 1",
            "y02: 1",
            "y03: 1",
            "y04: 1",
            "",
            "ntg XOR fgs -> mjb",
            "y02 OR x01 -> tnw",
            "kwq OR kpj -> z05",
            "x00 OR x03 -> fst",
            "tgd XOR rvg -> z01",
            "vdt OR tnw -> bfw",
            "bfw AND frj -> z10",
            "ffh OR nrd -> bqk",
            "y00 AND y03 -> djm",
            "y03 OR y00 -> psh",
            "bqk OR frj -> z08",
            "tnw OR fst -> frj",
            "gnj AND tgd -> z11",
            "bfw XOR mjb -> z00",
            "x03 OR x00 -> vdt",
            "gnj AND wpb -> z02",
            "x04 AND y00 -> kjc",
            "djm OR pbm -> qhw",
            "nrd AND vdt -> hwm",
            "kjc AND fst -> rvg",
            "y04 OR y02 -> fgs",
            "y01 AND x02 -> pbm",
            "ntg OR kjc -> kwq",
            "psh XOR fgs -> tgd",
            "qhw XOR tgd -> z09",
            "pbm OR djm -> kpj",
            "x03 XOR y03 -> ffh",
            "x00 XOR y04 -> ntg",
            "bfw OR bqk -> z06",
            "nrd XOR fgs -> wpb",
            "frj XOR qhw -> z04",
            "bqk OR frj -> z07",
            "y03 OR x01 -> nrd",
            "hwm AND bqk -> z03",
            "tgd XOR rvg -> z12",
            "tnw OR pbm -> gnj");
}

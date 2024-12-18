package y2024;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static aoc.Common.loadInputFromResources;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2024D17 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        Computer input = parse(loadInputFromResources());

        // 1
        assertThat(part1(example)).isEqualTo("4,6,3,5,6,3,5,2,1,0");
        assertThat(part1(input)).isEqualTo("4,0,4,7,1,2,7,1,6");

        // 2
        printBitChangingObservations();
        assertThat(part2(input)).isEqualTo(202322348616234L);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static Computer parse(List<String> strings) {
        Pattern p = Pattern.compile("Register A: (\\d+)\n" +
                "Register B: 0\n" +
                "Register C: 0\n" +
                "\n" +
                "Program: ([\\d,]+)");
        Matcher m = p.matcher(strings.stream().collect(Collectors.joining("\n")));
        checkState(m.matches());
        long a = Long.parseLong(m.group(1));
        List<Integer> programme = Splitter.on(',').splitToList(m.group(2)).stream()
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        return new Computer(a, 0, 0, programme);
    }

    private static String part1(Computer computer) {
        int ip = 0;
        List<Long> output = new ArrayList<>();
        computer = (Computer) computer.clone();

        while (true) {
            if (ip < 0 || ip >= computer.programme.size() - 1) {
                return output.stream().map(x -> x.toString()).collect(Collectors.joining(","));
            }

            int opcode = computer.programme.get(ip);
            switch (opcode) {
                case 0: { // adv
                    long numerator = computer.A;
                    long denominator = Math.round(Math.pow(2, combo(computer, ip + 1)));
                    long val = numerator / denominator;
                    computer.A = val;
                }
                break;
                case 1: { // bxl
                    computer.B = computer.B ^ computer.programme.get(ip + 1);
                }
                break;
                case 2: { // bst
                    long x = mod(combo(computer, ip + 1), 8);
                    computer.B = x;
                }
                break;
                case 3: { // jnz
                    if (computer.A != 0) {
                        ip = computer.programme.get(ip + 1);
                        continue;
                    }
                }
                break;
                case 4: { // bxc
                    computer.B = computer.B ^ computer.C;
                }
                break;
                case 5: { // out
                    long n = mod(combo(computer, ip + 1), 8);
                    output.add(n);
                }
                break;
                case 6: { // bdv
                    long numerator = computer.A;
                    long denominator = Math.round(Math.pow(2, combo(computer, ip + 1)));
                    long val = numerator / denominator;
                    computer.B = val;
                }
                break;
                case 7: { // cdv
                    long numerator = computer.A;
                    long denominator = Math.round(Math.pow(2, combo(computer, ip + 1)));
                    long val = numerator / denominator;
                    computer.C = val;
                }
                break;
                default:
                    throw new IllegalArgumentException("Unknown opcode " + opcode);
            }

            ip += 2;
        }
    }

    private static long mod(long a, long m) {
        long ret = a % m;
        if (ret < 0) {
            ret += m;
        }
        return ret;
    }

    private static long combo(Computer computer, int idx) {
        int n = computer.programme.get(idx);
        switch (n) {
            case 0:
            case 1:
            case 2:
            case 3:
                return n;
            case 4:
                return computer.A;
            case 5:
                return computer.B;
            case 6:
                return computer.C;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * INPUT prog
     * <p>
     * 2  bst  B = A & 0b111
     * 4
     * 1, bxl B = B ^ 1
     * 1
     * 7, cdv C = A >> B    B is 0-7 here
     * 5
     * 0  adv A = A >> 3
     * 3
     * 1, bxl  B = B ^ 0b100
     * 4,
     * 4, bxc  B = B ^ C
     * 5,
     * 5, out(B)
     * 5,
     * 3, jnz if (A != 0) goto 0
     * 0
     */
    private static List<Integer> part1_transpiled(long A) {
        List<Integer> acc = new ArrayList<>();

        long B, C;
        do {
            B = A & 0b111;
            B = B ^ 1;
            C = A >> B;
            A = A >> 3;
            B = B ^ 0b100;
            B = B ^ C;

            acc.add((int) (B & 0b111));

            if (acc.size() > 50) {
                return null;
            }

        } while (A != 0);

        return acc;
    }

    private static void printBitChangingObservations() {
        for (int i = 0; i <= 26; i++) {
            long A = 0b11000100101110101011011010;
            A = A ^ (1 << i);
            List<Integer> exp = List.of(4, 0, 4, 7, 1, 2, 7, 1, 6);

            List<Integer> obs = part1_transpiled(A);

            List<Integer> ch = new ArrayList<>();
            for (int j = 0; j < exp.size(); j++) {
                if (j >= obs.size() || exp.get(j) != obs.get(j)) {
                    ch.add(j);
                }
            }

            // changes always affect i/3 to i/3 - 2
            System.out.printf("Changing A bit %s (/3 = %s) changes output idx %s\n", i, i / 3, ch);
        }
    }

    private static long part2(Computer input) {
        // See printBitChangingObservations
        // Start at end of target seq
        // DFS until reach target
        return part2(input, 0, input.programme.size() - 1);
    }

    private static long part2(Computer input, long aSoFar, int targetSeqIdx) {
        List<Integer> target = input.programme;
        for (long n = 0; n < 8; n++) {
            long aCand = aSoFar | (n << (targetSeqIdx * 3));

            List<Integer> obs = part1_transpiled(aCand);
            if (target.equals(obs)) {
                return aCand;
            }
            if (obs.size() > targetSeqIdx &&
                    target.get(targetSeqIdx).equals(obs.get(targetSeqIdx))) {
                System.out.printf("looking at %s idx, a candidate = %s, obs = %s\n",
                        targetSeqIdx,
                        aCand,
                        obs);

                long recurse = part2(input, aCand, targetSeqIdx - 1);
                if (recurse > 0) {
                    return recurse;
                }
            }
        }
        return -1;
    }

    private static long part2_brute_force(Computer input) {
        String expected = input.programme.stream().map(x -> x.toString()).collect(Collectors.joining(","));
        for (long i = Integer.MAX_VALUE; ; i++) {
            if (i % 1000000 == 0) {
                System.out.println(Instant.now() + " " + i);
            }

            Computer c = (Computer) input.clone();
            c.A = i;
            String s = part1(c);
            if (expected.equals(s)) {
                return i;
            }
        }
    }

    @AllArgsConstructor
    static class Computer implements Cloneable {
        long A, B, C;
        List<Integer> programme;

        @SneakyThrows
        @Override
        public Object clone() {
            return super.clone();
        }
    }

    static Computer example = new Computer(729, 0, 0, List.of(0, 1, 5, 4, 3, 0));
}

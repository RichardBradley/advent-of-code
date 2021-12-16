package y2021;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

public class Y2021D16 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            String input = Resources.toString(Resources.getResource("y2021/Y2021D16.txt"), StandardCharsets.UTF_8);

            // 1
            assertThat(part1("A0016C880162017C3686B18A3D4780")).isEqualTo(31);
            assertThat(part1(input)).isEqualTo(974);

            // 2
            assertThat(part2(input)).isEqualTo(180616437720L);

        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private static long part1(String input) {
        Packet p = parse(input);
        return sumVersions(p);
    }

    private static int sumVersions(Packet p) {
        return p.version + p.children.stream().mapToInt(c -> sumVersions(c)).sum();
    }

    private static long part2(String input) {
        Packet p = parse(input);
        return eval(p);
    }

    private static long eval(Packet p) {
        switch (p.typeId) {
            case 0:
                return p.children.stream().mapToLong(c -> eval(c)).sum();
            case 1:
                return p.children.stream().mapToLong(c -> eval(c)).reduce(1, (a, b) -> a * b);
            case 2:
                return p.children.stream().mapToLong(c -> eval(c)).min().getAsLong();
            case 3:
                return p.children.stream().mapToLong(c -> eval(c)).max().getAsLong();
            case 4:
                Preconditions.checkArgument(p.children.isEmpty());
                return p.literalVal;
            case 5: {
                Preconditions.checkArgument(p.children.size() == 2);
                long a = eval(p.children.get(0));
                long b = eval(p.children.get(1));
                return a > b ? 1 : 0;
            }
            case 6: {
                Preconditions.checkArgument(p.children.size() == 2);
                long a = eval(p.children.get(0));
                long b = eval(p.children.get(1));
                return a < b ? 1 : 0;
            }
            case 7: {
                Preconditions.checkArgument(p.children.size() == 2);
                long a = eval(p.children.get(0));
                long b = eval(p.children.get(1));
                return a == b ? 1 : 0;
            }
            default:
                throw new IllegalArgumentException();
        }
    }

    static String hexToBin(String hex) {
        StringBuilder acc = new StringBuilder();
        for (int i = 0; i < hex.length(); i++) {
            acc.append(hexToBinChar(hex.charAt(i)));
        }
        return acc.toString();
    }

    static String hexToBinChar(char hex) {
        switch (hex) {
            case '0':
                return "0000";
            case '1':
                return "0001";
            case '2':
                return "0010";
            case '3':
                return "0011";
            case '4':
                return "0100";
            case '5':
                return "0101";
            case '6':
                return "0110";
            case '7':
                return "0111";
            case '8':
                return "1000";
            case '9':
                return "1001";
            case 'A':
                return "1010";
            case 'B':
                return "1011";
            case 'C':
                return "1100";
            case 'D':
                return "1101";
            case 'E':
                return "1110";
            case 'F':
                return "1111";
            default:
                throw new IllegalArgumentException();
        }
    }

    static Packet parse(String hex) {
        BitStream in = new BitStream(hexToBin(hex), 0);
        return parse(in); // Could check stream consumed fully here...
    }

    static Packet parse(BitStream in) {
        int version = in.takeInt(3);
        int typeId = in.takeInt(3);
        if (typeId == 4) {
            // literal value
            StringBuilder acc = new StringBuilder();
            String nextChunk;
            do {
                nextChunk = in.take(5);
                acc.append(nextChunk.substring(1));
            } while (nextChunk.charAt(0) == '1');
            long literalVal = Long.parseLong(acc.toString(), 2);
            return new Packet(version, typeId, Collections.emptyList(), literalVal);
        } else {
            // Operator
            if ("0".equals(in.take(1))) {
                // If the length type ID is 0, then the next 15 bits
                // are a number that represents the total length in
                // bits of the sub-packets contained by this packet.
                int len = in.takeInt(15);
                int expectedEndIdx = in.idx + len;
                List<Packet> children = new ArrayList<>();
                while (in.idx < expectedEndIdx) {
                    children.add(parse(in));
                }
                assertThat(in.idx).isEqualTo(expectedEndIdx);
                return new Packet(version, typeId, children, null);
            } else {
                // If the length type ID is 1, then the next 11
                // bits are a number that represents the number of
                // sub-packets immediately contained by this packet.
                int count = in.takeInt(11);
                List<Packet> children = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    children.add(parse(in));
                }
                return new Packet(version, typeId, children, null);
            }
        }
    }

    @AllArgsConstructor
    private static class BitStream {
        String input;
        int idx;

        String take(int len) {
            String ret = input.substring(idx, idx + len);
            idx += len;
            return ret;
        }

        int takeInt(int len) {
            return Integer.parseInt(take(len), 2);
        }
    }

    @Value
    static class Packet {
        int version;
        int typeId;
        List<Packet> children;
        Long literalVal;
    }
}

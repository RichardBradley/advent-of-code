package y2017;

import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;

import java.awt.*;
import java.util.List;
import java.util.Queue;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;

public class Y2017D14 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        String input = "hwlqcszp";

        assertThat(hexToBin("a0c201")).isEqualTo("101000001100001000000001");

        assertThat(countSetBits(hashToUsageArray(input))).isEqualTo(8304);
        assertThat(countSetRegions(hashToUsageArray(input))).isEqualTo(1018);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static final Point[] connections = new Point[]{
            new Point(-1, 0),
            new Point(1, 0),
            new Point(0, -1),
            new Point(0, 1)
    };

    private static int countSetRegions(List<String> usageArray) {
        List<StringBuilder> buff = usageArray.stream()
                .map(s -> new StringBuilder(s)).collect(Collectors.toList());

        for (int regionCount = 0; ; regionCount++) {
            // Find a set bit
            Point firstSetBit = null;
            outer:
            for (int x = 0; x < buff.size(); x++) {
                StringBuilder row = buff.get(x);
                for (int y = 0; y < row.length(); y++) {
                    if ('1' == row.charAt(y)) {
                        firstSetBit = new Point(x, y);
                        break outer;
                    }
                }
            }

            if (firstSetBit == null) {
                return regionCount;
            }

            // Remove its region
            Queue<Point> floodFillQueue = new ArrayDeque<>(Collections.singleton(firstSetBit));
            Point next;
            while (null != (next = floodFillQueue.poll())) {
                buff.get(next.x).setCharAt(next.y, '0');
                for (Point delta : connections) {
                    Point neighbour = new Point(next.x + delta.x, next.y + delta.y);
                    if (neighbour.x >= 0 && neighbour.x < buff.size()) {
                        StringBuilder row = buff.get(neighbour.x);
                        if (neighbour.y >= 0 && neighbour.y < row.length()) {
                            if ('1' == row.charAt(neighbour.y)) {
                                floodFillQueue.add(neighbour);
                            }
                        }
                    }
                }
            }
        }
    }

    private static int countSetBits(List<String> usageArray) {
        return usageArray.stream()
                .mapToInt(row -> (int) row.chars().filter(c -> c == '1').count())
                .sum();
    }

    private static List<String> hashToUsageArray(String input) {
        List<String> acc = new ArrayList<>();
        for (int i = 0; i < 128; i++) {
            String rowInput = input + "-" + i;
            acc.add(hexToBin(Y2017D10.hash2(rowInput)));
        }
        return acc;
    }

    private static String hexToBin(String hex) {
        StringBuilder acc = new StringBuilder();
        for (int i = 0; i < hex.length(); i += 2) {
            int x = Integer.parseInt(hex.substring(i, i + 2), 16);
            acc.append(Strings.padStart(Integer.toBinaryString(x), 8, '0'));
        }
        return acc.toString();
    }
}


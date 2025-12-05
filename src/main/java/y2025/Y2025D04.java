package y2025;

import com.google.common.base.Stopwatch;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static aoc.Common.loadInputFromResources;
import static com.google.common.truth.Truth.assertThat;

public class Y2025D04 {

    public static void main(String[] args) throws Exception {
        List<String> input = loadInputFromResources();
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(part1(example)).isEqualTo(13);
        assertThat(part1(input)).isEqualTo(1560);

        // 2
        assertThat(part2(example)).isEqualTo(43);
        assertThat(part2(input)).isEqualTo(9609);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static long part1(List<String> input) {
        long count = 0;
        for (int y = 0; y < input.size(); y++) {
            String line = input.get(y);
            for (int x = 0; x < line.length(); x++) {
                if ('@' == line.charAt(x)) {
                    int adjCount = 0;
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dy = -1; dy <= 1; dy++) {
                            if (dx == 0 && dy == 0) {
                                continue;
                            }
                            if ('@' == get(input, x + dx, y + dy)) {
                                adjCount++;
                            }
                        }
                    }
                    if (adjCount < 4) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    private static char get(List<String> lines, int x, int y) {
        if (y < 0 || y >= lines.size()) {
            return '.';
        }
        String line = lines.get(y);
        if (x < 0 || x >= line.length()) {
            return '.';
        }
        return line.charAt(x);
    }

    private static long part2(List<String> input) {
        long count = 0;
        char[][] map = input.stream().map(s -> s.toCharArray()).toArray(i -> new char[i][]);
        boolean changesMade;
        do {
            changesMade = false;
            for (int y = 0; y < input.size(); y++) {
                char[] line = map[y];
                for (int x = 0; x < line.length; x++) {
                    if ('@' == line[x] && countNeighbors(map, x, y) < 4) {
                        line[x] = '.';
                        count++;
                        changesMade = true;
                    }
                }
            }
        } while (changesMade);

        return count;
    }

    private static int countNeighbors(char[][] map, int x, int y) {
        int count = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) {
                    continue;
                }
                if ('@' == get(map, x + dx, y + dy)) {
                    count++;
                }
            }
        }
        return count;
    }

    private static char get(char[][] lines, int x, int y) {
        if (y < 0 || y >= lines.length) {
            return '.';
        }
        char[] line = lines[y];
        if (x < 0 || x >= line.length) {
            return '.';
        }
        return line[x];
    }


    private static long findMax(StringBuilder acc, String line, int i, int digitsRemaining) {
        if (digitsRemaining == 0) {
            return Long.parseLong(acc.toString());
        }
        if (i >= line.length()) {
            return 0;
        }

        // the max will start with the highest digit left of len-digitsRemaining
        int nextDigitPos = -1;
        char nextDigitVal = '\0';
        for (int j = i; j < line.length() - digitsRemaining + 1; j++) {
            char c = line.charAt(j);
            if (c > nextDigitVal) {
                nextDigitVal = c;
                nextDigitPos = j;
            }
        }

        if (nextDigitPos == -1) {
            return 0;
        } else {
            acc.append(nextDigitVal);
            return findMax(acc, line, nextDigitPos + 1, digitsRemaining - 1);
        }
    }

    static List<String> example = List.of(
            "..@@.@@@@.",
            "@@@.@.@.@@",
            "@@@@@.@.@@",
            "@.@@@@..@.",
            "@@.@@@@.@@",
            ".@@@@@@@.@",
            ".@.@.@.@@@",
            "@.@@@.@@@@",
            ".@@@@@@@@.",
            "@.@.@@@.@.");
}

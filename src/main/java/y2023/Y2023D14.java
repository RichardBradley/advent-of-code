package y2023;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2023D14 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            List<String> input = Resources.readLines(Resources.getResource("y2023/Y2023D14.txt"), StandardCharsets.UTF_8);

            // 1
            assertThat(part1(input)).isEqualTo(108614);

            // 2
            assertThat(part2(example)).isEqualTo(64);
            assertThat(part2(input)).isEqualTo(96447);

        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private static long part1(List<String> input) {
        // Tilt the platform so that the rounded rocks all roll north.
        // Afterward, what is the total load on the north support beams?
        List<StringBuilder> map = input.stream().map(s -> new StringBuilder(s)).collect(Collectors.toList());
        for (int y = 0; y < map.size(); y++) {
            StringBuilder row = map.get(y);
            for (int x = 0; x < row.length(); x++) {
                if ('O' == row.charAt(x)) {
                    row.setCharAt(x, '.');
                    for (int yy = y - 1; yy >= -1; yy--) {
                        if (yy == -1 || '.' != map.get(yy).charAt(x)) {
                            map.get(yy + 1).setCharAt(x, 'O');
                            break;
                        }
                    }
                }
            }
        }

        long acc = 0;
        for (int y = 0; y < map.size(); y++) {
            StringBuilder row = map.get(y);
            int weight = map.size() - y;
            for (int x = 0; x < row.length(); x++) {
                if ('O' == row.charAt(x)) {
                    acc += weight;
                }
            }
        }
        return acc;
    }

    private static void tilt(List<StringBuilder> map, int nesw) {
        if (nesw == 0) {
            for (int y = 0; y < map.size(); y++) {
                StringBuilder row = map.get(y);
                for (int x = 0; x < row.length(); x++) {
                    if ('O' == row.charAt(x)) {
                        row.setCharAt(x, '.');
                        for (int yy = y - 1; yy >= -1; yy--) {
                            if (yy == -1 || '.' != map.get(yy).charAt(x)) {
                                map.get(yy + 1).setCharAt(x, 'O');
                                break;
                            }
                        }
                    }
                }
            }
        } else if (nesw == 2) {
            for (int y = map.size() - 1; y >= 0; y--) {
                StringBuilder row = map.get(y);
                for (int x = 0; x < row.length(); x++) {
                    if ('O' == row.charAt(x)) {
                        row.setCharAt(x, '.');
                        for (int yy = y + 1; yy <= map.size(); yy++) {
                            if (yy == map.size() || '.' != map.get(yy).charAt(x)) {
                                map.get(yy - 1).setCharAt(x, 'O');
                                break;
                            }
                        }
                    }
                }
            }
        } else if (nesw == 3) {
            int width = map.get(0).length();
            int height = map.size();
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if ('O' == map.get(y).charAt(x)) {
                        map.get(y).setCharAt(x, '.');
                        for (int xx = x - 1; xx >= -1; xx--) {
                            if (xx == -1 || '.' != map.get(y).charAt(xx)) {
                                map.get(y).setCharAt(xx + 1, 'O');
                                break;
                            }
                        }
                    }
                }
            }
        } else if (nesw == 1) {
            int width = map.get(0).length();
            int height = map.size();
            for (int x = width - 1; x >= 0; x--) {
                for (int y = 0; y < height; y++) {
                    if ('O' == map.get(y).charAt(x)) {
                        map.get(y).setCharAt(x, '.');
                        for (int xx = x + 1; xx <= width; xx++) {
                            if (xx == width || '.' != map.get(y).charAt(xx)) {
                                map.get(y).setCharAt(xx - 1, 'O');
                                break;
                            }
                        }
                    }
                }
            }
        } else {
            throw new IllegalArgumentException("unexpected: " + nesw);
        }
    }

    private static long part2(List<String> input) {
        // Tilt the platform so that the rounded rocks all roll north.
        // Afterward, what is the total load on the north support beams?
        int[] dirs = new int[]{0, 3, 2, 1};
        List<StringBuilder> map = input.stream().map(s -> new StringBuilder(s)).collect(Collectors.toList());
        int targetCycles = 1000000000;
        Map<String, Integer> historyToCycleCount = new HashMap<>();
        historyToCycleCount.put(map.stream().collect(Collectors.joining()), 0);
        for (int cycle = 1; cycle <= targetCycles; cycle++) {
            for (int i = 0; i < dirs.length; i++) {
                tilt(map, dirs[i]);
            }

            Integer prevSeen = historyToCycleCount.put(map.stream().collect(Collectors.joining()), cycle);

            if (prevSeen != null) {
                int cycleLen = cycle - prevSeen;
                int loopCount = (targetCycles - cycle) / cycleLen;
                cycle += cycleLen * loopCount;
            }
        }

        long acc = 0;
        for (int y = 0; y < map.size(); y++) {
            StringBuilder row = map.get(y);
            int weight = map.size() - y;
            for (int x = 0; x < row.length(); x++) {
                if ('O' == row.charAt(x)) {
                    acc += weight;
                }
            }
        }
        return acc;
    }

    static List<String> example = List.of(
            "O....#....",
            "O.OO#....#",
            ".....##...",
            "OO.#O....O",
            ".O.....O#.",
            "O.#..O.#.#",
            "..O..#O..O",
            ".......O..",
            "#....###..",
            "#OO..#...."
    );
}

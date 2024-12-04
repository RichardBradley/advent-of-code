package y2024;

import com.google.common.base.Stopwatch;

import java.awt.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static aoc.Common.loadInputFromResources;
import static com.google.common.truth.Truth.assertThat;

public class Y2024D04 {

    public static void main(String[] args) throws Exception {
        List<String> input = loadInputFromResources();
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(part1(example)).isEqualTo(18);
        assertThat(part1(input)).isEqualTo(2603);

        // 2
        assertThat(part2(example)).isEqualTo(9);
        assertThat(part2(input)).isEqualTo(-1);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static long part1(List<String> input) {
        String search = "XMAS";

        Point[] dirs = new Point[]{
                new Point(0, 1),
                new Point(0, -1),
                new Point(1, 0),
                new Point(-1, 0),
                new Point(1, 1),
                new Point(1, -1),
                new Point(-1, 1),
                new Point(-1, -1)
        };

        int height = input.size();
        int width = input.get(0).length();
        int matchCount = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                dir:
                for (Point dir : dirs) {
                    for (int i = 0; i < search.length(); i++) {
                        if (get(input, x + i * dir.x, y + i * dir.y) != search.charAt(i)) {
                            continue dir;
                        }
                    }
                    matchCount++;
                }
            }
        }
        return matchCount;
    }

    private static long part2(List<String> input) {
        List<List<String>> targets = List.of(
                List.of(
                        "M.S",
                        ".A.",
                        "M.S"),
                List.of(
                        "M.M",
                        ".A.",
                        "S.S"),
                List.of(
                        "S.M",
                        ".A.",
                        "S.M"),
                List.of(
                        "S.S",
                        ".A.",
                        "M.M"));

        int height = input.size();
        int width = input.get(0).length();
        int targetSize = targets.get(0).size();
        int matchCount = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                targets:
                for (List<String> target : targets) {
                    for (int yy = 0; yy < targetSize; yy++) {
                        for (int xx = 0; xx < targetSize; xx++) {
                            char targetChar = get(target, xx, yy);
                            if (targetChar != '.' && targetChar != get(input, x + xx, y + yy)) {
                                continue targets;
                            }
                        }
                    }
                    matchCount++;
                }
            }
        }
        return matchCount;
    }

    private static char get(List<String> input, int x, int y) {
        if (y >= 0 && y < input.size()) {
            String row = input.get(y);
            if (x >= 0 && x < row.length()) {
                return row.charAt(x);
            }
        }
        return ' ';
    }

    static List<String> example = List.of(
            "MMMSXXMASM",
            "MSAMXMSMSA",
            "AMXSXMAAMM",
            "MSAMASMSMX",
            "XMASAMXAMM",
            "XXAMMXXAMA",
            "SMSMSASXSS",
            "SAXAMASAAA",
            "MAMMMXMMMM",
            "MXMXAXMASX");
}

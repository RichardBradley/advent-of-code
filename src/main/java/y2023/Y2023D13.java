package y2023;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2023D13 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            List<String> input = Resources.readLines(Resources.getResource("y2023/Y2023D13.txt"), StandardCharsets.UTF_8);

            // 1
            assertThat(part2(example, 0)).isEqualTo(405);
            assertThat(part2(input, 0)).isEqualTo(35232);

            // 2
            assertThat(part2(example, 1)).isEqualTo(400);
            assertThat(part2(input, 1)).isEqualTo(37982);

        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private static long part2(List<String> input, int smudgeCount) {
        long acc = 0;
        int startIdx = 0;
        for (int i = 0; i < input.size(); i++) {
            if ("".equals(input.get(i))) {
                acc += part2x(input.subList(startIdx, i), smudgeCount);
                startIdx = i + 1;
            }
        }
        acc += part2x(input.subList(startIdx, input.size()), smudgeCount);
        return acc;
    }

    private static long part2x(List<String> input, int smudgeCount) {
        Long retVal = null;
        int height = input.size();
        int width = input.get(0).length();
        input.forEach(line -> {
            checkState(line.length() == width);
        });

        // reflectY is number of rows above reflection
        reflectYLoop:
        for (int reflectY = 1; reflectY < height; reflectY++) {
            int diffs = 0;
            for (int i = 0; ; i++) {
                int y1 = reflectY + i;
                int y2 = reflectY - i - 1;

                if (y1 >= height) {
                    break;
                }
                if (y2 < 0) {
                    break;
                }

                for (int x = 0; x < width; x++) {
                    if (input.get(y1).charAt(x) != input.get(y2).charAt(x)) {
                        diffs++;
                    }
                }
            }

            if (diffs == smudgeCount) {
                checkState(retVal == null);
                retVal = 100L * reflectY;
            }
        }

        // reflectX is number of cols left of reflection
        reflectXLoop:
        for (int reflectX = 1; reflectX < width; reflectX++) {
            int diffs = 0;
            for (int i = 0; ; i++) {
                int x1 = reflectX + i;
                int x2 = reflectX - i - 1;

                if (x1 >= width) {
                    break;
                }
                if (x2 < 0) {
                    break;
                }

                for (int y = 0; y < height; y++) {
                    String line = input.get(y);
                    if (line.charAt(x1) != line.charAt(x2)) {
                        diffs++;
                    }
                }
            }

            if (diffs == smudgeCount) {
                checkState(retVal == null);
                retVal = (long) reflectX;
            }
        }

        return retVal;
    }

    private static List<String> example = List.of(
            "#.##..##.",
            "..#.##.#.",
            "##......#",
            "##......#",
            "..#.##.#.",
            "..##..##.",
            "#.#.##.#.",
            "",
            "#...##..#",
            "#....#..#",
            "..##..###",
            "#####.##.",
            "#####.##.",
            "..##..###",
            "#....#..#"
    );
}

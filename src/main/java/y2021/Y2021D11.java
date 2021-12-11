package y2021;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;

public class Y2021D11 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            List<String> input = Resources.readLines(Resources.getResource("y2021/Y2021D11.txt"), StandardCharsets.UTF_8);

            // 1
            assertThat(run(example, false)).isEqualTo(1656);
            assertThat(run(input, false)).isEqualTo(1669);

            // 2
            assertThat(run(example, true)).isEqualTo(195);
            assertThat(run(input, true)).isEqualTo(351);

        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private static long run(List<String> input, boolean part2) {
        long steps = part2 ? Integer.MAX_VALUE : 100;
        assertThat(input.get(0).length()).isEqualTo(input.size()); // require square
        int width = input.size();
        List<char[]> map = input.stream().map(s -> s.toCharArray()).collect(Collectors.toList());
        long flashCount = 0;
        Queue<Point> flashQueue = new ArrayDeque<>();

        for (int step = 1; step <= steps; step++) {
            int flashesThisStep = 0;
            // First, the energy level of each octopus increases by 1.
            for (int y = 0; y < width; y++) {
                char[] row = map.get(y);
                for (int x = 0; x < width; x++) {
                    char after = ++row[x];
                    if (after > '9') {
                        flashQueue.add(new Point(x, y));
                    }
                }
            }

            // Then, any octopus with an energy level greater than 9 flashes.
            // This increases the energy level of all adjacent octopuses
            // by 1, including octopuses that are diagonally adjacent.
            // If this causes an octopus to have an energy level greater
            // than 9, it also flashes. This process continues as long
            // as new octopuses keep having their energy level increased
            // beyond 9. (An octopus can only flash at most once per step.)
            Point nextFlash;
            while (null != (nextFlash = flashQueue.poll())) {
                char c = map.get(nextFlash.y)[nextFlash.x];
                if (c == '0') {
                    // already flashed
                    continue;
                }

                flashCount++;
                flashesThisStep++;
                map.get(nextFlash.y)[nextFlash.x] = '0';
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        int nX = nextFlash.x + dx;
                        int nY = nextFlash.y + dy;
                        if (nX >= 0 && nX < width && nY >= 0 && nY < width) {
                            char n = map.get(nY)[nX];
                            if (n == '0') {
                                continue;
                            } else {
                                n++;
                                map.get(nY)[nX] = n;
                                if (n > '9') {
                                    flashQueue.add(new Point(nX, nY));
                                }
                            }
                        }
                    }
                }
            }

            if (part2 && flashesThisStep == width * width) {
                return step;
            }
        }

        return flashCount;
    }

    private static List<String> example = List.of(
            "5483143223",
            "2745854711",
            "5264556173",
            "6141336146",
            "6357385478",
            "4167524645",
            "2176841721",
            "6882881134",
            "4846848554",
            "5283751526"
    );
}

package y2021;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;

public class Y2021D25 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            List<String> input = Resources.readLines(Resources.getResource("y2021/Y2021D25.txt"), StandardCharsets.UTF_8);

            assertThat(part1(example)).isEqualTo(58);
            assertThat(part1(input)).isEqualTo(474);

        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private static long part1(List<String> input) {
        // What is the first step on which no sea cucumbers move?
        List<StringBuilder> map = input.stream().map(s -> new StringBuilder(s)).collect(Collectors.toList());
        int height = map.size();
        int width = map.get(0).length();

        for (int step = 1; ; step++) {
            int moveCount = 0;

            // Every step, the sea cucumbers in the east-facing herd attempt
            // to move forward one location, then the sea cucumbers in the
            // south-facing herd attempt to move forward one location.
            // EAST:
            List<Point> canMove = new ArrayList<>();
            for (int y = 0; y < height; y++) {
                StringBuilder row = map.get(y);
                for (int x = 0; x < width; x++) {
                    char c = row.charAt(x);
                    if (c == '>') {
                        if (row.charAt((x + 1) % width) == '.') {
                            canMove.add(new Point(x, y));
                        }
                    }
                }
            }
            moveCount = canMove.size();
            for (Point point : canMove) {
                map.get(point.y).setCharAt(point.x, '.');
                map.get(point.y).setCharAt((point.x + 1) % width, '>');
            }

            // SOUTH
            canMove.clear();
            for (int y = 0; y < height; y++) {
                StringBuilder row = map.get(y);
                for (int x = 0; x < width; x++) {
                    char c = row.charAt(x);
                    if (c == 'v') {
                        if (map.get((y + 1) % height).charAt(x) == '.') {
                            canMove.add(new Point(x, y));
                        }
                    }
                }
            }
            moveCount += canMove.size();
            for (Point point : canMove) {
                map.get(point.y).setCharAt(point.x, '.');
                map.get((point.y + 1) % height).setCharAt(point.x, 'v');
            }

            if (moveCount == 0) {
                return step;
            }
        }
    }

    private static List<String> example = List.of(
            "v...>>.vv>",
            ".vv>>.vv..",
            ">>.>v>...v",
            ">>v>>.>.v.",
            "v>v.vv.v..",
            ">.>>..v...",
            ".vv..>.>v.",
            "v.v..>>v.v",
            "....v..v.>"
    );
}

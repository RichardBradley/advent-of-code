package y2022;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2022D14 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        List<String> input = Resources.readLines(Resources.getResource("y2022/Y2022D14.txt"), StandardCharsets.UTF_8);

        // 1
        assertThat(part1(example)).isEqualTo(24);
        System.out.println(part1(input));

        // 2
        assertThat(part2(example)).isEqualTo(93);
        System.out.println(part2(input));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    /**
     * How many units of sand come to rest before sand starts flowing into the abyss below?
     */
    private static int part1(List<String> input) {
        return run(input, false);
    }

    private static int run(List<String> input, boolean isPart2) {

        Map<Point, Character> world = new HashMap<>();
        for (String line : input) {
            List<String> points = Splitter.on(" -> ").splitToList(line);
            for (int i = 0; i < points.size() - 1; i++) {
                Point from = parsePoint(points.get(i));
                Point to = parsePoint(points.get(i + 1));
                if (from.x > to.x || from.y > to.y) {
                    Point tmp = from;
                    from = to;
                    to = tmp;
                }
                if (from.x == to.x) {
                    for (int y = from.y; y <= to.y; y++) {
                        world.put(new Point(from.x, y), '#');
                    }
                } else {
                    checkState(from.y == to.y);
                    for (int x = from.x; x <= to.x; x++) {
                        world.put(new Point(x, from.y), '#');
                    }
                }
            }
        }

        int bottomOfWorld = world.keySet().stream().mapToInt(p -> p.y).max().getAsInt();

        if (isPart2) {
            int minX = world.keySet().stream().mapToInt(p -> p.x).min().getAsInt();
            int maxX = world.keySet().stream().mapToInt(p -> p.x).max().getAsInt();
            for (int x = minX - 5000; x < maxX + 5000; x++) {
                world.put(new Point(x, 2 + bottomOfWorld), '#');
            }
            bottomOfWorld += 2;
        }

        // Sand falls from 500,0
        int sandCount = 0;
        addSandLoop:
        while (true) {
            Point currSandPos = new Point(500, 0);
            sandMoveLoop:
            while (true) {
                // fell into abyss?
                if (currSandPos.y > bottomOfWorld) {
                    break addSandLoop;
                }
                // fall down
                Point nextSandPos = new Point(currSandPos.x, currSandPos.y + 1);
                if (null == world.get(nextSandPos)) {
                    currSandPos = nextSandPos;
                    continue sandMoveLoop;
                }
                // fall down and left
                nextSandPos = new Point(currSandPos.x - 1, currSandPos.y + 1);
                if (null == world.get(nextSandPos)) {
                    currSandPos = nextSandPos;
                    continue sandMoveLoop;
                }
                // fall down and right
                nextSandPos = new Point(currSandPos.x + 1, currSandPos.y + 1);
                if (null == world.get(nextSandPos)) {
                    currSandPos = nextSandPos;
                    continue sandMoveLoop;
                }
                // stop where you are:
                world.put(currSandPos, 'o');
                sandCount++;

                // filled world?
                if (currSandPos.x == 500 && currSandPos.y == 0) {
                    break addSandLoop;
                }
                continue addSandLoop;
            }
        }

        return sandCount;
    }

    private static Point parsePoint(String s) {
        int i = s.indexOf(',');
        return new Point(
                Integer.parseInt(s.substring(0, i)),
                Integer.parseInt(s.substring(i + 1)));
    }

    private static int part2(List<String> input) {
        return run(input, true);
    }


    private static List<String> example = List.of(
            "498,4 -> 498,6 -> 496,6",
            "503,4 -> 502,4 -> 502,9 -> 494,9");
}

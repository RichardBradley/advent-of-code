package y2017;

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

public class Y2017D22 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            List<String> input = Resources.readLines(Resources.getResource("y2017/Y2017D22.txt"), StandardCharsets.UTF_8);

            assertThat(countInfectionsAfter(example, 70)).isEqualTo(41);
            assertThat(countInfectionsAfter(input, 10000)).isEqualTo(5259);

            assertThat(countInfectionsAfter2(example, 100)).isEqualTo(26);
            assertThat(countInfectionsAfter2(input, 10000000)).isEqualTo(2511722);
        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private static int countInfectionsAfter(List<String> input, int stepCount) {
        checkState((input.size()) == input.get(0).length());
        checkState((input.size() % 2) == 1);
        int currX = input.size() / 2;
        int currY = input.size() / 2;
        int dir = 0; // NESW

        Map<Point, Boolean> isInfected = new HashMap<>();
        for (int y = 0; y < input.size(); y++) {
            String row = input.get(y);
            for (int x = 0; x < input.size(); x++) {
                isInfected.put(new Point(x, y), row.charAt(x) == '#');
            }
        }

        int infectionsCount = 0;

        // print(isInfected, new Point(currX, currY));
        for (int step = 0; step < stepCount; step++) {
            Point currPos = new Point(currX, currY);
            Boolean currIsInfected = isInfected.getOrDefault(currPos, false);
            if (currIsInfected) {
                isInfected.put(currPos, false);
                dir = (dir + 1) % 4;
            } else {
                infectionsCount++;
                isInfected.put(currPos, true);
                dir = (dir + 3) % 4;
            }

            switch (dir) {
                case 0:
                    currY--;
                    break;
                case 1:
                    currX++;
                    break;
                case 2:
                    currY++;
                    break;
                case 3:
                    currX--;
                    break;
                default:
                    throw new IllegalStateException("dir: " + dir);
            }

            //  print(isInfected, new Point(currX, currY));
        }

        return infectionsCount;
    }

    private static int countInfectionsAfter2(List<String> input, int stepCount) {
        checkState((input.size()) == input.get(0).length());
        checkState((input.size() % 2) == 1);
        int currX = input.size() / 2;
        int currY = input.size() / 2;
        int dir = 0; // NESW

        Map<Point, Character> state = new HashMap<>();
        for (int y = 0; y < input.size(); y++) {
            String row = input.get(y);
            for (int x = 0; x < input.size(); x++) {
                state.put(new Point(x, y), row.charAt(x));
            }
        }

        int infectionsCount = 0;

        for (int step = 0; step < stepCount; step++) {
            Point currPos = new Point(currX, currY);
            char currState = state.getOrDefault(currPos, '.');
            char nextState;
            switch (currState) {
                case '.':
                    nextState = 'W';
                    dir = (dir + 3) % 4;
                    break;
                case 'W':
                    infectionsCount++;
                    nextState = '#';
                    break;
                case '#':
                    nextState = 'F';
                    dir = (dir + 1) % 4;
                    break;
                case 'F':
                    nextState = '.';
                    dir = (dir + 2) % 4;
                    break;
                default:
                    throw new IllegalStateException("currState: " + currState);
            }
            state.put(currPos, nextState);

            switch (dir) {
                case 0:
                    currY--;
                    break;
                case 1:
                    currX++;
                    break;
                case 2:
                    currY++;
                    break;
                case 3:
                    currX--;
                    break;
                default:
                    throw new IllegalStateException("dir: " + dir);
            }

        }

        return infectionsCount;
    }

    private static void print(Map<Point, Boolean> map, Point currNode) {
        System.out.println("=========");
        for (int y = -10; y < 10; y++) {
            for (int x = -10; x < 10; x++) {
                Point p = new Point(x, y);
                if (p.equals(currNode)) {
                    System.out.print("*");
                } else {
                    System.out.print(map.getOrDefault(p, false) ? "#" : ".");
                }
            }
            System.out.println();
        }
    }

    private static List<String> example = List.of(
            "..#",
            "#..",
            "...");
}

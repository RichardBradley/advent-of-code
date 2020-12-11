package y2020;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;

import java.awt.*;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Y2020D11 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        List<String> input = Resources.readLines(Resources.getResource("y2020/Y2020D11.txt"), StandardCharsets.UTF_8);

        part1(example);
        part1(input);

        part2(example);
        part2(input);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static void part1(List<String> input) {

        // Simulate your seating area by applying the seating rules repeatedly
        // until no seats change state. How many seats end up occupied?

        char[][] plan = input.stream().map(line -> line.toCharArray()).toArray(char[][]::new);
        int height = plan.length;
        int width = plan[0].length;

        char[][] next = new char[height][width];

        boolean changed;
        do {
            // print(plan);
            changed = false;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    char cell = plan[y][x];
                    char nextCell;
                    if (cell == '.') {
                        nextCell = '.';
                    } else {
                        int adjacentOccupied = 0;
                        for (int dy = -1; dy <= 1; dy++) {
                            for (int dx = -1; dx <= 1; dx++) {
                                if (dy != 0 || dx != 0) {
                                    int adjx = x + dx;
                                    if (adjx >= 0 && adjx < width) {
                                        int adjy = y + dy;
                                        if (adjy >= 0 && adjy < height) {
                                            if (plan[adjy][adjx] == '#') {
                                                adjacentOccupied++;
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (cell == 'L' && adjacentOccupied == 0) {
                            nextCell = '#';
                            changed = true;
                        } else if (cell == '#' && adjacentOccupied >= 4) {
                            nextCell = 'L';
                            changed = true;
                        } else {
                            nextCell = cell;
                        }
                    }
                    next[y][x] = nextCell;
                }
            }

            char[][] tmp = plan;
            plan = next;
            next = tmp;
        } while (changed);

        int occupiedCount = Arrays.stream(plan).mapToInt(row -> CharBuffer.wrap(row).chars().map(cell -> cell == '#' ? 1 : 0).sum()).sum();

        System.out.println("occupiedCount = " + occupiedCount);
    }

    private static void part2(List<String> input) {

        // Simulate your seating area by applying the seating rules repeatedly
        // until no seats change state. How many seats end up occupied?

        char[][] plan = input.stream().map(line -> line.toCharArray()).toArray(char[][]::new);
        int height = plan.length;
        int width = plan[0].length;

        char[][] next = new char[height][width];

        Point[] dirs = new Point[]{
                new Point(-1, -1), new Point(-1, 0), new Point(-1, 1),
                new Point(0, -1), new Point(0, 1),
                new Point(1, -1), new Point(1, 0), new Point(1, 1)
        };

        boolean changed;
        do {
            // print(plan);
            changed = false;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    char cell = plan[y][x];
                    char nextCell;
                    if (cell == '.') {
                        nextCell = '.';
                    } else {
                        int visibleOccupied = 0;

                        for (Point dir : dirs) {
                            for (int dist = 1; ; dist++) {
                                int lookX = x + dir.x * dist;
                                int lookY = y + dir.y * dist;
                                if (lookX < 0 || lookX >= width || lookY < 0 || lookY >= height) {
                                    break;
                                }
                                char seen = plan[lookY][lookX];
                                if (seen == '#') {
                                    visibleOccupied++;
                                    break;
                                } else if (seen == 'L') {
                                    break;
                                }
                            }
                        }

                        if (cell == 'L' && visibleOccupied == 0) {
                            nextCell = '#';
                            changed = true;
                        } else if (cell == '#' && visibleOccupied >= 5) {
                            nextCell = 'L';
                            changed = true;
                        } else {
                            nextCell = cell;
                        }
                    }
                    next[y][x] = nextCell;
                }
            }

            char[][] tmp = plan;
            plan = next;
            next = tmp;
        } while (changed);

        int occupiedCount = Arrays.stream(plan).mapToInt(row -> CharBuffer.wrap(row).chars().map(cell -> cell == '#' ? 1 : 0).sum()).sum();

        System.out.println("occupiedCount = " + occupiedCount);
    }

    private static void print(char[][] plan) {
        for (int y = 0; y < plan.length; y++) {
            System.out.println(plan[y]);
        }
        System.out.println();
    }

    static List<String> example = ImmutableList.of(
            "L.LL.LL.LL",
            "LLLLLLL.LL",
            "L.L.L..L..",
            "LLLL.LL.LL",
            "L.LL.LL.LL",
            "L.LLLLL.LL",
            "..L.L.....",
            "LLLLLLLLLL",
            "L.LLLLLL.L",
            "L.LLLLL.LL"
    );
}

package y2018;

import com.google.common.base.Stopwatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

public class Y2018D18 {
    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(getResourceValueAfter(10, testInput)).isEqualTo(1147);

        System.out.println(getResourceValueAfter(10, input));

        // 2
        System.out.println(getResourceValueAfter(1000000000, input));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");

    }

    private static int getResourceValueAfter(int targetStepCount, String initialState) {
        char[][] grid = parse(initialState);
        char[][] grid2 = parse(initialState);
        List<String> history = new ArrayList<>();
        history.add(initialState);

        for (int step = 1; step <= targetStepCount; step++) {
            evolve(grid, grid2);
            char[][] tmp = grid;
            grid = grid2;
            grid2 = tmp;

            if (history != null) {
                String newState = toString(grid);
                history.add(newState);
                for (int oldStep = 0; oldStep < step; oldStep++) {
                    if (history.get(oldStep).equals(newState)) {
                        System.out.println("Found loop from " + oldStep + " to " + step);

                        int loopLen = step - oldStep;
                        int skipCount = (targetStepCount - step) / loopLen;
                        if (skipCount > 0) {
                            System.out.println("Skipping " + (skipCount * loopLen) + " generations using observed loop");
                        }
                        step += (skipCount * loopLen);
                        // (grid is same)
                        history = null;
                        break;
                    }
                }
            }

            // System.out.println("Step " + step + " value = " + resourceValue(grid));
        }

        return resourceValue(grid);
    }

    static char[][] parse(String state) {
        String[] lines = state.split("\\n");
        char[][] grid = new char[lines.length][];
        for (int i = 0; i < grid.length; i++) {
            String line = lines[i];
            grid[i] = new char[line.length()];
            for (int j = 0; j < line.length(); j++) {
                grid[i][j] = line.charAt(j);
            }
        }
        return grid;
    }

    static String toString(char[][] state) {
        StringBuilder acc = new StringBuilder();
        String join = "";
        for (char[] chars : state) {
            acc.append(join);
            acc.append(chars);
            join = "\n";
        }
        return acc.toString();
    }

    static int resourceValue(char[][] grid) {
        int woodCount = 0;
        int lumberYardCount = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid.length; j++) {
                if (grid[i][j] == '|') {
                    woodCount++;
                } else if (grid[i][j] == '#') {
                    lumberYardCount++;
                }
            }
        }
        return woodCount * lumberYardCount;
    }

    private static void evolve(char[][] src, char[][] dest) {
        int size = src.length;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int neighbourWoodCount = 0;
                int neighbourLumberyardCount = 0;
                for (int dx = -1; dx <= +1; dx++) {
                    for (int dy = -1; dy <= +1; dy++) {
                        if (!(dx == 0 && dy == 0)) {
                            int x = i + dx;
                            int y = j + dy;
                            if (x >= 0 && x < size && y >= 0 && y < size) {
                                switch (src[x][y]) {
                                    case '.':
                                        break;
                                    case '#':
                                        neighbourLumberyardCount++;
                                        break;
                                    case '|':
                                        neighbourWoodCount++;
                                        break;
                                    default:
                                        throw new IllegalArgumentException(src[x][y] + "");
                                }
                            }
                        }
                    }
                }

                switch (src[i][j]) {
                    case '.':
                        if (neighbourWoodCount >= 3) {
                            dest[i][j] = '|';
                        } else {
                            dest[i][j] = '.';
                        }
                        break;
                    case '|':
                        if (neighbourLumberyardCount >= 3) {
                            dest[i][j] = '#';
                        } else {
                            dest[i][j] = '|';
                        }
                        break;
                    case '#':
                        if (neighbourLumberyardCount >= 1 && neighbourWoodCount >= 1) {
                            dest[i][j] = '#';
                        } else {
                            dest[i][j] = '.';
                        }
                        break;
                    default:
                        throw new IllegalArgumentException(src[i][j] + "");
                }
            }
        }
    }

    static String testInput =
            ".#.#...|#.\n" +
                    ".....#|##|\n" +
                    ".|..|...#.\n" +
                    "..|#.....#\n" +
                    "#.#|||#|#|\n" +
                    "...#.||...\n" +
                    ".|....|...\n" +
                    "||...#|.#|\n" +
                    "|.||||..|.\n" +
                    "...#.|..|.";


    private static String input =
            ".#||#|||##....|..#......|..#...##..|#....#|.......\n" +
                    "|..#..#....|.#|.|......||.|..#|...||#......|.....|\n" +
                    "..#|##.#.#.##...#..........#.#|...||.|..|##.#.|.||\n" +
                    "|.#.#|#.#.||...|...|||#|.#..#|..|#.#..##.|......#|\n" +
                    "#..|#|........|......##.|##..|..#|...#||.......|#.\n" +
                    "#...|#..#......##...##.|......|.#|#.|..|#.|#...|.#\n" +
                    "|#.....|.|.###..#...|....|..|.....|#..#..|.......#\n" +
                    ".....##.|........|...#...|#..|..##...|......||.|..\n" +
                    "#....#..|..#.........||.##..##|#.##.#....|...#.|.|\n" +
                    "...|..#.|.|#||..|#.||.....#|.#|.|#|.....#|#.###|##\n" +
                    "...|..#.||....||.#.|....|#...#|.||#.#..#...#...##.\n" +
                    "...||.|#......|...|#...#..|...||..|.#|.....##.|||.\n" +
                    "...|.#|.|#.|...#.....|.|...#|.|.........|||.|.##.|\n" +
                    "..|..|#..|........#.|#.||.#|..#.|....||...|.|.#...\n" +
                    ".|.|...#.|.#..........|..|........#|.|....|..|....\n" +
                    "|...|.#..|..||#.||#........|...|.|.|..|.#|..|...|.\n" +
                    "..#.#..#|......#|.#....###...#.#..|..|.....|....#.\n" +
                    "..|||..#...|#|.##..#|#.#.#..|......#.....||.##.##.\n" +
                    "...|...#.|##..|..|.|.#.|||#|......|.|..|.||#.#..||\n" +
                    "||.....|..#|.#...|.|.#.||.....##...|.#...|#.#.##..\n" +
                    ".|.|.#|..#........#..||.|.#|...###|.#..#........||\n" +
                    "|.##......|.|||..|...##.|.....#|||....#...#||||.|.\n" +
                    "...#...|||.......#..|.#.||.|.......|#|..|..#.|....\n" +
                    "|..|#.............|...##|....|.#|..|#...|#...|.|..\n" +
                    "|.|....|#...|##...#.....|..|..|...||#..|...|.#..||\n" +
                    "...|.##.##....#.|#......##|...|..#.#....||||.||||.\n" +
                    "||.#....#..#...|.||||##.....#..##......#..||##.#..\n" +
                    "........#....|..#..#|#|....#..|..#.....##|...#.|..\n" +
                    "..#.|#.|.#.#..#.....|..#...###....|#...........#.|\n" +
                    "#.|#|.#...|.#.#.|..|....|..|.|.#|.#|#.............\n" +
                    ".||......|||||...||.#......|#...|#.|.|..#.|.#|....\n" +
                    "|.#|.#.|#.#..#.##......#.#|#.....#..#....#.##|.#..\n" +
                    "#.#..|....###..|..|.||..|#..|...|...#|##....|#.#..\n" +
                    ".|#...|..#|..#.|||.|..||...|..#.#...|..|#......#..\n" +
                    ".##...#||..|#.#...|.......|.##.....|..|.#..|.#.|.#\n" +
                    "#||##....#.|.||.#....|.#|..|.|.#....#..#...||.....\n" +
                    "......||.#|........#....||.##...#....|.||...|..##|\n" +
                    "#........|..#|.......#.#.#|..|...#..||||...|.....#\n" +
                    "....#||.##....|..##...##|.....|..#.#.....#..|.....\n" +
                    ".|.|#....|..|.#|#....#..|...|..#|#...#.||...#.#...\n" +
                    "#....|.|#||....#.#|#|.#..|.#.....##........|...|#.\n" +
                    "#...#...|..|.#....|..|.#....#.|#...#...#|.|.#.....\n" +
                    "....#.......#....##|.#.|....##..|||##.....#|.....#\n" +
                    ".....||||..|.#|#..|...|.#..|...#|...|.##||.#||....\n" +
                    ".||....#...|..#.|#.#.|#|#|..#.........##...||..|#.\n" +
                    "...|.#.#..........##...#|...|.##.|.|.||.#......#..\n" +
                    "...###.#..|..#.....#|#.#.|#.######|.|#.....###.|.#\n" +
                    "..##.....##...|..|....#|..||....|.|....#..|...|..#\n" +
                    ".|.##.#...|.|.||.||.|#.#.....||#.#|.#|.|..#|.#..|#\n" +
                    ".|...............#.#..#.##......#|||.|..||..#....#";
}

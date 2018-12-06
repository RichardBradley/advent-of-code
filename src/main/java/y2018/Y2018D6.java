package y2018;

import java.awt.Point;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;
import static java.util.stream.Collectors.toList;

public class Y2018D6 {
    public static void main(String[] args) throws Exception {

        // 1
        assertThat(largestArea(parse(
                "1, 1",
                "1, 6",
                "8, 3",
                "3, 4",
                "5, 5",
                "8, 9"))).isEqualTo(17);

        System.out.println(largestArea(input));

        // 2

        // What is the size of the region containing all locations which have a total distance to all given coordinates of less than 10000?
        assertThat(sizeSafeArea(32, parse(
                "1, 1",
                "1, 6",
                "8, 3",
                "3, 4",
                "5, 5",
                "8, 9"))).isEqualTo(16);

        System.out.println(sizeSafeArea(10000, input));

    }

    private static int sizeSafeArea(int maxDist, List<Point> coords) {
        int countSafe = 0;

        int gridSize = 500;
        for (int x = 0; x < gridSize; x++) {
            for (int y = 0; y < gridSize; y++) {
                int dist = 0;
                for (Point coord : coords) {
                    dist += Math.abs(x - coord.x) + Math.abs(y - coord.y);
                }
                if (dist < maxDist) {
                    countSafe++;
                }
            }
        }

        return countSafe;
    }

    private static int largestArea(List<Point> coords) {

        int size = 500;
        char[][] grid = rect(size);

        for (int i = 0; i < coords.size(); i++) {
            Point coord = coords.get(i);
            grid[coord.x][coord.y] = (char) ('a' + i);
        }

        // Flood fill:
        grid = floodFill(grid);

//        prettyPrint(grid);

        // count areas, skip infinite
        int[] countsByIdx = new int[coords.size()];
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                char c = grid[x][y];
                if (c != '\0') {
                    int idx = c - 'a';

                    if (x == 0 || x == size - 1 || y == 0 || y == size - 1) {
                        // edge:
                        countsByIdx[idx] = Integer.MIN_VALUE;
                    } else {
                        countsByIdx[idx]++;
                    }
                }
            }
        }

        return Arrays.stream(countsByIdx).max().getAsInt();
    }

    private static char[][] rect(int size) {
        char[][] acc = new char[size][];
        for (int i = 0; i < size; i++) {
            acc[i] = new char[size];
        }
        return acc;
    }

    private static void prettyPrint(char[][] grid) {
        StringBuilder acc = new StringBuilder();
        int size = grid.length;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                acc.append(grid[x][y] == '\0' ? '.' : grid[x][y]);
            }
            acc.append('\n');
        }
        System.out.println(acc);
    }

    private static char[][] floodFill(char[][] grid) {
        int size = grid.length;
        int i = 0;
        boolean changes;
        do {
            System.out.println("flood fill loop " + (++i));

            char[][] nextGrid = rect(size);

            changes = false;
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    char c = grid[x][y];
                    if (c != '\0') {
                        // already filled
                        nextGrid[x][y] = c;
                    } else {

                        char neighbour = '\0';
                        boolean hasTwoNeighbours = false;

                        if (x > 0) {
                            char n = grid[x - 1][y];
                            if (n != '\0') {
                                if (neighbour != '\0' && neighbour != n) {
                                    hasTwoNeighbours = true;
                                }
                                neighbour = n;
                            }
                        }

                        if (x < size - 1) {
                            char n = grid[x + 1][y];
                            if (n != '\0') {
                                if (neighbour != '\0' && neighbour != n) {
                                    hasTwoNeighbours = true;
                                }
                                neighbour = n;
                            }
                        }

                        if (y > 0) {
                            char n = grid[x][y - 1];
                            if (n != '\0') {
                                if (neighbour != '\0' && neighbour != n) {
                                    hasTwoNeighbours = true;
                                }
                                neighbour = n;
                            }
                        }

                        if (y < size - 1) {
                            char n = grid[x][y + 1];
                            if (n != '\0') {
                                if (neighbour != '\0' && neighbour != n) {
                                    hasTwoNeighbours = true;
                                }
                                neighbour = n;
                            }
                        }

                        if (!hasTwoNeighbours && neighbour != '\0') {
                            nextGrid[x][y] = neighbour;
                            changes = true;
                        }
                    }
                }
            }

            grid = nextGrid;
        } while (changes);

        return grid;
    }

    static List<Point> input = parse(new String[]{
            "350, 353",
            "238, 298",
            "248, 152",
            "168, 189",
            "127, 155",
            "339, 202",
            "304, 104",
            "317, 144",
            "83, 106",
            "78, 106",
            "170, 230",
            "115, 194",
            "350, 272",
            "159, 69",
            "197, 197",
            "190, 288",
            "227, 215",
            "228, 124",
            "131, 238",
            "154, 323",
            "54, 185",
            "133, 75",
            "242, 184",
            "113, 273",
            "65, 245",
            "221, 66",
            "148, 82",
            "131, 351",
            "97, 272",
            "72, 93",
            "203, 116",
            "209, 295",
            "133, 115",
            "355, 304",
            "298, 312",
            "251, 58",
            "81, 244",
            "138, 115",
            "302, 341",
            "286, 103",
            "111, 95",
            "148, 194",
            "235, 262",
            "41, 129",
            "270, 275",
            "234, 117",
            "273, 257",
            "98, 196",
            "176, 122",
            "121, 258",
    });

    private static List<Point> parse(String... coords) {
        return Arrays.stream(coords)
                .map(c -> {
                    String[] x = c.split(", ");
                    checkState(x.length == 2);
                    return new Point(Integer.parseInt(x[0]), Integer.parseInt(x[1]));
                })
                .collect(toList());
    }
}

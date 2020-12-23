package y2020;

import com.google.common.base.Stopwatch;
import lombok.Value;

import java.util.concurrent.TimeUnit;


public class Y2020D17 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        countActiveAfterSteps3d(parse3d(example), 6);
        countActiveAfterSteps3d(parse3d(input), 6);

        countActiveAfterSteps4d(parse4d(example), 6);
        countActiveAfterSteps4d(parse4d(input), 6);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static boolean[][][] parse3d(String input) {
        String[] lines = input.split("\n");
        int height = lines.length;
        int gridSize = height + 24;
        int offset = gridSize / 2;
        boolean[][][] acc = new boolean[gridSize][gridSize][gridSize];
        for (int y = 0; y < lines.length; y++) {
            String line = lines[y];
            for (int x = 0; x < line.length(); x++) {
                if (line.charAt(x) == '#') {
                    acc[x + offset][y + offset][offset] = true;
                }
            }
        }
        return acc;
    }

    private static void countActiveAfterSteps3d(boolean[][][] state, int stepCount) {
        int gridSize = state.length;
        boolean[][][] next = new boolean[gridSize][gridSize][gridSize];
        for (int step = 0; step < stepCount; step++) {
            //print(step, state);

            for (int x = 1; x < gridSize - 1; x++) {
                for (int y = 1; y < gridSize - 1; y++) {
                    for (int z = 1; z < gridSize - 1; z++) {
                        int activeNeighbourCount = 0;
                        for (int dx = -1; dx <= 1; dx++) {
                            for (int dy = -1; dy <= 1; dy++) {
                                for (int dz = -1; dz <= 1; dz++) {
                                    if (!(dx == 0 && dy == 0 && dz == 0)) {
                                        if (state[x + dx][y + dy][z + dz]) {
                                            activeNeighbourCount++;
                                        }
                                    }
                                }
                            }
                        }

                        boolean nextVal;
                        if (state[x][y][z]) {
                            nextVal = (activeNeighbourCount == 2 || activeNeighbourCount == 3);
                        } else {
                            nextVal = activeNeighbourCount == 3;
                        }
                        next[x][y][z] = nextVal;
                    }
                }
            }

            boolean[][][] tmp = state;
            state = next;
            next = tmp;
        }

        // How many cubes are left in the active state after the sixth cycle?
        int count = 0;
        for (boolean[][] plane : state) {
            for (boolean[] row : plane) {
                for (boolean cell : row) {
                    if (cell) {
                        count++;
                    }
                }
            }
        }

        System.out.println("count = " + count);
    }


    private static boolean[][][][] parse4d(String input) {
        String[] lines = input.split("\n");
        int height = lines.length;
        int gridSize = height + 100;
        int offset = gridSize / 2;
        boolean[][][][] acc = new boolean[gridSize][gridSize][gridSize][gridSize];
        for (int y = 0; y < lines.length; y++) {
            String line = lines[y];
            for (int x = 0; x < line.length(); x++) {
                if (line.charAt(x) == '#') {
                    acc[x + offset][y + offset][offset][offset] = true;
                }
            }
        }
        return acc;
    }

    private static void countActiveAfterSteps4d(boolean[][][][] state, int stepCount) {
        int gridSize = state.length;
        boolean[][][][] next = new boolean[gridSize][gridSize][gridSize][gridSize];
        for (int step = 0; step < stepCount; step++) {
            //print(step, state);

            for (int x = 1; x < gridSize - 1; x++) {
                for (int y = 1; y < gridSize - 1; y++) {
                    for (int z = 1; z < gridSize - 1; z++) {
                        for (int t = 1; t < gridSize - 1; t++) {

                            int activeNeighbourCount = 0;
                            for (int dx = -1; dx <= 1; dx++) {
                                for (int dy = -1; dy <= 1; dy++) {
                                    for (int dz = -1; dz <= 1; dz++) {
                                        for (int dt = -1; dt <= 1; dt++) {
                                            if (!(dx == 0 && dy == 0 && dz == 0 && dt == 0)) {
                                                if (state[x + dx][y + dy][z + dz][t + dt]) {
                                                    activeNeighbourCount++;
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            boolean nextVal;
                            if (state[x][y][z][t]) {
                                nextVal = (activeNeighbourCount == 2 || activeNeighbourCount == 3);
                            } else {
                                nextVal = activeNeighbourCount == 3;
                            }
                            next[x][y][z][t] = nextVal;
                        }
                    }
                }
            }

            boolean[][][][] tmp = state;
            state = next;
            next = tmp;
        }

        // How many cubes are left in the active state after the sixth cycle?
        int count = 0;
        for (boolean[][][] hplane : state) {
            for (boolean[][] plane : hplane) {
                for (boolean[] row : plane) {
                    for (boolean cell : row) {
                        if (cell) {
                            count++;
                        }
                    }
                }
            }
        }

        System.out.println("count = " + count);
    }

    private static void print(int step, boolean[][][] state) {
        System.out.println("######## step = " + step);
        int gridSize = state.length;
        for (int z = 0; z < gridSize; z++) {
            boolean nonEmpty = false;
            StringBuffer plane = new StringBuffer();
            for (int y = 0; y < gridSize; y++) {
                for (int x = 0; x < gridSize; x++) {
                    if (state[x][y][z]) {
                        plane.append('#');
                        nonEmpty = true;
                    } else {
                        plane.append('.');
                    }
                }
                plane.append('\n');
            }
            if (nonEmpty) {
                System.out.println("#### z = " + z);
                System.out.println(plane);
            }
        }
    }

    @Value
    static class Point3 {
        int x;
        int y;
        int z;
    }

    static String example = ".#.\n" +
            "..#\n" +
            "###";

    static String input = "####...#\n" +
            "......##\n" +
            "####..##\n" +
            "##......\n" +
            "..##.##.\n" +
            "#.##...#\n" +
            "....##.#\n" +
            ".##.#.#.";
}

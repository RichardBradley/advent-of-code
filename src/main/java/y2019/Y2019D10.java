package y2019;

import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import lombok.Value;

import java.awt.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;

public class Y2019D10 {

    static boolean LOG = false;

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        angleTests();

        // 1
        assertThat(bestStationLocFor(example1)).isEqualTo("3,4 8");
        System.out.println("example ok");
        System.out.println(bestStationLocFor(input)); // 25,31 329

        // 2
        System.out.println("part 2");
        // System.out.println(printNthToVaporise(example21, 20, 8, 3));
        // System.out.println(printNthToVaporise(example22, 200, 11, 13));
        System.out.println(printNthToVaporise(input, 200, 25, 31)); // 512

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static void angleTests() {
        assertThat(getAngle(0, -3)).isEqualTo(0.0);
        assertThat(getAngle(1, -1)).isEqualTo(Math.PI / 4);
        assertThat(getAngle(1, 0)).isEqualTo(Math.PI / 2);
        assertThat(getAngle(1, 1)).isEqualTo(3 * Math.PI / 4);
        assertThat(getAngle(0, 1)).isEqualTo(Math.PI);
        assertThat(getAngle(-1, 1)).isEqualTo(5 * Math.PI / 4);
        assertThat(getAngle(-1, 0)).isEqualTo(6 * Math.PI / 4);
        assertThat(getAngle(-1, -1)).isEqualTo(7 * Math.PI / 4);

        Double a1 = getAngle(3, 5);
        Double a2 = getAngle(6, 10);
        assertThat(a1.equals(a2)).isTrue();
        assertThat(a1.hashCode()).isEqualTo(a2.hashCode());
    }

    private static String bestStationLocFor(String input) {
        boolean[][] field = parse(input);
        int height = field.length;
        int width = field[0].length;
        int maxVisible = Integer.MIN_VALUE;
        String maxVisibleLoc = "";

        StringBuilder[] mapView = new StringBuilder[height];
        for (int i = 0; i < height; i++) {
            mapView[i] = new StringBuilder();
            mapView[i].append(Strings.repeat(".", width));
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (field[y][x]) {
                    int visibleFrom = countVisibleFrom(field, x, y);
                    mapView[y].setCharAt(x, (char) ('0' + visibleFrom));
                    if (visibleFrom > maxVisible) {
                        maxVisible = visibleFrom;
                        maxVisibleLoc = x + "," + y;
                    }
                }
            }
        }

        if (LOG) {
            System.out.println(Arrays.stream(mapView).collect(Collectors.joining("\n")));
        }
        return maxVisibleLoc + " " + maxVisible;
    }

    private static String printNthToVaporise(String input, int nth, int stationX, int stationY) {
        boolean[][] field = parse(input);
        SortedMap<Double, SortedSet<Asteroid>> asteroidsByAngle = getAsteroidsByAngle(stationX, stationY, field);

        int count = 0;
        while (true) {
            for (Map.Entry<Double, SortedSet<Asteroid>> angleAsters : asteroidsByAngle.entrySet()) {
                SortedSet<Asteroid> asters = angleAsters.getValue();
                if (!asters.isEmpty()) {
                    Asteroid first = asters.first();
                    asters.remove(first);
                    if (++count == nth) {
                        return String.format("%sth is %s, output %s", count, first,
                                100 * first.loc.x + first.loc.y);
                    }
                    printf("%s Vaporized %s\n", count, first);
                }
            }
        }
    }

    private static SortedMap<Double, SortedSet<Asteroid>> getAsteroidsByAngle(int stationX, int stationY, boolean[][] field) {
        SortedMap<Double, SortedSet<Asteroid>> asteroidsByAngle = new TreeMap<>();
        int height = field.length;
        int width = field[0].length;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (field[y][x]) {
                    if (!(x == stationX && y == stationY)) {
                        int dx = x - stationX;
                        int dy = y - stationY;
                        Asteroid asteroid = new Asteroid(
                                new Point(x, y),
                                Math.sqrt(dx * dx + dy * dy));
                        double angle = getAngle(dx, dy);
                        asteroidsByAngle.compute(angle, (k, v) -> {
                            if (v == null) {
                                v = new TreeSet<>();
                            }
                            v.add(asteroid);
                            return v;
                        });
                    }
                }
            }
        }
        return asteroidsByAngle;
    }

    private static double getAngle(int dx, int dy) {
        double angle = Math.atan2(dx, -dy);
        if (angle < 0) {
            angle += 2 * Math.PI;
        }
        return angle;
    }

    @Value
    private static class Asteroid implements Comparable<Asteroid> {
        Point loc;
        double dist;

        @Override
        public int compareTo(Asteroid that) {
            return Double.compare(this.dist, that.dist);
        }
    }

    private static void printf(String fmt, Object... args) {
        if (LOG) {
            System.out.printf(fmt, args);
        }
    }

    private static int countVisibleFrom(boolean[][] field, int startX, int startY) {
        SortedMap<Double, SortedSet<Asteroid>> asteroidsByAngle = getAsteroidsByAngle(startX, startY, field);
        return asteroidsByAngle.size();
    }

    private static boolean[][] parse(String input) {
        String[] lines = input.split("\n");
        int height = lines.length;
        int width = lines[0].length();
        boolean[][] acc = new boolean[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                acc[y][x] = lines[y].charAt(x) == '#';
            }
        }
        return acc;
    }

    static String example1 = ".#..#\n" +
            ".....\n" +
            "#####\n" +
            "....#\n" +
            "...##";

    static String example21 = ".#....#####...#..\n" +
            "##...##.#####..##\n" +
            "##...#...#.#####.\n" +
            "..#.....X...###..\n" +
            "..#.#.....#....##";

    static String example22 = ".#..##.###...#######\n" +
            "##.############..##.\n" +
            ".#.######.########.#\n" +
            ".###.#######.####.#.\n" +
            "#####.##.#.##.###.##\n" +
            "..#####..#.#########\n" +
            "####################\n" +
            "#.####....###.#.#.##\n" +
            "##.#################\n" +
            "#####.##.###..####..\n" +
            "..######..##.#######\n" +
            "####.##.####...##..#\n" +
            ".#####..#.######.###\n" +
            "##...#.##########...\n" +
            "#.##########.#######\n" +
            ".####.#.###.###.#.##\n" +
            "....##.##.###..#####\n" +
            ".#.#.###########.###\n" +
            "#.#.#.#####.####.###\n" +
            "###.##.####.##.#..##";

    static String input = "....#...####.#.#...........#........\n" +
            "#####..#.#.#......#####...#.#...#...\n" +
            "##.##..#.#.#.....#.....##.#.#..#....\n" +
            "...#..#...#.##........#..#.......#.#\n" +
            "#...##...###...###..#...#.....#.....\n" +
            "##.......#.....#.........#.#....#.#.\n" +
            "..#...#.##.##.....#....##..#......#.\n" +
            "..###..##..#..#...#......##...#....#\n" +
            "##..##.....#...#.#...#......#.#.#..#\n" +
            "...###....#..#.#......#...#.......#.\n" +
            "#....#...##.......#..#.......#..#...\n" +
            "#...........#.....#.....#.#...#.##.#\n" +
            "###..#....####..#.###...#....#..#...\n" +
            "##....#.#..#.#......##.......#....#.\n" +
            "..#.#....#.#.#..#...#.##.##..#......\n" +
            "...#.....#......#.#.#.##.....#..###.\n" +
            "..#.#.###.......#..#.#....##.....#..\n" +
            ".#.#.#...#..#.#..##.#..........#...#\n" +
            ".....#.#.#...#..#..#...###.#...#.#..\n" +
            "#..#..#.....#.##..##...##.#.....#...\n" +
            "....##....#.##...#..........#.##....\n" +
            "...#....###.#...##........##.##..##.\n" +
            "#..#....#......#......###...........\n" +
            "##...#..#.##.##..##....#..#..##..#.#\n" +
            ".#....#..##.....#.#............##...\n" +
            ".###.........#....#.##.#..#.#..#.#..\n" +
            "#...#..#...#.#.#.....#....#......###\n" +
            "#...........##.#....#.##......#.#..#\n" +
            "....#...#..#...#.####...#.#..#.##...\n" +
            "......####.....#..#....#....#....#.#\n" +
            ".##.#..###..####...#.......#.#....#.\n" +
            "#.###....#....#..........#.....###.#\n" +
            "...#......#....##...##..#..#...###..\n" +
            "..#...###.###.........#.#..#.#..#...\n" +
            ".#.#.............#.#....#...........\n" +
            "..#...#.###...##....##.#.#.#....#.#.";
}

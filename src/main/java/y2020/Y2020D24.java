package y2020;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import javafx.geometry.Point3D;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.truth.Truth.assertThat;

public class Y2020D24 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        String input = Resources.toString(Resources.getResource("y2020/Y2020D24.txt"), StandardCharsets.UTF_8);

        assertThat(count(part1(example))).isEqualTo(10);
        assertThat(count(part1(input))).isEqualTo(427);

        assertThat(part2(example, 100)).isEqualTo(2208);
        assertThat(part2(input, 100)).isEqualTo(3837);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    // https://www.redblobgames.com/grids/hexagons/#neighbors-cube
    static Map<String, Point3D> dirs = ImmutableMap.<String, Point3D>builder()
            .put("e", new Point3D(1, 0, -1))
            .put("se", new Point3D(0, 1, -1))
            .put("sw", new Point3D(-1, 1, 0))
            .put("w", new Point3D(-1, 0, 1))
            .put("nw", new Point3D(0, -1, 1))
            .put("ne", new Point3D(1, -1, 0))
            .build();

    private static long part2(String input, int stepCount) {
        Map<Point3D, Boolean> state = part1(input);

        for (int i = 0; i < stepCount; i++) {
            Map<Point3D, Integer> neighbourCounts = new HashMap<>();
            for (Map.Entry<Point3D, Boolean> entry : state.entrySet()) {
                if (entry.getValue()) {
                    for (Point3D dir : dirs.values()) {
                        neighbourCounts.merge(entry.getKey().add(dir), 1, (a, b) -> a + b);
                    }
                }
            }

            Map<Point3D, Boolean> next = new HashMap<>();
            for (Map.Entry<Point3D, Integer> neighbourEntry : neighbourCounts.entrySet()) {
                Point3D point = neighbourEntry.getKey();
                boolean wasBlack = state.getOrDefault(point, false);
                Integer neighbourCount = neighbourEntry.getValue();
                if (wasBlack) {
                    // Any black tile with zero or more than 2 black tiles immediately adjacent to it is flipped to white.
                    if (neighbourCount == 1 || neighbourCount == 2) {
                        next.put(point, true);
                    }
                } else {
                    // Any white tile with exactly 2 black tiles immediately adjacent to it is flipped to black.
                    if (neighbourCount == 2) {
                        next.put(point, true);
                    }
                }
            }

            state = next;
        }

        return count(state);
    }

    private static Map<Point3D, Boolean> part1(String input) {
        Pattern p = Pattern.compile("se|sw|nw|ne|e|w");
        Map<Point3D, Boolean> isBlack = new HashMap<>();
        for (String line : input.split("\n")) {
            Point3D acc = new Point3D(0, 0, 0);
            Matcher m = p.matcher(line);
            while (m.find()) {
                Point3D dir = dirs.get(m.group());
                acc = acc.add(dir);
            }
            isBlack.merge(acc, true, (a, b) -> a ^ b);
        }

        return isBlack;
    }

    private static long count(Map<Point3D, Boolean> isBlack) {
        return isBlack.values().stream().filter(n -> n).count();
    }

    private static void decrementLoops(int[] counts, List<String> dirs, String d1, String d2, String d3) {
        int i1 = dirs.indexOf(d1);
        int i2 = dirs.indexOf(d2);
        int i3 = dirs.indexOf(d3);
        int loops = Math.min(Math.min(counts[i1], counts[i2]), counts[i3]);
        counts[i1] -= loops;
        counts[i2] -= loops;
        counts[i3] -= loops;
    }

    private static void decrementLoops(int[] counts, List<String> dirs, String d1, String d2) {
        int i1 = dirs.indexOf(d1);
        int i2 = dirs.indexOf(d2);
        int loops = Math.min(counts[i1], counts[i2]);
        counts[i1] -= loops;
        counts[i2] -= loops;
    }

    static String example = "sesenwnenenewseeswwswswwnenewsewsw\n" +
            "neeenesenwnwwswnenewnwwsewnenwseswesw\n" +
            "seswneswswsenwwnwse\n" +
            "nwnwneseeswswnenewneswwnewseswneseene\n" +
            "swweswneswnenwsewnwneneseenw\n" +
            "eesenwseswswnenwswnwnwsewwnwsene\n" +
            "sewnenenenesenwsewnenwwwse\n" +
            "wenwwweseeeweswwwnwwe\n" +
            "wsweesenenewnwwnwsenewsenwwsesesenwne\n" +
            "neeswseenwwswnwswswnw\n" +
            "nenwswwsewswnenenewsenwsenwnesesenew\n" +
            "enewnwewneswsewnwswenweswnenwsenwsw\n" +
            "sweneswneswneneenwnewenewwneswswnese\n" +
            "swwesenesewenwneswnwwneseswwne\n" +
            "enesenwswwswneneswsenwnewswseenwsese\n" +
            "wnwnesenesenenwwnenwsewesewsesesew\n" +
            "nenewswnwewswnenesenwnesewesw\n" +
            "eneswnwswnwsenenwnwnwwseeswneewsenese\n" +
            "neswnwewnwnwseenwseesewsenwsweewe\n" +
            "wseweeenwnesenwwwswnew";
}

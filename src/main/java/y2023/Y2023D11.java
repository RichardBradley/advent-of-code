package y2023;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;

public class Y2023D11 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            List<String> input = Resources.readLines(Resources.getResource("y2023/Y2023D11.txt"), StandardCharsets.UTF_8);

            // 1
            assertThat(part1(input)).isEqualTo(9957702);

            // 2
            // 512241445470 high
            assertThat(part2(input, 999999)).isEqualTo(512240933238L);

        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private static long part1(List<String> input) {
        // rows and cols with no galaxies expand
        List<StringBuilder> map = input.stream().map(s -> new StringBuilder(s)).collect(Collectors.toList());

        for (int y = 0; y < map.size(); y++) {
            if (map.get(y).indexOf("#") == -1) {
                map.add(y, new StringBuilder(map.get(y)));
                y++;
            }
        }
        for (int x = 0; x < map.get(0).length(); x++) {
            boolean noGalInCol = true;
            for (int y = 0; y < map.size(); y++) {
                if ('#' == map.get(y).charAt(x)) {
                    noGalInCol = false;
                    break;
                }
            }
            if (noGalInCol) {
                for (int y = 0; y < map.size(); y++) {
                    map.get(y).insert(x, '.');
                }
                x++;
            }
        }

        // find the length of the shortest path between every pair of galaxies.
        // What is the sum of these lengths?
        List<Point> galaxies = new ArrayList<>();
        for (int y = 0; y < map.size(); y++) {
            StringBuilder row = map.get(y);
            for (int x = 0; x < row.length(); x++) {
                if ('#' == row.charAt(x)) {
                    galaxies.add(new Point(x, y));
                }
            }
        }

        long sum = 0;
        for (int i = 0; i < galaxies.size(); i++) {
            Point gi = galaxies.get(i);
            for (int j = i + 1; j < galaxies.size(); j++) {
                Point gj = galaxies.get(j);
                sum += Math.abs(gi.x - gj.x) + Math.abs(gi.y - gj.y);
            }
        }
        return sum;
    }

    private static long part2(List<String> input, int growDist) {
        List<Point> galaxies = new ArrayList<>();
        for (int y = 0; y < input.size(); y++) {
            String row = input.get(y);
            for (int x = 0; x < row.length(); x++) {
                if ('#' == row.charAt(x)) {
                    galaxies.add(new Point(x, y));
                }
            }
        }

        // rows and cols with no galaxies expand by 1M
        for (int y = input.size() - 1; y >= 0; y--) {
            if (input.get(y).indexOf("#") == -1) {
                // all galaxies with g.y > y
                for (int i = 0; i < galaxies.size(); i++) {
                    Point g = galaxies.get(i);
                    if (g.y > y) {
                        galaxies.set(i, new Point(g.x, g.y + growDist));
                    }
                }
            }
        }
        for (int x = input.get(0).length() - 1; x >= 0; x--) {
            boolean noGalInCol = true;
            for (int y = 0; y < input.size(); y++) {
                if ('#' == input.get(y).charAt(x)) {
                    noGalInCol = false;
                    break;
                }
            }
            if (noGalInCol) {
                for (int i = 0; i < galaxies.size(); i++) {
                    Point g = galaxies.get(i);
                    if (g.x > x) {
                        galaxies.set(i, new Point(g.x + growDist, g.y));
                    }
                }
            }
        }

        // find the length of the shortest path between every pair of galaxies.
        // What is the sum of these lengths?
        long sum = 0;
        for (int i = 0; i < galaxies.size(); i++) {
            Point gi = galaxies.get(i);
            for (int j = i + 1; j < galaxies.size(); j++) {
                Point gj = galaxies.get(j);
                sum += Math.abs(gi.x - gj.x) + Math.abs(gi.y - gj.y);
            }
        }
        return sum;
    }
}

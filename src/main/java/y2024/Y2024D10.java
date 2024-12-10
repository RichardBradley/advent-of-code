package y2024;

import com.google.common.base.Stopwatch;

import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static aoc.Common.loadInputFromResources;
import static com.google.common.truth.Truth.assertThat;

public class Y2024D10 {

    public static void main(String[] args) throws Exception {
        List<String> input = loadInputFromResources();
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(part1(example)).isEqualTo(36);
        assertThat(part1(input)).isEqualTo(430);

        // 2
        assertThat(part2(example)).isEqualTo(81);
        assertThat(part2(input)).isEqualTo(928);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static long part1(List<String> input) {
        int scoreSum = 0;
        int width = input.get(0).length();
        for (int y = 0; y < input.size(); y++) {
            String line = input.get(y);
            for (int x = 0; x < width; x++) {
                char c = line.charAt(x);
                if ('0' == c) {
                    Set<Point> visited = new HashSet<>();
                    scoreSum += scoreTrailHead(input, new Point(x, y), visited);
                }
            }
        }
        return scoreSum;
    }

    private static int scoreTrailHead(List<String> input, Point p, Set<Point> visited) {
        if (visited.add(p)) {
            char c = get(input, p);
            if (c == '9') {
                return 1;
            }
            int score = 0;
            for (Point dir : dirs) {
                Point next = add(p, dir);
                char nextC = get(input, next);
                if (nextC == (c + 1)) {
                    score += scoreTrailHead(input, next, visited);
                }
            }
            return score;
        } else {
            return 0;
        }
    }

    private static Point add(Point a, Point b) {
        return new Point(a.x + b.x, a.y + b.y);
    }

    static Point[] dirs = new Point[]{
            new Point(0, -1),
            new Point(1, 0),
            new Point(0, 1),
            new Point(-1, 0)
    };

    private static char get(List<String> input, Point p) {
        if (p.y < 0 || p.y >= input.size()) {
            return '\0';
        }
        String line = input.get(p.y);
        if (p.x < 0 || p.x >= line.length()) {
            return '\0';
        }
        return line.charAt(p.x);
    }

    private static long part2(List<String> input) {
        int scoreSum = 0;
        int width = input.get(0).length();
        for (int y = 0; y < input.size(); y++) {
            String line = input.get(y);
            for (int x = 0; x < width; x++) {
                char c = line.charAt(x);
                if ('0' == c) {
                    scoreSum += scoreTrailHead2(input, new Point(x, y), (char) ('0' - 1));
                }
            }
        }
        return scoreSum;
    }

    private static int scoreTrailHead2(List<String> input, Point p, char prevC) {
        char c = get(input, p);
        if (c == prevC + 1) {
            if (c == '9') {
                return 1;
            }
            int score = 0;
            for (Point dir : dirs) {
                Point next = add(p, dir);
                score += scoreTrailHead2(input, next, c);
            }
            return score;
        } else {
            return 0;
        }
    }

    static List<String> example = List.of(
            "89010123",
            "78121874",
            "87430965",
            "96549874",
            "45678903",
            "32019012",
            "01329801",
            "10456732");
}

package y2021;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

public class Y2021D20 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            List<String> input = Resources.readLines(Resources.getResource("y2021/Y2021D20.txt"), StandardCharsets.UTF_8);

            // 1
            assertThat(enhance(example, 2)).isEqualTo(35);
            assertThat(enhance(input, 2)).isEqualTo(5249);

            // 2
            assertThat(enhance(example, 50)).isEqualTo(3351);
            assertThat(enhance(input, 50)).isEqualTo(15714);

        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private static long enhance(List<String> input, int steps) {
        String imageEnhancement = input.get(0);
        assertThat(imageEnhancement.length()).isEqualTo(512);
        assertThat(input.get(1)).isEmpty();
        Map<Point, Character> image = new HashMap<>();
        char defaultOutsideImage = '.';
        for (int y = 2; y < input.size(); y++) {
            String line = input.get(y);
            for (int x = 0; x < line.length(); x++) {
                image.put(new Point(x, y), line.charAt(x));
            }
        }

        for (int step = 1; step <= steps; step++) {
            int minY = image.keySet().stream().mapToInt(p -> p.y).min().getAsInt();
            int maxY = image.keySet().stream().mapToInt(p -> p.y).max().getAsInt();
            int minX = image.keySet().stream().mapToInt(p -> p.x).min().getAsInt();
            int maxX = image.keySet().stream().mapToInt(p -> p.x).max().getAsInt();
            Map<Point, Character> next = new HashMap<>();

            for (int x = minX - 1; x <= maxX + 1; x++) {
                for (int y = minY - 1; y <= maxY + 1; y++) {
                    StringBuilder digits = new StringBuilder();
                    digits
                            .append(getAsDigit(image, defaultOutsideImage, x - 1, y - 1))
                            .append(getAsDigit(image, defaultOutsideImage, x, y - 1))
                            .append(getAsDigit(image, defaultOutsideImage, x + 1, y - 1))
                            .append(getAsDigit(image, defaultOutsideImage, x - 1, y))
                            .append(getAsDigit(image, defaultOutsideImage, x, y))
                            .append(getAsDigit(image, defaultOutsideImage, x + 1, y))
                            .append(getAsDigit(image, defaultOutsideImage, x - 1, y + 1))
                            .append(getAsDigit(image, defaultOutsideImage, x, y + 1))
                            .append(getAsDigit(image, defaultOutsideImage, x + 1, y + 1));
                    int n = Integer.parseInt(digits.toString(), 2);
                    char newPixel = imageEnhancement.charAt(n);
                    next.put(new Point(x, y), newPixel);
                }
            }

            image = next;
            defaultOutsideImage = (defaultOutsideImage == '#')
                    ? imageEnhancement.charAt(511)
                    : imageEnhancement.charAt(0);
        }

        return image.values().stream().filter(x -> x == '#').count();
    }

    private static char getAsDigit(Map<Point, Character> image, char defaultOutsideImage, int x, int y) {
        return image.getOrDefault(new Point(x, y), defaultOutsideImage) == '#' ? '1' : '0';
    }

    private static List<String> example = List.of(
            "..#.#..#####.#.#.#.###.##.....###.##.#..###.####..#####..#....#..#..##..##" +
                    "#..######.###...####..#..#####..##..#.#####...##.#.#..#.##..#.#......#.###" +
                    ".######.###.####...#.##.##..#..#..#####.....#.#....###..#.##......#.....#." +
                    ".#..#..##..#...##.######.####.####.#.#...#.......#..#.#.#...####.##.#....." +
                    ".#..#...##.#.##..#...##.#.##..###.#......#.#.......#.#.#.####.###.##...#.." +
                    "...####.#..#..#.##.#....##..#.####....##...##..#...#......#.#.......#....." +
                    "..##..####..#...#.#.#...##..#.#..###..#####........#..####......#..#",
            "",
            "#..#.",
            "#....",
            "##..#",
            "..#..",
            "..###"
    );
}

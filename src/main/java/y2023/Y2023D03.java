package y2023;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.io.Resources;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

public class Y2023D03 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        List<String> input = Resources.readLines(Resources.getResource("y2023/Y2023D03.txt"), StandardCharsets.UTF_8);

        // 1
        assertThat(part1(example)).isEqualTo(4361);
        assertThat(part1(input)).isEqualTo(544664);

        // 2
        assertThat(part2(example)).isEqualTo(467835);
        assertThat(part2(input)).isEqualTo(84495585);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static long part1(List<String> input) {
        // What is the sum of all of the part numbers in the engine schematic?
        long sum = 0;
        for (int y = 0; y < input.size(); y++) {
            String line = input.get(y);
            for (int x = 0; x < line.length(); x++) {
                if (Character.isDigit(line.charAt(x))) {
                    int xEnd = x + 1;
                    while (xEnd < line.length() && Character.isDigit(line.charAt(xEnd))) {
                        xEnd++;
                    }

                    // check neighbours for symbols
                    boolean touchesSymbol = false;
                    neighboursLoop:
                    for (int yy = y - 1; yy <= y + 1; yy++) {
                        for (int xx = x - 1; xx <= xEnd; xx++) {
                            if (yy >= 0 && yy < input.size()
                                    && xx >= 0 && xx < line.length()) {
                                char c = input.get(yy).charAt(xx);
                                if ('.' != c && !Character.isDigit(c)) {
                                    touchesSymbol = true;
                                    break neighboursLoop;
                                }
                            }
                        }
                    }

                    if (touchesSymbol) {
                        sum += Integer.parseInt(line.substring(x, xEnd));
                    }

                    x = xEnd;
                }
            }
        }

        return sum;
    }

    private static long part2(List<String> input) {
        // What is the sum of all of the gear ratios in your engine schematic?
        ListMultimap<Point, Integer> gearLocationToAdjacentNumbers = MultimapBuilder.hashKeys().arrayListValues().build();
        long sum = 0;
        for (int y = 0; y < input.size(); y++) {
            String line = input.get(y);
            for (int x = 0; x < line.length(); x++) {
                if (Character.isDigit(line.charAt(x))) {
                    int xEnd = x + 1;
                    while (xEnd < line.length() && Character.isDigit(line.charAt(xEnd))) {
                        xEnd++;
                    }

                    // check neighbours for stars
                    for (int yy = y - 1; yy <= y + 1; yy++) {
                        for (int xx = x - 1; xx <= xEnd; xx++) {
                            if (yy >= 0 && yy < input.size()
                                    && xx >= 0 && xx < line.length()) {
                                char c = input.get(yy).charAt(xx);
                                if ('*' == c) {
                                    // touches a gear
                                    gearLocationToAdjacentNumbers.put(
                                            new Point(xx, yy),
                                            Integer.parseInt(line.substring(x, xEnd)));
                                }
                            }
                        }
                    }

                    x = xEnd;
                }
            }
        }

        for (Point gearLocation : gearLocationToAdjacentNumbers.keySet()) {
            List<Integer> ints = gearLocationToAdjacentNumbers.get(gearLocation);
            if (ints.size() == 2) {
                sum += (ints.get(0) * ints.get(1));
            }
        }

        return sum;
    }

    static List<String> example = List.of(
            "467..114..",
            "...*......",
            "..35..633.",
            "......#...",
            "617*......",
            ".....+.58.",
            "..592.....",
            "......755.",
            "...$.*....",
            ".664.598.."
    );
}

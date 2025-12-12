package y2025;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static aoc.Common.loadInputFromResources;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2025D12 {

    public static void main(String[] args) throws Exception {
        List<String> input = loadInputFromResources();
        Stopwatch sw = Stopwatch.createStarted();
        try {
            // 1
            System.out.println(part1(Input.parse(example)));
//            assertThat(part1(Input.parse(example))).isEqualTo(2);
            assertThat(part1(Input.parse(input))).isEqualTo(422);

        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    @Value
    static class Input {
        List<Shape> shapes;
        List<Bin> bins;

        static Input parse(List<String> input) {
            int lineIdx = 0;
            List<Shape> shapes = new ArrayList<>();

            for (; ; lineIdx++) {
                if (!input.get(lineIdx).equals(shapes.size() + ":")) {
                    break;
                }
                List<String> thisShapeLines = new ArrayList<>();
                for (lineIdx++; !input.get(lineIdx).isEmpty(); lineIdx++) {
                    thisShapeLines.add(input.get(lineIdx));
                }
                shapes.add(new Shape(thisShapeLines));
            }

            Pattern spacePat = Pattern.compile("([0-9]+)x([0-9]+): ([0-9 ]+)");
            List<Bin> bins = new ArrayList<>();
            for (; lineIdx < input.size(); lineIdx++) {
                String line = input.get(lineIdx);
                var m = spacePat.matcher(line);
                checkState(m.matches());
                int width = Integer.parseInt(m.group(1));
                int height = Integer.parseInt(m.group(2));
                List<Integer> shapeCounts = Splitter.on(" ").splitToList(m.group(3))
                        .stream()
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());
                checkState(shapeCounts.size() == shapes.size());
                bins.add(new Bin(width, height, shapeCounts));
            }

            return new Input(shapes, bins);
        }
    }

    @Value
    static class Shape {
        List<String> lines;
        int area;

        public Shape(List<String> lines) {
            this.lines = lines;
            area = lines.stream().mapToInt(line -> (int) line.chars().filter(c -> c == '#').count()).sum();
        }
    }

    @Value
    static class Bin {
        int width, height;
        List<Integer> shapeCounts;
    }

    private static long part1(Input input) {
        // How many of the regions can fit all of the presents listed?
        int count = 0;
        for (Bin bin : input.bins) {
            int binArea = bin.width * bin.height;
            int shapeArea = 0;
            checkState(bin.shapeCounts.size() == input.shapes.size());
            for (int i = 0; i < input.shapes.size(); i++) {
                shapeArea += bin.shapeCounts.get(i) * input.shapes.get(i).area;
            }

            if (shapeArea <= binArea) {
                count++;
            }
        }
        return count;
    }

    static List<String> example = List.of(
            "0:",
            "###",
            "##.",
            "##.",
            "",
            "1:",
            "###",
            "##.",
            ".##",
            "",
            "2:",
            ".##",
            "###",
            "##.",
            "",
            "3:",
            "##.",
            "###",
            "##.",
            "",
            "4:",
            "###",
            "#..",
            "###",
            "",
            "5:",
            "###",
            ".#.",
            "###",
            "",
            "4x4: 0 0 0 0 2 0",
            "12x5: 1 0 1 0 2 2",
            "12x5: 1 0 1 0 3 2");
    ;
}

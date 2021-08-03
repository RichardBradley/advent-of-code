package y2017;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import org.apache.commons.math3.util.Pair;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2017D21 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            List<String> input = Resources.readLines(Resources.getResource("y2017/Y2017D21.txt"), StandardCharsets.UTF_8);

            assertThat(countPixAfterSteps(example, 2)).isEqualTo(12);
            assertThat(countPixAfterSteps(input, 5)).isEqualTo(167);
            assertThat(countPixAfterSteps(input, 18)).isEqualTo(2425195);
        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private static long countPixAfterSteps(List<String> rulesSpec, int steps) {
        List<Pair<List<StringBuilder>, List<StringBuilder>>> rules = parse(rulesSpec);
        List<StringBuilder> image = ImmutableList.of(
                new StringBuilder(".#."),
                new StringBuilder("..#"),
                new StringBuilder("###"));

        for (int i = 0; i < steps; i++) {
            int size = image.size();
            checkState(size == image.get(0).length());
            List<StringBuilder> next;
            if (size % 2 == 0) {
                // break the pixels up into 2x2 squares, and
                // convert each 2x2 square into a 3x3 square by
                // following the corresponding enhancement rule.
                next = createIm(size / 2 * 3);
                for (int x = 0; x < size; x += 2) {
                    for (int y = 0; y < size; y += 2) {
                        List<StringBuilder> thisCell = extractSubImage(image, x, y, 2);
                        List<StringBuilder> nextCell = lookupRule(thisCell, rules);
                        checkState(nextCell.size() == 3);
                        writeSubImage(next, x / 2 * 3, y / 2 * 3, nextCell);
                    }
                }
            } else {
                // break the pixels up into 3x3 squares, and
                // convert each 3x3 square into a 4x4 square by
                // following the corresponding enhancement rule.
                checkState(size % 3 == 0);
                next = createIm(size / 3 * 4);
                for (int x = 0; x < size; x += 3) {
                    for (int y = 0; y < size; y += 3) {
                        List<StringBuilder> thisCell = extractSubImage(image, x, y, 3);
                        List<StringBuilder> nextCell = lookupRule(thisCell, rules);
                        checkState(nextCell.size() == 4);
                        writeSubImage(next, x / 3 * 4, y / 3 * 4, nextCell);
                    }
                }
            }

            image = next;
        }

        return image.stream().mapToLong(line -> line.chars().filter(c -> c == '#').count()).sum();
    }


    private static List<StringBuilder> lookupRule(List<StringBuilder> thisCell, List<Pair<List<StringBuilder>, List<StringBuilder>>> rules) {
        for (Pair<List<StringBuilder>, List<StringBuilder>> ruleAndResult : rules) {
            List<StringBuilder> rule = ruleAndResult.getFirst();
            if (rule.size() != thisCell.size()) {
                continue;
            }

            for (int flip = 0; flip < 2; flip++) {
                for (int rotate = 0; rotate < 4; rotate++) {
                    if (equals(thisCell, rule)) {
                        return ruleAndResult.getSecond();
                    }

                    rule = rotate(rule);
                }
                flip(rule);
            }
        }
        throw new IllegalArgumentException("No rule for: " + toString(thisCell));
    }

    private static boolean equals(List<StringBuilder> a, List<StringBuilder> b) {
        for (int y = 0; y < a.size(); y++) {
            StringBuilder aRow = a.get(y);
            StringBuilder bRow = b.get(y);
            for (int x = 0; x < aRow.length(); x++) {
                if (aRow.charAt(x) != bRow.charAt(x)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static String toString(List<StringBuilder> im) {
        return im.stream().collect(Collectors.joining("\n"));
    }

    private static List<StringBuilder> extractSubImage(List<StringBuilder> image, int x, int y, int size) {
        List<StringBuilder> out = createIm(size);
        for (int outY = 0; outY < size; outY++) {
            StringBuilder inRow = image.get(y + outY);
            StringBuilder outRow = out.get(outY);
            for (int outX = 0; outX < size; outX++) {
                outRow.setCharAt(outX, inRow.charAt(x + outX));
            }
        }
        return out;
    }

    private static void writeSubImage(List<StringBuilder> target, int x, int y, List<StringBuilder> subImage) {
        for (int inY = 0; inY < subImage.size(); inY++) {
            StringBuilder targetRow = target.get(y + inY);
            StringBuilder inRow = subImage.get(inY);
            for (int inX = 0; inX < subImage.size(); inX++) {
                targetRow.setCharAt(x + inX, inRow.charAt(inX));
            }
        }
    }

    private static List<StringBuilder> createIm(int size) {
        List<StringBuilder> acc = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            StringBuilder line = new StringBuilder();
            for (int j = 0; j < size; j++) {
                line.append(' ');
            }
            acc.add(line);
        }
        return acc;
    }

    private static List<Pair<List<StringBuilder>, List<StringBuilder>>> parse(List<String> rulesSpec) {
        List<Pair<List<StringBuilder>, List<StringBuilder>>> acc = new ArrayList<>();
        for (String s : rulesSpec) {
            String[] strings = s.split(" => ");
            checkState(strings.length == 2);
            acc.add(Pair.create(parse(strings[0]), parse(strings[1])));
        }
        return acc;
    }

    private static List<StringBuilder> parse(String slashPic) {
        String[] rows = slashPic.split("/");
        List<StringBuilder> out = new ArrayList<>(rows.length);
        for (int y = 0; y < rows.length; y++) {
            out.add(new StringBuilder(rows[y]));
        }
        return out;
    }

    private static void flip(List<StringBuilder> pic) {
        for (int i = 0; i < pic.size(); i++) {
            pic.get(i).reverse();
        }
    }

    private static List<StringBuilder> rotate(List<StringBuilder> pic) {
        int len = pic.size();
        List<StringBuilder> rotated = new ArrayList<>(len);
        for (int y = 0; y < len; y++) {
            StringBuilder row = new StringBuilder();
            for (int x = 0; x < len; x++) {
                row.append(pic.get(x).charAt(len - y - 1));
            }
            rotated.add(row);
        }
        return rotated;
    }

    private static List<String> example = ImmutableList.of(
            "../.# => ##./#../...",
            ".#./..#/### => #..#/..../..../#..#");

}

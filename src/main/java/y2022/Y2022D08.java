package y2022;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

public class Y2022D08 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        List<String> input = Resources.readLines(Resources.getResource("y2022/Y2022D08.txt"), StandardCharsets.UTF_8);

        // 1
        assertThat(part1(example)).isEqualTo(21);
        System.out.println(part1(input));

        // 2
        assertThat(part2(example)).isEqualTo(8);
        System.out.println(part2(input));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    static Point[] dirs = new Point[]{
            new Point(0, 1),
            new Point(0, -1),
            new Point(-1, 0),
            new Point(1, 0)
    };

    // how many trees are visible from outside the grid?
    private static int part1(List<String> input) {
        int visibleCount = 0;
        int height = input.size();
        int width = input.get(0).length();
        for (int y = 0; y < height; y++) {
            nextTree:
            for (int x = 0; x < width; x++) {

                char currTree = input.get(y).charAt(x);
                directions:
                for (Point dir : dirs) {
                    int xx = x;
                    int yy = y;
                    while (true) {
                        xx += dir.x;
                        yy += dir.y;

                        if (xx < 0 || xx >= width || yy < 0 || yy >= height) {
                            // left boundary, visible
                            visibleCount++;
                            // System.out.printf("(%s,%s) %s visible\n", x,y, currTree);
                            continue nextTree;
                        }
                        char otherTree = input.get(yy).charAt(xx);
                        if (otherTree >= currTree) {
                            // not visible in this direction
                            continue directions;
                        }
                    }
                }
                // System.out.printf("(%s,%s) %s not visible\n", x,y, currTree);
            }
        }

        return visibleCount;
    }

    // What is the highest scenic score possible for any tree?
    private static int part2(List<String> input) {
        int max = 0;
        int height = input.size();
        int width = input.get(0).length();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                max = Integer.max(max, score(input, x, y));
            }
        }
        return max;
    }

    private static int score(List<String> input, int x, int y) {
        int height = input.size();
        int width = input.get(0).length();
        char currTree = input.get(y).charAt(x);
        int score = 1;
        dirs:
        for (Point dir : dirs) {
            int xx = x;
            int yy = y;
            int treesSeen = 0;
            while (true) {
                xx += dir.x;
                yy += dir.y;

                if (xx < 0 || xx >= width || yy < 0 || yy >= height) {
                    // outside boundary
                    score *= treesSeen;
                    continue dirs;
                }
                treesSeen++;
                char otherTree = input.get(yy).charAt(xx);
                if (otherTree >= currTree) {
                    // view blocked, but can see this tree
                    score *= treesSeen;
                    continue dirs;
                }
            }
        }
        // System.out.printf("score (%ás,%s) %s is %s\n", x,y, currTree, score);
        return score;
    }

    private static List<String> example = List.of(
            "30373",
            "25512",
            "65332",
            "33549",
            "35390");
}

package y2024;

import com.google.common.base.Stopwatch;
import lombok.Value;
import org.apache.commons.math3.util.Pair;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static aoc.Common.loadInputFromResources;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2024D14 {

    public static void main(String[] args) throws Exception {
        List<String> input = loadInputFromResources();
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(part1(example, 100, 11, 7)).isEqualTo(12);
        assertThat(part1(input, 100, 101, 103)).isEqualTo(218433348);

        // 2
        assertThat(part2(input)).isEqualTo(6512);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    @Value
    static class RobotInit {
        Point pos;
        Point vel;
    }

    private static long part1(
            List<String> input,
            int steps,
            int width,
            int height) {
        List<RobotInit> robots = input.stream()
                .map(x -> parse(x))
                .collect(Collectors.toList());

        Map<Pair<Boolean, Boolean>, Integer> robotCountsByQuadrant = new HashMap<>();
        int midWidth = width / 2;
        int midHeight = height / 2;

        for (RobotInit robot : robots) {
            int x = mod(robot.pos.x + steps * robot.vel.x, width);
            int y = mod(robot.pos.y + steps * robot.vel.y, height);

            if (x == midWidth || y == midHeight) {
                // excluded
                continue;
            }
            Pair<Boolean, Boolean> quadrant = Pair.create(x < midWidth, y < midHeight);

            robotCountsByQuadrant.compute(quadrant, (Pair<Boolean, Boolean> k, Integer count) -> {
                if (count == null) {
                    return 1;
                } else {
                    return count + 1;
                }
            });
        }

        return robotCountsByQuadrant.values().stream().mapToInt(count -> count)
                .reduce(1, (a, b) -> a * b);
    }

    private static int mod(int a, int m) {
        int ret = a % m;
        if (ret < 0) {
            ret += m;
        }
        return ret;
    }

    static Pattern p = Pattern.compile("p=(\\d+),(\\d+) v=(-?\\d+),(-?\\d+)");

    private static RobotInit parse(String line) {
        Matcher m = p.matcher(line);
        checkState(m.matches());
        return new RobotInit(
                new Point(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2))),
                new Point(Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4))));
    }

    static Point[] dirs = new Point[]{
            new Point(0, -1),
            new Point(1, 0),
            new Point(0, 1),
            new Point(-1, 0),
            new Point(-1, -1),
            new Point(-1, 1),
            new Point(1, -1),
            new Point(1, 1),
    };

    private static Point add(Point a, Point b) {
        return new Point(a.x + b.x, a.y + b.y);
    }

    private static long part2(List<String> input) {
        List<RobotInit> robots = input.stream()
                .map(x -> parse(x))
                .collect(Collectors.toList());

        Map<Pair<Boolean, Boolean>, Integer> robotCountsByQuadrant = new HashMap<>();
        int width = 101;
        int height = 103;
        int leastIsolatedCount = Integer.MAX_VALUE;

        for (int steps = 1; ; steps++) {
            if (steps % 100000 == 0) {
                System.out.println("step = " + steps);
            }

            Set<Point> locs = new HashSet<>();
            for (RobotInit robot : robots) {
                int x = mod(robot.pos.x + steps * robot.vel.x, width);
                int y = mod(robot.pos.y + steps * robot.vel.y, height);
                locs.add(new Point(x, y));
            }

            // are all contiguous?
            int isolatedCount = 0;
            locsLoop:
            for (Point loc : locs) {
                for (Point dir : dirs) {
                    Point n = add(loc, dir);
                    if (locs.contains(n)) {
                        continue locsLoop;
                    }
                }
                isolatedCount++;
            }

            if (isolatedCount == 0) {
                System.out.println("found at step = " + steps);
                print(height, width, locs);
                return steps;
            }
            if (isolatedCount < leastIsolatedCount) {
                leastIsolatedCount = isolatedCount;
                System.out.println("step = " + steps);
                System.out.println("new least = " + isolatedCount);
                print(height, width, locs);
            }
        }
    }

    private static void print(int height, int width, Set<Point> locs) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Point p = new Point(x, y);
                if (locs.contains(p)) {
                    System.out.print('@');
                } else {
                    System.out.print('.');
                }
            }
            System.out.print('\n');
        }
    }

    static List<String> example = List.of(
            "p=0,4 v=3,-3",
            "p=6,3 v=-1,-3",
            "p=10,3 v=-1,2",
            "p=2,0 v=2,-1",
            "p=0,0 v=1,3",
            "p=3,0 v=-2,-2",
            "p=7,6 v=-1,-3",
            "p=3,0 v=-1,-2",
            "p=9,3 v=2,3",
            "p=7,3 v=-1,2",
            "p=2,4 v=2,-3",
            "p=9,5 v=-3,-3");
}

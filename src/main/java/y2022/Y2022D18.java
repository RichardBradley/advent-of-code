package y2022;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;

import javax.vecmath.Point3i;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2022D18 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        List<String> input = Resources.readLines(Resources.getResource("y2022/Y2022D18.txt"), StandardCharsets.UTF_8);

        // 1
        assertThat(part1(example)).isEqualTo(64);
        System.out.println(part1(input));

        // 2
        assertThat(part2(example)).isEqualTo(58);
        System.out.println(part2(input));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }


    private static int part1(List<String> input) {
        Set<Point3i> points = parse(input);

        // count the number of sides of each cube that
        // are not immediately connected to another cube.
        int surfaceArea = 0;
        for (Point3i p : points) {
            for (Point3i dir : dirs) {
                if (!points.contains(add(p, dir))) {
                    surfaceArea++;
                }
            }
        }
        return surfaceArea;
    }

    private static Point3i add(Point3i x, Point3i y) {
        Point3i res = (Point3i) x.clone();
        res.add(y);
        return res;
    }

    private static Set<Point3i> parse(List<String> input) {
        Set<Point3i> points = new HashSet<>();
        for (String line : input) {
            String[] parts = line.split(",");
            checkState(parts.length == 3);
            checkState(points.add(
                    new Point3i(
                            Integer.parseInt(parts[0]),
                            Integer.parseInt(parts[1]),
                            Integer.parseInt(parts[2]))));
        }
        return points;
    }

    static int minZ, maxZ;

    static Point3i[] dirs = new Point3i[]{
            new Point3i(1, 0, 0),
            new Point3i(-1, 0, 0),
            new Point3i(0, 1, 0),
            new Point3i(0, -1, 0),
            new Point3i(0, 0, 1),
            new Point3i(0, 0, -1)
    };

    private static int part2(List<String> input) {
        Set<Point3i> points = parse(input);

        minZ = points.stream().mapToInt(p -> p.z).min().getAsInt();
        maxZ = points.stream().mapToInt(p -> p.z).max().getAsInt();

        // count the number of sides of each cube that
        // are not immediately connected to another cube.
        int surfaceArea = 0;
        for (Point3i p : points) {
            for (Point3i dir : dirs) {
                if (isExternal(points, add(p, dir))) {
                    surfaceArea++;
                }
            }
        }

        return surfaceArea;
    }

    private static boolean isExternal(Set<Point3i> points, Point3i p) {
        if (points.contains(p)) {
            return false;
        }
        // flood fill, DFS, decrease Z first
        Set<Point3i> visited = new HashSet<>();
        Stack<Point3i> visitQueue = new Stack<>();
        visitQueue.add(p);
        while (true) {
            if (visitQueue.empty()) {
                return false;
            }
            Point3i curr = visitQueue.pop();

            if (curr.z < minZ || curr.z > maxZ) {
                return true;
            }

            for (Point3i dir : dirs) {
                Point3i next = (Point3i) curr.clone();
                next.add(dir);
                if (!points.contains(next) && visited.add(next)) {
                    visitQueue.add(next);
                }
            }
        }
    }

    private static List<String> example = List.of(
            "2,2,2",
            "1,2,2",
            "3,2,2",
            "2,1,2",
            "2,3,2",
            "2,2,1",
            "2,2,3",
            "2,2,4",
            "2,2,6",
            "1,2,5",
            "3,2,5",
            "2,1,5",
            "2,3,5");
}

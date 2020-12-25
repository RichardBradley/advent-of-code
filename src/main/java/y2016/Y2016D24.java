package y2016;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Collections2;
import com.google.common.io.Resources;
import org.apache.commons.math3.util.Pair;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Queue;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2016D24 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        String input = Resources.toString(Resources.getResource("y2016/Y2016D24.txt"), StandardCharsets.UTF_8);

        assertThat(minDist(exampleInput, true)).isEqualTo(14);
        assertThat(minDist(input, true)).isEqualTo(460);
        assertThat(minDist(input, false)).isEqualTo(668);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static int minDist(String input, boolean isPart1) {
        String[] maze = input.split("\n");
        Map<Integer, Point> targets = new HashMap<>();
        for (int y = 0; y < maze.length; y++) {
            String line = maze[y];
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                if (c >= '0' && c <= '9') {
                    checkState(null == targets.put(c - '0', new Point(x, y)));
                    line = line.replace(c, '.');
                    maze[y] = line;
                }
            }
        }

        Map<Pair<Integer, Integer>, Integer> distancesBetweenPairs = new HashMap<>();

        for (int firstTarget = 0; firstTarget < targets.size(); firstTarget++) {
            for (int secondTarget = firstTarget + 1; secondTarget < targets.size(); secondTarget++) {
                distancesBetweenPairs.put(
                        new Pair<>(firstTarget, secondTarget),
                        distance(maze, targets.get(firstTarget), targets.get(secondTarget)));
            }
        }

        int shortestRouteLen = Integer.MAX_VALUE;
        List<Integer> targetsOtherThanZero = targets.keySet().stream()
                .filter(x -> x != 0)
                .collect(Collectors.toList());
        for (List<Integer> permutation : Collections2.permutations(targetsOtherThanZero)) {
            int routeDist = distancesBetweenPairs.get(Pair.create(0, permutation.get(0)));
            for (int i = 1; i < permutation.size(); i++) {
                int from = permutation.get(i - 1);
                int to = permutation.get(i);
                Pair<Integer, Integer> key;
                if (from < to) {
                    key = Pair.create(from, to);
                } else {
                    key = Pair.create(to, from);
                }
                routeDist += distancesBetweenPairs.get(key);
            }
            if (!isPart1) {
                routeDist += distancesBetweenPairs.get(Pair.create(0, permutation.get(permutation.size() - 1)));
            }
            shortestRouteLen = Math.min(shortestRouteLen, routeDist);
        }

        return shortestRouteLen;
    }

    private static int distance(String[] maze, Point from, Point to) {
        // flood fill distance like Dijkstra:
        Set<Point> visited = new HashSet<>();
        Queue<Point> nextPoints = new ArrayDeque<>();
        Queue<Point> nextNextPoints = new ArrayDeque<>();
        Point[] neighbourDeltas = new Point[]{
                new Point(1, 0),
                new Point(0, 1),
                new Point(-1, 0),
                new Point(0, -1)
        };
        nextPoints.add(from);
        for (int dist = 0; ; dist++) {
            Point point;
            while (null != (point = nextPoints.poll())) {
                for (Point delta : neighbourDeltas) {
                    Point neighbour = new Point(point.x + delta.x, point.y + delta.y);
                    if (neighbour.equals(to)) {
                        return dist + 1;
                    }
                    if ('.' == getChar(maze, neighbour)) {
                        if (visited.add(neighbour)) {
                            nextNextPoints.add(neighbour);
                        }
                    }
                }
            }

            Queue<Point> tmp = nextPoints;
            nextPoints = nextNextPoints;
            nextNextPoints = tmp;
        }
    }

    private static char getChar(String[] maze, Point point) {
        if (point.y >= 0 && point.y < maze.length) {
            String line = maze[point.y];
            if (point.x >= 0 && point.x < line.length()) {
                return line.charAt(point.x);
            }
        }
        return '\0';
    }

    static String exampleInput =
            "###########\n" +
                    "#0.1.....2#\n" +
                    "#.#######.#\n" +
                    "#4.......3#\n" +
                    "###########";
}

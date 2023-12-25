package y2023;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;
import y2023.Y2023D17.Dirs;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2023D23 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            List<String> input = Resources.readLines(Resources.getResource("y2023/Y2023D23.txt"), StandardCharsets.UTF_8);

            // 1
            assertThat(part1(example)).isEqualTo(94);
            assertThat(part1(input)).isEqualTo(2318);

            // 2
            assertThat(part2(example)).isEqualTo(154);
            assertThat(part2(input)).isEqualTo(6426);


        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private static int part2(List<String> map) {
        Map<Point, Map<Point, Integer>> connections = new HashMap<>();
        Set<Point> junctions = new HashSet<>();
        Point start = new Point(1, 1);
        junctions.add(start);
        int width = map.get(0).length();
        int height = map.size();
        Point end = new Point(height - 2, width - 2);
        junctions.add(end);
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                Point p = new Point(x, y);
                if (isTraversible(map, p)) {
                    int travCount = 0;
                    for (Dirs dir : Dirs.values()) {
                        Point n = dir.advance(p);
                        if (isTraversible(map, n)) {
                            travCount++;
                        }
                    }
                    if (travCount > 2) {
                        junctions.add(p);
                    }
                }
            }
        }
        System.out.println(junctions.size() + " junctions found");

        for (Point junction : junctions) {
            Set<Point> visited = new HashSet<>();
            visited.add(junction);
            for (Dirs dir : Dirs.values()) {
                floodFillConnections(map, junction, junctions, connections, visited, dir.advance(junction), 1);
            }
        }

        // Now find longest possible route using the simplified graph
        {
            Set<Point> visited = new HashSet<>();
            return 2 + findLongestRouteDfs2(connections, end, 0, visited, start);
        }
    }

    private static int findLongestRouteDfs2(Map<Point, Map<Point, Integer>> connections, Point target, int lenSoFar, Set<Point> visited, Point curr) {
        if (curr.equals(target)) {
            return lenSoFar;
        }

        int ret;
        if (visited.add(curr)) {
            ret = 0;
            for (Map.Entry<Point, Integer> connection : connections.get(curr).entrySet()) {
                ret = Math.max(ret, findLongestRouteDfs2(
                        connections,
                        target,
                        lenSoFar + connection.getValue(),
                        visited,
                        connection.getKey()));
            }
            visited.remove(curr);
            return ret;
        } else {
            return 0;
        }
    }

    private static void floodFillConnections(List<String> map, Point fromJunct, Set<Point> junctions, Map<Point, Map<Point, Integer>> connections, Set<Point> visited, Point curr, int currDist) {
        if (visited.add(curr)) {
            if (junctions.contains(curr)) {
                Map<Point, Integer> fromJunctConn = connections.computeIfAbsent(fromJunct, k -> new HashMap<>());
                checkState(null == fromJunctConn.put(curr, currDist));
            } else if (isTraversible(map, curr)) {
                for (Dirs dir : Dirs.values()) {
                    floodFillConnections(map, fromJunct, junctions, connections, visited, dir.advance(curr), 1 + currDist);
                }
            }
        }
    }

    private static boolean isTraversible(List<String> map, Point p) {
        if (p.y == 0 || p.y == map.size() - 1) {
            return false;
        }
        char c = map.get(p.y).charAt(p.x);
        return c == '.' || c == '>' || c == '<' || c == 'v' || c == '^';
    }

    private static long part1(List<String> input) {
        // Find the longest hike you can take through the hiking trails listed on your map.
        // How many steps long is the longest hike?

        // DFS
        Point target = new Point(input.size() - 2, input.get(0).length() - 1);
        // start inside the border, to avoid needing edge conditions
        Point start = new Point(1, 1);
        Set<Point> visited = new HashSet<>();
        visited.add(new Point(1, 0));
        return findLongestRouteDfs(input, start, 1, target, visited);
    }

    private static int findLongestRouteDfs(List<String> map, Point curr, int lenSoFar, Point target, Set<Point> visited) {
        if (curr.equals(target)) {
            return lenSoFar;
        }
        char c = map.get(curr.y).charAt(curr.x);
        if (c == '#') {
            return 0;
        }

        int ret;
        if (visited.add(curr)) {
            if (c == '.') {
                ret = 0;
                for (Dirs dir : Dirs.values()) {
                    ret = Math.max(ret, findLongestRouteDfs(map, dir.advance(curr), lenSoFar + 1, target, visited));
                }
            } else if (c == '>') {
                ret = findLongestRouteDfs(map, Dirs.E.advance(curr), lenSoFar + 1, target, visited);
            } else if (c == '<') {
                ret = findLongestRouteDfs(map, Dirs.W.advance(curr), lenSoFar + 1, target, visited);
            } else if (c == '^') {
                ret = findLongestRouteDfs(map, Dirs.N.advance(curr), lenSoFar + 1, target, visited);
            } else if (c == 'v') {
                ret = findLongestRouteDfs(map, Dirs.S.advance(curr), lenSoFar + 1, target, visited);
            } else {
                throw new IllegalArgumentException("for " + c);
            }
            visited.remove(curr);
            return ret;
        } else {
            return 0;
        }
    }

    static List<String> example = List.of(
            "#.#####################",
            "#.......#########...###",
            "#######.#########.#.###",
            "###.....#.>.>.###.#.###",
            "###v#####.#v#.###.#.###",
            "###.>...#.#.#.....#...#",
            "###v###.#.#.#########.#",
            "###...#.#.#.......#...#",
            "#####.#.#.#######.#.###",
            "#.....#.#.#.......#...#",
            "#.#####.#.#.#########v#",
            "#.#...#...#...###...>.#",
            "#.#.#v#######v###.###v#",
            "#...#.>.#...>.>.#.###.#",
            "#####v#.#.###v#.#.###.#",
            "#.....#...#...#.#.#...#",
            "#.#########.###.#.#.###",
            "#...###...#...#...#.###",
            "###.###.#.###v#####v###",
            "#...#...#.#.>.>.#.>.###",
            "#.###.###.#.###.#.#v###",
            "#.....###...###...#...#",
            "#####################.#"
    );
}

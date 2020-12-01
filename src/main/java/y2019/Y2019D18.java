package y2019;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import lombok.SneakyThrows;
import lombok.Value;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigInteger;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.IntPredicate;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;
import static java.lang.System.console;
import static java.lang.System.out;
import static y2019.Y2019D09.parse;

public class Y2019D18 {

    private static boolean LOG = false;

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        testAdd();
        assertThat(shortestStepsToAllKeys(example1)).isEqualTo(8);
        out.println("example 1 ok");
        assertThat(shortestStepsToAllKeys(example2)).isEqualTo(86);
        out.println("example 2 ok");
        assertThat(shortestStepsToAllKeys(example3)).isEqualTo(136);
        out.println("example 3 ok");
        out.println(shortestStepsToAllKeys(input));

        // 2
//        out.println(timeToFillWithOxygen(input));

        out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static int shortestStepsToAllKeys(String mapStr) {
        String[] lines = mapStr.split("\n");
        int height = lines.length;
        int width = lines[0].length();
        char[][] map = new char[height][];
        for (int y = 0; y < height; y++) {
            map[y] = lines[y].toCharArray();
        }
        long keyCount = Arrays.stream(lines)
                .flatMapToInt(line -> line.chars())
                .filter(i -> isKey((char) i)).count();

        Point entry = null;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (map[y][x] == '@') {
                    checkState(entry == null);
                    entry = new Point(x, y);
                }
            }
        }
        checkState(entry != null);

        PriorityQueue<SearchState> searchQueue = new PriorityQueue<>();
        Map<String, Integer> shortestDistByKeySet = new HashMap<>();
        searchQueue.add(new SearchState(entry, 0, ""));
        while (true) {
            SearchState curr = searchQueue.poll();
            log("at " + curr);
            if (curr.collectedKeys.length() == keyCount) {
                return curr.dist;
            }
            List<ReachableKey> reachableKeys = curr.findReachableKeys(map);
            for (ReachableKey reachableKey : reachableKeys) {
                String newCollectedKeys = add(curr.collectedKeys, reachableKey.key);
                searchQueue.add(new SearchState(
                        reachableKey.location,
                        reachableKey.newDist,
                        newCollectedKeys));
                // qq how to discard bad paths? Need an admissable heuristic, but dist is no good, see example2
//                shortestDistByKeySet.compute(newCollectedKeys, (k, oldDist) -> {
//                    if (oldDist == null || oldDist > reachableKey.newDist) {
//                        SearchState ss = new SearchState(
//                                reachableKey.location,
//                                reachableKey.newDist,
//                                newCollectedKeys);
//                        log("enqueue " + ss);
//                        searchQueue.add(ss);
//                        return reachableKey.newDist;
//                    } else {
//                        log("ignore fetch of %s for %s as slower than previous alternative dist %s",
//                                reachableKey.key, reachableKey.newDist, oldDist);
//                        return oldDist;
//                    }
//                });
            }
        }
    }

    private static void log(String format, Object... args) {
        if (LOG) {
            out.printf(format + "\n", args);
        }
    }

    private static String add(String keys, char key) {
        int idxToAdd;
        for (idxToAdd = 0; idxToAdd < keys.length(); idxToAdd++) {
            if (keys.charAt(idxToAdd) > key) {
                break;
            }
        }
        return keys.substring(0, idxToAdd) + key + keys.substring(idxToAdd);
    }

    static void testAdd() {
        assertThat(add("", 'x')).isEqualTo("x");
        assertThat(add("a", 'x')).isEqualTo("ax");
        assertThat(add("x", 'a')).isEqualTo("ax");
        assertThat(add("acd", 'b')).isEqualTo("abcd");
    }

    private static boolean isKey(char c) {
        return c >= 'a' && c <= 'z';
    }

    private static boolean isDoor(char c) {
        return c >= 'A' && c <= 'Z';
    }

    @Value
    static class ReachableKey {
        Point location;
        char key;
        int newDist;
    }

    @Value
    static class SearchState implements Comparable<SearchState> {
        Point location;
        int dist;
        String collectedKeys;

        @Override
        public int compareTo(SearchState that) {
            return Integer.compare(this.dist, that.dist);
        }

        public List<ReachableKey> findReachableKeys(char[][] map) {
            // Looks like the maze is acyclic?
            // try a depth first search?
            List<ReachableKey> reachableKeys = new ArrayList<>();
            findReachableKeys(map, dist, location, new HashSet<>(), reachableKeys);
            return reachableKeys;
        }

        private void findReachableKeys(char[][] map, int dist, Point location, Set<Point> visited, List<ReachableKey> reachableKeys) {
            if (!visited.add(location)) {
                return; // already visited
            }
            char c = map[location.y][location.x];
            checkState(dist <= 200); // catch runaway
            if (isKey(c) && !contains(collectedKeys, c)) {
                reachableKeys.add(new ReachableKey(location, c, dist));
            } else if (c == '#') {
                return;
            } else if (isDoor(c) && !contains(collectedKeys, Character.toLowerCase(c))) {
                return;
            } else if (c == '.' || c == '@' || isKey(c) || isDoor(c)) {
                findReachableKeys(map, dist + 1, new Point(location.x + 1, location.y), visited, reachableKeys);
                findReachableKeys(map, dist + 1, new Point(location.x - 1, location.y), visited, reachableKeys);
                findReachableKeys(map, dist + 1, new Point(location.x, location.y + 1), visited, reachableKeys);
                findReachableKeys(map, dist + 1, new Point(location.x, location.y - 1), visited, reachableKeys);
            } else {
                throw new IllegalArgumentException("for " + c);
            }
        }

        private boolean contains(String keys, char k) {
            return keys.indexOf(k) >= 0;
        }
    }

    static String example1 =
            "#########\n" +
                    "#b.A.@.a#\n" +
                    "#########";

    static String example2 =
            "########################\n" +
                    "#f.D.E.e.C.b.A.@.a.B.c.#\n" +
                    "######################.#\n" +
                    "#d.....................#\n" +
                    "########################";

    static String example3 =
            "#################\n" +
                    "#i.G..c...e..H.p#\n" +
                    "########.########\n" +
                    "#j.A..b...f..D.o#\n" +
                    "########@########\n" +
                    "#k.E..a...g..B.n#\n" +
                    "########.########\n" +
                    "#l.F..d...h..C.m#\n" +
                    "#################";

    static String input =
            "#################################################################################\n" +
                    "#...#.......#....a..#...........#..e....#.....#...#...#...........#.............#\n" +
                    "#.#.#.#####.#.#####.#.#######.###.###.#.#.###.#.#.###.#.#########.#.###.#######.#\n" +
                    "#.#.#.#.#...#.#.K...#...#...#.....#.#.#.#.#.....#.#...#t......#...#.#...#.......#\n" +
                    "#.###B#.#.#.#.#.#######.###.#######.#.###.#######.#.#########.#.#####.###.#######\n" +
                    "#.#q..#.#.#.#.#...#.....#...#.......#...#...#.#...#.........#.#.......#.#.#.....#\n" +
                    "#.#.###.#.#.#.###.#.#####.#.#.#####.###.###.#.#.#####.#######.#########.#.###.#.#\n" +
                    "#...#...#.#.#...#.#.......#...#.....#...#...#.........#.....#.......#.#...#m..#.#\n" +
                    "#.#####.#.#####.#.#######.#########.#.###F#############.###.###.###.#.#.###.#####\n" +
                    "#...#...#.......#.......#.#......h#.#...#.#.....#.......#.....#.#...#.#.#.......#\n" +
                    "###.#.###########.#####.#.#.#####.#####.#.#.###.#.#####.#####.#.#.###G#.###.###.#\n" +
                    "#.#.#.......#...#...#...#.#...J.#.#.....#.#...#.#.....#.#...#.#.#.#...#...#...#.#\n" +
                    "#.#.#####.###.#.###.#####.#####.#.#.###.#.###.#.#####.#.#.#.#.###.#.#.###.#####S#\n" +
                    "#w#...#...#...#...#...#...#...#.#...#...#.#...#.....#.#.#.#.#.....#.#...#.......#\n" +
                    "#.###.#.###.#####.###.#.#####.#.#####.###.#.###.###.#.###.#.#######.###.#######.#\n" +
                    "#...#...#...#.....#...#.#...#.#...#.#.#.#.#.#.#.#...#.....#...#...#.#.#.........#\n" +
                    "#.#####.#.###.#####.###.#.#.#.###.#.#.#.#.#.#.#.###.#########.#.#.#.#.###########\n" +
                    "#.......#...#.....#.#.....#...#.#.#...#.#...#.#...#.....#...#.#.#.#.....#.......#\n" +
                    "#C#########.#####.#.#.#######.#.#.###.#.#.###.###.#####.#.###.#.#######.###.###.#\n" +
                    "#.#.......#.#.#...#...#...#.....#...#...#.......#.#.....#.#...#.....#.#...#...#.#\n" +
                    "#.###.#.#.#.#.#.###.###.#.#########.###.#####.###.#.#####.#.#####.#.#.###.###.###\n" +
                    "#...#.#.#.#.#.#.#y..#...#...#.....#.#...#.....#...#.#.....#...#...#.....#...#...#\n" +
                    "###.###.#I#.#.#.###########.#.###.#.#####.#####.###.#.###.#####.###########.#.#.#\n" +
                    "#...#...#.#.#.#...........#...#...#.#...#...#...#.#...#...#.....#.........#.#.#.#\n" +
                    "#.###.###.#.#.#####.###########.###.#.#.#####.###.#.#####.###.###.#######.#.###.#\n" +
                    "#.....#...#...#...#.............#.....#.#.....#...#.#...#.....#...#.....#...#...#\n" +
                    "#####.#######.#.#.###############.#####.#.#######.#.#.#.#####.#.###.###.#####.###\n" +
                    "#.....#.....#...#.#...#.............#...#.......#...#.#.#.....#.#.#.#.#.........#\n" +
                    "#.#####.###.#####.#.###.###########.#.###.#####.#####.#.#######.#.#.#.#########.#\n" +
                    "#...#.#.#.#.......#...........#...#.#...#.....#.......#...#.....#...#.....#...#.#\n" +
                    "###.#.#.#.###############.#####.#.#.###.#####.###########.#.#######.###.###.#.#.#\n" +
                    "#...#.#...#.......#.....#.#.....#.#...#.#.....#...#.....#...#.....#.#...#...#...#\n" +
                    "#.###.###.#.#####.#.###.###.#####.#####.#.#####.###.###.#####.###.#.#.###.#####.#\n" +
                    "#.#.....#.#...#.....#...#...#.....#.....#...#.......#...#.....#.#.#...#...#.....#\n" +
                    "#.###L#.#.#.#.#######.###.###.#####.###.###.#.#######.#.###.###.#.###.#.###.#####\n" +
                    "#o..#.#.#.#.#.....#.#.#...#.#...#...#.#.#.#.#.....#...#.....#.#...#...#.#.......#\n" +
                    "###.###.#.#######.#.#.#.#.#.###.#.###.#.#.#.#######.#########.#.#######.#######.#\n" +
                    "#.#...#.#.......#.#.#.#.#.#.#...#.#...#.#.#.#...#...#.........#.#.....#.......#.#\n" +
                    "#.###.#.#######.#.#.#.###.#.#.###.#.###.#.#.#.#.#.###.#######.#.#.###.#######.#.#\n" +
                    "#.............#...#.......#.......#...........#...#.........#.....#...........#.#\n" +
                    "#######################################.@.#######################################\n" +
                    "#.....#.....#.#.........#.#...#.....#...........#..u........#...#.......#.....Q.#\n" +
                    "#.#.###.#.#.#.#.#######.#.#.#.#.#.###.#.#.#.###.#.#########.###.#.#.###.#######.#\n" +
                    "#.#.#...#.#.#.#.....#...#.R.#.#.#.....#.#.#...#...#.......#r....#.#...#.........#\n" +
                    "#.#.#.###.#.#.#####.###.#####.#.#######.#.###.#######.###.#####.#.###.#####.#####\n" +
                    "#.#...#...#.......#...#.....#.#.#.#...#.#.#.#...#...#.#...#.....#b#.#...#...#..c#\n" +
                    "#.#####.#############.#####.#.#.#.#.#.#.#.#.###.#.#.#.#.###.#####.#.###.#####.#.#\n" +
                    "#p#...V.#..x#.......#...#...#.....#.#...#.#...#.#.#...#...#.#.#...#...#.....#.#.#\n" +
                    "###.#####.#.#.#####.###.#.###U#####.###.#.#.#.#.#.#######.#.#.#.###.#.#####.#.#.#\n" +
                    "#...#.....#.....#...#...#.#.#.#.....#.#.#...#.#.#.#...#.#.#...#...#.#.....#...#.#\n" +
                    "#.###.###########.###.###.#.#.#.#####.#.#.#####.#.#.#.#.#.###.###.#.#####.#####.#\n" +
                    "#.......#.......#...#.#.#.#...#...#.....#.#...#.#.#.#.#...#...#.#.#.....#...#.#.#\n" +
                    "#.#####.#.#####.###.#.#.#.#.#####.#######.#.#.#.#.#H#.###.#.###.#.#.###.###.#.#.#\n" +
                    "#.#...#.#.#...#.....#.#...#.....#...#...#.#.#...#.#.#...#.#.....#.#...#.#.#...#.#\n" +
                    "#.#.#.###.#.#######.#.###.#########.#.#.###.#####.#.###.###.#####.#####.#.###.#.#\n" +
                    "#.#.#.....#...#.....#...#.#.......#...#.#...#.....#.#.#.....#...#.......#.....#.#\n" +
                    "#.#.#########.#.#######.#.#.#####.#####.#.###.#.###.#.#######.#.#########.#####.#\n" +
                    "#.#.........#.#.#.#.....#...#...#.....#.#...#.#.#.........#...#...#.....#...#...#\n" +
                    "#.#########.#.#.#.#.#########.#.#####.#.#.#.#.###.#########.###.###.#.#####.#.#.#\n" +
                    "#.#.......#.#.#...#.#.......#.#.....#.#.#.#.#.....#.#.........#.#...#.#.....#.#.#\n" +
                    "#.#######.#.#.###.#.###.#####.#####.#.#.#.#.###.###.#.#########.#.###.#.#####.#.#\n" +
                    "#......z..#.#.#...#...#.....#.#.....#.#.#.#...#.....#.......#.#.#.#.#...#...#.#.#\n" +
                    "#########.#.#.#.#######.###.#.#.###.#.#.#.###.###########.###.#.#.#.#######.#.#.#\n" +
                    "#.#.......#.#...#.....#.#.#.#.#.#...#.#.#.#.#.............#...#.#.......#...#.#.#\n" +
                    "#.#.#######.#.###.#.#.#.#.#.#.#.#.###.#.#.#.###############.###.#######.#.###.#.#\n" +
                    "#...#.......#...#.#.#.#...#...#.#...#.#.#....j#...#...#....d#...#...#...#...#.#.#\n" +
                    "#.###.#########.###.#.###.#####.#####.#.#####.#.###.#.#.#######.#.#.#.#####.#.#.#\n" +
                    "#...#.#.......#.#...#...#...#.#.....#...#...#.#.....#.#.Z.#.....#.#...#.....#.#.#\n" +
                    "#D###.#####.###.#.#####.###.#.#####.###.###.#.#######.###.###.###.#######.###.#.#\n" +
                    "#.#...#...#..n#...#.N.#.#...#.....#.....#...#.....#...#.#...#.....#.....#.#...#.#\n" +
                    "###.###.#.###.#######.#.#.#####.#.#######.#######.#.#.#.###.#.#####.###.#.#.###W#\n" +
                    "#...#...#.............#.#.....#.#.....M.#.#.......#.#...#...#.......#.....#...#.#\n" +
                    "#.###.#.###############.#####.###.#####.#.#.#######.###.#.#######.#####.#####.#.#\n" +
                    "#.#.P.#...#...#.......#.#...#.....#...#.#.#...#.....#...#...#.#...#...#.#.....#i#\n" +
                    "#.#######.#.#.#.#####.#.#.#.#######.#.#.#.###.###.###.#####.#.#.###.#.###.#####.#\n" +
                    "#.......#...#.......#...#.#.......#.#...#...#....f#.E.#...#.#.....#.#.#...#.O.#.#\n" +
                    "#.#####.#################.###.#####.#####.#########.#####.#.#######X#.#.###.#.###\n" +
                    "#.#.#...#...#...T...#.A.#...#..k..#.#...#.#...#...#.#...#.#.....#...#.#.....#...#\n" +
                    "#.#.#.###.#.#.#####.#.#.###.#####.#.#.#.#.#.#.#.#.#.#.#.#.#####.#.###.#########.#\n" +
                    "#...#.....#.......#...#.........#.Y...#.#..s#...#..g..#.......#v..#............l#\n" +
                    "#################################################################################";
}

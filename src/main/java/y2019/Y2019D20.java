package y2019;

import com.google.common.base.Stopwatch;
import lombok.Value;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;
import static java.lang.System.out;

public class Y2019D20 {

    private static boolean LOG = false;

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(shortestPathAAtoZZ(example1)).isEqualTo(23);
        out.println("example 1 ok");
        assertThat(shortestPathAAtoZZ(example2)).isEqualTo(58);
        out.println("example 2 ok");
        out.println(shortestPathAAtoZZ(input));

        // 2
//        out.println(timeToFillWithOxygen(input));

        out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static int shortestPathAAtoZZ(String mazeSpec) {
        parse(mazeSpec);
        return -1; // qq
    }

    private static class Maze {

    }

    private static Maze parse(String mazeSpec) {
        // find portal labels
        Map<Point, Point> portals = new HashMap<>();
return null; // qq
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
            "         A           \n" +
                    "         A           \n" +
                    "  #######.#########  \n" +
                    "  #######.........#  \n" +
                    "  #######.#######.#  \n" +
                    "  #######.#######.#  \n" +
                    "  #######.#######.#  \n" +
                    "  #####  B    ###.#  \n" +
                    "BC...##  C    ###.#  \n" +
                    "  ##.##       ###.#  \n" +
                    "  ##...DE  F  ###.#  \n" +
                    "  #####    G  ###.#  \n" +
                    "  #########.#####.#  \n" +
                    "DE..#######...###.#  \n" +
                    "  #.#########.###.#  \n" +
                    "FG..#########.....#  \n" +
                    "  ###########.#####  \n" +
                    "             Z       \n" +
                    "             Z       ";

    static String example2 =
            "                   A               \n" +
                    "                   A               \n" +
                    "  #################.#############  \n" +
                    "  #.#...#...................#.#.#  \n" +
                    "  #.#.#.###.###.###.#########.#.#  \n" +
                    "  #.#.#.......#...#.....#.#.#...#  \n" +
                    "  #.#########.###.#####.#.#.###.#  \n" +
                    "  #.............#.#.....#.......#  \n" +
                    "  ###.###########.###.#####.#.#.#  \n" +
                    "  #.....#        A   C    #.#.#.#  \n" +
                    "  #######        S   P    #####.#  \n" +
                    "  #.#...#                 #......VT\n" +
                    "  #.#.#.#                 #.#####  \n" +
                    "  #...#.#               YN....#.#  \n" +
                    "  #.###.#                 #####.#  \n" +
                    "DI....#.#                 #.....#  \n" +
                    "  #####.#                 #.###.#  \n" +
                    "ZZ......#               QG....#..AS\n" +
                    "  ###.###                 #######  \n" +
                    "JO..#.#.#                 #.....#  \n" +
                    "  #.#.#.#                 ###.#.#  \n" +
                    "  #...#..DI             BU....#..LF\n" +
                    "  #####.#                 #.#####  \n" +
                    "YN......#               VT..#....QG\n" +
                    "  #.###.#                 #.###.#  \n" +
                    "  #.#...#                 #.....#  \n" +
                    "  ###.###    J L     J    #.#.###  \n" +
                    "  #.....#    O F     P    #.#...#  \n" +
                    "  #.###.#####.#.#####.#####.###.#  \n" +
                    "  #...#.#.#...#.....#.....#.#...#  \n" +
                    "  #.#####.###.###.#.#.#########.#  \n" +
                    "  #...#.#.....#...#.#.#.#.....#.#  \n" +
                    "  #.###.#####.###.###.#.#.#######  \n" +
                    "  #.#.........#...#.............#  \n" +
                    "  #########.###.###.#############  \n" +
                    "           B   J   C               \n" +
                    "           U   P   P               ";

    static String input =
            "                                       U         R     Q     F           T C   J                                         \n" +
                    "                                       S         E     V     C           Q G   C                                         \n" +
                    "  #####################################.#########.#####.#####.###########.#.###.#######################################  \n" +
                    "  #...#...................#.........#.......#.....#.#...#...#.#...#.......#.#...#...#.#.........#.#.#.....#...#...#.#.#  \n" +
                    "  #.#.###.#.###.#.#######.###.###.#.###.#####.#.###.#.###.#.#.#.#####.###.#.#.#.#.###.#.#########.#.#.#.###.###.###.#.#  \n" +
                    "  #.#...#.#...#.#.#...#.......#...#...#.#.#...#...#.......#.#.....#...#.....#.#...#.................#.#.#.#.#...#.....#  \n" +
                    "  #####.#########.###.###.#####.###.#.#.#.###.#############.###.###.#.#####.#.#.###.#####.#.#.###.#####.#.#.###.###.###  \n" +
                    "  #.......#.........#.....#.#...#...#.......#.....#.#.....#.#...#.#.#.....#.#.#.....#.#...#.#.#.#.#.....#.....#.#.....#  \n" +
                    "  #####.#.###.###.###.#.###.###.#.###.#.#.#####.#.#.###.#.#.#.###.###.#######.#.#####.#.#.#####.#.#####.###.###.#.#.#.#  \n" +
                    "  #.#...#.#.#.#.#.#...#.#.....#.#.#...#.#...#...#...#...#...#.......#...#.#.#.#.#.....#.#.......#.#.......#.#.....#.#.#  \n" +
                    "  #.###.###.###.#.#####.#.###.#########.#.#.###.#.#.#.###.#####.#.#####.#.#.#.###.#####.###.###.#####.#.#.#.###.#######  \n" +
                    "  #...#.........#.#.#...#.#...........#.#.#.#...#.#.#.#...#.#...#.....#.....#...#.#.#.#.#.#...#.#.#...#.#...#.#...#...#  \n" +
                    "  ###.###.#####.###.###########.#.###.###.#########.#.#####.###.#########.###.###.#.#.#.#.#######.#######.###.#.###.#.#  \n" +
                    "  #.......#.#.#.........#.#.#...#...#...#.......#.#.#.#.#...........#.....#.........#...#.....#.#...#.#...#.......#.#.#  \n" +
                    "  #####.#.#.#.#.###.###.#.#.#.#######.#.#.#.#.###.#.#.#.###.###.#######.#####.#.#########.#####.#.###.#.#####.###.###.#  \n" +
                    "  #.#...#.#...#.#...#.#...#.......#...#...#.#.#.....#.....#.#.......#.#...#.#.#...............#...#.#...#.......#...#.#  \n" +
                    "  #.#########.#######.###.###########.###.#########.#.#.#.#########.#.#.###.#.#.#####.#.#.#####.###.#.#.#.#.#########.#  \n" +
                    "  #.......#.#...#.#.....#.#.#...#.....#.....#.......#.#.#.#...#.....#.....#...#.#.#...#.#.#.......#.#.#.#.#...#.#.#.#.#  \n" +
                    "  ###.#####.###.#.#####.#.#.###.#####.###.#.#####.#.#.#######.#.#.#.###.#####.#.#.###.#####.###.#.#.###.###.###.#.#.#.#  \n" +
                    "  #...#...#.#.#.#.#.#.#...#.#.......#...#.#.#.....#.#.....#.....#.#.#.......#.#.....#.#.#.#...#.#.#.....#.#.#.....#.#.#  \n" +
                    "  ###.#.###.#.#.#.#.#.#.###.###.#.#.#.#.#######.###.###.#.#.#.#######.###.#####.#####.#.#.#.#.#########.#.#.###.###.#.#  \n" +
                    "  #.....#.#.......#...#.#.#.#...#.#...#.#...#...#.#.#...#.#.#.#.#...#.#.#.#.#...#.#.#.#...#.#.#.#.......#.#...........#  \n" +
                    "  ###.###.#######.###.#.#.#.#####.#.#.#####.#####.#.#.#######.#.###.#.#.###.#.###.#.###.###.###.###.###.#.#.#########.#  \n" +
                    "  #...#...#.#.#.............#...#.#.#.#.#...#.#.#.#.#.......#.......#.......#...#.........#...#.#.#.#...............#.#  \n" +
                    "  ###.###.#.#.#######.#.#.#.###.#####.#.#.###.#.#.#.#######.###.###.#######.#.#####.#######.###.#.#.#######.#.#########  \n" +
                    "  #...#...#.....#.#...#.#.#.#...#...#.....#...#.....#.........#.#...#.....#.#.....#...#.#.#.......#.#.......#.........#  \n" +
                    "  #.#.###.###.###.#########.###.#.#######.#.#######.#.#########.###.#.#.###.#.#####.###.#.###.#########.#.#.#########.#  \n" +
                    "  #.#.#...........#.#...#.#...#.........#...#.#.....#.#.....#.#.#...#.#.....#.#.................#.#.#.#.#.#.....#.#.#.#  \n" +
                    "  ###.###.#.#.#####.###.#.###.###.###.#.###.#.#.#####.#.###.#.#####.#.#######.#.###.#####.#######.#.#.#.###.#####.#.###  \n" +
                    "  #...#.#.#.#...#.#.#.#...........#...#.....#...#...#...#.#.....#...#.......#...#.......#.....#...#...#.#.#.........#.#  \n" +
                    "  #.#.#.###.#####.#.#.#####.#.#.#####.#####.#.#####.#####.#.#.#####.#######.#####.###.#######.###.#.#####.#.#########.#  \n" +
                    "  #.#.#.#...#.#...#...#.#...#.#.#.#.....#.#.#.........#.#...#.....#.#.........#.....#.......#.#...#...#.#.#.........#.#  \n" +
                    "  #.###.###.#.###.#.###.#########.###.#.#.#########.###.#.#.#.#.###.###.#########.###.#.#.#######.#.###.#.#.#########.#  \n" +
                    "  #...#...#.#.#...#.....#.#.....#...#.#.....#.......#.....#.#.#.#...#.......#.......#.#.#...#.#...#...#.#.#...#.#.....#  \n" +
                    "  #.###.#.#.#.#.###.#.###.#####.#.#########.#.#######.###########.###.###.#####.#######.#.###.#.#.###.#.#.#.###.###.###  \n" +
                    "  #.#.#.#...#.#...#.#.......#.#.#.#        B J       D           B   E   B     O      #.#.#...#.#.#.#.#.#...#.#.#.....#  \n" +
                    "  #.#.###.#.#.###.#####.#.###.#.#.#        M B       Z           Z   P   C     K      #####.#.###.#.#.#.###.#.#.###.###  \n" +
                    "  #...#.#.#.....#.#.#.#.#.#.#.#...#                                                   #...#.#.....#...........#.....#.#  \n" +
                    "  #.###.###.#####.#.#.###.#.#.#.#.#                                                   #.#####.###.#.#####.###.#.###.#.#  \n" +
                    "  #.....#...#.#.....#...#.......#.#                                                   #.......#...#.#.#.....#...#......MZ\n" +
                    "  #####.#.#.#.###.###.#######.###.#                                                   ###.#.###.###.#.###.###.#.###.#.#  \n" +
                    "ZR......#.#...#.........#.....#....FC                                               HM..#.#.#.#...#...#.#...#.#.#...#.#  \n" +
                    "  #.###.###.#.#.#######.###.#####.#                                                   #.#.###.###.#.###.#.#.#########.#  \n" +
                    "  #.#.......#.....#.........#...#.#                                                   #...#.#.#.#.....#.#.#...#.....#.#  \n" +
                    "  #.###.#.#####################.###                                                   #####.#.#.#######.#####.#####.###  \n" +
                    "  #.#...#.#.....#...#.#.#.......#.#                                                   #.#...........#...#...#.#........ZZ\n" +
                    "  #########.#.#.###.#.#.###.###.#.#                                                   #.#.#.#####.#####.###.###.#######  \n" +
                    "  #.#.......#.#.#.#.#.......#.#...#                                                   #...#...#...#.#.#...#.#.........#  \n" +
                    "  #.###.#####.###.#.#.#.#####.###.#                                                   #.###.###.###.#.#.#.#.#########.#  \n" +
                    "  #...#.....#.#.....#.#.#.........#                                                 QV..#...#.......#.#.#...#.#.....#..BZ\n" +
                    "  #.#######.#.###.#.#.#####.#######                                                   #.#########.###.#.###.#.#.###.#.#  \n" +
                    "HM..........#.....#.......#........TQ                                                 #...#.#.#.........#.#.....#.....#  \n" +
                    "  #.#.#############################                                                   #####.#.###########.#####.#.#####  \n" +
                    "  #.#.#.#.......#...........#.....#                                                   #.................#.....#.#.#.#..OK\n" +
                    "  #####.#####.#.#.#.###.###.###.#.#                                                   #.#.#.###.###.#.#.#.#########.#.#  \n" +
                    "AA..#.....#.#.#...#...#...#.#...#.#                                                 US..#.#...#.#...#.#.....#.....#...#  \n" +
                    "  #.#.#####.#.#############.#.#.#.#                                                   #.###.#######.#.#####.#.###.#.###  \n" +
                    "EK....#.....#...#.......#.#.#.#.#..UG                                                 #.#.#.#.#.....#.#.....#.#...#...#  \n" +
                    "  ###.###.#.#.#####.#####.#.#.###.#                                                   #.#.###.###########.###.#.#####.#  \n" +
                    "  #.......#...#...#.....#.....#...#                                                   #.#.....#...#.#.#.......#.......#  \n" +
                    "  #.#############.#.###############                                                   #####.###.###.#.#################  \n" +
                    "  #.#...........................#.#                                                   #...................#............II\n" +
                    "  ###.###.#.#.#.###.#.#######.###.#                                                   #.#.#####.#########.#.###.#.###.#  \n" +
                    "  #.#...#.#.#.#.#.#.#.#.........#.#                                                 SJ..#...#.....#...#.#...#...#...#.#  \n" +
                    "  #.#.#####.#####.#.#########.###.#                                                   #############.###.#####.#########  \n" +
                    "UG......#.....#...#.#...#...#.#....QM                                               KI..............#.......#...#.#...#  \n" +
                    "  #.#.#.#.#####.#####.#####.#.#.###                                                   #.###.###.###.#.#####.#.###.###.#  \n" +
                    "  #.#.#.#.#...#...#.....#...#.....#                                                   #.#.....#.#.....#.....#.#.....#.#  \n" +
                    "  #########.###.###.#####.#######.#                                                   #.###.###.###.###.#########.#.#.#  \n" +
                    "  #.#.......#...................#.#                                                   #.#.#.#...#.....#.#...#.....#....EP\n" +
                    "  #.#####.#.#.#####.#.#.#.#.###.###                                                   #.#.###.###.#####.#.#.#####.#.#.#  \n" +
                    "  #.......#.#...#...#.#.#.#.#.#...#                                                   #.....#...#...#.....#.......#.#.#  \n" +
                    "  #.#.#####.#.###.###.###.###.#.###                                                   #.#.#.###########.#######.#######  \n" +
                    "  #.#...#.#.#...#.#...#...#........NU                                                 #.#.#.#.#.....#.#.#.....#.#...#.#  \n" +
                    "  #####.#.#.#.#########.#.#####.###                                                   #######.#####.#.###.###.###.#.#.#  \n" +
                    "BC......#.......#...#.#.#.#...#...#                                                   #...#...#.#.....#.....#.....#....KI\n" +
                    "  ###############.###.#####.#.###.#                                                   ###.###.#.#.#.#.#####.#####.###.#  \n" +
                    "  #...........#...........#.#.#.#.#                                                   #.#.#.#.#...#.#.#.......#...#...#  \n" +
                    "  #.#######.#.#.###.#.#####.###.#.#                                                   #.#.#.#.#.#.#.###.###.#######.###  \n" +
                    "  #.......#.#...#.#.#.....#...#.#.#                                                 ZC..........#.#.......#.#...#...#.#  \n" +
                    "  #.###.#####.#.#.###.#####.###.###                                                   #.###.#.#.#####.#####.###.#####.#  \n" +
                    "  #...#...#.#.#...#.......#.#...#..RK                                                 #...#.#.#.#.#.......#.#.....#....SJ\n" +
                    "  #.#######.#.#.#######.###.###.#.#                                                   #########.#.###.#.#.#####.#.###.#  \n" +
                    "ZC....#...#.#.#...#.#.#...........#                                                 JC....#.#.#.#...#.#.#.#.#.#.#...#.#  \n" +
                    "  #######.#.#######.#.###.###.#####                                                   #.#.#.#.###.#########.#.#.###.#.#  \n" +
                    "NU......#...#...#...#...#.#.#.#.#..RE                                                 #.#...#.......#...#.#.#...#...#.#  \n" +
                    "  #.#####.###.#.#.#.#.#.###.###.#.#                                                   ###.#######.#.#.###.#.#.###.###.#  \n" +
                    "  #...........#...#...#...........#                                                   #...........#.............#.....#  \n" +
                    "  #.#.###.#.###.#####.#####.#.#.###      W         E     Z   C         I     M        ###.#.#.#.#.###.###.###.#.#.###.#  \n" +
                    "  #.#.#.#.#...#...#...#.....#.#...#      Z         K     R   G         I     Z        #...#.#.#.#.#...#...#...#.#.#...#  \n" +
                    "  ###.#.###.#####.###.#.###.#.#.#.#######.#########.#####.###.#########.#####.#########.#.#.#.#.#############.###.#####  \n" +
                    "  #.....#.#.#.#.#.#...#.#...#.#.#...#.#.#.#.#.......#.#.#...#.......#.....#...........#.#.#.#.#.....#.........#.......#  \n" +
                    "  ###.#.#.#.#.#.#####.#####.#.#######.#.#.#.#.#######.#.###.###.#####.#.#.###.#####.#####.#####.#.#####.###.#.#####.###  \n" +
                    "  #...#.#.........#...#.#...#.#.#.#.#...#...#.......#.....#...#...#...#.#.#.#.#.#.....#.....#...#.#.#...#...#.....#...#  \n" +
                    "  #.#.###.###.###.#.#.#.#####.#.#.#.###.###.#######.#.###.#.#####.###.#####.###.#.#############.#.#.#######.#.#.#.###.#  \n" +
                    "  #.#.#...#.....#.#.#.....#...#.....#.#.........#...#...#.....#...#.....#...#.#.......#...#...#.#.#.#.#.....#.#.#...#.#  \n" +
                    "  #.#####.###.#.###.###.###########.#.#.#####.#####.#.#####.###.#####.#####.#.#####.###.###.#######.#.#####.#####.#####  \n" +
                    "  #.#.....#...#.#.#.#.#.#.#.................#.#.#...#.#.#.....#.....#.....#.#.....#.......#.....#...#...#...#.......#.#  \n" +
                    "  #.###.#.#.#.###.###.#.#.#####.###.###.#######.#.#.#.#.#.###.#.#.#.#.#.###.#.###.#.#.#.###.#######.#.###.#.#.#.#.###.#  \n" +
                    "  #.#.#.#.#.#.....#.......#.......#.#...#.#.#.....#.#...#.#.#.#.#.#.#.#.#.......#...#.#.......#.........#.#.#.#.#.....#  \n" +
                    "  #.#.#########.#.###.#.#####.###.#.###.#.#.#.#.###.#.#####.#######.#.###.#.#.#.#######.#######.#####.#####.###.#.#.#.#  \n" +
                    "  #.........#...#.#...#.#.......#.#...#.#...#.#.#...#.....#.........#...#.#.#.#.....#...............#...#.....#.#.#.#.#  \n" +
                    "  #.#############.###.###.#.#######.###.#.#.#######.#.###.#####.###.#.#############.###.#.#####.#.###.#.#.#####.###.#.#  \n" +
                    "  #.....#...#.#...#.#.#.#.#.#.#.#.#.#.....#.#.#.....#...#.#.......#.#...#...#.....#...#.#.....#.#.#...#.#.#.#.....#.#.#  \n" +
                    "  #.###.#.###.#####.###.#####.#.#.#.###.#####.###.###.#####.#.###.###.#####.#.###.#.###########.###.#.###.#.###.#######  \n" +
                    "  #.#.#.....#.........#.#.........#.#...#.#...#.#.#.#.#...#.#...#...#.#.#.....#.#.......#.....#...#.#...#.....#.......#  \n" +
                    "  #.#.#.#####.#.#.###.#.#####.#.#.#.###.#.###.#.#.#.#.###.###.#####.#.#.#####.#######.###.###.#####.#.###.#######.###.#  \n" +
                    "  #...#...#...#.#.#.#.....#...#.#.#.#.....#.........#.......#.#...#.#...#.#.......#.#.......#.....#.#...#.#...#.....#.#  \n" +
                    "  #.#.#.#####.#####.#.#.#####.###.#######.###.#.###.#######.#####.#.###.#.###.#####.###.#.#############.###.###.###.#.#  \n" +
                    "  #.#.#.....#...#.....#.....#.#...#...#...#.#.#.#.#...#.......#.#...#.#.#...#.........#.#.....#.#.............#.#.#.#.#  \n" +
                    "  #.#.###.#########.#######.#####.###.###.#.#.###.#####.###.#.#.#.###.#.#.#.###.#.###.#.#.#.#.#.#.###.#####.#.#.#.#.###  \n" +
                    "  #.#.#...#.........#.#...................#.......#.......#.#.#...#.#...#.#.#...#...#.#.#.#.#.#...#.......#.#.#.#.....#  \n" +
                    "  #.#####.#####.###.#.###.###.###.#.###.###.#.###.#######.#.#####.#.#.###.#.#.###.#########.#######.###.#######.###.#.#  \n" +
                    "  #.#.....#.#...#.#.#.....#...#...#.#.....#.#...#.#.......#.#.......#...#.#.#...#.......#.#.....#.....#.#.#.#.....#.#.#  \n" +
                    "  #####.###.#.###.#.#.#.#.###.###.#.###.#####.#####.###.###########.#.#.#.#.#.###.###.#.#.#########.###.#.#.###.###.###  \n" +
                    "  #.......#...#.....#.#.#.#...#...#.#.#.#.#.....#.#.#.....#.......#.#.#...#.#...#.#...#.#.#.#...#.#.#...#...#.....#.#.#  \n" +
                    "  #.#######.###.###.#######.#####.#.#.#.#.#.###.#.#####.#.#.#######.#######.#.###########.#.###.#.###.###.#########.#.#  \n" +
                    "  #.#.....#.#.....#...#.#...#...#.#.#.....#.#.......#.#.#.....#...#.#.....#.#.....#...............#.......#.#...#.#...#  \n" +
                    "  #######.###.#########.###.#.#.#####.#.#####.###.#.#.#####.###.###.#.###.#.#.#####.#.#####.#.###.#########.#.###.#.#.#  \n" +
                    "  #.............#.......#...#.#.....#.#.#.#.....#.#.#.......#.......#...#.#.#...#.#.#.#.#...#.#.....#.#...........#.#.#  \n" +
                    "  #.#####.#####.###.###.###########.###.#.###.#.###.###.###.#.#######.#.#.#.#.###.#.###.###.#.#######.#.###.#.#.#####.#  \n" +
                    "  #...#.....#...#.....#.....................#.#...#.#.....#.#.......#.#.#...#.............#.#.............#.#.#.....#.#  \n" +
                    "  #######################################.#######.#######.#########.###.#######.#######################################  \n" +
                    "                                         B       J       R         Q   D       W                                         \n" +
                    "                                         M       B       K         M   Z       Z                                         ";
}
package y2019;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;
import lombok.Value;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;
import static java.lang.System.out;

public class Y2019D18 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            List<String> input = Resources.readLines(Resources.getResource("y2019/Y2019D18.txt"), StandardCharsets.UTF_8);

            // 1
            testAdd();
            assertThat(shortestStepsToAllKeys(example1)).isEqualTo(8);
            out.println("example 1 ok");
            assertThat(shortestStepsToAllKeys(example2)).isEqualTo(86);
            out.println("example 2 ok");
            assertThat(shortestStepsToAllKeys(example3)).isEqualTo(136);
            out.println("example 3 ok");
            assertThat(shortestStepsToAllKeys(example4)).isEqualTo(81);
            out.println("example 4 ok");

            // 4274 too high
            out.println(shortestStepsToAllKeys(input));

            // 2
        } finally {
            out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }


    private static int shortestStepsToAllKeys(List<String> input) {
        KeyMap map = new KeyMap(input);

        PriorityQueue<SearchState> searchQueue = new PriorityQueue<>();
        Map<SearchState, Integer> minDist = new HashMap<>();
        searchQueue.add(new SearchState(map.startLoc, 0, ""));

        long lastReportTimeMillis = System.currentTimeMillis();
        while (true) {
            SearchState curr = searchQueue.poll();
            if (curr.collectedKeys.length() == map.keyCount) {
                return curr.dist;
            }

            if (System.currentTimeMillis() - lastReportTimeMillis > 10000) {
                lastReportTimeMillis = System.currentTimeMillis();
                out.println("Queue len = " + searchQueue.size() + " Current node = " + curr);
            }

            map.forEachNeighbour(curr, neighbour -> {
                Integer prevDist = minDist.get(neighbour);
                if (prevDist == null || prevDist > neighbour.dist) {
                    searchQueue.add(neighbour);
                    minDist.put(neighbour, neighbour.dist);
                }
            });
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
    static class SearchState implements Comparable<SearchState> {
        Point location;
        int dist;
        String collectedKeys;

        @Override
        public int compareTo(SearchState that) {
            return Integer.compare(this.dist, that.dist);
        }
    }

    @Value
    static class ReachablePoint {
        Point location;
        char val;
        int dist; // dist from reachable key search start, not total
    }

    static class KeyMap {

        final char[][] map;
        final int keyCount;
        final Point startLoc;
        Map<Point, List<ReachablePoint>> reachableCache = new HashMap<>();

        KeyMap(List<String> lines) {
            int height = lines.size();
            int width = lines.get(0).length();
            map = new char[height][];
            for (int y = 0; y < height; y++) {
                map[y] = lines.get(y).toCharArray();
            }
            keyCount = (int) lines.stream()
                    .flatMapToInt(line -> line.chars())
                    .filter(i -> isKey((char) i))
                    .count();

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
            startLoc = entry;
        }

        public void forEachNeighbour(SearchState curr, Consumer<SearchState> callback) {

            List<ReachablePoint> reachable = reachableCache.computeIfAbsent(curr.location,
                    (p) -> {
                        ArrayList<ReachablePoint> acc = new ArrayList<>();
                        HashSet<Point> visited = new HashSet<>();
                        visited.add(p);
                        findReachable(1, new Point(p.x + 1, p.y), visited, acc);
                        findReachable(1, new Point(p.x - 1, p.y), visited, acc);
                        findReachable(1, new Point(p.x, p.y + 1), visited, acc);
                        findReachable(1, new Point(p.x, p.y - 1), visited, acc);
                        return acc;
                    });

            for (ReachablePoint reachablePoint : reachable) {
                char c = reachablePoint.val;
                if (isKey(c) && !contains(curr.collectedKeys, c)) {
                    // move to key and pick it up:
                    callback.accept(
                            new SearchState(
                                    reachablePoint.location,
                                    curr.dist + reachablePoint.dist,
                                    add(curr.collectedKeys, c)));
                } else if (isKey(c) || (isDoor(c) && contains(curr.collectedKeys, Character.toLowerCase(c)))) {
                    // move to the unlocked door or to the already collected key
                    callback.accept(
                            new SearchState(
                                    reachablePoint.location,
                                    curr.dist + reachablePoint.dist,
                                    curr.collectedKeys));
                } else if (isDoor(c)) {
                    // blocked
                } else {
                    throw new IllegalArgumentException("for " + c);
                }
            }
        }

        private void findReachable(int dist, Point location, Set<Point> visited, List<ReachablePoint> reachable) {
            if (!visited.add(location)) {
                return; // already visited
            }

            char c = map[location.y][location.x];
            if (isKey(c) || isDoor(c)) {
                reachable.add(new ReachablePoint(location, c, dist));
            } else if (c == '#') {
                return;
            } else if (c == '.' || c == '@') {
                findReachable(dist + 1, new Point(location.x + 1, location.y), visited, reachable);
                findReachable(dist + 1, new Point(location.x - 1, location.y), visited, reachable);
                findReachable(dist + 1, new Point(location.x, location.y + 1), visited, reachable);
                findReachable(dist + 1, new Point(location.x, location.y - 1), visited, reachable);
            } else {
                throw new IllegalArgumentException("for " + c);
            }
        }

        private boolean contains(String keys, char k) {
            return keys.indexOf(k) >= 0;
        }
    }

    static List<String> example1 = List.of(
            "#########",
            "#b.A.@.a#",
            "#########");

    static List<String> example2 = List.of(
            "########################",
            "#f.D.E.e.C.b.A.@.a.B.c.#",
            "######################.#",
            "#d.....................#",
            "########################");

    static List<String> example3 = List.of(
            "#################",
            "#i.G..c...e..H.p#",
            "########.########",
            "#j.A..b...f..D.o#",
            "########@########",
            "#k.E..a...g..B.n#",
            "########.########",
            "#l.F..d...h..C.m#",
            "#################");

    static List<String> example4 = List.of(
            "########################",
            "#@..............ac.GI.b#",
            "###d#e#f################",
            "###A#B#C################",
            "###g#h#i################",
            "########################"
    );
}

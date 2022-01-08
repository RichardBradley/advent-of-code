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

            testCharSetAsBits();

            // 1
            assertThat(shortestStepsToAllKeys(example1, false)).isEqualTo(8);
            out.println("example 1 ok");
            assertThat(shortestStepsToAllKeys(example2, false)).isEqualTo(86);
            out.println("example 2 ok");
            assertThat(shortestStepsToAllKeys(example3, false)).isEqualTo(136);
            out.println("example 3 ok");
            assertThat(shortestStepsToAllKeys(example4, false)).isEqualTo(81);
            out.println("example 4 ok");

            assertThat(shortestStepsToAllKeys(input, false)).isEqualTo(4250);

            // 2
            assertThat(shortestStepsToAllKeys(input, true)).isEqualTo(1640);

        } finally {
            out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }


    private static int shortestStepsToAllKeys(List<String> input, boolean part2) {
        KeyMap map = new KeyMap(input, part2);

        PriorityQueue<SearchState> searchQueue = new PriorityQueue<>();
        Map<SearchState, Integer> minDist = new HashMap<>();
        searchQueue.add(new SearchState(map.startLoc, 0, 0, map));

        long lastReportTimeMillis = System.currentTimeMillis();
        while (true) {
            SearchState curr = searchQueue.poll();
            if (curr.collectedKeys == map.allKeysAsBitSet) {
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

    static void testCharSetAsBits() {
        int set = 0;
        assertThat(set = CharSetAsBits.add(set, 'x')).isEqualTo(0x800000);
        assertThat(CharSetAsBits.contains(set, 'x')).isTrue();
        assertThat(CharSetAsBits.contains(set, 'd')).isFalse();
        assertThat(CharSetAsBits.contains(set, 'e')).isFalse();
        assertThat(set = CharSetAsBits.add(set, 'd')).isEqualTo(0x800008);
        assertThat(CharSetAsBits.contains(set, 'x')).isTrue();
        assertThat(CharSetAsBits.contains(set, 'd')).isTrue();
        assertThat(CharSetAsBits.contains(set, 'e')).isFalse();
    }

    private static boolean isKey(char c) {
        return c >= 'a' && c <= 'z';
    }

    private static boolean isDoor(char c) {
        return c >= 'A' && c <= 'Z';
    }

    @Value
    static class SearchState implements Comparable<SearchState> {
        List<Point> locations;
        int dist;
        int distPlusHeuristic;
        int collectedKeys;

        public SearchState(List<Point> locations, int dist, int collectedKeys, KeyMap keyMap) {
            this.locations = locations;
            this.dist = dist;
            this.collectedKeys = collectedKeys;

            // A* -- need to compute the heuristic: an estimate of the distance from here
            // to the goal that is not an over-estimate.
            //
            // This is very difficult here, as we may re-use the steps to reach several keys.
            // We will use min manhattan distance to any remaining key + 1 for each other
            // uncollected key.
            int uncollectedKeyCount = 0;
            int minDistToAnyKey = Integer.MAX_VALUE;
            for (Map.Entry<Character, Point> e : keyMap.keyLocations.entrySet()) {
                char key = e.getKey();
                Point keyLoc = e.getValue();
                if (!CharSetAsBits.contains(collectedKeys, key)) {
                    uncollectedKeyCount++;
                    for (Point loc : locations) {
                        minDistToAnyKey = Math.min(
                                minDistToAnyKey,
                                Math.abs(loc.x - keyLoc.x) + Math.abs(loc.y - keyLoc.y));
                    }
                }
            }
            int heuristic = minDistToAnyKey + (uncollectedKeyCount - 1);

            distPlusHeuristic = dist + heuristic;
        }

        @Override
        public int compareTo(SearchState that) {
            return Integer.compare(this.distPlusHeuristic, that.distPlusHeuristic);
        }

        @Override
        public String toString() {
            return "SearchState{" +
                    "locations=" + locations +
                    ", dist=" + dist +
                    ", distPlusHeuristic=" + distPlusHeuristic +
                    ", collectedKeys=" + CharSetAsBits.toString(collectedKeys) +
                    '}';
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
        final int allKeysAsBitSet;
        final List<Point> startLoc;
        final Map<Character, Point> keyLocations;
        final Map<Point, List<ReachablePoint>> reachableCache = new HashMap<>();

        KeyMap(List<String> lines, boolean part2) {
            int height = lines.size();
            int width = lines.get(0).length();
            map = new char[height][];
            for (int y = 0; y < height; y++) {
                map[y] = lines.get(y).toCharArray();
            }

            keyLocations = new HashMap<>();
            int allKeysAsBitSet = 0;
            Point entry = null;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    char c = map[y][x];
                    if (c == '@') {
                        checkState(entry == null);
                        entry = new Point(x, y);
                    } else if (isKey(c)) {
                        checkState(null == keyLocations.put(c, new Point(x, y)));
                        allKeysAsBitSet = CharSetAsBits.add(allKeysAsBitSet, c);
                    }
                }
            }
            checkState(entry != null);
            this.allKeysAsBitSet = allKeysAsBitSet;

            if (part2) {
                startLoc = new ArrayList<>();
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        if (2 == Math.abs(dx) + Math.abs(dy)) {
                            map[entry.y + dy][entry.x + dx] = '@';
                            startLoc.add(new Point(entry.x + dx, entry.y + dy));
                        } else {
                            map[entry.y + dy][entry.x + dx] = '#';
                        }
                    }
                }
            } else {
                startLoc = Collections.singletonList(entry);
            }
        }

        public void forEachNeighbour(SearchState curr, Consumer<SearchState> callback) {

            for (Point location : curr.locations) {
                List<ReachablePoint> reachable = reachableCache.computeIfAbsent(location,
                        (p) -> {
                            ArrayList<ReachablePoint> acc = new ArrayList<>();
                            Map<Point, Integer> bestVisitedDist = new HashMap<>();
                            bestVisitedDist.put(p, 0);
                            findReachable(1, new Point(p.x + 1, p.y), bestVisitedDist, acc);
                            findReachable(1, new Point(p.x - 1, p.y), bestVisitedDist, acc);
                            findReachable(1, new Point(p.x, p.y + 1), bestVisitedDist, acc);
                            findReachable(1, new Point(p.x, p.y - 1), bestVisitedDist, acc);
                            return acc;
                        });

                for (ReachablePoint reachablePoint : reachable) {
                    char c = reachablePoint.val;
                    if (isKey(c) && !CharSetAsBits.contains(curr.collectedKeys, c)) {
                        // move to key and pick it up:
                        callback.accept(
                                new SearchState(
                                        replace(curr.locations, location, reachablePoint.location),
                                        curr.dist + reachablePoint.dist,
                                        CharSetAsBits.add(curr.collectedKeys, c),
                                        this));
                    } else if (isKey(c) || (isDoor(c) && CharSetAsBits.contains(curr.collectedKeys, Character.toLowerCase(c)))) {
                        // move to the unlocked door or to the already collected key
                        callback.accept(
                                new SearchState(
                                        replace(curr.locations, location, reachablePoint.location),
                                        curr.dist + reachablePoint.dist,
                                        curr.collectedKeys,
                                        this));
                    } else if (isDoor(c)) {
                        // blocked
                    } else {
                        throw new IllegalArgumentException("for " + c);
                    }
                }
            }
        }

        private List<Point> replace(List<Point> locations, Point old, Point newLoc) {
            if (locations.size() == 1) {
                return Collections.singletonList(newLoc);
            } else {
                List<Point> acc = new ArrayList<>();
                for (Point location : locations) {
                    if (location != old) {
                        acc.add(location);
                    } else {
                        acc.add(newLoc);
                    }
                }
                return acc;
            }
        }

        private void findReachable(int dist, Point location, Map<Point, Integer> bestVisitedDist, List<ReachablePoint> reachable) {
            Integer prevDist = bestVisitedDist.get(location);
            if (prevDist != null && prevDist <= dist) {
                return; // already visited
            }
            bestVisitedDist.put(location, dist);

            char c = map[location.y][location.x];
            if (isKey(c) || isDoor(c)) {
                reachable.add(new ReachablePoint(location, c, dist));
            } else if (c == '#') {
                return;
            } else if (c == '.' || c == '@') {
                findReachable(dist + 1, new Point(location.x + 1, location.y), bestVisitedDist, reachable);
                findReachable(dist + 1, new Point(location.x - 1, location.y), bestVisitedDist, reachable);
                findReachable(dist + 1, new Point(location.x, location.y + 1), bestVisitedDist, reachable);
                findReachable(dist + 1, new Point(location.x, location.y - 1), bestVisitedDist, reachable);
            } else {
                throw new IllegalArgumentException("for " + c);
            }
        }


    }

    static class CharSetAsBits {
        static int[] bitMasks;

        static {
            bitMasks = new int[26];
            int b = 1;
            for (int i = 0; i < 26; i++) {
                bitMasks[i] = b;
                b <<= 1;
            }
        }

        static boolean contains(int charSet, char c) {
            return 0 != (charSet & bitMasks[c - 'a']);
        }

        public static int add(int set, char c) {
            return set | bitMasks[c - 'a'];
        }

        public static String toString(int charSet) {
            StringBuilder acc = new StringBuilder();
            for (int i = 0; i < bitMasks.length; i++) {
                if (contains(charSet, (char) ('a' + i))) {
                    acc.append((char) ('a' + i));
                }
            }
            return acc.toString();
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

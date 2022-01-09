package y2019;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Iterables;
import com.google.common.io.Resources;
import lombok.Value;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;
import static java.lang.System.out;

public class Y2019D20 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            List<String> input = Resources.readLines(Resources.getResource("y2019/Y2019D20.txt"), StandardCharsets.UTF_8);

            // 1
            assertThat(shortestPathAAtoZZ(example1, false)).isEqualTo(23);
            out.println("example 1 ok");
            assertThat(shortestPathAAtoZZ(example2, false)).isEqualTo(58);
            out.println("example 2 ok");
            out.println(shortestPathAAtoZZ(input, false));

            // 2
            out.println(shortestPathAAtoZZ(input, true));
        } finally {
            out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private static class Maze {
        final Set<Point> walkableSpaces;
        final Map<Point, Point> outerPortals;
        final Map<Point, Point> innerPortals;
        final Point aa;
        final Point zz;
        final boolean isPart2;

        public Maze(List<String> mazeSpec, boolean isPart2) {
            this.isPart2 = isPart2;
            walkableSpaces = new HashSet<>();
            Map<String, List<Point>> portalsByName = new HashMap<>();
            for (int y = 0; y < mazeSpec.size(); y++) {
                String line = mazeSpec.get(y);
                for (int x = 0; x < line.length(); x++) {
                    char c = line.charAt(x);
                    if ('.' == c) {
                        walkableSpaces.add(new Point(x, y));
                    } else if ('A' <= c && 'Z' >= c) {
                        // horiz label?
                        if (x + 1 < line.length()) {
                            char n = line.charAt(x + 1);
                            if ('A' <= n && 'Z' >= n) {
                                String label = c + "" + n;
                                Point loc = x + 2 < line.length() && '.' == line.charAt(x + 2)
                                        ? new Point(x + 2, y)
                                        : new Point(x - 1, y);
                                portalsByName.computeIfAbsent(label, v -> new ArrayList<>())
                                        .add(loc);
                            }
                        }
                        // vert label?
                        if (y + 1 < mazeSpec.size()) {
                            // IntelliJ has stripped trailing spaces from my input :-(
                            char n = x < mazeSpec.get(y + 1).length()
                                    ? mazeSpec.get(y + 1).charAt(x)
                                    : ' ';
                            if ('A' <= n && 'Z' >= n) {
                                String label = c + "" + n;
                                char nn = y + 2 < mazeSpec.size() && x < mazeSpec.get(y + 2).length()
                                        ? mazeSpec.get(y + 2).charAt(x)
                                        : ' ';
                                Point loc = '.' == nn
                                        ? new Point(x, y + 2)
                                        : new Point(x, y - 1);
                                portalsByName.computeIfAbsent(label, v -> new ArrayList<>())
                                        .add(loc);
                            }
                        }
                    }
                }
            }

            int maxWalkableX = walkableSpaces.stream().mapToInt(p -> p.x).max().getAsInt();
            int maxWalkableY = walkableSpaces.stream().mapToInt(p -> p.y).max().getAsInt();
            innerPortals = new HashMap<>();
            outerPortals = new HashMap<>();
            Point aa = null;
            Point zz = null;
            for (Map.Entry<String, List<Point>> entry : portalsByName.entrySet()) {
                String name = entry.getKey();
                List<Point> locs = entry.getValue();
                if ("AA".equals(name)) {
                    aa = Iterables.getOnlyElement(locs);
                } else if ("ZZ".equals(name)) {
                    zz = Iterables.getOnlyElement(locs);
                } else {
                    checkState(locs.size() == 2);

                    Point a = locs.get(0);
                    Point b = locs.get(1);
                    boolean aIsOuter = a.x == 2 || a.x == maxWalkableX || a.y == 2 || a.y == maxWalkableY;
                    boolean bIsOuter = b.x == 2 || b.x == maxWalkableX || b.y == 2 || b.y == maxWalkableY;
                    checkState(aIsOuter ^ bIsOuter);
                    if (aIsOuter) {
                        outerPortals.put(a, b);
                        innerPortals.put(b, a);
                    } else {
                        innerPortals.put(a, b);
                        outerPortals.put(b, a);
                    }
                }
            }
            this.aa = checkNotNull(aa);
            this.zz = checkNotNull(zz);
        }

        public void forEachNeighbour(SearchState curr, Consumer<SearchState> callback) {
            Point p = curr.location;
            Point right = new Point(p.x + 1, p.y);
            if (walkableSpaces.contains(right)) {
                callback.accept(new SearchState(right, curr.depth));
            }
            Point left = new Point(p.x - 1, p.y);
            if (walkableSpaces.contains(left)) {
                callback.accept(new SearchState(left, curr.depth));
            }
            Point down = new Point(p.x, p.y + 1);
            if (walkableSpaces.contains(down)) {
                callback.accept(new SearchState(down, curr.depth));
            }
            Point up = new Point(p.x, p.y - 1);
            if (walkableSpaces.contains(up)) {
                callback.accept(new SearchState(up, curr.depth));
            }
            if (isPart2) {
                if (curr.depth > 0) {
                    Point outer = outerPortals.get(p);
                    if (outer != null) {
                        callback.accept(new SearchState(outer, curr.depth - 1));
                    }
                }
                Point inner = innerPortals.get(p);
                if (inner != null) {
                    callback.accept(new SearchState(inner, curr.depth + 1));
                }
            } else {
                Point outer = outerPortals.get(p);
                if (outer != null) {
                    callback.accept(new SearchState(outer, 0));
                }
                Point inner = innerPortals.get(p);
                if (inner != null) {
                    callback.accept(new SearchState(inner, 0));
                }
            }
        }
    }

    private static int shortestPathAAtoZZ(List<String> mazeSpec, boolean isPart2) {
        Maze maze = new Maze(mazeSpec, isPart2);

        PriorityQueue<SearchStateWithDist> searchQueue = new PriorityQueue<>();
        Map<SearchState, Integer> minDist = new HashMap<>();
        searchQueue.add(new SearchStateWithDist(new SearchState(maze.aa, 0), 0));

        long lastReportTimeMillis = System.currentTimeMillis();
        while (true) {
            SearchStateWithDist curr = searchQueue.poll();
            if (curr.state.depth == 0 && maze.zz.equals(curr.state.location)) {
                return curr.dist;
            }

            if (System.currentTimeMillis() - lastReportTimeMillis > 10000) {
                lastReportTimeMillis = System.currentTimeMillis();
                out.println("Queue len = " + searchQueue.size() + " Current node = " + curr);
            }

            int nextDist = curr.dist + 1;
            maze.forEachNeighbour(curr.state, neighbour -> {
                Integer prevDist = minDist.get(neighbour);
                if (prevDist == null || prevDist > nextDist) {
                    searchQueue.add(new SearchStateWithDist(neighbour, nextDist));
                    minDist.put(neighbour, nextDist);
                }
            });
        }
    }

    @Value
    static class SearchStateWithDist implements Comparable<SearchStateWithDist> {
        SearchState state;
        int dist;

        @Override
        public int compareTo(SearchStateWithDist that) {
            return Integer.compare(this.dist, that.dist);
        }
    }

    @Value
    static class SearchState {
        Point location;
        int depth;
    }

    static List<String> example1 = List.of(
            "         A           ",
            "         A           ",
            "  #######.#########  ",
            "  #######.........#  ",
            "  #######.#######.#  ",
            "  #######.#######.#  ",
            "  #######.#######.#  ",
            "  #####  B    ###.#  ",
            "BC...##  C    ###.#  ",
            "  ##.##       ###.#  ",
            "  ##...DE  F  ###.#  ",
            "  #####    G  ###.#  ",
            "  #########.#####.#  ",
            "DE..#######...###.#  ",
            "  #.#########.###.#  ",
            "FG..#########.....#  ",
            "  ###########.#####  ",
            "             Z       ",
            "             Z       ");

    static List<String> example2 = List.of(
            "                   A               ",
            "                   A               ",
            "  #################.#############  ",
            "  #.#...#...................#.#.#  ",
            "  #.#.#.###.###.###.#########.#.#  ",
            "  #.#.#.......#...#.....#.#.#...#  ",
            "  #.#########.###.#####.#.#.###.#  ",
            "  #.............#.#.....#.......#  ",
            "  ###.###########.###.#####.#.#.#  ",
            "  #.....#        A   C    #.#.#.#  ",
            "  #######        S   P    #####.#  ",
            "  #.#...#                 #......VT",
            "  #.#.#.#                 #.#####  ",
            "  #...#.#               YN....#.#  ",
            "  #.###.#                 #####.#  ",
            "DI....#.#                 #.....#  ",
            "  #####.#                 #.###.#  ",
            "ZZ......#               QG....#..AS",
            "  ###.###                 #######  ",
            "JO..#.#.#                 #.....#  ",
            "  #.#.#.#                 ###.#.#  ",
            "  #...#..DI             BU....#..LF",
            "  #####.#                 #.#####  ",
            "YN......#               VT..#....QG",
            "  #.###.#                 #.###.#  ",
            "  #.#...#                 #.....#  ",
            "  ###.###    J L     J    #.#.###  ",
            "  #.....#    O F     P    #.#...#  ",
            "  #.###.#####.#.#####.#####.###.#  ",
            "  #...#.#.#...#.....#.....#.#...#  ",
            "  #.#####.###.###.#.#.#########.#  ",
            "  #...#.#.....#...#.#.#.#.....#.#  ",
            "  #.###.#####.###.###.#.#.#######  ",
            "  #.#.........#...#.............#  ",
            "  #########.###.###.#############  ",
            "           B   J   C               ",
            "           U   P   P               ");
}

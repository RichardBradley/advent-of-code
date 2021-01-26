package y2016;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;
import lombok.Value;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Queue;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2016D22 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        List<NodeInfo> input = parse(Resources.toString(Resources.getResource("y2016/Y2016D22.txt"), StandardCharsets.UTF_8));

        // 1
        System.out.println(countViablePairs(input));

        // 2
        assertThat(findShortestRouteLen(parse(exampleInput))).isEqualTo(7);
        System.out.println("example ok");

        System.out.println(findShortestRouteLen(input));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static int findShortestRouteLen(List<NodeInfo> world) {
        // Not solvable in general case.
        // See e.g. https://www.reddit.com/r/adventofcode/comments/5jry0y/2016_day_22_part_2_any_general_solution/
        //
        // If:
        //  1. There is only 1 empty disk
        //  2. There are no "viable pairs" that do not involve the empty disk
        // then we can treat this like a "15" sliding puzzle and solve more directly
        assertThat(world.stream().filter(n -> n.usedTB == 0).count()).isEqualTo(1);
        assertAllViablePairsInvolveEmptyDisk(world);

        Map<Point, NodeInfo> nodes = new HashMap<>();
        int width = -1;
        int height = -1;
        int maxFree = -1;
        for (NodeInfo nodeInfo : world) {
            checkState(null == nodes.put(new Point(nodeInfo.x, nodeInfo.y), nodeInfo));
            width = Math.max(width, nodeInfo.x + 1);
            height = Math.max(height, nodeInfo.y + 1);
            maxFree = Math.max(maxFree, nodeInfo.availTB);
        }

        String[] maze = new String[height];
        Point emptyNodePoint = null;
        for (int y = 0; y < height; y++) {
            StringBuilder line = new StringBuilder();
            for (int x = 0; x < width; x++) {
                NodeInfo node = nodes.get(new Point(x, y));
                if (x == 0 && y == 0) {
                    line.append("O");
                } else if (x == (width - 1) && y == 0) {
                    line.append("G");
                } else if (node.usedTB == 0) {
                    line.append("□");
                    emptyNodePoint = new Point(x, y);
                } else if (node.usedTB > maxFree) {
                    line.append("#");
                } else {
                    line.append(".");
                }
            }
            maze[y] = line.toString().replace('G', '.');
            System.out.println(line.toString());
        }

        // first move the gap to the top right, leaving the goal data one to its left
        int moves = distance(maze, emptyNodePoint, new Point(width - 1, 0));
        // then move the goal data to the left; each step takes 5 moves
        moves += 5 * (width - 2);

        return moves;
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

    private static int countViablePairs(List<NodeInfo> nodes) {
        int acc = 0;
        for (NodeInfo nodeA : nodes) {
            for (NodeInfo nodeB : nodes) {
                if (nodeA != nodeB) {
                    if (nodeA.usedTB != 0) {
                        if (nodeB.availTB >= nodeA.usedTB) {
                            acc++;
                        }
                    }
                }
            }
        }
        return acc;
    }

    private static void assertAllViablePairsInvolveEmptyDisk(List<NodeInfo> nodes) {
        for (NodeInfo nodeA : nodes) {
            for (NodeInfo nodeB : nodes) {
                if (nodeA != nodeB) {
                    if (nodeA.usedTB != 0) {
                        if (nodeB.availTB >= nodeA.usedTB) {
                            assertThat(nodeB.usedTB).isEqualTo(0);
                        }
                    }
                }
            }
        }
    }

    @Value
    static class NodeInfo {
        int x;
        int y;
        int sizeTB;
        int usedTB;
        int availTB;
    }

    static List<NodeInfo> parse(String input) {
        List<NodeInfo> acc = new ArrayList<>();
        Pattern nodePatt = Pattern.compile("/dev/grid/node-x(\\d+)-y(\\d+) +(\\d+)T +(\\d+)T +(\\d+)T +\\d+%");
        for (String line : Splitter.on("\n").split(input)) {
            if ("root@ebhq-gridcenter# df -h".equals(line)
                    || "Filesystem              Size  Used  Avail  Use%".equals(line)) {
                continue;
            }
            Matcher matcher = nodePatt.matcher(line);
            checkArgument(matcher.matches());
            acc.add(new NodeInfo(
                    Integer.parseInt(matcher.group(1)),
                    Integer.parseInt(matcher.group(2)),
                    Integer.parseInt(matcher.group(3)),
                    Integer.parseInt(matcher.group(4)),
                    Integer.parseInt(matcher.group(5))));
        }
        return acc;
    }

    static String exampleInput = "Filesystem              Size  Used  Avail  Use%\n" +
            "/dev/grid/node-x0-y0   10T    8T     2T   80%\n" +
            "/dev/grid/node-x0-y1   11T    6T     5T   54%\n" +
            "/dev/grid/node-x0-y2   32T   28T     4T   87%\n" +
            "/dev/grid/node-x1-y0    9T    7T     2T   77%\n" +
            "/dev/grid/node-x1-y1    8T    0T     8T    0%\n" +
            "/dev/grid/node-x1-y2   11T    7T     4T   63%\n" +
            "/dev/grid/node-x2-y0   10T    6T     4T   60%\n" +
            "/dev/grid/node-x2-y1    9T    8T     1T   88%\n" +
            "/dev/grid/node-x2-y2    9T    6T     3T   66%";
}

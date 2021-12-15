package y2021;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;
import lombok.Value;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

public class Y2021D15 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            List<String> input = Resources.readLines(Resources.getResource("y2021/Y2021D15.txt"), StandardCharsets.UTF_8);

            // 1
            assertThat(lowestPathCost(example, false)).isEqualTo(40);
            assertThat(lowestPathCost(input, false)).isEqualTo(403);

            // 2
            assertThat(lowestPathCost(example, true)).isEqualTo(315);
            assertThat(lowestPathCost(input, true)).isEqualTo(2840);

        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    static Point[] neighbourDeltas = new Point[]{
            new Point(1, 0),
            new Point(0, 1),
            new Point(-1, 0),
            new Point(0, -1)
    };

    private static long lowestPathCost(List<String> input, boolean isPart2) {
        int height = (isPart2 ? 5 : 1) * input.size();
        int width = (isPart2 ? 5 : 1) * input.get(0).length();

        Map<Point, Long> lowestCostByPoint = new HashMap<>();
        lowestCostByPoint.put(new Point(0, 0), 0L);
        PriorityQueue<SearchState> searchQueue = new PriorityQueue<>();
        searchQueue.add(new SearchState(new Point(0, 0), 0));

        SearchState curr;
        while (null != (curr = searchQueue.poll())) {
            Point location = curr.location;
            long currCost = lowestCostByPoint.get(location);

            if (location.y == height - 1 && location.x == width - 1) {
                return currCost;
            }

            for (Point delta : neighbourDeltas) {
                Point neighbour = new Point(location.x + delta.x, location.y + delta.y);
                if (neighbour.x >= 0 && neighbour.x < width
                        && neighbour.y >= 0 && neighbour.y < height) {
                    long neighbourCost = currCost + getEntryCost(neighbour.x, neighbour.y, input);

                    Long prevCost = lowestCostByPoint.get(neighbour);
                    if (prevCost == null || prevCost > neighbourCost) {
                        lowestCostByPoint.put(neighbour, neighbourCost);
                        searchQueue.add(new SearchState(neighbour, neighbourCost));
                    }
                }
            }
        }
        throw new IllegalArgumentException();
    }

    private static long getEntryCost(int x, int y, List<String> input) {
        int mapHeight = input.size();
        int mapWidth = input.get(0).length();
        int xIncr = x / mapWidth;
        int yIncr = y / mapHeight;
        int val = xIncr + yIncr + (input.get(y % mapHeight).charAt(x % mapWidth) - '0');
        return ((val - 1) % 9) + 1;
    }

    @Value
    static class SearchState implements Comparable<SearchState> {
        Point location;
        long dist;

        @Override
        public int compareTo(SearchState that) {
            return Long.compare(this.dist, that.dist);
        }
    }

    private static List<String> example = List.of(
            "1163751742",
            "1381373672",
            "2136511328",
            "3694931569",
            "7463417111",
            "1319128137",
            "1359912421",
            "3125421639",
            "1293138521",
            "2311944581"
    );
}

package y2022;

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
import java.util.stream.IntStream;

import static com.google.common.truth.Truth.assertThat;

public class Y2022D12 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        List<String> input = Resources.readLines(Resources.getResource("y2022/Y2022D12.txt"), StandardCharsets.UTF_8);

        // 1
        assertThat(part1(example)).isEqualTo(31);
        System.out.println(part1(input));

        // 2
        assertThat(part2(example)).isEqualTo(29);
        System.out.println(part2(input));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static int part1(List<String> input) {
        return run(input, false);
    }

    private static int run(List<String> input, boolean isPart2) {
        // Dijkstra:
        Map<Point, Integer> minDistToPoint = new HashMap<>();
        PriorityQueue<State> queue = new PriorityQueue<>();
        if (isPart2) {
            for (int y = 0; y < input.size(); y++) {
                String line = input.get(y);
                for (int x = 0; x < line.length(); x++) {
                    char c = line.charAt(x);
                    if ('S' == c || 'a' == c) {
                        queue.add(new State(0, 'a', x, y));
                    }
                }
            }
        } else {
            int startY = IntStream.range(0, input.size()).filter(i -> input.get(i).contains("S")).findFirst().getAsInt();
            int startX = input.get(startY).indexOf('S');
            queue.add(new State(0, 'a', startX, startY));
        }

        int targetY = IntStream.range(0, input.size()).filter(i -> input.get(i).contains("E")).findFirst().getAsInt();
        int targetX = input.get(targetY).indexOf('E');

        while (true) {
            State current = queue.poll();

            if (current.x == targetX && current.y == targetY) {
//                for (int y = 0; y < input.size(); y++) {
//                    for (int x = 0; x < input.get(0).length(); x++) {
//                        Integer i = minDistToPoint.get(new Point(x, y));
//                        System.out.print(i == null ? '.' : (char) ('a' + i));
//                    }
//                    System.out.println();
//                }

                return current.distFromStart;
            }

            for (Point dir : dirs) {
                int nextX = current.x + dir.x;
                int nextY = current.y + dir.y;
                char nextElev = get(input, nextX, nextY);
                if ((nextElev == 'E' && current.elev >= 'y')
                        || (nextElev >= 'a' && nextElev <= (current.elev + 1))) {
                    int nextDist = current.distFromStart + 1;

                    minDistToPoint.compute(new Point(nextX, nextY), (k, oldDist) -> {
                        if (oldDist == null || oldDist > nextDist) {
                            queue.add(new State(nextDist, nextElev, nextX, nextY));
                            return nextDist;
                        }
                        return oldDist;
                    });
                }
            }
        }
    }

    private static char get(List<String> input, int x, int y) {
        if (y >= 0 && y < input.size()) {
            String line = input.get(y);
            if (x >= 0 && x < line.length()) {
                return line.charAt(x);
            }
        }
        return Character.MAX_VALUE;
    }

    @Value
    static class State implements Comparable<State> {
        int distFromStart;
        char elev;
        int x;
        int y;

        @Override
        public int compareTo(State that) {
            return Integer.compare(this.distFromStart, that.distFromStart);
        }
    }

    static Point[] dirs = new Point[]{
            new Point(0, -1),
            new Point(1, 0),
            new Point(0, 1),
            new Point(-1, 0)
    };

    private static int part2(List<String> input) {
        return run(input, true);
    }

    private static List<String> example = List.of(
            "Sabqponm",
            "abcryxxl",
            "accszExk",
            "acctuvwj",
            "abdefghi");
}

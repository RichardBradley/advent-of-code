package y2022;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2022D09 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        List<String> input = Resources.readLines(Resources.getResource("y2022/Y2022D09.txt"), StandardCharsets.UTF_8);

        // 1
        assertThat(part1(example)).isEqualTo(13);
        System.out.println(part1(input));

        // 2
        assertThat(part2(example)).isEqualTo(1);
        assertThat(part2(example2)).isEqualTo(36);
        System.out.println(part2(input));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    static Map<Character, Point> dirs = ImmutableMap.of(
            'U', new Point(0, -1),
            'D', new Point(0, 1),
            'L', new Point(-1, 0),
            'R', new Point(1, 0));

    //How many positions does the tail of the rope visit at least once?
    private static int part1(List<String> input) {
        Pattern p = Pattern.compile("([UDRL]) (\\d+)");
        Point head = new Point(0, 0);
        Point tail = new Point(0, 0);
        Set<Point> tailVisited = new HashSet<>();
        tailVisited.add(tail);
        // printState(head, tail);

        for (String line : input) {
            Matcher m = p.matcher(line);
            checkState(m.matches());

            char dirC = m.group(1).charAt(0);
            Point dir = dirs.get(dirC);
            int dist = Integer.parseInt(m.group(2));

            for (int i = 0; i < dist; i++) {
                head = new Point(head.x + dir.x, head.y + dir.y);
                tail = computeNewPositionChasing(tail, head);
                tailVisited.add(tail);

                // printState(head, tail);
            }
        }

        return tailVisited.size();
    }

    private static void printState(Point head, Point tail) {
        for (int y = -6; y <= 0; y++) {
            for (int x = 0; x <= 6; x++) {
                char out = '.';
                if (x == 0 && y == 0) {
                    out = 's';
                }
                if (x == tail.x && y == tail.y) {
                    out = 'T';
                }
                if (x == head.x && y == head.y) {
                    out = 'H';
                }
                System.out.print(out);
            }
            System.out.println();
        }
        System.out.println();
    }

    //How many positions does the tail of the rope visit at least once?
    private static int part2(List<String> input) {
        Pattern p = Pattern.compile("([UDRL]) (\\d+)");
        List<Point> knots = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            knots.add(new Point(0, 0));
        }
        Set<Point> tailEndVisited = new HashSet<>();
        tailEndVisited.add(new Point(0, 0));

        for (String line : input) {
            Matcher m = p.matcher(line);
            checkState(m.matches());

            char dirC = m.group(1).charAt(0);
            Point dir = dirs.get(dirC);
            int dist = Integer.parseInt(m.group(2));

            for (int i = 0; i < dist; i++) {

                Point oldHead = knots.get(0);
                Point newHead = new Point(oldHead.x + dir.x, oldHead.y + dir.y);
                knots.set(0, newHead);

                for (int knotId = 1; knotId < knots.size(); knotId++) {
                    Point tail = knots.get(knotId);
                    Point newTail = computeNewPositionChasing(tail, knots.get(knotId - 1));
                    knots.set(knotId, newTail);
                }

                tailEndVisited.add(knots.get(knots.size() - 1));
            }
        }

        return tailEndVisited.size();
    }

    private static Point computeNewPositionChasing(Point tail, Point target) {
        int dx = target.x - tail.x;
        int dy = target.y - tail.y;

        int maxDistHeadTail = Math.max(Math.abs(dx), Math.abs(dy));
        if (maxDistHeadTail <= 1) {
            // tail does not move
            return tail;
        } else {
            // If the head is ever two steps directly up, down, left,
            // or right from the tail, the tail must also move one
            // step in that direction so it remains close enough:
            if (dx == 0 || dy == 0 && maxDistHeadTail == 2) {
                return new Point(
                        tail.x + dx / 2,
                        tail.y + dy / 2);
            } else {
                // Otherwise, if the head and tail aren't touching
                // and aren't in the same row or column, the tail
                // always moves one step diagonally to keep up:
                return new Point(
                        tail.x + Math.min(1, Math.max(-1, dx)),
                        tail.y + +Math.min(1, Math.max(-1, dy)));
            }
        }
    }


    private static List<String> example = List.of(
            "R 4",
            "U 4",
            "L 3",
            "D 1",
            "R 4",
            "D 1",
            "L 5",
            "R 2");

    private static List<String> example2 = List.of(
            "R 5",
            "U 8",
            "L 8",
            "D 3",
            "R 17",
            "D 10",
            "L 25",
            "U 20");
}

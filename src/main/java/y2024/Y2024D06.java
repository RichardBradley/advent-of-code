package y2024;

import com.google.common.base.Stopwatch;
import lombok.Value;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static aoc.Common.loadInputFromResources;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2024D06 {

    public static void main(String[] args) throws Exception {
        List<String> input = loadInputFromResources();
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(part1(example)).isEqualTo(41);
        assertThat(part1(input)).isEqualTo(4663);

        // 2
        assertThat(part2(example)).isEqualTo(6);
        assertThat(part2(input)).isEqualTo(1530);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    @Value
    static class GuardState {
        int facing;
        Point loc;
    }

    private static long part1(List<String> map) {
        Point[] dirs = new Point[]{
                new Point(0, -1), // N
                new Point(1, 0), // E
                new Point(0, 1), // S
                new Point(-1, 0) //W
        };
        int facing = 0;
        Point guardLoc = find(map, '^');

        Set<Point> visited = new HashSet<>();
        Set<GuardState> prevStates = new HashSet<>();
        visited.add(guardLoc);
        prevStates.add(new GuardState(facing, guardLoc));

        while (true) {
            Point next = add(guardLoc, dirs[facing]);
            char nextC = get(map, next);
            if (nextC == '\0') {
                break;
            } else if (nextC == '#') {
                facing = (facing + 1) % 4;
            } else {
                guardLoc = next;
                visited.add(guardLoc);
                if (!prevStates.add(new GuardState(facing, guardLoc))) {
                    return -1; // loop
                }
            }
        }

        return visited.size();
    }

    private static char get(List<String> map, Point p) {
        if (p.y < 0 || p.y >= map.size()) {
            return '\0';
        }
        String row = map.get(p.y);
        if (p.x < 0 || p.x >= row.length()) {
            return '\0';
        }
        return row.charAt(p.x);
    }

    private static Point add(Point guardLoc, Point dir) {
        return new Point(guardLoc.x + dir.x, guardLoc.y + dir.y);
    }

    private static Point find(List<String> map, char c) {
        for (int y = 0; y < map.size(); y++) {
            int x = map.get(y).indexOf(c);
            if (x >= 0) {
                return new Point(x, y);
            }
        }
        throw new IllegalArgumentException("not found");
    }

    private static long part2(List<String> map) {
        map = new ArrayList<>(map);
        int loopCount = 0;
        int width = map.get(0).length();
        for (int y = 0; y < map.size(); y++) {
            for (int x = 0; x < width; x++) {
                if ('.' == get(map, new Point(x, y))) {
                    String row = map.get(y);
                    String modRow = row.substring(0, x) + '#' + row.substring(x + 1);
                    checkState(modRow.length() == width);
                    map.set(y, modRow);

                    if (part1(map) == -1) {
                        loopCount++;
                    }
                    map.set(y, row);
                }
            }
        }
        return loopCount;
    }

    static List<String> example = List.of(
            "....#.....",
            ".........#",
            "..........",
            "..#.......",
            ".......#..",
            "..........",
            ".#..^.....",
            "........#.",
            "#.........",
            "......#...");
}

package y2022;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;
import lombok.Value;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

public class Y2022D23 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        List<String> input = Resources.readLines(Resources.getResource("y2022/Y2022D23.txt"), StandardCharsets.UTF_8);

        // 1
        assertThat(run(example, 10, false)).isEqualTo(110);
        System.out.println(run(input, 10, false));

        // 2
        assertThat(run(example, Integer.MAX_VALUE, true)).isEqualTo(20);
        System.out.println(run(input, Integer.MAX_VALUE, true));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }


    interface Dirs {
        Point N = new Point(0, -1);
        Point E = new Point(1, 0);
        Point S = new Point(0, 1);
        Point W = new Point(-1, 0);

        Point NE = add(N, E);
        Point NW = add(N, W);
        Point SE = add(S, E);
        Point SW = add(S, W);

        List<Point> allAdjacent = List.of(N, NE, E, SE, S, SW, W, NW);
    }

    @Value
    static class Consideration {
        List<Point> ifEmpty;
        Point thenMoveTo;
    }

    private static int run(List<String> input, int roundCount, boolean isPart2) {
        List<Consideration> considerations = new ArrayList<>();
        // If there is no Elf in the N, NE, or NW adjacent positions, the Elf proposes moving north one step.
        considerations.add(new Consideration(
                List.of(Dirs.N, Dirs.NE, Dirs.NW),
                Dirs.N));
        //If there is no Elf in the S, SE, or SW adjacent positions, the Elf proposes moving south one step.
        considerations.add(new Consideration(
                List.of(Dirs.S, Dirs.SE, Dirs.SW),
                Dirs.S));
        //If there is no Elf in the W, NW, or SW adjacent positions, the Elf proposes moving west one step.
        considerations.add(new Consideration(
                List.of(Dirs.W, Dirs.NW, Dirs.SW),
                Dirs.W));
        //If there is no Elf in the E, NE, or SE adjacent positions, the Elf proposes moving east one step.
        considerations.add(new Consideration(
                List.of(Dirs.E, Dirs.NE, Dirs.SE),
                Dirs.E));

        Set<Point> elves = parse(input);

        for (int round = 1; round <= roundCount; round++) {
            // During the first half of each round, each Elf considers
            // the eight positions adjacent to themself. If no other
            // Elves are in one of those eight positions, the Elf does
            // not do anything during this round. Otherwise, the Elf
            // looks in each of four directions in the following order
            // and proposes moving one step in the first valid direction:
            Map<Point, Point> proposedMoves = new HashMap<>();
            for (Point elf : elves) {
                final Set<Point> elvesCopy = elves;
                if (Dirs.allAdjacent.stream().allMatch(d -> !elvesCopy.contains(add(elf, d)))) {
                    continue;
                }
                for (Consideration consideration : considerations) {
                    if (consideration.ifEmpty.stream().allMatch(d -> !elvesCopy.contains(add(elf, d)))) {
                        proposedMoves.put(elf, add(elf, consideration.thenMoveTo));
                        break;
                    }
                }
            }
            // Simultaneously, each Elf moves to their proposed
            // destination tile if they were the only Elf to propose
            // moving to that position. If two or more Elves propose
            // moving to the same position, none of those Elves move.
            Map<Point, Integer> proposedDestCounts = new HashMap<>();
            for (Point dest : proposedMoves.values()) {
                proposedDestCounts.compute(dest, (k, val) -> val == null ? 1 : val + 1);
            }
            boolean anyMovesMade = false;
            Set<Point> nextElves = new HashSet<>();
            for (Point elf : elves) {
                Point dest = proposedMoves.get(elf);
                if (dest == null) {
                    nextElves.add(elf);
                } else if (1 == proposedDestCounts.get(dest)) {
                    nextElves.add(dest);
                    anyMovesMade = true;
                } else {
                    nextElves.add(elf);
                }
            }

            if (!anyMovesMade && isPart2) {
                return round;
            }

            elves = nextElves;
            // Finally, at the end of the round, the first direction the Elves
            // considered is moved to the end of the list of directions.
            Consideration c = considerations.remove(0);
            considerations.add(c);
        }

        // Simulate the Elves' process and find the smallest
        // rectangle that contains the Elves after 10 rounds. How
        // many empty ground tiles does that rectangle contain?
        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE, minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;
        for (Point elf : elves) {
            minX = Math.min(minX, elf.x);
            minY = Math.min(minY, elf.y);
            maxX = Math.max(maxX, elf.x);
            maxY = Math.max(maxY, elf.y);
        }
        return (maxX - minX + 1) * (maxY - minY + 1) - elves.size();
    }

    private static Set<Point> parse(List<String> input) {
        Set<Point> acc = new HashSet<>();
        for (int y = 0; y < input.size(); y++) {
            String line = input.get(y);
            for (int x = 0; x < line.length(); x++) {
                if (line.charAt(x) == '#') {
                    acc.add(new Point(x, y));
                }
            }
        }
        return acc;
    }

    static Point add(Point a, Point b) {
        return new Point(a.x + b.x, a.y + b.y);
    }

    private static List<String> example = List.of(
            "....#..",
            "..###.#",
            "#...#.#",
            ".#...##",
            "#.###..",
            "##.#.##",
            ".#..#..");
}

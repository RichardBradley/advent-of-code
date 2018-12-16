package y2018;

import com.google.common.base.Stopwatch;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.Value;

import java.awt.*;
import java.time.Instant;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;
import static java.util.stream.Collectors.toList;
import static y2018.Y2018D15.Side.Elves;
import static y2018.Y2018D15.Side.Goblins;

public class Y2018D15 {
    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        runToCompletion(movementExample1);
        runToCompletion(movementExample2);
        System.out.println(runToCompletion(combatExample.clone()));

        test1.verify();
        test2.verify();
        test3.verify();
        test4.verify();
        test5.verify();

        System.out.println(runToCompletion(input.clone()));

        // 2
        assertThat(minElfAttackPowerForNoDeaths(combatExample).minPower).isEqualTo(15);
        assertThat(minElfAttackPowerForNoDeaths(test2.input).minPower).isEqualTo(4);
        assertThat(minElfAttackPowerForNoDeaths(test3.input).minPower).isEqualTo(15);
        assertThat(minElfAttackPowerForNoDeaths(test4.input).minPower).isEqualTo(12);
        assertThat(minElfAttackPowerForNoDeaths(test5.input).minPower).isEqualTo(34);

        System.out.println("Part 2 result:");
        System.out.println(minElfAttackPowerForNoDeaths(input));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static Part2Output minElfAttackPowerForNoDeaths(World input) {
        long startingElfCount = input.units.stream().filter(u -> u.side == Elves).count();
        for (int elfPower = 4; ; elfPower++ ) {
            World world2 = input.clone();
            for (Unit unit : world2.units) {
                if (unit.side == Elves) {
                    unit.attackPower = elfPower;
                }
            }
            String outcome = runToCompletion(world2);
            if (startingElfCount == world2.units.stream().filter(u -> u.side == Elves).count()) {
                return new Part2Output(elfPower, outcome);
            }
        }
    }

    @Value
    static class Part2Output {
        int minPower;
        String outcome;
    }

    private static String runToCompletion(World world) {
        for (int completedRounds = 0; ; completedRounds++) {
//            System.out.println("After " + completedRounds + " round(s)");
//            world.printState();

            List<Unit> unitsInTurnOrder = world.findUnitsInTurnOrder();
            for (Unit unit : unitsInTurnOrder) {
                // killed units may still appear in unitsInTurnOrder
                if (unit.hitPoints > 0) {
                    // Each unit begins its turn by identifying all possible targets (enemy units).
                    // If no targets remain, combat ends.
                    if (world.units.stream().noneMatch(u -> u.side != unit.side)) {
                        Side winningSide = world.units.get(0).side;
                        world.units.forEach(u -> checkState(winningSide == u.side));
                        int totalHitPoints = world.units.stream().mapToInt(u -> u.hitPoints).sum();

                        return String.format(
                                "Combat ends after %s full rounds\n" +
                                        "%s win with %s total hit points left\n" +
                                        "Outcome: %s * %s = %s",
                                completedRounds,
                                winningSide,
                                totalHitPoints,
                                completedRounds,
                                totalHitPoints,
                                completedRounds * totalHitPoints);
                    }

                    Unit target = findAdjacentEnemy(unit, world);
                    if (target == null) {
                        Point moveTo = determineMoveToFor(unit, world);
                        if (moveTo != null) {
                            world.move(unit, moveTo);
                            target = findAdjacentEnemy(unit, world);
                        }
                    }

                    if (target != null) {
                        target.hitPoints -= unit.attackPower;
                        if (target.hitPoints <= 0) {
                            world.remove(target);
                        }
                    }
                }
            }
        }
    }

    private static Point determineMoveToFor(Unit unit, World world) {
        char enemy = unit.side == Elves ? 'G' : 'E';

        int[][] distFromStart = alloc(world.width, world.height);
        distFromStart[unit.y][unit.x] = 1;
        Set<Point> open = new HashSet<>();
        open.add(new Point(unit.x, unit.y));

        for (int steps = 2; ; steps++) {
            Set<Point> possibleTargetDestinations = new HashSet<>();
            Set<Point> nextOpenSet = new HashSet<>();
            for (Point point : open) {
                for (Point next : adjacentTo(point)) {
                    if (0 == distFromStart[next.y][next.x]) {
                        char c = world.state[next.y][next.x];
                        if (c == '.') {
                            nextOpenSet.add(next);
                            distFromStart[next.y][next.x] = steps;
                        } else if (c == enemy) {
                            // FOUND an enemy:
                            possibleTargetDestinations.add(point);
                        }
                    }
                }
            }

            if (!possibleTargetDestinations.isEmpty()) {
                Point chosenTarget = possibleTargetDestinations.stream().min(Unit.readingOrderP).get();
                // now backtrack
                return bestFirstStepTowards(unit, world, chosenTarget);
            }

            open = nextOpenSet;
            if (open.isEmpty()) {
                return null;
            }
        }
    }

    private static Point bestFirstStepTowards(Unit unit, World world, Point chosenTarget) {
        Point unitPos = new Point(unit.x, unit.y);
        int[][] distFromStart = alloc(world.width, world.height);
        distFromStart[chosenTarget.y][chosenTarget.x] = 1;
        Set<Point> open = new HashSet<>();
        open.add(chosenTarget);

        for (int steps = 2; ; steps++) {
            Set<Point> possibleSteps = new HashSet<>();
            Set<Point> nextOpenSet = new HashSet<>();
            for (Point point : open) {
                for (Point next : adjacentTo(point)) {
                    if (0 == distFromStart[next.y][next.x]) {
                        if (next.equals(unitPos)) {
                            possibleSteps.add(point);
                        } else {
                            char c = world.state[next.y][next.x];
                            if (c == '.') {
                                nextOpenSet.add(next);
                                distFromStart[next.y][next.x] = steps;
                            }
                        }
                    }
                }
            }

            if (!possibleSteps.isEmpty()) {
                return possibleSteps.stream().min(Unit.readingOrderP).get();
            }

            open = nextOpenSet;
        }
    }

    private static Point[] adjacentTo(Point point) {
        return new Point[]{
                new Point(point.x - 1, point.y),
                new Point(point.x, point.y - 1),
                new Point(point.x + 1, point.y),
                new Point(point.x, point.y + 1)};
    }

    private static int[][] alloc(int width, int height) {
        int[][] acc = new int[height][];
        for (int i = 0; i < height; i++) {
            acc[i] = new int[width];
        }
        return acc;
    }

    /**
     * the unit first determines all of the targets that are in range of it
     * by being immediately adjacent to it. If there are no such targets,
     * the unit ends its turn. Otherwise, the adjacent target with the
     * fewest hit points is selected; in a tie, the adjacent target with
     * the fewest hit points which is first in reading order is selected.
     */
    private static Unit findAdjacentEnemy(Unit unit, World world) {
        return world.units.stream()
                .filter(other -> other != unit && other.side != unit.side && other.isAdjacent(unit))
                .min(Unit.targettingOrder)
                .orElse(null);
    }

    static class World implements Cloneable {

        int width;
        int height;
        char[][] state; // indexed as Strings, [y][x]
        public List<Unit> units = new ArrayList<>();

        public static World parse(String s) {
            s = s.trim();
            World w = new World();
            String[] lines = s.split("\n");
            w.width = lines[0].length();
            w.height = lines.length;
            w.state = new char[w.height][];
            for (int y = 0; y < lines.length; y++) {
                w.state[y] = new char[w.width];
                String line = lines[y];
                checkState(line.length() == w.width);
                for (int x = 0; x < w.width; x++) {
                    char c = line.charAt(x);
                    w.state[y][x] = c;
                    if (c == 'E') {
                        w.units.add(new Unit(x, y, Elves));
                    }
                    if (c == 'G') {
                        w.units.add(new Unit(x, y, Goblins));
                    }
                }
            }
            return w;
        }

        @SneakyThrows
        public World clone() {
            World x = (World) super.clone();
            x.units = units.stream().map(Unit::clone).collect(toList());
            x.state = x.state.clone();
            for (int i = 0; i < x.state.length; i++) {
                x.state[i] = x.state[i].clone();
            }
            return x;
        }

        public List<Unit> findUnitsInTurnOrder() {
            return units.stream()
                    .sorted(Unit.readingOrder)
                    .collect(toList());
        }

        public void move(Unit unit, Point moveTo) {
            state[unit.y][unit.x] = '.';
            state[moveTo.y][moveTo.x] = (unit.side == Elves ? 'E' : 'G');
            unit.x = moveTo.x;
            unit.y = moveTo.y;
        }

        public void remove(Unit unit) {
            state[unit.y][unit.x] = '.';
            units.remove(unit);
        }

        public void printState() {
            for (int y = 0; y < state.length; y++) {
                char[] line = state[y];
                System.out.print(line);
                int yy = y;
                System.out.print("   ");
                System.out.print(units.stream()
                        .filter(u -> u.y == yy)
                        .sorted(Unit.readingOrder)
                        .map(unit -> (unit.side == Elves ? 'E' : 'G') + "(" + unit.hitPoints + ")")
                        .collect(Collectors.joining(", ")));
                System.out.println();
            }
            System.out.println();
        }
    }

    static class Example {

        World input;
        private String humanReadableExample;
        private String expectedSummary;


        public static Example parse(String humanReadableExample) {
            Example acc = new Example();
            acc.humanReadableExample = humanReadableExample;

            StringBuilder worldStr = new StringBuilder();
            int worldWidth = -1;
            String[] lines = humanReadableExample.split("\n");
            {
                Matcher m = Pattern.compile("^(#+) .*").matcher(lines[0]);
                checkState(m.matches());
                worldStr.append(m.group(1)).append("\n");
                worldWidth = m.group(1).length();
            }
            int idx = 1;
            for (; ; idx++) {
                String line = lines[idx];
                if (line.isEmpty()) {
                    break;
                }
                int spaceIdx = line.indexOf(' ');
                checkState(spaceIdx == worldWidth);
                worldStr.append(line.substring(0, spaceIdx)).append('\n');
            }
            acc.input = World.parse(worldStr.toString());

            StringBuilder summaryStr = new StringBuilder();
            for (; idx < lines.length; idx++) {
                summaryStr.append(lines[idx]).append("\n");
            }
            acc.expectedSummary = summaryStr.toString();

            return acc;
        }

        public void verify() {
            String summary = runToCompletion(input.clone());
            assertThat(summary.trim()).isEqualTo(expectedSummary.trim());
        }
    }

    static class Unit implements Cloneable {

        static Comparator<Unit> readingOrder =
                Comparator.comparing((Unit u) -> u.y).thenComparing(u -> u.x);
        static Comparator<Point> readingOrderP =
                Comparator.comparing((Point p) -> p.y).thenComparing(p -> p.x);

        public static Comparator<Unit> targettingOrder = Comparator.comparing((Unit u) -> u.hitPoints).thenComparing(readingOrder);


        public final Side side;
        int x;
        int y;
        int attackPower = 3;
        int hitPoints = 200;

        Unit(int x, int y, Side side) {
            this.x = x;
            this.y = y;
            this.side = side;
        }

        @SneakyThrows
        public Unit clone() {
            return (Unit) super.clone();
        }

        public boolean isAdjacent(Unit other) {
            int dx = Math.abs(other.x - x);
            int dy = Math.abs(other.y - y);
            return ((dx == 0 && dy == 1) || (dx == 1 && dy == 0));
        }
    }

    static enum Side {
        Elves,
        Goblins;
    }

    static World movementExample1 = World.parse(
            "#######\n" +
                    "#E..G.#\n" +
                    "#...#.#\n" +
                    "#.G.#G#\n" +
                    "#######");

    static World movementExample2 = World.parse(
            "#########\n" +
                    "#G..G..G#\n" +
                    "#.......#\n" +
                    "#.......#\n" +
                    "#G..E..G#\n" +
                    "#.......#\n" +
                    "#.......#\n" +
                    "#G..G..G#\n" +
                    "#########");

    static World combatExample = World.parse(
            "#######\n" +
                    "#.G...#\n" +
                    "#...EG#\n" +
                    "#.#.#G#\n" +
                    "#..G#E#\n" +
                    "#.....#\n" +
                    "#######");

    static Example test1 = Example.parse(
            "#######       #######\n" +
                    "#G..#E#       #...#E#   E(200)\n" +
                    "#E#E.E#       #E#...#   E(197)\n" +
                    "#G.##.#  -->  #.E##.#   E(185)\n" +
                    "#...#E#       #E..#E#   E(200), E(200)\n" +
                    "#...E.#       #.....#\n" +
                    "#######       #######\n" +
                    "\n" +
                    "Combat ends after 37 full rounds\n" +
                    "Elves win with 982 total hit points left\n" +
                    "Outcome: 37 * 982 = 36334"
    );

    static Example test2 = Example.parse(
            "#######       #######   \n" +
                    "#E..EG#       #.E.E.#   E(164), E(197)\n" +
                    "#.#G.E#       #.#E..#   E(200)\n" +
                    "#E.##E#  -->  #E.##.#   E(98)\n" +
                    "#G..#.#       #.E.#.#   E(200)\n" +
                    "#..E#.#       #...#.#   \n" +
                    "#######       #######   \n" +
                    "\n" +
                    "Combat ends after 46 full rounds\n" +
                    "Elves win with 859 total hit points left\n" +
                    "Outcome: 46 * 859 = 39514");

    static Example test3 = Example.parse(
            "#######       #######   \n" +
                    "#E.G#.#       #G.G#.#   G(200), G(98)\n" +
                    "#.#G..#       #.#G..#   G(200)\n" +
                    "#G.#.G#  -->  #..#..#   \n" +
                    "#G..#.#       #...#G#   G(95)\n" +
                    "#...E.#       #...G.#   G(200)\n" +
                    "#######       #######   \n" +
                    "\n" +
                    "Combat ends after 35 full rounds\n" +
                    "Goblins win with 793 total hit points left\n" +
                    "Outcome: 35 * 793 = 27755");

    static Example test4 = Example.parse(
            "#######       #######   \n" +
                    "#.E...#       #.....#   \n" +
                    "#.#..G#       #.#G..#   G(200)\n" +
                    "#.###.#  -->  #.###.#   \n" +
                    "#E#G#G#       #.#.#.#   \n" +
                    "#...#G#       #G.G#G#   G(98), G(38), G(200)\n" +
                    "#######       #######   \n" +
                    "\n" +
                    "Combat ends after 54 full rounds\n" +
                    "Goblins win with 536 total hit points left\n" +
                    "Outcome: 54 * 536 = 28944");

    static Example test5 = Example.parse(
            "#########       #########   \n" +
                    "#G......#       #.G.....#   G(137)\n" +
                    "#.E.#...#       #G.G#...#   G(200), G(200)\n" +
                    "#..##..G#       #.G##...#   G(200)\n" +
                    "#...##..#  -->  #...##..#   \n" +
                    "#...#...#       #.G.#...#   G(200)\n" +
                    "#.G...G.#       #.......#   \n" +
                    "#.....G.#       #.......#   \n" +
                    "#########       #########   \n" +
                    "\n" +
                    "Combat ends after 20 full rounds\n" +
                    "Goblins win with 937 total hit points left\n" +
                    "Outcome: 20 * 937 = 18740");

    static World input = World.parse("################################\n" +
            "#######.G...####################\n" +
            "#########...####################\n" +
            "#########.G.####################\n" +
            "#########.######################\n" +
            "#########.######################\n" +
            "#########G######################\n" +
            "#########.#...##################\n" +
            "#########.....#..###############\n" +
            "########...G....###.....########\n" +
            "#######............G....########\n" +
            "#######G....G.....G....#########\n" +
            "######..G.....#####..G...#######\n" +
            "######...G...#######......######\n" +
            "#####.......#########....G..E###\n" +
            "#####.####..#########G...#....##\n" +
            "####..####..#########..G....E..#\n" +
            "#####.####G.#########...E...E.##\n" +
            "#########.E.#########.........##\n" +
            "#####........#######.E........##\n" +
            "######........#####...##...#..##\n" +
            "###...................####.##.##\n" +
            "###.............#########..#####\n" +
            "#G#.#.....E.....#########..#####\n" +
            "#...#...#......##########.######\n" +
            "#.G............#########.E#E####\n" +
            "#..............##########...####\n" +
            "##..#..........##########.E#####\n" +
            "#..#G..G......###########.######\n" +
            "#.G.#..........#################\n" +
            "#...#..#.......#################\n" +
            "################################");
}

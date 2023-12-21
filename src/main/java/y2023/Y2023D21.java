package y2023;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import lombok.Value;
import org.apache.commons.math3.util.Pair;
import scala.math.Ordering;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;
import static java.lang.System.in;
import static java.lang.System.out;

public class Y2023D21 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            List<String> input = Resources.readLines(Resources.getResource("y2023/Y2023D21.txt"), StandardCharsets.UTF_8);

            System.out.printf("Example is %s x %s\n", example.get(0).length(), example.size());
            System.out.printf("input is %s x %s\n", input.get(0).length(), input.size());

            // 1
            assertThat(part1(example, 6)).isEqualTo(16);
            assertThat(part1(input, 64)).isEqualTo(3598);

            // 2
            assertThat(new Part2c(input).countReachablePlots(26501365)).isEqualTo(601441063166538L);

//            assertThat(new Part2b(input).countReachablePlots(26501365)).isEqualTo(0);
//
////            assertThat(new Part2b(example).countReachablePlots(6)).isEqualTo(16);
//            assertThat(part1(exampleTripled, 10)).isEqualTo(50);
//            assertThat(new Part2b(example).countReachablePlots(10)).isEqualTo(50);
//            System.out.printf("Example 10 OK " +  sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
//            assertThat(new Part2b(example).countReachablePlots(500)).isEqualTo(167004);
//            System.out.printf("Example 500 OK " +  sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
//            assertThat(new Part2b(example).countReachablePlots(1000)).isEqualTo(668697);
//            System.out.printf("Example 1000 OK " +  sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
//            assertThat(new Part2b(example).countReachablePlots(5000)).isEqualTo(16733044);
//            System.out.printf("Example 5000 OK " +  sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
//            assertThat(new Part2b(input).countReachablePlots(26501365)).isEqualTo(0);

//            assertThat(new Part2(example).run(500)).isEqualTo(167004);
//            assertThat(new Part2(example).run(5000)).isEqualTo(16733044);
//            assertThat(new Part2(input).run(26501365)).isEqualTo(0);

        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    enum Dirs {
        N, E, S, W;

        public Point advance(Point p) {
            switch (this) {
                case N:
                    return new Point(p.x, p.y - 1);
                case E:
                    return new Point(p.x + 1, p.y);
                case S:
                    return new Point(p.x, p.y + 1);
                case W:
                    return new Point(p.x - 1, p.y);
                default:
                    throw new IllegalStateException();
            }
        }
    }

    private static char get(List<String> input, Point p) {
        if (p.y < 0 || p.y >= input.size()) {
            return 'X';
        }
        String line = input.get(p.y);
        if (p.x < 0 || p.x >= line.length()) {
            return 'X';
        }
        char c = line.charAt(p.x);
        return c == 'S' ? '.' : c;
    }

    static long part1(List<String> input, int stepCount) {
        Set<Point> possiblePoints = new HashSet<>();
        for (int y = 0; y < input.size(); y++) {
            int x = input.get(y).indexOf('S');
            if (x >= 0) {
                possiblePoints.add(new Point(x, y));
            }
        }
        checkState(possiblePoints.size() == 1);

        for (int step = 0; step < stepCount; step++) {
            Set<Point> nextPoints = new HashSet<>();
            for (Point p : possiblePoints) {
                for (Dirs dir : Dirs.values()) {
                    Point n = dir.advance(p);
                    if ('.' == get(input, n)) {
                        nextPoints.add(n);
                    }
                }
            }
            possiblePoints = nextPoints;
        }

        //  printMap(input, possiblePoints);

        return possiblePoints.size();
    }

    private static void printMap(List<String> map, Set<Point> possiblePoints) {
        for (int y = 0; y < map.size(); y++) {
            String row = map.get(y);
            for (int x = 0; x < row.length(); x++) {
                if (possiblePoints.contains(new Point(x, y))) {
                    System.out.print('O');
                } else {
                    System.out.print(row.charAt(x));
                }
            }
            System.out.println();
        }
    }

    private static int mod(int a, int m) {
        int ret = a % m;
        if (ret < 0) {
            ret += m;
        }
        return ret;
    }

    static class Part2c {

        private final int height;
        private final int width;
        private final List<String> map;
        private final Point start;

        public Part2c(List<String> map) {
            this.map = map;
            height = map.size();
            width = map.get(0).length();
            Point start = null;
            for (int y = 0; y < map.size(); y++) {
                int x = map.get(y).indexOf('S');
                if (x >= 0) {
                    start = new Point(x, y);
                }
            }
            this.start = start;
        }

        public long countReachablePlots(int stepCount) {
            // Some total bullshit
            // https://github.com/villuna/aoc23/wiki/A-Geometric-solution-to-advent-of-code-2023,-day-21
            assertThat(width).isEqualTo(height);

            long targetN = (stepCount - 65) / (long) height;
            assertThat(height * targetN + 65).isEqualTo(stepCount);

            int x1 = 3;
            double y1 = countSquaresByDirectSimulation(x1 * height + 65);
            int x2 = 4;
            double y2 = countSquaresByDirectSimulation(x2 * height + 65);
            int x3 = 5;
            double y3 = countSquaresByDirectSimulation(x3 * height + 65);

            // https://math.stackexchange.com/questions/680646/get-polynomial-function-from-3-points
            double a = (x1 * (y3 - y2) + x2 * (y1 - y3) + x3 * (y2 - y1))
                    / ((x1 - x2) * (x1 - x3) * (x2 - x3));
            double b = (y2 - y1) / (x2 - x1) - a * (x1 + x2);
            double c = y1 - a * x1 * x1 - b * x1;

            assertThat(a * x1 * x1 + b * x1 + c).isEqualTo(y1);
            assertThat(a * x2 * x2 + b * x2 + c).isEqualTo(y2);
            assertThat(a * x3 * x3 + b * x3 + c).isEqualTo(y3);

            long ret = (long) (a * targetN * targetN + b * targetN + c);
            return ret;
        }

        private char getTiled(Point p) {
            String row = map.get(mod(p.y, height));
            char c = row.charAt(mod(p.x, width));
            return c == 'S' ? '.' : c;
        }

        long countSquaresByDirectSimulation(int stepCount) {
            Set<Point> possiblePoints = new HashSet<>();
            possiblePoints.add(start);

            for (int step = 0; step < stepCount; step++) {
                Set<Point> nextPoints = new HashSet<>();
                for (Point p : possiblePoints) {
                    for (Dirs dir : Dirs.values()) {
                        Point n = dir.advance(p);
                        if ('.' == getTiled(n)) {
                            nextPoints.add(n);
                        }
                    }
                }
                possiblePoints = nextPoints;
            }

            //  printMap(input, possiblePoints);

            return possiblePoints.size();
        }
    }

//
//    static class Part2b {
//        private final int height;
//        private final int width;
//        private final List<String> map;
//        private final Point start;
//
//        public Part2b(List<String> map) {
//            this.map = map;
//            height = map.size();
//            width = map.get(0).length();
//            Point start = null;
//            for (int y = 0; y < map.size(); y++) {
//                int x = map.get(y).indexOf('S');
//                if (x >= 0) {
//                    start = new Point(x, y);
//                }
//            }
//            this.start = start;
//        }
//
//        private char get(Point p) {
//            if (p.y < 0 || p.y >= map.size()) {
//                return 'X';
//            }
//            String line = map.get(p.y);
//            if (p.x < 0 || p.x >= line.length()) {
//                return 'X';
//            }
//            char c = line.charAt(p.x);
//            return c == 'S' ? '.' : c;
//        }
//
//        private char getTiled(Point p) {
//            String row = map.get(mod(p.y, height));
//            char c = row.charAt(mod(p.x, width));
//            return c == 'S' ? '.' : c;
//        }
//
//        private Point pToGardenCoord(Point p) {
//            return new Point(p.x / width, p.y / height);
//        }
//
//        private Point pToModCoord(Point p) {
//            return new Point(mod(p.x, width), mod(p.y, height));
//        }
//
//        @Value
//        static class GardenEntry implements Comparable<GardenEntry> {
//            Point gardenId;
//            int stepCountRemaining;
//            Map<Point, Integer> starts;
//
//            @Override
//            public int compareTo(GardenEntry o) {
//                return -Integer.compare(this.stepCountRemaining, o.stepCountRemaining);
//            }
//        }
//
//        long countReachablePlots(int targetStepCount) {
//
//            // A garden is a whole copy of the map
//            // This point is per-garden coords
//            Set<Point> visitedGardens = new HashSet<>();
//            long plotCount = 0;
//
//            PriorityQueue<GardenEntry> toVisitQueue = new PriorityQueue<>();
//            toVisitQueue.add(new GardenEntry(new Point(0, 0), targetStepCount, Map.of(start, 0)));
//            GardenEntry curr;
//            long lastReportTimeMillis = 0;
//            while (null != (curr = toVisitQueue.poll())) {
//                if (visitedGardens.add(curr.gardenId)) {
//                    FillResult result = fillSingleGarden(curr.starts, curr.stepCountRemaining);
//                    plotCount += result.plotCount;
////                    System.out.printf("Filled garden %s, found %s, new total %s, steps remaining %s, exits %s\n",
////                            curr.gardenId, result.plotCount, plotCount, curr.stepCountRemaining, result.exits);
//                    for (Map.Entry<Dirs, Map<Point, Integer>> entry : result.exits.entrySet()) {
//                        Point nextGardenId = entry.getKey().advance(curr.gardenId);
//                        int minStepToNext = entry.getValue().values().stream().mapToInt(i -> i).min().getAsInt();
//                        checkState(minStepToNext > 0);
//                        int nextStepCountRemaining = curr.stepCountRemaining - minStepToNext;
//                        Map<Point, Integer> nextEntryPoints = entry.getValue().entrySet().stream().collect(Collectors.toMap(
//                                e -> e.getKey(),
//                                e -> e.getValue() - minStepToNext));
//                        toVisitQueue.add(new GardenEntry(nextGardenId, nextStepCountRemaining, nextEntryPoints));
//                    }
//                }
//
//                if (System.currentTimeMillis() - lastReportTimeMillis > 10000) {
//                    lastReportTimeMillis = System.currentTimeMillis();
//                    out.printf("visited = %s plotCount = %s stepCountRemaining = %s \n",
//                            visitedGardens.size(),
//                            plotCount,
//                            curr.stepCountRemaining);
//                }
//            }
//            return plotCount;
//        }
//
//        @Value
//        static class FillResult {
//            int plotCount;
//            int minStepToNext;
//            // All exits to each adjacent garden, paired with the (distance - minStepToNext)
//            // In that garden's coords
//            // Need to track all exits, not just best one, because there
//            // is in fact a multiple exit that matters to the left of the starting
//            // grid, as shown by the "In exactly 10 steps" example
//            Map<Dirs, Map<Point, Integer>> exits;
//        }
//
//        Map<Pair<Map<Point, Integer>, Boolean>, FillResult> fillCacheByStartsAndIsEven = new HashMap<>();
//
//        private FillResult fillSingleGarden(Map<Point, Integer> starts, int stepCountRemaining) {
//            // coords in here is relative to the current garden
//
//            boolean isEven = (stepCountRemaining % 2) == 0;
//            FillResult fillCacheEntry = fillCacheByStartsAndIsEven.get(new Pair(starts, isEven));
//            if (fillCacheEntry != null && stepCountRemaining > fillCacheEntry.minStepToNext) {
////                System.out.println("cache hit");
//                return fillCacheEntry;
//            }
//            System.out.println("cache miss");
//
//            int maxStartOffset = starts.values().stream().mapToInt(i -> i).max().getAsInt();
//
//            Set<Point> visited = new HashSet<>();
//            Queue<Point> toVisit = new ArrayDeque<>();
//            Queue<Point> toVisitNext = new ArrayDeque<>();
//        //    Set<Point> qqPlots = new HashSet<>();
//            int plotCount = 0;
//            Map<Dirs, Map<Point, Integer>> exits = new HashMap<>();
//            for (int steps = 0; steps <= stepCountRemaining; steps++) {
//                // add in starts for this step
//                if (steps <= maxStartOffset) {
//                    for (Map.Entry<Point, Integer> e : starts.entrySet()) {
//                        if (e.getValue() == steps) {
//                            toVisit.add(e.getKey());
//                        }
//                    }
//                }
//
//                Point next;
//                while (null != (next = toVisit.poll())) {
//                    if (next.x < 0) {
//                        addExitIfBest(exits, Dirs.W, steps, new Point(next.x + width, next.y));
////                        // exit W
////                        exits.putIfAbsent(Dirs.W, new Pair<>(, steps));
//                    } else if (next.x >= width) {
//                        addExitIfBest(exits, Dirs.E, steps, new Point(next.x - width, next.y));
////                        // exit E
////                        exits.putIfAbsent(Dirs.E, new Pair<>(new Point(next.x - width, next.y), steps));
//                    } else if (next.y < 0) {
//                        addExitIfBest(exits, Dirs.N, steps, new Point(next.x, next.y + height));
////                        exits.putIfAbsent(Dirs.N, new Pair<>(new Point(next.x, next.y + height), steps));
//                    } else if (next.y >= height) {
//                        addExitIfBest(exits, Dirs.S, steps, new Point(next.x, next.y - height));
////                        exits.putIfAbsent(Dirs.S, new Pair<>(new Point(next.x, next.y - height), steps));
//                    } else {
//                        char terrain = map.get(next.y).charAt(next.x);
//                        if (('.' == terrain || 'S' == terrain) && visited.add(next)) {
//                            if ((stepCountRemaining - steps) % 2 == 0) {
//                                plotCount++;
//                           //     qqPlots.add(next);
//                            }
//                            for (Dirs dir : Dirs.values()) {
//                                toVisitNext.add(dir.advance(next));
//                            }
//                        }
//                    }
//                }
//                if (toVisitNext.isEmpty()) {
//                    break;
//                }
//                Queue<Point> tmp = toVisit;
//                toVisit = toVisitNext;
//                toVisitNext = tmp;
//            }
//
//            int minStepToNext = exits.values().stream()
//                    .flatMap(m -> m.values().stream())
//                    .mapToInt(i -> i).max().orElseGet(() -> 0);
//
//            FillResult result = new FillResult(
//                    plotCount,
//                    minStepToNext,
//                            exits.entrySet().stream().collect(Collectors.toMap(
//                                    k -> k.getKey(),
//                                    v -> v.getValue() - minStepToNext)));
//
//            // cacheable if all sides were reached
//            if (exits.size() == 4) {
//                ;
//                fillCacheByStartsAndIsEven.put(new Pair(starts, isEven),
//                        new FillResult(minStepCount, result));
//            }
//
//         //   printMap(map, qqPlots);
//
//            return result;
//        }
//
//        private void addExitIfBest(Map<Dirs, Map<Point, Integer>> exits, Dirs dir, int steps, Point p) {
//            // We don't want to add redundant starts (e.g. time = n, offset = n)
//            // but we do annoyingly need to track multiple independent starts to a garden
//            Map<Point, Integer> prev = exits.get(dir);
//            if (prev == null) {
//                HashMap<Point, Integer> m = new HashMap<>();
//                m.put(p, steps);
//                exits.put(dir, m);
//            } else {
//                for (Map.Entry<Point, Integer> entry : prev.entrySet()) {
//                    int dist = Math.abs(entry.getKey().x - p.x) + Math.abs(entry.getKey().y - p.y);
//                    int dt = steps - entry.getValue();
//                    if (dist == dt) {
//                  //      System.out.printf("Discarding redundant exit %s %s %s\n",steps, p, entry);
//                        return;
//                    }
//                }
//                prev.put(p, steps);
//            }
//        }
//    }

    static class Part2 {

        private final int height;
        private final int width;
        private final List<String> map;

        public Part2(List<String> map) {
            this.map = map;
            height = map.size();
            width = map.get(0).length();

            groupingTests();
        }


        private void groupingTests() {
            Set<Point> possiblePoints = Set.of(
                    new Point(0, 0),
                    new Point(1, 0),
                    new Point(0 + width, 0),
                    new Point(2 + width, 0));

            Map<Point, Set<Point>> tiledPoints = possiblePoints.stream().collect(Collectors.groupingBy(
                    p -> pToTileCoord(p),
                    Collectors.mapping(this::pToModCoord, Collectors.toSet())));

            System.out.print(tiledPoints);
        }

        private char getTiled(Point p) {
            String row = map.get(mod(p.y, height));
            char c = row.charAt(mod(p.x, width));
            return c == 'S' ? '.' : c;
        }

        private Point pToTileCoord(Point p) {
            return new Point(p.x / width, p.y / height);
        }

        private Point pToModCoord(Point p) {
            return new Point(mod(p.x, width), mod(p.y, height));
        }

        long run(int targetStepCount) {

            Set<Point> possiblePoints = new HashSet<>();
            for (int y = 0; y < height; y++) {
                int x = map.get(y).indexOf('S');
                if (x >= 0) {
                    possiblePoints.add(new Point(x, y));
                }
            }
            checkState(possiblePoints.size() == 1);

            Map<Set<Point>, Integer> historyToStep = new HashMap<>();
            Map<Integer, Set<Point>> historyByStep = new HashMap<>();
            for (int step = 1; step <= targetStepCount; step++) {
                Set<Point> nextPoints = new HashSet<>();
                for (Point p : possiblePoints) {
                    for (Dirs dir : Dirs.values()) {
                        Point n = dir.advance(p);
                        if ('.' == getTiled(n)) {
                            nextPoints.add(n);
                        }
                    }
                }

                possiblePoints = nextPoints;

                Integer prevSeen = historyToStep.put(possiblePoints, step);
                historyByStep.put(step, possiblePoints);

                if (step >= 96 && step <= 100) {
                    System.out.printf("At step=%s, four central tiles:\n", step);
                    for (int y = 0; y <= 2 * height; y++) {
                        for (int x = 0; x <= 2 * height; x++) {
                            Point p = new Point(x, y);
                            System.out.print(possiblePoints.contains(p) ? 'O' : getTiled(p));
                        }
                        System.out.println();
                    }
                }

                Set<Point> hundredEvenTile = null;
                Set<Point> hundredOddTile = null;
                if (step == 100) {
                    System.out.printf("At step=100, by tiles:\n");

                    Map<Point, Set<Point>> tiledPoints = possiblePoints.stream().collect(Collectors.groupingBy(
                            p -> pToTileCoord(p),
                            Collectors.mapping(this::pToModCoord, Collectors.toSet())));

                    Map<Point, Set<Point>> tiledPoints2 = new HashMap<>();
                    for (Point p : possiblePoints) {
                        Set<Point> s = tiledPoints2.computeIfAbsent(pToTileCoord(p), (k) -> new HashSet<>());
                        s.add(pToModCoord(p));
                    }

                    assertThat(tiledPoints).isEqualTo(tiledPoints2);

                    int minX = tiledPoints.keySet().stream().mapToInt(p -> p.x).min().getAsInt();
                    int maxX = tiledPoints.keySet().stream().mapToInt(p -> p.x).max().getAsInt();
                    int minY = tiledPoints.keySet().stream().mapToInt(p -> p.y).min().getAsInt();
                    int maxY = tiledPoints.keySet().stream().mapToInt(p -> p.y).max().getAsInt();

                    Set<Point> zeroTile = tiledPoints.get(new Point(0, 0));
                    hundredEvenTile = zeroTile;
                    Set<Point> oneTile = tiledPoints.get(new Point(1, 0));
                    hundredOddTile = oneTile;

                    for (int y = minY; y <= maxY; y++) {
                        for (int x = minX; x <= maxX; x++) {
                            Set<Point> thisTile = tiledPoints.getOrDefault(new Point(x, y), Collections.emptySet());
                            if (thisTile.isEmpty()) {
                                System.out.print(' ');
                            } else if (thisTile.equals(zeroTile)) {
                                System.out.print('0');
                            } else if (thisTile.equals(oneTile)) {
                                System.out.print('1');
                            } else {
                                System.out.print('*');
                            }
                        }
                        System.out.println();
                    }

                    Comparator<Point> cmp = Comparator.comparing((Point p) -> p.y).thenComparing(p -> p.x);
                    TreeSet<Point> oneOne = new TreeSet<>(cmp);
                    oneOne.addAll(tiledPoints.get(new Point(1, 1)));
                    TreeSet<Point> zero = new TreeSet<>(cmp);
                    zero.addAll(zeroTile);
                    assertThat(oneOne).isEqualTo(zero);

//                    Set<Point> zeroGardenPoints = possiblePoints.stream()
//                            .filter(p -> p.x >= 0 && p.x < width && p.y >= 0 && p.y < height)
//                            .collect(Collectors.toSet());
                }

                if (step == 101) {
                    System.out.printf("At step=101, by tiles:\n");

                    Map<Point, Set<Point>> tiledPoints = possiblePoints.stream().collect(Collectors.groupingBy(
                            p -> pToTileCoord(p),
                            Collectors.mapping(this::pToModCoord, Collectors.toSet())));

                    int minX = tiledPoints.keySet().stream().mapToInt(p -> p.x).min().getAsInt();
                    int maxX = tiledPoints.keySet().stream().mapToInt(p -> p.x).max().getAsInt();
                    int minY = tiledPoints.keySet().stream().mapToInt(p -> p.y).min().getAsInt();
                    int maxY = tiledPoints.keySet().stream().mapToInt(p -> p.y).max().getAsInt();

                    Set<Point> zeroTile = tiledPoints.get(new Point(0, 0));
                    Set<Point> oneTile = tiledPoints.get(new Point(1, 0));

                    for (int y = minY; y <= maxY; y++) {
                        for (int x = minX; x <= maxX; x++) {
                            Set<Point> thisTile = tiledPoints.getOrDefault(new Point(x, y), Collections.emptySet());
                            if (thisTile.isEmpty()) {
                                System.out.print(' ');
                            } else if (thisTile.equals(zeroTile)) {
                                System.out.print('0');
                            } else if (thisTile.equals(oneTile)) {
                                System.out.print('1');
                            } else {
                                System.out.print('*');
                            }
                        }
                        System.out.println();
                    }

                    System.out.printf("zeroTile == hundredEvenTile: %s\n", zeroTile.equals(hundredEvenTile));
                    System.out.printf("oneTile == hundredEvenTile: %s\n", oneTile.equals(hundredEvenTile));
                    System.out.printf("zeroTile == hundredOddTile: %s\n", zeroTile.equals(hundredOddTile));
                    System.out.printf("oneTile == hundredOddTile: %s\n", oneTile.equals(hundredOddTile));


//                    Set<Point> zeroGardenPoints = possiblePoints.stream()
//                            .filter(p -> p.x >= 0 && p.x < width && p.y >= 0 && p.y < height)
//                            .collect(Collectors.toSet());
                }


//                if (step == 100 && false) {
//                    int height = input.size();
//                    int width = input.get(0).length();
//
//                    Map<Point, Integer> thisTiledCounts = historyByStep.get(step)
//                            .stream()
//                            .map(p -> new Point(mod(p.x, width), mod(p.y, height)))
//                            .collect(Collectors.toMap(p -> p, p -> 1, (a, b) -> a + b));
//
//                    for (int i = 1; i < 100; i++) {
//
//                        Map<Point, Integer> tiledCounts = historyByStep.get(step - i)
//                                .stream()
//                                .map(p -> new Point(mod(p.x, width), mod(p.y, height)))
//                                .collect(Collectors.toMap(p -> p, p -> 1, (a, b) -> a + b));
//
//                        Map<Integer, Integer> diffCountsByDiff = new TreeMap<>();
//                        for (int y = 0; y < height; y++) {
//                            for (int x = 0; x < width; x++) {
//                                int diff = thisTiledCounts.getOrDefault(new Point(x, y), 0)
//                                        - tiledCounts.getOrDefault(new Point(x, y), 0);
//
//                                diffCountsByDiff.compute(diff, (k, v) -> (v == null ? 0 : v) + 1);
//
////                            System.out.printf("%04d ",
////                                    thisTiledCounts.getOrDefault(new Point(x, y), 0)
////                                    - tiledCounts.getOrDefault(new Point(x, y), 0));
//                            }
////                        System.out.println();
//                        }
//                        System.out.printf("For %s steps ago %s\n", i, diffCountsByDiff);
//
//                    }
//
//                    return -1;
//                }


                if (prevSeen != null) {
                    int cycleLen = step - prevSeen;
                    int loopCount = (targetStepCount - step) / cycleLen;
                    System.out.println("Skipping " + (cycleLen * loopCount));
                    step += cycleLen * loopCount;
                }
            }

            return possiblePoints.size();
        }
    }

    static List<String> example = List.of(
            "...........",
            ".....###.#.",
            ".###.##..#.",
            "..#.#...#..",
            "....#.#....",
            ".##..S####.",
            ".##..#...#.",
            ".......##..",
            ".##.#.####.",
            ".##..##.##.",
            "..........."
    );

    static List<String> exampleTripled = List.of(
            ".................................",
            ".....###.#......###.#......###.#.",
            ".###.##..#..###.##..#..###.##..#.",
            "..#.#...#....#.#...#....#.#...#..",
            "....#.#........#.#........#.#....",
            ".##...####..##...####..##...####.",
            ".##..#...#..##..#...#..##..#...#.",
            ".......##.........##.........##..",
            ".##.#.####..##.#.####..##.#.####.",
            ".##..##.##..##..##.##..##..##.##.",
            ".................................",
            ".................................",
            ".....###.#......###.#......###.#.",
            ".###.##..#..###.##..#..###.##..#.",
            "..#.#...#....#.#...#....#.#...#..",
            "....#.#........#.#........#.#....",
            ".##...####..##..S####..##...####.",
            ".##..#...#..##..#...#..##..#...#.",
            ".......##.........##.........##..",
            ".##.#.####..##.#.####..##.#.####.",
            ".##..##.##..##..##.##..##..##.##.",
            ".................................",
            ".................................",
            ".....###.#......###.#......###.#.",
            ".###.##..#..###.##..#..###.##..#.",
            "..#.#...#....#.#...#....#.#...#..",
            "....#.#........#.#........#.#....",
            ".##...####..##...####..##...####.",
            ".##..#...#..##..#...#..##..#...#.",
            ".......##.........##.........##..",
            ".##.#.####..##.#.####..##.#.####.",
            ".##..##.##..##..##.##..##..##.##.",
            "................................."
    );
}

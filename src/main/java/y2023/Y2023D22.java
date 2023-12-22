package y2023;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.io.Resources;
import lombok.Value;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2023D22 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            List<String> input = Resources.readLines(Resources.getResource("y2023/Y2023D22.txt"), StandardCharsets.UTF_8);

            rectTests();

            // 1
            assertThat(part1(example)).isEqualTo(5);
            assertThat(part1(input)).isEqualTo(482);

            // 2
            assertThat(part2(example)).isEqualTo(7);
            assertThat(part2(input)).isEqualTo(103010);


        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private static void rectTests() {
        Rect3 a = new Rect3(new Point3(1, 2, 3), new Point3(4, 5, 6));
        assertThat(a.intersectsXY(new Rect3(new Point3(4, 5, 99), new Point3(9, 9, 99)))).isTrue();
        assertThat(a.intersectsXY(new Rect3(new Point3(0, 0, 99), new Point3(9, 9, 99)))).isTrue();
        assertThat(a.intersectsXY(new Rect3(new Point3(5, 6, 99), new Point3(7, 8, 99)))).isFalse();
    }

    @Value
    static class Point3 {
        int x;
        int y;
        int z;
    }

    @Value
    static class Rect3 {
        Point3 a;
        Point3 b;

        public boolean intersectsXY(Rect3 other) {
            return this.a.x <= other.b.x && this.b.x >= other.a.x &&
                    this.b.y >= other.a.y && this.a.y <= other.b.y;
        }

        public Rect3 moveDown(int fallDist) {
            return new Rect3(
                    new Point3(a.x, a.y, a.z - fallDist),
                    new Point3(b.x, b.y, b.z - fallDist));
        }
    }

    private static long part1(List<String> input) {

        Input parsed = parseAndSettle(input);
        List<Rect3> bricks = parsed.bricks;
        ListMultimap<Integer, Integer> bricksBelowByBrickIdx = parsed.bricksBelowByBrickIdx;

        // Then, find which bricks are supporting no others
        int disintegratableCount = 0;
        for (int i = 0; i < bricks.size(); i++) {
            Rect3 curr = bricks.get(i);

            char currName = (char) ('A' + i);
//            System.out.println("Checking " + currName);
            boolean isRemoveable = true;
            for (Map.Entry<Integer, Collection<Integer>> aboveEntry : bricksBelowByBrickIdx.asMap().entrySet()) {
                if (aboveEntry.getValue().contains(i)) {
                    Rect3 possibleAbove = bricks.get(aboveEntry.getKey());
                    char aboveEntryName = (char) ('A' + aboveEntry.getKey());
                    boolean doesRestOn = (possibleAbove.a.z == curr.b.z + 1);
                    boolean hasOtherSupport = false;
                    if (doesRestOn) {
                        for (int otherSupportIdx : aboveEntry.getValue()) {
                            if (otherSupportIdx != i) {
                                Rect3 otherSupport = bricks.get(otherSupportIdx);
                                if (otherSupport.b.z + 1 == possibleAbove.a.z) {
                                    hasOtherSupport = true;
//                                    System.out.println(aboveEntryName + " does rest on it, but is also supported by " + (char) ('A' + otherSupportIdx));
                                    break;
                                }
                            }
                        }
                    }
                    if (doesRestOn && !hasOtherSupport) {
//                        System.out.println(aboveEntryName + " rests on it and has no other supports");
                        isRemoveable = false;
                        break;
                    }
                }
            }

            if (isRemoveable) {
//                System.out.println(currName + " is removable");
                disintegratableCount++;
            } else {
//                System.out.println(currName + " is not removable");
            }
        }

        return disintegratableCount;
    }

    @Value
    static class Input {
        List<Rect3> bricks;
        // cache of x-y intersections
        ListMultimap<Integer, Integer> bricksBelowByBrickIdx;
    }

    private static long part2(List<String> input) {
        Input parsed = parseAndSettle(input);
        List<Rect3> bricks = parsed.bricks;
        ListMultimap<Integer, Integer> bricksBelowByBrickIdx = parsed.bricksBelowByBrickIdx;

        // For each brick, determine how many other bricks would fall if that brick were disintegrated.
        int acc = 0;
        for (int i = 0; i < bricks.size(); i++) {
            List<Rect3> bricksCopy = (List<Rect3>) ((ArrayList<Rect3>) bricks).clone();
            bricksCopy.set(i, new Rect3(new Point3(0, 0, 0), new Point3(0, 0, 0)));
            acc += computeFallsInPlace(bricksCopy, bricksBelowByBrickIdx).size();
        }

        return acc;
    }

    private static Input parseAndSettle(List<String> input) {
        List<Rect3> bricks = new ArrayList<>();
        Pattern p = Pattern.compile("(\\d+),(\\d+),(\\d+)~(\\d+),(\\d+),(\\d+)");
        for (String line : input) {
            Matcher m = p.matcher(line);
            checkState(m.matches());
            Rect3 brick = new Rect3(
                    new Point3(
                            Integer.parseInt(m.group(1)),
                            Integer.parseInt(m.group(2)),
                            Integer.parseInt(m.group(3))),
                    new Point3(
                            Integer.parseInt(m.group(4)),
                            Integer.parseInt(m.group(5)),
                            Integer.parseInt(m.group(6))));
            bricks.add(brick);

            checkState(brick.a.x <= brick.b.x);
            checkState(brick.a.y <= brick.b.y);
            checkState(brick.a.z <= brick.b.z);
        }

        // cache x-y intersections
        ListMultimap<Integer, Integer> bricksBelowByBrickIdx = Multimaps.newListMultimap(new HashMap<>(), ArrayList::new);
        for (int i = 0; i < bricks.size(); i++) {
            Rect3 curr = bricks.get(i);
            for (int j = 0; j < bricks.size(); j++) {
                if (j != i) {
                    Rect3 other = bricks.get(j);

                    boolean overlapsXY = curr.intersectsXY(other);
                    if (overlapsXY) {
                        if (other.b.z <= curr.a.z) {
                            bricksBelowByBrickIdx.put(i, j);
                        }
                    }
                }
            }
        }

        // first, all bricks fall downwards
        computeFallsInPlace(bricks, bricksBelowByBrickIdx);

        return new Input(bricks, bricksBelowByBrickIdx);
    }

    private static Set<Integer> computeFallsInPlace(List<Rect3> bricks, ListMultimap<Integer, Integer> bricksBelowByBrickIdx) {
        Set<Integer> changedBricks = new HashSet<>();
        boolean changesMade;
        do {
            changesMade = false;
            for (int i = 0; i < bricks.size(); i++) {
                Rect3 curr = bricks.get(i);

                int minZ = Math.min(curr.a.z, curr.b.z);
                if (minZ == 1) {
                    // brick on ground
                    continue;
                }

                int floorZ = bricksBelowByBrickIdx.get(i)
                        .stream()
                        .mapToInt(j -> bricks.get(j).b.z)
                        .max()
                        .orElseGet(() -> 0);

                int fallDist = curr.a.z - floorZ - 1;
                if (fallDist > 0) {
                    bricks.set(i, curr.moveDown(fallDist));
                    changedBricks.add(i);
                    changesMade = true;
//                } else {
//                    checkState(fallDist == 0);
                }
            }
        } while (changesMade);

        return changedBricks;
    }

    static List<String> example = List.of(
            "1,0,1~1,2,1",
            "0,0,2~2,0,2",
            "0,2,3~2,2,3",
            "0,0,4~0,2,4",
            "2,0,5~2,2,5",
            "0,1,6~2,1,6",
            "1,1,8~1,1,9"
    );
}

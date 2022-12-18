package y2022;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Iterables;
import com.google.common.io.Resources;
import lombok.Value;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

public class Y2022D17 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        String input = Iterables.getOnlyElement(Resources.readLines(Resources.getResource("y2022/Y2022D17.txt"), StandardCharsets.UTF_8));

        // 1
        assertThat(run(example, 2022)).isEqualTo(3068);
        System.out.println(run(input, 2022)); // 3109

        // 2
        assertThat(run(example, 1000000000000L)).isEqualTo(1514285714288L);
        System.out.println(run(input, 1000000000000L));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    @Value
    private static class LoopDetectCacheKey {
        static final int STACK_CONTEXT_DEPTH = 60; // from observation, max drop was 52

        int nextRockIdx;
        int inputIdx;
        String topOfStack;
    }

    @Value
    private static class LoopDetectCacheValue {
        long pieceCount;
        long maxY;
    }

    private static long run(String input, long targetPieceCount) {
        // Represent each rock as a set of points relative to bottom left of piece
        Set<Point>[] rocks = parsePieces(rockShapeInput);
        int nextRockIdx = 0;
        int inputIdx = 0;

        World world = new World();
        long skippedRows = 0;
        Map<LoopDetectCacheKey, LoopDetectCacheValue> stateToPieceCountCache = new HashMap<>();

        for (long pieceCount = 0; pieceCount < targetPieceCount; pieceCount++) {

            // check for loops, skip ahead if possible
            if (world.chamber.size() > LoopDetectCacheKey.STACK_CONTEXT_DEPTH) {
                LoopDetectCacheKey key = new LoopDetectCacheKey(
                        nextRockIdx,
                        inputIdx,
                        world.makeTopOfStack(LoopDetectCacheKey.STACK_CONTEXT_DEPTH));
                LoopDetectCacheValue val = stateToPieceCountCache.get(key);
                if (val == null) {
                    stateToPieceCountCache.put(key, new LoopDetectCacheValue(pieceCount, world.maxY + skippedRows));
                } else {
                    long loopPieceCount = pieceCount - val.pieceCount;
                    long loopBrickHeight = (world.maxY + skippedRows) - val.maxY;
                    long loopApplyCount = (targetPieceCount - pieceCount) / loopPieceCount;
                    if (loopApplyCount > 0) {
                        long oldPieceCount = pieceCount;
                        pieceCount += loopApplyCount * loopPieceCount;
                        skippedRows += loopApplyCount * loopBrickHeight;
                        System.out.printf(
                                "Skipping %s pieces from %s to %s and %s height with %s times loop of len %s, height %s\n",
                                loopApplyCount * loopPieceCount,
                                oldPieceCount,
                                pieceCount,
                                loopApplyCount * loopBrickHeight,
                                loopApplyCount,
                                loopPieceCount,
                                loopBrickHeight);
                    }
                }
            }

            // Each rock appears so that its left edge is two units away from
            // the left wall and its bottom edge is three units above the
            // highest rock in the room (or the floor, if there isn't one).
            Set<Point> currRock = rocks[nextRockIdx];
            nextRockIdx = (nextRockIdx + 1) % rocks.length;

            int rockBLX = 2;
            int rockBLY = Math.toIntExact(4 + world.maxY());

            while (true) {
//                world.print(currRock, rockBLX, rockBLY);

                // After a rock appears, it alternates between being pushed by
                // a jet of hot gas one unit (in the direction indicated by the
                // next symbol in the jet pattern) and then falling one unit down.
                int jetDx = input.charAt(inputIdx) == '>' ? 1 : -1;
                inputIdx = (inputIdx + 1) % input.length();

                boolean canMoveWithJet = true;
                for (Point p : currRock) {
                    canMoveWithJet &= world.isClear(new Point(rockBLX + p.x + jetDx, rockBLY + p.y));
                }
                if (canMoveWithJet) {
                    rockBLX += jetDx;
                }

//                world.print(currRock, rockBLX, rockBLY);

                boolean canFall = true;
                for (Point p : currRock) {
                    canFall &= world.isClear(new Point(rockBLX + p.x, rockBLY + p.y - 1));
                }
                if (canFall) {
                    rockBLY--;
                } else {
                    // If a downward movement would have caused a falling
                    // rock to move into the floor or an already-fallen rock,
                    // the falling rock stops where it is (having landed on
                    // something) and a new rock immediately begins falling.
                    for (Point p : currRock) {
                        world.addRock(rockBLX + p.x, rockBLY + p.y);
                    }
//                    world.print();
                    break;
                }
            }
        }

        return world.maxY + skippedRows + 1;
    }

    private static class World {
        private static char[] EMPTY_ROW = "......." .toCharArray();
        List<char[]> chamber = new ArrayList<>();
        long maxY = -1;

        public long maxY() {
            return maxY;
        }

        public boolean isClear(Point p) {
            if (p.y < 0 || p.x < 0 || p.x >= 7) {
                return false;
            }
            if (p.y >= chamber.size()) {
                return true;
            }
            return '.' == chamber.get(p.y)[p.x];
        }

        public void addRock(int x, int y) {
            while (y >= chamber.size()) {
                chamber.add(EMPTY_ROW.clone());
            }
            chamber.get(y)[x] = '#';
            maxY = Math.max(maxY, y);
        }

        public void print() {
            print(null, 0, 0);
        }

        public void print(Set<Point> currRock, int rockBLX, int rockBLY) {
            for (int y = Math.toIntExact(maxY + 4); y >= 0; y--) {
                System.out.print("|");
                for (int x = 0; x < 7; x++) {
                    Point pInRock = new Point(x - rockBLX, y - rockBLY);
                    if (currRock != null && currRock.contains(pInRock)) {
                        System.out.print("@");
                    } else {
                        Point p = new Point(x, y);
                        System.out.print(isClear(p) ? '.' : '#');
                    }
                }
                System.out.println("|");
            }
            System.out.println("+-------+");
        }

        public String makeTopOfStack(int stackContextDepth) {
            StringBuilder acc = new StringBuilder();
            for (int i = 0; i < stackContextDepth; i++) {
                acc.append(chamber.get(chamber.size() - i - 1));
                acc.append('\n');
            }
            return acc.toString();
        }
    }

    private static Set<Point>[] parsePieces(String[] piecesInput) {
        Set<Point>[] acc = new Set[piecesInput.length];
        for (int i = 0; i < piecesInput.length; i++) {
            Set<Point> piece = new HashSet<>();
            acc[i] = piece;
            String pieceInput = piecesInput[i];
            String[] lines = pieceInput.split("\n");
            for (int y = 0; y < lines.length; y++) {
                String line = lines[y];
                for (int x = 0; x < line.length(); x++) {
                    if ('#' == line.charAt(x)) {
                        piece.add(new Point(x, lines.length - y - 1));
                    }
                }
            }
        }
        return acc;
    }

    private static int part2(List<String> input) {
        return -1;
    }


    static String[] rockShapeInput = new String[]{
            "####",
            ".#.\n" +
                    "###\n" +
                    ".#.",
            "..#\n" +
                    "..#\n" +
                    "###",
            "#\n" +
                    "#\n" +
                    "#\n" +
                    "#",
            "##\n" +
                    "##"
    };

    private static String example =
            ">>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>";
}

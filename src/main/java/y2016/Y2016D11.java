package y2016;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import lombok.Value;
import org.apache.commons.math3.util.CombinatoricsUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2016D11 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(minStepsToBringAllToFourthFloor(parse(exampleInput, false))).isEqualTo(11);
        System.out.println("example ok");

        System.out.println(minStepsToBringAllToFourthFloor(parse(input, false)));

        // 2
        System.out.println(minStepsToBringAllToFourthFloor(parse(input, true)));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    @Value
    static class SearchState implements Comparable<SearchState> {
        int movesToHere;
        LabState labState;
        private final int searchHeuristicCost;

        public SearchState(int movesToHere, LabState labState) {
            this.movesToHere = movesToHere;
            this.labState = labState;

            this.searchHeuristicCost = movesToHere + minMovesToFinish(labState.itemFloors);
        }

        @Override
        public int compareTo(SearchState that) {
            return Integer.compare(this.searchHeuristicCost, that.searchHeuristicCost);
        }

        private static int minMovesToFinish(byte[] itemFloors) {
            // To be admissable, must not over-estimate the dist to finish
            int moves = 0;
            for (int floorIdx = 0; floorIdx < 3; floorIdx++) {
                int countOnThisFloor = 0;
                for (int i = 0; i < itemFloors.length; i++) {
                    if (itemFloors[i] == floorIdx) {
                        countOnThisFloor++;
                    }
                }
                moves += countOnThisFloor / 2 + (countOnThisFloor % 2);
            }
            return moves;
        }
    }

    @Value
    static class LabState {
        byte elevatorFloor; // zero-indexed
        // Byte 2N is the zero-indexed floor of the Nth generator
        // Byte 2N+1 is the zero-indexed floor of the Nth chip
        byte[] itemFloors;

        @Override
        public boolean equals(Object o) {
            LabState that = (LabState) o;
            return this.elevatorFloor == that.elevatorFloor && Arrays.equals(this.itemFloors, that.itemFloors);
        }

        @Override
        public int hashCode() {
            return 17 * elevatorFloor + Arrays.hashCode(itemFloors);
        }

        public boolean isAllOnFourthFloor() {
            for (int i = 0; i < itemFloors.length; i++) {
                if (itemFloors[i] != 3) {
                    return false;
                }
            }
            return true;
        }

        private static boolean floorIsSafe(byte[] itemFloors, byte floorIdx) {
            // If a chip is ever left in the same area as another RTG, and it's not connected to its own RTG, the chip will be fried.
            // ...
            // keep chips connected to their corresponding RTG when they're in the same room, and away from other RTGs otherwise.

            // Byte 2N is the zero-indexed floor of the Nth generator
            // Byte 2N+1 is the zero-indexed floor of the Nth chip

            for (int n = 0; n < itemFloors.length / 2; n++) {
                byte chipFloor = itemFloors[n * 2 + 1];
                if (chipFloor == floorIdx) {
                    boolean chipIsShielded = (itemFloors[n * 2] == floorIdx);
                    if (!chipIsShielded) {
                        for (int m = 0; m < itemFloors.length / 2; m++) {
                            if (m != n) {
                                if (itemFloors[m * 2] == floorIdx) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }

            return true;
        }

        public List<LabState> possibleMoves() {
            List<LabState> acc = new ArrayList<>();
            if (elevatorFloor < 3) {
                addPossibleMovesTo((byte) (elevatorFloor + 1), acc);
            }
            if (elevatorFloor > 0) {
                addPossibleMovesTo((byte) (elevatorFloor - 1), acc);
            }
            return acc;
        }

        private void addPossibleMovesTo(byte targetFloor, List<LabState> acc) {
            int countOnElevatorFloor = 0;
            List<Integer> idxOnElevatorFloor = new ArrayList<>();
            for (int i = 0; i < itemFloors.length; i++) {
                if (itemFloors[i] == elevatorFloor) {
                    countOnElevatorFloor++;
                    idxOnElevatorFloor.add(i);
                }
            }

            // Empty moves disallowed

            if (countOnElevatorFloor >= 1) {
                // move 1 item
                for (int itemIdxToMove : idxOnElevatorFloor) {
                    byte[] newItemFloors = itemFloors.clone();
                    newItemFloors[itemIdxToMove] = targetFloor;
                    if (floorIsSafe(newItemFloors, elevatorFloor) && floorIsSafe(newItemFloors, targetFloor)) {
                        acc.add(new LabState(targetFloor, newItemFloors));
                    }
                }
            }
            if (countOnElevatorFloor >= 2) {
                // move 2 items
                Iterator<int[]> combs = CombinatoricsUtils.combinationsIterator(countOnElevatorFloor, 2);
                while (combs.hasNext()) {
                    int[] nextComb = combs.next();
                    byte[] newItemFloors = itemFloors.clone();
                    newItemFloors[idxOnElevatorFloor.get(nextComb[0])] = targetFloor;
                    newItemFloors[idxOnElevatorFloor.get(nextComb[1])] = targetFloor;
                    if (floorIsSafe(newItemFloors, elevatorFloor) && floorIsSafe(newItemFloors, targetFloor)) {
                        acc.add(new LabState(targetFloor, newItemFloors));
                    }
                }
            }
        }
    }

    static int minStepsToBringAllToFourthFloor(SearchState startState) {

        long lastReportTimeMillis = System.currentTimeMillis();

        // A* search:
        // Heuristic is min number of moves to finish (ignoring radiation safety). This is "admissable"
        PriorityQueue<SearchState> queue = new PriorityQueue<>();
        Map<LabState, Integer> minMovesToReachStates = new HashMap<>();
        queue.add(startState);

        while (true) {
            SearchState state = queue.poll();

            if (state.labState.isAllOnFourthFloor()) {
                Integer prevEntry = minMovesToReachStates.get(state.labState);
                checkState(prevEntry == null || prevEntry >= state.movesToHere);
                return state.movesToHere;
            }

            minMovesToReachStates.compute(state.labState, (k, oldDist) -> {

                if (oldDist == null || oldDist > state.movesToHere) {
                    int nextMoveCount = state.movesToHere + 1;
                    for (LabState nextState : state.labState.possibleMoves()) {
                        Integer nextStatePrevMoves = minMovesToReachStates.get(nextState);
                        if (nextStatePrevMoves == null || nextStatePrevMoves > nextMoveCount) {
                            queue.add(new SearchState(nextMoveCount, nextState));
                        }
                    }

                    return state.movesToHere;
                } else {
                    // already reached
                    return oldDist;
                }
            });

            if (System.currentTimeMillis() - lastReportTimeMillis > 10000) {
                lastReportTimeMillis = System.currentTimeMillis();
                System.out.println("Queue len = " + queue.size() + " Current node = " + state);
            }
        }
    }

    @Value
    static class Item {
        String element;
        boolean isGenerator;
        byte floorIdx;
    }

    static SearchState parse(String[] spec, boolean partTwo) {
        checkArgument(spec.length == 4);
        List<Item> items = new ArrayList<>();
        for (int floorIdx = 0; floorIdx < 4; floorIdx++) {
            Pattern p = Pattern.compile("The \\w+ floor contains ((nothing relevant)|a (.*))\\.");
            Matcher m = p.matcher(spec[floorIdx]);
            checkArgument(m.matches());
            if (m.group(2) != null) {
                continue;
            } else {
                Iterable<String> itemsSpec = Splitter.onPattern(", a |, and a | and a ")
                        .trimResults()
                        .omitEmptyStrings()
                        .split(m.group(3));
                Pattern itemPat = Pattern.compile("(\\w+) generator|(\\w+)-compatible microchip");
                for (String itemSpec : itemsSpec) {
                    Matcher itemM = itemPat.matcher(itemSpec);
                    checkArgument(itemM.matches());
                    if (itemM.group(1) != null) {
                        items.add(new Item(itemM.group(1), true, (byte) floorIdx));
                    } else {
                        items.add(new Item(itemM.group(2), false, (byte) floorIdx));
                    }
                }
            }
        }

        Set<String> elements = items.stream().map(i -> i.element).collect(Collectors.toSet());
        for (String element : elements) {
            checkArgument(items.stream().filter(i -> i.element.equals(element)).count() == 2);
        }

        // Byte 2N is the zero-indexed floor of the Nth generator
        // Byte 2N+1 is the zero-indexed floor of the Nth chip
        byte[] itemFloors = new byte[elements.size() * 2];
        int n = 0;
        for (String element : elements) {
            itemFloors[2 * n] = items.stream()
                    .filter(i -> i.element.equals(element) && i.isGenerator)
                    .findFirst()
                    .get()
                    .floorIdx;
            itemFloors[2 * n + 1] = items.stream()
                    .filter(i -> i.element.equals(element) && !i.isGenerator)
                    .findFirst()
                    .get()
                    .floorIdx;

            n++;
        }

        if (partTwo) {
            byte[] itemFloors2 = new byte[itemFloors.length + 4];
            System.arraycopy(itemFloors, 0, itemFloors2, 0, itemFloors.length);
            itemFloors = itemFloors2;
        }

        return new SearchState(0, new LabState((byte) 0, itemFloors));
    }

    static String[] exampleInput = new String[]{
            "The first floor contains a hydrogen-compatible microchip and a lithium-compatible microchip.",
            "The second floor contains a hydrogen generator.",
            "The third floor contains a lithium generator.",
            "The fourth floor contains nothing relevant.",
    };

    static String[] input = new String[]{
            "The first floor contains a promethium generator and a promethium-compatible microchip.",
            "The second floor contains a cobalt generator, a curium generator, a ruthenium generator, and a plutonium generator.",
            "The third floor contains a cobalt-compatible microchip, a curium-compatible microchip, a ruthenium-compatible microchip, and a plutonium-compatible microchip.",
            "The fourth floor contains nothing relevant.",
    };
}

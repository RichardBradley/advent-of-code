package y2021;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;
import lombok.Value;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2021D23 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            List<String> input = Resources.readLines(Resources.getResource("y2021/Y2021D23.txt"), StandardCharsets.UTF_8);

            // 1
            assertThat(part1(example)).isEqualTo(12521);
            assertThat(part1(input)).isEqualTo(13520);

            // 2
            assertThat(part2(example)).isEqualTo(44169);
            assertThat(part2(input)).isEqualTo(48708);

        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private static long part1(List<String> input) {
        PriorityQueue<SearchState> searchQueue = new PriorityQueue<>();
        searchQueue.add(new SearchState(0, input));
        long lastReportTimeMillis = System.currentTimeMillis();
        Map<List<String>, Integer> lowestCostByMap = new HashMap<>();

        while (true) {
            SearchState curr = searchQueue.poll();
            if (curr.isComplete()) {
                return curr.powerCost;
            }

            for (SearchState possibleMove : curr.possibleMoves()) {
                Integer prevCost = lowestCostByMap.get(possibleMove.map);
                if (prevCost == null || prevCost > possibleMove.powerCost) {
                    lowestCostByMap.put(possibleMove.map, possibleMove.powerCost);
                    searchQueue.add(possibleMove);
                }
            }

            if (System.currentTimeMillis() - lastReportTimeMillis > 10000) {
                lastReportTimeMillis = System.currentTimeMillis();
                System.out.println("Queue len = " + searchQueue.size() + " Current node = " + curr);
            }
        }
    }

    @Value
    private static class SearchState implements Comparable<SearchState> {
        int powerCost;
        List<String> map;

        @Override
        public int compareTo(SearchState o) {
            return Integer.compare(this.powerCost, o.powerCost);
        }

        public boolean isComplete() {
            int maxRoomY = map.size() == 5 ? 3 : 5;

            for (int type = 'A'; type <= 'D'; type++) {
                int x = 3 + 2 * (type - 'A');
                for (int y = 2; y <= maxRoomY; y++) {
                    if (map.get(y).charAt(x) != type) {
                        return false;
                    }
                }
            }
            return true;
        }

        public Collection<SearchState> possibleMoves() {
            List<SearchState> acc = new ArrayList<>();
            int hallwayY = 1;
            int maxRoomY = map.size() == 5 ? 3 : 5;

            // 1. Can move from their start room to the hallway:
            roomScan:
            for (int room = 0; room < 4; room++) {
                int startX = 3 + 2 * room;
                char roomType = (char) ('A' + room);
                for (int startY = 2; startY <= maxRoomY; startY++) {
                    for (int prevRoomY = startY - 1; prevRoomY >= 2; prevRoomY--) {
                        if ('.' != map.get(prevRoomY).charAt(startX)) {
                            continue roomScan; // exit from room blocked
                        }
                    }
                    char actor = map.get(startY).charAt(startX);
                    if (actor != '.') {
                        int dist = startY - hallwayY; // to move up
                        // walk left?
                        for (int x = startX - 1; x >= 1; x--) {
                            if (x != 3 && x != 5 && x != 7 && x != 9) {
                                if (map.get(hallwayY).charAt(x) == '.') {
                                    int newCost = (dist + (startX - x)) * getMovementCost(actor);
                                    acc.add(new SearchState(
                                            powerCost + newCost,
                                            move(map, startX, startY, x, hallwayY)));
                                } else {
                                    // hallway blocked
                                    break;
                                }
                            }
                        }
                        // walk right?
                        for (int x = startX + 1; x <= 11; x++) {
                            if (x != 3 && x != 5 && x != 7 && x != 9) {
                                if (map.get(hallwayY).charAt(x) == '.') {
                                    int newCost = (dist + (x - startX)) * getMovementCost(actor);
                                    acc.add(new SearchState(
                                            powerCost + newCost,
                                            move(map, startX, startY, x, hallwayY)));
                                } else {
                                    // hallway blocked
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            // 2. Can move from the hallway into destination room, unless
            // a non matching amphi is there
            fromHallwayLoop:
            for (int x = 1; x <= 11; x++) {
                char type = map.get(hallwayY).charAt(x);
                if ('.' != type) {
                    int roomX = 3 + 2 * (type - 'A');
                    // is it clear between here and target room?
                    for (int xx = Math.min(x, roomX); xx <= Math.max(x, roomX); xx++) {
                        if (xx != x && '.' != map.get(hallwayY).charAt(xx)) {
                            continue fromHallwayLoop;
                        }
                    }
                    // is the room empty or has only correct type?
                    int destY = -1;
                    for (int y = 2; y <= maxRoomY; y++) {
                        char c = map.get(y).charAt(roomX);
                        if (c == '.') {
                            destY = y;
                        } else if (c != type) {
                            continue fromHallwayLoop;
                        }
                    }
                    int newCost = (Math.abs(x - roomX) + destY - hallwayY) * getMovementCost(type);
                    acc.add(new SearchState(
                            powerCost + newCost,
                            move(map, x, hallwayY, roomX, destY)));
                }
            }

            return acc;
        }

        private List<String> move(List<String> map, int startX, int startY, int newX, int newY) {
            List<String> newMap = new ArrayList<>(map);
            char actor = newMap.get(startY).charAt(startX);
            checkState(actor >= 'A' && actor <= 'D');
            newMap.set(startY, setCharAt(newMap.get(startY), startX, '.'));
            checkState(newMap.get(newY).charAt(newX) == '.');
            newMap.set(newY, setCharAt(newMap.get(newY), newX, actor));
            return newMap;
        }

        private String setCharAt(String s, int i, char c) {
            StringBuilder sb = new StringBuilder(s);
            sb.setCharAt(i, c);
            return sb.toString();
        }

        @Override
        public String toString() {
            StringBuilder acc = new StringBuilder();
            acc.append("SearchState cost = ").append(powerCost).append("\n");
            for (String s : map) {
                acc.append(s).append("\n");
            }
            return acc.toString();
        }
    }

    static int getMovementCost(char type) {
        switch (type) {
            case 'A':
                return 1;
            case 'B':
                return 10;
            case 'C':
                return 100;
            case 'D':
                return 1000;
            default:
                throw new IllegalArgumentException();
        }
    }

    private static long part2(List<String> input) {
        ArrayList<String> input2 = new ArrayList<>(input);
        input2.add(3, "  #D#C#B#A#");
        input2.add(4, "  #D#B#A#C#");

        return part1(input2);
    }

    private static List<String> example = List.of(
            "#############",
            "#...........#",
            "###B#C#B#D###",
            "  #A#D#C#A#",
            "  #########"
    );
}

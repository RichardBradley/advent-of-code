package y2020;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Y2020D15 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        getNumberForTurn(ImmutableList.of(0, 3, 6), 10);
        getNumberForTurn(input, 2020);
        getNumberForTurn(input, 30000000);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static void getNumberForTurn(List<Integer> input, int targetTurn) {
        Map<Integer, Integer> lastTurnNumberWasSaid = new HashMap<>();
        for (int i = 0; i < input.size() - 1; i++) {
            lastTurnNumberWasSaid.put(input.get(i), i + 1);
            // System.out.printf("Turn %s = %s\n", i+1, input.get(i));
        }
        int nextNumber = input.get(input.size() - 1);
        for (int turn = input.size(); turn < targetTurn; turn++) {
            // System.out.printf("Turn %s = %s\n", turn, nextNumber);
            int lastTurnForNextNumber = lastTurnNumberWasSaid.getOrDefault(nextNumber, 0);
            lastTurnNumberWasSaid.put(nextNumber, turn);
            nextNumber = lastTurnForNextNumber == 0 ? 0 : turn - lastTurnForNextNumber;
        }

        System.out.printf("The %sth number is %s\n", targetTurn, nextNumber);
    }

    private static List<Integer> input = ImmutableList.of(0, 14, 1, 3, 7, 9);
}

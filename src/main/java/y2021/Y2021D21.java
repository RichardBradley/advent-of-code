package y2021;

import com.google.common.base.Stopwatch;
import lombok.Value;
import org.apache.commons.math3.util.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

public class Y2021D21 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            // 1
            assertThat(part1(example)).isEqualTo(739785);
            assertThat(part1(input)).isEqualTo(711480);

            // 2
            assertThat(part2(example)).isEqualTo(444356092776315L);
            assertThat(part2(input)).isEqualTo(265845890886828L);

        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private static long part1(Pair<Integer, Integer> input) {
        long p1Score = 0;
        long p2Score = 0;
        long p1Loc = input.getFirst();
        long p2Loc = input.getSecond();
        DeterministicDie die = new DeterministicDie();

        while (true) {
            for (int i = 0; i < 3; i++) {
                p1Loc += die.next();
            }
            p1Loc = ((p1Loc - 1) % 10) + 1;
            p1Score += p1Loc;
            if (p1Score >= 1000) break;

            for (int i = 0; i < 3; i++) {
                p2Loc += die.next();
            }
            p2Loc = ((p2Loc - 1) % 10) + 1;
            p2Score += p2Loc;
            if (p2Score >= 1000) break;
        }

        return Math.min(p1Score, p2Score) * die.rollCount;
    }

    private static class DeterministicDie {
        int currVal = 0;
        int rollCount = 0;

        int next() {
            rollCount++;
            currVal++;
            if (currVal > 100) {
                currVal = 1;
            }
            return currVal;
        }
    }

    private static long part2(Pair<Integer, Integer> input) {
        // unfinished games only:
        Map<GameState, Long> stateCounts = new HashMap<>();
        stateCounts.put(
                new GameState(
                        input.getFirst(),
                        0,
                        input.getSecond(),
                        0),
                1L);
        long p1WinCount = 0;
        long p2WinCount = 0;

        Map<Integer, Long> diceSumToFreq = new HashMap<>();
        diceSumToFreq.put(0, 1L);
        for (int rollCount = 0; rollCount < 3; rollCount++) {
            Map<Integer, Long> next = new HashMap<>();
            for (int diceVal = 1; diceVal <= 3; diceVal++) {
                for (Map.Entry<Integer, Long> prev : diceSumToFreq.entrySet()) {
                    add(next, prev.getKey() + diceVal, prev.getValue());
                }
            }
            diceSumToFreq = next;
        }

        while (!stateCounts.isEmpty()) {
            // p1 turn
            Map<GameState, Long> nextStateCounts = new HashMap<>();
            for (Map.Entry<GameState, Long> entry : stateCounts.entrySet()) {
                GameState state = entry.getKey();
                long stateFreq = entry.getValue();

                for (Map.Entry<Integer, Long> diceOutcome : diceSumToFreq.entrySet()) {
                    int diceSum = diceOutcome.getKey();
                    long diceFreq = diceOutcome.getValue();

                    int p1Loc = state.p1Loc + diceSum;
                    p1Loc = ((p1Loc - 1) % 10) + 1;
                    int p1Score = state.p1Score + p1Loc;
                    if (p1Score >= 21) {
                        p1WinCount += (stateFreq * diceFreq);
                    } else {
                        add(nextStateCounts,
                                new GameState(p1Loc, p1Score, state.p2Loc, state.p2Score),
                                stateFreq * diceFreq);
                    }
                }
            }
            stateCounts = nextStateCounts;

            // p1 turn
            nextStateCounts = new HashMap<>();
            for (Map.Entry<GameState, Long> entry : stateCounts.entrySet()) {
                GameState state = entry.getKey();
                long stateFreq = entry.getValue();

                for (Map.Entry<Integer, Long> diceOutcome : diceSumToFreq.entrySet()) {
                    int diceSum = diceOutcome.getKey();
                    long diceFreq = diceOutcome.getValue();

                    int p2Loc = state.p2Loc + diceSum;
                    p2Loc = ((p2Loc - 1) % 10) + 1;
                    int p2Score = state.p2Score + p2Loc;
                    if (p2Score >= 21) {
                        p2WinCount += (stateFreq * diceFreq);
                    } else {
                        add(nextStateCounts,
                                new GameState(state.p1Loc, state.p1Score, p2Loc, p2Score),
                                stateFreq * diceFreq);
                    }
                }
            }
            stateCounts = nextStateCounts;
        }

        return Math.max(p1WinCount, p2WinCount);
    }

    private static <T> void add(Map<T, Long> map, T key, long val) {
        map.compute(key, (k, v) -> (v == null ? 0 : v) + val);
    }

    @Value
    static class GameState {
        int p1Loc;
        int p1Score;
        int p2Loc;
        int p2Score;
    }

    private static Pair<Integer, Integer> example = new Pair<>(4, 8);
    private static Pair<Integer, Integer> input = new Pair<>(5, 10);
}

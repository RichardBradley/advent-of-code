package y2018;

import com.google.common.base.Strings;
import com.google.common.primitives.Ints;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.core.util.Integers;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.truth.Truth.assertThat;

public class Y2018D12 {
    public static void main(String[] args) throws Exception {

        // 1
        assertThat(sumPlantIdxAfterGen(20, testInitialState, testRules, true)).isEqualTo(325);

        System.out.println(sumPlantIdxAfterGen(20, initialState, rules, true));

        // 2
        System.out.println(sumPlantIdxAfterGen(50000000000L, initialState, rules, true));
    }

    static long sumPlantIdxAfterGen(long targetGen, String initialState, Map<String, Character> rules, boolean useLoops) {
        PlantState state = new PlantState(initialState, 0);
        Map<Long, PlantState> historyByGen = new TreeMap<>();
        historyByGen.put(0L, state);

        for (long g = 0; g < targetGen; g++) {
            StringBuilder nextStateData = new StringBuilder();
            int nextStateOffset = -1;

            for (int i = -2; i < state.data.length() + 2; i++) {

                // Grab 5 chars centred around i:
                StringBuilder s = new StringBuilder();
                s.append("....", 0, Math.max(0, 2 - i));
                int dataStart = Math.max(0, i - 2);
                int dataEnd = Math.min(state.data.length(), i + 3);
                s.append(state.data, dataStart, dataEnd);
                s.append("....", 0, 5 - s.length());

                Character resultC = rules.get(s.toString());
                char result = resultC == null ? '.' : resultC;

                if (nextStateData.length() == 0) {
                    if (result == '.') {
                        continue;
                    } else {
                        nextStateOffset = Ints.checkedCast(state.offset + i);
                    }
                }
                nextStateData.append(result);
            }

            while (nextStateData.charAt(nextStateData.length() - 1) == '.') {
                nextStateData.deleteCharAt(nextStateData.length() - 1);
            }

            PlantState nextState = new PlantState(nextStateData.toString(), nextStateOffset);

            // Check for loops and skip:
            if (useLoops) {
                for (Map.Entry<Long, PlantState> stateByGen : historyByGen.entrySet()) {
                    PlantState pastState = stateByGen.getValue();
                    if (pastState.data.equals(nextState.data)) {
                        long loopLen = g - stateByGen.getKey();
                        long skipCount = (targetGen - 1 - g) / loopLen;
                        if (skipCount > 0) {
                            System.out.println("Skipping " + (skipCount * loopLen) + " generations using observed loop");
                        }
                        g += skipCount * loopLen;
                        nextState.offset += skipCount * (nextState.offset - pastState.offset);
                        break;
                    }
                }
            }

            historyByGen.put(g, nextState);
            state = nextState;

            // Logging
//            System.out.println((g + 1) + ": " + Strings.repeat(".", Ints.checkedCast(2 + state.offset)) + state.data);
        }

        long acc = 0;
        for (int i = 0; i < state.data.length(); i++) {
            if (state.data.charAt(i) == '#') {
                acc += (i + state.offset);
            }
        }
        return acc;
    }

    @AllArgsConstructor
    static class PlantState {
        String data;
        long offset;
    }

    static Map<String, Character> parseRules(String... rules) {
        Map<String, Character> acc = new HashMap<>(rules.length);
        Pattern pattern = Pattern.compile("([.#]{5}) => ([#.])");
        for (String rule : rules) {
            Matcher matcher = pattern.matcher(rule);
            checkArgument(matcher.matches());
            acc.put(matcher.group(1), matcher.group(2).charAt(0));
        }
        return acc;
    }

    static String testInitialState = "#..#.#..##......###...###";

    static Map<String, Character> testRules = parseRules(
            "...## => #",
            "..#.. => #",
            ".#... => #",
            ".#.#. => #",
            ".#.## => #",
            ".##.. => #",
            ".#### => #",
            "#.#.# => #",
            "#.### => #",
            "##.#. => #",
            "##.## => #",
            "###.. => #",
            "###.# => #",
            "####. => #"
    );

    static String initialState = "##.......#.######.##..#...#.#.#..#...#..####..#.##...#....#...##..#..#.##.##.###.##.#.......###....#";

    static Map<String, Character> rules = parseRules(
            ".#### => .",
            "....# => .",
            "###.. => .",
            "..#.# => .",
            "##### => #",
            "####. => .",
            "#.##. => #",
            "#.#.# => .",
            "##.#. => #",
            ".###. => .",
            "#..#. => #",
            "###.# => .",
            "#.### => .",
            "##... => #",
            ".#.## => .",
            "..#.. => .",
            "#...# => #",
            "..... => .",
            ".##.. => .",
            "...#. => .",
            "#.#.. => .",
            ".#..# => #",
            ".#.#. => .",
            ".#... => #",
            "..##. => .",
            "#..## => .",
            "##.## => #",
            "...## => #",
            "..### => #",
            "#.... => .",
            ".##.# => #",
            "##..# => #"
    );
}

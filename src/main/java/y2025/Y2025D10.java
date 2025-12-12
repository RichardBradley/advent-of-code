package y2025;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static aoc.Common.loadInputFromResources;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2025D10 {

    public static void main(String[] args) throws Exception {
        List<String> input = loadInputFromResources();
        Stopwatch sw = Stopwatch.createStarted();
        try {
            // 1
            assertThat(part1(example)).isEqualTo(7);
            assertThat(part1(input)).isEqualTo(425);

            // 2
            // Sam's example:
            assertThat(part2(List.of("[...] (0,1) (0,2) (1,2) {2,2,2}"))).isEqualTo(3);

            assertThat(part2(example)).isEqualTo(33);
            assertThat(part2(input)).isEqualTo(15883);

        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private static long part1(List<String> input) {
        int sumPresses = 0;
        Pattern linePat = Pattern.compile("\\[([.#]+)] ([()0-9, ]+) ([{},0-9]+)");
        Pattern buttPat = Pattern.compile("\\(([0-9,]+)\\)");
        lineLoop:
        for (int lineIdx = 0; lineIdx < input.size(); lineIdx++) {
            System.out.printf("Line %s of %s\n", lineIdx + 1, input.size());
            String line = input.get(lineIdx);
            Matcher m = linePat.matcher(line);
            checkState(m.matches());
            String targetState = m.group(1);
            String buttonsPart = m.group(2);

            List<List<Integer>> buttons = Splitter.on(" ")
                    .splitToList(buttonsPart).stream()
                    .map(s -> {
                        Matcher bm = buttPat.matcher(s);
                        checkState(bm.matches());
                        return Splitter.on(",").splitToList(bm.group(1)).stream()
                                .map(Integer::parseInt)
                                .collect(Collectors.toList());
                    })
                    .collect(Collectors.toList());

            // Breadth first search of button presses:
            List<StringBuilder> currStates = new ArrayList<>();
            {
                StringBuilder startState = new StringBuilder();
                for (int i = 0; i < targetState.length(); i++) {
                    startState.append('.');
                }
                currStates.add(startState);
            }
            for (int pressCount = 1; ; pressCount++) {
                List<StringBuilder> nextStates = new ArrayList<>();
                for (StringBuilder currState : currStates) {
                    for (List<Integer> button : buttons) {
                        StringBuilder nextState = new StringBuilder(currState);
                        for (int idx : button) {
                            nextState.setCharAt(idx, nextState.charAt(idx) == '.' ? '#' : '.');
                        }
                        if (nextState.toString().equals(targetState)) {
                            sumPresses += pressCount;
                            continue lineLoop;
                        } else {
                            nextStates.add(nextState);
                        }
                    }
                }
                currStates = nextStates;
            }
        }
        return sumPresses;
    }

    private static long part2(List<String> input) {
        int sumPresses = 0;
        Pattern linePat = Pattern.compile("\\[([.#]+)] ([()0-9, ]+) \\{([,0-9]+)}");
        Pattern buttPat = Pattern.compile("\\(([0-9,]+)\\)");
        for (int lineIdx = 0; lineIdx < input.size(); lineIdx++) {
            System.out.printf("%s Line %s of %s\n", Instant.now(), lineIdx + 1, input.size());
            String line = input.get(lineIdx);
            Matcher m = linePat.matcher(line);
            checkState(m.matches());
            List<Integer> target = Splitter.on(",").splitToList(m.group(3)).stream()
                    .map(s -> Integer.parseInt(s))
                    .collect(Collectors.toList());
            String buttonsPart = m.group(2);

            List<List<Integer>> buttons = Splitter.on(" ")
                    .splitToList(buttonsPart).stream()
                    .map(s -> {
                        Matcher bm = buttPat.matcher(s);
                        checkState(bm.matches());
                        return Splitter.on(",").splitToList(bm.group(1)).stream()
                                .map(Integer::parseInt)
                                .collect(Collectors.toList());
                    })
                    .collect(Collectors.toList());

            sumPresses += countMinPressesForTarget(
                    new HashMap<>(),
                    genPossibleButtonPressesByOddsString(target.size(), buttons),
                    target);
        }
        return sumPresses;
    }

    private static Map<String, List<List<List<Integer>>>> genPossibleButtonPressesByOddsString(int width, List<List<Integer>> buttons) {
        Map<String, List<List<List<Integer>>>> acc = new HashMap<>();
        int maxBitmask = 1 << buttons.size();
        // include "pressing no buttons" as an option
        for (int bitmask = 0; bitmask < maxBitmask; bitmask++) {
            List<List<Integer>> buttonPressCombo = new ArrayList<>();
            StringBuilder oddsString = new StringBuilder();
            oddsString.append(".".repeat(width));
            for (int i = 0; i < buttons.size(); i++) {
                if ((bitmask & (1 << i)) != 0) {
                    buttonPressCombo.add(buttons.get(i));
                    for (int idx : buttons.get(i)) {
                        oddsString.setCharAt(idx, oddsString.charAt(idx) == '.' ? '#' : '.');
                    }
                }
            }
            acc.computeIfAbsent(oddsString.toString(), k -> new ArrayList<>())
                    .add(buttonPressCombo);
        }
        return acc;
    }

    private static long countMinPressesForTarget(
            Map<List<Integer>, Long> minPressesByTargetCache,
            Map<String, List<List<List<Integer>>>> possibleButtonPressesByOddsString,
            List<Integer> target) {
        Long cached = minPressesByTargetCache.get(target);
        if (cached != null) {
            return cached;
        }

        // recurse as per https://www.reddit.com/r/adventofcode/comments/1pk87hl/2025_day_10_part_2_bifurcate_your_way_to_victory/
        // Looking for two targets: odd remainders and N/2 even remainders:
        StringBuilder odds = new StringBuilder();
        int maxTarget = 0;
        for (int t : target) {
            if (t % 2 == 1) {
                odds.append('#');
            } else {
                odds.append('.');
            }
            checkState(t >= 0);
            maxTarget = Math.max(maxTarget, t);
        }

        long ret;
        if (maxTarget == 0) {
            ret = 0;
        } else {
            List<List<List<Integer>>> possibleButtonPresses =
                    possibleButtonPressesByOddsString.get(odds.toString());
            if (possibleButtonPresses == null) {
                ret = Long.MAX_VALUE;
            } else {
                long minResult = Long.MAX_VALUE;
                possibleButtonPresses:
                for (List<List<Integer>> buttonPressCombo : possibleButtonPresses) {
                    List<Integer> newTarget = new ArrayList<>(target);
                    for (List<Integer> button : buttonPressCombo) {
                        for (int idx : button) {
                            int newVal = newTarget.get(idx) - 1;
                            if (newVal < 0) {
                                continue possibleButtonPresses;
                            }
                            newTarget.set(idx, newVal);
                        }
                    }
                    for (int i = 0; i < newTarget.size(); i++) {
                        int x = newTarget.get(i);
                        checkState(x % 2 == 0);
                        newTarget.set(i, x / 2);
                    }
                    long recurse = countMinPressesForTarget(minPressesByTargetCache, possibleButtonPressesByOddsString, newTarget);
                    if (recurse == Long.MAX_VALUE) {
                        continue;
                    }
                    long subResult = buttonPressCombo.size() + 2 * recurse;
                    minResult = Math.min(minResult, subResult);
                }
                ret = minResult;
            }
        }
        minPressesByTargetCache.put(target, ret);
        return ret;
    }

    static List<String> example = List.of(
            "[.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}",
            "[...#.] (0,2,3,4) (2,3) (0,4) (0,1,2) (1,2,3,4) {7,5,12,7,2}",
            "[.###.#] (0,1,2,3,4) (0,3,4) (0,1,2,4,5) (1,2) {10,11,11,5,10,5}");
}

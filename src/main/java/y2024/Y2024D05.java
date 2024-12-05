package y2024;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import lombok.Value;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static aoc.Common.loadInputFromResources;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2024D05 {

    public static void main(String[] args) throws Exception {
        List<String> input = loadInputFromResources();
        Stopwatch sw = Stopwatch.createStarted();

        Input exampleParsed = parse(example);
        Input inputParsed = parse(input);

        // 1
        assertThat(part1(exampleParsed)).isEqualTo(143);
        assertThat(part1(inputParsed)).isEqualTo(7307);

        // 2
        assertThat(part2(exampleParsed)).isEqualTo(123);
        assertThat(part2(inputParsed)).isEqualTo(4713);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    @Value
    private static class Input {
        Map<Integer, Set<Integer>> orderRulesByBeforePN;
        List<List<Integer>> updates;
    }

    private static long part1(Input input) {
        // find which updates are valid, sum the middle PN
        return input.updates.stream()
                .filter(update -> isValid(update, input.orderRulesByBeforePN))
                .mapToInt(update -> findMiddle(update))
                .sum();
    }

    private static long part2(Input input) {
        return input.updates.stream()
                .filter(update -> !isValid(update, input.orderRulesByBeforePN))
                .map(update -> putInOrder(update, input.orderRulesByBeforePN))
                .mapToInt(update -> findMiddle(update))
                .sum();
    }

    private static Input parse(List<String> input) {
        Map<Integer, Set<Integer>> orderRulesByBeforePN = new HashMap<>();
        List<List<Integer>> updates = new ArrayList<>();

        Pattern orderRulePat = Pattern.compile("(\\d+)\\|(\\d+)");
        int lineIdx = 0;
        for (; ; lineIdx++) {
            String line = input.get(lineIdx);
            if (line.isEmpty()) {
                lineIdx++;
                break;
            }
            Matcher matcher = orderRulePat.matcher(line);
            checkState(matcher.matches());
            int a = Integer.parseInt(matcher.group(1));
            int b = Integer.parseInt(matcher.group(2));
            orderRulesByBeforePN.computeIfAbsent(a, (x) -> new HashSet<>()).add(b);
        }

        for (; lineIdx < input.size(); lineIdx++) {
            String line = input.get(lineIdx);
            updates.add(Splitter.on(',').splitToList(line)
                    .stream().map(Integer::parseInt)
                    .collect(Collectors.toList()));
        }

        return new Input(orderRulesByBeforePN, updates);
    }

    private static List<Integer> putInOrder(List<Integer> update, Map<Integer, Set<Integer>> orderRulesByBeforePN) {
        List<Integer> acc = new ArrayList<>();
        outerLoop:
        while (!update.isEmpty()) {
            idxLoop:
            for (int idx = 0; idx < update.size(); idx++) {
                int i = update.get(idx);
                // can i go next: are there no j with j|i
                for (int otherIdx = 0; otherIdx < update.size(); otherIdx++) {
                    if (otherIdx != idx) {
                        Set<Integer> afterOther = orderRulesByBeforePN.get(update.get(otherIdx));
                        if (afterOther != null && afterOther.contains(i)) {
                            continue idxLoop;
                        }
                    }
                }

                acc.add(i);
                update.remove(idx);
                continue outerLoop;
            }
        }

        return acc;
    }

    private static int findMiddle(List<Integer> update) {
        checkState(update.size() % 2 == 1);
        return update.get(update.size() / 2);
    }

    private static boolean isValid(List<Integer> update, Map<Integer, Set<Integer>> orderRulesByBeforePN) {
        for (int idx = 0; idx < update.size(); idx++) {
            int pn = update.get(idx);
            Set<Integer> afters = orderRulesByBeforePN.get(pn);
            if (afters != null) {
                for (int ii = 0; ii < idx; ii++) {
                    if (afters.contains(update.get(ii))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    static List<String> example = List.of(
            "47|53",
            "97|13",
            "97|61",
            "97|47",
            "75|29",
            "61|13",
            "75|53",
            "29|13",
            "97|29",
            "53|29",
            "61|53",
            "97|53",
            "61|29",
            "47|13",
            "75|47",
            "97|75",
            "47|61",
            "75|61",
            "47|29",
            "75|13",
            "53|13",
            "",
            "75,47,61,53,29",
            "97,61,53,29,13",
            "75,29,13",
            "75,97,47,61,53",
            "61,13,29",
            "97,13,75,29,47");
}

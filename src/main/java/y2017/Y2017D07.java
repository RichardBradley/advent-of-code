package y2017;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Iterables;
import com.google.common.io.Resources;
import lombok.Value;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;
import static java.util.stream.Collectors.toList;

public class Y2017D07 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        List<InputLine> lines = Resources.readLines(Resources.getResource("y2017/Y2017D07.txt"), StandardCharsets.UTF_8).stream()
                .map(x -> parse(x))
                .collect(toList());
        String bottom = findBottom(lines);
        System.out.println(bottom);
        part2(bottom, lines);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static void part2(String bottom, List<InputLine> lines) {
        Map<String, InputLine> specByName = lines.stream().collect(Collectors.toMap(x -> x.name, x -> x));
        findWeightAndCheckBalance(specByName, bottom);
    }

    private static int findWeightAndCheckBalance(Map<String, InputLine> specByName, String currName) {
        InputLine curr = specByName.get(currName);
        if (curr.children.isEmpty()) {
            return curr.weight;
        } else {
            List<Integer> childWeights = curr.children.stream()
                    .map(c -> findWeightAndCheckBalance(specByName, c))
                    .collect(toList());

            Map<Integer, Integer> countsByWeight =
                    childWeights.stream().collect(Collectors.toMap(x -> x, x -> 1, (a, b) -> a + b));
            if (countsByWeight.size() == 1) {
                // all same weight
                return childWeights.stream().mapToInt(x -> x).sum() + curr.weight;
            } else {
                checkState(countsByWeight.size() == 2);
                int badWeight = countsByWeight.entrySet().stream().filter(x -> x.getValue() == 1).findAny().get().getKey();
                int goodWeight = countsByWeight.entrySet().stream().filter(x -> x.getValue() > 1).findAny().get().getKey();
                int badChildIdx = childWeights.indexOf(badWeight);

                int diff = badWeight - goodWeight;
                System.out.printf("%s is too heavy by %s, should be %s\n",
                        curr.children.get(badChildIdx),
                        diff,
                        specByName.get(curr.children.get(badChildIdx)).weight - diff);

                return goodWeight * curr.children.size() + curr.weight;
            }
        }
    }

    private static String findBottom(List<InputLine> lines) {
        Set<String> candidates = new HashSet<>();
        for (InputLine line : lines) {
            if (!line.children.isEmpty()) {
                candidates.add(line.name);
            }
        }
        for (InputLine line : lines) {
            if (!line.children.isEmpty()) {
                candidates.removeAll(line.children);
            }
        }
        return Iterables.getOnlyElement(candidates);
    }

    @Value
    static class InputLine {
        String name;
        int weight;
        List<String> children;
    }

    static Pattern inputPat = Pattern.compile("(\\w+) \\((\\d+)\\)( -> ([\\w ,]+))?");

    private static InputLine parse(String line) {
        Matcher matcher = inputPat.matcher(line);
        checkState(matcher.matches());
        String name = matcher.group(1);
        int weight = Integer.parseInt(matcher.group(2));
        List<String> children = matcher.group(4) == null
                ? Collections.emptyList()
                : Splitter.on(", ").splitToList(matcher.group(4));
        return new InputLine(name, weight, children);
    }
}


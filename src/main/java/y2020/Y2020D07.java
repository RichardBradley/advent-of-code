package y2020;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class Y2020D07 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        Map<String, Map<String, Integer>> rules = parseRules(Resources.readLines(Resources.getResource("y2020/Y2020D07.txt"), StandardCharsets.UTF_8));

        part1(rules);
        part2(rules);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static void part1(Map<String, Map<String, Integer>> rules) {
        // How many bag colors can eventually contain at least one shiny gold bag?
        Set<String> validBagColours = new HashSet<>();
        for (String bagColour : rules.keySet()) {
            if (searchFor("shiny gold", validBagColours, bagColour, rules)) {
                validBagColours.add(bagColour);
            }
        }

        System.out.println("validBagColours size = " + validBagColours.size());
    }

    private static boolean searchFor(
            String target,
            Set<String> validBagColours,
            String currentBagColour,
            Map<String, Map<String, Integer>> rules) {
        if (!currentBagColour.equals(target)) {
            Map<String, Integer> currentRule = rules.get(currentBagColour);
            checkNotNull(currentRule);
            for (Map.Entry<String, Integer> childBag : currentRule.entrySet()) {
                String childBagColour = childBag.getKey();
                if (childBagColour.equals(target)
                        || searchFor(target, validBagColours, childBagColour, rules)) {
                    validBagColours.add(currentBagColour);
                    return true;
                }
            }
        }
        return false;
    }

    private static void part2(Map<String, Map<String, Integer>> rules) {
        // How many individual bags are required inside your single shiny gold bag?
        int bagCount = countBagsAndContents("shiny gold", rules) - 1;
        System.out.println("bags inside shiny gold = " + bagCount);
    }

    private static int countBagsAndContents(String currentBagColour, Map<String, Map<String, Integer>> rules) {
        Map<String, Integer> rule = rules.get(currentBagColour);
        checkNotNull(rule);
        return 1 + rule.entrySet().stream()
                .mapToInt(entry -> entry.getValue() * countBagsAndContents(entry.getKey(), rules))
                .sum();
    }

    private static Map<String, Map<String, Integer>> parseRules(List<String> lines) {
        // e.g. "mirrored gold bags contain 3 wavy brown bags, 5 posh beige bags, 3 light crimson bags, 3 vibrant salmon bags."
        Pattern outerPattern = Pattern.compile("([\\w ]+) bags contain ([\\w ,]+).");
        Pattern innerPattern = Pattern.compile("(\\d+) ([\\w ]+) bags?");
        Map<String, Map<String, Integer>> rulesByBagColour = new HashMap<>();
        for (String line : lines) {
            Matcher outerMatcher = outerPattern.matcher(line);
            checkState(outerMatcher.matches());
            String bagColour = outerMatcher.group(1);
            Map<String, Integer> containsCountByColour = new HashMap<>();
            String ruleSpec = outerMatcher.group(2);
            if (!"no other bags".equals(ruleSpec)) {
                Splitter.on(", ").split(ruleSpec).forEach(clause -> {
                    Matcher innerMatcher = innerPattern.matcher(clause);
                    checkState(innerMatcher.matches());
                    String innerBagColour = innerMatcher.group(2);
                    int innerBagCount = Integer.parseInt(innerMatcher.group(1));
                    checkState(innerBagCount > 0);
                    checkState(null == containsCountByColour.put(innerBagColour, innerBagCount));
                });
            }
            checkState(null ==
                    rulesByBagColour.put(bagColour, containsCountByColour));
        }
        return rulesByBagColour;
    }
}

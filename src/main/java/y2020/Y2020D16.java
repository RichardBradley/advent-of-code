package y2020;

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

public class Y2020D16 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        Spec input = parse(Resources.toString(Resources.getResource("y2020/Y2020D16.txt"), StandardCharsets.UTF_8));

        part1(input);

        part2(input);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static void part2(Spec input) {
        List<List<Integer>> validTickets = input.nearbyTickets.stream()
                .filter(fields -> fields.stream().noneMatch(val -> input.fieldsByName.values().stream().allMatch(fieldSpec -> !fieldSpec.isValid(val))))
                .collect(Collectors.toList());

        int fieldCount = validTickets.get(0).size();
        Map<Integer, String> knownFieldNamesByIndex = new HashMap<>();
        Set<String> knownFieldNames = new HashSet<>();

        while (knownFieldNames.size() < fieldCount) {
            boolean changesMade = false;
            for (int i = 0; i < fieldCount; i++) {
                if (!knownFieldNamesByIndex.containsKey(i)) {
                    Set<String> possibleFieldNames = new TreeSet<>(input.fieldsByName.keySet());
                    possibleFieldNames.removeAll(knownFieldNames);

                    for (List<Integer> validTicket : validTickets) {
                        for (Map.Entry<String, FieldSpec> fieldEntry : input.fieldsByName.entrySet()) {
                            if (!fieldEntry.getValue().isValid(validTicket.get(i))) {
                                possibleFieldNames.remove(fieldEntry.getKey());
                            }
                        }
                    }
                    if (possibleFieldNames.size() == 0) {
                        throw new IllegalStateException();
                    } else if (possibleFieldNames.size() == 1) {
                        String name = Iterables.getOnlyElement(possibleFieldNames);
                        knownFieldNames.add(name);
                        checkState(null == knownFieldNamesByIndex.put(i, name));
                        changesMade = true;
                    }
                }
            }
            checkState(changesMade);
        }

        // look for the six fields on your ticket that start with the word departure.
        // What do you get if you multiply those six values together?
        long acc = 1;
        for (Map.Entry<Integer, String> knownFieldNameByIndex : knownFieldNamesByIndex.entrySet()) {
            String name = knownFieldNameByIndex.getValue();
            int index = knownFieldNameByIndex.getKey();
            if (name.startsWith("departure")) {
                acc *= input.yourTicketValues.get(index);
            }
        }
        System.out.println("answer = " + acc);
    }

    private static void part1(Spec input) {
        // Adding together all of the invalid values produces your ticket scanning error rate
        // What is your ticket scanning error rate?
        int errorRate = input.nearbyTickets.stream()
                .mapToInt(ticket ->
                        ticket.stream().mapToInt(i -> i)
                                .filter(val -> input.fieldsByName.values().stream().allMatch(fieldSpec -> !fieldSpec.isValid(val)))
                                .sum())
                .sum();

        System.out.println("errorRate = " + errorRate);
    }

    private static Spec parse(String input) {
        String[] sections = input.split("\n\n");
        checkState(sections.length == 3);

        Map<String, FieldSpec> fieldsByName = new HashMap<>();
        Pattern fieldP = Pattern.compile("([\\w ]+): (\\d+)-(\\d+) or (\\d+)-(\\d+)");
        for (String line : sections[0].split("\n")) {
            Matcher fieldM = fieldP.matcher(line);
            checkState(fieldM.matches());
            checkState(null == fieldsByName.put(fieldM.group(1),
                    new FieldSpec(
                            Integer.parseInt(fieldM.group(2)),
                            Integer.parseInt(fieldM.group(3)),
                            Integer.parseInt(fieldM.group(4)),
                            Integer.parseInt(fieldM.group(5)))));
        }

        checkState(sections[1].startsWith("your ticket:\n"));
        List<Integer> yourTicketValues = Splitter.on(',').splitToList(sections[1].substring("your ticket:\n".length()))
                .stream().map(Integer::parseInt).collect(Collectors.toList());

        checkState(sections[2].startsWith("nearby tickets:\n"));
        List<List<Integer>> nearbyTickets = Splitter.on('\n').splitToList(sections[2].substring("nearby tickets:\n".length()))
                .stream().map(line ->
                        Splitter.on(',').splitToList(line)
                                .stream().map(Integer::parseInt).collect(Collectors.toList()))
                .collect(Collectors.toList());

        return new Spec(fieldsByName, yourTicketValues, nearbyTickets);
    }

    @Value
    static class Spec {
        Map<String, FieldSpec> fieldsByName;
        List<Integer> yourTicketValues;
        List<List<Integer>> nearbyTickets;
    }

    @Value
    static class FieldSpec {
        int range1LB;
        int range1UB;
        int range2LB;
        int range2UB;

        public boolean isValid(int val) {
            return (range1LB <= val && range1UB >= val)
                    || (range2LB <= val && range2UB >= val);
        }
    }
}

package y2018;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2018D7 {
    public static void main(String[] args) throws Exception {

        // 1
        assertThat(getAlphaOrder(testInput)).isEqualTo("CABDFE");

        System.out.println(getAlphaOrder(input));

        // 2
        assertThat(getParallelDuration(2, (c -> (c - 'A' + 1)), testInput)).isEqualTo(15);

        System.out.println(getParallelDuration(5, (c -> (c - 'A' + 61)), input));
    }

    private static String getAlphaOrder(Multimap<Character, Character> deps) {
        StringBuilder acc = new StringBuilder();
        Set<Character> completedSteps = new HashSet<>();

        Set<Character> allStepNames = new TreeSet<>();
        allStepNames.addAll(deps.keySet());
        allStepNames.addAll(deps.values());

        // find alphabetically first step where all deps are completed
        while (acc.length() < allStepNames.size()) {
            for (Character step : allStepNames) {
                if (!completedSteps.contains(step)) {
                    Collection<Character> stepDeps = deps.get(step);
                    if (stepDeps == null || completedSteps.containsAll(stepDeps)) {
                        // step is ready
                        acc.append(step);
                        completedSteps.add(step);
                        break;
                    }
                }
            }
        }
        return acc.toString();
    }


    private static int getParallelDuration(int workerCount, Function<Character, Integer> stepDurationFn, Multimap<Character, Character> deps) {
        char[][] workerAssigns = new char[workerCount][];
        for (int i = 0; i < workerAssigns.length; i++) {
            workerAssigns[i] = new char[10000];
        }

        Set<Character> allStepNames = new TreeSet<>();
        allStepNames.addAll(deps.keySet());
        allStepNames.addAll(deps.values());

        Set<Character> completedSteps = new HashSet<>();
        Set<Character> startedSteps = new HashSet<>();
        Multimap<Integer, Character> upcomingCompletionSections = Multimaps.newListMultimap(new HashMap<>(), ArrayList::new);

        for (int second = 0; ; second++) {

            completedSteps.addAll(upcomingCompletionSections.get(second));

            if (completedSteps.size() == allStepNames.size()) {
                // Done!
                return second;
            }

            for (int workerIdx = 0; workerIdx < workerAssigns.length; workerIdx++) {
                if (workerAssigns[workerIdx][second] != '\0') {
                    continue;
                }

                for (Character step : allStepNames) {
                    if (!completedSteps.contains(step) && !startedSteps.contains(step)) {
                        Collection<Character> stepDeps = deps.get(step);
                        if (stepDeps == null || completedSteps.containsAll(stepDeps)) {
                            // step is ready
                            Integer stepDuration = stepDurationFn.apply(step);
                            for (int i = 0; i < stepDuration; i++) {
                                workerAssigns[workerIdx][second + i] = step;
                            }
                            startedSteps.add(step);
                            upcomingCompletionSections.put(second + stepDuration, step);
                            break;
                        }
                    }
                }
            }
        }
    }

    private static Multimap<Character, Character> parse(String... deps) {
        ListMultimap<Character, Character> acc = Multimaps.newListMultimap(new HashMap<>(), ArrayList::new);
        Pattern pattern = Pattern.compile("Step ([A-Z]) must be finished before step ([A-Z]) can begin.");
        for (String dep : deps) {
            Matcher matcher = pattern.matcher(dep);
            checkState(matcher.matches());
            Character step = matcher.group(2).charAt(0);
            Character depStep = matcher.group(1).charAt(0);
            acc.put(step, depStep);
        }
        return acc;
    }

    static Multimap<Character, Character> testInput = parse(
            "Step C must be finished before step A can begin.",
            "Step C must be finished before step F can begin.",
            "Step A must be finished before step B can begin.",
            "Step A must be finished before step D can begin.",
            "Step B must be finished before step E can begin.",
            "Step D must be finished before step E can begin.",
            "Step F must be finished before step E can begin.");

    static Multimap<Character, Character> input = parse(new String[]{
            "Step I must be finished before step G can begin.",
            "Step J must be finished before step A can begin.",
            "Step L must be finished before step D can begin.",
            "Step V must be finished before step S can begin.",
            "Step U must be finished before step T can begin.",
            "Step F must be finished before step Z can begin.",
            "Step D must be finished before step A can begin.",
            "Step E must be finished before step Z can begin.",
            "Step C must be finished before step Q can begin.",
            "Step H must be finished before step X can begin.",
            "Step A must be finished before step Z can begin.",
            "Step Z must be finished before step M can begin.",
            "Step P must be finished before step Y can begin.",
            "Step N must be finished before step K can begin.",
            "Step R must be finished before step W can begin.",
            "Step K must be finished before step O can begin.",
            "Step W must be finished before step S can begin.",
            "Step G must be finished before step Q can begin.",
            "Step Q must be finished before step B can begin.",
            "Step S must be finished before step T can begin.",
            "Step B must be finished before step M can begin.",
            "Step T must be finished before step Y can begin.",
            "Step M must be finished before step O can begin.",
            "Step X must be finished before step O can begin.",
            "Step O must be finished before step Y can begin.",
            "Step C must be finished before step O can begin.",
            "Step B must be finished before step O can begin.",
            "Step T must be finished before step O can begin.",
            "Step S must be finished before step X can begin.",
            "Step E must be finished before step K can begin.",
            "Step Q must be finished before step M can begin.",
            "Step E must be finished before step P can begin.",
            "Step Q must be finished before step S can begin.",
            "Step E must be finished before step O can begin.",
            "Step D must be finished before step P can begin.",
            "Step X must be finished before step Y can begin.",
            "Step I must be finished before step U can begin.",
            "Step B must be finished before step X can begin.",
            "Step F must be finished before step T can begin.",
            "Step B must be finished before step T can begin.",
            "Step V must be finished before step R can begin.",
            "Step I must be finished before step Q can begin.",
            "Step I must be finished before step A can begin.",
            "Step M must be finished before step X can begin.",
            "Step Z must be finished before step S can begin.",
            "Step C must be finished before step S can begin.",
            "Step T must be finished before step M can begin.",
            "Step K must be finished before step X can begin.",
            "Step Z must be finished before step P can begin.",
            "Step V must be finished before step H can begin.",
            "Step Z must be finished before step B can begin.",
            "Step M must be finished before step Y can begin.",
            "Step C must be finished before step K can begin.",
            "Step W must be finished before step Y can begin.",
            "Step J must be finished before step Z can begin.",
            "Step Q must be finished before step O can begin.",
            "Step T must be finished before step X can begin.",
            "Step P must be finished before step Q can begin.",
            "Step P must be finished before step K can begin.",
            "Step D must be finished before step M can begin.",
            "Step P must be finished before step N can begin.",
            "Step S must be finished before step B can begin.",
            "Step H must be finished before step Y can begin.",
            "Step R must be finished before step K can begin.",
            "Step G must be finished before step S can begin.",
            "Step P must be finished before step S can begin.",
            "Step C must be finished before step Z can begin.",
            "Step Q must be finished before step Y can begin.",
            "Step F must be finished before step R can begin.",
            "Step N must be finished before step B can begin.",
            "Step G must be finished before step M can begin.",
            "Step E must be finished before step X can begin.",
            "Step D must be finished before step E can begin.",
            "Step D must be finished before step C can begin.",
            "Step U must be finished before step O can begin.",
            "Step H must be finished before step Z can begin.",
            "Step L must be finished before step C can begin.",
            "Step L must be finished before step F can begin.",
            "Step V must be finished before step D can begin.",
            "Step F must be finished before step X can begin.",
            "Step V must be finished before step W can begin.",
            "Step S must be finished before step Y can begin.",
            "Step K must be finished before step T can begin.",
            "Step D must be finished before step Z can begin.",
            "Step C must be finished before step W can begin.",
            "Step V must be finished before step M can begin.",
            "Step F must be finished before step H can begin.",
            "Step A must be finished before step M can begin.",
            "Step G must be finished before step Y can begin.",
            "Step H must be finished before step M can begin.",
            "Step N must be finished before step W can begin.",
            "Step J must be finished before step K can begin.",
            "Step C must be finished before step B can begin.",
            "Step Z must be finished before step Y can begin.",
            "Step L must be finished before step E can begin.",
            "Step G must be finished before step B can begin.",
            "Step Q must be finished before step T can begin.",
            "Step D must be finished before step W can begin.",
            "Step H must be finished before step G can begin.",
            "Step L must be finished before step O can begin.",
            "Step N must be finished before step O can begin."
    });
}

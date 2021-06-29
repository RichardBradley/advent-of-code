package y2017;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2017D12 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        List<String> input = Resources.readLines(Resources.getResource("y2017/Y2017D12.txt"), StandardCharsets.UTF_8);

        assertThat(group0Size(example)).isEqualTo(6);
        assertThat(group0Size(input)).isEqualTo(113);

        assertThat(countGroups(example)).isEqualTo(2);
        assertThat(countGroups(input)).isEqualTo(202);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static int countGroups(List<String> input) {
        Map<Integer, Set<Integer>> connectionsFromMap = parse(input);
        Set<Integer> reachable = new HashSet<>();
        for (int groupCount = 0; ; groupCount++) {
            Optional<Integer> firstNotReachable = connectionsFromMap.keySet().stream()
                    .filter(i -> !reachable.contains(i))
                    .findFirst();

            if (!firstNotReachable.isPresent()) {
                return groupCount;
            }
            reachable.add(firstNotReachable.get());

            boolean changesMade;
            do {
                changesMade = false;
                for (Integer i : new ArrayList<>(reachable)) {
                    changesMade |= reachable.addAll(connectionsFromMap.get(i));
                }
            } while (changesMade);
        }
    }

    private static int group0Size(List<String> input) {
        Map<Integer, Set<Integer>> connectionsFromMap = parse(input);

        Set<Integer> reachableFromZero = new HashSet<>();
        reachableFromZero.add(0);
        boolean changesMade;
        do {
            changesMade = false;
            for (Integer i : new ArrayList<>(reachableFromZero)) {
                changesMade |= reachableFromZero.addAll(connectionsFromMap.get(i));
            }
        } while (changesMade);

        return reachableFromZero.size();
    }

    private static Map<Integer, Set<Integer>> parse(List<String> input) {
        Map<Integer, Set<Integer>> connectionsFromMap = new HashMap<>();
        Pattern linePat = Pattern.compile("(\\d+) <-> (.*)");
        for (String line : input) {
            Matcher matcher = linePat.matcher(line);
            checkState(matcher.matches());
            int from = Integer.parseInt(matcher.group(1));
            Set<Integer> connections = connectionsFromMap.computeIfAbsent(from, i -> new HashSet<>());
            Splitter.on(",").trimResults().split(matcher.group(2)).forEach(i -> {
                connections.add(Integer.parseInt(i));
            });
        }
        return connectionsFromMap;
    }

    static List<String> example = ImmutableList.of(
            "0 <-> 2",
            "1 <-> 1",
            "2 <-> 0, 3, 4",
            "3 <-> 2, 4",
            "4 <-> 2, 3, 6",
            "5 <-> 6",
            "6 <-> 4, 5");
}


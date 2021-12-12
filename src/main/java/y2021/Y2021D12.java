package y2021;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2021D12 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            List<String> input = Resources.readLines(Resources.getResource("y2021/Y2021D12.txt"), StandardCharsets.UTF_8);

            // 1
            assertThat(part1(example)).isEqualTo(10);
            assertThat(part1(example2)).isEqualTo(19);
            assertThat(part1(input)).isEqualTo(3887);

            // 2
            assertThat(part2(example)).isEqualTo(36);
            assertThat(part2(input)).isEqualTo(104834);

        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private static long part1(List<String> input) {
        Map<String, Set<String>> connections = parseConnections(input);

        // DFS
        return countPaths("start", new HashSet<>(), false, connections);
    }

    private static long part2(List<String> input) {
        Map<String, Set<String>> connections = parseConnections(input);

        return countPaths("start", new HashSet<>(), true, connections);
    }

    private static Map<String, Set<String>> parseConnections(List<String> input) {
        Map<String, Set<String>> connections = new HashMap<>();
        for (String line : input) {
            String[] parts = line.split("-");
            checkState(parts.length == 2);
            connections.computeIfAbsent(parts[0], x -> new HashSet<>())
                    .add(parts[1]);
            connections.computeIfAbsent(parts[1], x -> new HashSet<>())
                    .add(parts[0]);
        }
        return connections;
    }

    private static long countPaths(String curr, Set<String> visitedSmall, boolean mayRevisitOneSmall, Map<String, Set<String>> connections) {
        long count = 0;
        for (String possibleNext : connections.get(curr)) {
            if ("start".equals(possibleNext)) {
                // don't revisit start
            } else if ("end".equals(possibleNext)) {
                // System.out.println(visited + ",end");
                count++;
            } else if (isSmall(possibleNext)) {
                if (!visitedSmall.add(possibleNext)) {
                    if (mayRevisitOneSmall) {
                        count += countPaths(possibleNext, visitedSmall, false, connections);
                    } else {
                        // cannot proceed
                    }
                } else {
                    count += countPaths(possibleNext, visitedSmall, mayRevisitOneSmall, connections);
                    visitedSmall.remove(possibleNext);
                }
            } else {
                count += countPaths(possibleNext, visitedSmall, mayRevisitOneSmall, connections);
            }
        }
        return count;
    }

    private static boolean isSmall(String name) {
        char c = name.charAt(0);
        return c > 'a' && c <= 'z';
    }

    private static List<String> example = List.of(
            "start-A",
            "start-b",
            "A-c",
            "A-b",
            "b-d",
            "A-end",
            "b-end"
    );

    private static List<String> example2 = List.of(
            "dc-end",
            "HN-start",
            "start-kj",
            "dc-start",
            "dc-HN",
            "LN-dc",
            "HN-end",
            "kj-sa",
            "kj-HN",
            "kj-dc");
}

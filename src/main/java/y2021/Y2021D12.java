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
            assertThat(part2(input)).isEqualTo(-1);

        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private static long part1(List<String> input) {
        Map<String, Set<String>> connections = new HashMap<>();
        for (String line : input) {
            String[] parts = line.split("-");
            checkState(parts.length == 2);
            connections.computeIfAbsent(parts[0], x -> new HashSet<>())
                    .add(parts[1]);
            connections.computeIfAbsent(parts[1], x -> new HashSet<>())
                    .add(parts[0]);
        }

        // DFS
        return countPaths("start", new ArrayList<>(), new HashSet<>(), connections);
    }

    private static long countPaths(String curr, List<String> visited, Set<String> visitedSmall, Map<String, Set<String>> connections) {
        long count = 0;
        for (String possibleNext : connections.get(curr)) {
            if ("start".equals(possibleNext)) {
                // don't revisit start -- unwritten rule
            } else if ("end".equals(possibleNext)) {
                // System.out.println(visited + ",end");
                count++;
            } else if (isSmall(possibleNext)) {
                if (!visitedSmall.add(possibleNext)) {
                    // cannot proceed
                } else {
                    visited.add(possibleNext);
                    count += countPaths(possibleNext, visited, visitedSmall, connections);
                    visited.remove(visited.size() - 1);
                    visitedSmall.remove(possibleNext);
                }
            } else {
                visited.add(possibleNext);
                count += countPaths(possibleNext, visited, visitedSmall, connections);
                visited.remove(visited.size() - 1);
            }
        }
        return count;
    }


    private static long part2(List<String> input) {
        Map<String, Set<String>> connections = new HashMap<>();
        for (String line : input) {
            String[] parts = line.split("-");
            checkState(parts.length == 2);
            connections.computeIfAbsent(parts[0], x -> new HashSet<>())
                    .add(parts[1]);
            connections.computeIfAbsent(parts[1], x -> new HashSet<>())
                    .add(parts[0]);
        }

        // DFS
        return countPaths2("start", new ArrayList<>(), new HashSet<>(), false, connections);
    }

    private static long countPaths2(String curr, List<String> visited, Set<String> visitedSmall, boolean hasRevisitedOneSmall, Map<String, Set<String>> connections) {
        long count = 0;
        for (String possibleNext : connections.get(curr)) {
            if ("start".equals(possibleNext)) {
                // don't revisit start
            } else if ("end".equals(possibleNext)) {
                // System.out.println(visited + ",end");
                count++;
            } else if (isSmall(possibleNext)) {
                if (!visitedSmall.add(possibleNext)) {
                    if (!hasRevisitedOneSmall) {
                        visited.add(possibleNext);
                        count += countPaths2(possibleNext, visited, visitedSmall, true, connections);
                        visited.remove(visited.size() - 1);
                    } else {
                        // cannot proceed
                    }
                } else {
                    visited.add(possibleNext);
                    count += countPaths2(possibleNext, visited, visitedSmall, hasRevisitedOneSmall, connections);
                    visited.remove(visited.size() - 1);
                    visitedSmall.remove(possibleNext);
                }
            } else {
                visited.add(possibleNext);
                count += countPaths2(possibleNext, visited, visitedSmall, hasRevisitedOneSmall, connections);
                visited.remove(visited.size() - 1);
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

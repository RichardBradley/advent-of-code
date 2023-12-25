package y2023;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.io.Resources;
import org.apache.commons.math3.util.Pair;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2023D25 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            List<String> input = Resources.readLines(Resources.getResource("y2023/Y2023D25.txt"), StandardCharsets.UTF_8);

            // 1
            assertThat(part1b(example)).isEqualTo(54);
            assertThat(part1b(input)).isEqualTo(589036);


        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private static Pair createOrderedPair(String a, String b) {
        int cmp = a.compareTo(b);
        if (cmp < 0) {
            return new Pair(a, b);
        } else if (cmp > 0) {
            return new Pair(b, a);
        } else {
            throw new IllegalArgumentException();
        }
    }

    private static long part1b(List<String> input) {

        SetMultimap<String, String> connectionsMap = Multimaps.newSetMultimap(new HashMap<>(), HashSet::new);
        // lexically least is LHS in a wire
        List<Pair<String, String>> allWires = new ArrayList<>();
        Set<String> allNodes = new HashSet<>();

        Pattern p = Pattern.compile("(\\w+): ([\\w ]+)");
        for (String line : input) {
            Matcher m = p.matcher(line);
            checkState(m.matches());
            String lhs = m.group(1);
            for (String rhs : Splitter.on(" ").trimResults().splitToList(m.group(2))) {
                checkState(!lhs.equals(rhs));
                allNodes.add(lhs);
                allNodes.add(rhs);
                connectionsMap.put(lhs, rhs);
                connectionsMap.put(rhs, lhs);
                allWires.add(createOrderedPair(lhs, rhs));
            }
        }

        // https://www.reddit.com/r/adventofcode/comments/18qbsxs/comment/kewcfk2/?utm_source=reddit&utm_medium=web2x&context=3
        // Give each node a random score in the range [-1,1].
        // Then set each node to the average of the scores of its neighbours, and rescale so that the minimum
        // and maximum scores remain -1 and 1. Given the nature of the graph, we expect it to converge to a
        // case where one of the components post-cut ends up with score 1 and the other with score -1.
        // (Pretty sure this could be proved formally.)
        // Count how many edges there are between a node with score > 0 and a node with score < 0.
        // If there are exactly 3, we are done: just count how many nodes there are on each side of the axis.
        // On my data, it took 19 rounds to converge to a solution.
        for (int reset = 0; reset < 5; reset++) {

            Random rnd = new Random();
            Map<String, Double> scores = new HashMap<>();
            for (String node : allNodes) {
                scores.put(node, rnd.nextDouble() * 2 - 1);
            }

            for (int i = 0; i < 50; i++) {
                {
                    Map<String, Double> nextScores = new HashMap<>();
                    for (String node : allNodes) {
                        double sum = 0;
                        int count = 0;
                        for (String n : connectionsMap.get(node)) {
                            sum += scores.get(n);
                            count++;
                        }
                        nextScores.put(node, sum / count);
                    }
                    scores = nextScores;
                }

                // rescale
                double min = scores.values().stream().mapToDouble(d -> d).min().getAsDouble();
                double max = scores.values().stream().mapToDouble(d -> d).max().getAsDouble();
                for (String node : scores.keySet()) {
                    scores.put(node, (scores.get(node) - min) / (max - min) * 2.0 - 1.0);
                }

                // Count how many edges there are between a node with score > 0 and a node with score < 0.
                // If there are exactly 3, we are done:
                Set<Pair<String, String>> keyWires = new HashSet<>();
                for (Pair<String, String> wire : allWires) {
                    if (Double.compare(0, scores.get(wire.getFirst()))
                            != Double.compare(0, scores.get(wire.getSecond()))) {
                        keyWires.add(wire);
                    }
                }
                if (keyWires.size() == 3) {
                    List<Set<String>> groups = findGroups(connectionsMap, allNodes, keyWires);
                    if (groups.size() == 2) {
                        return groups.get(0).size() * groups.get(1).size();
                    }
                }
            }
        }
        throw new IllegalArgumentException();
    }

    private static long part1BruteForce(List<String> input) {
        // Find the three wires you need to disconnect in order to divide the components into two separate groups.
        // What do you get if you multiply the sizes of these two groups together?

        SetMultimap<String, String> connectionsMap = Multimaps.newSetMultimap(new HashMap<>(), HashSet::new);
        // lexically least is LHS in a wire
        List<Pair<String, String>> allWires = new ArrayList<>();
        Set<String> allNodes = new HashSet<>();

        Pattern p = Pattern.compile("(\\w+): ([\\w ]+)");
        for (String line : input) {
            Matcher m = p.matcher(line);
            checkState(m.matches());
            String lhs = m.group(1);
            for (String rhs : Splitter.on(" ").trimResults().splitToList(m.group(2))) {
                checkState(!lhs.equals(rhs));
                allNodes.add(lhs);
                allNodes.add(rhs);
                connectionsMap.put(lhs, rhs);
                connectionsMap.put(rhs, lhs);
                allWires.add(createOrderedPair(lhs, rhs));
            }
        }


        // Find the three wires you need to disconnect:
        long lastReportTimeMillis = 0;

        Set<Pair<String, String>> disconnected = new HashSet<>();
        for (int i = 0; i < allWires.size(); i++) {
            disconnected.add(allWires.get(i));
            for (int j = i + 1; j < allWires.size(); j++) {
                disconnected.add(allWires.get(j));
                for (int k = j + 1; k < allWires.size(); k++) {
                    disconnected.add(allWires.get(k));

                    List<Set<String>> groups = findGroups(connectionsMap, allNodes, disconnected);
                    if (groups.size() == 2) {
                        return groups.get(0).size() * groups.get(1).size();
                    }

                    disconnected.remove(allWires.get(k));
                }
                disconnected.remove(allWires.get(j));

                if (System.currentTimeMillis() - lastReportTimeMillis > 10000) {
                    lastReportTimeMillis = System.currentTimeMillis();
                    System.out.printf("%s i = %s of %s\n",
                            Instant.now(),
                            i,
                            allWires.size());
                }
            }
            disconnected.remove(allWires.get(i));
        }
        throw new IllegalStateException();
    }

    private static List<Set<String>> findGroups(SetMultimap<String, String> connections, Set<String> allNodes, Set<Pair<String, String>> disconnected) {
        Set<String> visited = new HashSet<>();
        List<Set<String>> groups = new ArrayList<>();
        for (String node : allNodes) {
            if (visited.add(node)) {
                Set<String> group = new HashSet<>();
                groups.add(group);
                group.add(node);

                Queue<String> toVisit = new ArrayDeque<>();
                toVisit.add(node);
                String next;
                while (null != (next = toVisit.poll())) {
                    visited.add(next);
                    for (String dest : connections.get(next)) {
                        Pair<String, String> wire = createOrderedPair(next, dest);
                        if (!disconnected.contains(wire)) {
                            if (group.add(dest)) {
                                toVisit.add(dest);
                            }
                        }
                    }
                }
            }
        }

        return groups;
    }

    static List<String> example = List.of(
            "jqt: rhn xhk nvd",
            "rsh: frs pzl lsr",
            "xhk: hfx",
            "cmg: qnr nvd lhk bvb",
            "rhn: xhk bvb hfx",
            "bvb: xhk hfx",
            "pzl: lsr hfx nvd",
            "qnr: nvd",
            "ntq: jqt hfx bvb xhk",
            "nvd: lhk",
            "lsr: lhk",
            "rzs: qnr cmg lsr rsh",
            "frs: qnr lhk lsr"
    );
}

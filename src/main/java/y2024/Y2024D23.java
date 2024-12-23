package y2024;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Sets;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static aoc.Common.loadInputFromResources;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;
import static java.util.Collections.emptySet;

public class Y2024D23 {

    public static void main(String[] args) throws Exception {
        List<String> input = loadInputFromResources();
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(part1(example)).isEqualTo(7);
        assertThat(part1(input)).isEqualTo(1119);

        // 2
        assertThat(part2(example)).isEqualTo("co,de,ka,ta");
        assertThat(part2(input)).isEqualTo("av,fr,gj,hk,ii,je,jo,lq,ny,qd,uq,wq,xc");

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static long part1(List<String> input) {
        Map<String, Set<String>> connections = new HashMap<>();
        Pattern p = Pattern.compile("([a-z]+)-([a-z]+)");
        for (String line : input) {
            Matcher m = p.matcher(line);
            checkState(m.matches());
            addConn(connections, m.group(1), m.group(2));
            addConn(connections, m.group(2), m.group(1));
        }

        int groupsOfThreeStartingTCount = 0;
        for (String a : connections.keySet()) {
            boolean aStartsWithT = a.startsWith("t");
            Set<String> conns = connections.get(a);
            if (conns.size() >= 3) {
                ArrayList<String> connsL = new ArrayList<>(conns);
                for (int i = 0; i < connsL.size() - 1; i++) {
                    String b = connsL.get(i);
                    boolean bStartsWithT = b.startsWith("t");
                    for (int j = i + 1; j < connsL.size(); j++) {
                        String c = connsL.get(j);
                        if (connections.getOrDefault(b, emptySet()).contains(c)) {
                            boolean cStartsWithT = c.startsWith("t");
                            if (aStartsWithT || bStartsWithT || cStartsWithT) {
                                groupsOfThreeStartingTCount++;
                            }
                        }
                    }
                }
            }
        }

        return groupsOfThreeStartingTCount / 3;
    }

    private static void addConn(Map<String, Set<String>> connections, String from, String to) {
        connections.computeIfAbsent(from, k -> new HashSet<>()).add(to);
    }

    private static String part2(List<String> input) {
        Map<String, Set<String>> connections = new HashMap<>();
        Pattern p = Pattern.compile("([a-z]+)-([a-z]+)");
        for (String line : input) {
            Matcher m = p.matcher(line);
            checkState(m.matches());
            addConn(connections, m.group(1), m.group(2));
            addConn(connections, m.group(2), m.group(1));
        }

        int maxConnectionCount = connections.values().stream().mapToInt(Set::size).max().getAsInt();
        Set<String> computers = new HashSet<>(connections.keySet());
        System.out.printf("%s computers, maxConnectionCount is %s\n", computers.size(), maxConnectionCount);

        for (int n = maxConnectionCount; n > 0; n--) {
            Set<String> matches = new HashSet<>();
            for (Map.Entry<String, Set<String>> e : connections.entrySet()) {
                String startingComp = e.getKey();
                Set<String> conns = e.getValue();

                // Find a fully connected group containing `startingComp` of size `n`
                for (Set<String> possibleConns : Sets.combinations(conns, n - 1)) {
                    if (possibleConns.stream().allMatch(a ->
                            possibleConns.stream().allMatch(b ->
                                    a.equals(b) ||
                                            connections.getOrDefault(a, emptySet()).contains(b)))) {

                        TreeSet<String> g = new TreeSet<>(possibleConns);
                        g.add(startingComp);
                        matches.add(
                                g.stream().collect(Collectors.joining(",")));
                    }
                }
            }

            if (!matches.isEmpty()) {
                return matches.stream().collect(Collectors.joining("\n"));
            }
        }

        return "not found";
    }

    static List<String> example = List.of(
            "kh-tc",
            "qp-kh",
            "de-cg",
            "ka-co",
            "yn-aq",
            "qp-ub",
            "cg-tb",
            "vc-aq",
            "tb-ka",
            "wh-tc",
            "yn-cg",
            "kh-ub",
            "ta-co",
            "de-co",
            "tc-td",
            "tb-wq",
            "wh-td",
            "ta-ka",
            "td-qp",
            "aq-cg",
            "wq-ub",
            "ub-vc",
            "de-ta",
            "wq-aq",
            "wq-vc",
            "wh-yn",
            "ka-de",
            "kh-ta",
            "co-tc",
            "wh-qp",
            "tb-vc",
            "td-yn");
}

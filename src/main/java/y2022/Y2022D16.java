package y2022;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;
import lombok.Value;
import org.apache.commons.math3.util.Pair;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2022D16 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        List<String> input = Resources.readLines(Resources.getResource("y2022/Y2022D16.txt"), StandardCharsets.UTF_8);

        // 1
        assertThat(part1(example)).isEqualTo(1651);
        System.out.println("example 1 ok");
        System.out.println(part1(input)); // 1728

        // 2
        assertThat(part2(example)).isEqualTo(1707);
        System.out.println("example 2 ok");
        System.out.println(part2(input)); // 2304

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    @Value
    static class ActorState {
        String location;
        int timeRemaining;
    }

    private static int part1(List<String> input) {
        // Exhaustive DFS
        World world = parse(input);
        AtomicInteger bestPressure = new AtomicInteger();
        findBestPath(
                bestPressure,
                world,
                Collections.emptySet(),
                "AA",
                30,
                "AA",
                0,
                0);
        return bestPressure.get();
    }

    private static int part2(List<String> input) {
        // Exhaustive DFS
        World world = parse(input);
        AtomicInteger bestPressure = new AtomicInteger();
        findBestPath(
                bestPressure,
                world,
                Collections.emptySet(),
                "AA",
                26,
                "AA",
                26,
                0);
        return bestPressure.get();
    }

    private static void findBestPath(
            AtomicInteger bestPressure,
            World world,
            Set<String> openValves,
            String a1Loc,
            int a1TimeRemaining,
            String a2Loc,
            int a2TimeRemaining,
            int finalPressure) {
        if (finalPressure > bestPressure.get()) {
            // System.out.println("New best pressure: " + finalPressure);
            bestPressure.set(finalPressure);
        }

        if (a2TimeRemaining > a1TimeRemaining) {
            int tmpT = a1TimeRemaining;
            String tmpS = a1Loc;
            a1TimeRemaining = a2TimeRemaining;
            a1Loc = a2Loc;
            a2TimeRemaining = tmpT;
            a2Loc = tmpS;
        }

        // Move a1
        // I think there might be an edge case bug here: if a1 has no valid moves but a2 does, despite
        // a1 having longer left than a2, then we might not add a2's final move.
        // This takes 116s, but doesn't seem to complete if we check both a1 and a2 here.
        for (String valve : world.getLocationsWithNonZeroValves().keySet()) {
            if (!openValves.contains(valve)) {
                int timeToOpenValve = 1 + world.locationDistances.get(new Pair(a1Loc, valve));
                if (timeToOpenValve < a1TimeRemaining) {
                    int newTimeRemaining = a1TimeRemaining - timeToOpenValve;
                    int newPressure = finalPressure + world.locations.get(valve).flowRateIfOpen * newTimeRemaining;
                    findBestPath(
                            bestPressure,
                            world,
                            add(openValves, valve),
                            valve,
                            newTimeRemaining,
                            a2Loc,
                            a2TimeRemaining,
                            newPressure);
                }
            }
        }
    }

    @Value
    static class World {
        Map<String, Location> locations;
        Map<Pair<String, String>, Integer> locationDistances;
        Map<String, Location> locationsWithNonZeroValves;
    }

    @Value
    static class Location {
        String name;
        int flowRateIfOpen;
        String[] tunnelDestinations;
    }

    private static Set<String> add(Set<String> xs, String x) {
        return new ImmutableSet.Builder<String>().addAll(xs).add(x).build();
    }

    private static World parse(List<String> input) {
        Map<String, Location> locations = new HashMap<>();
        Pattern p = Pattern.compile("Valve (\\w+) has flow rate=(\\d+); tunnels? leads? to valves? ([\\w ,]+)");
        for (String line : input) {
            Matcher m = p.matcher(line);
            checkState(m.matches());
            String[] tunnelDestinations = m.group(3).split(", ");
            locations.put(m.group(1),
                    new Location(
                            m.group(1),
                            Integer.parseInt(m.group(2)),
                            tunnelDestinations));
        }

        // qq could reindex to ints, rather than string names??

        // Floyd–Warshall min paths between each location:
        Map<Pair<String, String>, Integer> locationDistances = new HashMap<>();
        for (Location location : locations.values()) {
            for (String tunnelDestination : location.tunnelDestinations) {
                locationDistances.put(new Pair<>(location.name, tunnelDestination), 1);
            }
        }
        List<String> locNames = locations.values().stream().map(l -> l.name).collect(Collectors.toList());
        for (int k = 0; k < locNames.size(); k++) {
            for (int i = 0; i < locNames.size(); i++) {
                for (int j = 0; j < locNames.size(); j++) {
                    Pair<String, String> edgeIJ = Pair.create(locNames.get(i), locNames.get(j));
                    Integer distIJ = locationDistances.get(edgeIJ);
                    Pair<String, String> edgeIK = Pair.create(locNames.get(i), locNames.get(k));
                    Integer distIK = locationDistances.get(edgeIK);
                    Pair<String, String> edgeKJ = Pair.create(locNames.get(k), locNames.get(j));
                    Integer distKJ = locationDistances.get(edgeKJ);
                    if (distIK != null && distKJ != null &&
                            (distIJ == null || distIJ > (distIK + distKJ))) {
                        locationDistances.put(edgeIJ, distIK + distKJ);
                    }
                }
            }
        }

        Map<String, Location> locationsWithNonZeroValves = locations.entrySet()
                .stream()
                .filter(e -> e.getValue().flowRateIfOpen > 0)
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

        return new World(locations, locationDistances, locationsWithNonZeroValves);
    }

    private static List<String> example = List.of(
            "Valve AA has flow rate=0; tunnels lead to valves DD, II, BB",
            "Valve BB has flow rate=13; tunnels lead to valves CC, AA",
            "Valve CC has flow rate=2; tunnels lead to valves DD, BB",
            "Valve DD has flow rate=20; tunnels lead to valves CC, AA, EE",
            "Valve EE has flow rate=3; tunnels lead to valves FF, DD",
            "Valve FF has flow rate=0; tunnels lead to valves EE, GG",
            "Valve GG has flow rate=0; tunnels lead to valves FF, HH",
            "Valve HH has flow rate=22; tunnel leads to valve GG",
            "Valve II has flow rate=0; tunnels lead to valves AA, JJ",
            "Valve JJ has flow rate=21; tunnel leads to valve II");
}

package y2025;

import com.google.common.base.Stopwatch;
import org.apache.commons.math3.util.Pair;

import javax.vecmath.Point3d;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static aoc.Common.loadInputFromResources;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2025D08 {

    public static void main(String[] args) throws Exception {
        List<String> input = loadInputFromResources();
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(part1(example, 10)).isEqualTo(40);
        assertThat(part1(input, 1000)).isEqualTo(117000);

        // 2
        assertThat(part2(example)).isEqualTo(25272);
        assertThat(part2(input)).isEqualTo(8368033065L);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static long part1(List<String> input, int connectionsToMake) {
        List<Point3d> lights = input.stream().map(line -> {
                    String[] xs = line.split(",");
                    checkState(xs.length == 3);
                    return new Point3d(Double.parseDouble(xs[0]), Double.parseDouble(xs[1]), Double.parseDouble(xs[2]));
                })
                .collect(Collectors.toList());

        TreeSet<Pair<Point3d, Point3d>> pointPairsByDist = new TreeSet<>(
                Comparator.comparingDouble(p -> p.getFirst().distance(p.getSecond())));

        for (int i = 0; i < lights.size(); i++) {
            Point3d pi = lights.get(i);
            for (int j = i + 1; j < lights.size(); j++) {
                pointPairsByDist.add(new Pair<>(pi, lights.get(j)));
            }
        }

        List<Set<Point3d>> circuits = new ArrayList<>();
        for (int i = 0; i < connectionsToMake; i++) {
            Pair<Point3d, Point3d> closestPair = pointPairsByDist.pollFirst();
            pointPairsByDist.remove(closestPair);

            Optional<Set<Point3d>> firstCircuit = circuits.stream()
                    .filter(circuit -> circuit.contains(closestPair.getFirst()))
                    .findFirst();
            Optional<Set<Point3d>> secondCircuit = circuits.stream()
                    .filter(circuit -> circuit.contains(closestPair.getSecond()))
                    .findFirst();

            if (firstCircuit.isPresent() && secondCircuit.isPresent()) {
                if (firstCircuit.equals(secondCircuit)) {
                    // both points already in same circuit, do nothing
                    continue;
                } else {
                    circuits.remove(secondCircuit.get());
                    firstCircuit.get().addAll(secondCircuit.get());
                }
            } else if (firstCircuit.isPresent()) {
                firstCircuit.get().add(closestPair.getSecond());
            } else if (secondCircuit.isPresent()) {
                secondCircuit.get().add(closestPair.getFirst());
            } else {
                Set<Point3d> newCircuit = new HashSet<>();
                newCircuit.add(closestPair.getFirst());
                newCircuit.add(closestPair.getSecond());
                circuits.add(newCircuit);
            }
        }

        circuits.sort(Comparator.comparingInt((Set<Point3d> s) -> s.size()).reversed());

        return circuits.stream().limit(3).mapToLong(Set::size).reduce(1, (a, b) -> a * b);
    }

    private static long part2(List<String> input) {
        List<Point3d> lights = input.stream().map(line -> {
                    String[] xs = line.split(",");
                    checkState(xs.length == 3);
                    return new Point3d(Double.parseDouble(xs[0]), Double.parseDouble(xs[1]), Double.parseDouble(xs[2]));
                })
                .collect(Collectors.toList());

        TreeSet<Pair<Point3d, Point3d>> pointPairsByDist = new TreeSet<>(
                Comparator.comparingDouble(p -> p.getFirst().distance(p.getSecond())));

        for (int i = 0; i < lights.size(); i++) {
            Point3d pi = lights.get(i);
            for (int j = i + 1; j < lights.size(); j++) {
                pointPairsByDist.add(new Pair<>(pi, lights.get(j)));
            }
        }

        List<Set<Point3d>> circuits = new ArrayList<>();
        for (int i = 0; ; i++) {
            Pair<Point3d, Point3d> closestPair = pointPairsByDist.pollFirst();
            pointPairsByDist.remove(closestPair);

            Optional<Set<Point3d>> firstCircuit = circuits.stream()
                    .filter(circuit -> circuit.contains(closestPair.getFirst()))
                    .findFirst();
            Optional<Set<Point3d>> secondCircuit = circuits.stream()
                    .filter(circuit -> circuit.contains(closestPair.getSecond()))
                    .findFirst();

            if (firstCircuit.isPresent() && secondCircuit.isPresent()) {
                if (firstCircuit.equals(secondCircuit)) {
                    continue;
                } else {
                    circuits.remove(secondCircuit.get());
                    firstCircuit.get().addAll(secondCircuit.get());
                }
            } else if (firstCircuit.isPresent()) {
                firstCircuit.get().add(closestPair.getSecond());
            } else if (secondCircuit.isPresent()) {
                secondCircuit.get().add(closestPair.getFirst());
            } else {
                Set<Point3d> newCircuit = new HashSet<>();
                newCircuit.add(closestPair.getFirst());
                newCircuit.add(closestPair.getSecond());
                circuits.add(newCircuit);
            }

            if (circuits.size() == 1 && circuits.get(0).size() == lights.size()) {
                return (long) (closestPair.getFirst().x * closestPair.getSecond().x);
            }
        }
    }

    static List<String> example = List.of(
            "162,817,812",
            "57,618,57",
            "906,360,560",
            "592,479,940",
            "352,342,300",
            "466,668,158",
            "542,29,236",
            "431,825,988",
            "739,650,466",
            "52,470,668",
            "216,146,977",
            "819,987,18",
            "117,168,530",
            "805,96,715",
            "346,949,466",
            "970,615,88",
            "941,993,340",
            "862,61,35",
            "984,92,344",
            "425,690,689");
}

package y2025;

import com.google.common.base.Stopwatch;
import lombok.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static aoc.Common.loadInputFromResources;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2025D11 {

    public static void main(String[] args) throws Exception {
        List<String> input = loadInputFromResources();
        Stopwatch sw = Stopwatch.createStarted();
        try {
            // 1
            assertThat(part1(example)).isEqualTo(5);
            assertThat(part1(input)).isEqualTo(658);

            // 2
            assertThat(part2(example2)).isEqualTo(2);
            assertThat(part2(input)).isEqualTo(371113003846800L);

        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private static long part1(List<String> input) {
        Map<String, String[]> paths = new HashMap<>();
        for (String line : input) {
            String[] fromTo = line.split(": ");
            checkState(fromTo.length == 2);
            String from = fromTo[0];
            String[] to = fromTo[1].split(" ");
            paths.put(from, to);
        }

        return countPaths("you", paths);
    }

    private static long countPaths(String curr, Map<String, String[]> paths) {
        String[] nexts = paths.get(curr);
        long sum = 0;
        for (String next : nexts) {
            if (next.equals("out")) {
                sum++;
            } else {
                sum += countPaths(next, paths);
            }
        }
        return sum;
    }

    @Value
    static class State {
        String curr;
        boolean visitedDac;
        boolean visitedFft;
    }

    private static long part2(List<String> input) {
        Map<String, String[]> paths = new HashMap<>();
        for (String line : input) {
            String[] fromTo = line.split(": ");
            checkState(fromTo.length == 2);
            String from = fromTo[0];
            String[] to = fromTo[1].split(" ");
            paths.put(from, to);
        }

        return countPaths2("svr", false, false, new HashMap<>(), paths);
    }

    private static long countPaths2(String curr, boolean visitedDac, boolean visitedFft, Map<State, Long> cache, Map<String, String[]> paths) {
        State key = new State(curr, visitedDac, visitedFft);
        Long cached = cache.get(key);
        if (cached != null) {
            return cached;
        }

        String[] nexts = paths.get(curr);
        long sum = 0;
        for (String next : nexts) {
            if (next.equals("out")) {
                if (visitedDac && visitedFft) {
                    sum++;
                }
            } else if (next.equals("dac")) {
                sum += countPaths2(next, true, visitedFft, cache, paths);
            } else if (next.equals("fft")) {
                sum += countPaths2(next, visitedDac, true, cache, paths);
            } else {
                sum += countPaths2(next, visitedDac, visitedFft, cache, paths);
            }
        }

        cache.put(key, sum);
        return sum;
    }

    static List<String> example = List.of(
            "aaa: you hhh",
            "you: bbb ccc",
            "bbb: ddd eee",
            "ccc: ddd eee fff",
            "ddd: ggg",
            "eee: out",
            "fff: out",
            "ggg: out",
            "hhh: ccc fff iii",
            "iii: out");

    static List<String> example2 = List.of(
            "svr: aaa bbb",
            "aaa: fft",
            "fft: ccc",
            "bbb: tty",
            "tty: ccc",
            "ccc: ddd eee",
            "ddd: hub",
            "hub: fff",
            "eee: dac",
            "dac: fff",
            "fff: ggg hhh",
            "ggg: out",
            "hhh: out");
    ;
}

package y2024;

import com.google.common.base.Stopwatch;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static aoc.Common.loadInputFromResources;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2024D25 {

    public static void main(String[] args) throws Exception {
        List<String> input = loadInputFromResources();
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(part1(example)).isEqualTo(3);
        assertThat(part1(input)).isEqualTo(3107);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static long part1(List<String> input) {
        List<int[]> locks = new ArrayList<>();
        List<int[]> keys = new ArrayList<>();

        for (int i = 0; i < input.size(); i += 8) {
            checkState(i == 0 || "".equals(input.get(i - 1)));

            if (input.get(i).equals("#####")) {
                // lock
                checkState(input.get(i + 6).equals("....."));
                int[] lock = new int[5];
                for (int x = 0; x < 5; x++) {
                    for (int y = 0; y <= 5; y++) {
                        if (input.get(i + y).charAt(x) == '#') {
                            lock[x] = y;
                        }
                    }
                }
                locks.add(lock);
            } else {
                // key
                checkState(input.get(i + 6).equals("#####"));
                int[] key = new int[5];
                for (int x = 0; x < 5; x++) {
                    for (int y = 0; y <= 5; y++) {
                        if (input.get(i + 6 - y).charAt(x) == '#') {
                            key[x] = y;
                        }
                    }
                }
                keys.add(key);
            }
        }

        int matches = 0;
        for (int[] lock : locks) {
            keyLoop:
            for (int[] key : keys) {
                for (int x = 0; x < 5; x++) {
                    if (lock[x] + key[x] > 5) {
                        continue keyLoop;
                    }
                }
                matches++;
            }
        }

        System.out.println("locks: ");
        for (int[] lock : locks) {
            System.out.println(Arrays.stream(lock).mapToObj(x -> "" + x).collect(Collectors.joining(",")));
        }
        System.out.println("keys: ");
        for (int[] key : keys) {
            System.out.println(Arrays.stream(key).mapToObj(x -> "" + x).collect(Collectors.joining(",")));
        }

        return matches;
    }

    static List<String> example = List.of(
            "#####",
            ".####",
            ".####",
            ".####",
            ".#.#.",
            ".#...",
            ".....",
            "",
            "#####",
            "##.##",
            ".#.##",
            "...##",
            "...#.",
            "...#.",
            ".....",
            "",
            ".....",
            "#....",
            "#....",
            "#...#",
            "#.#.#",
            "#.###",
            "#####",
            "",
            ".....",
            ".....",
            "#.#..",
            "###..",
            "###.#",
            "###.#",
            "#####",
            "",
            ".....",
            ".....",
            ".....",
            "#....",
            "#.#..",
            "#.#.#",
            "#####");
}

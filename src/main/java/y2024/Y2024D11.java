package y2024;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Iterables;
import lombok.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static aoc.Common.loadInputFromResources;
import static com.google.common.truth.Truth.assertThat;

public class Y2024D11 {

    public static void main(String[] args) throws Exception {
        List<String> input = loadInputFromResources();
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(part1(example)).isEqualTo(55312);
        assertThat(part1(input)).isEqualTo(172484);

        // 2
        assertThat(part2(input)).isEqualTo(205913561055242L);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static long part1(List<String> input) {
        List<Long> stones = Splitter.on(' ').splitToList(Iterables.getOnlyElement(input))
                .stream().map(Long::parseLong)
                .collect(Collectors.toList());

        List<Long> stones2 = new ArrayList<>();

        for (int i = 1; i <= 25; i++) {
            for (long stone : stones) {
                if (stone == 0) {
                    stones2.add(1L);
                } else {
                    String s = Long.toString(stone);
                    if (s.length() % 2 == 0) {
                        int halfLen = s.length() / 2;
                        stones2.add(Long.parseLong(s.substring(0, halfLen)));
                        stones2.add(Long.parseLong(s.substring(halfLen)));
                    } else {
                        stones2.add(stone * 2024);
                    }
                }
            }

            List<Long> tmp = stones;
            stones = stones2;
            stones2 = tmp;
            stones2.clear();
        }
        return stones.size();
    }

    @Value
    static class CacheKey {
        long stoneVal;
        int steps;
    }

    static Map<CacheKey, Long> stonesAfterStepsCache = new HashMap<>();

    private static long part2(List<String> input) {
        return Splitter.on(' ').splitToList(Iterables.getOnlyElement(input))
                .stream().map(Long::parseLong)
                .mapToLong(x -> countAfterBlinks(x, 75, stonesAfterStepsCache))
                .sum();
    }

    private static long countAfterBlinks(long stone, int steps, Map<CacheKey, Long> cache) {
        if (steps == 0) {
            return 1;
        }

        CacheKey key = new CacheKey(stone, steps);
        Long cached = cache.get(key);
        if (cached == null) {
            long val;
            if (stone == 0) {
                val = countAfterBlinks(1, steps - 1, cache);
            } else {
                String s = Long.toString(stone);
                if (s.length() % 2 == 0) {
                    int halfLen = s.length() / 2;
                    val = countAfterBlinks(Long.parseLong(s.substring(0, halfLen)), steps - 1, cache)
                            + countAfterBlinks(Long.parseLong(s.substring(halfLen)), steps - 1, cache);
                } else {
                    val = countAfterBlinks(stone * 2024, steps - 1, cache);
                }
            }

            cache.put(key, val);
            return val;
        } else {
            return cached;
        }
    }

    static List<String> example = List.of(
            "125 17");
}

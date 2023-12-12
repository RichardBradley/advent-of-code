package y2023;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;
import lombok.Value;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;

public class Y2023D12 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            List<String> input = Resources.readLines(Resources.getResource("y2023/Y2023D12.txt"), StandardCharsets.UTF_8);

            // 1
            assertThat(part1(input)).isEqualTo(7541);

            // 2
            assertThat(part2(input)).isEqualTo(17485169859432L);

        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private static long part1(List<String> input) {
        return input.stream()
                .mapToLong(line -> {
                    String[] parts = line.split(" ");
                    StringBuilder springs = new StringBuilder(parts[0]);
                    List<Integer> groupSpec = Splitter.on(",")
                            .splitToList(parts[1]).stream()
                            .map(Integer::parseInt)
                            .collect(Collectors.toList());

                    return countPossibleArrangements(
                            new HashMap<>(),
                            springs,
                            0,
                            groupSpec,
                            0,
                            null);
                })
                .sum();
    }

    @Value
    private static class MemoKey {
        int springIdx;
        int groupIdx;
        Integer currentSpringGroupLen;
    }

    private static long countPossibleArrangements(
            Map<MemoKey, Long> memo,
            StringBuilder springs,
            int springIdx,
            List<Integer> groupSpec,
            int groupIdx,
            Integer currentSpringGroupLen) {

        MemoKey memoKey = new MemoKey(springIdx, groupIdx, currentSpringGroupLen);
        Long cached = memo.get(memoKey);
        if (cached != null) {
            return cached;
        }

        long count;
        if (springIdx >= springs.length()) {
            if (currentSpringGroupLen != null) {
                // end a spring group
                if (groupIdx == groupSpec.size() - 1
                        && groupSpec.get(groupIdx) == (int) currentSpringGroupLen) {
                    count = 1;
                } else {
                    count = 0;
                }
            } else {
                count = groupIdx == groupSpec.size() ? 1 : 0;
            }
        } else {
            count = 0;
            char c = springs.charAt(springIdx);
            if (c == '.' || c == '?') {
                if (currentSpringGroupLen != null) {
                    // end a spring group
                    if (groupIdx < groupSpec.size()
                            && groupSpec.get(groupIdx) == (int) currentSpringGroupLen) {
                        count += countPossibleArrangements(
                                memo,
                                springs,
                                springIdx + 1,
                                groupSpec,
                                groupIdx + 1,
                                null);
                    } else {
                        // count += 0;
                    }
                } else {
                    count += countPossibleArrangements(
                            memo,
                            springs,
                            springIdx + 1,
                            groupSpec,
                            groupIdx,
                            null);
                }
            }
            if (c == '#' || c == '?') {
                // continue or start a spring group
                if (groupIdx >= groupSpec.size()) {
                    // count += 0
                } else {
                    int nextGroupLen = (currentSpringGroupLen == null ? 0 : currentSpringGroupLen) + 1;
                    if (nextGroupLen <= groupSpec.get(groupIdx)) {
                        count += countPossibleArrangements(
                                memo,
                                springs,
                                springIdx + 1,
                                groupSpec,
                                groupIdx,
                                nextGroupLen);
                    } else {
                        // count += 0
                    }
                }
            }
        }

        memo.put(memoKey, count);
        return count;
    }

    private static long part2(List<String> input) {
        List<String> newInput = input.stream()
                .map(line -> {
                    String[] parts = line.split(" ");
                    StringBuilder part1 = new StringBuilder();
                    StringBuilder part2 = new StringBuilder();
                    for (int i = 0; i < 5; i++) {
                        if (i != 0) {
                            part1.append("?");
                            part2.append(",");
                        }
                        part1.append(parts[0]);
                        part2.append(parts[1]);
                    }
                    return part1 + " " + part2;
                })
                .collect(Collectors.toList());

        return part1(newInput);
    }
}

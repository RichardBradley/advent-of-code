package y2021;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2021D14 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            List<String> input = Resources.readLines(Resources.getResource("y2021/Y2021D14.txt"), StandardCharsets.UTF_8);

            // 1
            assertThat(part1(example)).isEqualTo(1588);
            assertThat(part1(input)).isEqualTo(4244);

            // 2
            assertThat(part2(example, 10)).isEqualTo(1588);
            assertThat(part2(input, 10)).isEqualTo(4244);
            assertThat(part2(example, 40)).isEqualTo(2188189693529L);
            assertThat(part2(input, 40)).isEqualTo(4807056953866L);

        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private static long part1(List<String> input) {
        String chain = input.get(0);
        Map<String, String> replacements = parse(input);

        for (int step = 1; step <= 10; step++) {
            StringBuilder next = new StringBuilder();
            for (int i = 0; i < chain.length() - 1; i++) {
                String curr = chain.charAt(i) + "" + chain.charAt(i + 1);
                next.append(chain.charAt(i));
                String r = replacements.get(curr);
                if (r != null) {
                    next.append(r);
                }
            }
            next.append(chain.charAt(chain.length() - 1));
            chain = next.toString();
        }

        Map<Integer, List<Integer>> letters = chain.chars().mapToObj(x -> x).collect(Collectors.groupingBy((Integer c) -> c));
        List<Map.Entry<Integer, List<Integer>>> lettersSorted = letters.entrySet().stream()
                .sorted(Comparator.comparing((Map.Entry<Integer, List<Integer>> kv) -> kv.getValue().size()).reversed())
                .collect(Collectors.toList());
        return lettersSorted.get(0).getValue().size() - lettersSorted.get(lettersSorted.size() - 1).getValue().size();
    }

    private static long part2(List<String> input, int stepCount) {
        String chain = input.get(0);
        Map<String, String> replacements = parse(input);
        Map<String, Long> pairCounts = new HashMap<>();
        for (int i = 0; i < chain.length() - 1; i++) {
            String curr = chain.charAt(i) + "" + chain.charAt(i + 1);
            add(pairCounts, curr, 1);
        }
        // Add sentinel values to simplify counting letters at the end:
        add(pairCounts, "_" + chain.charAt(0), 1);
        add(pairCounts, chain.charAt(chain.length() - 1) + "_", 1);

        for (int step = 1; step <= stepCount; step++) {
            Map<String, Long> nextPairCounts = new HashMap<>();
            for (Map.Entry<String, Long> entry : pairCounts.entrySet()) {
                String pair = entry.getKey();
                long count = entry.getValue();
                String r = replacements.get(pair);
                if (r != null) {
                    String lhs = pair.charAt(0) + r;
                    add(nextPairCounts, lhs, count);
                    String rhs = r + pair.charAt(1);
                    add(nextPairCounts, rhs, count);
                } else {
                    add(nextPairCounts, pair, count);
                }
            }
            pairCounts = nextPairCounts;
        }

        Map<Character, Long> lettersToCount = new HashMap<>();
        for (Map.Entry<String, Long> pairCount : pairCounts.entrySet()) {
            add(lettersToCount, pairCount.getKey().charAt(0), pairCount.getValue());
            add(lettersToCount, pairCount.getKey().charAt(1), pairCount.getValue());
        }
        for (Character character : lettersToCount.keySet()) {
            Long twiceCount = lettersToCount.get(character);
            checkState(twiceCount % 2 == 0);
            lettersToCount.put(character, twiceCount / 2);
        }

        List<Map.Entry<Character, Long>> lettersSorted = lettersToCount.entrySet().stream()
                .filter(kv -> kv.getKey() != '_')
                .sorted(Comparator.comparing((Map.Entry<Character, Long> kv) -> kv.getValue()).reversed())
                .collect(Collectors.toList());

        return lettersSorted.get(0).getValue() - lettersSorted.get(lettersSorted.size() - 1).getValue();
    }

    private static Map<String, String> parse(List<String> input) {
        Map<String, String> replacements = new HashMap<>();
        checkState("".equals(input.get(1)));
        Pattern p = Pattern.compile("([A-Z][A-Z]) -> ([A-Z])");
        for (int i = 2; i < input.size(); i++) {
            String line = input.get(i);
            Matcher m = p.matcher(line);
            checkState(m.matches());
            checkState(null == replacements.put(m.group(1), m.group(2)));
        }
        return replacements;
    }

    private static <T> void add(Map<T, Long> map, T key, long val) {
        map.compute(key, (k, v) -> (v == null ? 0 : v) + val);
    }

    private static List<String> example = List.of(
            "NNCB",
            "",
            "CH -> B",
            "HH -> N",
            "CB -> H",
            "NH -> C",
            "HB -> C",
            "HC -> B",
            "HN -> C",
            "NN -> C",
            "BH -> H",
            "NC -> B",
            "NB -> B",
            "BN -> B",
            "BB -> N",
            "BC -> B",
            "CC -> N",
            "CN -> C"
    );
}

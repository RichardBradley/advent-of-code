package y2020;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.experimental.Wither;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2020D19 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        String input = Resources.toString(Resources.getResource("y2020/Y2020D19.txt"), StandardCharsets.UTF_8);

        assertThat(countMatches(example, false)).isEqualTo(2);
        assertThat(countMatches(input, false)).isEqualTo(203);

        assertThat(countMatches(example2, true)).isEqualTo(12);
        assertThat(countMatches(input, true)).isEqualTo(304);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static long countMatches(String input, boolean isPart2) {
        String[] sections = input.split("\n\n");
        checkState(sections.length == 2);

        String[] candidates = sections[1].split("\n");

        Map<Integer, String> rules = Arrays.stream(sections[0].split("\n"))
                .map(line -> {
                    String[] nameVal = line.split(": ");
                    checkState(nameVal.length == 2);
                    int idx = Integer.parseInt(nameVal[0]);
                    return new Object[]{idx, nameVal[1]};
                })
                .collect(Collectors.toMap(x -> (int) x[0], x -> (String) x[1]));

        if (isPart2) {
            rules.put(8, "42 | 42 8");
            rules.put(11, "42 31 | 42 11 31");
        }

        Ast rule0 = rulesToAst(0, rules);

        return Arrays.stream(candidates).filter(candidate -> isMatch(rule0, candidate)).count();
    }

    @Value
    static class InputState {
        String input;
        @Wither
        int offset;

        public boolean isEof() {
            return offset == input.length();
        }
    }

    static boolean isMatch(Ast rule, String input) {
        return rule.match(new InputState(input, 0))
                .anyMatch(InputState::isEof);
    }

    private static Ast rulesToAst(int i, Map<Integer, String> rules) {
        String rule = rules.get(i);
        if (rule.equals("\"a\"") || rule.equals("\"b\"")) {
            return new Literal(rule.charAt(1));
        } else {
            Alt it = new Alt();
            Iterable<String> branches = Splitter.on('|').trimResults().split(rule);
            it.branches = StreamSupport.stream(branches.spliterator(), false)
                    .map(branch ->
                            new Seq(Arrays.stream(branch.split(" "))
                                    .map(n -> {
                                        int nn = Integer.parseInt(n);
                                        if (nn == i) {
                                            return it;
                                        } else {
                                            return rulesToAst(nn, rules);
                                        }
                                    })
                                    .collect(Collectors.toList()))
                    )
                    .collect(Collectors.toList());
            return it;
        }
    }

    interface Ast {
        Stream<InputState> match(InputState in);
    }

    private static class Alt implements Ast {
        List<Ast> branches;

        @Override
        public Stream<InputState> match(InputState in) {
            Stream<InputState> acc = Stream.empty();
            for (Ast val : branches) {
                acc = Stream.concat(acc, val.match(in));
            }
            return acc;
        }
    }

    @Value
    private static class Literal implements Ast {
        char c;

        @Override
        public Stream<InputState> match(InputState in) {
            if (in.isEof() || c != in.input.charAt(in.offset)) {
                return Stream.empty();
            }
            return Stream.of(in.withOffset(in.offset + 1));
        }
    }

    @Value
    private static class Seq implements Ast {
        List<Ast> vals;

        @Override
        @SneakyThrows
        public Stream<InputState> match(InputState in) {
            Stream<InputState> acc = Stream.of(in);
            for (Ast val : vals) {
                acc = acc.flatMap(state -> val.match(state));
            }
            return acc;
        }
    }

    private static String example =
            "0: 4 1 5\n" +
                    "1: 2 3 | 3 2\n" +
                    "2: 4 4 | 5 5\n" +
                    "3: 4 5 | 5 4\n" +
                    "4: \"a\"\n" +
                    "5: \"b\"\n" +
                    "\n" +
                    "ababbb\n" +
                    "bababa\n" +
                    "abbbab\n" +
                    "aaabbb\n" +
                    "aaaabbb";

    private static String example2 =
            "42: 9 14 | 10 1\n" +
                    "9: 14 27 | 1 26\n" +
                    "10: 23 14 | 28 1\n" +
                    "1: \"a\"\n" +
                    "11: 42 31\n" +
                    "5: 1 14 | 15 1\n" +
                    "19: 14 1 | 14 14\n" +
                    "12: 24 14 | 19 1\n" +
                    "16: 15 1 | 14 14\n" +
                    "31: 14 17 | 1 13\n" +
                    "6: 14 14 | 1 14\n" +
                    "2: 1 24 | 14 4\n" +
                    "0: 8 11\n" +
                    "13: 14 3 | 1 12\n" +
                    "15: 1 | 14\n" +
                    "17: 14 2 | 1 7\n" +
                    "23: 25 1 | 22 14\n" +
                    "28: 16 1\n" +
                    "4: 1 1\n" +
                    "20: 14 14 | 1 15\n" +
                    "3: 5 14 | 16 1\n" +
                    "27: 1 6 | 14 18\n" +
                    "14: \"b\"\n" +
                    "21: 14 1 | 1 14\n" +
                    "25: 1 1 | 1 14\n" +
                    "22: 14 14\n" +
                    "8: 42\n" +
                    "26: 14 22 | 1 20\n" +
                    "18: 15 15\n" +
                    "7: 14 5 | 1 21\n" +
                    "24: 14 1\n" +
                    "\n" +
                    "abbbbbabbbaaaababbaabbbbabababbbabbbbbbabaaaa\n" +
                    "bbabbbbaabaabba\n" +
                    "babbbbaabbbbbabbbbbbaabaaabaaa\n" +
                    "aaabbbbbbaaaabaababaabababbabaaabbababababaaa\n" +
                    "bbbbbbbaaaabbbbaaabbabaaa\n" +
                    "bbbababbbbaaaaaaaabbababaaababaabab\n" +
                    "ababaaaaaabaaab\n" +
                    "ababaaaaabbbaba\n" +
                    "baabbaaaabbaaaababbaababb\n" +
                    "abbbbabbbbaaaababbbbbbaaaababb\n" +
                    "aaaaabbaabaaaaababaa\n" +
                    "aaaabbaaaabbaaa\n" +
                    "aaaabbaabbaaaaaaabbbabbbaaabbaabaaa\n" +
                    "babaaabbbaaabaababbaabababaaab\n" +
                    "aabbbbbaabbbaaaaaabbbbbababaaaaabbaaabba";
}

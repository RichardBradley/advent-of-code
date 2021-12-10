package y2021;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;

public class Y2021D10 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            List<String> input = Resources.readLines(Resources.getResource("y2021/Y2021D10.txt"), StandardCharsets.UTF_8);

            // 1
            assertThat(score("<{([([[(<>()){}]>(<<{{", true)).isEqualTo(25137);
            assertThat(part1(example)).isEqualTo(26397);
            assertThat(part1(input)).isEqualTo(411471);

            // 2
            assertThat(part2(example)).isEqualTo(288957);
            // 133901619 too low
            assertThat(part2(input)).isEqualTo(-1);

        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private static long part1(List<String> input) {
        return input.stream().mapToLong(x -> score(x, true)).sum();
    }

    private static long part2(List<String> input) {
        List<Long> incompletes = input.stream().map(x -> score(x, false))
                .filter(x -> x != 0)
                .collect(Collectors.toList());
        return incompletes
                .stream()
                .sorted()
                .skip(incompletes.size() / 2)
                .findFirst()
                .get();
    }

    private static long score(String line, boolean part1) {
        Stack<Character> stack = new Stack<>();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            switch (c) {
                case '(':
                case '[':
                case '<':
                case '{':
                    stack.push(c);
                    break;
                case ')': {
                    char last = stack.pop();
                    if (last != '(') {
                        return part1 ? 3 : 0;
                    }
                    break;
                }
                case ']': {
                    char last = stack.pop();
                    if (last != '[') {
                        return part1 ? 57 : 0;
                    }
                    break;
                }
                case '}': {
                    char last = stack.pop();
                    if (last != '{') {
                        return part1 ? 1197 : 0;
                    }
                    break;
                }
                case '>': {
                    char last = stack.pop();
                    if (last != '<') {
                        return part1 ? 25137 : 0;
                    }
                    break;
                }
                default:
                    throw new IllegalArgumentException();
            }
        }
        if (!stack.empty()) {
            // truncated
            if (part1) {
                return 0;
            } else {
                long score = 0;
                while (!stack.empty()) {
                    char c = stack.pop();
                    score *= 5;
                    switch (c) {
                        case '(':
                            score += 1;
                            break;
                        case '[':
                            score += 2;
                            break;
                        case '{':
                            score += 3;
                            break;
                        case '<':
                            score += 4;
                            break;
                        default:
                            throw new IllegalArgumentException();
                    }
                }
                return score;
            }
        }
        return 0; // valid
    }

    private static List<String> example = List.of(
            "[({(<(())[]>[[{[]{<()<>>",
            "[(()[<>])]({[<{<<[]>>(",
            "{([(<{}[<>[]}>{[]{[(<()>",
            "(((({<>}<{<{<>}{[]{[]{}",
            "[[<[([]))<([[{}[[()]]]",
            "[{[{({}]{}}([{[{{{}}([]",
            "{<[[]]>}<{[{[{[]{()[[[]",
            "[<(<(<(<{}))><([]([]()",
            "<{([([[(<>()){}]>(<<{{",
            "<{([{{}}[<[[[<>{}]]]>[]]"
    );

}

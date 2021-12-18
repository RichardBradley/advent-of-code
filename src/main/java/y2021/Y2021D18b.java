package y2021;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;
import lombok.SneakyThrows;

import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2021D18b {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            List<String> input = Resources.readLines(Resources.getResource("y2021/Y2021D18.txt"), StandardCharsets.UTF_8);

            // 1
            assertThat(part1(example)).isEqualTo(4140);
            assertThat(part1(input)).isEqualTo(4173);

            // 2
            assertThat(part2(example)).isEqualTo(3993);
            assertThat(part2(input)).isEqualTo(4706);

        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private static long part1(List<String> input) {
        return getMagnitude(input.stream()
                .reduce((a, b) -> add(a, b))
                .get());
    }

    private static long part2(List<String> input) {
        long largestMag = 0;
        for (String a : input) {
            for (String b : input) {
                if (a != b) {
                    largestMag = Math.max(getMagnitude(add(a, b)), largestMag);
                }
            }
        }
        return largestMag;
    }

    private static long getMagnitude(String s) {
        return getMagnitude(new StringReader(s));
    }

    @SneakyThrows
    private static long getMagnitude(Reader in) {
        int next = in.read();
        if (next == '[') {
            long left = getMagnitude(in);
            checkState(',' == in.read());
            long right = getMagnitude(in);
            checkState(']' == in.read());
            return 3 * left + 2 * right;
        } else {
            return next - '0';
        }
    }

    private static String add(String a, String b) {
        StringBuilder sum = new StringBuilder().append("(").append(squareToRound(a)).append(",").append(squareToRound(b)).append(")");
        reduce(sum);
        return roundToSquare(sum.toString());
    }

    private static String squareToRound(String s) {
        return s.replace("[", "(").replace("]", ")");
    }

    private static String roundToSquare(String s) {
        return s.replace("(", "[").replace(")", "]");
    }

    // Work with round brackets in here, to allow single character numbers via char overflow
    private static void reduce(StringBuilder snailNumber) {
        reduceLoop:
        while (true) {
            // If any pair is nested inside four pairs, the leftmost such pair explodes.
            int nestingCount = 0;
            for (int i = 0; i < snailNumber.length(); i++) {
                switch (snailNumber.charAt(i)) {
                    case '(':
                        if (5 == (++nestingCount)) {
                            // explode
                            int left = snailNumber.charAt(i + 1) - '0';
                            for (int j = i; j > 0; j--) {
                                char c = snailNumber.charAt(j);
                                if (c >= '0') {
                                    snailNumber.setCharAt(j, (char) (c + left));
                                    break;
                                }
                            }
                            int right = snailNumber.charAt(i + 3) - '0';
                            for (int j = i + 4; j < snailNumber.length(); j++) {
                                char c = snailNumber.charAt(j);
                                if (c >= '0') {
                                    snailNumber.setCharAt(j, (char) (c + right));
                                    break;
                                }
                            }

                            snailNumber.setCharAt(i, '0');
                            snailNumber.delete(i + 1, i + 5);

                            continue reduceLoop;
                        }
                        break;
                    case ')':
                        nestingCount--;
                        break;
                }
            }
            // If any regular number is 10 or greater, the leftmost such regular number splits.
            for (int i = 0; i < snailNumber.length(); i++) {
                char c = snailNumber.charAt(i);
                if (c > '9') {
                    int newLeft = (c - '0') / 2;
                    int newRight = (c - '0') - newLeft;
                    snailNumber.setCharAt(i, '(');
                    snailNumber.insert(i + 1, (char) (newLeft + '0') + "," + (char) (newRight + '0') + ")");
                    continue reduceLoop;
                }
            }

            // no changes found
            break;
        }
    }

    private static List<String> example = List.of(
            "[[[0,[5,8]],[[1,7],[9,6]]],[[4,[1,2]],[[1,4],2]]]",
            "[[[5,[2,8]],4],[5,[[9,9],0]]]",
            "[6,[[[6,2],[5,6]],[[7,6],[4,7]]]]",
            "[[[6,[0,7]],[0,9]],[4,[9,[9,0]]]]",
            "[[[7,[6,4]],[3,[1,3]]],[[[5,5],1],9]]",
            "[[6,[[7,3],[3,2]]],[[[3,8],[5,7]],4]]",
            "[[[[5,4],[7,7]],8],[[8,3],8]]",
            "[[9,3],[[9,9],[6,[4,9]]]]",
            "[[2,[[7,7],7]],[[5,8],[[9,3],[0,2]]]]",
            "[[[[5,2],5],[8,[3,7]]],[[5,[7,5]],[4,4]]]"
    );
}

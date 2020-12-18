package y2020;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;


public class Y2020D18 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        List<String> input = Resources.readLines(Resources.getResource("y2020/Y2020D18.txt"), StandardCharsets.UTF_8);

        assertThat(evalPart1("1 + (2 * 3) + (4 * (5 + 6))")).isEqualTo(51);
        assertThat(evalPart1("((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2")).isEqualTo(13632);
        System.out.println("sum = " + input.stream().mapToLong(line -> evalPart1(line)).sum());

        assertThat(evalPart2("((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2")).isEqualTo(23340);
        System.out.println("sum = " + input.stream().mapToLong(line -> evalPart2(line)).sum());

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static long evalPart2(String input) {
        // addition is evaluated before multiplication.
        try {
            PushbackReader in = new PushbackReader(new StringReader(input), 2);
            long acc = evalPart2(in);
            checkState(in.read() == -1);
            return acc;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static long evalPart2(PushbackReader in) throws IOException {
        // addition is evaluated before multiplication.
        long acc = evalAddPart2(in);
        while (true) {
            int next = in.read();
            if (next == -1) {
                return acc;
            } else if (next == ' ') {
                next = in.read();
                if (next == '*') {
                    checkState(' ' == in.read());
                    acc *= evalAddPart2(in);
                } else if (next == '+') {
                    in.unread(next);
                    in.unread(' ');
                    return acc;
                } else {
                    throw new IllegalArgumentException("next = " + (char) next);
                }
            } else if (next == ')') {
                in.unread(next);
                return acc;
            } else {
                throw new IllegalArgumentException("next = " + (char) next);
            }
        }
    }

    private static long evalAddPart2(PushbackReader in) throws IOException {
        long acc = evalNumberOrBracketsPart2(in);
        while (true) {
            int next = in.read();
            if (next == -1) {
                return acc;
            } else if (next == ' ') {
                next = in.read();
                if (next == '+') {
                    checkState(' ' == in.read());
                    acc += evalNumberOrBracketsPart2(in);
                } else if (next == '*') {
                    in.unread(next);
                    in.unread(' ');
                    return acc;
                } else {
                    throw new IllegalArgumentException("next = " + (char) next);
                }
            } else if (next == ')') {
                in.unread(next);
                return acc;
            } else {
                throw new IllegalArgumentException("next = " + (char) next);
            }
        }
    }

    private static long evalNumberOrBracketsPart2(PushbackReader in) throws IOException {
        long next = in.read();
        if (next >= '0' && next <= '9') {
            return next - '0';
        } else if (next == '(') {
            long acc = evalPart2(in);
            next = in.read();
            checkState(')' == next);
            return acc;
        } else {
            throw new IllegalArgumentException("next = " + (char) next);
        }
    }

    private static long evalPart1(String input) {
        // the operators have the same precedence, and are evaluated left-to-right regardless of the order in which they appear.
        try {
            PushbackReader in = new PushbackReader(new StringReader(input));
            long acc = evalPart1(in);
            checkState(in.read() == -1);
            return acc;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static long evalPart1(PushbackReader in) throws IOException {
        long acc = evalNumberOrBracketsPart1(in);
        while (true) {
            int next = in.read();
            if (next == -1) {
                return acc;
            } else if (next == ' ') {
                next = in.read();
                if (next == '+') {
                    checkState(' ' == in.read());
                    acc += evalNumberOrBracketsPart1(in);
                } else if (next == '*') {
                    checkState(' ' == in.read());
                    acc *= evalNumberOrBracketsPart1(in);
                } else {
                    throw new IllegalArgumentException("next = " + next);
                }
            } else if (next == ')') {
                in.unread(next);
                return acc;
            } else {
                throw new IllegalArgumentException("next = " + (char) next);
            }
        }
    }

    private static long evalNumberOrBracketsPart1(PushbackReader in) throws IOException {
        long next = in.read();
        if (next >= '0' && next <= '9') {
            return next - '0';
        } else if (next == '(') {
            long acc = evalPart1(in);
            next = in.read();
            checkState(')' == next);
            return acc;
        } else {
            throw new IllegalArgumentException("next = " + (char) next);
        }
    }
}
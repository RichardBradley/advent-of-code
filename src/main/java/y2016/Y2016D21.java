package y2016;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import lombok.Value;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2016D21 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(apply(exampleInput, new StringBuffer("abcde"))).isEqualTo("decab");
        System.out.println("example ok");

        System.out.println(apply(input, new StringBuffer("abcdefgh")));

        // 2
        // The example is non-deterministic, but the real one seems to work fine
        assertThat(apply(new String[]{"rotate based on position of letter d"}, new StringBuffer("abdec"))).isEqualTo("decab");
        assertThat(apply(new String[]{"rotate based on position of letter d"}, new StringBuffer("ecabd"))).isEqualTo("decab");

        // assertThat(unapply(exampleInput, new StringBuffer("decab"))).isEqualTo("abcde");
        // System.out.println("example ok");

        System.out.println(unapply(input, new StringBuffer("fbgdceah")));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    static String apply(String[] spec, StringBuffer password) {
        Pattern swapPosPatt = Pattern.compile("swap position (\\d+) with position (\\d+)");
        Pattern swapLetPatt = Pattern.compile("swap letter ([a-z]) with letter ([a-z])");
        Pattern rotateStepsPatt = Pattern.compile("rotate (left|right) (\\d+) steps?");
        Pattern rotateFromLetPatt = Pattern.compile("rotate based on position of letter ([a-z])");
        Pattern reverseSpanPatt = Pattern.compile("reverse positions (\\d+) through (\\d+)");
        Pattern movePosPatt = Pattern.compile("move position (\\d+) to position (\\d+)");
        Matcher matcher;

        for (String instr : spec) {
            if ((matcher = swapPosPatt.matcher(instr)).matches()) {
                // swap position X with position Y means that the letters at indexes X and Y (counting from 0) should be swapped.
                int x = Integer.parseInt(matcher.group(1));
                int y = Integer.parseInt(matcher.group(2));
                char tmp = password.charAt(x);
                password.setCharAt(x, password.charAt(y));
                password.setCharAt(y, tmp);
            } else if ((matcher = swapLetPatt.matcher(instr)).matches()) {
                // swap letter X with letter Y means that the letters X and Y should be swapped (regardless of where they appear in the string).
                int xIdx = password.indexOf(matcher.group(1));
                int yIdx = password.indexOf(matcher.group(2));
                char tmp = password.charAt(xIdx);
                password.setCharAt(xIdx, password.charAt(yIdx));
                password.setCharAt(yIdx, tmp);
            } else if ((matcher = rotateStepsPatt.matcher(instr)).matches()) {
                // rotate left/right X steps means that the whole string should be rotated; for example, one right rotation would turn abcd into dabc.
                int delta = ("left".equals(matcher.group(1)) ? 1 : -1)
                        * Integer.parseInt(matcher.group(2));
                String prev = password.toString();
                int len = password.length();
                for (int i = 0; i < len; i++) {
                    password.setCharAt(i, prev.charAt((i + delta + len) % len));
                }
            } else if ((matcher = rotateFromLetPatt.matcher(instr)).matches()) {
                // rotate based on position of letter X means that the whole string should be rotated
                // to the right based on the index of letter X (counting from 0) as determined before
                // this instruction does any rotations. Once the index is determined, rotate the string
                // to the right one time, plus a number of times equal to that index, plus one additional
                // time if the index was at least 4.
                String prev = password.toString();
                int xIdx = prev.indexOf(matcher.group(1));
                int delta = -1 * (1 + xIdx + (xIdx >= 4 ? 1 : 0));
                int len = password.length();
                for (int i = 0; i < len; i++) {
                    password.setCharAt(i, prev.charAt((i + delta + len + len) % len));
                }

                // check for identity used below
                //   y = (2x + 1) mod len    if x < 4
                //   y = (2x + 2) mod len    if x >= 4
                if (xIdx >= 4) {
                    checkState(((2 * xIdx + 2) % len) == password.indexOf(matcher.group(1)));
                } else {
                    checkState(((2 * xIdx + 1) % len) == password.indexOf(matcher.group(1)));
                }
            } else if ((matcher = reverseSpanPatt.matcher(instr)).matches()) {
                // reverse positions X through Y means that the span of letters at indexes X through Y
                // (including the letters at X and Y) should be reversed in order.
                int x = Integer.parseInt(matcher.group(1));
                int y = Integer.parseInt(matcher.group(2));
                String prev = password.toString();
                for (int i = 0; i <= (y - x); i++) {
                    password.setCharAt(x + i, prev.charAt(y - i));
                }
            } else if ((matcher = movePosPatt.matcher(instr)).matches()) {
                // move position X to position Y means that the letter which is
                // at index X should be removed from the string, then inserted such that it ends up at index Y.
                int x = Integer.parseInt(matcher.group(1));
                int y = Integer.parseInt(matcher.group(2));
                char c = password.charAt(x);
                password.deleteCharAt(x);
                password.insert(y, c);
            } else {
                throw new IllegalArgumentException(instr);
            }
        }

        return password.toString();
    }

    static String unapply(String[] spec, StringBuffer password) {
        Pattern swapPosPatt = Pattern.compile("swap position (\\d+) with position (\\d+)");
        Pattern swapLetPatt = Pattern.compile("swap letter ([a-z]) with letter ([a-z])");
        Pattern rotateStepsPatt = Pattern.compile("rotate (left|right) (\\d+) steps?");
        Pattern rotateFromLetPatt = Pattern.compile("rotate based on position of letter ([a-z])");
        Pattern reverseSpanPatt = Pattern.compile("reverse positions (\\d+) through (\\d+)");
        Pattern movePosPatt = Pattern.compile("move position (\\d+) to position (\\d+)");
        Matcher matcher;

        for (String instr : Lists.reverse(Arrays.asList(spec))) {
            if ((matcher = swapPosPatt.matcher(instr)).matches()) {
                // swap position X with position Y means that the letters at indexes X and Y (counting from 0) should be swapped.
                // reverse is the same
                int x = Integer.parseInt(matcher.group(1));
                int y = Integer.parseInt(matcher.group(2));
                char tmp = password.charAt(x);
                password.setCharAt(x, password.charAt(y));
                password.setCharAt(y, tmp);
            } else if ((matcher = swapLetPatt.matcher(instr)).matches()) {
                // swap letter X with letter Y means that the letters X and Y should be swapped (regardless of where they appear in the string).
                // reverse is the same
                int xIdx = password.indexOf(matcher.group(1));
                int yIdx = password.indexOf(matcher.group(2));
                char tmp = password.charAt(xIdx);
                password.setCharAt(xIdx, password.charAt(yIdx));
                password.setCharAt(yIdx, tmp);
            } else if ((matcher = rotateStepsPatt.matcher(instr)).matches()) {
                // rotate left/right X steps means that the whole string should be rotated; for example, one right rotation would turn abcd into dabc.
                // reverse is the opposite direction
                int delta = ("left".equals(matcher.group(1)) ? -1 : +1)
                        * Integer.parseInt(matcher.group(2));
                String prev = password.toString();
                int len = password.length();
                for (int i = 0; i < len; i++) {
                    password.setCharAt(i, prev.charAt((i + delta + len) % len));
                }
            } else if ((matcher = rotateFromLetPatt.matcher(instr)).matches()) {
                // rotate based on position of letter X means that the whole string should be rotated
                // to the right based on the index of letter X (counting from 0) as determined before
                // this instruction does any rotations. Once the index is determined, rotate the string
                // to the right one time, plus a number of times equal to that index, plus one additional
                // time if the index was at least 4.


                // new index of X is:
                //   y = (2x + 1) mod len    if x < 4
                //   y = (2x + 2) mod len    if x >= 4
                int y = password.indexOf(matcher.group(1)); // new index of X
                int len = password.length();
                int x = 0;
                for (; ; x++) {
                    if (x < 4 && y == ((2 * x + 1) % len)) {
                        break;
                    } else if (x >= 4 && y == ((2 * x + 2) % len)) {
                        break;
                    } else if (x < len) {
                        continue;
                    }
                    throw new IllegalStateException();
                }

                String prev = password.toString();
                int delta = (1 + x + (x >= 4 ? 1 : 0));
                for (int i = 0; i < len; i++) {
                    password.setCharAt(i, prev.charAt((i + delta + len + len) % len));
                }
            } else if ((matcher = reverseSpanPatt.matcher(instr)).matches()) {
                // reverse positions X through Y means that the span of letters at indexes X through Y
                // (including the letters at X and Y) should be reversed in order.
                // reverse is the same
                int x = Integer.parseInt(matcher.group(1));
                int y = Integer.parseInt(matcher.group(2));
                String prev = password.toString();
                for (int i = 0; i <= (y - x); i++) {
                    password.setCharAt(x + i, prev.charAt(y - i));
                }
            } else if ((matcher = movePosPatt.matcher(instr)).matches()) {
                // move position X to position Y means that the letter which is
                // at index X should be removed from the string, then inserted such that it ends up at index Y.
                // reverse is swap x/y
                int x = Integer.parseInt(matcher.group(1));
                int y = Integer.parseInt(matcher.group(2));
                char c = password.charAt(y);
                password.deleteCharAt(y);
                password.insert(x, c);
            } else {
                throw new IllegalArgumentException(instr);
            }
        }

        return password.toString();
    }

    static String[] exampleInput = new String[]{
            "swap position 4 with position 0",
            "swap letter d with letter b",
            "reverse positions 0 through 4",
            "rotate left 1 step",
            "move position 1 to position 4",
            "move position 3 to position 0",
            "rotate based on position of letter b",
            "rotate based on position of letter d"
    };

    static String[] input = new String[]{
            "move position 2 to position 1",
            "move position 2 to position 5",
            "move position 2 to position 4",
            "swap position 0 with position 2",
            "move position 6 to position 5",
            "swap position 0 with position 4",
            "reverse positions 1 through 6",
            "move position 7 to position 2",
            "rotate right 4 steps",
            "rotate left 6 steps",
            "rotate based on position of letter a",
            "rotate based on position of letter c",
            "move position 2 to position 0",
            "swap letter d with letter a",
            "swap letter g with letter a",
            "rotate left 6 steps",
            "reverse positions 4 through 7",
            "swap position 6 with position 5",
            "swap letter b with letter a",
            "rotate based on position of letter d",
            "rotate right 6 steps",
            "move position 3 to position 1",
            "swap letter g with letter a",
            "swap position 3 with position 6",
            "rotate left 7 steps",
            "swap letter b with letter c",
            "swap position 3 with position 7",
            "move position 2 to position 6",
            "swap letter b with letter a",
            "rotate based on position of letter d",
            "swap letter f with letter b",
            "move position 3 to position 4",
            "rotate left 3 steps",
            "rotate left 6 steps",
            "rotate based on position of letter c",
            "move position 1 to position 3",
            "swap letter e with letter a",
            "swap letter a with letter c",
            "rotate left 2 steps",
            "move position 6 to position 5",
            "swap letter a with letter g",
            "rotate left 5 steps",
            "reverse positions 3 through 6",
            "move position 7 to position 2",
            "swap position 6 with position 5",
            "swap letter e with letter c",
            "reverse positions 2 through 7",
            "rotate based on position of letter e",
            "swap position 3 with position 5",
            "swap letter e with letter d",
            "rotate left 3 steps",
            "rotate based on position of letter c",
            "move position 4 to position 7",
            "rotate based on position of letter e",
            "reverse positions 3 through 5",
            "rotate based on position of letter h",
            "swap position 3 with position 0",
            "swap position 3 with position 4",
            "move position 7 to position 4",
            "rotate based on position of letter a",
            "reverse positions 6 through 7",
            "rotate based on position of letter g",
            "swap letter d with letter h",
            "reverse positions 0 through 3",
            "rotate right 2 steps",
            "rotate right 6 steps",
            "swap letter a with letter g",
            "reverse positions 2 through 4",
            "rotate based on position of letter e",
            "move position 6 to position 0",
            "reverse positions 0 through 6",
            "move position 5 to position 1",
            "swap position 5 with position 2",
            "rotate right 3 steps",
            "move position 3 to position 1",
            "rotate left 1 step",
            "reverse positions 1 through 3",
            "rotate left 4 steps",
            "reverse positions 5 through 6",
            "rotate right 7 steps",
            "reverse positions 0 through 2",
            "move position 0 to position 2",
            "swap letter b with letter c",
            "rotate based on position of letter d",
            "rotate left 1 step",
            "swap position 2 with position 1",
            "swap position 6 with position 5",
            "swap position 5 with position 0",
            "swap letter a with letter c",
            "move position 7 to position 3",
            "move position 6 to position 7",
            "rotate based on position of letter h",
            "move position 3 to position 0",
            "move position 4 to position 5",
            "rotate left 4 steps",
            "swap letter h with letter c",
            "swap letter f with letter e",
            "swap position 1 with position 3",
            "swap letter e with letter b",
            "rotate based on position of letter e",
    };
}

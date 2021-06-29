package y2017;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2017D16 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        String input = Resources.toString(Resources.getResource("y2017/Y2017D16.txt"), StandardCharsets.UTF_8);
        String example = "s1,x3/4,pe/b";

        assertThat(dance(5, "s3", 1)).isEqualTo("cdeab");
        assertThat(dance(5, example, 1)).isEqualTo("baedc");
        assertThat(dance(16, input, 1)).isEqualTo("fnloekigdmpajchb");
        assertThat(dance(16, input, 1000000000)).isEqualTo("amkjepdhifolgncb");

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static String dance(int n, String input, int repeatCount) {
        StringBuilder state = new StringBuilder();
        for (int i = 0; i < n; i++) {
            state.append((char) ('a' + i));
        }
        Map<String, Integer> repeatCountByState = new HashMap<>();

        List<String> steps = Splitter.on(",").splitToList(input);
        for (int repeat = 0; repeat < repeatCount; repeat++) {
            for (String step : steps) {
                switch (step.charAt(0)) {
                    case 's':
                        int spinSize = Integer.parseInt(step.substring(1));
                        String movers = state.substring(n - spinSize, n);
                        state.insert(0, movers);
                        state.setLength(n);
                        break;
                    case 'x': {
                        int slashIndex = step.indexOf('/');
                        int a = Integer.parseInt(step.substring(1, slashIndex));
                        int b = Integer.parseInt(step.substring(slashIndex + 1));
                        char tmp = state.charAt(a);
                        state.setCharAt(a, state.charAt(b));
                        state.setCharAt(b, tmp);
                        break;
                    }
                    case 'p': {
                        char a = step.charAt(1);
                        checkState(step.charAt(2) == '/');
                        char b = step.charAt(3);
                        int aIdx = state.indexOf(Character.toString(a));
                        int bIdx = state.indexOf(Character.toString(b));
                        state.setCharAt(aIdx, b);
                        state.setCharAt(bIdx, a);
                        break;
                    }
                    default:
                        throw new IllegalArgumentException(step);
                }
            }

            Integer prevRepeat = repeatCountByState.put(state.toString(), repeat);
            if (prevRepeat != null) {
                repeatCountByState.clear();
                System.out.println("Found loop from " + prevRepeat + " to " + repeat);
                int loopLen = repeat - prevRepeat;
                if (repeat + loopLen < repeatCount) {
                    repeat += (repeatCount - repeat) / loopLen * loopLen;
                    checkState(repeat < repeatCount);
                    checkState(repeat + loopLen > repeatCount);
                }
            }
        }

        return state.toString();
    }


    private static int countMatches2(long a, long aMul, long b, long bMul, int steps) {
        int matchesCount = 0;
        for (int step = 0; step < steps; step++) {
            do {
                a = (a * aMul) % 2147483647L;
            } while (0 != (a % 4));

            do {
                b = (b * bMul) % 2147483647L;
            } while (0 != (b % 8));

            if ((a & 0xffff) == (b & 0xffff)) {
                matchesCount++;
            }
        }
        return matchesCount;
    }

    private static int countMatches(long a, long aMul, long b, long bMul, int steps) {
        int matchesCount = 0;
        for (int step = 0; step < steps; step++) {
            a = (a * aMul) % 2147483647L;
            b = (b * bMul) % 2147483647L;

            if ((a & 0xffff) == (b & 0xffff)) {
                matchesCount++;
            }
        }
        return matchesCount;
    }
}


package y2017;

import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

public class Y2017D15 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        assertThat(countMatches(65, 16807, 8921, 48271, 5))
                .isEqualTo(1);

        assertThat(countMatches(591, 16807, 393, 48271, 40000000))
                .isEqualTo(619);

        assertThat(countMatches2(65, 16807, 8921, 48271, 1056))
                .isEqualTo(1);

        assertThat(countMatches2(591, 16807, 393, 48271, 5000000))
                .isEqualTo(290);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
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


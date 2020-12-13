package y2020;

import com.google.common.base.Stopwatch;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2020D13 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        part1(939, new int[]{7, 13, 59, 31, 19});
        part1(inputTimestamp, inputBusIds);

        part2(new int[]{7, 13, 0, 0, 59, 0, 31, 19});
        part2(inputBusSpecs);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static void part1(int timestamp, int[] busIds) {

        int shortestWait = Integer.MAX_VALUE;
        int shortestBusId = 0;

        for (int busId : busIds) {
            int timeSinceDepart = timestamp % busId;
            int wait = timeSinceDepart == 0 ? 0 : (busId - timeSinceDepart);
            if (wait < shortestWait) {
                shortestWait = wait;
                shortestBusId = busId;
            }
        }

        // What is the ID of the earliest bus you can take to the airport
        // multiplied by the number of minutes you'll need to wait for that bus?
        System.out.println("ans = " + (shortestBusId * shortestWait));
    }

    private static void part2(int[] busSpecs) {
        // Use a TreeMap, to sort the divisors in descending order
        Map<BigInteger, BigInteger> divisorToRemainders = new TreeMap<>(Comparator.reverseOrder());
        for (int i = 0; i < busSpecs.length; i++) {
            int divisor = busSpecs[i];
            if (divisor != 0) {
                int targetRemainder = i == 0 ? 0 : divisor - i;
                while (targetRemainder < 0) {
                    targetRemainder += divisor;
                }
                checkState(null == divisorToRemainders.put(
                        BigInteger.valueOf(divisor),
                        BigInteger.valueOf(targetRemainder)));
            }
        }

        // https://en.wikipedia.org/wiki/Chinese_remainder_theorem
        assertPairWiseCoprime(divisorToRemainders.keySet());

        // Following https://en.wikipedia.org/wiki/Chinese_remainder_theorem#Search_by_sieving
        BigInteger step = BigInteger.ONE;
        BigInteger candidate = BigInteger.ZERO;
        List<Map.Entry<BigInteger, BigInteger>> solved = new ArrayList<>();
        for (Map.Entry<BigInteger, BigInteger> divisorToRemainder : divisorToRemainders.entrySet()) {
            BigInteger divisor = divisorToRemainder.getKey();
            BigInteger remainder = divisorToRemainder.getValue();

            while (!candidate.mod(divisor).equals(remainder)) {
                candidate = candidate.add(step);
            }
            solved.add(divisorToRemainder);
            step = step.multiply(divisor);
            System.out.printf("candidate = %s\n", candidate);
            for (Map.Entry<BigInteger, BigInteger> solvedDivRem : solved) {
                System.out.printf("%s mod %s = %s (target %s)\n", candidate, solvedDivRem.getKey(), candidate.mod(solvedDivRem.getKey()), solvedDivRem.getValue());
            }
        }
    }

    private static void assertPairWiseCoprime(Set<BigInteger> xs) {
        for (BigInteger x1 : xs) {
            for (BigInteger x2 : xs) {
                if (!x1.equals(x2)) {
                    assertThat(x1.gcd(x2)).isEqualTo(BigInteger.ONE);
                }
            }
        }
    }

    static int inputTimestamp = 1011416;
    static int[] inputBusIds = new int[]{41, 37, 911, 13, 17, 23, 29, 827, 19};
    static int[] inputBusSpecs = new int[]{41, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 37, 0, 0, 0, 0, 0, 911, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 13, 17, 0, 0, 0, 0, 0, 0, 0, 0, 23, 0, 0, 0, 0, 0, 29, 0, 827, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 19};
}

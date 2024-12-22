package y2024;

import com.google.common.base.Stopwatch;
import lombok.Value;
import scala.Int;

import java.net.Inet4Address;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static aoc.Common.loadInputFromResources;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2024D22 {

    public static void main(String[] args) throws Exception {
        List<String> input = loadInputFromResources();
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(nextSecret(123, 1)).isEqualTo(15887950);
        assertThat(nextSecret(123, 2)).isEqualTo(16495136);
        assertThat(nextSecret(1, 2000)).isEqualTo(8685429);
        assertThat(part1(example)).isEqualTo(37327623);
        assertThat(part1(input)).isEqualTo(13753970725L);

        // 2
        assertThat(part2(example2)).isEqualTo(23);
        assertThat(part2(input)).isEqualTo(1570);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static long part1(List<String> input) {
        long sum = 0L;
        for (int i = 0; i < input.size(); i++) {
            System.out.printf("part1 %s of %s\n", i, input.size());
            String s = input.get(i);
            long x = Long.parseLong(s);
            long l = nextSecret(x, 2000);
            sum += l;
        }
        return sum;
    }

    private static long nextSecret(long x, int n) {
        for (int i = 0; i < n; i++) {
            x = nextSecret(x);
        }
        return x;
    }

    private static long nextSecret(long x) {
        long a = prune((x * 64) ^ x);
        long b = prune((a / 32) ^ a);
        return prune((b * 2048) ^ b);
    }

    private static long prune(long a) {
        long ret = a % 16777216;
        if (ret < 0) {
            ret += 16777216;
        }
        return ret;
    }

    @Value
    static class FourSeq {
        int a, b, c, d;

        public FourSeq next(int x) {
            return new FourSeq(b, c, d, x);
        }
    }

    private static long part2(List<String> input) {
        Map<FourSeq, Map<Integer, Integer>> seqToMonkeyIdToFirstPrice = new HashMap<>();

        for (int monkeyId = 0; monkeyId < input.size(); monkeyId++) {
            System.out.printf("examining %s of %s\n", monkeyId, input.size());
            long a = Long.parseLong(input.get(monkeyId));
            long b = nextSecret(a);
            long c = nextSecret(b);
            long d = nextSecret(c);
            FourSeq seq = new FourSeq(0, digitDiff(b, a), digitDiff(c, b), digitDiff(d, c));
            long secret = d;

            for (int step = 4; step <= 2000; step++) {
                long nextSecret = nextSecret(secret);
                seq = seq.next(digitDiff(nextSecret, secret));
                secret = nextSecret;

                Map<Integer, Integer> monkeyIdToFirstPrice = seqToMonkeyIdToFirstPrice.computeIfAbsent(seq, k -> new HashMap<>());
                monkeyIdToFirstPrice.putIfAbsent(monkeyId, mod10(nextSecret));
            }
        }

        long bestBananaCount = 0;
        for (Map<Integer, Integer> monkeyIdToFirstPrice : seqToMonkeyIdToFirstPrice.values()) {
            long bananaCount = monkeyIdToFirstPrice.values().stream().mapToLong(x -> x).sum();
            bestBananaCount = Math.max(bestBananaCount, bananaCount);
        }
        return bestBananaCount;
    }

    static int digitDiff(long a, long b) {
        return mod10(a) - mod10(b);
    }

    private static int mod10(long a) {
        long ret = a % 10;
        if (ret < 0) {
            ret += 10;
        }
        return (int) ret;
    }

    static List<String> example = List.of(
            "1",
            "10",
            "100",
            "2024");

    static List<String> example2 = List.of(
            "1",
            "2",
            "3",
            "2024");
}

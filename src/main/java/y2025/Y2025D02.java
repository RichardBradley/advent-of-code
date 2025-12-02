package y2025;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Iterables;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import static aoc.Common.loadInputFromResources;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2025D02 {

    public static void main(String[] args) throws Exception {
        List<String> input = loadInputFromResources();
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(part1(example)).isEqualTo(1227775554L);
        assertThat(part1(input)).isEqualTo(16793817782L);

        // 2
        assertThat(part2(example)).isEqualTo(4174379265L);
        assertThat(part2(input)).isEqualTo(27469417404L);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static long part1(List<String> input) {
        String line = Iterables.getOnlyElement(input);
        long sum = 0;
        for (String rangeStr : Splitter.on(",")
                .trimResults()
                .splitToList(line)) {
            String[] fromTo = rangeStr.split("-");
            checkState(fromTo.length == 2);
            long from = Long.parseLong(fromTo[0]);
            long to = Long.parseLong(fromTo[1]);

            sum += sumInvalidIds(from, to);
        }
        return sum;
    }

    private static long part2(List<String> input) {
        String line = Iterables.getOnlyElement(input);
        Set<Long> ids = new TreeSet<>();
        for (String rangeStr : Splitter.on(",")
                .trimResults()
                .splitToList(line)) {
            String[] fromTo = rangeStr.split("-");
            checkState(fromTo.length == 2);
            long from = Long.parseLong(fromTo[0]);
            long to = Long.parseLong(fromTo[1]);

            findInvalidIdsPart2(ids, from, to);
        }
        return ids.stream().mapToLong(Long::longValue).sum();
    }

    private static long sumInvalidIds(long from, long to) {
        checkState(from < to);
        String fromStr = Long.toString(from);
        int fromLen = fromStr.length();
        String toStr = Long.toString(to);
        int toLen = toStr.length();

        if (toLen > fromLen) {
            // 95 - 105  => 95-99 + 100-105
            checkState(toLen == fromLen + 1);
            long nines = Long.parseLong("9".repeat(fromLen));

            return sumInvalidIds(from, nines)
                    + sumInvalidIds(nines + 1, to);
        }
        checkState(fromLen == toLen);

        if (fromLen % 2 != 0) {
            return 0;
        }

        int halfLen = fromLen / 2;
        long firstInvalid = Long.parseLong(fromStr.substring(0, halfLen).repeat(2));
        // 1212 => 1212 + 1 + 10^2 => 1212 + 101 => 1313
        long step = 1 + Math.round(Math.pow(10, halfLen));
        if (firstInvalid < from) {
            firstInvalid += step;
        }
        long lastInvalid = Long.parseLong(toStr.substring(0, halfLen).repeat(2));
        if (lastInvalid > to) {
            lastInvalid -= step;
        }

        long sum = 0;
        for (long x = firstInvalid; x <= lastInvalid; x += step) {
            sum += x;
        }
        return sum;
    }

    private static void findInvalidIdsPart2(Set<Long> ids, long from, long to) {
        checkState(from < to);
        String fromStr = Long.toString(from);
        int fromLen = fromStr.length();
        String toStr = Long.toString(to);
        int toLen = toStr.length();

        if (toLen > fromLen) {
            // 95 - 105  => 95-99 + 100-105
            checkState(toLen == fromLen + 1);
            long nines = Long.parseLong("9".repeat(fromLen));

            findInvalidIdsPart2(ids, from, nines);
            findInvalidIdsPart2(ids, nines + 1, to);
            return;
        }
        checkState(fromLen == toLen);

        for (int subSeqLen = 1; subSeqLen <= fromLen / 2; subSeqLen++) {
            if (fromLen % subSeqLen == 0) {
                findInvalidIdsPart2(ids, from, to, subSeqLen);
            }
        }
    }

    static void findInvalidIdsPart2(Set<Long> ids, long from, long to, int subSeqLen) {
        int fromLen = Long.toString(from).length();

        int subSeqCount = fromLen / subSeqLen;
        long step = 1;
        for (int i = 1; i < subSeqCount; i++) {
            step *= Math.round(Math.pow(10, subSeqLen));
            step++;
        }
        // step is e.g. 10101

        // start is e.g. 101010
        long start = Long.parseLong(Long.toString(Math.round(Math.pow(10, subSeqLen - 1))).repeat(subSeqCount));

        for (long x = start; x <= to; x += step) {
            if (x >= from) {
                ids.add(x);
            }
        }
    }

    static List<String> example = List.of(
            "11-22,95-115,998-1012,1188511880-1188511890,222220-222224,\n" +
                    "1698522-1698528,446443-446449,38593856-38593862,565653-565659,\n" +
                    "824824821-824824827,2121212118-2121212124");
}

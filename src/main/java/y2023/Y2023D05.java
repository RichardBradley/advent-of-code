package y2023;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;
import lombok.Value;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2023D05 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        List<String> input = Resources.readLines(Resources.getResource("y2023/Y2023D05.txt"), StandardCharsets.UTF_8);

        rangeTests();

        // 1
        assertThat(part1(example)).isEqualTo(35);
        assertThat(part1(input)).isEqualTo(579439039);

        // 2
        assertThat(part2(example)).isEqualTo(46);
        assertThat(part2(input)).isEqualTo(7873084);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static long part1(List<String> input) {
        // What is the lowest location number that corresponds to any of the initial seed numbers?

        int lineIdx = 0;
        Pattern p = Pattern.compile("seeds: ([\\d ]+)");
        Matcher m = p.matcher(input.get(lineIdx++));
        checkState(m.matches());
        List<Long> startingSeeds = Splitter.on(" ")
                .splitToList(m.group(1)).stream()
                .map(s -> Long.parseLong(s))
                .collect(Collectors.toList());

        checkState("".equals(input.get(lineIdx++)));

        List<Long> currentIds = startingSeeds;
        String currentType = "seed";
        while (lineIdx < input.size()) {
            List<Long> nextIds = new ArrayList<>();

            Pattern headerP = Pattern.compile("(\\w+)-to-(\\w+) map:");
            Matcher headerM = headerP.matcher(input.get(lineIdx++));
            checkState(headerM.matches());
            assertThat(currentType).isEqualTo(headerM.group(1));
            String nextType = headerM.group(2);

            Set<Long> mapped = new HashSet<>();
            String line;
            while (lineIdx < input.size() && !"".equals(line = input.get(lineIdx++))) {
                List<Long> rangeSpec = Splitter.on(" ")
                        .splitToList(line).stream()
                        .map(s -> Long.parseLong(s))
                        .collect(Collectors.toList());
                assertThat(rangeSpec.size()).isEqualTo(3);
                long toId = rangeSpec.get(0);
                long fromId = rangeSpec.get(1);
                long len = rangeSpec.get(2);

                for (Long currentId : currentIds) {
                    if (currentId >= fromId && currentId < (fromId + len)) {
                        long mapsTo = currentId + (toId - fromId);
                        System.out.printf("%s number %s corresponds to %s number %s\n",
                                currentType, currentId, nextType, mapsTo);
                        nextIds.add(mapsTo);
                        mapped.add(currentId);
                    }
                }
            }

            // Any source numbers that aren't mapped correspond to the same destination number.
            for (Long currentId : currentIds) {
                if (!mapped.contains(currentId)) {
                    long mapsTo = currentId;
                    System.out.printf("%s number %s corresponds to %s number %s\n",
                            currentType, currentId, nextType, mapsTo);
                    nextIds.add(mapsTo);
                    mapped.add(currentId);
                }
            }

            assertThat(currentIds.size()).isEqualTo(nextIds.size());
            System.out.printf("\n\nFinished %s now at %s\n",
                    currentType, nextType);
            currentType = nextType;
            currentIds = nextIds;
        }

        return currentIds.stream().mapToLong(i -> i).min().getAsLong();
    }

    @Value
    private static class Range {
        long from;
        long len;

        // excl
        long end() {
            return from + len;
        }

        boolean overlaps(Range other) {
            return from < other.end() && end() > other.from;
        }

        RangeSplit splitBy(Range other) {
            Range before = (from < other.from)
                    ? new Range(from, Math.min(end(), other.from) - from)
                    : null;
            Range after = (end() > other.end())
                    ? new Range(Math.max(from, other.end()), end() - Math.max(from, other.end()))
                    : null;
            long s = Math.max(from, other.from);
            long e = Math.min(end(), other.end());
            Range overlap = new Range(s, e - s);
            return new RangeSplit(before, overlap, after);
        }
    }

    @Value
    private static class RangeSplit {
        @Nullable
        Range before;
        Range overlap;
        @Nullable
        Range after;
    }

    private static void rangeTests() {
        assertThat(new Range(1, 4).overlaps(new Range(5, 5))).isFalse();
        assertThat(new Range(1, 6).overlaps(new Range(5, 5))).isTrue();
        assertThat(new Range(11, 5).overlaps(new Range(5, 5))).isFalse();
        assertThat(new Range(10, 6).overlaps(new Range(5, 6))).isTrue();
        assertThat(new Range(5, 1).overlaps(new Range(4, 5))).isTrue();
        assertThat(new Range(10, 10).splitBy(new Range(12, 4)))
                .isEqualTo(new RangeSplit(new Range(10, 2), new Range(12, 4), new Range(16, 4)));
    }

    private static long part2(List<String> input) {
        // What is the lowest location number that corresponds to any of the initial seed numbers?
        int lineIdx = 0;
        Pattern p = Pattern.compile("seeds: ([\\d ]+)");
        Matcher m = p.matcher(input.get(lineIdx++));
        checkState(m.matches());
        List<Long> startingSeedRanges = Splitter.on(" ")
                .splitToList(m.group(1)).stream()
                .map(s -> Long.parseLong(s))
                .collect(Collectors.toList());

        List<Range> startingSeeds = new ArrayList<>();
        for (int i = 0; i < startingSeedRanges.size(); i += 2) {
            long from = startingSeedRanges.get(i);
            long count = startingSeedRanges.get(i + 1);
            startingSeeds.add(new Range(from, count));
        }

        checkState("".equals(input.get(lineIdx++)));

        List<Range> currentIds = startingSeeds;
        String currentType = "seed";
        while (lineIdx < input.size()) {
            List<Range> nextIds = new ArrayList<>();

            Pattern headerP = Pattern.compile("(\\w+)-to-(\\w+) map:");
            Matcher headerM = headerP.matcher(input.get(lineIdx++));
            checkState(headerM.matches());
            assertThat(currentType).isEqualTo(headerM.group(1));
            String nextType = headerM.group(2);

            String line;
            while (lineIdx < input.size() && !"".equals(line = input.get(lineIdx++))) {
                List<Long> rangeSpec = Splitter.on(" ")
                        .splitToList(line).stream()
                        .map(s -> Long.parseLong(s))
                        .collect(Collectors.toList());
                assertThat(rangeSpec.size()).isEqualTo(3);
                long toId = rangeSpec.get(0);
                long fromId = rangeSpec.get(1);
                long len = rangeSpec.get(2);
                Range fromMapR = new Range(fromId, len);

                for (int i = 0; i < currentIds.size(); i++) {
                    Range current = currentIds.get(i);
                    if (current.overlaps(fromMapR)) {
                        currentIds.remove(i);
                        i--;
                        RangeSplit split = current.splitBy(fromMapR);
                        if (split.before != null) {
                            currentIds.add(split.before);
                        }
                        if (split.after != null) {
                            currentIds.add(split.after);
                        }
                        // mapped
                        nextIds.add(new Range(split.overlap.from + (toId - fromId),
                                split.overlap.len));
                    }
                }
            }

            // Any source numbers that aren't mapped correspond to the same destination number.
            for (Range current : currentIds) {
                nextIds.add(current);
            }

            currentType = nextType;
            currentIds = nextIds;
        }

        return currentIds.stream().mapToLong(i -> i.from).min().getAsLong();
    }

    static List<String> example = List.of(
            "seeds: 79 14 55 13",
            "",
            "seed-to-soil map:",
            "50 98 2",
            "52 50 48",
            "",
            "soil-to-fertilizer map:",
            "0 15 37",
            "37 52 2",
            "39 0 15",
            "",
            "fertilizer-to-water map:",
            "49 53 8",
            "0 11 42",
            "42 0 7",
            "57 7 4",
            "",
            "water-to-light map:",
            "88 18 7",
            "18 25 70",
            "",
            "light-to-temperature map:",
            "45 77 23",
            "81 45 19",
            "68 64 13",
            "",
            "temperature-to-humidity map:",
            "0 69 1",
            "1 0 69",
            "",
            "humidity-to-location map:",
            "60 56 37",
            "56 93 4"
    );
}

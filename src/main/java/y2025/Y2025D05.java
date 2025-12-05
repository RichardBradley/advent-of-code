package y2025;

import com.google.common.base.Stopwatch;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static aoc.Common.loadInputFromResources;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2025D05 {

    public static void main(String[] args) throws Exception {
        List<String> input = loadInputFromResources();
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(part1(example)).isEqualTo(3);
        assertThat(part1(input)).isEqualTo(505);

        // 2
        assertThat(part2(example)).isEqualTo(14);
        assertThat(part2(input)).isEqualTo(344423158480189L);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    @Value
    static class RangeInc {
        long from;
        long to;

        public boolean overlaps(RangeInc other) {
            return this.from <= other.to && other.from <= this.to;
        }

        public RangeInc merge(RangeInc other) {
            return new RangeInc(Math.min(this.from, other.from), Math.max(this.to, other.to));
        }
    }

    private static long part1(List<String> input) {
        List<RangeInc> ranges = new ArrayList<>();
        int lineIdx = 0;
        for (; lineIdx < input.size(); lineIdx++) {
            String line = input.get(lineIdx);
            if (line.isEmpty()) {
                break;
            }
            String[] parts = line.split("-");
            checkState(parts.length == 2);
            ranges.add(new RangeInc(Long.parseLong(parts[0]), Long.parseLong(parts[1])));
        }

        int validCount = 0;
        for (lineIdx++; lineIdx < input.size(); lineIdx++) {
            String line = input.get(lineIdx);
            long id = Long.parseLong(line);
            for (RangeInc range : ranges) {
                if (id >= range.from && id <= range.to) {
                    validCount++;
                    break;
                }
            }
        }
        return validCount;
    }

    private static long part2(List<String> input) {
        List<RangeInc> ranges = new ArrayList<>();
        int lineIdx = 0;
        for (; lineIdx < input.size(); lineIdx++) {
            String line = input.get(lineIdx);
            if (line.isEmpty()) {
                break;
            }
            String[] parts = line.split("-");
            checkState(parts.length == 2);
            ranges.add(new RangeInc(Long.parseLong(parts[0]), Long.parseLong(parts[1])));
        }

        for (int i = 0; i < ranges.size(); i++) {
            for (int j = i + 1; j < ranges.size(); j++) {
                if (ranges.get(i).overlaps(ranges.get(j))) {
                    ranges.set(i, ranges.get(i).merge(ranges.get(j)));
                    ranges.remove(j);
                    i--;
                    break;
                }
            }
        }

        long count = 0;
        for (RangeInc range : ranges) {
            count += (range.to - range.from + 1);
        }
        return count;
    }

    static List<String> example = List.of(
            "3-5",
            "10-14",
            "16-20",
            "12-18",
            "",
            "1",
            "5",
            "8",
            "11",
            "17",
            "32");
}

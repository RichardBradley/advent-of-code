package y2022;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;
import lombok.Value;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;
import static java.lang.Math.toIntExact;

public class Y2022D20 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        List<String> input = Resources.readLines(Resources.getResource("y2022/Y2022D20.txt"), StandardCharsets.UTF_8);

        // 1
        assertThat(part1(example)).isEqualTo(3);
        System.out.println(part1(input)); // 3473

        // 2
        assertThat(part2(example)).isEqualTo(1623178306);
        System.out.println(part2(input));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    @Value
    static class Item {
        int originalIdx;
        long value;
    }

    private static long part1(List<String> input) {
        // In the above example, the 1000th number after 0 is 4, the 2000th
        // is -3, and the 3000th is 2; adding these together produces 3.
        List<Item> list = new ArrayList<>();
        int size = input.size();
        int sizeMinusOne = size - 1;
        for (int i = 0; i < size; i++) {
            list.add(new Item(i, Integer.parseInt(input.get(i))));
        }

        for (int i = 0; i < size; i++) {
            int ii = i;
            int currIdx = findIdx(list, x -> x.originalIdx == ii);
            Item item = list.get(currIdx);
            int targetIdx = toIntExact(mod(currIdx + item.value, sizeMinusOne));

            // their circle is differently connected to ours
            if (targetIdx == 0) {
                targetIdx = list.size() - 1;
            }

            list.remove(currIdx);
            list.add(targetIdx, item);
            // System.out.println(list.stream().map(x -> x.value + "").collect(Collectors.joining(", ")));
        }

        int idxOfZero = findIdx(list, x -> x.value == 0);

        return list.get((idxOfZero + 1000) % list.size()).value
                + list.get((idxOfZero + 2000) % list.size()).value
                + list.get((idxOfZero + 3000) % list.size()).value;
    }

    private static long mod(long a, long m) {
        long ret = a % m;
        if (ret < 0) {
            ret += m;
            checkState(ret > 0);
        }
        return ret;
    }

    static int findIdx(List<Item> xs, Function<Item, Boolean> predicate) {
        for (int i = 0; i < xs.size(); i++) {
            if (predicate.apply(xs.get(i))) {
                return i;
            }
        }
        throw new RuntimeException("not found");
    }


    private static long part2(List<String> input) {
        List<Item> list = new ArrayList<>();
        int size = input.size();
        int sizeMinusOne = size - 1;
        for (int i = 0; i < size; i++) {
            list.add(new Item(i, 811589153 * Long.parseLong(input.get(i))));
        }

        for (int n = 0; n < 10; n++) {
            for (int i = 0; i < size; i++) {
                int ii = i;
                int currIdx = findIdx(list, x -> x.originalIdx == ii);
                Item item = list.get(currIdx);
                int targetIdx = toIntExact(mod(currIdx + item.value, sizeMinusOne));

                // their circle is differently connected to ours
                if (targetIdx == 0) {
                    targetIdx = list.size() - 1;
                }

                list.remove(currIdx);
                list.add(targetIdx, item);
                // System.out.println(list.stream().map(x -> x.value + "").collect(Collectors.joining(", ")));
            }
        }

        int idxOfZero = findIdx(list, x -> x.value == 0);

        return list.get((idxOfZero + 1000) % list.size()).value
                + list.get((idxOfZero + 2000) % list.size()).value
                + list.get((idxOfZero + 3000) % list.size()).value;
    }


    private static List<String> example = List.of(
            "1",
            "2",
            "-3",
            "3",
            "-2",
            "0",
            "4");
}

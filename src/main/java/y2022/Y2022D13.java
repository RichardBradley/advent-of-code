package y2022;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2022D13 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        List<String> input = Resources.readLines(Resources.getResource("y2022/Y2022D13.txt"), StandardCharsets.UTF_8);

        // 1
        assertThat(part1(example)).isEqualTo(13);
        System.out.println(part1(input));

        // 2
        assertThat(part2(example)).isEqualTo(140);
        System.out.println(part2(input));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    /**
     * Determine which pairs of packets are already in the right order.
     * What is the sum of the indices of those pairs?
     */
    private static int part1(List<String> input) {
        int sumOfIndices = 0;
        int index = 1;
        int inputIdx = 0;
        while (true) {
            JsonElement first = parse(input.get(inputIdx++));
            JsonElement second = parse(input.get(inputIdx++));

            boolean isRightOrder = isRightOrder(first, second);
            if (isRightOrder) {
                sumOfIndices += index;
            }

            index++;
            if (inputIdx < input.size()) {
                checkState("".equals(input.get(inputIdx++)));
            } else {
                break;
            }
        }

        return sumOfIndices;
    }

    private static Boolean isRightOrder(JsonElement first, JsonElement second) {
        if (first.isJsonPrimitive() && second.isJsonPrimitive()) {
            // If both values are integers, the lower integer should
            // come first. If the left integer is lower than the right
            // integer, the inputs are in the right order. If the left
            // integer is higher than the right integer, the inputs are
            // not in the right order. Otherwise, the inputs are the same
            // integer; continue checking the next part of the input.
            int left = first.getAsInt();
            int right = second.getAsInt();
            if (left < right) {
                return true;
            }
            if (right < left) {
                return false;
            }
            return null;
        }
        // If both values are lists
        if (first.isJsonArray() && second.isJsonArray()) {
            return isRightOrder(first.getAsJsonArray(), second.getAsJsonArray());
        }
        // If exactly one value is an integer, convert the
        // integer to a list which contains that integer
        // as its only value, then retry the comparison.
        if (first.isJsonArray()) {
            return isRightOrder(
                    first.getAsJsonArray(),
                    wrapAsArray(second.getAsJsonPrimitive()));
        } else if (second.isJsonArray()) {
            return isRightOrder(
                    wrapAsArray(first.getAsJsonPrimitive()),
                    second.getAsJsonArray());
        }
        throw new IllegalArgumentException("unreachable");
    }

    private static JsonArray wrapAsArray(JsonElement e) {
        JsonArray arr = new JsonArray(1);
        arr.add(e);
        return arr;
    }

    private static Boolean isRightOrder(JsonArray first, JsonArray second) {
        // If both values are lists, compare the first value of
        // each list, then the second value, and so on. If the left
        // list runs out of items first, the inputs are in the right
        // order. If the right list runs out of items first, the
        // inputs are not in the right order. If the lists are the
        // same length and no comparison makes a decision about
        // the order, continue checking the next part of the input.

        for (int i = 0; ; i++) {
            if (i >= first.size()) {
                if (i >= second.size()) {
                    return null;
                } else {
                    return true;
                }
            }
            if (i >= second.size()) {
                return false;
            }
            JsonElement leftVal = first.get(i);
            JsonElement rightVal = second.get(i);
            Boolean itemComp = isRightOrder(leftVal, rightVal);
            if (itemComp != null) {
                return itemComp;
            }
        }
    }

    private static Gson gson = new Gson();

    private static JsonElement parse(String line) {
        return gson.fromJson(line, JsonElement.class);
    }

    private static int part2(List<String> input) {
        List<String> list = new ArrayList<>(input.stream().filter(i -> !i.equals("")).collect(Collectors.toList()));
        list.add("[[2]]");
        list.add("[[6]]");

        list.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                JsonElement first = parse(o1);
                JsonElement second = parse(o2);

                Boolean rightOrder = isRightOrder(first, second);
                return rightOrder ? -1 : 1;
            }
        });

        return (list.indexOf("[[2]]") + 1)
                * (list.indexOf("[[6]]") + 1);
    }


    private static List<String> example = List.of(
            "[1,1,3,1,1]",
            "[1,1,5,1,1]",
            "",
            "[[1],[2,3,4]]",
            "[[1],4]",
            "",
            "[9]",
            "[[8,7,6]]",
            "",
            "[[4,4],4,4]",
            "[[4,4],4,4,4]",
            "",
            "[7,7,7,7]",
            "[7,7,7]",
            "",
            "[]",
            "[3]",
            "",
            "[[[]]]",
            "[[]]",
            "",
            "[1,[2,[3,[4,[5,6,7]]]],8,9]",
            "[1,[2,[3,[4,[5,6,0]]]],8,9]");
}

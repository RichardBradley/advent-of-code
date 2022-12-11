package y2022;

import com.google.common.base.Function;
import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;
import lombok.Value;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2022D11 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        List<String> input = Resources.readLines(Resources.getResource("y2022/Y2022D11.txt"), StandardCharsets.UTF_8);

        // 1
        assertThat(part1(example)).isEqualTo(10605);
        System.out.println(part1(input));

        // 2
        assertThat(part2(example)).isEqualTo(2713310158L);
        System.out.println(part2(input));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    /**
     * What is the level of monkey business after 20 rounds of stuff-slinging simian shenanigans?
     */
    private static long part1(List<String> input) {
        World world = parse(input);

        for (int round = 0; round < 20; round++) {
            for (Monkey monkey : world.monkeys) {
                for (Long item : monkey.items) {
                    long newWorry = monkey.operation.apply(item) / 3;
                    int target = monkey.test.apply(newWorry);
                    world.monkeys.get(target).items.add(newWorry);
                    monkey.itemsInspectedCount++;
                }
                monkey.items.clear();
            }
        }

        return world.monkeys.stream()
                .sorted(Comparator.<Monkey, Integer>comparing(m -> m.itemsInspectedCount).reversed())
                .limit(2)
                .mapToInt(m -> m.itemsInspectedCount)
                .reduce(1, (a, b) -> a * b);
    }

    private static long part2(List<String> input) {
        World world = parse(input);

        for (int round = 0; round < 10000; round++) {
            for (Monkey monkey : world.monkeys) {
                for (Long item : monkey.items) {
                    long newWorry = monkey.operation.apply(item) % world.modulus;
                    int target = monkey.test.apply(newWorry);
                    world.monkeys.get(target).items.add(newWorry);
                    monkey.itemsInspectedCount++;
                }
                monkey.items.clear();
            }
        }

        return world.monkeys.stream()
                .sorted(Comparator.<Monkey, Integer>comparing(m -> m.itemsInspectedCount).reversed())
                .limit(2)
                .mapToLong(m -> m.itemsInspectedCount)
                .reduce(1, (a, b) -> a * b);
    }

    @Value
    static class World {
        List<Monkey> monkeys;
        long modulus;
    }

    private static World parse(List<String> input) {
        long modulus = 1;
        List<Monkey> acc = new ArrayList<>();
        int idx = 0;
        while (true) {
            checkState(input.get(idx++).equals("Monkey " + acc.size() + ":"));
            Monkey m = new Monkey();
            acc.add(m);

            {
                String line = input.get(idx++);
                checkState(line.startsWith("  Starting items: "));
                String[] itemsS = line.substring("  Starting items: ".length()).split(", ");
                m.items = Arrays.stream(itemsS).map(Long::parseLong).collect(Collectors.toList());
            }

            {
                Pattern p1 = Pattern.compile("  Operation: new = old ([+*]) (\\d+|old)");
                Matcher m1 = p1.matcher(input.get(idx++));
                checkState(m1.matches());
                boolean isAdd = m1.group(1).equals("+");
                boolean isOld = m1.group(2).equals("old");
                long operand = isOld ? 0 : Integer.parseInt(m1.group(2));
                m.operation = (old -> {
                    long op = isOld ? old : operand;
                    return isAdd ? old + op : Math.multiplyExact(old, op);
                });
            }

            {
                Pattern p2 = Pattern.compile("  Test: divisible by (\\d+)");
                Matcher m2 = p2.matcher(input.get(idx++));
                checkState(m2.matches());
                long divOp = Integer.parseInt(m2.group(1));
                modulus *= divOp;

                Pattern p3 = Pattern.compile("    If true: throw to monkey (\\d+)");
                Matcher m3 = p3.matcher(input.get(idx++));
                checkState(m3.matches());
                int targetTrue = Integer.parseInt(m3.group(1));

                Pattern p4 = Pattern.compile("    If false: throw to monkey (\\d+)");
                Matcher m4 = p4.matcher(input.get(idx++));
                checkState(m4.matches());
                int targetFalse = Integer.parseInt(m4.group(1));

                m.test = (worry -> (worry % divOp == 0) ? targetTrue : targetFalse);
            }

            if (idx == input.size()) {
                return new World(acc, modulus);
            } else {
                checkState(input.get(idx++).equals(""));
            }
        }
    }

    private static class Monkey {
        List<Long> items = new ArrayList<>();
        Function<Long, Long> operation; // old -> new worry level
        Function<Long, Integer> test; // worry level -> target monkey id
        int itemsInspectedCount = 0;
    }

    private static List<String> example = List.of(
            "Monkey 0:",
            "  Starting items: 79, 98",
            "  Operation: new = old * 19",
            "  Test: divisible by 23",
            "    If true: throw to monkey 2",
            "    If false: throw to monkey 3",
            "",
            "Monkey 1:",
            "  Starting items: 54, 65, 75, 74",
            "  Operation: new = old + 6",
            "  Test: divisible by 19",
            "    If true: throw to monkey 2",
            "    If false: throw to monkey 0",
            "",
            "Monkey 2:",
            "  Starting items: 79, 60, 97",
            "  Operation: new = old * old",
            "  Test: divisible by 13",
            "    If true: throw to monkey 1",
            "    If false: throw to monkey 3",
            "",
            "Monkey 3:",
            "  Starting items: 74",
            "  Operation: new = old + 3",
            "  Test: divisible by 17",
            "    If true: throw to monkey 0",
            "    If false: throw to monkey 1");
}

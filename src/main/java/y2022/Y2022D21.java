package y2022;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;
import lombok.Value;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2022D21 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        List<String> input = Resources.readLines(Resources.getResource("y2022/Y2022D21.txt"), StandardCharsets.UTF_8);

        // 1
        assertThat(part1(example)).isEqualTo(152);
        System.out.println(part1(input));

        // 2
        assertThat(part2(example)).isEqualTo(301);
        assertThat(part2(input)).isEqualTo(3379022190351L);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static long part1(List<String> input) {
        Map<String, Object> monkeysByName = parse(input);

        return eval(monkeysByName, "root", null);
    }

    static class ContainsUnknownXEx extends RuntimeException {
    }

    private static long eval(Map<String, Object> monkeysByName, String monkeyName, Long x) {
        Object currVal = monkeysByName.get(monkeyName);
        if (currVal instanceof Long) {
            return (Long) currVal;
        }
        if ("X".equals(currVal)) {
            if (x == null) {
                throw new ContainsUnknownXEx();
            } else {
                return x;
            }
        }
        MonkeyExpr expr = (MonkeyExpr) currVal;
        long lhs = eval(monkeysByName, expr.lhs, x);
        long rhs = eval(monkeysByName, expr.rhs, x);
        switch (expr.op) {
            case "+":
                return lhs + rhs;
            case "-":
                return lhs - rhs;
            case "*":
                return lhs * rhs;
            case "/":
                return lhs / rhs;
            default:
                throw new IllegalArgumentException(expr.op);
        }
    }

    @Value
    static class MonkeyExpr {
        String lhs;
        String op;
        String rhs;
    }

    private static long part2(List<String> input) {
        Map<String, Object> monkeysByName = parse(input);

        // part 2 changes:
        monkeysByName.put("humn", "X");
        MonkeyExpr root = (MonkeyExpr) monkeysByName.get("root");

        // need equal between lhs and rhs
        // rhs is a constant:
        long target = eval(monkeysByName, root.rhs, null);
        long x = findX(monkeysByName, target, root.lhs);
        assertThat(eval(monkeysByName, root.lhs, x)).isEqualTo(target);
        return x;
    }

    private static Map<String, Object> parse(List<String> input) {
        Map<String, Object> monkeysByName = new HashMap<>();
        Pattern p = Pattern.compile("(\\w+): ((\\d+)|(\\w+) ([+/*-]) (\\w+))");
        for (String line : input) {
            Matcher m = p.matcher(line);
            checkState(m.matches());
            String name = m.group(1);
            if (null != m.group(3)) {
                checkState(null == monkeysByName.put(name, Long.parseLong(m.group(3))));
            } else {
                String lhs = m.group(4);
                String op = m.group(5);
                String rhs = m.group(6);
                checkState(null == monkeysByName.put(name, new MonkeyExpr(lhs, op, rhs)));
            }
        }
        return monkeysByName;
    }

    private static long findX(Map<String, Object> monkeysByName, long target, String name) {
        Object curr = monkeysByName.get(name);
        if ("X".equals(curr)) {
            return target;
        }
        if (!(curr instanceof MonkeyExpr)) {
            throw new IllegalStateException();
        }
        MonkeyExpr expr = (MonkeyExpr) curr;
        switch (expr.op) {
            case "+":
                try {
                    // target = X + rhs
                    // X = target - rhs
                    return findX(monkeysByName, target - eval(monkeysByName, expr.rhs, null), expr.lhs);
                } catch (ContainsUnknownXEx e) {
                    return findX(monkeysByName, target - eval(monkeysByName, expr.lhs, null), expr.rhs);
                }
            case "-":
                try {
                    // target = X - rhs
                    // X = target + rhs
                    return findX(monkeysByName, target + eval(monkeysByName, expr.rhs, null), expr.lhs);
                } catch (ContainsUnknownXEx e) {
                    // target = lhs - X
                    // X = lhs - target
                    return findX(monkeysByName, eval(monkeysByName, expr.lhs, null) - target, expr.rhs);
                }
            case "*":
                try {
                    // target = X * rhs
                    // X = target / rhs
                    return findX(monkeysByName, target / eval(monkeysByName, expr.rhs, null), expr.lhs);
                } catch (ContainsUnknownXEx e) {
                    return findX(monkeysByName, target / eval(monkeysByName, expr.lhs, null), expr.rhs);
                }
            case "/":
                try {
                    // target = X / rhs
                    // X = target * rhs
                    return findX(monkeysByName, target * eval(monkeysByName, expr.rhs, null), expr.lhs);
                } catch (ContainsUnknownXEx e) {
                    return findX(monkeysByName, target * eval(monkeysByName, expr.lhs, null), expr.rhs);
                }
            default:
                throw new IllegalArgumentException(expr.op);
        }
    }

    private static List<String> example = List.of(
            "root: pppw + sjmn",
            "dbpl: 5",
            "cczh: sllz + lgvd",
            "zczc: 2",
            "ptdq: humn - dvpt",
            "dvpt: 3",
            "lfqf: 4",
            "humn: 5",
            "ljgn: 2",
            "sjmn: drzm * dbpl",
            "sllz: 4",
            "pppw: cczh / lfqf",
            "lgvd: ljgn * ptdq",
            "drzm: hmdt - zczc",
            "hmdt: 32");
}

package y2022;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;
import lombok.Value;
import org.apache.commons.math3.fraction.BigFraction;
import org.apache.commons.math3.fraction.Fraction;

import java.math.BigInteger;
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
        assertThat(part2(input)).isLessThan(5842743947753L);
        System.out.println(part2(input)); // 5842743947753 too high

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static long part1(List<String> input) {
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

        return toLong(eval(monkeysByName, "root", null));
    }

    private static long toLong(BigFraction x) {
        assertThat(x.getDenominator()).isEqualTo(BigInteger.ONE);
        return x.getNumerator().longValueExact();
    }

    static class ContainsUnknownXEx extends RuntimeException {
    }

    private static BigFraction eval(Map<String, Object> monkeysByName, String monkeyName, BigFraction x) {
        Object currVal = monkeysByName.get(monkeyName);
        if (currVal instanceof BigFraction) {
            return (BigFraction) currVal;
        }
        if (currVal instanceof Long) {
            return new BigFraction((Long) currVal);
        }
        if ("X".equals(currVal)) {
            if (x == null) {
                throw new ContainsUnknownXEx();
            } else {
                return x;
            }
        }
        MonkeyExpr expr = (MonkeyExpr) currVal;
        BigFraction lhs = eval(monkeysByName, expr.lhs, x);
        BigFraction rhs = eval(monkeysByName, expr.rhs, x);
        switch (expr.op) {
            case "+":
                return lhs.add(rhs);
            case "-":
                return lhs.subtract(rhs);
            case "*":
                return lhs.multiply(rhs);
            case "/":
                return lhs.divide(rhs);
            default:
                throw new IllegalArgumentException(expr.op);
        }
        // qq   monkeysByName.put(monkeyName, val);
    }

    @Value
    static class MonkeyExpr {
        String lhs;
        String op;
        String rhs;
    }


    private static long part2(List<String> input) {
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

        // part 2 changes:
        monkeysByName.put("humn", "X");
        MonkeyExpr root = (MonkeyExpr) monkeysByName.get("root");

        // need equal between lhs and rhs
        // rhs is a constant:
        BigFraction target = eval(monkeysByName, root.rhs, null);

        // qq del
        simplify(monkeysByName, root.lhs);

        BigFraction x = findX(monkeysByName, target, root.lhs);

        assertThat(eval(monkeysByName, root.lhs, x)).isEqualTo(target);
        return toLong(x);
//
//        System.out.println("Want equal between:");
//        System.out.println(asEq(monkeysByName, root.lhs));
//        System.out.println(asEq(monkeysByName, root.rhs));
//
//        return eval(monkeysByName, "root");

    }

    private static void simplify(Map<String, Object> monkeysByName, String name) {
        Object curr = monkeysByName.get(name);
        if (curr instanceof MonkeyExpr) {
            try {
                monkeysByName.put(name, eval(monkeysByName, name, null));
            } catch ( ContainsUnknownXEx e) {
                simplify(monkeysByName, ((MonkeyExpr) curr).lhs);
                simplify(monkeysByName, ((MonkeyExpr) curr).rhs);
            }
        }
    }

    private static BigFraction findX(Map<String, Object> monkeysByName, BigFraction target, String name) {

        Object curr = monkeysByName.get(name);
        if ("X".equals(curr)) {
            return target;
        }
        if (!(curr instanceof MonkeyExpr)) {
            throw new IllegalStateException();
        }
        MonkeyExpr expr = (MonkeyExpr) curr;
        BigFraction x;
        switch (expr.op) {
            case "+":
                try {
                    // target = X + rhs
                    // X = target - rhs
                    x = findX(monkeysByName, target.subtract(eval(monkeysByName, expr.rhs, null)), expr.lhs);
                    checkAnswer(monkeysByName, target, name, x);
                } catch (ContainsUnknownXEx e) {
                    x = findX(monkeysByName, target.subtract(eval(monkeysByName, expr.lhs, null)), expr.rhs);
                    checkAnswer(monkeysByName, target, name, x);
                }
                break;
            case "-":
                try {
                    // target = X - rhs
                    // X = target + rhs
                    x = findX(monkeysByName, target.add(eval(monkeysByName, expr.rhs, null)), expr.lhs);
                    checkAnswer(monkeysByName, target, name, x);
                } catch (ContainsUnknownXEx e) {
                    // target = lhs - X
                    // X = lhs - target
                    x = findX(monkeysByName, eval(monkeysByName, expr.lhs, null).subtract(target), expr.rhs);
                    checkAnswer(monkeysByName, target, name, x);
                }
                break;
            case "*":
                try {
                    // target = X * rhs
                    // X = target / rhs

                    x = findX(monkeysByName, target.divide(eval(monkeysByName, expr.rhs, null)), expr.lhs);
                    checkAnswer(monkeysByName, target, name, x);
                } catch (ContainsUnknownXEx e) {
                    x = findX(monkeysByName, target.divide(eval(monkeysByName, expr.lhs, null)), expr.rhs);
                    checkAnswer(monkeysByName, target, name, x);
                }
                break;
            case "/":
                try {
                    // target = X / rhs
                    // X = target * rhs
                    x = findX(monkeysByName, target .multiply(eval(monkeysByName, expr.rhs, null)), expr.lhs);
                    checkAnswer(monkeysByName, target, name, x);
                } catch (ContainsUnknownXEx e) {
                    x = findX(monkeysByName, target .multiply( eval(monkeysByName, expr.lhs, null)), expr.rhs);
                    checkAnswer(monkeysByName, target, name, x);
                }
                break;
            default:
                throw new IllegalArgumentException(expr.op);
        }

        return x;

    }

    private static long exactDiv(long a, long b) {
        long res = a / b;

        assertThat(b * res).isEqualTo(a);
        return res;
    }

    private static void checkAnswer(Map<String, Object> monkeysByName, BigFraction target, String name, BigFraction x) {
//        Object curr = monkeysByName.get(name);
//        if ("X".equals(curr)) {
//            assertThat(x).isEqualTo(target)
//            return "X = " + target;
//        }
//        if (!(curr instanceof MonkeyExpr)) {
//            throw new IllegalStateException();
//        }
//
//
        BigFraction obs = eval(monkeysByName, name, x);
        if (!obs.equals( target)) {
            MonkeyExpr e = (MonkeyExpr) monkeysByName.get(name);
            String msg = String.format(
                    "Failed consistency:\n" +
                            "%s = %s\n" +
                            "found x = %s\n" +
                            "but then got %s != %s\n" +
                            "from %s = %s %s %s",
                    target,
                    asEq(monkeysByName, name),
                    x,
                    target,
                    obs,
                    target,
                    eval(monkeysByName, e.lhs, x),
                    e.op,
                    eval(monkeysByName, e.rhs, x));
            throw new IllegalStateException(msg);
        }
    }

    private static String asEq(Map<String, Object> monkeysByName, String name) {
        Object currVal = monkeysByName.get(name);
        if (currVal instanceof Long) {
            return Long.toString((Long) currVal);
        }
        if (currVal instanceof Double) {
            return Double.toString((Double) currVal);
        }
        if (currVal instanceof String) {
            return (String) currVal;
        }
        if (currVal instanceof BigFraction) {
            return ((BigFraction) currVal).toString();
        }
        MonkeyExpr expr = (MonkeyExpr) currVal;
        String lhs = asEq(monkeysByName, expr.lhs);
        String rhs = asEq(monkeysByName, expr.rhs);
        return String.format("(%s %s %s)", lhs, expr.op, rhs);
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

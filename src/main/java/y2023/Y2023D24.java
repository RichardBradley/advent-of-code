package y2023;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;
import com.microsoft.z3.*;
import lombok.Value;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.fraction.BigFraction;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2023D24 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            List<String> input = Resources.readLines(Resources.getResource("y2023/Y2023D24.txt"), StandardCharsets.UTF_8);

            // 1
            assertThat(part1(example, 7, 27)).isEqualTo(2);
            assertThat(part1(input, 200000000000000L, 400000000000000L)).isEqualTo(16727);

            // 2
            assertThat(part2(example)).isEqualTo(47);
            assertThat(part2(input)).isEqualTo(0);


        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    @Value
    static class Hailstone {
        Point3 position;
        Point3 velocity;

        public String toString() {
            return String.format("%s, %s, %s @ %s, %s, %s",
                    position.x, position.y, position.z,
                    velocity.x, velocity.y, velocity.z);
        }
    }

    @Value
    static class Point3 {
        long x;
        long y;
        long z;
    }

    private static long part1(List<String> input, long minXY, long maxXY) {
        List<Hailstone> hailstones = parse(input);

        BigFraction minXYf = new BigFraction(minXY);
        BigFraction maxXYf = new BigFraction(maxXY);

        int intersections = 0;
        for (int i = 0; i < hailstones.size(); i++) {
            Hailstone a = hailstones.get(i);
            for (int j = i + 1; j < hailstones.size(); j++) {
                Hailstone b = hailstones.get(j);

                // Need to use exact fractions here, as double loses 0.3% accuracy on some pairs,
                // which is enough to give the wrong answer but probably too large for an "epsilon" comparison

                // Wikipedia
                // https://en.wikipedia.org/wiki/Line%E2%80%93line_intersection
                // Given two points on each line
                BigFraction x1 = new BigFraction(a.position.x);
                BigFraction y1 = new BigFraction(a.position.y);
                BigFraction x2 = new BigFraction(a.position.x + a.velocity.x);
                BigFraction y2 = new BigFraction(a.position.y + a.velocity.y);
                BigFraction x3 = new BigFraction(b.position.x);
                BigFraction y3 = new BigFraction(b.position.y);
                BigFraction x4 = new BigFraction(b.position.x + b.velocity.x);
                BigFraction y4 = new BigFraction(b.position.y + b.velocity.y);

                try {
                    BigFraction px = ((x1.multiply(y2).subtract(y1.multiply(x2)).multiply(x3.subtract(x4)).subtract((x1.subtract(x2).multiply((x3.multiply(y4).subtract(y3.multiply(x4))))))))
                            .divide(((x1.subtract(x2).multiply((y3.subtract(y4))).subtract((y1.subtract(y2).multiply((x3.subtract(x4))))))));
                    BigFraction py = ((x1.multiply(y2).subtract(y1.multiply(x2)).multiply((y3.subtract(y4))).subtract((y1.subtract(y2)).multiply((x3.multiply(y4).subtract(y3.multiply(x4)))))))
                            .divide((x1.subtract(x2).multiply((y3.subtract(y4)))).subtract((y1.subtract(y2)).multiply((x3.subtract(x4)))));

                    if (px.compareTo(minXYf) >= 0 && px.compareTo(maxXYf) <= 0
                            && py.compareTo(minXYf) >= 0 && py.compareTo(maxXYf) <= 0) {
                        // is in future?
                        if (((px.doubleValue() - a.position.x) / a.velocity.x) > 0
                                && ((px.doubleValue() - b.position.x) / b.velocity.x) > 0) {
                            intersections++;
                        }
                    }
                } catch (MathArithmeticException e) {
                    // Ignore divide by zero -- those are lines which don't intersect
                    checkState(e.getMessage().equals("denominator must be different from 0"));
                }
            }
        }
        return intersections;
    }

    private static List<Hailstone> parse(List<String> input) {
        Pattern p = Pattern.compile("(\\d+), +(\\d+), +(\\d+) @ +(-?\\d+), +(-?\\d+), +(-?\\d+)");
        List<Hailstone> hailstones = input.stream().map(line -> {
                    Matcher m = p.matcher(line);
                    checkState(m.matches());
                    return new Hailstone(
                            new Point3(
                                    Long.parseLong(m.group(1)),
                                    Long.parseLong(m.group(2)),
                                    Long.parseLong(m.group(3))),
                            new Point3(
                                    Long.parseLong(m.group(4)),
                                    Long.parseLong(m.group(5)),
                                    Long.parseLong(m.group(6))));
                })
                .collect(Collectors.toList());
        return hailstones;
    }

    static long part2_algebra(List<String> input) {
        List<Hailstone> hailstones = parse(input);

        // Find X,Y,Z, VX,VY,VZ, T1 ... TN
        // so
        // t1 (shot) = t1 hail1
        // etc

        // x_i, vx_i are const, Ti, X, Y, VX, VY are unknown:
        // X + Ti*VX == x_i + Ti * vx_i
        // Y + Ti*VY == y_i + Ti * vy_i
        // Ti ( VY - vy_i) = y_i - Y
        // Ti = (y_i - Y) /  (VY - vy_i)
        // (x_i - X) / (VX - vx_i) = (y_i - Y) /  (VY - vy_i)
        // (VY - vy_i) * (x_i - X) = (y_i - Y) * (VX - vx_i)
        // VY = (y_i - Y) * (VX - vx_i) / (x_i - X) + vy_i

        // Solve for VY, then
        // sub in 1 & 2
        // (y_1 - Y) * (VX - vx_1) / (x_1 - X) + vy_1 = (y_2 - Y) * (VX - vx_2) / (x_2 - X) + vy_2

        // Solve for Y
        // (y_1 * VX - y_1 * vx_1 - Y * VX + Y * vx_1) / (x_1 - X) + vy_1 = (y_2 * VX - y_2 * vx_2 - Y * VX + Y * vx_2) / (x_2 - X) + vy_2
        // (Y * (vx_1 - VX) + y_1 * VX - y_1 * vx_1) * (x_2 - X) + vy_1 * (x_2 - X) = (Y * (vx_2 - VX) + y_2 * VX - y_2 * vx2) * (x_1 - X) + vy_2 * (x_1 - X)
        // Y * (vx_1 - VX) * (x_2 - X) + (y_1 * VX - y_1 * vx_1) * (x_2 - X) + vy_1 * (x_2 - X) = Y * (vx_2 - VX)* (x_1 - X) + (y_2 * VX - y_2 * vx2) * (x_1 - X) + vy_2 * (x_1 - X)
        // Y * ((vx_1 - VX) * (x_2 - X) - (vx_2 - VX)* (x_1 - X)) = (y_2 * VX - y_2 * vx2) * (x_1 - X) + vy_2 * (x_1 - X) - (y_1 * VX - y_1 * vx_1) * (x_2 - X) - vy_1 * (x_2 - X)
        // Y = (y_2 * VX - y_2 * vx2) * (x_1 - X) + vy_2 * (x_1 - X) - (y_1 * VX - y_1 * vx_1) * (x_2 - X) - vy_1 * (x_2 - X) / ((vx_1 - VX) * (x_2 - X) - (vx_2 - VX)* (x_1 - X))
        // free vars left: Y, VX, X

        // sub in 3 & 4, equate the two Ys
        // (y_2 * VX - y_2 * vx2) * (x_1 - X) + vy_2 * (x_1 - X) - (y_1 * VX - y_1 * vx_1) * (x_2 - X) - vy_1 * (x_2 - X) / ((vx_1 - VX) * (x_2 - X) - (vx_2 - VX)* (x_1 - X))
        //  = (y_4 * VX - y_4 * vx2) * (x_3 - X) + vy_4 * (x_3 - X) - (y_3 * VX - y_3 * vx_3) * (x_4 - X) - vy_3 * (x_4 - X) / ((vx_3 - VX) * (x_4 - X) - (vx_4 - VX)* (x_3 - X))

        // Solve for X
        // ??? got confused / fed up at this point.

        return -1;
    }

    static long part2(List<String> input) {
        // Find x,y,z, vx,vy,vz, t1 ... tN
        // so
        // t1 (shot) = t1 hail1
        // etc

        com.microsoft.z3.Global.ToggleWarningMessages(true);
        Log.open("test.log");
        HashMap<String, String> cfg = new HashMap<>();
        cfg.put("model", "true");
        Context ctx = new Context(cfg);

        Solver s = ctx.mkSolver();

        IntExpr x = (IntExpr) ctx.mkConst(ctx.mkSymbol("x"), ctx.getIntSort());
        IntExpr y = (IntExpr) ctx.mkConst(ctx.mkSymbol("y"), ctx.getIntSort());
        IntExpr z = (IntExpr) ctx.mkConst(ctx.mkSymbol("z"), ctx.getIntSort());
        IntExpr vx = (IntExpr) ctx.mkConst(ctx.mkSymbol("vx"), ctx.getIntSort());
        IntExpr vy = (IntExpr) ctx.mkConst(ctx.mkSymbol("vy"), ctx.getIntSort());
        IntExpr vz = (IntExpr) ctx.mkConst(ctx.mkSymbol("vz"), ctx.getIntSort());

        List<Hailstone> hailstones = parse(input);
        for (int i = 0; i < hailstones.size(); i++) {
            Hailstone h = hailstones.get(i);

            IntExpr ti = (IntExpr) ctx.mkConst(ctx.mkSymbol("t" + i), ctx.getIntSort());
            // x + ti*vx == x_i + ti * vx_i
            s.add(ctx.mkEq(
                    ctx.mkAdd(x, ctx.mkMul(ti, vx)),
                    ctx.mkAdd(ctx.mkInt(h.position.x), ctx.mkMul(ti, ctx.mkInt(h.velocity.x)))));
            s.add(ctx.mkEq(
                    ctx.mkAdd(y, ctx.mkMul(ti, vy)),
                    ctx.mkAdd(ctx.mkInt(h.position.y), ctx.mkMul(ti, ctx.mkInt(h.velocity.y)))));
            s.add(ctx.mkEq(
                    ctx.mkAdd(z, ctx.mkMul(ti, vz)),
                    ctx.mkAdd(ctx.mkInt(h.position.z), ctx.mkMul(ti, ctx.mkInt(h.velocity.z)))));
        }

        if (s.check() == Status.SATISFIABLE) {
            Model m = s.getModel();
            System.out.println("Solution:");
            System.out.println("x = " + m.evaluate(x, false));
            System.out.println("y = " + m.evaluate(y, false));
            System.out.println("z = " + m.evaluate(z, false));
            System.out.println("vx = " + m.evaluate(vx, false));
            System.out.println("vy = " + m.evaluate(vy, false));
            System.out.println("vz = " + m.evaluate(vz, false));

            return getLong(m, x)
                    + getLong(m, y)
                    + getLong(m, z);
        } else {
            throw new IllegalStateException("Failed to solve");
        }
    }

    private static long getLong(Model m, IntExpr expr) {
        return ((IntNum) m.evaluate(expr, false)).getInt64();
    }

    static List<String> example = List.of(
            "19, 13, 30 @ -2,  1, -2",
            "18, 19, 22 @ -1, -1, -2",
            "20, 25, 34 @ -2, -2, -4",
            "12, 31, 28 @ -1, -2, -1",
            "20, 19, 15 @  1, -5, -3"
    );
}

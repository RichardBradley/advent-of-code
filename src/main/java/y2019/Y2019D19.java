package y2019;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;

import java.awt.*;
import java.math.BigInteger;
import java.util.Formatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.lang.System.out;
import static y2019.Y2019D09.parse;

public class Y2019D19 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        out.println(runAndPrintPicture(input, 0, 50, 0, 50));

        // 2
        out.println(part2(input, 100));

        out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static int part2(BigInteger[] program, int targetWidth) {
        // x,y is start of sample area
        TractorBeam beam = new TractorBeam(program);

        int beamStartX = 430;
        yLoop:
        for (int y = 600; ; y++) {
            boolean xIsBeforeBeam = true;
            for (int x = beamStartX; ; x++) {
                if (xIsBeforeBeam) {
                    if (beam.isPulled(x, y)) {
                        xIsBeforeBeam = false;
                        beamStartX = x;
                    } else {
                        continue;
                    }
                } else if (!beam.isPulled(x, y)) {
                    // left the RHS of the beam
                    continue yLoop;
                }

                // x,y is in the beam; check if the ship fits here:
                boolean shipFit = true;
                shipFitLoop:
                for (int dx = 0; dx < 100; dx++) {
                    for (int dy = 0; dy < 100; dy++) {
                        if (!beam.isPulled(x + dx, y + dy)) {
                            shipFit = false;
                            break shipFitLoop;
                        }
                    }
                }

                if (shipFit) {
                    return x * 10000 + y;
                }
            }
        }
    }

    private static String runAndPrintPicture(BigInteger[] program, int startX, int width, int startY, int height) {
        StringBuilder acc = new StringBuilder();
        new Formatter(acc).format("(%s,%s) -> (%s,%s)\n", startX, startY, startX + width, startY + height);
        int pulledCount = 0;
        TractorBeam beam = new TractorBeam(program);

        for (int y = startY; y < startY + height; y++) {
            for (int x = startX; x < startX + width; x++) {
                boolean pulled = beam.isPulled(x, y);
                acc.append(pulled ? '#' : '.');
                if (pulled) {
                    pulledCount++;
                }
            }
            acc.append('\n');
        }
        acc.append("Count = ").append(pulledCount);
        return acc.toString();
    }

    private static class TractorBeam {
        final Map<Point, Boolean> isPulledCache = new HashMap<>();
        final BigInteger[] program;

        private TractorBeam(BigInteger[] program) {
            this.program = program;
        }

        boolean isPulled(int x, int y) {
            return isPulledCache.computeIfAbsent(new Point(x, y), p -> {
                Y2019D09.ProgramState programState = new Y2019D09.ProgramState(program);
                Y2019D09.EvalResult evalResult = Y2019D09.evalPartial(
                        programState,
                        new LinkedList<>(ImmutableList.of(BigInteger.valueOf(x), BigInteger.valueOf(y))));

                return ((Y2019D09.Output) evalResult).getOutputVal().equals(BigInteger.ONE);
            });
        }
    }

    static BigInteger[] input = parse("109,424,203,1,21101,11,0,0,1105,1,282,21102,18,1,0,1105,1,259,2102,1,1,221,203,1,21102,1,31,0,1106,0,282,21101,38,0,0,1105,1,259,21001,23,0,2,21201,1,0,3,21101,0,1,1,21101,0,57,0,1105,1,303,1201,1,0,222,20102,1,221,3,20101,0,221,2,21101,259,0,1,21102,80,1,0,1106,0,225,21101,127,0,2,21102,91,1,0,1106,0,303,1201,1,0,223,20102,1,222,4,21101,259,0,3,21101,0,225,2,21102,225,1,1,21102,1,118,0,1106,0,225,21001,222,0,3,21101,0,89,2,21101,133,0,0,1105,1,303,21202,1,-1,1,22001,223,1,1,21101,0,148,0,1105,1,259,2102,1,1,223,21002,221,1,4,21001,222,0,3,21101,0,21,2,1001,132,-2,224,1002,224,2,224,1001,224,3,224,1002,132,-1,132,1,224,132,224,21001,224,1,1,21102,195,1,0,106,0,108,20207,1,223,2,20102,1,23,1,21102,1,-1,3,21101,0,214,0,1105,1,303,22101,1,1,1,204,1,99,0,0,0,0,109,5,1201,-4,0,249,22102,1,-3,1,21201,-2,0,2,22101,0,-1,3,21102,250,1,0,1105,1,225,21202,1,1,-4,109,-5,2105,1,0,109,3,22107,0,-2,-1,21202,-1,2,-1,21201,-1,-1,-1,22202,-1,-2,-2,109,-3,2106,0,0,109,3,21207,-2,0,-1,1206,-1,294,104,0,99,22101,0,-2,-2,109,-3,2106,0,0,109,5,22207,-3,-4,-1,1206,-1,346,22201,-4,-3,-4,21202,-3,-1,-1,22201,-4,-1,2,21202,2,-1,-1,22201,-4,-1,1,21201,-2,0,3,21101,0,343,0,1106,0,303,1105,1,415,22207,-2,-3,-1,1206,-1,387,22201,-3,-2,-3,21202,-2,-1,-1,22201,-3,-1,3,21202,3,-1,-1,22201,-3,-1,2,22101,0,-4,1,21101,384,0,0,1106,0,303,1105,1,415,21202,-4,-1,-4,22201,-4,-3,-4,22202,-3,-2,-2,22202,-2,-4,-4,22202,-3,-2,-3,21202,-4,-1,-2,22201,-3,-2,1,21201,1,0,-4,109,-5,2105,1,0");
}

package y2019;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;

import java.math.BigInteger;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkState;
import static java.lang.System.out;
import static y2019.Y2019D09.parse;

public class Y2019D19 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        // out.println(runAndPrintPicture(input, 0, 50, 0, 50));

        // 2
        out.println(findFirstRowOfWidth(input, 100));
//        out.println(runAndPrintPicture(input));
//        out.println(findPath(runAndPrintPicture(input)));
        // assertThat(expandFunc());
//        out.println(timeToFillWithOxygen(input));

        out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static int findFirstRowOfWidth(BigInteger[] input, int targetWidth) {
        // x,y is start of sample area
        int x = 430;
        Integer startOfSufficientWidthRun = null;

        for (int y = 600; ; y++) {
            checkState(!isPulled(input, x, y));
            int xOffsetOfFirstPulledPoint = 1;
            while (!isPulled(input, x + xOffsetOfFirstPulledPoint, y)) {
                xOffsetOfFirstPulledPoint++;
            }
            int xOffsetOfNonPulledPoint = xOffsetOfFirstPulledPoint+1;
            while (isPulled(input, x + xOffsetOfNonPulledPoint, y)) {
                xOffsetOfNonPulledPoint++;
            }
            int width = xOffsetOfNonPulledPoint - xOffsetOfFirstPulledPoint; // 627 should be 104
            if (width < targetWidth) {
                startOfSufficientWidthRun = null;
            } else if (startOfSufficientWidthRun == null) {
                startOfSufficientWidthRun = y;
            } else {
                if (y - startOfSufficientWidthRun > targetWidth) {
                    return -1 ; //"qq"; /// need to include offset...
                }
            }

        }

//        double approxBeamAngleRatio = 38.0 / 50;
//        double approxWidthRation = 8.0 / 50;
//
//        double guessedY = 50 / 8.0 * 100;
//
//        out.println(runAndPrintPicture(input, (int) (approxBeamAngleRatio * guessedY), 200, (int) guessedY, 200));
//        return -1;
    }

    private static String runAndPrintPicture(BigInteger[] program, int startX, int width, int startY, int height) {
        StringBuilder acc = new StringBuilder();
        new Formatter(acc).format("(%s,%s) -> (%s,%s)\n", startX, startY, startX + width, startY + height);
        int pulledCount = 0;

        for (int y = startY; y < startY + height; y++) {
            for (int x = startX; x < startX + width; x++) {
                boolean pulled = isPulled(program, x, y);
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

    private static boolean isPulled(BigInteger[] program, int x, int y) {
        Y2019D09.ProgramState programState = new Y2019D09.ProgramState(program);
        Y2019D09.EvalResult evalResult = Y2019D09.evalPartial(
                programState,
                new LinkedList<>(ImmutableList.of(BigInteger.valueOf(x), BigInteger.valueOf(y))));

        return ((Y2019D09.Output) evalResult).getOutputVal().equals(BigInteger.ONE);
    }

    static BigInteger[] input = parse("109,424,203,1,21101,11,0,0,1105,1,282,21102,18,1,0,1105,1,259,2102,1,1,221,203,1,21102,1,31,0,1106,0,282,21101,38,0,0,1105,1,259,21001,23,0,2,21201,1,0,3,21101,0,1,1,21101,0,57,0,1105,1,303,1201,1,0,222,20102,1,221,3,20101,0,221,2,21101,259,0,1,21102,80,1,0,1106,0,225,21101,127,0,2,21102,91,1,0,1106,0,303,1201,1,0,223,20102,1,222,4,21101,259,0,3,21101,0,225,2,21102,225,1,1,21102,1,118,0,1106,0,225,21001,222,0,3,21101,0,89,2,21101,133,0,0,1105,1,303,21202,1,-1,1,22001,223,1,1,21101,0,148,0,1105,1,259,2102,1,1,223,21002,221,1,4,21001,222,0,3,21101,0,21,2,1001,132,-2,224,1002,224,2,224,1001,224,3,224,1002,132,-1,132,1,224,132,224,21001,224,1,1,21102,195,1,0,106,0,108,20207,1,223,2,20102,1,23,1,21102,1,-1,3,21101,0,214,0,1105,1,303,22101,1,1,1,204,1,99,0,0,0,0,109,5,1201,-4,0,249,22102,1,-3,1,21201,-2,0,2,22101,0,-1,3,21102,250,1,0,1105,1,225,21202,1,1,-4,109,-5,2105,1,0,109,3,22107,0,-2,-1,21202,-1,2,-1,21201,-1,-1,-1,22202,-1,-2,-2,109,-3,2106,0,0,109,3,21207,-2,0,-1,1206,-1,294,104,0,99,22101,0,-2,-2,109,-3,2106,0,0,109,5,22207,-3,-4,-1,1206,-1,346,22201,-4,-3,-4,21202,-3,-1,-1,22201,-4,-1,2,21202,2,-1,-1,22201,-4,-1,1,21201,-2,0,3,21101,0,343,0,1106,0,303,1105,1,415,22207,-2,-3,-1,1206,-1,387,22201,-3,-2,-3,21202,-2,-1,-1,22201,-3,-1,3,21202,3,-1,-1,22201,-3,-1,2,22101,0,-4,1,21101,384,0,0,1106,0,303,1105,1,415,21202,-4,-1,-4,22201,-4,-3,-4,22202,-3,-2,-2,22202,-2,-4,-4,22202,-3,-2,-3,21202,-4,-1,-2,22201,-3,-2,1,21201,1,0,-4,109,-5,2105,1,0");
}

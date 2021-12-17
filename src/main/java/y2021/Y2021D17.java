package y2021;

import com.google.common.base.Stopwatch;
import lombok.Value;

import java.util.concurrent.TimeUnit;

public class Y2021D17 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            System.out.println("## Example");
            search(example);
            // maxHeightThatReachesTarget=45
            // validInitVelCount=112
            System.out.println("## Input");
            search(input);

        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private static void search(TargetSpec input) {
        int maxHeightThatReachesTarget = 0;
        int validInitVelCount = 0;

        // Need an upper bound for y velocity
        // By symmetry, the probe always passes 0 on the way back down if you fire up
        // So if initial y vel is greater than -minY, it will shoot past the target in less than
        // one step on the way down
        int yVelUpperBound = 1 + -input.minY;

        for (int initialXVel = 1; initialXVel <= input.maxX; initialXVel++) {
            initialVelSearch:
            for (int initialYVel = input.minY; initialYVel < yVelUpperBound; initialYVel++) {
                int x = 0;
                int xVel = initialXVel;
                int y = 0;
                int yVel = initialYVel;
                int maxHeightThisTrial = 0;

                for (int step = 1; ; step++) {
                    x += xVel;
                    xVel = Math.max(0, xVel - 1);
                    y += yVel;
                    yVel--;
                    maxHeightThisTrial = Math.max(y, maxHeightThisTrial);

                    if (x > input.maxX || y < input.minY) {
                        // Fell off bottom or past target x
                        continue initialVelSearch;
                    } else if (x >= input.minX && y <= input.maxY) {
                        // in target zone
                        validInitVelCount++;
                        maxHeightThatReachesTarget = Math.max(maxHeightThisTrial, maxHeightThatReachesTarget);
                        continue initialVelSearch;
                    }
                    // else continue step
                }
            }
        }

        System.out.println("maxHeightThatReachesTarget=" + maxHeightThatReachesTarget);
        System.out.println("validInitVelCount=" + validInitVelCount);
    }

    @Value
    static class TargetSpec {
        int minX;
        int maxX;
        int minY;
        int maxY;
    }

    private static TargetSpec example = new TargetSpec(20, 30, -10, -5);
    private static TargetSpec input = new TargetSpec(269, 292, -68, -44);
}

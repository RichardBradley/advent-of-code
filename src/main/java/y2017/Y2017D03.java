package y2017;

import com.google.common.base.Stopwatch;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

public class Y2017D03 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        assertThat(distToNInSpiral(1)).isEqualTo(0);
        assertThat(distToNInSpiral(12)).isEqualTo(3);
        assertThat(distToNInSpiral(23)).isEqualTo(2);
        assertThat(distToNInSpiral(1024)).isEqualTo(31);
        assertThat(distToNInSpiral(312051)).isEqualTo(430);

        assertThat(part2(141)).isEqualTo(142);
        assertThat(part2(312051)).isEqualTo(312453);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static int distToNInSpiral(int n) {
        int x = 0;
        int y = 0;
        int nextDir = 1; // NESW
        int sideLen = 1;

        n--; // 0-based

        while (n > sideLen) {
            switch (nextDir) {
                case 0:
                    y -= sideLen;
                    break;
                case 1:
                    x += sideLen;
                    break;
                case 2:
                    y += sideLen;
                    break;
                case 3:
                    x -= sideLen;
                    break;
            }
            nextDir = (nextDir + 3) % 4;
            n -= sideLen;
            if (nextDir == 3 || nextDir == 1) {
                sideLen++;
            }
        }

        switch (nextDir) {
            case 0:
                y -= n;
                break;
            case 1:
                x += n;
                break;
            case 2:
                y += n;
                break;
            case 3:
                x -= n;
                break;
        }

        return Math.abs(x) + Math.abs(y);
    }

    private static int part2(int target) {
        Map<Point, Integer> storedValues = new HashMap();
        int x = 0;
        int y = 0;
        int nextDir = 1; // NESW
        int sideLen = 1;

        storedValues.put(new Point(0, 0), 1);

        while (true) {
            for (int i = 0; i < sideLen; i++) {
                switch (nextDir) {
                    case 0:
                        y--;
                        break;
                    case 1:
                        x++;
                        break;
                    case 2:
                        y++;
                        break;
                    case 3:
                        x--;
                        break;
                }
                int sumAdjacent = 0;
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        if (dx != 0 || dy != 0) {
                            sumAdjacent += storedValues.getOrDefault(new Point(x + dx, y + dy), 0);
                        }
                    }
                }
                if (sumAdjacent > target) {
                    return sumAdjacent;
                }
                storedValues.put(new Point(x, y), sumAdjacent);
            }
            nextDir = (nextDir + 3) % 4;
            if (nextDir == 3 || nextDir == 1) {
                sideLen++;
            }
        }
    }
}

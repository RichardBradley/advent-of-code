package y2016;

import com.google.common.base.Stopwatch;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

public class Y2016D01 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(getDistAfter("R2, L3")).isEqualTo(5);
        assertThat(getDistAfter("R2, R2, R2")).isEqualTo(2);
        assertThat(getDistAfter("R5, L5, R5, R3")).isEqualTo(12);

        System.out.println(getDistAfter(input));

        // 2
        assertThat(getDistAtFirstDuplicate("R8, R4, R4, R8")).isEqualTo(4);

        System.out.println(getDistAtFirstDuplicate(input));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");

    }

    private static int getDistAfter(String instructions) {
        int currentDir = 0; // NESW
        int currentX = 0;
        int currentY = 0;

        for (String instr : instructions.split(",\\s")) {
            switch (instr.charAt(0)) {
                case 'R':
                    currentDir++;
                    if (currentDir > 3) {
                        currentDir = 0;
                    }
                    break;
                case 'L':
                    currentDir--;
                    if (currentDir < 0) {
                        currentDir = 3;
                    }
                    break;
                default:
                    throw new IllegalArgumentException("instr: " + instr);
            }

            int len = Integer.parseInt(instr.substring(1));
            switch (currentDir) {
                case 0:
                    currentY += len;
                    break;
                case 1:
                    currentX += len;
                    break;
                case 2:
                    currentY -= len;
                    break;
                case 3:
                    currentX -= len;
                    break;
                default:
                    throw new IllegalArgumentException("instr: " + instr);
            }
        }

        return Math.abs(currentX) + Math.abs(currentY);
    }

    private static int getDistAtFirstDuplicate(String instructions) {
        int currentDir = 0; // NESW
        int currentX = 0;
        int currentY = 0;
        Set<Point> visited = new HashSet<>();

        for (String instr : instructions.split(",\\s")) {
            switch (instr.charAt(0)) {
                case 'R':
                    currentDir++;
                    if (currentDir > 3) {
                        currentDir = 0;
                    }
                    break;
                case 'L':
                    currentDir--;
                    if (currentDir < 0) {
                        currentDir = 3;
                    }
                    break;
                default:
                    throw new IllegalArgumentException("instr: " + instr);
            }

            int len = Integer.parseInt(instr.substring(1));
            for (int i = 0; i < len; i++) {
                switch (currentDir) {
                    case 0:
                        currentY++;
                        break;
                    case 1:
                        currentX++;
                        break;
                    case 2:
                        currentY--;
                        break;
                    case 3:
                        currentX--;
                        break;
                    default:
                        throw new IllegalArgumentException("instr: " + instr);
                }

                if (!visited.add(new Point(currentX, currentY))) {
                    return Math.abs(currentX) + Math.abs(currentY);
                }
            }
        }

        throw new IllegalStateException("No duplicates visited");
    }

    static String input = "R1, L3, R5, R5, R5, L4, R5, R1, R2, L1, L1, R5, R1, L3, L5, L2, R4, L1, R4, R5, L3, R5, L1, R3, L5, R1, L2, R1, L5, L1, R1, R4, R1, L1, L3, R3, R5, L3, R4, L4, R5, L5, L1, L2, R4, R3, R3, L185, R3, R4, L5, L4, R48, R1, R2, L1, R1, L4, L4, R77, R5, L2, R192, R2, R5, L4, L5, L3, R2, L4, R1, L5, R5, R4, R1, R2, L3, R4, R4, L2, L4, L3, R5, R4, L2, L1, L3, R1, R5, R5, R2, L5, L2, L3, L4, R2, R1, L4, L1, R1, R5, R3, R3, R4, L1, L4, R1, L2, R3, L3, L2, L1, L2, L2, L1, L2, R3, R1, L4, R1, L1, L4, R1, L2, L5, R3, L5, L2, L2, L3, R1, L4, R1, R1, R2, L1, L4, L4, R2, R2, R2, R2, R5, R1, L1, L4, L5, R2, R4, L3, L5, R2, R3, L4, L1, R2, R3, R5, L2, L3, R3, R1, R3";
}

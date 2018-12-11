package y2018;

import java.awt.*;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2018D11 {
    public static void main(String[] args) throws Exception {

        // 1
        // power level of the fuel cell at 3,5 in a grid with serial number 8:
        assertThat(getPowerLevel(3,5, 8)).isEqualTo(4);
        assertThat(getPowerLevel(122,79, 57)).isEqualTo(-5);
        assertThat(getPowerLevel(217,196, 39)).isEqualTo(0);
        assertThat(getPowerLevel(101,153, 71)).isEqualTo(4);

        // from grid examples
        assertThat(getPowerLevel(33,45, 18)).isEqualTo(4);
        assertThat(getPowerLevel(34,45, 18)).isEqualTo(4);
        assertThat(getPowerLevel(35,45, 18)).isEqualTo(4);
        assertThat(getPowerLevel(33,46, 18)).isEqualTo(3);
        assertThat(getPowerLevel(34,46, 18)).isEqualTo(3);
        assertThat(getPowerLevel(35,46, 18)).isEqualTo(4);


        assertThat(getBestPowerLevelCoord3x3(18)).isEqualTo(new Point(33,45));
        assertThat(getBestPowerLevelCoord3x3(42)).isEqualTo(new Point(21,61));

        System.out.println(getBestPowerLevelCoord3x3(4151));

        // 2
        // (commented out as they are rather slow)
//        assertThat(getBestPowerLevelCoord(18)).iásEqualTo("90,269,16");
//        assertThat(getBestPowerLevelCoord(42)).isEqualTo("232,251,12");


        System.out.println(getBestPowerLevelCoord(4151));
    }

    private static Point getBestPowerLevelCoord3x3(int serialNo) {
        int size = 300;
        int[][] grid = new int[size][];
        for (int x = 0; x < size; x++) {
            grid[x] = new int[size];
            for (int y = 0; y < size; y++) {
                grid[x][y] = getPowerLevel(x + 1, y+ 1, serialNo);
            }
        }
        int maxPower = Integer.MIN_VALUE;
        Point maxPowerCoord = null;
        for (int x = 0; x < size -2; x++) {
            for (int y = 0; y < size -2; y++) {
                int sum = 0;
                for (int dx = 0; dx <= 2; dx++) {
                    for (int dy = 0; dy <= 2 ; dy++) {
                        sum += grid[x + dx][y + dy];
                    }
                }
                if (sum > maxPower) {
                    maxPower = sum;
                    maxPowerCoord = new Point(x + 1, y + 1);
                }
            }
        }

        return maxPowerCoord;
    }

    private static String getBestPowerLevelCoord(int serialNo) {
        int size = 300;
        int[][] grid = new int[size][];
        for (int x = 0; x < size; x++) {
            grid[x] = new int[size];
            for (int y = 0; y < size; y++) {
                grid[x][y] = getPowerLevel(x + 1, y+ 1, serialNo);
            }
        }
        int maxPower = Integer.MIN_VALUE;
        String maxPowerCoord = null;
        for (int squareSize = 3; squareSize <= 300; squareSize ++) {
            System.out.println("qq squareSize=" + squareSize);
            for (int x = 0; x < size - squareSize - 1; x++) {
                for (int y = 0; y < size - squareSize - 1; y++) {
                    int sum = 0;
                    for (int dx = 0; dx < squareSize; dx++) {
                        for (int dy = 0; dy < squareSize; dy++) {
                            sum += grid[x + dx][y + dy];
                        }
                    }
                    if (sum > maxPower) {
                        maxPower = sum;
                        maxPowerCoord = (x + 1) + "," + (y + 1) + "," + squareSize;
                    }
                }
            }
        }

        return maxPowerCoord;
    }

    private static int getPowerLevel(int x, int y, int serialNo) {
        // Find the fuel cell's rack ID, which is its X coordinate plus 10.
        int rackId = x + 10;
        //Begin with a power level of the rack ID times the Y coordinate.
        int power = rackId * y;
        //Increase the power level by the value of the grid serial number (your puzzle input).
        power += serialNo;
        //Set the power level to itself multiplied by the rack ID.
        power *= rackId;
        //Keep only the hundreds digit of the power level (so 12345 becomes 3; numbers with no hundreds digit become 0).
        power = (power / 100) % 10;
        //Subtract 5 from the power level.
        return power - 5;
    }

}

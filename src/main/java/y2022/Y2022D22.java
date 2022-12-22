package y2022;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2022D22 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        List<String> input = Resources.readLines(Resources.getResource("y2022/Y2022D22.txt"), StandardCharsets.UTF_8);

        // 1
        assertThat(part1(example)).isEqualTo(6032);
        System.out.println(part1(input));

        // 2
//        assertThat(part2(example)).isEqualTo(-1);
        assertThat(part2(input)).isLessThan(178022);
        System.out.println(part2(input));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    static Point[] dirs = new Point[]{ // NESW
            new Point(0, -1),
            new Point(1, 0),
            new Point(0, 1),
            new Point(-1, 0)
    };

    private static int part1(List<String> input) {
        List<String> map = input.subList(0, input.size() - 2);
        int height = map.size();

        String instructions = input.get(input.size() - 1);
        // You begin the path in the leftmost open tile of the top row of tiles. Initially, you are facing to the right
        int x = map.get(0).indexOf('.');
        int y = 0;
        int facing = 1;

        int instrIdx = 0;
        while (instrIdx < instructions.length()) {
            int dist = 0;
            while (instrIdx < instructions.length() && Character.isDigit(instructions.charAt(instrIdx))) {
                dist *= 10;
                dist += (instructions.charAt(instrIdx++) - '0');
            }
            checkState(dist > 0);

            // move forward 'dist'
            for (int i = 0; i < dist; i++) {
                int xx, yy;
                switch (facing) {
                    case 0: // N
                        xx = x;
                        yy = y - 1;
                        if (yy < 0 || ' ' == map.get(yy).charAt(xx)) {
                            // resume at bottom of col
                            for (int yyy = height - 1; yyy >= 0; yyy--) {
                                if (xx < map.get(yyy).length() && ' ' != map.get(yyy).charAt(xx)) {
                                    yy = yyy;
                                    break;
                                }
                            }
                        }
                        break;
                    case 2: // S
                        xx = x;
                        yy = y + 1;
                        if (yy >= height || xx >= map.get(yy).length() || ' ' == map.get(yy).charAt(xx)) {
                            // resume at top of col
                            for (int yyy = 0; yyy < height; yyy++) {
                                if (xx < map.get(yyy).length() && ' ' != map.get(yyy).charAt(x)) {
                                    yy = yyy;
                                    break;
                                }
                            }
                        }
                        break;
                    case 1: {// E
                        xx = x + 1;
                        yy = y;
                        int width = map.get(yy).length();
                        if (xx >= width || ' ' == map.get(yy).charAt(xx)) {
                            // resume at left of col
                            for (int xxx = 0; xxx < width; xxx++) {
                                if (' ' != map.get(yy).charAt(xxx)) {
                                    xx = xxx;
                                    break;
                                }
                            }
                        }
                    }
                    break;
                    case 3: { // W
                        xx = x - 1;
                        yy = y;
                        int width = map.get(yy).length();
                        if (xx < 0 || ' ' == map.get(yy).charAt(xx)) {
                            // resume at bottom of col
                            for (int xxx = width - 1; xxx >= 0; xxx--) {
                                if (' ' != map.get(yy).charAt(xxx)) {
                                    xx = xxx;
                                    break;
                                }
                            }
                        }
                    }
                    break;
                    default:
                        throw new RuntimeException();
                }

                if ('#' == map.get(yy).charAt(xx)) {
                    // If you run into a wall, you stop moving forward and continue with the next instruction.
                    break;
                } else {
                    assertThat(map.get(yy).charAt(xx)).isEqualTo('.');
                    x = xx;
                    y = yy;
                }
            }

            // rotate
            if (instrIdx < instructions.length()) {
                char rot = instructions.charAt(instrIdx++);
                switch (rot) {
                    case 'R':
                        facing = (facing + 1) % 4;
                        break;
                    case 'L':
                        facing = (facing + 3) % 4;
                        break;
                    default:
                        throw new IllegalArgumentException("for: " + rot);
                }
            }
        }

        // determine numbers for your final row, column, and facing as your
        // final position appears from the perspective of the original map.
        // Rows start from 1 at the top and count downward; columns start
        // from 1 at the left and count rightward. (In the above example,
        // row 1, column 1 refers to the empty space with no tile on it
        // in the top-left corner.) Facing is 0 for right (>), 1 for down
        // (v), 2 for left (<), and 3 for up (^). The final password is the
        // sum of 1000 times the row, 4 times the column, and the facing.
        return 1000 * (y + 1) + 4 * (x + 1) + ((facing + 3) % 4);
    }

    static class Loc {
        int x;
        int y;
        int facing;
    }

    interface Facings {
        final int N = 0;
        final int E = 1;
        int S = 2;
        int W = 3;
    }

    private static int part2(List<String> input) {
        List<String> map = input.subList(0, input.size() - 2);

        String instructions = input.get(input.size() - 1);
        // You begin the path in the leftmost open tile of the top
        // row of tiles. Initially, you are facing to the right
        int x = map.get(0).indexOf('.');
        int y = 0;
        int facing = Facings.E;

        int instrIdx = 0;
        while (instrIdx < instructions.length()) {
            int dist = 0;
            while (instrIdx < instructions.length() && Character.isDigit(instructions.charAt(instrIdx))) {
                dist *= 10;
                dist += (instructions.charAt(instrIdx++) - '0');
            }
            checkState(dist > 0);

            // move forward 'dist'
            for (int i = 0; i < dist; i++) {
                int xx, yy;
                int prevFacing = facing;

                switch (facing) {
                    case Facings.N:
                        xx = x;
                        yy = y - 1;

                        if (yy == -1 && xx >= 50 && xx < 100) {
                            // edge 'y' on my diag
                            facing = Facings.E;
                            yy = 150 + (xx - 50);
                            xx = 0;
                        } else if (yy == -1 && xx >= 100 && xx < 150) {
                            // edge 'z' on my diag
                            xx = xx - 100;
                            yy = 199;
                        } else if (yy == 99 && xx < 50) {
                            // edge 'a'
                            facing = Facings.E;
                            yy = 50 + xx;
                            xx = 50;
                        }
                        break;
                    case 2: // S
                        xx = x;
                        yy = y + 1;

                        if (yy == 50 && xx >= 100) {
                            // edge 'e'
                            facing = Facings.W;
                            yy = 50 + (xx - 100);
                            xx = 99;
                        } else if (yy == 150 && xx >= 50 && xx < 100) {
                            // edge 'g'
                            facing = Facings.W;
                            yy = 150 + (xx - 50);
                            xx = 49;
                        } else if (yy == 200 && xx < 50) {
                            // edge 'z'
                            xx = xx + 100;
                            yy = 0;
                        }

                        break;
                    case 1: {// E
                        xx = x + 1;
                        yy = y;


                        if (xx == 150) {
                            // edge 'F'
                            facing = Facings.W;
                            yy = 149 - (yy);
                            xx = 99;
                        } else if (xx == 100 && yy >= 50 && yy < 100) {
                            // edge 'e'
                            facing = Facings.N;
                            xx = 100 + (yy - 50);
                            yy = 49;
                        } else if (xx == 100 && yy >= 100 && yy < 150) {
                            // edge 'F'
                            facing = Facings.W;
                            yy = 49 - (yy - 100);
                            xx = 149;
                        } else if (xx == 50 && yy >= 150) {
                            // edge 'g'
                            facing = Facings.N;
                            xx = 50 + yy - 150;
                            yy = 149;
                        }
                    }
                    break;
                    case 3: { // W
                        xx = x - 1;
                        yy = y;

                        if (xx == 49 && yy < 50) {
                            // edge 'b'
                            facing = Facings.E;
                            yy = 149 - (yy);
                            xx = 0;
                        } else if (xx == 49 && yy >= 50 && yy < 100) {
                            // edge 'a'
                            facing = Facings.S;
                            xx = yy - 50;
                            yy = 100;
                        } else if (xx == -1 && yy >= 100 && yy < 150) {
                            // edge 'b'
                            facing = Facings.E;
                            yy = 49 - (yy - 100);
                            xx = 50;
                        } else if (xx == -1 && yy >= 150 && yy < 200) {
                            // edge 'y'
                            facing = Facings.S;
                            xx = 50 + yy - 150;
                            yy = 0;
                        }
                    }
                    break;
                    default:
                        throw new RuntimeException();
                }

                if ('#' == map.get(yy).charAt(xx)) {
                    // If you run into a wall, you stop moving forward and continue with the next instruction.
                    facing = prevFacing;
                    break;
                } else {
                    assertThat(map.get(yy).charAt(xx)).isEqualTo('.');
                    x = xx;
                    y = yy;
                }
            }

            // rotate
            if (instrIdx < instructions.length()) {
                char rot = instructions.charAt(instrIdx++);
                switch (rot) {
                    case 'R':
                        facing = (facing + 1) % 4;
                        break;
                    case 'L':
                        facing = (facing + 3) % 4;
                        break;
                    default:
                        throw new IllegalArgumentException("for: " + rot);
                }
            }
        }

        // determine numbers for your final row, column, and facing as your
        // final position appears from the perspective of the original map.
        // Rows start from 1 at the top and count downward; columns start
        // from 1 at the left and count rightward. (In the above example,
        // row 1, column 1 refers to the empty space with no tile on it
        // in the top-left corner.) Facing is 0 for right (>), 1 for down
        // (v), 2 for left (<), and 3 for up (^). The final password is the
        // sum of 1000 times the row, 4 times the column, and the facing.
        return 1000 * (y + 1) + 4 * (x + 1) + ((facing + 3) % 4);
    }


    private static List<String> example = List.of(
            "        ...#",
            "        .#..",
            "        #...",
            "        ....",
            "...#.......#",
            "........#...",
            "..#....#....",
            "..........#.",
            "        ...#....",
            "        .....#..",
            "        .#......",
            "        ......#.",
            "",
            "10R5L5R10L4R5L5");
}

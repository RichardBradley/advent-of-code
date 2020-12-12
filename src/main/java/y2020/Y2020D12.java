package y2020;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkState;

public class Y2020D12 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        List<String> input = Resources.readLines(Resources.getResource("y2020/Y2020D12.txt"), StandardCharsets.UTF_8);

        part1(input);
        part2(example1);
        part2(input);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static void part1(List<String> input) {

        int x = 0;
        int y = 0;
        int facing = 1; // N:0, E:1, S:2, W:3

        for (String line : input) {
            char instr = line.charAt(0);
            int arg = Integer.parseInt(line.substring(1));
            switch (instr) {
                case 'N':
                    y -= arg;
                    break;
                case 'S':
                    y += arg;
                    break;
                case 'E':
                    x += arg;
                    break;
                case 'W':
                    x -= arg;
                    break;
                case 'L':
                    checkState(arg % 90 == 0);
                    facing = (facing - (arg / 90) + 4) % 4;
                    break;
                case 'R':
                    checkState(arg % 90 == 0);
                    facing = (facing + (arg / 90)) % 4;
                    break;
                case 'F':
                    switch (facing) {
                        case 0:
                            y -= arg;
                            break;
                        case 2:
                            y += arg;
                            break;
                        case 3:
                            x -= arg;
                            break;
                        case 1:
                            x += arg;
                            break;
                        default:
                            throw new IllegalArgumentException();
                    }
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }

        System.out.println("dist = " + (Math.abs(x) + Math.abs(y)));
    }

    private static void part2(List<String> input) {

        int x = 0;
        int y = 0;

        int waypointDX = 10;
        int waypointDY = -1;

        for (String line : input) {
            char instr = line.charAt(0);
            int arg = Integer.parseInt(line.substring(1));
            switch (instr) {
                case 'N':
                    waypointDY -= arg;
                    break;
                case 'S':
                    waypointDY += arg;
                    break;
                case 'E':
                    waypointDX += arg;
                    break;
                case 'W':
                    waypointDX -= arg;
                    break;
                case 'L': {
                    checkState(arg % 90 == 0);
                    AffineTransform rot = AffineTransform.getRotateInstance(-(arg / 180.0 * Math.PI));
                    Point2D after = rot.transform(new Point(waypointDX, waypointDY), null);
                    waypointDX = (int) after.getX();
                    waypointDY = (int) after.getY();
                    break;
                }
                case 'R': {
                    checkState(arg % 90 == 0);
                    AffineTransform rot = AffineTransform.getRotateInstance(+(arg / 180.0 * Math.PI));
                    Point2D after = rot.transform(new Point(waypointDX, waypointDY), null);
                    waypointDX = (int) after.getX();
                    waypointDY = (int) after.getY();
                    break;
                }
                case 'F':
                    x += arg * waypointDX;
                    y += arg * waypointDY;
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }

        System.out.println("dist = " + (Math.abs(x) + Math.abs(y)));
    }

    static List<String> example1 = Splitter.on("\n").splitToList("F10\n" +
            "N3\n" +
            "F7\n" +
            "R90\n" +
            "F11");
}

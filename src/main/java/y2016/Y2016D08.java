package y2016;

import com.google.common.base.Stopwatch;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Y2016D08 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        System.out.println(screen(
                7,
                3,
                "rect 3x2",
                "rotate column x=1 by 1",
                "rotate row y=0 by 4",
                "rotate column x=1 by 1"));

        System.out.println();
        System.out.println();

        String screen = screen(50, 6, input);
        System.out.println(screen);
        System.out.println(countLitPixels(screen));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static long countLitPixels(String screen) {
        return screen.chars().filter(x -> x == '#').count();
    }

    private static String screen(int width, int height, String... instructions) {
        StringBuilder[] screen = new StringBuilder[height];
        for (int i = 0; i < height; i++) {
            screen[i] = new StringBuilder();
            for (int j = 0; j < width; j++) {
                screen[i].append('.');
            }
        }

        Pattern rectInstr = Pattern.compile("rect (\\d+)x(\\d+)");
        Pattern rowInstr = Pattern.compile("rotate row y=(\\d+) by (\\d+)");
        Pattern colInstr = Pattern.compile("rotate column x=(\\d+) by (\\d+)");

        for (String instruction : instructions) {
            {
                Matcher rectM = rectInstr.matcher(instruction);
                if (rectM.matches()) {
                    int w = Integer.parseInt(rectM.group(1));
                    int h = Integer.parseInt(rectM.group(2));
                    for (int y = 0; y < h; y++) {
                        for (int x = 0; x < w; x++) {
                            screen[y].setCharAt(x, '#');
                        }
                    }
                    continue;
                }
            }
            {
                Matcher rowM = rowInstr.matcher(instruction);
                if (rowM.matches()) {
                    int y = Integer.parseInt(rowM.group(1));
                    int dx = Integer.parseInt(rowM.group(2));

                    StringBuilder row = screen[y];
                    StringBuilder oldRow = new StringBuilder(row);
                    for (int x = 0; x < width; x++) {
                        row.setCharAt(x, oldRow.charAt((x - dx + width) % width));
                    }
                    continue;
                }
            }
            {
                Matcher colM = colInstr.matcher(instruction);
                if (colM.matches()) {
                    int x = Integer.parseInt(colM.group(1));
                    int dy = Integer.parseInt(colM.group(2));

                    StringBuilder oldCol = new StringBuilder();
                    for (int y = 0; y < height; y++) {
                        oldCol.append(screen[y].charAt(x));
                    }

                    for (int y = 0; y < height; y++) {
                        screen[y].setCharAt(x, oldCol.charAt((y - dy + height) % height));
                    }
                    continue;
                }
            }
            throw new IllegalArgumentException(instruction);
        }

        return Arrays.stream(screen).
                map(x -> x.toString()).
                collect(Collectors.joining("\n"));
    }

    static String[] input = new String[]{
            "rect 1x1",
            "rotate row y=0 by 5",
            "rect 1x1",
            "rotate row y=0 by 6",
            "rect 1x1",
            "rotate row y=0 by 5",
            "rect 1x1",
            "rotate row y=0 by 2",
            "rect 1x1",
            "rotate row y=0 by 5",
            "rect 2x1",
            "rotate row y=0 by 2",
            "rect 1x1",
            "rotate row y=0 by 4",
            "rect 1x1",
            "rotate row y=0 by 3",
            "rect 2x1",
            "rotate row y=0 by 7",
            "rect 3x1",
            "rotate row y=0 by 3",
            "rect 1x1",
            "rotate row y=0 by 3",
            "rect 1x2",
            "rotate row y=1 by 13",
            "rotate column x=0 by 1",
            "rect 2x1",
            "rotate row y=0 by 5",
            "rotate column x=0 by 1",
            "rect 3x1",
            "rotate row y=0 by 18",
            "rotate column x=13 by 1",
            "rotate column x=7 by 2",
            "rotate column x=2 by 3",
            "rotate column x=0 by 1",
            "rect 17x1",
            "rotate row y=3 by 13",
            "rotate row y=1 by 37",
            "rotate row y=0 by 11",
            "rotate column x=7 by 1",
            "rotate column x=6 by 1",
            "rotate column x=4 by 1",
            "rotate column x=0 by 1",
            "rect 10x1",
            "rotate row y=2 by 37",
            "rotate column x=19 by 2",
            "rotate column x=9 by 2",
            "rotate row y=3 by 5",
            "rotate row y=2 by 1",
            "rotate row y=1 by 4",
            "rotate row y=0 by 4",
            "rect 1x4",
            "rotate column x=25 by 3",
            "rotate row y=3 by 5",
            "rotate row y=2 by 2",
            "rotate row y=1 by 1",
            "rotate row y=0 by 1",
            "rect 1x5",
            "rotate row y=2 by 10",
            "rotate column x=39 by 1",
            "rotate column x=35 by 1",
            "rotate column x=29 by 1",
            "rotate column x=19 by 1",
            "rotate column x=7 by 2",
            "rotate row y=4 by 22",
            "rotate row y=3 by 5",
            "rotate row y=1 by 21",
            "rotate row y=0 by 10",
            "rotate column x=2 by 2",
            "rotate column x=0 by 2",
            "rect 4x2",
            "rotate column x=46 by 2",
            "rotate column x=44 by 2",
            "rotate column x=42 by 1",
            "rotate column x=41 by 1",
            "rotate column x=40 by 2",
            "rotate column x=38 by 2",
            "rotate column x=37 by 3",
            "rotate column x=35 by 1",
            "rotate column x=33 by 2",
            "rotate column x=32 by 1",
            "rotate column x=31 by 2",
            "rotate column x=30 by 1",
            "rotate column x=28 by 1",
            "rotate column x=27 by 3",
            "rotate column x=26 by 1",
            "rotate column x=23 by 2",
            "rotate column x=22 by 1",
            "rotate column x=21 by 1",
            "rotate column x=20 by 1",
            "rotate column x=19 by 1",
            "rotate column x=18 by 2",
            "rotate column x=16 by 2",
            "rotate column x=15 by 1",
            "rotate column x=13 by 1",
            "rotate column x=12 by 1",
            "rotate column x=11 by 1",
            "rotate column x=10 by 1",
            "rotate column x=7 by 1",
            "rotate column x=6 by 1",
            "rotate column x=5 by 1",
            "rotate column x=3 by 2",
            "rotate column x=2 by 1",
            "rotate column x=1 by 1",
            "rotate column x=0 by 1",
            "rect 49x1",
            "rotate row y=2 by 34",
            "rotate column x=44 by 1",
            "rotate column x=40 by 2",
            "rotate column x=39 by 1",
            "rotate column x=35 by 4",
            "rotate column x=34 by 1",
            "rotate column x=30 by 4",
            "rotate column x=29 by 1",
            "rotate column x=24 by 1",
            "rotate column x=15 by 4",
            "rotate column x=14 by 1",
            "rotate column x=13 by 3",
            "rotate column x=10 by 4",
            "rotate column x=9 by 1",
            "rotate column x=5 by 4",
            "rotate column x=4 by 3",
            "rotate row y=5 by 20",
            "rotate row y=4 by 20",
            "rotate row y=3 by 48",
            "rotate row y=2 by 20",
            "rotate row y=1 by 41",
            "rotate column x=47 by 5",
            "rotate column x=46 by 5",
            "rotate column x=45 by 4",
            "rotate column x=43 by 5",
            "rotate column x=41 by 5",
            "rotate column x=33 by 1",
            "rotate column x=32 by 3",
            "rotate column x=23 by 5",
            "rotate column x=22 by 1",
            "rotate column x=21 by 2",
            "rotate column x=18 by 2",
            "rotate column x=17 by 3",
            "rotate column x=16 by 2",
            "rotate column x=13 by 5",
            "rotate column x=12 by 5",
            "rotate column x=11 by 5",
            "rotate column x=3 by 5",
            "rotate column x=2 by 5",
            "rotate column x=1 by 5",
    };
}

package y2021;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;

public class Y2021D02 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            List<String> input = Resources.readLines(Resources.getResource("y2021/Y2021D02.txt"), StandardCharsets.UTF_8);

            // 1
            assertThat(part1(asList(example))).isEqualTo(150);
            assertThat(part1(input)).isEqualTo(1804520);

            // 2
            assertThat(part2(asList(example))).isEqualTo(900);
            assertThat(part2(input)).isEqualTo(1971095320);

        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private static int part1(List<String> input) {
        int x = 0, y = 0;
        for (String s : input) {
            String[] parts = s.split(" ");
            checkArgument(parts.length == 2);
            int dist = Integer.parseInt(parts[1]);
            switch (parts[0]) {
                case "forward":
                    x += dist;
                    break;
                case "down":
                    y += dist;
                    break;
                case "up":
                    y -= dist;
                    break;
                default:
                    throw new IllegalArgumentException(parts[0]);
            }
        }
        return x * y;
    }

    private static int part2(List<String> input) {
        int x = 0, y = 0, aim = 0;
        for (String s : input) {
            String[] parts = s.split(" ");
            checkArgument(parts.length == 2);
            int dist = Integer.parseInt(parts[1]);
            switch (parts[0]) {
                case "forward":
                    x += dist;
                    y += aim * dist;
                    break;
                case "down":
                    aim += dist;
                    break;
                case "up":
                    aim -= dist;
                    break;
                default:
                    throw new IllegalArgumentException(parts[0]);
            }
        }
        return x * y;
    }

    private static String[] example = new String[]{
            "forward 5",
            "down 5",
            "forward 8",
            "up 3",
            "down 8",
            "forward 2"
    };
}

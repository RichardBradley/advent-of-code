package y2022;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Y2022D01 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        List<String> input = Resources.readLines(Resources.getResource("y2022/Y2022D01.txt"), StandardCharsets.UTF_8);

        // 1
        System.out.println(part1(input));

        // 2
        System.out.println(part2(input));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static int part1(List<String> input) {
        int maxElf = 0;
        int currElf = 0;
        for (String line : input) {
            if (line.equals("")) {
                maxElf = Math.max(maxElf, currElf);
                currElf = 0;
            } else {
                currElf += Integer.parseInt(line);
            }
        }
        maxElf = Math.max(maxElf, currElf);

        return maxElf;
    }

    private static int part2(List<String> input) {
        List<Integer> elves = new ArrayList<>();
        int currElf = 0;
        for (String line : input) {
            if (line.equals("")) {
                elves.add(currElf);
                currElf = 0;
            } else {
                currElf += Integer.parseInt(line);
            }
        }
        elves.add(currElf);

        return elves.stream()
                .sorted(Comparator.<Integer>naturalOrder().reversed())
                .limit(3)
                .mapToInt(i -> i)
                .sum();
    }
}

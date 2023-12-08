package y2023;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Iterables;
import com.google.common.io.Resources;
import org.apache.commons.math3.util.Pair;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;
import static org.apache.commons.math3.util.ArithmeticUtils.lcm;

public class Y2023D08 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            List<String> input = Resources.readLines(Resources.getResource("y2023/Y2023D08.txt"), StandardCharsets.UTF_8);

            // 1
            assertThat(part1(input)).isEqualTo(15517);

            // 2
            assertThat(part2(input)).isEqualTo(14935034899483L);

        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private static long part1(List<String> input) {
        String instructions = input.get(0);

        Pattern p = Pattern.compile("(\\w{3}) = \\((\\w{3}), (\\w{3})\\)");
        Map<String, Pair<String, String>> network = new HashMap<>();
        for (int i = 2; i < input.size(); i++) {
            Matcher m = p.matcher(input.get(i));
            checkState(m.matches());
            checkState(null == network.put(m.group(1), Pair.create(m.group(2), m.group(3))));
        }
        ;

        String currLoc = "AAA";
        int instructionIdx = 0;
        int step = 1;
        for (; ; step++) {
            char instruction = instructions.charAt((instructionIdx++) % instructions.length());
            Pair<String, String> choice = network.get(currLoc);
            String nextLoc = instruction == 'R' ? choice.getSecond() : choice.getFirst();
            if ("ZZZ".equals(nextLoc)) {
                return step;
            }
            currLoc = nextLoc;
        }
    }

    private static long part2(List<String> input) {
        String instructions = input.get(0);

        Pattern p = Pattern.compile("(\\w{3}) = \\((\\w{3}), (\\w{3})\\)");
        Map<String, Pair<String, String>> network = new HashMap<>();
        for (int i = 2; i < input.size(); i++) {
            Matcher m = p.matcher(input.get(i));
            checkState(m.matches());
            checkState(null == network.put(m.group(1), Pair.create(m.group(2), m.group(3))));
        }

        String[] currLocs = network.keySet().stream()
                .filter(s -> s.endsWith("A"))
                .toArray(String[]::new);

        // a: They all loop around a Z
        // b: Their loops all start at step [0] and finish at a Z
        // So just find the loop lengths and LCM them all together
        // no hint in the question that a & b hold
        long[] loopLengths = new long[currLocs.length];

        for (int i = 0; i < currLocs.length; i++) {
            String start = currLocs[i];
            String currLoc = start;

            Map<Pair<String, Integer>, Integer> history = new HashMap<>();
            Set<Integer> zSteps = new HashSet<>();
            history.put(Pair.create(currLoc, 0), 0);
            int instructionIdx = 0;
            for (int step = 1; ; step++) {
                char instruction = instructions.charAt(instructionIdx);
                instructionIdx++;
                if (instructionIdx == instructions.length()) {
                    instructionIdx = 0;
                }
                Pair<String, String> choice = network.get(currLoc);
                String nextLoc = instruction == 'R' ? choice.getSecond() : choice.getFirst();
                if (nextLoc.endsWith("Z")) {
                    zSteps.add(step);
                }
                if (history.containsKey(Pair.create(nextLoc, instructionIdx))) {
                    // a: They all loop around a Z
                    assertThat(zSteps.size()).isEqualTo(1);
                    int loopLen = step - history.get(Pair.create(nextLoc, instructionIdx));
                    Integer zStep = Iterables.getOnlyElement(zSteps);
                    int loopOffset = zStep - loopLen;
                    // b: Their loops all start at step [0] and finish at a Z
                    assertThat(loopOffset).isEqualTo(0);
                    System.out.printf("%s: step %s, loopOffset %s, zStep %s\n",
                            i, step, loopOffset, zStep);
                    loopLengths[i] = loopLen;
                    break;
                } else {
                    history.put(Pair.create(nextLoc, instructionIdx), step);
                }
                currLoc = nextLoc;
            }
        }

        return Arrays.stream(loopLengths)
                .reduce(1, (a, b) -> lcm(a, b));
    }
}

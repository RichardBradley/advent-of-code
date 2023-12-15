package y2023;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Iterables;
import com.google.common.io.Resources;
import org.apache.commons.math3.util.Pair;

import java.net.Inet4Address;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2023D15 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            List<String> input = Resources.readLines(Resources.getResource("y2023/Y2023D15.txt"), StandardCharsets.UTF_8);

            // 1
            assertThat(part1(input)).isEqualTo(513172);

            // 2
            assertThat(part2(input)).isEqualTo(237806);

        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private static int hash(String s) {
        int curr = 0;
        for (int i = 0; i < s.length(); i++) {
            curr += s.charAt(i);
            curr *= 17;
            curr %= 256;
        }
        return curr;
    }

    private static long part1(List<String> input) {
        String line = Iterables.getOnlyElement(input);
        return Arrays.stream(line.split(",")).mapToInt(s -> hash(s)).sum();
    }

    private static long part2(List<String> input) {
        String line = Iterables.getOnlyElement(input);
        List<String> instructions = Splitter.on(",").splitToList(line);
        List<List<Pair<String, Integer>>> boxes = new ArrayList<>();
        for (int i = 0; i < 256; i++) {
            boxes.add(new ArrayList<>());
        }

        Pattern p = Pattern.compile("(\\w+)(-|=([0-9]))");
        for (String instruction : instructions) {
            Matcher m = p.matcher(instruction);
            checkState(m.matches());
            String label = m.group(1);
            String newLenseValue = m.group(3);
            boolean opIsMinus = null == newLenseValue;

            int boxId = hash(label);
            List<Pair<String, Integer>> box = boxes.get(boxId);

            if (opIsMinus) {
                // go to the relevant box and remove the lens with
                // the given label if it is present in the box
                Iterator<Pair<String, Integer>> it = box.iterator();
                while (it.hasNext()) {
                    if (it.next().getFirst().equals(label)) {
                        it.remove();
                        break;
                    }
                }
            } else {
                boolean madeReplacement = false;
                for (int i = 0; i < box.size(); i++) {
                    Pair<String, Integer> lens = box.get(i);
                    if (lens.getFirst().equals(label)) {
                        box.set(i, new Pair<>(label, Integer.parseInt(newLenseValue)));
                        madeReplacement = true;
                        break;
                    }
                }
                if (!madeReplacement) {
                    box.add(new Pair<>(label, Integer.parseInt(newLenseValue)));
                }
            }
        }

        // add up the focusing power of all of the lenses
        long acc = 0;
        for (int boxIdx = 0; boxIdx < boxes.size(); boxIdx++) {
            List<Pair<String, Integer>> box = boxes.get(boxIdx);
            for (int lensIdx = 0; lensIdx < box.size(); lensIdx++) {
                Pair<String, Integer> lens = box.get(lensIdx);
                acc += (1 + boxIdx) * (1 + lensIdx) * lens.getSecond();
            }
        }
        return acc;
    }
}

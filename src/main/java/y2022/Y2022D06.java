package y2022;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Iterables;
import com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

public class Y2022D06 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        List<String> input = Resources.readLines(Resources.getResource("y2022/Y2022D06.txt"), StandardCharsets.UTF_8);

        // 1
        assertThat(findNonRepeatingSeqIdx(example, 4)).isEqualTo(7);
        System.out.println(findNonRepeatingSeqIdx(Iterables.getOnlyElement(input), 4));

        // 2
        assertThat(findNonRepeatingSeqIdx(example, 14)).isEqualTo(19);
        System.out.println(findNonRepeatingSeqIdx(Iterables.getOnlyElement(input), 14));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static int findNonRepeatingSeqIdx(String input, int seqLen) {
        Deque<Character> lastFour = new ArrayDeque<>();
        for (int i = 0; i < seqLen; i++) {
            lastFour.add(input.charAt(i));
        }

        for (int i = seqLen; ; i++) {
            if (new HashSet<>(lastFour).size() == seqLen) {
                return i;
            }

            lastFour.remove();
            lastFour.add(input.charAt(i));
        }
    }

    private static String example = "mjqjpqmgbljsphdztnvjfqwrcgsmlb";
}

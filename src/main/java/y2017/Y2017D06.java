package y2017;

import com.google.common.base.Stopwatch;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;

public class Y2017D06 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        assertThat(part1(new int[]{0, 2, 7, 0})).isEqualTo("step = 5, loop len = 4");
        assertThat(part1(input)).isEqualTo("step = 11137, loop len = 1037");

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static String part1(int[] bufferState) {
        Map<String, Integer> seenStates = new HashMap<>();
        for (int stepCount = 0; ; stepCount++) {
            String stateAsString = Arrays.stream(bufferState)
                    .mapToObj(Integer::toString)
                    .collect(Collectors.joining(","));
            Integer previousSeenStepCount = seenStates.put(stateAsString, stepCount);
            if (previousSeenStepCount != null) {
                return String.format("step = %s, loop len = %s",
                        stepCount,
                        stepCount - previousSeenStepCount);
            }

            int maxCount = -1;
            int idxOfMaxCount = -1;
            for (int i = 0; i < bufferState.length; i++) {
                if (bufferState[i] > maxCount) {
                    maxCount = bufferState[i];
                    idxOfMaxCount = i;
                }
            }
            bufferState[idxOfMaxCount] = 0;
            for (int i = 1; i <= maxCount; i++) {
                bufferState[(idxOfMaxCount + i) % bufferState.length]++;
            }
        }
    }

    private static int[] input = new int[]{14, 0, 15, 12, 11, 11, 3, 5, 1, 6, 8, 4, 9, 1, 8, 4};
}

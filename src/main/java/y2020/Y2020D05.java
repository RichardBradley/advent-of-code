package y2020;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2020D05 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        assertThat(getSeatId("FBFBBFFRLR")).isEqualTo(357);
        assertThat(getSeatId("BFFFBBFRRR")).isEqualTo(567);
        assertThat(getSeatId("FFFBBBFRRR")).isEqualTo(119);
        assertThat(getSeatId("BBFFBBFRLL")).isEqualTo(820);

        List<String> input = Resources.readLines(Resources.getResource("y2020/Y2020D05.txt"), StandardCharsets.UTF_8);

        // What is the highest seat ID on a boarding pass?
        System.out.println("highest seat ID = " +
                input.stream().mapToInt(x -> getSeatId(x)).max());

        // Find seat in a gap:
        int[] seats = input.stream().mapToInt(x -> getSeatId(x))
                .sorted()
                .toArray();
        for (int i = 0; i < seats.length; i++) {
            if (seats[i] + 1 != seats[i + 1]) {
                System.out.println("Your seat is " + (seats[i] + 1));
                break;
            }
        }

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static int getSeatId(String seatSpec) {
        int minRowInc = 0;
        int maxRowEx = 128;
        for (int i = 0; i < 7; i++) {
            switch (seatSpec.charAt(i)) {
                case 'F':
                    maxRowEx = (maxRowEx - minRowInc) / 2 + minRowInc;
                    break;
                case 'B':
                    minRowInc = (maxRowEx - minRowInc) / 2 + minRowInc;
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }
        checkState(minRowInc + 1 == maxRowEx);
        int minColInc = 0;
        int maxColEx = 8;
        for (int i = 7; i < 10; i++) {
            switch (seatSpec.charAt(i)) {
                case 'L':
                    maxColEx = (maxColEx - minColInc) / 2 + minColInc;
                    break;
                case 'R':
                    minColInc = (maxColEx - minColInc) / 2 + minColInc;
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }
        checkState(minColInc + 1 == maxColEx);

        return minRowInc * 8 + minColInc;
    }
}

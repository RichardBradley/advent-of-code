package y2016;

import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

public class Y2016D18 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(countSafeTiles(".^^.^.^^^^", 10)).isEqualTo(38);
        System.out.println("example ok");

        System.out.println(countSafeTiles(".^^^^^.^^^..^^^^^...^.^..^^^.^^....^.^...^^^...^^^^..^...^...^^.^.^.......^..^^...^.^.^^..^^^^^...^.", 40));

        // 2
        System.out.println(countSafeTiles(".^^^^^.^^^..^^^^^...^.^..^^^.^^....^.^...^^^...^^^^..^...^...^^.^.^.......^..^^...^.^.^^..^^^^^...^.", 400000));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    static int countSafeTiles(String startRow, int rowCount) {
        int width = startRow.length();
        boolean[] curr = new boolean[width];
        int safeCount = 0;
        for (int i = 0; i < width; i++) {
            curr[i] = (startRow.charAt(i) == '^');
            if (!curr[i]) {
                safeCount++;
            }
        }
        boolean[] next = new boolean[curr.length];
        for (int row = 1; row < rowCount; row++) {
            for (int col = 0; col < width; col++) {
                boolean leftIsTrap = col > 0 && curr[col - 1];
                boolean centreIsTrap = curr[col];
                boolean rightIsTrap = col < (width - 1) && curr[col + 1];
                if ((leftIsTrap && centreIsTrap && !rightIsTrap)
                        || (centreIsTrap && rightIsTrap && !leftIsTrap)
                        || (leftIsTrap && !centreIsTrap && !rightIsTrap)
                        || (rightIsTrap && !centreIsTrap && !leftIsTrap)) {
                    next[col] = true;
                } else {
                    next[col] = false;
                    safeCount++;
                }
            }
            boolean[] tmp = curr;
            curr = next;
            next = tmp;
        }
        return safeCount;
    }
}

package y2017;

import com.google.common.base.Stopwatch;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

public class Y2017D17 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {

            assertThat(runSpin(3, 9)).isEqualTo(5);
            assertThat(runSpin(3, 2017)).isEqualTo(638);
            assertThat(runSpin(394, 2017)).isEqualTo(926);

            assertThat(part2(3, 8)).isEqualTo(5);
            assertThat(part2(3, 9)).isEqualTo(9);
            // Takes 235sec with linked list, takes 570ms with this method!
            assertThat(part2(394, 50000000)).isEqualTo(10150888);
        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private static int runSpin(int stepsForEachInsert, int insertCount) {
        long lastReportTimeMillis = System.currentTimeMillis();
        Node curr = new Node(0, null);
        curr.next = curr;

        for (int i = 1; i <= insertCount; i++) {
            for (int step = 0; step < stepsForEachInsert; step++) {
                curr = curr.next;
            }
            Node newVal = new Node(i, curr.next);
            curr.next = newVal;
            curr = newVal;

            if (System.currentTimeMillis() - lastReportTimeMillis > 10000) {
                lastReportTimeMillis = System.currentTimeMillis();
                System.out.println("i = " + i);
            }
        }

        return curr.next.val;
    }

    /**
     * Don't need to actually track the list; just put "0" in index 0, and
     * track what's at index 1 for the output
     */
    private static int part2(int stepsForEachInsert, int insertCount) {
        int listLen = 1;
        int valAtIndex1 = -1;
        int currIndex = 0;

        for (int i = 1; i <= insertCount; i++) {
            currIndex = (currIndex + stepsForEachInsert) % listLen + 1;
            listLen++;
            if (currIndex == 1) {
                valAtIndex1 = i;
            }
        }

        return valAtIndex1;
    }

    @Data
    @AllArgsConstructor
    private static class Node {
        int val;
        Node next;
    }
}


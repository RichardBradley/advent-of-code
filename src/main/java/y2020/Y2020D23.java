package y2020;

import com.google.common.base.Stopwatch;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

public class Y2020D23 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        assertThat(run("389125467", 9, 10, true)).isEqualTo("92658374");
        assertThat(run("716892543", 9, 100, true)).isEqualTo("49725386");

        assertThat(run("716892543", 1000000, 10000000, false)).isEqualTo("538935646702");

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static String run(String input, int cupCount, int stepCount, boolean isPart1) {
        // Singly linked list, looking clockwise
        Map<Integer, Cup> cupsByLabel = new HashMap<>();
        Cup currentCup = new Cup(input.charAt(0) - '0', null);
        cupsByLabel.put(currentCup.label, currentCup);
        {
            Cup c = currentCup;
            for (int i = 1; i < input.length(); i++) {
                int label = input.charAt(i) - '0';
                Cup next = new Cup(label, null);
                cupsByLabel.put(label, next);
                c.next = next;
                c = next;
            }
            for (int i = input.length() + 1; i <= cupCount; i++) {
                Cup next = new Cup(i, null);
                cupsByLabel.put(i, next);
                c.next = next;
                c = next;
            }
            c.next = currentCup;
        }

        for (int step = 1; step <= stepCount; step++) {

            // The crab picks up the three cups that are immediately clockwise
            // of the current cup. They are removed from the circle; cup
            // spacing is adjusted as necessary to maintain the circle.
            Cup removedHead = currentCup.next;
            currentCup.next = removedHead.next.next.next;
            removedHead.next.next.next = null;

            // The crab selects a destination cup: the cup with a label
            // equal to the current cup's label minus one. If this would
            // select one of the cups that was just picked up, the crab
            // will keep subtracting one until it finds a cup that wasn't
            // just picked up. If at any point in this process the value
            // goes below the lowest value on any cup's label, it wraps
            // around to the highest value on any cup's label instead.
            int destLabel = wrap(currentCup.label - 1, cupCount);
            while (destLabel == removedHead.label
                    || destLabel == removedHead.next.label
                    || destLabel == removedHead.next.next.label) {
                destLabel = wrap(destLabel - 1, cupCount);
            }
            Cup destCup = cupsByLabel.get(destLabel);

            // The crab places the cups it just picked up so that
            // they are immediately clockwise of the destination cup.
            // They keep the same order as when they were picked up.
            Cup afterDest = destCup.next;
            destCup.next = removedHead;
            removedHead.next.next.next = afterDest;

            // The crab selects a new current cup: the cup which
            // is immediately clockwise of the current cup.
            currentCup = currentCup.next;
        }

        Cup cupOne = cupsByLabel.get(1);
        if (isPart1) {
            return toString(cupOne).substring(1);
        } else {
            return Long.toString((long)cupOne.next.label * (long)cupOne.next.next.label);
        }
    }

    private static int wrap(int n, int cupCount) {
        if (n < 1) {
            n += cupCount;
        }
        return n;
    }

    private static String toString(Cup first) {
        StringBuilder acc = new StringBuilder();
        acc.append(first.label);
        Cup c = first.next;
        while (c != first) {
            acc.append(c.label);
            c = c.next;
        }
        return acc.toString();
    }

    @Data
    @AllArgsConstructor
    static class Cup {
        int label;
        Cup next;
    }
}

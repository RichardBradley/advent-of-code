package y2016;

import com.google.common.base.Stopwatch;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

public class Y2016D19 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(whichElfGetsAllPart1(5)).isEqualTo(3);
        System.out.println("example ok");

        System.out.println(whichElfGetsAllPart1(3017957)); // 1841611

        // 2
        assertThat(whichElfGetsAllPart2(5)).isEqualTo(2);
        System.out.println("example ok");
        System.out.println(whichElfGetsAllPart2(3017957)); // 1423634

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    static int whichElfGetsAllPart1(int elfCount) {
        int[] presentCountByElf = new int[elfCount];
        Arrays.fill(presentCountByElf, 1);
        int actingElfIdx = -1;
        while (true) {
            if (++actingElfIdx == elfCount) {
                actingElfIdx = 0;
            }
            if (presentCountByElf[actingElfIdx] == 0) {
//                System.out.printf("Elf %s has no presents and is skipped.\n", actingElfIdx + 1);
                continue;
            }

            // look once for the next elf with presents
            int targetElfIdx = actingElfIdx;
            while (true) {
                if (++targetElfIdx == elfCount) {
                    targetElfIdx = 0;
                }
                if (targetElfIdx == actingElfIdx) {
                    // none found, acting elf wins
                    return actingElfIdx + 1;
                }
                if (presentCountByElf[targetElfIdx] != 0) {
//                    System.out.printf("Elf %s takes Elf %s's %s present(s).\n", actingElfIdx + 1, targetElfIdx + 1, presentCountByElf[targetElfIdx]);
                    // steal from this elf
                    presentCountByElf[actingElfIdx] += presentCountByElf[targetElfIdx];
                    presentCountByElf[targetElfIdx] = 0;
                    break;
                }
            }
        }
    }

    @Data
    @AllArgsConstructor
    static class Elf {
        int startingPosition;
        int presentCount;
        Elf next;

        public String toString() {
            return String.format("Elf #%s presents = %s next = #%s",
                    startingPosition,
                    presentCount,
                    next == null ? "null" : next.startingPosition);
        }
    }

    static int whichElfGetsAllPart2(int elfCount) {
        long lastReportTimeMillis = System.currentTimeMillis();

        Elf firstElf = new Elf(1, 1, null);
        Elf prevElf = firstElf;
        Elf oppositeElf = null;
        Elf beforeOppositeElf = null;
        int initialOppositeOneIndex = elfCount / 2 + 1;
        for (int i = 2; i <= elfCount; i++) {
            Elf nextElf = new Elf(i, 1, null);
            prevElf.next = nextElf;
            prevElf = nextElf;

            if (i == initialOppositeOneIndex) {
                oppositeElf = nextElf;
            } else if ((i + 1) == initialOppositeOneIndex) {
                beforeOppositeElf = nextElf;
            }
        }
        prevElf.next = firstElf;

        if (elfCount < 10) {
            printState(firstElf, oppositeElf, beforeOppositeElf);
        }

        Elf actingElf = firstElf;
        while (elfCount > 1) {
            // Steal from oppositeElf, remove them from the circle
            actingElf.presentCount += oppositeElf.presentCount;
            beforeOppositeElf.next = oppositeElf.next;
            elfCount--;

            // fix up oppositeElf pointer:
            // the new opposite elf (opposite the next to act) is 2 ahead if count was odd
            // or 1 ahead if count was even
            oppositeElf = oppositeElf.next;
            if (0 == (elfCount % 2)) {
                beforeOppositeElf = oppositeElf;
                oppositeElf = oppositeElf.next;
            }

            if (elfCount < 10) {
                System.out.println("### New state");
                printState(actingElf, oppositeElf, beforeOppositeElf);
            }

            // Next turn:
            actingElf = actingElf.next;

            if (System.currentTimeMillis() - lastReportTimeMillis > 10000) {
                lastReportTimeMillis = System.currentTimeMillis();
                System.out.println("elfCount = " + elfCount);
            }
        }

        return actingElf.startingPosition;
    }

    private static void printState(Elf firstElf, Elf oppositeElf, Elf beforeOppositeElf) {
        System.out.println(firstElf + (firstElf == oppositeElf ? " OPPOSITE" : "") + (firstElf == beforeOppositeElf ? " BEFORE OP" : ""));
        for (Elf curr = firstElf.next; curr != firstElf; curr = curr.next) {
            System.out.println(curr + (curr == oppositeElf ? " OPPOSITE" : "") + (curr == beforeOppositeElf ? " BEFORE OP" : ""));
        }
    }
}

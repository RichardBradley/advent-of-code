package y2016;

import com.google.common.base.Stopwatch;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

public class Y2016D19 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(whichElfGetsAll(5)).isEqualTo(3);
        System.out.println("example ok");

        System.out.println(whichElfGetsAll(3017957));

        // 2
        assertThat(whichElfGetsAll2(5)).isEqualTo(2);
        System.out.println("example ok");
        System.out.println(whichElfGetsAll2(3017957));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    static int whichElfGetsAll(int elfCount) {
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
    }

    static int whichElfGetsAll2(int elfCount) {
        return -1;
//        LinkedList<Elf> elves = new LinkedList<>();
//        for (int i = 1; i <= elfCount; i++) {
//            elves.add(new Elf(i, 1));
//        }
//        while (elves.size() > 1) {
//            for (Elf elf : elves) {
//
//            }
//        }
//        int actingElfIdx = -1;
//        while (true) {
//            if (++actingElfIdx == elfCount) {
//                actingElfIdx = 0;
//            }
//            if (presentCountByElf[actingElfIdx] == 0) {
////                System.out.printf("Elf %s has no presents and is skipped.\n", actingElfIdx + 1);
//                continue;
//            }
//
//            // look once for the next elf with presents
//            int targetElfIdx = actingElfIdx;
//            while (true) {
//                if (++targetElfIdx == elfCount) {
//                    targetElfIdx = 0;
//                }
//                if (targetElfIdx == actingElfIdx) {
//                    // none found, acting elf wins
//                    return actingElfIdx + 1;
//                }
//                if (presentCountByElf[targetElfIdx] != 0) {
////                    System.out.printf("Elf %s takes Elf %s's %s present(s).\n", actingElfIdx + 1, targetElfIdx + 1, presentCountByElf[targetElfIdx]);
//                    // steal from this elf
//                    presentCountByElf[actingElfIdx] += presentCountByElf[targetElfIdx];
//                    presentCountByElf[targetElfIdx] = 0;
//                    break;
//                }
//            }
//        }
    }
}

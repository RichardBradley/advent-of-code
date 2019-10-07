package y2015;

import com.google.common.base.Stopwatch;
import org.apache.commons.math3.util.CombinatoricsUtils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

public class Y2015D24 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(
                getQuantumEntanglementOfTheFirstGroupOfPackagesInTheIdealConfiguration4(
                        example,
                        3))
                .isEqualTo(99);

        System.out.println("Example took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");


        System.out.println(
                getQuantumEntanglementOfTheFirstGroupOfPackagesInTheIdealConfiguration4(
                        input,
                        3));

        // 2
        System.out.println("########");
        System.out.println(
                getQuantumEntanglementOfTheFirstGroupOfPackagesInTheIdealConfiguration4(
                        input,
                        4));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");

    }

    static long getQuantumEntanglementOfTheFirstGroupOfPackagesInTheIdealConfiguration4(
            int[] weights, int compartmentCount) {
        int sum = Arrays.stream(weights).sum();
        assertThat(sum % compartmentCount).isEqualTo(0);
        int targetCompartmentSum = sum / compartmentCount;

        long smallestValidQuantumEntanglement = Long.MAX_VALUE;

        // looking for the valid package with the smallest count, so can stop when any are found
        int validGroupCount = 0;
        for (int size = 0; ; size++) {
            Iterator<int[]> combinationsIterator = CombinatoricsUtils.combinationsIterator(weights.length, size);
            while (combinationsIterator.hasNext()) {
                int[] combIndexes = combinationsIterator.next();
                int combSumWeight = 0;
                for (int i = 0; i < size; i++) {
                    combSumWeight += weights[combIndexes[i]];
                }

                // Don't bother to check if the packages are divisible into the other compartments!
                if (combSumWeight == targetCompartmentSum) {
                    validGroupCount++;

                    long quantumEntanglement = 1;
                    for (int i = 0; i < size; i++) {
                        quantumEntanglement *= weights[combIndexes[i]];
                    }

                    if (quantumEntanglement < smallestValidQuantumEntanglement) {
                        smallestValidQuantumEntanglement = quantumEntanglement;
                    }
                }
            }

            if (validGroupCount > 0) {
                break;
            }
        }

        return smallestValidQuantumEntanglement;
    }

    static int[] example = new int[]{
            1, 2, 3, 4, 5, 7, 8, 9, 10, 11
    };

    static int[] input = new int[]{
            1,
            2,
            3,
            5,
            7,
            13,
            17,
            19,
            23,
            29,
            31,
            37,
            41,
            43,
            53,
            59,
            61,
            67,
            71,
            73,
            79,
            83,
            89,
            97,
            101,
            103,
            107,
            109,
            113
    };
}

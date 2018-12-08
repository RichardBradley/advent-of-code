package y2015;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

import java.util.stream.StreamSupport;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class Y2015D17 {
    public static void main(String[] args) throws Exception {

        // 1
        assertThat(countCombinations(testInput, 0, 25)).isEqualTo(4);

        System.out.println(countCombinations(input, 0, 150));

        // 2
        assertThat(countCombinationsOfMinLength(testInput, 25)).isEqualTo(3);

        System.out.println(countCombinationsOfMinLength(input, 150));
    }

    private static int countCombinations(int[] containers, int offset, int totalVol) {
        if (offset == containers.length) {
            return totalVol == 0 ? 1 : 0;
        }
        int acc = 0;
        if (totalVol - containers[offset] >= 0) {
            acc += countCombinations(containers, offset + 1, totalVol - containers[offset]);
        }
        return acc + countCombinations(containers, offset + 1, totalVol);
    }

    private static long countCombinationsOfMinLength(int[] containers, int totalVol) {
        Iterable<Iterable<Integer>> combinations = generateCombinations(containers, 0, totalVol, emptyList());
        int minCombLength = (int) StreamSupport.stream(combinations.spliterator(), false)
                .mapToLong(x -> Iterators.size(x.iterator()))
                .min().getAsLong();
        return (int) StreamSupport.stream(combinations.spliterator(), false).filter(x -> Iterators.size(x.iterator()) == minCombLength).count();
    }

    // This would be a lot prettier in Scala with "::" cons instead of Iterables.concat
    private static Iterable<Iterable<Integer>> generateCombinations(int[] containers, int offset, int totalVol, Iterable<Integer> currentComb) {
        if (offset == containers.length) {
            return totalVol == 0 ? singletonList(currentComb) : emptyList();
        }
        Iterable<Iterable<Integer>> acc = generateCombinations(containers, offset + 1, totalVol, currentComb);
        if (totalVol - containers[offset] >= 0) {
            return Iterables.concat(
                    acc,
                    generateCombinations(containers, offset + 1, totalVol - containers[offset],
                            Iterables.concat(singletonList(containers[offset]), currentComb)));
        } else {
            return acc;
        }
    }

    static int[] testInput = {20, 15, 10, 5, 5};

    private static int[] input = new int[]{
            43,
            3,
            4,
            10,
            21,
            44,
            4,
            6,
            47,
            41,
            34,
            17,
            17,
            44,
            36,
            31,
            46,
            9,
            27,
            38
    };
}

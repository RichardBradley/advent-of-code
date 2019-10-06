package y2015;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Sets;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static com.google.common.truth.Truth.assertThat;

public class Y2015D24 {

    static Instant nextLog = Instant.now().plusSeconds(10);

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(
                getQuantumEntanglementOfTheFirstGroupOfPackagesInTheIdealConfiguration3(
                        example))
                .isEqualTo(99);

        System.out.println("Example took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");


        System.out.println(
                getQuantumEntanglementOfTheFirstGroupOfPackagesInTheIdealConfiguration3(
                        input));

//        // 2
//        System.out.println("########");
//        System.out.println(run(parse(input), new int[]{1, 0}));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");

    }

    static int getQuantumEntanglementOfTheFirstGroupOfPackagesInTheIdealConfiguration4(int[] weights) {
        org.apache.commons.math3.util.CombinatoricsUtils x;
    }


    static int getQuantumEntanglementOfTheFirstGroupOfPackagesInTheIdealConfiguration3(int[] weights) {

        Collections.shuffle(Arrays.asList(weights));

        int sum = Arrays.stream(weights).sum();
        assertThat(sum % 3).isEqualTo(0);
        int targetCompartmentSum = sum / 3;
        AtomicInteger minFirstCompartmentCount = new AtomicInteger(Integer.MAX_VALUE);
        AtomicInteger smallestValidQuantumEntanglement = new AtomicInteger(Integer.MAX_VALUE);

        int[] weightUsage = new int[weights.length];
        fillFirstCompartment(
                0,
                0,
                targetCompartmentSum,
                weights,
                weightUsage,
                firstCompartmentCount -> {

                    if (firstCompartmentCount < minFirstCompartmentCount.get()) {
                        minFirstCompartmentCount.set(firstCompartmentCount);
                        smallestValidQuantumEntanglement.set(getFirstCompartmentQuantumEntanglement(weights, weightUsage));
                    } else if (firstCompartmentCount == minFirstCompartmentCount.get()) {
                        int newQe = getFirstCompartmentQuantumEntanglement(weights, weightUsage);
                        if (newQe < smallestValidQuantumEntanglement.get()) {
                            smallestValidQuantumEntanglement.set(newQe);
                        }
                    }
                });

        return smallestValidQuantumEntanglement.get();
    }

    private static int getFirstCompartmentQuantumEntanglement(int[] weights, int[] weightUsage) {
        int qe = 1;
        for (int i = 0; i < weightUsage.length; i++) {
            if (weightUsage[i] == 1) {
                qe *= weights[i];
            }
        }
        return qe;
    }

    static void fillFirstCompartment(
            int firstCompartmentWeight,
            int firstCompartmentCount,
            int targetWeight,
            int[] weights,
            int[] weightUsage,
            Consumer<Integer> onPossibleFill) {


        for (int i = 0; i < weights.length; i++) {
            if (weightUsage[i] == 0) {
                int newWeight = firstCompartmentWeight + weights[i];
                int newCount = firstCompartmentCount + 1;
                weightUsage[i] = 1;
                if (newWeight == targetWeight) {
                    if (canFillTwoCompartments(targetWeight, 0, 0, weights, weightUsage)) {
                        onPossibleFill.accept(newCount);
                    }
                } else if (newWeight < targetWeight) {
                    fillFirstCompartment(
                            newWeight,
                            newCount,
                            targetWeight,
                            weights,
                            weightUsage,
                            onPossibleFill);
                }

                weightUsage[i] = 0;
            }
        }

        Instant now = Instant.now();
        if (now.isAfter(nextLog)) {
            System.out.println(Arrays.toString(weightUsage));
            nextLog = now.plusSeconds(10);
        }
    }

    private static boolean canFillTwoCompartments(
            int targetWeight,
            int comp2Weight,
            int comp3Weight,
            int[] weights,
            int[] weightUsage) {

        if (comp2Weight < targetWeight) {
            for (int i = 0; i < weights.length; i++) {
                if (weightUsage[i] == 0) {
                    weightUsage[i] = 2;

                    if (canFillTwoCompartments(
                            targetWeight,
                            comp2Weight + weights[i],
                            comp3Weight,
                            weights,
                            weightUsage)) {
                        weightUsage[i] = 0;
                        return true;
                    } else {
                        weightUsage[i] = 0;
                    }
                }
            }
            return false;

        } else if (comp2Weight == targetWeight) {

            if (comp3Weight < targetWeight) {
                for (int i = 0; i < weights.length; i++) {
                    if (weightUsage[i] == 0) {
                        weightUsage[i] = 3;

                        if (canFillTwoCompartments(
                                targetWeight,
                                comp2Weight,
                                comp3Weight + weights[i],
                                weights,
                                weightUsage)) {
                            weightUsage[i] = 0;
                            return true;
                        } else {
                            weightUsage[i] = 0;
                        }
                    }
                }
                return false;
            } else if (comp3Weight == targetWeight) {
                return true;
            } else { // comp3Weight > targetWeight
                return false;
            }

        } else { // comp2Weight > targetWeight
            return false;
        }
    }


    static int getQuantumEntanglementOfTheFirstGroupOfPackagesInTheIdealConfiguration2(int[] weights) {
        Instant nextLog = Instant.now().plusSeconds(10);

        int smallestCountInFirstComp = Integer.MAX_VALUE;
        int smallestValidQuantumEntanglement = Integer.MAX_VALUE;

        int[] compartmentIndexes = new int[weights.length];
        do {
            int sumFirstComp = 0;
            int countFirstComp = 0;
            int productFirstComp = 1;
            int sumSecondComp = 0;
            int sumThirdComp = 0;
            for (int i = 0; i < compartmentIndexes.length; i++) {
                switch (compartmentIndexes[i]) {
                    case 0:
                        sumFirstComp += weights[i];
                        productFirstComp *= weights[i];
                        countFirstComp++;
                        break;
                    case 1:
                        sumSecondComp += weights[i];
                        break;
                    case 2:
                        sumThirdComp += weights[i];
                        break;
                }
            }

            if (countFirstComp > 0
                    && sumFirstComp == sumSecondComp
                    && sumFirstComp == sumThirdComp) {

                if (countFirstComp < smallestCountInFirstComp) {
                    smallestCountInFirstComp = countFirstComp;
                    smallestValidQuantumEntanglement = productFirstComp;
                } else if (countFirstComp == smallestCountInFirstComp
                        && productFirstComp < smallestValidQuantumEntanglement) {
                    smallestValidQuantumEntanglement = productFirstComp;
                }
            }

            Instant now = Instant.now();
            if (now.isAfter(nextLog)) {
                System.out.println(Arrays.toString(compartmentIndexes));
                nextLog = now.plusSeconds(10);
            }

        } while (incr(compartmentIndexes));

        return smallestValidQuantumEntanglement;

//        int smallestValidQuantumEntanglement = Integer.MAX_VALUE;
//
//        for (int countInFirstComp = 1; ; countInFirstComp++) {
//            for (Set<Integer> firstComp : Sets.combinations(weights, countInFirstComp)) {
//                int sumFirstComp = firstComp.stream().mapToInt(x -> x).sum();
//                Sets.SetView<Integer> remain = Sets.difference(weights, firstComp);
//                for (Set<Integer> secondComp : Sets.powerSet(remain)) {
//                    int sumSecondComp = secondComp.stream().mapToInt(x -> x).sum();
//                    if (sumSecondComp == sumFirstComp) {
//                        Sets.SetView<Integer> thirdComp = Sets.difference(remain, secondComp);
//                        int sumThirdComp = thirdComp.stream().mapToInt(x -> x).sum();
//                        if (sumThirdComp == sumFirstComp) {
//                            smallestValidQuantumEntanglement =
//                                    Math.min(
//                                            firstComp.stream().mapToInt(x -> x).reduce((a, b) -> a * b).getAsInt(),
//                                            smallestValidQuantumEntanglement);
//                        }
//                    }
//                }
//            }
//
//            if (smallestValidQuantumEntanglement != Integer.MAX_VALUE) {
//                return smallestValidQuantumEntanglement;
//            }
//            System.out.println("No matches for " + countInFirstComp);
//        }
    }

    // lexical increment, false on overflow
    private static boolean incr(int[] compartmentIndexes) {
        for (int i = 0; i < compartmentIndexes.length; i++) {
            if (++compartmentIndexes[i] == 3) {
                compartmentIndexes[i] = 0;
                continue;
            }
            return true;
        }
        return false;
    }

    static int getQuantumEntanglementOfTheFirstGroupOfPackagesInTheIdealConfiguration(Set<Integer> weights) {
        int smallestValidQuantumEntanglement = Integer.MAX_VALUE;

        for (int countInFirstComp = 1; ; countInFirstComp++) {
            for (Set<Integer> firstComp : Sets.combinations(weights, countInFirstComp)) {
                int sumFirstComp = firstComp.stream().mapToInt(x -> x).sum();
                Sets.SetView<Integer> remain = Sets.difference(weights, firstComp);
                for (Set<Integer> secondComp : Sets.powerSet(remain)) {
                    int sumSecondComp = secondComp.stream().mapToInt(x -> x).sum();
                    if (sumSecondComp == sumFirstComp) {
                        Sets.SetView<Integer> thirdComp = Sets.difference(remain, secondComp);
                        int sumThirdComp = thirdComp.stream().mapToInt(x -> x).sum();
                        if (sumThirdComp == sumFirstComp) {
                            smallestValidQuantumEntanglement =
                                    Math.min(
                                            firstComp.stream().mapToInt(x -> x).reduce((a, b) -> a * b).getAsInt(),
                                            smallestValidQuantumEntanglement);
                        }
                    }
                }
            }

            if (smallestValidQuantumEntanglement != Integer.MAX_VALUE) {
                return smallestValidQuantumEntanglement;
            }
            System.out.println("No matches for " + countInFirstComp);
        }
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

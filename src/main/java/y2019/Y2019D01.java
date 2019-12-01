package y2019;

import com.google.common.base.Stopwatch;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

public class Y2019D01 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        System.out.println(getTotalFuelReq(input));

        // 2
        assertThat(getRecursiveMass(14)).isEqualTo(2);
        assertThat(getRecursiveMass(100756)).isEqualTo(50346);
        System.out.println(Arrays.stream(input).map(i -> getRecursiveMass(i)).sum());

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static int getRecursiveMass(int mass) {
        int acc = 0;
        while (true) {
            int fuel = mass / 3 - 2;
            if (fuel > 0) {
                acc += fuel;
                mass = fuel;
            } else {
                return acc;
            }
        }
    }

    private static int getTotalFuelReq(int[] input) {
        return Arrays.stream(input)
                .map(i -> i / 3 - 2)
                .sum();
    }

    static int[] input = new int[]{
            68884,
            100920,
            114424,
            139735,
            103685,
            133067,
            77650,
            77695,
            85927,
            108144,
            131312,
            97795,
            83234,
            61637,
            137735,
            126903,
            71037,
            58593,
            54510,
            66117,
            54164,
            60761,
            128623,
            52359,
            55458,
            145494,
            57319,
            98478,
            110008,
            86620,
            103271,
            86924,
            116773,
            87534,
            102462,
            119945,
            126017,
            84706,
            129840,
            97831,
            136000,
            79667,
            133831,
            92793,
            148917,
            75262,
            129853,
            60513,
            89914,
            79584,
            64229,
            124145,
            127684,
            142628,
            52734,
            130649,
            87191,
            126500,
            137058,
            109782,
            108641,
            102147,
            132881,
            119065,
            58999,
            62462,
            105232,
            79743,
            127994,
            143392,
            61072,
            59375,
            57361,
            128021,
            101544,
            135661,
            135469,
            51693,
            103286,
            146654,
            97886,
            133910,
            71306,
            147224,
            73771,
            91292,
            116892,
            116906,
            107424,
            68283,
            100285,
            105709,
            120370,
            92931,
            146706,
            131745,
            101710,
            85089,
            98788,
            116232,
    };
}

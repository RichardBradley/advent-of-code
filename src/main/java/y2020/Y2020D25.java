package y2020;

import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

public class Y2020D25 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        assertThat(part1(7, 5764801, 17807724)).isEqualTo(14897079);
        assertThat(part1(7, 17607508, 15065270)).isEqualTo(12285001);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    static long part1(long subjectNumber, long publicKey1, long publicKey2) {
        long loopSize1 = findLoopSize(subjectNumber, publicKey1);
        long loopSize2 = findLoopSize(subjectNumber, publicKey2);

        long encryptionKey1 = transform(publicKey1, loopSize2);
        long encryptionKey2 = transform(publicKey2, loopSize1);
        assertThat(encryptionKey1).isEqualTo(encryptionKey2);
        return encryptionKey1;
    }

    private static long findLoopSize(long subjectNumber, long target) {
        long value = 1;
        int loopCount = 0;
        while (value != target) {
            value *= subjectNumber;
            value %= 20201227;
            loopCount++;
        }
        return loopCount;
    }

    private static long transform(long subjectNumber, long loopSize) {
        long value = 1;
        for (int i = 0; i < loopSize; i++) {
            value *= subjectNumber;
            value %= 20201227;
        }
        return value;
    }
}

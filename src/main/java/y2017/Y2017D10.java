package y2017;

import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

public class Y2017D10 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        assertThat(hash(getListTo(5), new int[]{3, 4, 1, 5})).isEqualTo(12);
        assertThat(hash(getListTo(256), input)).isEqualTo(46600);

        assertThat(hash2("")).isEqualTo("a2582a3a0e66e6e86e3812dcb672a272");
        assertThat(hash2("AoC 2017")).isEqualTo("33efeb34ea91902bb2f59c9920caa6cd");
        assertThat(hash2(inputStr)).isEqualTo("23234babdc6afa036749cfa9b597de1b");

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static String hash2(String data) {
        int[] list = getListTo(256);
        int[] inputLengths = new int[data.length() + 5];
        for (int i = 0; i < data.length(); i++) {
            inputLengths[i] = data.charAt(i);
        }
        System.arraycopy(new int[]{17, 31, 73, 47, 23}, 0, inputLengths, data.length(), 5);

        int currPos = 0;
        int skipSize = 0;
        for (int round = 0; round < 64; round++) {
            for (int inputLength : inputLengths) {
                for (int i = 0; i < inputLength / 2; i++) {
                    int idx1 = (currPos + i) % list.length;
                    int idx2 = (currPos + inputLength - i - 1) % list.length;
                    int tmp = list[idx1];
                    list[idx1] = list[idx2];
                    list[idx2] = tmp;
                }
                currPos += skipSize + inputLength;
                currPos = currPos % list.length;
                skipSize++;
            }
        }

        StringBuilder acc = new StringBuilder();
        for (int i = 0; i < 256; i += 16) {
            int digitAcc = 0;
            for (int j = 0; j < 16; j++) {
                digitAcc ^= list[i + j];
            }
            acc.append(String.format("%02x", digitAcc));
        }
        return acc.toString();
    }

    private static int hash(int[] list, int[] inputLengths) {
        int currPos = 0;
        int skipSize = 0;
        for (int inputLength : inputLengths) {
            for (int i = 0; i < inputLength / 2; i++) {
                int idx1 = (currPos + i) % list.length;
                int idx2 = (currPos + inputLength - i - 1) % list.length;
                int tmp = list[idx1];
                list[idx1] = list[idx2];
                list[idx2] = tmp;
            }
            currPos += skipSize + inputLength;
            currPos = currPos % list.length;
            skipSize++;

            // System.out.printf("list = %s  currPos = %s  skipSize = %s\n", Arrays.toString(list), currPos, skipSize);
        }
        return list[0] * list[1];
    }

    private static int[] getListTo(int n) {
        int[] acc = new int[n];
        for (int i = 0; i < n; i++) {
            acc[i] = i;
        }
        return acc;
    }

    private static int[] input = new int[]{18, 1, 0, 161, 255, 137, 254, 252, 14, 95, 165, 33, 181, 168, 2, 188};
    private static String inputStr = "18,1,0,161,255,137,254,252,14,95,165,33,181,168,2,188";
}


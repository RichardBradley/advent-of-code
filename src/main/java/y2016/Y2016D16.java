package y2016;

import com.google.common.base.Stopwatch;

import java.security.MessageDigest;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2016D16 {

    private static MessageDigest md5;

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(doubleUp(new StringBuilder("111100001010")).toString()).isEqualTo("1111000010100101011110000");
        assertThat(checkSum(new StringBuilder("110010110100"))).isEqualTo("100");
        assertThat(getFilledChecksum("10000", 20)).isEqualTo("01100");
        System.out.println("example ok");

        System.out.println(getFilledChecksum("01000100010010111", 272));

        // 2
        System.out.println(getFilledChecksum("01000100010010111", 35651584));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static StringBuilder doubleUp(StringBuilder in) {
        int len = in.length();
        in.append('0');
        for (int i = len - 1; i >= 0; i--) {
            in.append(in.charAt(i) == '0' ? '1' : '0');
        }
        return in;
    }

    private static String checkSum(StringBuilder in) {
        checkArgument(in.length() % 2 == 0);
        while (true) {
            StringBuilder next = new StringBuilder();
            for (int i = 0; i < in.length(); i += 2) {
                next.append((in.charAt(i) == in.charAt(i + 1)) ? '1' : '0');
            }
            if (next.length() % 2 == 0) {
                in = next;
            } else {
                return next.toString();
            }
        }
    }

    private static String getFilledChecksum(String init, int length) {
        StringBuilder disk = new StringBuilder(init);
        while (disk.length() < length) {
            disk = doubleUp(disk);
        }
        disk.delete(length, disk.length());
        checkState(disk.length() == length);
        return checkSum(disk);
    }
}

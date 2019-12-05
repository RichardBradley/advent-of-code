package y2019;

import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

public class Y2019D04 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        System.out.println(countMatchingPasswords(false));

        // 2
        assertThat(isCandidate("112233", true)).isTrue();
        assertThat(isCandidate("123444", true)).isFalse();
        assertThat(isCandidate("111122", true)).isTrue();
        assertThat(isCandidate("111567", true)).isFalse();
        assertThat(isCandidate("456777", true)).isFalse();
        System.out.println(countMatchingPasswords(true));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static int countMatchingPasswords(boolean part2) {
        int count = 0;
        for (int pass = 172851; pass <= 675869; pass++) {
            String s = Integer.toString(pass);
            if (isCandidate(s, part2)) {
                count++;
            }
        }
        return count;
    }

    private static boolean isCandidate(String pass, boolean part2) {
        boolean hasDoubleDigit = false;
        for (int i = 0; i < 5; i++) {
            if (pass.charAt(i) == pass.charAt(i + 1)) {
                if (!part2) {
                    hasDoubleDigit = true;
                } else {
                    if (!(
                            (i - 1 >= 0 && pass.charAt(i - 1) == pass.charAt(i))
                                    || (i + 2 < 6 && pass.charAt(i + 2) == pass.charAt(i)))) {
                        hasDoubleDigit = true;
                    }
                }
            }
            if (pass.charAt(i) > pass.charAt(i + 1)) {
                return false;
            }
        }
        return hasDoubleDigit;
    }
}

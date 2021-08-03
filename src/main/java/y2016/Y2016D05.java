package y2016;

import com.google.common.base.Stopwatch;
import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Hex;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

public class Y2016D05 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(md5("abc3231929")).isEqualTo("00000155F8105DFF7F56EE10FA9B9ABD");
        assertThat(genPassword("abc")).isEqualTo("18f47a30");

        System.out.println(genPassword("ffykfhsq"));

        // 2
        assertThat(genPassword2("abc")).isEqualTo("05ace8e3");

        System.out.println(genPassword2("ffykfhsq"));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static String genPassword2(String doorId) {
        StringBuilder acc = new StringBuilder();
        acc.append("________");
        int i = 0;
        while (acc.indexOf("_") >= 0) {

            String md5 = md5(doorId + i);
            if (md5.startsWith("00000")) {
                int idx = md5.charAt(5) - '0';
                if (idx < acc.length() && acc.charAt(idx) == '_') {
                    acc.setCharAt(idx, md5.charAt(6));
                }
            }

            if (++i == Integer.MAX_VALUE) {
                throw new IllegalStateException();
            }
        }
        return acc.toString().toLowerCase();
    }

    private static String genPassword(String doorId) {
        StringBuilder acc = new StringBuilder();
        int i = 0;
        while (acc.length() < 8) {

            String md5 = md5(doorId + i);
            if (md5.startsWith("00000")) {
                acc.append(md5.charAt(5));
            }

            if (++i == Integer.MAX_VALUE) {
                throw new IllegalStateException();
            }
        }
        return acc.toString().toLowerCase();
    }

    @SneakyThrows
    private static String md5(String x) {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] digest = md5.digest(x.getBytes(StandardCharsets.UTF_8));
        return new String(Hex.encodeHex(digest, false));
    }

}

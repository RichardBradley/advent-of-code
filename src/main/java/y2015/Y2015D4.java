package y2015;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import static com.google.common.truth.Truth.assertThat;

public class Y2015D4 {
    public static void main(String[] args) throws Exception {

        // 1
        assertThat(mine5("abcdef")).isEqualTo(609043);
        assertThat(mine5("pqrstuv")).isEqualTo(1048970);

        System.out.println(mine5("yzbqklnj"));

        // 2
        System.out.println(mine6("yzbqklnj"));
    }

    private static int mine5(String key) throws Exception {
        MessageDigest md5 = MessageDigest.getInstance("MD5");

        for (int i=0; ; i++) {
            md5.reset();
            byte[] hash = md5.digest((key + i).getBytes(StandardCharsets.UTF_8));
            // 5 leading zeroes in hex:
            if (hash[0] == 0 && hash[1] == 0 && (hash[2] & 0xF0) == 0) {
                return i;
            }
        }
    }

    private static int mine6(String key) throws Exception {
        MessageDigest md5 = MessageDigest.getInstance("MD5");

        for (int i=0; ; i++) {
            md5.reset();
            byte[] hash = md5.digest((key + i).getBytes(StandardCharsets.UTF_8));
            // 5 leading zeroes in hex:
            if (hash[0] == 0 && hash[1] == 0 && hash[2] == 0) {
                return i;
            }
        }
    }
}

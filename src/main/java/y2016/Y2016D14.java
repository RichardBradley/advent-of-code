package y2016;

import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Hex;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

public class Y2016D14 {

    private static MessageDigest md5;

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        md5 = MessageDigest.getInstance("MD5");

        // 1
        assertThat(getIndexOfNthKey("abc", 64, false)).isEqualTo(22728);
        System.out.println("example ok");

        System.out.println(getIndexOfNthKey("ihaygndm", 64, false));

        // 2
        assertThat(hash("abc", 0, true)).isEqualTo("a107ff634856bb300138cac6568c0f24");
        assertThat(getIndexOfNthKey("abc", 64, true)).isEqualTo(22551);
        System.out.println("example ok");

        System.out.println(getIndexOfNthKey("ihaygndm", 64, true));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    @SneakyThrows
    private static int getIndexOfNthKey(String salt, int nth, boolean ver2) {
        // Simpler to waste memory than try to get a circular buffer exactly right here...
        List<String> hashesByIdx = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            hashesByIdx.add(hash(salt, i, ver2));
        }

        outer:
        for (int idx = 0; ; idx++) {

            int nextFutureHashIdx = hashesByIdx.size();
            hashesByIdx.add(hash(salt, nextFutureHashIdx, ver2));

            // A hash is a key only if:
            // It contains three of the same character in a row, like 777. Only consider the first such triplet in a hash.
            // One of the next 1000 hashes in the stream contains that same character five times in a row, like 77777.
            String searchKey = findSearchKeyFromFirstTriplet(hashesByIdx.get(idx));
            if (searchKey != null) {
                for (int i = idx + 1; i <= idx + 1000; i++) {
                    if (hashesByIdx.get(i).contains(searchKey)) {
                        if ((--nth) == 0) {
                            return idx;
                        }
                        continue outer;
                    }
                }
            }
        }
    }

    private static String hash(String salt, int i, boolean ver2) {
        String currHash = (salt + i);
        int hashCount = ver2 ? 2017 : 1;
        for (int j = 0; j < hashCount; j++) {
            currHash = Hex.encodeHexString(md5.digest(currHash.getBytes(StandardCharsets.UTF_8)));
        }
        return currHash;
    }

    private static String findSearchKeyFromFirstTriplet(String str) {
        for (int i = 0; i < str.length() - 2; i++) {
            char c = str.charAt(i);
            if (str.charAt(i + 1) == c && str.charAt(i + 2) == c) {
                return Strings.repeat(c + "", 5);
            }
        }
        return null;
    }
}

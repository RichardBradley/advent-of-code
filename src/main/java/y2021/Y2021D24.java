package y2021;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

public class Y2021D24 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            List<String> input = Resources.readLines(Resources.getResource("y2021/Y2021D24.txt"), StandardCharsets.UTF_8);
            // You got rank 405 on this star's leaderboard.
            // You got rank 334 on this star's leaderboard.

            // part 1
            assertThat(evalModelNo(new ModelNo("94992994195998")))
                    .isEqualTo(true);

            // part 2
            assertThat(evalModelNo(new ModelNo("21191861151161")))
                    .isEqualTo(true);

            explore2("");
            System.out.println("countZero=" + countZero);
            System.out.println("countNz=" + countNz);

        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private static void explore2(String candidate) {
        for (int i = 1; i <= 9; i++) {
            String nextCand = candidate + i;
            try {
                switch (evalModelNo_simplified(new ModelNo(nextCand))) {
                    case Results.ADD_A_DIGIT:
                        explore2(nextCand);
                        break;
                    case Results.INVALID:
                        continue;
                    case Results.ALL_OK:
                        System.out.println("OK: " + nextCand);
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
            } catch (NeedMoreDigitsEx e) {
                explore2(nextCand);
            }
        }
    }

    static class NeedMoreDigitsEx extends Exception {}

    static long countZero;
    static long countNz;

    static class Results {
        final static int ADD_A_DIGIT = 1;
        final static int INVALID = 2;
        final static int ALL_OK = 3;
    }

    private static int evalModelNo_simplified(ModelNo m) throws NeedMoreDigitsEx {
        long w = 0, x = 0, y = 0, z = 0;

        z = m.nextDigit() * 26;
        y = m.nextDigit() + 6;
        z = z + y;

        z = z * 26;
        y = m.nextDigit() + 4;
        z = z + y;

        w = m.nextDigit(); // 55
        y = 26;
        z = z * y;
        y = w + 2;
        z = z + y;

        w = m.nextDigit(); // 73
        y = 26;
        z = z * y;
        y = w + 9;
        z = z + y;


        w = m.nextDigit(); // 91
        x = z % 26;
        z = z / 26;
        x = x + -2;
        x = (x == w) ? 0 : 1;

        if (x != 0) {
            return Results.INVALID;
        }

//        if (x == 0) {
//            countZero++;
//        } else {
//            countNz++;
//        }
////        // qqq
////        System.out.println(String.format(
////                "%s, %s, %s, %s",
////                w, x, y, z));
//        if (System.currentTimeMillis() > 0) return false;


        y = 25 * x + 1;
        z = z * y;
        y = w;
        y = y + 1;
        y = y * x;
        z = z + y;

        w = m.nextDigit(); // 109
        x = z % 26 + 11;
        x = (x == w) ? 0 : 1;

        if (x != 1) {
            return Results.INVALID;
        }

        y = 25;
        y = y * x;
        y = y + 1;
        z = z * y;
        y = w;
        y = y + 10;
        y = y * x;
        z = z + y;

        w = m.nextDigit(); // 127
        x = z;
        x = x % 26;
        z = z / 26;
        x = x + -15;
        x = (x == w) ? 0 : 1;


        // some of each here, fewer zero
        if (x != 0) {
            return Results.INVALID;
        }

        y = 25 * x + 1;
        z = z * y;
        y = w;
        y = y + 6;
        y = y * x;
        z = z + y;
        w = m.nextDigit(); // 145
        x = z % 26;
        z = z / 26;
        x = x + -10;
        x = (x == w) ? 0 : 1;


        if (x != 0) {
            return Results.INVALID;
        }



        y = 25;
        y = y * x;
        y = y + 1;
        z = z * y;
        y = w;
        y = y + 4;
        y = y * x;
        z = z + y;
        w = m.nextDigit(); // 163
        x = z;
        x = x % 26;
        x = x + 10;
        x = (x == w) ? 0 : 1;

        if (x != 1) {
            return Results.INVALID;
        }

        y = 25;
        y = y * x;
        y = y + 1;
        z = z * y;
        y = w;
        y = y + 6;
        y = y * x;
        z = z + y;
        w = m.nextDigit(); // 181
        x = z;
        x = x % 26;
        z = z / 26;
        x = x + -10;
        x = (x == w) ? 0 : 1;


        if (x != 0) {
            return Results.INVALID;
        }

        y = 25;
        y = y * x;
        y = y + 1;
        z = z * y;
        y = w;
        y = y + 3;
        y = y * x;
        z = z + y;
        w = m.nextDigit(); // 199
        x = z;
        x = x % 26;
        z = z / 26;
        x = x + -4;
        x = (x == w) ? 0 : 1;

        if (x == 0) {
            countZero++;
        } else {
            countNz++;
        }
        // if (true) return Results.INVALID;


        if (x != 0) {
            return Results.INVALID;
        }

        y = 25;
        y = y * x;
        y = y + 1;
        z = z * y;
        y = w;
        y = y + 9;
        y = y * x;
        z = z + y;

        w = m.nextDigit(); // 217
        x = z;
        x = x % 26;
        z = z / 26;
        x = x + -1;
        x = (x == w) ? 0 : 1;

        if (x != 0) {
            return Results.INVALID;
        }

        y = 25;
        y = y * x;
        y = y + 1;
        z = z * y;
        y = w;
        y = y + 15;
        y = y * x;
        z = z + y;


        w = m.nextDigit(); // 235
        x = z % 26 - 1;
        z = z / 26;
        x = (x == w) ? 0 : 1; // need x == w
        y = 25 * x + 1; // y is 1 or 26
        z = z * y;
        y = (w + 5) * x; // need x negative or zero
        z = z + y; // need z = -y,  y = -z


        if (z == 0) {
            return Results.ALL_OK;
        } else {
            return Results.INVALID;
        }
    }

    private static boolean evalModelNo(ModelNo m) throws NeedMoreDigitsEx {
        long w = 0, x = 0, y = 0, z = 0;

        w = m.nextDigit();
        x = x * 0;
        x = x + z;
        x = x % 26;
        z = z / 1;
        x = x + 10;
        x = (x == w) ? 1 : 0;
        x = (x == 0) ? 1 : 0;
        y = y * 0;
        y = y + 25;
        y = y * x;
        y = y + 1;
        z = z * y;
        y = y * 0;
        y = y + w;
        y = y + 0;
        y = y * x;
        z = z + y;
        w = m.nextDigit();
        x = x * 0;
        x = x + z;
        x = x % 26;
        z = z / 1;
        x = x + 12;
        x = (x == w) ? 1 : 0;
        x = (x == 0) ? 1 : 0;
        y = y * 0;
        y = y + 25;
        y = y * x;
        y = y + 1;
        z = z * y;
        y = y * 0;
        y = y + w;
        y = y + 6;
        y = y * x;
        z = z + y;
        w = m.nextDigit();
        x = x * 0;
        x = x + z;
        x = x % 26;
        z = z / 1;
        x = x + 13;
        x = (x == w) ? 1 : 0;
        x = (x == 0) ? 1 : 0;
        y = y * 0;
        y = y + 25;
        y = y * x;
        y = y + 1;
        z = z * y;
        y = y * 0;
        y = y + w;
        y = y + 4;
        y = y * x;
        z = z + y;
        w = m.nextDigit();
        x = x * 0;
        x = x + z;
        x = x % 26;
        z = z / 1;
        x = x + 13;
        x = (x == w) ? 1 : 0;
        x = (x == 0) ? 1 : 0;
        y = y * 0;
        y = y + 25;
        y = y * x;
        y = y + 1;
        z = z * y;
        y = y * 0;
        y = y + w;
        y = y + 2;
        y = y * x;
        z = z + y;
        w = m.nextDigit();
        x = x * 0;
        x = x + z;
        x = x % 26;
        z = z / 1;
        x = x + 14;
        x = (x == w) ? 1 : 0;
        x = (x == 0) ? 1 : 0;
        y = y * 0;
        y = y + 25;
        y = y * x;
        y = y + 1;
        z = z * y;
        y = y * 0;
        y = y + w;
        y = y + 9;
        y = y * x;
        z = z + y;
        w = m.nextDigit();
        x = x * 0;
        x = x + z;
        x = x % 26;
        z = z / 26;
        x = x + -2;
        x = (x == w) ? 1 : 0;
        x = (x == 0) ? 1 : 0;
        y = y * 0;
        y = y + 25;
        y = y * x;
        y = y + 1;
        z = z * y;
        y = y * 0;
        y = y + w;
        y = y + 1;
        y = y * x;
        z = z + y;
        w = m.nextDigit();
        x = x * 0;
        x = x + z;
        x = x % 26;
        z = z / 1;
        x = x + 11;
        x = (x == w) ? 1 : 0;
        x = (x == 0) ? 1 : 0;
        y = y * 0;
        y = y + 25;
        y = y * x;
        y = y + 1;
        z = z * y;
        y = y * 0;
        y = y + w;
        y = y + 10;
        y = y * x;
        z = z + y;
        w = m.nextDigit();
        x = x * 0;
        x = x + z;
        x = x % 26;
        z = z / 26;
        x = x + -15;
        x = (x == w) ? 1 : 0;
        x = (x == 0) ? 1 : 0;
        y = y * 0;
        y = y + 25;
        y = y * x;
        y = y + 1;
        z = z * y;
        y = y * 0;
        y = y + w;
        y = y + 6;
        y = y * x;
        z = z + y;
        w = m.nextDigit();
        x = x * 0;
        x = x + z;
        x = x % 26;
        z = z / 26;
        x = x + -10;
        x = (x == w) ? 1 : 0;
        x = (x == 0) ? 1 : 0;
        y = y * 0;
        y = y + 25;
        y = y * x;
        y = y + 1;
        z = z * y;
        y = y * 0;
        y = y + w;
        y = y + 4;
        y = y * x;
        z = z + y;
        w = m.nextDigit();
        x = x * 0;
        x = x + z;
        x = x % 26;
        z = z / 1;
        x = x + 10;
        x = (x == w) ? 1 : 0;
        x = (x == 0) ? 1 : 0;
        y = y * 0;
        y = y + 25;
        y = y * x;
        y = y + 1;
        z = z * y;
        y = y * 0;
        y = y + w;
        y = y + 6;
        y = y * x;
        z = z + y;
        w = m.nextDigit();
        x = x * 0;
        x = x + z;
        x = x % 26;
        z = z / 26;
        x = x + -10;
        x = (x == w) ? 1 : 0;
        x = (x == 0) ? 1 : 0;
        y = y * 0;
        y = y + 25;
        y = y * x;
        y = y + 1;
        z = z * y;
        y = y * 0;
        y = y + w;
        y = y + 3;
        y = y * x;
        z = z + y;
        w = m.nextDigit();
        x = x * 0;
        x = x + z;
        x = x % 26;
        z = z / 26;
        x = x + -4;
        x = (x == w) ? 1 : 0;
        x = (x == 0) ? 1 : 0;
        y = y * 0;
        y = y + 25;
        y = y * x;
        y = y + 1;
        z = z * y;
        y = y * 0;
        y = y + w;
        y = y + 9;
        y = y * x;
        z = z + y;
        w = m.nextDigit();
        x = x * 0;
        x = x + z;
        x = x % 26;
        z = z / 26;
        x = x + -1;
        x = (x == w) ? 1 : 0;
        x = (x == 0) ? 1 : 0;
        y = y * 0;
        y = y + 25;
        y = y * x;
        y = y + 1;
        z = z * y;
        y = y * 0;
        y = y + w;
        y = y + 15;
        y = y * x;
        z = z + y;
        w = m.nextDigit();
        x = x * 0;
        x = x + z;
        x = x % 26;
        z = z / 26;
        x = x + -1;
        x = (x == w) ? 1 : 0;
        x = (x == 0) ? 1 : 0;
        y = y * 0;
        y = y + 25;
        y = y * x;
        y = y + 1;
        z = z * y;
        y = y * 0;
        y = y + w;
        y = y + 5;
        y = y * x;
        z = z + y;


        return z == 0;
    }

    static class ModelNo {
        String val;
        int idx = 0;

        public ModelNo(String val) {
            this.val = val;
        }

        long nextDigit() throws NeedMoreDigitsEx {
            if (!hasNext()) {
                throw new NeedMoreDigitsEx();
            }
            return val.charAt(idx++) - '0';
        }

        boolean hasNext() {
            return idx <= (val.length() -1);
        }
    }
}

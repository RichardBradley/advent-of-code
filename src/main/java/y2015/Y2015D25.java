package y2015;

import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

public class Y2015D25 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(getCode(6, 6))
                .isEqualTo(27995004);
        assertThat(getCode(6, 5))
                .isEqualTo(31663883);

        System.out.println("Example took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");

        System.out.println(getCode(3029, 2947));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");

    }

    static long getCode(int targetX, int targetY) {
        long currentCode = 20151125;
        for (int startY = 2; ; startY++) {
            for (int i = 0; i < startY; i++) {

                long nextCode = (currentCode * 252533) % 33554393;

                int y = startY - i;
                int x = 1 + i;

                if (y == targetY && x == targetX) {
                    return nextCode;
                } else {
                    currentCode = nextCode;
                }
            }
        }
    }
}

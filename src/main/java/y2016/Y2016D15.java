package y2016;

import com.google.common.base.Stopwatch;
import lombok.SneakyThrows;
import lombok.Value;

import java.security.MessageDigest;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.truth.Truth.assertThat;

public class Y2016D15 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(getFirstValidCapsuleStart(exampleInput)).isEqualTo(5);
        System.out.println("example ok");

        System.out.println(getFirstValidCapsuleStart(input));

        // 2
        System.out.println(getFirstValidCapsuleStart(input2));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    @SneakyThrows
    private static int getFirstValidCapsuleStart(World world) {
        tloop:
        for (int t = 0; ; t++) {
            for (int i = 0; i < world.discSizes.length; i++) {
                // time at disk (i + 1) is t + i + 1
                checkArgument((t + i + 1 + world.discOffsets[i]) % world.discSizes[i] >= 0);
                if ((t + i + 1 + world.discOffsets[i]) % world.discSizes[i] != 0) {
                    continue tloop;
                }
            }
            return t;
        }
    }

    @Value
    static class World {
        int[] discSizes;
        int[] discOffsets;
    }

    private static World parse(String... spec) {
        int count = spec.length;
        int[] discSizes = new int[count];
        int[] discOffsets = new int[count];
        Pattern p = Pattern.compile("Disc #(\\d) has (\\d+) positions; at time=0, it is at position (\\d+)\\.");
        for (int i = 0; i < count; i++) {
            Matcher matcher = p.matcher(spec[i]);
            checkArgument(matcher.matches());
            checkArgument(i + 1 == Integer.parseInt(matcher.group(1)));
            discSizes[i] = Integer.parseInt(matcher.group(2));
            discOffsets[i] = Integer.parseInt(matcher.group(3));
        }
        return new World(discSizes, discOffsets);
    }

    static World exampleInput = parse(
            "Disc #1 has 5 positions; at time=0, it is at position 4.",
            "Disc #2 has 2 positions; at time=0, it is at position 1.");

    static World input = parse(
            "Disc #1 has 17 positions; at time=0, it is at position 5.",
            "Disc #2 has 19 positions; at time=0, it is at position 8.",
            "Disc #3 has 7 positions; at time=0, it is at position 1.",
            "Disc #4 has 13 positions; at time=0, it is at position 7.",
            "Disc #5 has 5 positions; at time=0, it is at position 1.",
            "Disc #6 has 3 positions; at time=0, it is at position 0."
    );

    static World input2 = parse(
            "Disc #1 has 17 positions; at time=0, it is at position 5.",
            "Disc #2 has 19 positions; at time=0, it is at position 8.",
            "Disc #3 has 7 positions; at time=0, it is at position 1.",
            "Disc #4 has 13 positions; at time=0, it is at position 7.",
            "Disc #5 has 5 positions; at time=0, it is at position 1.",
            "Disc #6 has 3 positions; at time=0, it is at position 0.",
            "Disc #7 has 11 positions; at time=0, it is at position 0."
    );
}

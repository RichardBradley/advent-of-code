package y2017;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2017D09 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        assertThat(scoreGroup("{}")).isEqualTo(1);
        assertThat(scoreGroup("{{{}}}")).isEqualTo(6);
        assertThat(scoreGroup("{{{},{},{{}}}}")).isEqualTo(16);
        assertThat(scoreGroup("{{<!!>},{<!!>},{<!!>},{<!!>}}")).isEqualTo(9);
        assertThat(scoreGroup("{{<a!>},{<a!>},{<a!>},{<ab>}}")).isEqualTo(3);
        String input = Resources.toString(Resources.getResource("y2017/Y2017D09.txt"), StandardCharsets.UTF_8);
        assertThat(scoreGroup(input)).isEqualTo(9251);
        assertThat(part2(input)).isEqualTo(4322);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static int part2(String input) {
        boolean inGarbage = false;
        int garbageCount = 0;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (!inGarbage) {
                if (c == '<') {
                    inGarbage = true;
                }
            } else {
                if (c == '!') {
                    i++;
                } else if (c == '>') {
                    inGarbage = false;
                } else {
                    garbageCount++;
                }
            }
        }
        return garbageCount;
    }

    private static int scoreGroup(String input) throws IOException {
        PushbackReader in = new PushbackReader(new StringReader(input));
        int score = parseGroup(in, 0);
        checkState(in.read() == -1);
        return score;
    }

    private static int parseGroup(PushbackReader in, int groupDepth) throws IOException {
        // group starts
        int next = in.read();
        checkState(next == '{');

        groupDepth++;
        int thisScore = groupDepth;

        // zero or more other things, separated by commas
        outer:
        while (true) {
            next = in.read();
            switch (next) {
                case '{':
                    in.unread(next);
                    thisScore += parseGroup(in, groupDepth);
                    break;
                case '<':
                    readGarbage(in);
                    break;
                case '}':
                    break outer;
                default:
                    throw new IllegalArgumentException("" + (char)next);
            }

            next = in.read();
            if (next != ',') {
                break;
            }
        }

        checkState(next == '}');
        return thisScore;
    }

    private static void readGarbage(PushbackReader in) throws IOException {
        while (true) {
            int next = in.read();
            switch (next) {
                case '>':
                    return;
                case '!':
                    checkState(0 < in.read());
                    break;
                default:
                    break;
            }
        }
    }
}


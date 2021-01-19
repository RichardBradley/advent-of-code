package y2017;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Y2017D4 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        System.out.println(Resources.readLines(Resources.getResource("y2017/Y2017D04.txt"), StandardCharsets.UTF_8).stream()
                .filter(Y2017D4::isValidPassphrase)
                .count());

        System.out.println(Resources.readLines(Resources.getResource("y2017/Y2017D04.txt"), StandardCharsets.UTF_8).stream()
                .filter(Y2017D4::isValidPassphrase2)
                .count());

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static boolean isValidPassphrase(String p) {
        Set<String> words = new HashSet<>();
        for (String s : Splitter.on(" ").split(p)) {
            if (!words.add(s)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isValidPassphrase2(String p) {
        Set<String> words = new HashSet<>();
        for (String word : Splitter.on(" ").split(p)) {
            String lettersSortedWord = new String(word.chars().sorted().toArray(), 0, word.length());
            if (!words.add(lettersSortedWord)) {
                return false;
            }
        }
        return true;
    }
}

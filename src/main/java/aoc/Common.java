package aoc;

import com.google.common.io.Resources;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Common {
    public static List<String> loadInputFromResources() throws IOException {
        String className = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .getCallerClass().getName();
        String resourceName = className.replace('.', '/') + ".txt";
        return Resources.readLines(
                Resources.getResource(resourceName),
                StandardCharsets.UTF_8);
    }
}

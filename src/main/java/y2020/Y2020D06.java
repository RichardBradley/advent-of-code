package y2020;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Y2020D06 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        String input = Resources.toString(Resources.getResource("y2020/Y2020D06.txt"), StandardCharsets.UTF_8);

        part1(input);
        part2(input);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static void part1(String input) {
        Iterable<String> groups = Splitter.on("\n\n").split(input);
        int sumOfQuestionCounts = StreamSupport.stream(groups.spliterator(), false)
                .mapToInt(group -> (int) group.chars().filter(c -> 'a' <= c && 'z' >= c).distinct().count())
                .sum();

        System.out.println("sumOfQuestionCounts = " + sumOfQuestionCounts);
    }

    private static void part2(String input) {
        Iterable<String> groups = Splitter.on("\n\n").split(input);
        int sumOfAllYesQuestionCounts = StreamSupport.stream(groups.spliterator(), false)
                .mapToInt(group -> {
                    List<Set<Integer>> answers = StreamSupport.stream(Splitter.on("\n").split(group).spliterator(), false)
                            .map(line -> line.chars().boxed().collect(Collectors.toSet()))
                            .collect(Collectors.toList());

                    return answers.stream().reduce((a, b) -> Sets.intersection(a, b))
                            .get().size();
                })
                .sum();

        System.out.println("sumOfAllYesQuestionCounts = " + sumOfAllYesQuestionCounts);
    }
}

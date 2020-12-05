package y2020;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.google.common.base.Preconditions.checkState;

public class Y2020D04 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        String input = Resources.toString(Resources.getResource("y2020/Y2020D04.txt"), StandardCharsets.UTF_8);
        part1(input);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static void part1(String input) {
        Map<String, Function<String, Boolean>> fieldValidations = new HashMap<>();
        fieldValidations.put(
                "byr", (String s) -> {
                    int year = Integer.parseInt(s);
                    return s.length() == 4 && year >= 1920 && year <= 2002;
                });
        fieldValidations.put(
                "iyr", (String s) -> {
                    int year = Integer.parseInt(s);
                    return s.length() == 4 && year >= 2010 && year <= 2020;
                });
        fieldValidations.put(
                "eyr", (String s) -> {
                    int year = Integer.parseInt(s);
                    return s.length() == 4 && year >= 2020 && year <= 2030;
                });
        fieldValidations.put(
                "hgt", (String s) -> {
                    if (s.endsWith("cm")) {
                        int cm = Integer.parseInt(s.substring(0, s.length() - 2));
                        return cm >= 150 && cm <= 193;
                    } else if (s.endsWith("in")) {
                        int in = Integer.parseInt(s.substring(0, s.length() - 2));
                        return in >= 59 && in <= 76;
                    } else {
                        return false;
                    }
                });
        fieldValidations.put(
                "hcl", (String s) -> Pattern.matches("#[0-9a-f]{6}", s));
        fieldValidations.put(
                "ecl", (String s) -> Pattern.matches("amb|blu|brn|gry|grn|hzl|oth", s));
        fieldValidations.put(
                "pid", (String s) -> Pattern.matches("[0-9]{9}", s));

        AtomicInteger allFieldsPresentCount = new AtomicInteger();
        AtomicInteger validCount = new AtomicInteger();

        Splitter.on("\n\n").split(input).forEach(passport -> {
            Map<String, String> fields = StreamSupport.stream(Splitter.on(Pattern.compile("[ \n]")).trimResults().split(passport).spliterator(), false)
                    .map(field -> {
                        String[] keyName = field.split(":");
                        checkState(keyName.length == 2);
                        return keyName;
                    })
                    .collect(Collectors.toMap(p -> p[0], p -> p[1]));

            boolean hasAllFields = fieldValidations.keySet().stream().allMatch(requiredField -> fields.containsKey(requiredField));
            if (hasAllFields) {
                allFieldsPresentCount.incrementAndGet();
            }

            if (hasAllFields && fieldValidations.entrySet().stream().allMatch(e -> {
                try {
                    return e.getValue().apply(fields.getOrDefault(e.getKey(), ""));
                } catch (Exception ex) {
                    return false;
                }
            })) {
                validCount.incrementAndGet();
            }
        });

        System.out.println("allFieldsPresentCount = " + allFieldsPresentCount);
        System.out.println("validCount = " + validCount);
    }
}

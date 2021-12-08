package y2021;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2021D08 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            List<String> input = Resources.readLines(Resources.getResource("y2021/Y2021D08.txt"), StandardCharsets.UTF_8);

            // 1
            assertThat(part1(example)).isEqualTo(26);
            assertThat(part1(input)).isEqualTo(554);

            // 2
            assertThat(part2(List.of("acedgfb cdfbe gcdfa fbcad dab cefabd cdfgeb eafb cagedb ab | cdfeb fcadb cdfeb cdbaf"))).isEqualTo(5353);
            assertThat(part2(example)).isEqualTo(61229);
            assertThat(part2(input)).isEqualTo(-1);

        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private static long part1(List<String> input) {
        // In the output values, how many times do digits 1, 4, 7, or 8 appear?
        int count = 0;
        for (String line : input) {
            String[] inOut = line.split(" \\| ");
            checkState(inOut.length == 2);
            String[] in = inOut[0].split(" ");
            checkState(in.length == 10);
            String[] out = inOut[1].split(" ");
            checkState(out.length == 4);

            for (String s : out) {
                if (s.length() == 2 || s.length() == 3 || s.length() == 4 || s.length() == 7) {
                    count++;
                }
            }
        }
        return count;
    }

    private static long part2(List<String> input) {
        // What do you get if you add up all of the output values?
        int sum = 0;
        for (String line : input) {
            String[] inOut = line.split(" \\| ");
            checkState(inOut.length == 2);
            String[] in = inOut[0].split(" ");
            checkState(in.length == 10);
            String[] out = inOut[1].split(" ");
            checkState(out.length == 4);

            Map<Character, Character> mapping = deduceMapping(in);
            checkNotNull(mapping, "no mapping found");
            // out are digits in a 4 dig number
            sum += Integer.parseInt(
                    Arrays.stream(out)
                            .map(x -> Integer.toString(eval(x, mapping)))
                            .collect(Collectors.joining()));
        }
        return sum;
    }

    private static List<Character> allChars = List.of('a', 'b', 'c', 'd', 'e', 'f', 'g');

    private static Map<Character, Character> deduceMapping(String[] in) {
        Map<Character, Set<Character>> possibilities = new HashMap<>();
        for (char c = 'a'; c <= 'g'; c++) {
            possibilities.put(c, new HashSet<>(allChars));
        }

        for (String output : in) {
            if (output.length() == 2) {
                // Only digit could be 1, cf
                for (char c : output.toCharArray()) {
                    possibilities.get(c).retainAll(List.of('c', 'f'));
                }
            } else if (output.length() == 3) {
                for (char c : output.toCharArray()) {
                    possibilities.get(c).retainAll(List.of('a', 'c', 'f'));
                }
            } else if (output.length() == 4) {
                for (char c : output.toCharArray()) {
                    possibilities.get(c).retainAll(List.of('b', 'c', 'd', 'f'));
                }
            } else if (output.length() == 7) {
                // no actual info...
            }
        }

        // Guess combos:
        return deduceMapping('a', possibilities, new HashMap<>(), in);
    }

    static Set<String> valids = Set.of(
            "abcefg", "cf", "acdeg", "acdfg", "bcdf", "abdfg", "abdefg", "acf", "abcdefg", "abcdfg");

    private static Map<Character, Character> deduceMapping(
            char currChar,
            Map<Character, Set<Character>> possibilities,
            Map<Character, Character> currMapping,
            String[] in) {

        if (currChar == 'h') {
            // if currMapping gives only valid numbers for in, then could be valid
            boolean valid = true;
            for (String s : in) {
                StringBuilder acc = new StringBuilder();
                s.chars().map(c -> currMapping.get((char) c)).sorted().forEach(c -> acc.append((char) c));
                String mapped = acc.toString();
                valid &= valids.contains(mapped);
            }
            return valid ? currMapping : null;
        } else {
            Map<Character, Character> ret = null;
            possCharLoop:
            for (Character possMappedChar : possibilities.get(currChar)) {
                // check not previously used
                for (char c = 'a'; c < currChar; c++) {
                    if (currMapping.get(c) == possMappedChar) {
                        continue possCharLoop;
                    }
                }

                currMapping.put(currChar, possMappedChar);
                Map<Character, Character> inner = deduceMapping((char) (currChar + 1), possibilities, currMapping, in);
                if (inner != null) {
                    checkState(ret == null, "ambiguous");
                    ret = new HashMap<>(inner);
                }
            }
            return ret;
        }
    }

    private static int eval(String s, Map<Character, Character> mapping) {
        StringBuilder acc = new StringBuilder();
        s.chars().map(c -> mapping.get((char) c)).sorted().forEach(c -> acc.append((char) c));
        switch (acc.toString()) {
            case "abcefg":
                return 0;
            case "cf":
                return 1;
            case "acdeg":
                return 2;
            case "acdfg":
                return 3;
            case "bcdf":
                return 4;
            case "abdfg":
                return 5;
            case "abdefg":
                return 6;
            case "acf":
                return 7;
            case "abcdefg":
                return 8;
            case "abcdfg":
                return 9;
            default:
                throw new IllegalArgumentException();
        }
    }

    private static List<String> example = List.of(
            "be cfbegad cbdgef fgaecd cgeb fdcge agebfd fecdb fabcd edb | fdgacbe cefdb cefbgd gcbe",
            "edbfga begcd cbg gc gcadebf fbgde acbgfd abcde gfcbed gfec | fcgedb cgb dgebacf gc",
            "fgaebd cg bdaec gdafb agbcfd gdcbef bgcad gfac gcb cdgabef | cg cg fdcagb cbg",
            "fbegcd cbd adcefb dageb afcb bc aefdc ecdab fgdeca fcdbega | efabcd cedba gadfec cb",
            "aecbfdg fbg gf bafeg dbefa fcge gcbea fcaegb dgceab fcbdga | gecf egdcabf bgf bfgea",
            "fgeab ca afcebg bdacfeg cfaedg gcfdb baec bfadeg bafgc acf | gebdcfa ecba ca fadegcb",
            "dbcfg fgd bdegcaf fgec aegbdf ecdfab fbedc dacgb gdcebf gf | cefg dcbef fcge gbcadfe",
            "bdfegc cbegaf gecbf dfcage bdacg ed bedf ced adcbefg gebcd | ed bcgafe cdgba cbgef",
            "egadfb cdbfeg cegd fecab cgb gbdefca cg fgcdab egfdb bfceg | gbdfcae bgc cg cgb",
            "gcafb gcf dcaebfg ecagb gf abcdeg gaef cafbge fdbac fegbdc | fgae cfgab fg bagce");

}

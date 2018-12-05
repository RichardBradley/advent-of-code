package y2018.day2;

import java.util.Map;
import java.util.stream.Collectors;

public class Q1j {

    public static void main(String[] args) {
        int countHasLetterTwice = 0;
        int countHasLetterThreeTimes = 0;

        for (String id : Input$.MODULE$.ids()) {

            Map<Integer, Long> countsByLetter = id.chars().boxed().collect(
                    Collectors.groupingBy(x -> x, Collectors.counting()));

            boolean hasLetterTwice = false;
            boolean hasLetterThreeTimes = false;
            for (Map.Entry<Integer, Long> countByLetter : countsByLetter.entrySet()) {
                if (countByLetter.getValue() == 2L) {
                    hasLetterTwice = true;
                }
                if (countByLetter.getValue() == 3L) {
                    hasLetterThreeTimes = true;
                }
            }

            if (hasLetterTwice) {
                countHasLetterTwice++;
            }
            if (hasLetterThreeTimes) {
                countHasLetterThreeTimes++;
            }
        }

        System.out.println("Checksum = " + countHasLetterTwice * countHasLetterThreeTimes);
    }

    private static String[] test = new String[]{
            "abcdef",
            "bababc",
            "abbcde",
            "abcccd",
            "aabcdd",
            "abcdee",
            "ababab"
    };
}
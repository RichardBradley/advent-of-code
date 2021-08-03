package y2015;

import static com.google.common.truth.Truth.assertThat;

public class Y2015D10 {
    public static void main(String[] args) throws Exception {

        // 1
        assertThat(lookAndSay("1", 5)).isEqualTo("312211");

        System.out.println(lookAndSay("3113322113", 40).length());

        // 2
        System.out.println(lookAndSay("3113322113", 50).length());
    }

    private static String lookAndSay(String input, int repetitions) {
        for (int i = 0; i < repetitions; i++) {
            input = lookAndSay(input);
        }
        return input;
    }

    private static String lookAndSay(String input) {
        StringBuilder acc = new StringBuilder();
        char lastDigit = '\0';
        int lastDigitRepCount = 0;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != lastDigit) {
                if (lastDigitRepCount > 0) {
                    acc.append("" + lastDigitRepCount);
                    acc.append(lastDigit);
                }
                lastDigit = c;
                lastDigitRepCount = 1;
            } else {
                lastDigitRepCount++;
            }
        }
        if (lastDigitRepCount > 0) {
            acc.append("" + lastDigitRepCount);
            acc.append(lastDigit);
        }

        return acc.toString();
    }
}

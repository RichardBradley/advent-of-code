package y2015;

import static com.google.common.truth.Truth.assertThat;

public class Y2015D11 {
    public static void main(String[] args) throws Exception {

        // 1
        assertThat(isValidPassword("hijklmmn")).isEqualTo(false);
        assertThat(isValidPassword("abbceffg")).isEqualTo(false);
        assertThat(isValidPassword("abbcegjk")).isEqualTo(false);

        assertThat(nextPassword("abcdefgh")).isEqualTo("abcdffaa");
        assertThat(nextPassword("ghijklmn")).isEqualTo("ghjaabcc");

        String nextPassword = nextPassword("hxbxwxba");
        System.out.println(nextPassword);

        // 2
        System.out.println(nextPassword(nextPassword));
    }

    private static String nextPassword(String pw) {
        char[] chars = pw.toCharArray();
        do {
            increment(chars);
        } while (!isValidPassword(chars));
        return new String(chars);
    }

    private static void increment(char[] p) {
        p[p.length-1]++;
        for (int i=p.length-1; i>0; i--) {
            if (p[i] == ('z' + 1)) {
                p[i] = 'a';
                p[i-1]++;
            }
        }
    }

    private static boolean isValidPassword(String p) {
        return isValidPassword(p.toCharArray());
    }
    
    private static boolean isValidPassword(char[] p) {
        // Passwords must include one increasing straight of at least three letters,
        // like abc, bcd, cde, and so on, up to xyz. They cannot skip letters; abd doesn't count.
        boolean valid = false;
        for (int i = 0; i < p.length - 2; i++) {
            if (p[i] + 1 == p[i + 1] && p[i] + 2 == p[i + 2]) {
                valid = true;
                break;
            }
        }
        if (!valid) return false;

        // Passwords may not contain the letters i, o, or l, as these letters can be mistaken
        // for other characters and are therefore confusing.
        for (char c : p) {
            if (c == 'i' || c == 'o' || c == 'l') {
                return false;
            }
        }

        // Passwords must contain at least two different, non-overlapping pairs of letters,
        // like aa, bb, or zz.
        char firstPair = '\0';
        for (int i = 0; i < p.length - 1; i++) {
            if (firstPair == '\0') {
                if (p[i] == p[i + 1]) {
                    firstPair = p[i];
                    i++;
                }
            } else if (p[i] != firstPair && p[i] == p[i + 1]) {
                return true;
            }
        }
        return false;
    }
}

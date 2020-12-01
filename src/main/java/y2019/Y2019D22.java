package y2019;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.System.out;

public class Y2019D22 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(toString(shuffle("deal into new stack", 10))).isEqualTo("9 8 7 6 5 4 3 2 1 0");
        assertThat(toString(shuffle("cut 3", 10))).isEqualTo("3 4 5 6 7 8 9 0 1 2");
        assertThat(toString(shuffle("cut -4", 10))).isEqualTo("6 7 8 9 0 1 2 3 4 5");
        assertThat(toString(shuffle("cut 6", 10))).isEqualTo("6 7 8 9 0 1 2 3 4 5");
        assertThat(toString(shuffle("deal with increment 3", 10))).isEqualTo("0 7 4 1 8 5 2 9 6 3");
        assertThat(toString(shuffle(example1, 10))).isEqualTo("0 3 6 9 2 5 8 1 4 7");
        assertThat(toString(shuffle(example2, 10))).isEqualTo("3 0 7 4 1 8 5 2 9 6");
        assertThat(toString(shuffle(example3, 10))).isEqualTo("6 3 0 7 4 1 8 5 2 9");
        assertThat(toString(shuffle(example4, 10))).isEqualTo("9 2 5 8 1 4 7 0 3 6");
        out.println("examples ok");
        assertThat(indexOf(shuffle(input, 10007), 2019)).isEqualTo(4775);

        // 2
        assertThat(getCardInPositionAfterShuffle(8, 10, "deal into new stack")).isEqualTo(1);
        assertThat(getCardInPositionAfterShuffle(8, 10, "cut 3")).isEqualTo(5);
        assertThat(getCardInPositionAfterShuffle(8, 10, "cut -4")).isEqualTo(2);
        assertThat(getCardInPositionAfterShuffle(8, 10, "cut 6")).isEqualTo(2);
        assertThat(getCardInPositionAfterShuffle(8, 10, "deal with increment 3")).isEqualTo(6);

        assertThat(toString(shuffle("deal with increment 7", 10))).isEqualTo("0 3 6 9 2 5 8 1 4 7");
        assertThat(getCardInPositionAfterShuffle(8, 10, "deal with increment 7")).isEqualTo(4);

        String twoShuff= "deal with increment 7\n" +
                "deal into new stack";
        assertThat(toString(shuffle(twoShuff, 10))).isEqualTo("7 4 1 8 5 2 9 6 3 0");
        assertThat(getCardInPositionAfterShuffle(8, 10, twoShuff)).isEqualTo(3);

        String qq= "cut 6\n" +
                "deal with increment 7\n" +
                "deal into new stack";
        assertThat(toString(shuffle(qq, 10))).isEqualTo("3 0 7 4 1 8 5 2 9 6");
        assertThat(getCardInPositionAfterShuffle(8, 10, qq)).isEqualTo(9);

        assertThat(getCardInPositionAfterShuffle(8, 10, example1)).isEqualTo(4);
        assertThat(getCardInPositionAfterShuffle(8, 10, example2)).isEqualTo(9);
        assertThat(getCardInPositionAfterShuffle(8, 10, example3)).isEqualTo(2);
        assertThat(getCardInPositionAfterShuffle(8, 10, example4)).isEqualTo(3);
        assertThat(getCardInPositionAfterShuffle(4775, 10007, input)).isEqualTo(2019);
        assertThat(getCardInPositionAfterShuffle(2020, 119315717514047L, input)).isEqualTo(-1);
//        out.println(timeToFillWithOxygen(input));

        out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static long getCardInPositionAfterShuffle(long position, long deckSize, String spec) {
        long currentCard = position;
        for (String line : Lists.reverse(Arrays.asList(spec.split("\n")))) {
            if (line.equals("deal into new stack")) {
                // reverse deck
                currentCard = deckSize - currentCard - 1;
            } else if (line.startsWith("cut ")) {
                int cutSize = Integer.parseInt(line.substring("cut ".length()));
                // cut is a rotate
                currentCard = (currentCard - cutSize + deckSize) % deckSize;
            } else if (line.startsWith("deal with increment ")) {
                BigInteger incrementSize = new BigInteger(line.substring("deal with increment ".length()));
                // this is mul mod, so:
                // current = (prev * incrementSize) % deckSize
                // so
                BigInteger deckSizeB = BigInteger.valueOf(deckSize);
                BigInteger modInverse = incrementSize.modInverse(deckSizeB);
                long prev = modInverse.multiply(BigInteger.valueOf(currentCard)).mod(deckSizeB).longValueExact();

                currentCard = prev;
            } else {
                throw new IllegalArgumentException(line);
            }
        }
        return currentCard;
    }

    private static int indexOf(int[] deck, int c) {
        for (int i = 0; i < deck.length; i++) {
            if (deck[i] == c) {
                return i;
            }
        }
        return -1;
    }

    private static String toString(int[] deck) {
        StringBuilder acc = new StringBuilder();
        String sep = "";
        for (int i : deck) {
            acc.append(sep).append(i);
            sep = " ";
        }
        return acc.toString();
    }

    private static int[] shuffle(String spec, int deckSize) {
        int[] deck = new int[deckSize];
        for (int i = 0; i < deckSize; i++) {
            deck[i] = i;
        }
        int[] buff = new int[deckSize];
        for (String line : spec.split("\n")) {
            if (line.equals("deal into new stack")) {
                for (int i = 0; i < deckSize; i++) {
                    buff[deckSize - i - 1] = deck[i];
                }
                int[] tmp = deck;
                deck = buff;
                buff = tmp;
            } else if (line.startsWith("cut ")) {
                int cutSize = Integer.parseInt(line.substring("cut ".length()));
                // cut is a rotate
                // could do an arraycopy here?
                int drawFromIdx = cutSize;
                for (int i = 0; i < deckSize; i++) {
                    if (drawFromIdx < 0) {
                        drawFromIdx += deckSize;
                    } else if (drawFromIdx >= deckSize) {
                        drawFromIdx -= deckSize;
                    }
                    buff[i] = deck[drawFromIdx];
                    drawFromIdx++;
                }
                int[] tmp = deck;
                deck = buff;
                buff = tmp;
            } else if (line.startsWith("deal with increment ")) {
                int incrementSize = Integer.parseInt(line.substring("deal with increment ".length()));
                int targetIdx = 0;
                for (int i = 0; i < deckSize; i++) {
                    if (targetIdx >= deckSize) {
                        targetIdx -= deckSize;
                    }
                    buff[targetIdx] = deck[i];
                    targetIdx += incrementSize;
                }
                int[] tmp = deck;
                deck = buff;
                buff = tmp;
            } else {
                throw new IllegalArgumentException(line);
            }
        }
        return deck;
    }

    static String example1 = "deal with increment 7\n" +
            "deal into new stack\n" +
            "deal into new stack";
    static String example2 = "cut 6\n" +
            "deal with increment 7\n" +
            "deal into new stack";
    static String example3 = "deal with increment 7\n" +
            "deal with increment 9\n" +
            "cut -2";
    static String example4 = "deal into new stack\n" +
            "cut -2\n" +
            "deal with increment 7\n" +
            "cut 8\n" +
            "cut -4\n" +
            "deal with increment 7\n" +
            "cut 3\n" +
            "deal with increment 9\n" +
            "deal with increment 3\n" +
            "cut -1";

    static String input = "cut -7812\n" +
            "deal with increment 55\n" +
            "cut -3909\n" +
            "deal with increment 51\n" +
            "deal into new stack\n" +
            "deal with increment 4\n" +
            "cut -77\n" +
            "deal with increment 26\n" +
            "deal into new stack\n" +
            "deal with increment 36\n" +
            "cut 5266\n" +
            "deal with increment 20\n" +
            "cut 8726\n" +
            "deal with increment 22\n" +
            "cut 4380\n" +
            "deal into new stack\n" +
            "cut 3342\n" +
            "deal with increment 16\n" +
            "cut -2237\n" +
            "deal into new stack\n" +
            "deal with increment 20\n" +
            "cut 7066\n" +
            "deal with increment 18\n" +
            "cut 5979\n" +
            "deal with increment 9\n" +
            "cut 2219\n" +
            "deal with increment 44\n" +
            "cut 7341\n" +
            "deal with increment 10\n" +
            "cut -6719\n" +
            "deal with increment 42\n" +
            "deal into new stack\n" +
            "cut -2135\n" +
            "deal with increment 75\n" +
            "cut 5967\n" +
            "deal into new stack\n" +
            "cut 6401\n" +
            "deal with increment 39\n" +
            "deal into new stack\n" +
            "deal with increment 56\n" +
            "cut 7735\n" +
            "deal with increment 49\n" +
            "cut -6350\n" +
            "deal with increment 50\n" +
            "deal into new stack\n" +
            "deal with increment 72\n" +
            "deal into new stack\n" +
            "cut 776\n" +
            "deal into new stack\n" +
            "deal with increment 18\n" +
            "cut 9619\n" +
            "deal with increment 9\n" +
            "deal into new stack\n" +
            "cut 5343\n" +
            "deal into new stack\n" +
            "cut 9562\n" +
            "deal with increment 65\n" +
            "cut 4499\n" +
            "deal with increment 58\n" +
            "cut -4850\n" +
            "deal into new stack\n" +
            "cut -9417\n" +
            "deal into new stack\n" +
            "deal with increment 33\n" +
            "cut 2763\n" +
            "deal with increment 61\n" +
            "cut 7377\n" +
            "deal with increment 27\n" +
            "cut 895\n" +
            "deal into new stack\n" +
            "deal with increment 41\n" +
            "cut -1207\n" +
            "deal with increment 22\n" +
            "cut -7401\n" +
            "deal with increment 48\n" +
            "cut 5776\n" +
            "deal with increment 3\n" +
            "cut 2097\n" +
            "deal with increment 49\n" +
            "cut -8098\n" +
            "deal with increment 68\n" +
            "cut 2296\n" +
            "deal with increment 35\n" +
            "cut -4471\n" +
            "deal with increment 56\n" +
            "cut -2778\n" +
            "deal with increment 5\n" +
            "cut -6386\n" +
            "deal with increment 54\n" +
            "cut -7411\n" +
            "deal with increment 20\n" +
            "cut -4222\n" +
            "deal into new stack\n" +
            "cut -5236\n" +
            "deal with increment 64\n" +
            "cut -3581\n" +
            "deal with increment 11\n" +
            "cut 3255\n" +
            "deal with increment 20\n" +
            "cut -5914";
}

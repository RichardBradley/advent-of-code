package y2020;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2020D22 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        String input = Resources.toString(Resources.getResource("y2020/Y2020D22.txt"), StandardCharsets.UTF_8);

        assertThat(part1(example)).isEqualTo(306);
        assertThat(part1(input)).isEqualTo(35202);

        assertThat(part2(example)).isEqualTo(-291);
        assertThat(part2(input)).isEqualTo(-32317);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static long part2(String input) {
        String[] players = input.split("\n\n");
        checkState(players.length == 2);
        checkState(players[0].startsWith("Player 1:\n"));
        Queue<Integer> player1 = new ArrayDeque<>(Splitter.on("\n").splitToList(players[0].substring("Player 1:\n".length())).stream()
                .map(Integer::parseInt)
                .collect(Collectors.toList()));
        checkState(players[1].startsWith("Player 2:\n"));
        Queue<Integer> player2 = new ArrayDeque<>(Splitter.on("\n").splitToList(players[1].substring("Player 2:\n".length())).stream()
                .map(Integer::parseInt)
                .collect(Collectors.toList()));

        return playRecursiveCombat(player1, player2);
    }

    /**
     * @return positive score if player 1 wins, negative score if player 2 wins
     */
    private static long playRecursiveCombat(Queue<Integer> player1, Queue<Integer> player2) {
        Set<String> previousRounds = new HashSet<>();
        while (!player1.isEmpty() && !player2.isEmpty()) {
            String state = String.format("p1=%s p2=%s", player1, player2);
            if (!previousRounds.add(state)) {
                return scoreDeck(player1);
            }

            int p1card = player1.remove();
            int p2card = player2.remove();

            boolean p1wins;
            if (p1card <= player1.size() && p2card <= player2.size()) {
                p1wins = 0 < playRecursiveCombat(
                        player1.stream().limit(p1card)
                                .collect(Collectors.toCollection(ArrayDeque::new)),
                        player2.stream().limit(p2card)
                                .collect(Collectors.toCollection(ArrayDeque::new)));
            } else if (p1card > p2card) {
                p1wins = true;
            } else if (p2card > p1card) {
                p1wins = false;
            } else {
                throw new IllegalStateException();
            }

            if (p1wins) {
                player1.add(p1card);
                player1.add(p2card);
            } else {
                player2.add(p2card);
                player2.add(p1card);
            }
        }

        if (player2.isEmpty()) {
            return scoreDeck(player1);
        } else {
            return -scoreDeck(player2);
        }
    }

    private static long scoreDeck(Queue<Integer> deck) {
        long multiplier = deck.size();
        long acc = 0;
        for (Integer card : deck) {
            acc += multiplier * card;
            multiplier--;
        }
        return acc;
    }

    private static long part1(String input) {
        String[] players = input.split("\n\n");
        checkState(players.length == 2);
        checkState(players[0].startsWith("Player 1:\n"));
        Queue<Integer> player1 = new ArrayDeque<>(Splitter.on("\n").splitToList(players[0].substring("Player 1:\n".length())).stream()
                .map(Integer::parseInt)
                .collect(Collectors.toList()));
        checkState(players[1].startsWith("Player 2:\n"));
        Queue<Integer> player2 = new ArrayDeque<>(Splitter.on("\n").splitToList(players[1].substring("Player 2:\n".length())).stream()
                .map(Integer::parseInt)
                .collect(Collectors.toList()));

        while (!player1.isEmpty() && !player2.isEmpty()) {
            int p1card = player1.remove();
            int p2card = player2.remove();

            // The winner keeps both cards, placing them
            // on the bottom of their own deck so that the
            // winner's card is above the other card.
            if (p1card > p2card) {
                player1.add(p1card);
                player1.add(p2card);
            } else if (p2card > p1card) {
                player2.add(p2card);
                player2.add(p1card);
            } else {
                throw new IllegalStateException();
            }
        }

        // the winning player's score. The bottom card in their deck is worth
        // the value of the card multiplied by 1, the second-from-the-bottom
        // card is worth the value of the card multiplied by 2, and so on
        return player1.isEmpty() ? scoreDeck(player2) : scoreDeck(player1);
    }

    static String example = "Player 1:\n" +
            "9\n" +
            "2\n" +
            "6\n" +
            "3\n" +
            "1\n" +
            "\n" +
            "Player 2:\n" +
            "5\n" +
            "8\n" +
            "4\n" +
            "7\n" +
            "10";
}

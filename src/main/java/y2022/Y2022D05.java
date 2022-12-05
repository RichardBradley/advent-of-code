package y2022;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;
import lombok.Value;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2022D05 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        List<String> input = Resources.readLines(Resources.getResource("y2022/Y2022D05.txt"), StandardCharsets.UTF_8);

        // 1
        assertThat(part1(example)).isEqualTo("CMZ");
        System.out.println(part1(input));

        // 2
        assertThat(part2(example)).isEqualTo("MCD");
        System.out.println(part2(input));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static String part1(List<String> input) {
        Input state = parse(input);

        for (Move move : state.moves) {
            for (int i = 0; i < move.moveCount; i++) {
                char c = state.piles.get(move.fromIdx).pop();
                state.piles.get(move.toIdx).push(c);
            }
        }

        StringBuilder acc = new StringBuilder();
        for (Stack<Character> pile : state.piles) {
            acc.append(pile.peek());
        }
        return acc.toString();
    }

    private static String part2(List<String> input) {
        Input state = parse(input);

        for (Move move : state.moves) {
            Stack<Character> fromPile = state.piles.get(move.fromIdx);
            Stack<Character> toPile = state.piles.get(move.toIdx);
            toPile.addAll(
                    fromPile.subList(
                            fromPile.size() - move.moveCount,
                            fromPile.size()));
            fromPile.setSize(fromPile.size() - move.moveCount);
        }

        StringBuilder acc = new StringBuilder();
        for (Stack<Character> pile : state.piles) {
            acc.append(pile.peek());
        }
        return acc.toString();
    }

    private static class Input {
        List<Stack<Character>> piles;
        List<Move> moves;
    }

    @Value
    private static class Move {
        int fromIdx;
        int toIdx;
        int moveCount;
    }

    private static Input parse(List<String> input) {
        int sepIdx = 0;
        int stackCount = -1;
        for (; sepIdx < input.size(); sepIdx++) {
            String line = input.get(sepIdx);
            if (line.startsWith(" 1")) {
                stackCount =
                        Splitter.on(" ").trimResults().omitEmptyStrings().splitToList(line)
                                .size();
                break;
            }
        }

        Input acc = new Input();
        acc.moves = new ArrayList<>();
        acc.piles = new ArrayList<>();
        for (int i = 0; i < stackCount; i++) {
            acc.piles.add(new Stack<>());
        }

        // Piles, bottom to top
        for (int i = sepIdx - 1; i >= 0; i--) {
            String line = input.get(i);
            int pileIdx = 0;
            for (int x = 1; x < line.length(); x += 4) {
                char c = line.charAt(x);
                if (c != ' ') {
                    acc.piles.get(pileIdx).push(c);
                }
                pileIdx++;
            }
        }

        Pattern p = Pattern.compile("move (\\d+) from (\\d+) to (\\d+)");
        for (int i = sepIdx + 2; i < input.size(); i++) {
            String line = input.get(i);
            Matcher m = p.matcher(line);
            checkState(m.matches());
            acc.moves.add(new Move(
                    Integer.parseInt(m.group(2)) - 1,
                    Integer.parseInt(m.group(3)) - 1,
                    Integer.parseInt(m.group(1))));
        }

        return acc;
    }

    private static List<String> example = List.of(
            "    [D]    ",
            "[N] [C]    ",
            "[Z] [M] [P]",
            " 1   2   3 ",
            "",
            "move 1 from 2 to 1",
            "move 3 from 1 to 3",
            "move 2 from 2 to 1",
            "move 1 from 1 to 2");
}

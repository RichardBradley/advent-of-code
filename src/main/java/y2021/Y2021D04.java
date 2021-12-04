package y2021;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;
import lombok.Value;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2021D04 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            BingoInput input = parse(Resources.readLines(Resources.getResource("y2021/Y2021D04.txt"), StandardCharsets.UTF_8));
            BingoInput example = parse(Arrays.asList(Y2021D04.example));

            Output exampleOutput = runGame(example);
            Output output = runGame(input);

            // 1
            assertThat(exampleOutput.firstWinScore).isEqualTo(4512);
            assertThat(output.firstWinScore).isEqualTo(63552);

            // 2
            assertThat(exampleOutput.lastWinScore).isEqualTo(1924);
            assertThat(output.lastWinScore).isEqualTo(9020);

        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private static Output runGame(BingoInput input) {
        Set<Integer> drawn = new HashSet<>();
        Set<Integer> wonBoardIndexes = new HashSet<>();
        Integer firstWinScore = null;
        for (int calledNumber : input.calledNumbers) {
            drawn.add(calledNumber);

            for (int boardIdx = 0; boardIdx < input.boards.size(); boardIdx++) {
                if (!wonBoardIndexes.contains(boardIdx)) {
                    if (isWinner(drawn, input.boards.get(boardIdx))) {
                        wonBoardIndexes.add(boardIdx);
                        if (wonBoardIndexes.size() == 1) {
                            firstWinScore = score(boardIdx, drawn, input, calledNumber);
                        }
                        if (wonBoardIndexes.size() == input.boards.size()) {
                            return new Output(firstWinScore, score(boardIdx, drawn, input, calledNumber));
                        }
                    }
                }
            }
        }
        throw new IllegalArgumentException("no winner");
    }

    private static int score(int winnerIdx, Set<Integer> drawn, BingoInput input, int lastDrawn) {
        int[][] board = input.boards.get(winnerIdx);
        int unmarkedSum = 0;
        for (int[] row : board) {
            for (int x : row) {
                if (!drawn.contains(x)) {
                    unmarkedSum += x;
                }
            }
        }
        return unmarkedSum * lastDrawn;
    }

    private static boolean isWinner(Set<Integer> drawn, int[][] board) {
        rows:
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                if (!drawn.contains(board[y][x])) {
                    continue rows;
                }
            }
            // row winner
            return true;
        }

        cols:
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                if (!drawn.contains(board[y][x])) {
                    continue cols;
                }
            }
            // col winner
            return true;
        }

        return false;
    }

    private static BingoInput parse(List<String> lines) {
        String[] calledNumbersS = lines.get(0).split(",");
        int[] calledNumbers = new int[calledNumbersS.length];
        for (int i = 0; i < calledNumbers.length; i++) {
            calledNumbers[i] = Integer.parseInt(calledNumbersS[i]);
        }

        List<int[][]> boards = new ArrayList<>();
        int lineIdx = 1;
        while (lineIdx < lines.size()) {
            checkState("".equals(lines.get(lineIdx)));
            lineIdx++;
            int[][] board = new int[5][];
            for (int y = 0; y < 5; y++) {
                board[y] = new int[5];
                List<String> boardLine = Splitter.on(' ').omitEmptyStrings()
                        .splitToList(lines.get(lineIdx + y));
                checkState(boardLine.size() == 5);
                for (int x = 0; x < 5; x++) {
                    board[y][x] = Integer.parseInt(boardLine.get(x));
                }
            }
            boards.add(board);
            lineIdx += 5;
        }

        return new BingoInput(calledNumbers, boards);
    }

    private static String[] example = new String[]{
            "7,4,9,5,11,17,23,2,0,14,21,24,10,16,13,6,15,25,12,22,18,20,8,19,3,26,1",
            "",
            "22 13 17 11  0",
            " 8  2 23  4 24",
            "21  9 14 16  7",
            " 6 10  3 18  5",
            " 1 12 20 15 19",
            "",
            " 3 15  0  2 22",
            " 9 18 13 17  5",
            "19  8  7 25 23",
            "20 11 10 24  4",
            "14 21 16 12  6",
            "",
            "14 21 17 24  4",
            "10 16 15  9 19",
            "18  8 23 26 20",
            "22 11 13  6  5",
            " 2  0 12  3  7"
    };

    @Value
    private static class BingoInput {
        int[] calledNumbers;
        List<int[][]> boards; // boardIdx, y, x
    }

    @Value
    private static class Output {
        int firstWinScore;
        int lastWinScore;
    }
}

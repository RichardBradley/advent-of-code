package y2019;

import com.google.common.base.Stopwatch;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.math.BigInteger;
import java.util.Queue;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;
import static y2019.Y2019D09.evalPartial;

public class Y2019D13 {

    static final boolean WRITE_VIDEO = true;

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        System.out.println(countBlockTiles(input));

        // 2
        System.out.println(runGameToCompletion(input));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static int runGameToCompletion(BigInteger[] program) throws Exception {
        program[0] = BigInteger.valueOf(2); // free play mode
        Y2019D09.ProgramState programState = new Y2019D09.ProgramState(program);
        GameState gameState = new GameState();
        VideoWriter videoWriter;
        if (WRITE_VIDEO) {
            videoWriter = new VideoWriter();
        }
        Queue<BigInteger> ballTrackingInput = gameState.getBallTrackingInput();

        while (true) {

            int drawX;
            int drawY;

            Y2019D09.EvalResult evalResult = evalPartial(programState, ballTrackingInput);
            if (evalResult instanceof Y2019D09.Ouput) {
                Y2019D09.Ouput output = (Y2019D09.Ouput) evalResult;
                drawX = output.getOutputVal().intValueExact();
            } else if (evalResult instanceof Y2019D09.Terminated) {
                if (WRITE_VIDEO) {
                    videoWriter.finish();
                }
                return gameState.score;
            } else {
                throw new IllegalStateException(evalResult.toString());
            }
            evalResult = evalPartial(programState, ballTrackingInput);
            if (evalResult instanceof Y2019D09.Ouput) {
                Y2019D09.Ouput output = (Y2019D09.Ouput) evalResult;
                drawY = output.getOutputVal().intValueExact();
            } else {
                throw new IllegalStateException(evalResult.toString());
            }
            evalResult = evalPartial(programState, ballTrackingInput);
            if (evalResult instanceof Y2019D09.Ouput) {
                Y2019D09.Ouput output = (Y2019D09.Ouput) evalResult;
                if (drawX == -1 && drawY == 0) {
                    gameState.score = output.getOutputVal().intValueExact();
                } else {
                    gameState.setPix(drawX, drawY, output.getOutputVal().intValueExact());
                }
                if (WRITE_VIDEO) {
                    // skip frames for steps where the ball hasn't been drawn yet
                    // or where the engine is blanking a pixel
                    if (gameState.ballX != 0 && !output.getOutputVal().equals(ZERO)) {
                        videoWriter.putFrame(gameState.drawImage());
                    }
                } else {
                    gameState.draw();
                }
            } else {
                throw new IllegalStateException(evalResult.toString());
            }
        }
    }

    static class GameState {
        char[][] display = new char[25][35];
        int score = 0;
        int paddleX = 0;
        int ballX = 0;
        int blockCount = 0;

        Queue<BigInteger> getBallTrackingInput() {
            return new AbstractQueue<BigInteger>() {

                private BigInteger MINUS_ONE = BigInteger.valueOf(-1);

                @Override
                public BigInteger poll() {
                    if (ballX < paddleX) {
                        return MINUS_ONE;
                    } else if (ballX > paddleX) {
                        return ONE;
                    } else {
                        return ZERO;
                    }
                }

                @Override
                public Iterator<BigInteger> iterator() {
                    throw new UnsupportedOperationException();
                }

                @Override
                public int size() {
                    return 1;
                }

                @Override
                public boolean offer(BigInteger bigInteger) {
                    throw new UnsupportedOperationException();
                }


                @Override
                public BigInteger peek() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        char[] icons = new char[]{' ', '#', 'O', '=', '@'};

        public void setPix(int x, int y, int val) {
            if (val == 3) {
                paddleX = x;
            } else if (val == 4) {
                ballX = x;
            } else if (val == 0) {
                if (display[y][x] == 'O') {
                    blockCount--;
                }
            } else if (val == 2) {
                if (display[y][x] != 'O') {
                    blockCount++;
                }
            }

            display[y][x] = icons[val];
        }

        public void draw() {
            StringBuilder acc = new StringBuilder();
            acc.append("\n\n");
            acc.append("Score = ").append(score).append("\n\n");
            for (char[] line : display) {
                acc.append(line).append("\n");
            }
            System.out.print(acc);
        }

        public BufferedImage drawImage() {
            int width = display[0].length;
            int height = display.length;
            int outputPxPerPx = 10;
            int scoreHeight = 20;
            BufferedImage im = new BufferedImage(
                    width * outputPxPerPx,
                    height * outputPxPerPx + scoreHeight,
                    BufferedImage.TYPE_3BYTE_BGR);
            Graphics g = im.getGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, im.getWidth(), im.getHeight());
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    switch (display[y][x]) {
                        case ' ':
                            break;
                        case '@':
                            g.setColor(Color.RED);
                            g.fillOval(
                                    x * outputPxPerPx,
                                    y * outputPxPerPx,
                                    outputPxPerPx,
                                    outputPxPerPx);
                            break;
                        case '=':
                            int vMargin = outputPxPerPx / 5;
                            g.setColor(Color.BLUE);
                            g.fillRect(
                                    x * outputPxPerPx,
                                    y * outputPxPerPx + vMargin,
                                    outputPxPerPx,
                                    outputPxPerPx - 2 * vMargin);
                            break;
                        case '#':
                            g.setColor(Color.DARK_GRAY);
                            g.fillRect(
                                    x * outputPxPerPx,
                                    y * outputPxPerPx,
                                    outputPxPerPx,
                                    outputPxPerPx);
                            break;
                        case 'O':
                            g.setColor(new Color(112, 130, 56));
                            g.fillRect(
                                    x * outputPxPerPx,
                                    y * outputPxPerPx,
                                    outputPxPerPx,
                                    outputPxPerPx);
                            break;
                    }
                }
            }
            g.setColor(Color.BLACK);
            g.drawString("Score = " + score, scoreHeight / 2, height * outputPxPerPx + scoreHeight / 2);
            return im;
        }
    }

    static long countBlockTiles(BigInteger[] program) {

        int width = 1000;
        int height = 1000;
        int[][] grid = new int[height][width];

        Y2019D09.ProgramState state = new Y2019D09.ProgramState(program);
        Queue<BigInteger> inputs = new LinkedList<>();
        while (true) {

            int drawX;
            int drawY;

            Y2019D09.EvalResult evalResult = evalPartial(state, inputs);
            if (evalResult instanceof Y2019D09.Ouput) {
                Y2019D09.Ouput output = (Y2019D09.Ouput) evalResult;
                drawX = output.getOutputVal().intValueExact();
            } else if (evalResult instanceof Y2019D09.Terminated) {
                printGrid(grid);
                return Arrays.stream(grid)
                        .flatMapToInt(g -> IntStream.of(g))
                        .filter(i -> i == 2)
                        .count();
            } else {
                throw new IllegalStateException(evalResult.toString());
            }
            evalResult = evalPartial(state, inputs);
            if (evalResult instanceof Y2019D09.Ouput) {
                Y2019D09.Ouput output = (Y2019D09.Ouput) evalResult;
                drawY = output.getOutputVal().intValueExact();
            } else {
                throw new IllegalStateException(evalResult.toString());
            }
            evalResult = evalPartial(state, inputs);
            if (evalResult instanceof Y2019D09.Ouput) {
                Y2019D09.Ouput output = (Y2019D09.Ouput) evalResult;
                grid[drawY][drawX] = output.getOutputVal().intValueExact();
            } else {
                throw new IllegalStateException(evalResult.toString());
            }
        }
    }

    private static void printGrid(int[][] grid) {
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                if (grid[y][x] != 0) {
                    minX = Math.min(x, minX);
                    maxX = Math.max(x, maxX);
                    minY = Math.min(y, minY);
                    maxY = Math.max(y, maxY);
                }
            }
        }

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                char c;
                switch (grid[y][x]) {
                    case 0:
                        c = ' ';
                        break;
                    case 1:
                        c = '#';
                        break;
                    case 2:
                        c = 'O';
                        break;
                    case 3:
                        c = '=';
                        break;
                    case 4:
                        c = '@';
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
                System.out.print(c);
            }
            System.out.println();
        }
    }

    static BigInteger[] input = Y2019D09.parse(
            "1,380,379,385,1008,2249,380030,381,1005,381,12,99,109,2250,1102,1,0,383,1101,0,0,382,20101,0,382,1,20101,0,383,2,21102,1,37,0,1106,0,578,4,382,4,383,204,1,1001,382,1,382,1007,382,35,381,1005,381,22,1001,383,1,383,1007,383,23,381,1005,381,18,1006,385,69,99,104,-1,104,0,4,386,3,384,1007,384,0,381,1005,381,94,107,0,384,381,1005,381,108,1105,1,161,107,1,392,381,1006,381,161,1102,1,-1,384,1105,1,119,1007,392,33,381,1006,381,161,1102,1,1,384,20101,0,392,1,21102,21,1,2,21102,0,1,3,21101,0,138,0,1105,1,549,1,392,384,392,21002,392,1,1,21102,21,1,2,21101,3,0,3,21102,1,161,0,1105,1,549,1101,0,0,384,20001,388,390,1,20101,0,389,2,21102,180,1,0,1105,1,578,1206,1,213,1208,1,2,381,1006,381,205,20001,388,390,1,21002,389,1,2,21102,1,205,0,1105,1,393,1002,390,-1,390,1102,1,1,384,21001,388,0,1,20001,389,391,2,21101,0,228,0,1105,1,578,1206,1,261,1208,1,2,381,1006,381,253,21001,388,0,1,20001,389,391,2,21101,0,253,0,1106,0,393,1002,391,-1,391,1101,0,1,384,1005,384,161,20001,388,390,1,20001,389,391,2,21101,0,279,0,1106,0,578,1206,1,316,1208,1,2,381,1006,381,304,20001,388,390,1,20001,389,391,2,21102,304,1,0,1105,1,393,1002,390,-1,390,1002,391,-1,391,1101,0,1,384,1005,384,161,20101,0,388,1,21001,389,0,2,21102,1,0,3,21102,1,338,0,1106,0,549,1,388,390,388,1,389,391,389,20101,0,388,1,21001,389,0,2,21102,1,4,3,21101,365,0,0,1106,0,549,1007,389,22,381,1005,381,75,104,-1,104,0,104,0,99,0,1,0,0,0,0,0,0,260,15,18,1,1,17,109,3,21202,-2,1,1,21202,-1,1,2,21101,0,0,3,21102,414,1,0,1106,0,549,21202,-2,1,1,22101,0,-1,2,21102,1,429,0,1106,0,601,1201,1,0,435,1,386,0,386,104,-1,104,0,4,386,1001,387,-1,387,1005,387,451,99,109,-3,2105,1,0,109,8,22202,-7,-6,-3,22201,-3,-5,-3,21202,-4,64,-2,2207,-3,-2,381,1005,381,492,21202,-2,-1,-1,22201,-3,-1,-3,2207,-3,-2,381,1006,381,481,21202,-4,8,-2,2207,-3,-2,381,1005,381,518,21202,-2,-1,-1,22201,-3,-1,-3,2207,-3,-2,381,1006,381,507,2207,-3,-4,381,1005,381,540,21202,-4,-1,-1,22201,-3,-1,-3,2207,-3,-4,381,1006,381,529,21202,-3,1,-7,109,-8,2105,1,0,109,4,1202,-2,35,566,201,-3,566,566,101,639,566,566,2101,0,-1,0,204,-3,204,-2,204,-1,109,-4,2105,1,0,109,3,1202,-1,35,593,201,-2,593,593,101,639,593,593,21001,0,0,-2,109,-3,2105,1,0,109,3,22102,23,-2,1,22201,1,-1,1,21101,0,409,2,21102,1,437,3,21102,1,805,4,21102,1,630,0,1106,0,456,21201,1,1444,-2,109,-3,2106,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,2,0,2,0,0,0,2,2,0,0,2,0,2,2,2,0,2,2,0,0,2,2,0,0,2,2,2,2,2,2,2,0,1,1,0,0,2,2,0,0,0,2,2,2,0,0,0,2,2,2,0,2,0,2,0,2,0,2,0,2,0,2,2,2,2,2,0,1,1,0,0,2,2,2,2,0,2,0,2,2,2,2,2,0,0,0,2,0,2,2,0,2,0,0,2,2,0,2,2,0,0,0,1,1,0,2,0,2,2,2,2,0,0,0,2,2,0,0,2,2,2,0,2,2,2,2,0,2,2,0,0,2,0,2,0,0,0,1,1,0,2,0,2,2,0,2,2,0,0,0,0,2,0,2,2,2,0,2,0,0,2,2,2,0,0,0,0,2,2,2,0,0,1,1,0,2,0,0,0,0,0,2,0,2,0,2,0,2,0,0,2,2,2,0,0,0,2,0,0,0,0,0,2,2,2,2,0,1,1,0,2,2,2,2,0,0,0,0,2,2,2,0,2,2,0,0,2,2,0,2,2,0,2,0,2,2,0,2,2,0,2,0,1,1,0,2,0,0,2,2,0,2,2,2,0,2,2,0,0,2,0,0,0,0,2,2,0,2,2,2,2,2,0,2,2,0,0,1,1,0,0,0,0,2,2,0,0,2,2,0,0,0,2,2,2,2,0,2,0,2,2,2,0,2,0,0,0,0,0,0,2,0,1,1,0,0,2,2,0,0,0,2,0,2,2,2,0,0,2,2,2,2,2,0,0,2,2,0,2,2,0,0,0,2,2,0,0,1,1,0,2,0,0,0,0,0,2,2,2,0,0,2,0,2,2,0,2,2,0,2,0,2,2,2,2,2,2,2,0,2,0,0,1,1,0,0,2,2,0,0,2,2,2,0,2,2,0,2,2,2,2,2,0,0,0,0,2,0,2,0,2,0,2,2,2,0,0,1,1,0,2,2,2,0,2,2,2,2,2,0,0,2,0,0,0,2,0,2,0,0,2,2,0,2,2,2,2,2,0,0,2,0,1,1,0,2,0,0,2,2,0,0,0,0,0,2,0,0,2,2,2,2,2,0,2,2,0,2,0,0,2,0,2,0,2,2,0,1,1,0,2,2,0,2,2,2,0,2,0,2,0,0,0,2,2,2,2,0,2,2,0,2,0,0,2,0,2,0,2,2,2,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,50,83,17,51,29,67,27,74,3,96,21,38,14,55,71,75,15,64,69,72,2,30,70,52,29,61,96,52,48,79,27,36,90,39,21,41,55,56,8,7,13,5,39,49,22,52,66,77,95,3,46,35,75,31,31,96,86,23,72,71,27,20,6,58,70,37,48,67,24,58,27,92,29,82,30,53,76,42,54,65,62,4,57,20,42,57,25,16,76,48,77,36,61,22,31,65,88,7,50,34,54,7,1,38,62,62,83,33,70,73,46,14,89,23,98,14,28,75,79,15,3,98,79,3,4,28,22,5,73,63,60,66,45,36,96,80,48,23,97,98,79,79,82,45,72,6,68,17,51,13,34,27,95,84,59,41,40,40,13,86,21,92,37,30,54,59,48,94,63,37,53,2,62,87,47,42,28,60,48,97,61,68,59,39,31,22,88,34,72,54,11,89,34,68,35,71,5,68,97,37,43,41,80,42,39,91,94,41,56,18,10,76,69,39,4,4,11,14,32,45,85,65,57,51,72,70,53,71,29,9,78,24,31,16,9,63,40,10,26,73,69,45,72,15,98,83,59,3,21,72,97,19,74,69,61,61,62,55,91,28,4,25,27,61,89,91,16,56,11,63,93,25,88,17,12,44,69,92,90,41,6,30,3,89,1,17,21,56,93,2,47,14,14,92,21,52,83,36,36,11,97,6,57,53,97,88,48,70,53,94,84,79,56,2,35,36,68,18,97,60,75,85,30,4,89,14,45,13,88,41,16,59,52,8,47,50,76,93,36,87,22,65,36,32,56,63,31,97,51,70,4,49,37,5,27,48,16,48,79,92,55,3,94,35,46,79,4,92,46,22,87,21,88,50,36,82,67,40,63,97,69,91,63,98,68,2,17,3,87,59,71,87,18,30,13,86,87,84,39,14,63,49,83,57,5,66,11,61,81,9,81,52,62,47,32,86,28,96,4,57,4,57,95,91,71,91,57,1,16,46,40,38,62,7,85,87,76,22,43,23,77,85,73,37,37,90,53,7,25,30,57,98,73,66,56,48,19,74,53,4,65,38,94,9,22,55,67,89,81,96,36,42,3,17,73,28,56,40,42,72,28,20,4,49,2,14,18,10,34,78,13,13,65,6,55,47,97,37,24,51,88,42,22,60,35,2,10,27,37,13,51,53,24,26,81,62,68,30,25,34,9,29,51,6,22,76,21,40,38,97,7,64,31,80,64,10,89,69,50,64,74,94,22,75,30,41,48,58,77,70,48,22,86,10,35,82,84,8,23,28,21,79,98,43,34,19,71,39,80,35,37,81,33,8,35,56,68,23,2,38,32,32,86,60,37,42,53,10,16,5,45,92,20,78,90,25,19,94,44,7,81,22,3,4,37,14,26,3,42,92,22,44,58,28,63,41,81,94,85,2,96,63,67,87,42,55,27,22,94,14,86,19,88,65,93,91,11,47,67,98,28,6,43,46,41,33,27,84,96,39,40,54,81,39,68,85,79,48,59,27,68,34,51,36,64,8,54,44,17,58,54,83,17,56,79,57,5,52,25,8,73,23,63,89,91,72,74,4,12,97,67,6,67,88,52,92,97,28,75,85,64,29,20,5,35,7,54,38,14,93,62,59,74,93,86,91,82,23,83,1,35,5,21,18,71,7,39,8,32,68,57,95,67,39,19,98,89,17,87,37,78,54,36,22,30,35,68,95,61,31,72,86,85,33,12,81,91,1,23,63,91,34,5,86,70,65,69,72,20,84,38,13,94,47,22,40,85,15,18,95,26,68,63,59,38,73,24,69,31,21,87,90,66,87,84,30,79,76,55,33,55,33,94,7,55,380030");
}

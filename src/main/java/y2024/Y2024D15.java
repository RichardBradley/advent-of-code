package y2024;

import com.google.common.base.Stopwatch;
import lombok.Value;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static aoc.Common.loadInputFromResources;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2024D15 {

    public static void main(String[] args) throws Exception {
        List<String> input = loadInputFromResources();
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(part1(example)).isEqualTo(2028);
        assertThat(part1(input)).isEqualTo(1463715);

        // 2
        assertThat(part2(input)).isEqualTo(1481392);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static long part1(List<String> input) {
        Input parsed = parse(input);
        Point loc = parsed.start;
        List<StringBuffer> map = parsed.map;
        for (Point move : parsed.moves) {
            if (move(map, loc, move)) {
                loc = add(loc, move);
            }
            // print(map);
        }

        int score = 0;
        for (int y = 0; y < map.size(); y++) {
            StringBuffer line = map.get(y);
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                if (c == 'O') {
                    score += 100 * y + x;
                }
            }
        }
        return score;
    }

    private static void print(List<StringBuffer> map) {
        System.out.println();
        for (StringBuffer line : map) {
            System.out.println(line);
        }
    }

    private static boolean move(List<StringBuffer> map, Point from, Point dir) {
        Point next = add(from, dir);
        char cAtNext = get(map, next);
        if (cAtNext == '.') {
            set(map, next, get(map, from));
            set(map, from, '.');
            return true;
        } else if (cAtNext == '#') {
            return false;
        } else if (cAtNext == 'O') {
            if (move(map, next, dir)) {
                cAtNext = get(map, next);
                set(map, next, get(map, from));
                set(map, from, cAtNext);
                return true;
            } else {
                return false;
            }
        } else {
            throw new IllegalArgumentException("for: " + cAtNext);
        }
    }

    static Point N = new Point(0, -1);
    static Point E = new Point(1, 0);
    static Point S = new Point(0, 1);
    static Point W = new Point(-1, 0);

    private static boolean move2(List<StringBuffer> map, Point from, Point dir, boolean dryRun) {
        Point next = add(from, dir);
        char cAtNext = get(map, next);
        if (cAtNext == '.') {
            if (!dryRun) {
                set(map, next, get(map, from));
                set(map, from, '.');
            }
            return true;
        } else if (cAtNext == '#') {
            return false;
        } else if (cAtNext == '[') {
            Point nextRHS = new Point(next.x + 1, next.y);
            boolean canMove;
            if (dir.equals(N) || dir.equals(S)) {
                canMove = move2(map, next, dir, dryRun)
                        && move2(map, nextRHS, dir, dryRun);
                if (canMove) {
                    if (!dryRun) {
                        swap(map, from, next);
                    }
                    return true;
                } else {
                    return false;
                }
            } else if (dir.equals(E)) {
                canMove = move2(map, nextRHS, dir, dryRun);
                if (canMove) {
                    if (!dryRun) {
                        swap(map, next, nextRHS);
                        swap(map, from, next);
                    }
                    return true;
                } else {
                    return false;
                }
            } else if (dir.equals(W)) {
                throw new IllegalArgumentException("for: " + dir);
            } else {
                throw new IllegalArgumentException("for: " + dir);
            }
        } else if (cAtNext == ']') {
            Point nextLHS = new Point(next.x - 1, next.y);
            boolean canMove;
            if (dir.equals(N) || dir.equals(S)) {
                canMove = move2(map, next, dir, dryRun)
                        && move2(map, nextLHS, dir, dryRun);
                if (canMove) {
                    if (!dryRun) {
                        swap(map, from, next);
                    }
                    return true;
                } else {
                    return false;
                }
            } else if (dir.equals(W)) {
                canMove = move2(map, nextLHS, dir, dryRun);
                if (canMove) {
                    if (!dryRun) {
                        swap(map, next, nextLHS);
                        swap(map, from, next);
                    }
                    return true;
                } else {
                    return false;
                }
            } else if (dir.equals(E)) {
                throw new IllegalArgumentException("for: " + dir);
            } else {
                throw new IllegalArgumentException("for: " + dir);
            }
        } else {
            throw new IllegalArgumentException("for: " + cAtNext);
        }
    }

    private static void swap(List<StringBuffer> map, Point a, Point b) {
        char c = get(map, b);
        set(map, b, get(map, a));
        set(map, a, c);
    }

    private static void set(List<StringBuffer> map, Point p, char c) {
        map.get(p.y).setCharAt(p.x, c);
    }

    private static char get(List<StringBuffer> map, Point p) {
        return map.get(p.y).charAt(p.x);
    }

    private static Point add(Point a, Point b) {
        return new Point(a.x + b.x, a.y + b.y);
    }

    @Value
    static class Input {
        Point start;
        List<StringBuffer> map;
        List<Point> moves;
    }

    private static Input parse(List<String> input) {
        int lineIdx = 0;
        List<StringBuffer> map = new ArrayList<>();
        Point start = null;
        for (; ; lineIdx++) {
            String line = input.get(lineIdx);
            if (line.equals("")) {
                lineIdx++;
                break;
            }
            StringBuffer sb = new StringBuffer(line);
            int robotX = sb.indexOf("@");
            if (robotX >= 0) {
                checkState(start == null);
                start = new Point(robotX, lineIdx);
            }
            map.add(sb);
        }
        checkState(start != null);

        List<Point> moves = new ArrayList<>();
        for (; lineIdx < input.size(); lineIdx++) {
            String line = input.get(lineIdx);
            for (int x = 0; x < line.length(); x++) {
                switch (line.charAt(x)) {
                    case '>':
                        moves.add(new Point(1, 0));
                        break;
                    case '<':
                        moves.add(new Point(-1, 0));
                        break;
                    case '^':
                        moves.add(new Point(0, -1));
                        break;
                    case 'v':
                        moves.add(new Point(0, 1));
                        break;
                    default:
                        throw new IllegalArgumentException("for: " + line.charAt(x));
                }
            }
        }

        return new Input(start, map, moves);
    }

    private static long part2(List<String> input) {
        Input parsed = parse(input);
        List<StringBuffer> map = new ArrayList<>();
        for (StringBuffer lineIn : parsed.map) {
            StringBuffer lineOut = new StringBuffer();
            for (int x = 0; x < lineIn.length(); x++) {
                char c = lineIn.charAt(x);
                switch (c) {
                    case '.':
                        lineOut.append("..");
                        break;
                    case '#':
                        lineOut.append("##");
                        break;
                    case 'O':
                        lineOut.append("[]");
                        break;
                    case '@':
                        lineOut.append("@.");
                        break;
                    default:
                        throw new IllegalArgumentException("for: " + c);
                }
            }
            map.add(lineOut);
        }
        Point loc = new Point(2 * parsed.start.x, parsed.start.y);

        for (Point move : parsed.moves) {
            if (move2(map, loc, move, true)) {
                move2(map, loc, move, false);
                loc = add(loc, move);
            }
            // print(map);
        }

        int score = 0;
        for (int y = 0; y < map.size(); y++) {
            StringBuffer line = map.get(y);
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                if (c == '[') {
                    score += 100 * y + x;
                }
            }
        }
        return score;
    }

    static List<String> example = List.of(
            "########",
            "#..O.O.#",
            "##@.O..#",
            "#...O..#",
            "#.#.O..#",
            "#...O..#",
            "#......#",
            "########",
            "",
            "<^^>>>vv<v>>v<<");
}

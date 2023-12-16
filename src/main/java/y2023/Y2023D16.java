package y2023;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;
import lombok.Value;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Queue;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;

public class Y2023D16 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            List<String> input = Resources.readLines(Resources.getResource("y2023/Y2023D16.txt"), StandardCharsets.UTF_8);

            // 1
            assertThat(part1(example)).isEqualTo(46);
            assertThat(part1(input)).isEqualTo(7979);

            // 2
            assertThat(part2(example)).isEqualTo(51);
            assertThat(part2(input)).isEqualTo(8437);

        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private static int hash(String s) {
        int curr = 0;
        for (int i = 0; i < s.length(); i++) {
            curr += s.charAt(i);
            curr *= 17;
            curr %= 256;
        }
        return curr;
    }

    enum Dirs {
        N, E, S, W;

        public Point advance(Point p) {
            switch (this) {
                case N:
                    return new Point(p.x, p.y - 1);
                case E:
                    return new Point(p.x + 1, p.y);
                case S:
                    return new Point(p.x, p.y + 1);
                case W:
                    return new Point(p.x - 1, p.y);
                default:
                    throw new IllegalStateException();
            }
        }
    }

    @Value
    private static class BeamPos {
        Point loc;
        Dirs dir;

        public Collection<BeamPos> next(List<String> map) {
            char c = map.get(loc.y).charAt(loc.x);
            if (c == '.'
                    || (c == '-' && (dir == Dirs.E || dir == Dirs.W))
                    || (c == '|' && (dir == Dirs.N || dir == Dirs.S))) {
                Point next = dir.advance(loc);
                if (isInBounds(next, map)) {
                    return Collections.singleton(new BeamPos(next, dir));
                } else {
                    return Collections.emptyList();
                }
            } else if (c == '/' || c == '\\') {
                Dirs newDir;
                switch (dir) {
                    case N:
                        newDir = (c == '/' ? Dirs.E : Dirs.W);
                        break;
                    case E:
                        newDir = (c == '/' ? Dirs.N : Dirs.S);
                        break;
                    case S:
                        newDir = (c == '/' ? Dirs.W : Dirs.E);
                        break;
                    case W:
                        newDir = (c == '/' ? Dirs.S : Dirs.N);
                        break;
                    default:
                        throw new IllegalStateException();
                }
                Point next = newDir.advance(loc);
                if (isInBounds(next, map)) {
                    return Collections.singleton(new BeamPos(next, newDir));
                } else {
                    return Collections.emptyList();
                }
            } else if (c == '|') {
                List<BeamPos> acc = new ArrayList<>();
                Point n1 = Dirs.N.advance(loc);
                if (isInBounds(n1, map)) {
                    acc.add(new BeamPos(n1, Dirs.N));
                }
                Point n2 = Dirs.S.advance(loc);
                if (isInBounds(n2, map)) {
                    acc.add(new BeamPos(n2, Dirs.S));
                }
                return acc;
            } else if (c == '-') {
                List<BeamPos> acc = new ArrayList<>();
                Point n1 = Dirs.E.advance(loc);
                if (isInBounds(n1, map)) {
                    acc.add(new BeamPos(n1, Dirs.E));
                }
                Point n2 = Dirs.W.advance(loc);
                if (isInBounds(n2, map)) {
                    acc.add(new BeamPos(n2, Dirs.W));
                }
                return acc;
            } else {
                throw new IllegalStateException();
            }
        }

        public String toString() {
            return String.format("(%s,%s) %s", loc.x, loc.y, dir);
        }
    }

    private static boolean isInBounds(Point p, List<String> map) {
        if (p.y < 0 || p.y >= map.size()) {
            return false;
        }
        String row = map.get(p.y);
        return p.x >= 0 && p.x < row.length();
    }

    private static long part1(List<String> input) {
        BeamPos start = new BeamPos(new Point(0, 0), Dirs.E);
        return part1(input, start);
    }

    private static long part1(List<String> input, BeamPos start) {
        Set<BeamPos> visited = new HashSet<>();
        Queue<BeamPos> toVisit = new ArrayDeque<>();
        visited.add(start);
        toVisit.add(start);
        BeamPos curr;
        while (null != (curr = toVisit.poll())) {
            Collection<BeamPos> nexts = curr.next(input);
            for (BeamPos next : nexts) {
                if (visited.add(next)) {
                    toVisit.add(next);
                }
            }
        }

        return visited.stream()
                .map(p -> p.loc)
                .collect(Collectors.toSet())
                .size();
    }

    private static long part2(List<String> input) {
        long max = 0;
        int width = input.get(0).length();
        int height = input.size();
        for (int y = 0; y < height; y++) {
            max = Math.max(max, part1(input, new BeamPos(new Point(0, y), Dirs.E)));
            max = Math.max(max, part1(input, new BeamPos(new Point(width - 1, y), Dirs.W)));
        }
        for (int x = 0; x < width; x++) {
            max = Math.max(max, part1(input, new BeamPos(new Point(x, 0), Dirs.S)));
            max = Math.max(max, part1(input, new BeamPos(new Point(x, height - 1), Dirs.N)));
        }
        return max;
    }

    static List<String> example = List.of(
            ".|...\\....",
            "|.-.\\.....",
            ".....|-...",
            "........|.",
            "..........",
            ".........\\",
            "..../.\\\\..",
            ".-.-/..|..",
            ".|....-|.\\",
            "..//.|...."
    );
}

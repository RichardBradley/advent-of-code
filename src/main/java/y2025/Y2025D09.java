package y2025;

import com.google.common.base.Stopwatch;
import lombok.Value;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static aoc.Common.loadInputFromResources;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2025D09 {

    public static void main(String[] args) throws Exception {
        List<String> input = loadInputFromResources();
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(part1(example)).isEqualTo(50);
        assertThat(part1(input)).isEqualTo(4774877510L);

        // 2
        assertThat(part2(example, true)).isEqualTo(24);
        assertThat(part2(input, false)).isEqualTo(1560475800);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static long part1(List<String> input) {
        List<Point> redTiles = input.stream().map(line -> {
                    String[] xs = line.split(",");
                    checkState(xs.length == 2);
                    return new Point(Integer.parseInt(xs[0]), Integer.parseInt(xs[1]));
                })
                .collect(Collectors.toList());

        long maxArea = 0;

        for (int i = 0; i < redTiles.size(); i++) {
            Point pi = redTiles.get(i);
            for (int j = i + 1; j < redTiles.size(); j++) {
                Point pj = redTiles.get(j);
                long area = (Math.abs(pi.x - pj.x) + 1L) * (Math.abs(pi.y - pj.y) + 1L);
                if (area > maxArea) {
                    maxArea = area;
                }
            }
        }

        return maxArea;
    }

    /**
     * Inclusive of the ends
     */
    @Value
    static class Line {
        Point a;
        Point b;

        public boolean intersects(Line other) {
            if (a.x == b.x && other.a.x == other.b.x) {
                // both vertical
                if (a.x == other.a.x) {
                    int maxY = Math.max(a.y, b.y);
                    int minY = Math.min(a.y, b.y);

                    return (other.a.y >= minY && other.a.y <= maxY)
                            || (other.b.y >= minY && other.b.y <= maxY);
                } else {
                    return false;
                }
            } else if (a.y == b.y && other.a.y == other.b.y) {
                // both horizontal
                if (a.y == other.a.y) {
                    int maxX = Math.max(a.x, b.x);
                    int minX = Math.min(a.x, b.x);
                    return (other.a.x >= minX && other.a.x <= maxX)
                            || (other.b.x >= minX && other.b.x <= maxX);
                } else {
                    return false;
                }
            } else if (a.x == b.x) {
                // this is vertical, other is horizontal
                checkState(other.a.y == other.b.y);

                int otherMinX = Math.min(other.a.x, other.b.x);
                int otherMaxX = Math.max(other.a.x, other.b.x);
                if (otherMinX <= a.x && otherMaxX >= a.x) {
                    int thisMinY = Math.min(a.y, b.y);
                    int thisMaxY = Math.max(a.y, b.y);
                    int otherY = other.a.y; // == other.b.y
                    return otherY >= thisMinY && otherY <= thisMaxY;
                } else {
                    return false;
                }
            } else {
                // this is horizontal, other is vertical
                checkState(a.y == b.y);

                int thisMinX = Math.min(a.x, b.x);
                int thisMaxX = Math.max(a.x, b.x);
                if (thisMinX <= other.a.x && thisMaxX >= other.a.x) {
                    int otherMinY = Math.min(other.a.y, other.b.y);
                    int otherMaxY = Math.max(other.a.y, other.b.y);
                    int thisY = a.y; // == b.y
                    return thisY >= otherMinY && thisY <= otherMaxY;
                } else {
                    return false;
                }
            }
        }
    }

    private static long part2(List<String> input, boolean part1) {
        List<Point> redTiles = input.stream().map(line -> {
                    String[] xs = line.split(",");
                    checkState(xs.length == 2);
                    return new Point(Integer.parseInt(xs[0]), Integer.parseInt(xs[1]));
                })
                .collect(Collectors.toList());

        if (part1) {
            System.out.println("Area:");
            {
                List<Line> lines = new ArrayList<>();
                for (int i = 0; i < redTiles.size(); i++) {
                    Point r1 = redTiles.get(i);
                    Point r2 = redTiles.get((i + 1) % redTiles.size());
                    lines.add(new Line(r1, r2));
                }
                print(lines);
            }
        }

        checkState(isRightHandPoly(redTiles));
        List<Line> outerBorder = getOuterBorderOfRightHandPoly(redTiles);
        if (part1) {
            System.out.println("Outer border:");
            print(outerBorder);
        }

        long maxArea = 0;

        if (part1) {
            // tests:
            assertThat(isOutsideAllowedArea(new Point(7, 3), new Point(11, 1), outerBorder)).isFalse();
            assertThat(isOutsideAllowedArea(new Point(9, 7), new Point(9, 5), outerBorder)).isFalse();
            assertThat(isOutsideAllowedArea(new Point(9, 5), new Point(2, 3), outerBorder)).isFalse();
        }

        for (int i = 0; i < redTiles.size(); i++) {
            Point pi = redTiles.get(i);
            for (int j = i + 1; j < redTiles.size(); j++) {
                Point pj = redTiles.get(j);
                long area = (Math.abs(pi.x - pj.x) + 1L) * (Math.abs(pi.y - pj.y) + 1L);
                if (area > maxArea) {
                    // Check if the rect crosses any edges of the restricted area:
                    if (isOutsideAllowedArea(pi, pj, outerBorder)) {
                        continue;
                    }

                    maxArea = area;
                }
            }
        }

        return maxArea;
    }

    private static void print(List<Line> lines) {
        Set<Point> ps = new HashSet<>();
        for (Line line : lines) {
            for (int x = Math.min(line.a.x, line.b.x); x <= Math.max(line.a.x, line.b.x); x++) {
                for (int y = Math.min(line.a.y, line.b.y); y <= Math.max(line.a.y, line.b.y); y++) {
                    ps.add(new Point(x, y));
                }
            }
        }
        int minX = ps.stream().mapToInt(p -> p.x).min().getAsInt();
        int maxX = ps.stream().mapToInt(p -> p.x).max().getAsInt();
        int minY = ps.stream().mapToInt(p -> p.y).min().getAsInt();
        int maxY = ps.stream().mapToInt(p -> p.y).max().getAsInt();
        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                if (ps.contains(new Point(x, y))) {
                    System.out.print('#');
                } else {
                    System.out.print('.');
                }
            }
            System.out.println();
        }
    }

    private static List<Line> getOuterBorderOfRightHandPoly(List<Point> redTiles) {
        ArrayList<Line> acc = new ArrayList<>();

        for (int i = 0; i < redTiles.size(); i++) {
            // doing the line from r1 to r2
            Point r0 = redTiles.get((i - 1 + redTiles.size()) % redTiles.size());
            Point r1 = redTiles.get(i);
            Point r2 = redTiles.get((i + 1) % redTiles.size());
            Point r3 = redTiles.get((i + 2) % redTiles.size());

            int dir01 = getDir(r0, r1);
            int dir12 = getDir(r1, r2);
            int dir23 = getDir(r2, r3);
            boolean isRightTurnNext = ((dir12 + 1) % 4 == dir23);
            boolean isRightTurnPrev = ((dir01 + 1) % 4 == dir12);

            switch (dir12) {
                case 0: // right
                    acc.add(new Line(
                            new Point(r1.x - (isRightTurnPrev ? 1 : -1), r1.y - 1),
                            new Point(r2.x + (isRightTurnNext ? 1 : -1), r2.y - 1)));
                    break;
                case 1: // down
                    acc.add(new Line(
                            new Point(r1.x + 1, r1.y - (isRightTurnPrev ? 1 : -1)),
                            new Point(r2.x + 1, r2.y + (isRightTurnNext ? 1 : -1))));
                    break;
                case 2: // left
                    acc.add(new Line(
                            new Point(r1.x + (isRightTurnPrev ? 1 : -1), r1.y + 1),
                            new Point(r2.x - (isRightTurnNext ? 1 : -1), r2.y + 1)));
                    break;
                case 3: // up
                    acc.add(new Line(
                            new Point(r1.x - 1, r1.y + (isRightTurnPrev ? 1 : -1)),
                            new Point(r2.x - 1, r2.y - (isRightTurnNext ? 1 : -1))));
                    break;
                default:
                    throw new IllegalStateException();
            }
        }

        // I think there may be some bugs if the border overlaps closely with itself
        // but that doesn't seem to occur in the question input

        return acc;
    }

    private static boolean isRightHandPoly(List<Point> redTiles) {
        int leftTurns = 0;
        int rightTurns = 0;
        for (int i = 0; i < redTiles.size(); i++) {
            Point r1 = redTiles.get(i);
            Point r2 = redTiles.get((i + 1) % redTiles.size());
            Point r3 = redTiles.get((i + 2) % redTiles.size());

            int dir12 = getDir(r1, r2);
            int dir23 = getDir(r2, r3);
            if ((dir12 + 1) % 4 == dir23) {
                rightTurns++;
            } else if ((dir12 + 3) % 4 == dir23) {
                leftTurns++;
            } else {
                throw new IllegalStateException("Not a right angle turn");
            }
        }

        return leftTurns + 4 == rightTurns;
    }

    private static int getDir(Point r1, Point r2) {
        if (r2.x > r1.x && r2.y == r1.y) {
            return 0; // right
        } else if (r2.y > r1.y && r2.x == r1.x) {
            return 1; // down
        } else if (r2.x < r1.x && r2.y == r1.y) {
            return 2; // left
        } else if (r2.y < r1.y && r2.x == r1.x) {
            return 3; // up
        } else {
            throw new IllegalStateException();
        }
    }

    private static boolean isOutsideAllowedArea(Point a, Point b, List<Line> outerBorder) {
        List<Line> linesInRect = List.of(
                new Line(new Point(a.x, a.y), new Point(b.x, a.y)),
                new Line(new Point(b.x, a.y), new Point(b.x, b.y)),
                new Line(new Point(b.x, b.y), new Point(a.x, b.y)),
                new Line(new Point(a.x, b.y), new Point(a.x, a.y))
        );

        for (Line borderLine : outerBorder) {
            for (Line rectLine : linesInRect) {
                if (rectLine.intersects(borderLine)) {
                    return true;
                }
            }
        }
        return false;
    }

    static List<String> example = List.of(
            "7,1",
            "11,1",
            "11,7",
            "9,7",
            "9,5",
            "2,5",
            "2,3",
            "7,3");
}

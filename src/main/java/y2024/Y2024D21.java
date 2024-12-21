package y2024;

import com.google.common.base.Stopwatch;
import org.apache.commons.math3.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static aoc.Common.loadInputFromResources;
import static com.google.common.truth.Truth.assertThat;

public class Y2024D21 {

    public static void main(String[] args) throws Exception {
        List<String> input = loadInputFromResources();
        Stopwatch sw = Stopwatch.createStarted();

        minPressesForDirectionalInner("<A^A>^^AvvvA", 1);

        assertThat(getPossibleKeyPressesForNumericInput("029A"))
                .isEqualTo(List.of(
                        "<A^A>^^AvvvA",
                        "<A^A^>^AvvvA",
                        "<A^A^^>AvvvA"));

        assertThat(minPressesForDirectional("<", 0)).isEqualTo(4);
        assertThat(minPressesForDirectional("<A^A>^^AvvvA", 0)).isEqualTo("v<<A>>^A<A>AvA<^AA>A<vAAA>^A".length());
        assertThat(minPressesForDirectional("<A^A>^^AvvvA", 1)).isEqualTo("<vA<AA>>^AvAA<^A>A<v<A>>^AvA^A<vA>^A<v<A>^A>AAvA^A<v<A>A>^AAAvA<^A>A".length());

        assertThat(minPressesForChain("029A", 2)).isEqualTo("<vA<AA>>^AvAA<^A>A<v<A>>^AvA^A<vA>^A<v<A>^A>AAvA^A<v<A>A>^AAAvA<^A>A".length());

        assertThat(minPressesForChain("029A", 2)).isEqualTo("<vA<AA>>^AvAA<^A>A<v<A>>^AvA^A<vA>^A<v<A>^A>AAvA^A<v<A>A>^AAAvA<^A>A".length());
        assertThat(minPressesForChain("980A", 2)).isEqualTo("<v<A>>^AAAvA^A<vA<AA>>^AvAA<^A>A<v<A>A>^AAAvA<^A>A<vA>^A<A>A".length());
        assertThat(minPressesForChain("179A", 2)).isEqualTo("<v<A>>^A<vA<A>>^AAvAA<^A>A<v<A>>^AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A".length());
        assertThat(minPressesForChain("456A", 2)).isEqualTo("<v<A>>^AA<vA<A>>^AAvAA<^A>A<vA>^A<A>A<vA>^A<A>A<v<A>A>^AAvA<^A>A".length());
        assertThat(minPressesForChain("379A", 2)).isEqualTo("<v<A>>^AvA^A<vA<AA>>^AAvA<^A>AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A".length());

        // 1
        assertThat(part1(example)).isEqualTo(126384);
        assertThat(part1(input)).isEqualTo(222670);

        // 2
        System.out.println("starting part 2");
        assertThat(part2(input)).isEqualTo(271397390297138L);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static long minPressesForChain(String code, int depth) {
        return getPossibleKeyPressesForNumericInput(code).stream()
                .mapToLong(s -> minPressesForDirectional(s, depth - 1))
                .min().getAsLong();
    }

    private static long part1(List<String> input) {
        long acc = 0;
        for (String code : input) {
            long len = minPressesForChain(code, 2);
            int numericPartOfCode = Integer.parseInt(code.replace("A", ""));
            acc += len * numericPartOfCode;
        }
        return acc;
    }

    private static long part2(List<String> input) {
        long acc = 0;
        for (String code : input) {
            System.out.println("for code " + code);

            long len = minPressesForChain(code, 25);
            int numericPartOfCode = Integer.parseInt(code.replace("A", ""));
            acc += len * numericPartOfCode;
        }
        return acc;
    }

    private static Map<Pair<String, Integer>, Long> minPressesForDirectionalCache = new HashMap<>();

    private static long minPressesForDirectional(String code, int depth) {
        Pair<String, Integer> key = Pair.create(code, depth);
        Long cached = minPressesForDirectionalCache.get(key);
        if (cached == null) {
            long v = minPressesForDirectionalInner(code, depth);
            minPressesForDirectionalCache.put(key, v);
            return v;
        }
        return cached;
    }

    static Pattern groupEndingAPatt = Pattern.compile("[^A]*A+");

    private static long minPressesForDirectionalInner(String code, int depth) {
        if (depth == 0) {
            return getPossibleKeyPressesForDirectionalInput(code).stream()
                    .mapToInt(s -> s.length())
                    .min().getAsInt();
        }
        return getPossibleKeyPressesForDirectionalInput(code).stream()
                .mapToLong(s -> {

                    // split into groups ending A, recurse
                    assertThat(s).endsWith("A");
                    long acc = 0;
                    Matcher m = groupEndingAPatt.matcher(s);
                    while (m.find()) {
                        String g = m.group();
                        acc += minPressesForDirectional(g, depth - 1);
                    }

                    return acc;
                })
                .min().getAsLong();
    }

    // All different ways to type the given number code
    private static List<String> getPossibleKeyPressesForNumericInput(String code) {
        List<String> acc = new ArrayList<>();
        getPossibleKeyPressesForNumericInput(code, 2, 3, 0, "", acc);
        return acc;
    }

    private static void getPossibleKeyPressesForNumericInput(String code, int x, int y, int idx, String route, List<String> acc) {
        if (x == 0 && y == 3) {
            // avoid num pad gap
            return;
        }
        if (code.length() == idx) {
            acc.add(route);
        } else {
            Point target = numericPos(code.charAt(idx));
            if (target.x == x && target.y == y) {
                getPossibleKeyPressesForNumericInput(code, x, y, idx + 1, route + "A", acc);
            } else {
                if (x > target.x) {
                    getPossibleKeyPressesForNumericInput(code, x - 1, y, idx, route + "<", acc);
                }
                if (x < target.x) {
                    getPossibleKeyPressesForNumericInput(code, x + 1, y, idx, route + ">", acc);
                }
                if (y > target.y) {
                    getPossibleKeyPressesForNumericInput(code, x, y - 1, idx, route + "^", acc);
                }
                if (y < target.y) {
                    getPossibleKeyPressesForNumericInput(code, x, y + 1, idx, route + "v", acc);
                }
            }
        }
    }

    private static Point numericPos(char c) {
        switch (c) {
            case '7':
                return new Point(0, 0);
            case '8':
                return new Point(1, 0);
            case '9':
                return new Point(2, 0);
            case '4':
                return new Point(0, 1);
            case '5':
                return new Point(1, 1);
            case '6':
                return new Point(2, 1);
            case '1':
                return new Point(0, 2);
            case '2':
                return new Point(1, 2);
            case '3':
                return new Point(2, 2);
            case '0':
                return new Point(1, 3);
            case 'A':
                return new Point(2, 3);
            default:
                throw new IllegalStateException("Unexpected character " + c);
        }
    }

    private static Point directionalPos(char c) {
        switch (c) {
            case '^':
                return new Point(1, 0);
            case 'A':
                return new Point(2, 0);
            case '<':
                return new Point(0, 1);
            case 'v':
                return new Point(1, 1);
            case '>':
                return new Point(2, 1);
            default:
                throw new IllegalStateException("Unexpected character " + c);
        }
    }

    private static List<String> getPossibleKeyPressesForDirectionalInput(String target) {
        List<String> acc = new ArrayList<>();
        getPossibleKeyPressesForDirectionalInput(target, 2, 0, 0, "", acc);
        return acc;
    }

    private static void getPossibleKeyPressesForDirectionalInput(String code, int x, int y, int idx, String route, List<String> acc) {
        if (x == 0 && y == 0) {
            // avoid num pad gap
            return;
        }
        if (code.length() == idx) {
            acc.add(route);
        } else {
            Point target = directionalPos(code.charAt(idx));
            if (target.x == x && target.y == y) {
                getPossibleKeyPressesForDirectionalInput(code, x, y, idx + 1, route + "A", acc);
            } else {
                if (x > target.x) {
                    getPossibleKeyPressesForDirectionalInput(code, x - 1, y, idx, route + "<", acc);
                }
                if (x < target.x) {
                    getPossibleKeyPressesForDirectionalInput(code, x + 1, y, idx, route + ">", acc);
                }
                if (y > target.y) {
                    getPossibleKeyPressesForDirectionalInput(code, x, y - 1, idx, route + "^", acc);
                }
                if (y < target.y) {
                    getPossibleKeyPressesForDirectionalInput(code, x, y + 1, idx, route + "v", acc);
                }
            }
        }
    }

    static List<String> example = List.of(
            "029A",
            "980A",
            "179A",
            "456A",
            "379A");
}

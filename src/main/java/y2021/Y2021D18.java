package y2021;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2021D18 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            List<String> input = Resources.readLines(Resources.getResource("y2021/Y2021D18.txt"), StandardCharsets.UTF_8);

            // 1
            SnailNumberPair explodeExample1 = (SnailNumberPair) parse("[[[[[9,8],1],2],3],4]");
            explodeExample1.reduce();
            assertThat(explodeExample1.toString()).isEqualTo("[[[[0,9],2],3],4]");

            SnailNumber addExample1 = add(parse("[[[[4,3],4],4],[7,[[8,4],9]]]"), parse("[1,1]"));
            assertThat(addExample1.toString()).isEqualTo("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]");

            SnailNumber addExample2 = add(parse("[[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]"), parse("[7,[[[3,7],[4,3]],[[6,3],[8,8]]]]"));
            assertThat(addExample2.toString()).isEqualTo("[[[[4,0],[5,4]],[[7,7],[6,0]]],[[8,[7,7]],[[7,9],[5,0]]]]");

            SnailNumber addExample3 = add(parse("[[[[4,0],[5,4]],[[7,7],[6,0]]],[[8,[7,7]],[[7,9],[5,0]]]]"), parse("[[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]]"));
            assertThat(addExample3.toString()).isEqualTo("[[[[6,7],[6,7]],[[7,7],[0,7]]],[[[8,7],[7,7]],[[8,8],[8,0]]]]");

            assertThat(add(
                    parse("[[[[6,7],[6,7]],[[7,7],[0,7]]],[[[8,7],[7,7]],[[8,8],[8,0]]]]"),
                    parse("[[[[2,4],7],[6,[0,5]]],[[[6,8],[2,8]],[[2,1],[4,5]]]]")).toString())
                    .isEqualTo("[[[[7,0],[7,7]],[[7,7],[7,8]]],[[[7,7],[8,8]],[[7,7],[8,7]]]]");

            assertThat(add(
                    parse("[[[[7,0],[7,7]],[[7,7],[7,8]]],[[[7,7],[8,8]],[[7,7],[8,7]]]]"),
                    parse("[7,[5,[[3,8],[1,4]]]]")).toString())
                    .isEqualTo("[[[[7,7],[7,8]],[[9,5],[8,7]]],[[[6,8],[0,8]],[[9,9],[9,0]]]]");

            assertThat(add(
                    parse("[[[[7,7],[7,8]],[[9,5],[8,7]]],[[[6,8],[0,8]],[[9,9],[9,0]]]]"),
                    parse("[[2,[2,2]],[8,[8,1]]]")).toString())
                    .isEqualTo("[[[[6,6],[6,6]],[[6,0],[6,7]]],[[[7,7],[8,9]],[8,[8,1]]]]");

            assertThat(add(
                    parse("[[[[6,6],[6,6]],[[6,0],[6,7]]],[[[7,7],[8,9]],[8,[8,1]]]]"),
                    parse("[2,9]")).toString())
                    .isEqualTo("[[[[6,6],[7,7]],[[0,7],[7,7]]],[[[5,5],[5,6]],9]]");

            assertThat(add(
                    parse("[[[[6,6],[7,7]],[[0,7],[7,7]]],[[[5,5],[5,6]],9]]"),
                    parse("[1,[[[9,3],9],[[9,0],[0,7]]]]")).toString())
                    .isEqualTo("[[[[7,8],[6,7]],[[6,8],[0,8]]],[[[7,7],[5,0]],[[5,5],[5,6]]]]");

            assertThat(add(
                    parse("[[[[7,8],[6,7]],[[6,8],[0,8]]],[[[7,7],[5,0]],[[5,5],[5,6]]]]"),
                    parse("[[[5,[7,4]],7],1]")).toString())
                    .isEqualTo("[[[[7,7],[7,7]],[[8,7],[8,7]]],[[[7,0],[7,7]],9]]");

            assertThat(add(
                    parse("[[[[7,7],[7,7]],[[8,7],[8,7]]],[[[7,0],[7,7]],9]]"),
                    parse("[[[[4,2],2],6],[8,7]]")).toString())
                    .isEqualTo("[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]");


            assertThat(example.stream().map(x -> parse(x)).reduce((a, b) -> add(a, b)).get().toString())
                    .isEqualTo("[[[[6,6],[7,6]],[[7,7],[7,0]]],[[[7,7],[7,7]],[[7,8],[9,9]]]]");

            assertThat(part1(example)).isEqualTo(4140);
            assertThat(part1(input)).isEqualTo(4173);

            // 2
            assertThat(part2(example)).isEqualTo(3993);
            assertThat(part2(input)).isEqualTo(4706);

        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private static long part1(List<String> input) {
        return input.stream().map(s -> parse(s))
                .reduce((a, b) -> add(a, b))
                .get()
                .getMagnitude();
    }

    private static SnailNumber add(SnailNumber a, SnailNumber b) {
        SnailNumberPair sum = new SnailNumberPair(a, b);
        sum.reduce();
        return sum;
    }

    private static long part2(List<String> input) {
        long largestMag = 0;
        for (String a : input) {
            for (String b : input) {
                if (a != b) {
                    largestMag = Math.max(add(parse(a), parse(b)).getMagnitude(), largestMag);
                }
            }
        }
        return largestMag;
    }

    static JsonParser jsonParser = new JsonParser();

    static SnailNumber parse(String in) {
        JsonElement e = jsonParser.parse(in);
        return jsonToSnail(e);
    }

    private static SnailNumber jsonToSnail(JsonElement e) {
        if (e.isJsonArray()) {
            JsonArray a = (JsonArray) e;
            checkArgument(a.size() == 2);
            return new SnailNumberPair(jsonToSnail(a.get(0)), jsonToSnail(a.get(1)));
        } else if (e.isJsonPrimitive()) {
            return new SnailNumberNumber(e.getAsInt());
        } else {
            throw new IllegalArgumentException("bad input: " + e);
        }
    }

    interface SnailNumber {
        long getMagnitude();
    }

    @Data
    @AllArgsConstructor
    static class SnailNumberPair implements SnailNumber {
        SnailNumber left;
        SnailNumber right;

        @Override
        public long getMagnitude() {
            // The magnitude of a pair is 3 times the magnitude of its left
            // element plus 2 times the magnitude of its right element.
            return 3 * left.getMagnitude() + 2 * right.getMagnitude();
        }

        @FunctionalInterface
        interface Visitor {

            boolean visit(int depth, SnailNumberPair parent, SnailNumber it);
        }

        public boolean visit(int thisDepth, Visitor visitor) {
            if (!visitor.visit(thisDepth + 1, this, left)) {
                return false;
            }
            if (left instanceof SnailNumberPair) {
                if (!((SnailNumberPair) left).visit(thisDepth + 1, visitor)) {
                    return false;
                }
            }
            if (!visitor.visit(thisDepth + 1, this, right)) {
                return false;
            }
            if (right instanceof SnailNumberPair) {
                if (!((SnailNumberPair) right).visit(thisDepth + 1, visitor)) {
                    return false;
                }
            }
            return true;
        }

        public void reduce() {
            reduceOps:
            while (true) {
                // If any pair is nested inside four pairs, the leftmost such pair explodes.
                // Need to do a DFS to get the pairs in printing order
                AtomicReference<SnailNumberNumber> lastBeforeExplode = new AtomicReference<>();
                AtomicReference<SnailNumberPair> exploder = new AtomicReference<>();
                AtomicReference<SnailNumberPair> exploderParent = new AtomicReference<>();
                AtomicReference<SnailNumberNumber> nextAfterExplode = new AtomicReference<>();
                AtomicReference<SnailNumberNumber> firstGeTen = new AtomicReference<>();
                AtomicReference<SnailNumberPair> firstGeTenParent = new AtomicReference<>();
                this.visit(0, (depth, parent, it) -> {
                    if (exploder.get() == null) {
                        if (depth == 4 && it instanceof SnailNumberPair) {
                            exploder.set((SnailNumberPair) it);
                            exploderParent.set(parent);
                        } else if (it instanceof SnailNumberNumber) {
                            SnailNumberNumber n = (SnailNumberNumber) it;
                            lastBeforeExplode.set(n);
                            if (firstGeTen.get() == null && n.val >= 10) {
                                firstGeTen.set(n);
                                firstGeTenParent.set(parent);
                            }
                        }
                    } else {
                        if (parent != exploder.get() && it instanceof SnailNumberNumber) {
                            nextAfterExplode.set((SnailNumberNumber) it);
                            return false;
                        }
                    }
                    return true;
                });

                if (null != exploder.get()) {
                    // To explode a pair, the pair's left value is
                    // added to the first regular number to the left
                    // of the exploding pair (if any), and the pair's
                    // right value is added to the first regular
                    // number to the right of the exploding pair (if
                    // any). Exploding pairs will always consist of
                    // two regular numbers. Then, the entire exploding
                    // pair is replaced with the regular number 0.
                    if (lastBeforeExplode.get() != null) {
                        lastBeforeExplode.get().val += ((SnailNumberNumber) exploder.get().left).val;
                    }
                    if (nextAfterExplode.get() != null) {
                        nextAfterExplode.get().val += ((SnailNumberNumber) exploder.get().right).val;
                    }
                    exploderParent.get().replaceChild(exploder.get(), new SnailNumberNumber(0));
                    continue reduceOps;
                }

                // If any regular number is 10 or greater, the leftmost such regular number splits.
                if (null != firstGeTen.get()) {
                    // To split a regular number, replace it with a
                    // pair; the left element of the pair should be the
                    // regular number divided by two and rounded down,
                    // while the right element of the pair should be the
                    // regular number divided by two and rounded up.
                    long left = firstGeTen.get().val / 2;
                    long right = firstGeTen.get().val - left;
                    firstGeTenParent.get().replaceChild(
                            firstGeTen.get(),
                            new SnailNumberPair(new SnailNumberNumber(left), new SnailNumberNumber(right)));
                    continue reduceOps;
                }

                break;
            }
        }

        private void replaceChild(SnailNumber old, SnailNumber replacement) {
            if (left == old) {
                left = replacement;
            } else {
                checkState(right == old);
                right = replacement;
            }
        }

        @Override
        public String toString() {
            return "[" + left + "," + right + "]";
        }
    }

    @Data
    @AllArgsConstructor
    static class SnailNumberNumber implements SnailNumber {
        long val;

        @Override
        public long getMagnitude() {
            return val;
        }

        @Override
        public String toString() {
            return Long.toString(val);
        }
    }

    private static List<String> example = List.of(
            "[[[0,[5,8]],[[1,7],[9,6]]],[[4,[1,2]],[[1,4],2]]]",
            "[[[5,[2,8]],4],[5,[[9,9],0]]]",
            "[6,[[[6,2],[5,6]],[[7,6],[4,7]]]]",
            "[[[6,[0,7]],[0,9]],[4,[9,[9,0]]]]",
            "[[[7,[6,4]],[3,[1,3]]],[[[5,5],1],9]]",
            "[[6,[[7,3],[3,2]]],[[[3,8],[5,7]],4]]",
            "[[[[5,4],[7,7]],8],[[8,3],8]]",
            "[[9,3],[[9,9],[6,[4,9]]]]",
            "[[2,[[7,7],7]],[[5,8],[[9,3],[0,2]]]]",
            "[[[[5,2],5],[8,[3,7]]],[[5,[7,5]],[4,4]]]"
    );
}

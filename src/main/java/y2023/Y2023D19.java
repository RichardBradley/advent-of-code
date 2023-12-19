package y2023;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;
import lombok.Value;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2023D19 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            List<String> input = Resources.readLines(Resources.getResource("y2023/Y2023D19.txt"), StandardCharsets.UTF_8);

            // 1
            assertThat(part1(example)).isEqualTo(19114);
            assertThat(part1(input)).isEqualTo(406934);

            // 2
            assertThat(part2(example)).isEqualTo(167409079868000L);
            assertThat(part2(input)).isEqualTo(131192538505367L);

        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    @Value
    static class Step {
        String conditionVar;
        String op;
        Integer operand;
        String next;
    }

    static long part1(List<String> input) {
        Map<String, List<Step>> pipelines = new HashMap<>();
        Pattern pipelinePatt = Pattern.compile("(\\w+)\\{(.*)}");
        Pattern stepPatt = Pattern.compile("(([xmas])([<>])(\\d+):)?(\\w+)");
        int lineNo;
        for (lineNo = 0; lineNo < input.size(); lineNo++) {
            String line = input.get(lineNo);
            if ("".equals(line)) {
                lineNo++;
                break;
            }
            Matcher m = pipelinePatt.matcher(line);
            checkState(m.matches());
            String name = m.group(1);
            String stepsStr = m.group(2);
            List<Step> steps = Splitter.on(",").splitToList(stepsStr)
                    .stream().map(step -> {
                        Matcher m1 = stepPatt.matcher(step);
                        checkState(m1.matches());
                        return new Step(m1.group(2), m1.group(3), m1.group(4) == null ? null : Integer.parseInt(m1.group(4)), m1.group(5));
                    })
                    .collect(Collectors.toList());
            checkState(null == pipelines.put(name, steps));
        }

        long acc = 0;
        Pattern inputPatt = Pattern.compile("\\{x=(\\d+),m=(\\d+),a=(\\d+),s=(\\d+)}");
        inputLoop:
        for (; lineNo < input.size(); lineNo++) {
            String line = input.get(lineNo);
            Matcher matcher = inputPatt.matcher(line);
            checkState(matcher.matches());

            int x = Integer.parseInt(matcher.group(1));
            int m = Integer.parseInt(matcher.group(2));
            int a = Integer.parseInt(matcher.group(3));
            int s = Integer.parseInt(matcher.group(4));

            System.out.print(line + ": in -> ");

            List<Step> pipeline = pipelines.get("in");
            while (true) {
                stepLoop:
                for (Step step : pipeline) {
                    boolean isMatch;
                    if (step.conditionVar != null) {
                        int val;
                        switch (step.conditionVar) {
                            case "x":
                                val = x;
                                break;
                            case "m":
                                val = m;
                                break;
                            case "a":
                                val = a;
                                break;
                            case "s":
                                val = s;
                                break;
                            default:
                                throw new IllegalArgumentException();
                        }

                        switch (step.op) {
                            case ">":
                                isMatch = val > step.operand;
                                break;
                            case "<":
                                isMatch = val < step.operand;
                                break;
                            default:
                                throw new IllegalArgumentException();
                        }
                    } else {
                        isMatch = true;
                    }

                    if (isMatch) {
                        if ("A".equals(step.next)) {
                            acc += x + m + a + s;
                            System.out.println("A");
                            continue inputLoop;
                        } else if ("R".equals(step.next)) {
                            System.out.println("R");
                            continue inputLoop;
                        } else {
                            System.out.print(step.next + " -> ");

                            pipeline = pipelines.get(step.next);
                            break stepLoop; // step loop
                        }
                    }
                }
            }
        }
        return acc;
    }

    @Value
    static class Range {
        int fromInc;
        int toExcl;

        public static Optional<Range> createIfNonEmpty(int fromInc, int toExcl) {
            if (toExcl > fromInc) {
                return Optional.of(new Range(fromInc, toExcl));
            } else {
                return Optional.empty();
            }
        }

        public long size() {
            return toExcl - fromInc;
        }
    }

    @Value
    static class XmasRange {
        Range x, m, a, s;

        public long size() {
            return x.size() * m.size() * a.size() * s.size();
        }

        public Split splitOn(Step step) {
            switch (step.conditionVar) {
                case "x":
                    if ("<".equals(step.op)) {
                        Optional<Range> match = Range.createIfNonEmpty(x.fromInc, step.operand);
                        Optional<Range> noMatch = Range.createIfNonEmpty(step.operand, x.toExcl);
                        return new Split(
                                match.map(x -> new XmasRange(x, m, a, s)),
                                noMatch.map(x -> new XmasRange(x, m, a, s)));
                    } else {
                        Optional<Range> noMatch = Range.createIfNonEmpty(x.fromInc, step.operand + 1);
                        Optional<Range> match = Range.createIfNonEmpty(step.operand + 1, x.toExcl);
                        return new Split(
                                match.map(x -> new XmasRange(x, m, a, s)),
                                noMatch.map(x -> new XmasRange(x, m, a, s)));
                    }
                case "m":
                    if ("<".equals(step.op)) {
                        Optional<Range> match = Range.createIfNonEmpty(m.fromInc, step.operand);
                        Optional<Range> noMatch = Range.createIfNonEmpty(step.operand, m.toExcl);
                        return new Split(
                                match.map(m -> new XmasRange(x, m, a, s)),
                                noMatch.map(m -> new XmasRange(x, m, a, s)));
                    } else {
                        Optional<Range> noMatch = Range.createIfNonEmpty(m.fromInc, step.operand + 1);
                        Optional<Range> match = Range.createIfNonEmpty(step.operand + 1, m.toExcl);
                        return new Split(
                                match.map(m -> new XmasRange(x, m, a, s)),
                                noMatch.map(m -> new XmasRange(x, m, a, s)));
                    }
                case "a":
                    if ("<".equals(step.op)) {
                        Optional<Range> match = Range.createIfNonEmpty(a.fromInc, step.operand);
                        Optional<Range> noMatch = Range.createIfNonEmpty(step.operand, a.toExcl);
                        return new Split(
                                match.map(a -> new XmasRange(x, m, a, s)),
                                noMatch.map(a -> new XmasRange(x, m, a, s)));
                    } else {
                        Optional<Range> noMatch = Range.createIfNonEmpty(a.fromInc, step.operand + 1);
                        Optional<Range> match = Range.createIfNonEmpty(step.operand + 1, a.toExcl);
                        return new Split(
                                match.map(a -> new XmasRange(x, m, a, s)),
                                noMatch.map(a -> new XmasRange(x, m, a, s)));
                    }
                case "s":
                    if ("<".equals(step.op)) {
                        Optional<Range> match = Range.createIfNonEmpty(s.fromInc, step.operand);
                        Optional<Range> noMatch = Range.createIfNonEmpty(step.operand, s.toExcl);
                        return new Split(
                                match.map(s -> new XmasRange(x, m, a, s)),
                                noMatch.map(s -> new XmasRange(x, m, a, s)));
                    } else {
                        Optional<Range> noMatch = Range.createIfNonEmpty(s.fromInc, step.operand + 1);
                        Optional<Range> match = Range.createIfNonEmpty(step.operand + 1, s.toExcl);
                        return new Split(
                                match.map(s -> new XmasRange(x, m, a, s)),
                                noMatch.map(s -> new XmasRange(x, m, a, s)));
                    }
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    @Value
    static class Split {
        Optional<XmasRange> match;
        Optional<XmasRange> noMatch;
    }

    static long part2(List<String> input) {
        Map<String, List<Step>> pipelines = new HashMap<>();
        Pattern pipelinePatt = Pattern.compile("(\\w+)\\{(.*)}");
        Pattern stepPatt = Pattern.compile("(([xmas])([<>])(\\d+):)?(\\w+)");
        int lineNo;
        for (lineNo = 0; lineNo < input.size(); lineNo++) {
            String line = input.get(lineNo);
            if ("".equals(line)) {
                lineNo++;
                break;
            }
            Matcher m = pipelinePatt.matcher(line);
            checkState(m.matches());
            String name = m.group(1);
            String stepsStr = m.group(2);
            List<Step> steps = Splitter.on(",").splitToList(stepsStr)
                    .stream().map(step -> {
                        Matcher m1 = stepPatt.matcher(step);
                        checkState(m1.matches());
                        return new Step(m1.group(2), m1.group(3), m1.group(4) == null ? null : Integer.parseInt(m1.group(4)), m1.group(5));
                    })
                    .collect(Collectors.toList());
            checkState(null == pipelines.put(name, steps));
        }

        Range startingRange = new Range(1, 4001);
        return numberOfValidParts(new XmasRange(startingRange, startingRange, startingRange, startingRange), "in", 0, pipelines);
    }

    private static long numberOfValidParts(XmasRange xmasRange, String pipelineName, int stepIdx, Map<String, List<Step>> pipelines) {
        if ("R".equals(pipelineName)) {
            return 0;
        }
        if ("A".equals(pipelineName)) {
            return xmasRange.size();
        }
        List<Step> steps = pipelines.get(pipelineName);
        Step step = steps.get(stepIdx);
        if (step.conditionVar != null) {
            Split split = xmasRange.splitOn(step);
            return split.match.map(r -> numberOfValidParts(r, step.next, 0, pipelines)).orElse(0L)
                    + split.noMatch.map(r -> numberOfValidParts(r, pipelineName, stepIdx + 1, pipelines)).orElse(0L);
        } else {
            return numberOfValidParts(xmasRange, step.next, 0, pipelines);
        }
    }

    static List<String> example = List.of(
            "px{a<2006:qkq,m>2090:A,rfg}",
            "pv{a>1716:R,A}",
            "lnx{m>1548:A,A}",
            "rfg{s<537:gd,x>2440:R,A}",
            "qs{s>3448:A,lnx}",
            "qkq{x<1416:A,crn}",
            "crn{x>2662:A,R}",
            "in{s<1351:px,qqz}",
            "qqz{s>2770:qs,m<1801:hdj,R}",
            "gd{a>3333:R,R}",
            "hdj{m>838:A,pv}",
            "",
            "{x=787,m=2655,a=1222,s=2876}",
            "{x=1679,m=44,a=2067,s=496}",
            "{x=2036,m=264,a=79,s=2244}",
            "{x=2461,m=1339,a=466,s=291}",
            "{x=2127,m=1623,a=2188,s=1013}"
    );
}

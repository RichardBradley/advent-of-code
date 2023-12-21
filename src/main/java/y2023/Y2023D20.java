package y2023;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Iterables;
import com.google.common.io.Resources;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;
import static org.apache.commons.math3.util.ArithmeticUtils.lcm;

public class Y2023D20 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            List<String> input = Resources.readLines(Resources.getResource("y2023/Y2023D20.txt"), StandardCharsets.UTF_8);

            // 1
            assertThat(part1(example1)).isEqualTo(32000000);
            assertThat(part1(example2)).isEqualTo(11687500);
            assertThat(part1(input)).isEqualTo(1020211150);

            // 2
            // 1020211150 low
            // 2147483647 low
            assertThat(part2(input)).isEqualTo(0);

        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    @AllArgsConstructor
    abstract static class Node {
        protected String type;
        protected String name;
        protected List<String> outputs;

        abstract void receive(Pulse pulse, Consumer<Pulse> onSend);
    }

    static class Broadcast extends Node {

        public Broadcast(String type, String name, List<String> outputs) {
            super(type, name, outputs);
        }

        void receive(Pulse pulse, Consumer<Pulse> onSend) {
            for (String output : outputs) {
                onSend.accept(new Pulse(pulse.isHigh, name, output));
            }
        }
    }

    static class FlipFlop extends Node {
        boolean state = false;

        public FlipFlop(String type, String name, List<String> outputs) {
            super(type, name, outputs);
        }

        void receive(Pulse pulse, Consumer<Pulse> onSend) {
            if (pulse.isHigh) {
                // do nothing
            } else {
                state = !state;
                for (String output : outputs) {
                    onSend.accept(new Pulse(state, name, output));
                }
            }
        }
    }

    static class Conjunction extends Node {
        Map<String, Boolean> lastReceivedByName = new HashMap<>();

        public Conjunction(String type, String name, List<String> outputs) {
            super(type, name, outputs);
        }

        void initInput(String from) {
            lastReceivedByName.put(from, false);
        }

        void receive(Pulse pulse, Consumer<Pulse> onSend) {
            lastReceivedByName.put(pulse.from, pulse.isHigh);
            if (lastReceivedByName.values().stream().allMatch(p -> p)) {
                for (String output : outputs) {
                    onSend.accept(new Pulse(false, name, output));
                }
            } else {
                for (String output : outputs) {
                    onSend.accept(new Pulse(true, name, output));
                }
            }
        }
    }

    @Value
    static class Pulse {
        boolean isHigh;
        String from;
        String to;
    }


    static long part1(List<String> input) {
        Map<String, Node> nodes = parse(input);

        Queue<Pulse> pulses = new ArrayDeque<>();
        AtomicLong lowPulsesSent = new AtomicLong();
        AtomicLong highPulsesSent = new AtomicLong();
        for (long buttonPressCount = 1; buttonPressCount <= 1000; buttonPressCount++) {
            pulses.add(new Pulse(false, "button", "broadcaster"));
            lowPulsesSent.incrementAndGet();
            Pulse pulse;
            while (null != (pulse = pulses.poll())) {
                Node node = nodes.get(pulse.to);
                if (node == null) {
                    // output or rx
                } else {
                    node.receive(pulse, newPulse -> {
                        if (newPulse.isHigh) {
                            highPulsesSent.incrementAndGet();
                        } else {
                            lowPulsesSent.incrementAndGet();
                        }
                        pulses.add(newPulse);
                    });
                }
            }
        }

        return lowPulsesSent.get() * highPulsesSent.get();
    }

    // output is an "&" across multiple nodes
    // need to find when each input to that sends one high pulse
    static long part2(List<String> input) {
        Map<String, Node> nodes = parse(input);

        Node preOutput = Iterables.getOnlyElement(nodes.values().stream()
                .filter(n -> n.outputs.contains("rx"))
                .collect(Collectors.toList()));

        checkState(preOutput instanceof Conjunction);
        Set<String> inputs = ((Conjunction) preOutput).lastReceivedByName.keySet();

        List<Long> loopLengths = new ArrayList<>();
        for (String inputNode : inputs) {
            long period = findPeriodAt(input, inputNode);
            loopLengths.add(period);
        }

        return loopLengths.stream()
                .reduce(1L, (a, b) -> lcm(a, b));
    }

    private static long findPeriodAt(List<String> input, String targetNode) {
        Map<String, Node> nodes = parse(input);
        Queue<Pulse> pulses = new ArrayDeque<>();

        for (long buttonPressCount = 1; ; buttonPressCount++) {
            List<Pulse> outputPulses = new ArrayList<>();
            pulses.add(new Pulse(false, "button", "broadcaster"));
            Pulse pulse;
            while (null != (pulse = pulses.poll())) {
                if (pulse.from.equals(targetNode)) {
                    outputPulses.add(pulse);
                }
                Node node = nodes.get(pulse.to);
                if (node == null) {
                    // output or rx
                } else {
                    node.receive(pulse, newPulse -> {
                        pulses.add(newPulse);
                    });
                }
            }

            if (outputPulses.size() == 1
                    && outputPulses.get(0).isHigh) {
                return buttonPressCount;
            }
//            if (outputPulses.size() > 0 && outputPulses.get(outputPulses.size() - 1).isHigh) {
//                return buttonPressCount;
//            }
            System.out.printf("Checking %s, step %s outputs %s\n", targetNode, buttonPressCount, outputPulses);
        }
    }

    private static Map<String, Node> parse(List<String> input) {
        Map<String, Node> nodes = new HashMap<>();
        Pattern p = Pattern.compile("([b%&])(\\w+) -> ([\\w, ]+)");
        for (String line : input) {
            Matcher m = p.matcher(line);
            checkState(m.matches());
            String type = m.group(1);
            String name = m.group(2);
            if (type.equals("b")) {
                name = "b" + name;
            }
            List<String> outputs = Splitter.on(", ").splitToList(m.group(3));
            switch (type) {
                case "b":
                    checkState(null == nodes.put(name, new Broadcast(type, name, outputs)));
                    break;
                case "%":
                    checkState(null == nodes.put(name, new FlipFlop(type, name, outputs)));
                    break;
                case "&":
                    checkState(null == nodes.put(name, new Conjunction(type, name, outputs)));
                    break;
                default:
                    throw new IllegalStateException();
            }
        }
        for (Map.Entry<String, Node> nodeEntry : nodes.entrySet()) {
            String from = nodeEntry.getKey();
            for (String output : nodeEntry.getValue().outputs) {
                Node to = nodes.get(output);
                if (to instanceof Conjunction) {
                    ((Conjunction) to).initInput(from);
                }
            }
        }
        return nodes;
    }

    static List<String> example1 = List.of(
            "broadcaster -> a, b, c",
            "%a -> b",
            "%b -> c",
            "%c -> inv",
            "&inv -> a"
    );

    static List<String> example2 = List.of(
            "broadcaster -> a",
            "%a -> inv, con",
            "&inv -> b",
            "%b -> con",
            "&con -> output"
    );
}

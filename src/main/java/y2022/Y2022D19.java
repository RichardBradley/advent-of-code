package y2022;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;
import lombok.Value;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2022D19 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        List<String> input = Resources.readLines(Resources.getResource("y2022/Y2022D19.txt"), StandardCharsets.UTF_8);

        // 1
        assertThat(run(example, false)).isEqualTo(33);
        System.out.println(run(input, false)); // 2341, 6 min

        // 2
        assertThat(run(example, true)).isEqualTo(56 * 62);
        System.out.println(run(input, true)); // 3689, 17 sec

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    @Value
    static class Blueprint {
        int id;
        int oreRobotCostOre;
        int clayRobotCostOre;
        int obsidianRobotCostOre;
        int obsidianRobotCostClay;
        int geodeRobotCostOre;
        int geodeRobotCostObsidian;
        final int maxOreSpend;

        public Blueprint(int id,
                         int oreRobotCostOre,
                         int clayRobotCostOre,
                         int obsidianRobotCostOre,
                         int obsidianRobotCostClay,
                         int geodeRobotCostOre,
                         int geodeRobotCostObsidian) {
            this.id = id;
            this.oreRobotCostOre = oreRobotCostOre;
            this.clayRobotCostOre = clayRobotCostOre;
            this.obsidianRobotCostOre = obsidianRobotCostOre;
            this.obsidianRobotCostClay = obsidianRobotCostClay;
            this.geodeRobotCostOre = geodeRobotCostOre;
            this.geodeRobotCostObsidian = geodeRobotCostObsidian;

            this.maxOreSpend = IntStream.of(
                    clayRobotCostOre,
                    oreRobotCostOre,
                    geodeRobotCostOre,
                    obsidianRobotCostOre).max().getAsInt();
        }
    }

    private static int run(List<String> input, boolean isPart2) {
        List<Blueprint> blueprints = parse(input);
        if (isPart2) {
            blueprints = blueprints.subList(0, Math.min(3, blueprints.size()));
        }
        int totalQualityLevel = 0;
        int largestGeodeOutProduct = 1;

        for (Blueprint blueprint : blueprints) {

            int endTime = isPart2 ? 32 : 24;
            State start = new State(0, 0, 1, 0, 0, 0, 0, 0, 0);
            PriorityQueue<SearchState> queue = new PriorityQueue<>(Collections.singleton(
                    new SearchState(start)));
            Map<State, Integer> bestScoresByState = new HashMap<>();
            int bestFinalScore = 0;
            AtomicInteger visitCount = new AtomicInteger();
            AtomicInteger pruneByExactState = new AtomicInteger();
            int pruneByProjectedScore = 0;

            while (true) {
                SearchState curr = queue.poll();
                if (curr == null) {
                    totalQualityLevel += blueprint.id * bestFinalScore;
                    largestGeodeOutProduct = Math.multiplyExact(largestGeodeOutProduct, bestFinalScore);
                    break;
                }

                if (curr.state.time == endTime) {
                    if (curr.state.geodeCount > bestFinalScore) {
                        bestFinalScore = curr.state.geodeCount;
                    }
                } else {
                    checkState(curr.state.time < endTime);

                    // prune if can't do better than current best
                    int remainingTime = endTime - curr.state.time;
                    int maxRemainingScore = (int) (remainingTime
                            * (2 * curr.state.geodeRobotCount + remainingTime)
                            / 2.0);
                    if (curr.state.geodeCount + maxRemainingScore < bestFinalScore) {
                        pruneByProjectedScore++;
                        continue;
                    }

                    bestScoresByState.compute(curr.state, (k, oldBestScore) -> {
                        if (oldBestScore == null || oldBestScore < curr.state.geodeCount) {
                            curr.visitPossibleNextStates(blueprint, queue::add);
                            visitCount.incrementAndGet();
                            return curr.state.geodeCount;
                        } else {
                            // already reached better
                            pruneByExactState.incrementAndGet();

                            return oldBestScore;
                        }
                    });
                }
            }

            System.out.printf(
                    "Done %s, %s%% pruned by exact, %s pruned by projected score\n",
                    blueprint.id,
                    100.0 * pruneByExactState.get() / (visitCount.get() + (double) pruneByExactState.get()),
                    pruneByProjectedScore);
        }

        return isPart2 ? largestGeodeOutProduct : totalQualityLevel;
    }

    @Value
    static class State {
        int time;
        int oreCount;
        int oreRobotCount;
        int clayCount;
        int clayRobotCount;
        int obsidianCount;
        int obsidianRobotCount;
        int geodeCount;
        int geodeRobotCount;
    }

    @Value
    static class SearchState implements Comparable<SearchState> {
        State state;

        @Override
        public int compareTo(SearchState o) {
            return -Integer.compare(this.state.geodeCount, o.state.geodeCount);
        }

        public void visitPossibleNextStates(Blueprint blueprint, Consumer<SearchState> callback) {
            // Can make one of 4 robots
            // Don't bother making an intermediate robot if we can't spend what it would make
            if (state.oreCount >= blueprint.oreRobotCostOre
                    && state.oreRobotCount < blueprint.maxOreSpend) {
                callback.accept(new SearchState(new State(
                        state.time + 1,
                        state.oreCount + state.oreRobotCount - blueprint.oreRobotCostOre,
                        state.oreRobotCount + 1,
                        state.clayCount + state.clayRobotCount,
                        state.clayRobotCount,
                        state.obsidianCount + state.obsidianRobotCount,
                        state.obsidianRobotCount,
                        state.geodeCount + state.geodeRobotCount,
                        state.geodeRobotCount)));
            }
            // clay robot
            if (state.oreCount >= blueprint.clayRobotCostOre
                    && state.clayRobotCount < blueprint.obsidianRobotCostClay) {
                callback.accept(new SearchState(new State(
                        state.time + 1,
                        state.oreCount + state.oreRobotCount - blueprint.clayRobotCostOre,
                        state.oreRobotCount,
                        state.clayCount + state.clayRobotCount,
                        state.clayRobotCount + 1,
                        state.obsidianCount + state.obsidianRobotCount,
                        state.obsidianRobotCount,
                        state.geodeCount + state.geodeRobotCount,
                        state.geodeRobotCount)));
            }
            // obs robot
            if (state.oreCount >= blueprint.obsidianRobotCostOre
                    && state.clayCount >= blueprint.obsidianRobotCostClay
                    && state.obsidianRobotCount < blueprint.geodeRobotCostObsidian) {
                callback.accept(new SearchState(new State(
                        state.time + 1,
                        state.oreCount + state.oreRobotCount - blueprint.obsidianRobotCostOre,
                        state.oreRobotCount,
                        state.clayCount + state.clayRobotCount - blueprint.obsidianRobotCostClay,
                        state.clayRobotCount,
                        state.obsidianCount + state.obsidianRobotCount,
                        state.obsidianRobotCount + 1,
                        state.geodeCount + state.geodeRobotCount,
                        state.geodeRobotCount)));
            }
            if (state.oreCount >= blueprint.geodeRobotCostOre
                    && state.obsidianCount >= blueprint.geodeRobotCostObsidian) {
                callback.accept(new SearchState(new State(
                        state.time + 1,
                        state.oreCount + state.oreRobotCount - blueprint.geodeRobotCostOre,
                        state.oreRobotCount,
                        state.clayCount + state.clayRobotCount,
                        state.clayRobotCount,
                        state.obsidianCount + state.obsidianRobotCount - blueprint.geodeRobotCostObsidian,
                        state.obsidianRobotCount,
                        state.geodeCount + state.geodeRobotCount,
                        state.geodeRobotCount + 1)));
            }
            callback.accept(new SearchState(new State(
                    state.time + 1,
                    state.oreCount + state.oreRobotCount,
                    state.oreRobotCount,
                    state.clayCount + state.clayRobotCount,
                    state.clayRobotCount,
                    state.obsidianCount + state.obsidianRobotCount,
                    state.obsidianRobotCount,
                    state.geodeCount + state.geodeRobotCount,
                    state.geodeRobotCount)));
        }
    }

    private static List<Blueprint> parse(List<String> input) {
        List<Blueprint> acc = new ArrayList<>();
        Pattern p = Pattern.compile("Blueprint (\\d+): Each ore robot costs (\\d+) ore. Each clay robot costs (\\d+) ore. Each obsidian robot costs (\\d+) ore and (\\d+) clay. Each geode robot costs (\\d+) ore and (\\d+) obsidian.");
        for (String line : input) {
            Matcher m = p.matcher(line);
            checkState(m.matches());
            assertThat(Integer.parseInt(m.group(1))).isEqualTo(acc.size() + 1);
            acc.add(new Blueprint(
                    Integer.parseInt(m.group(1)),
                    Integer.parseInt(m.group(2)),
                    Integer.parseInt(m.group(3)),
                    Integer.parseInt(m.group(4)),
                    Integer.parseInt(m.group(5)),
                    Integer.parseInt(m.group(6)),
                    Integer.parseInt(m.group(7))));
        }
        return acc;
    }

    private static List<String> example = List.of(
            "Blueprint 1: " +
                    "Each ore robot costs 4 ore. " +
                    "Each clay robot costs 2 ore. " +
                    "Each obsidian robot costs 3 ore and 14 clay. " +
                    "Each geode robot costs 2 ore and 7 obsidian.",
            "Blueprint 2: " +
                    "Each ore robot costs 2 ore. " +
                    "Each clay robot costs 3 ore. " +
                    "Each obsidian robot costs 3 ore and 8 clay. " +
                    "Each geode robot costs 3 ore and 12 obsidian.");
}

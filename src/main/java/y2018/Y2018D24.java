package y2018;

import com.google.common.base.Stopwatch;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.Value;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;
import static java.util.stream.Collectors.toList;

public class Y2018D24 {
    public static void main(String[] args) {
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(-countSurvivors(testInput)).isEqualTo(5216);

        System.out.println(-countSurvivors(input));

        // 2
        assertThat(countSurvivors(testInput, 1570)).isEqualTo(51);

        System.out.println(findMinBoost(input));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    static final boolean LOG = false;

    private static int findMinBoost(Scenario scenario) {
        for (int i = 0; ; i++) {
            System.out.print("Testing boost value " + i + ": ");
            int survivors = countSurvivors(scenario, i);
            System.out.println(survivors);
            if (survivors > 0) {
                return survivors;
            }
        }
    }

    private static int countSurvivors(Scenario scenario, int boost) {
        scenario = scenario.clone();
        for (UnitGroup unitGroup : scenario.immuneSystem) {
            unitGroup.attackDamage += boost;
        }
        return countSurvivors(scenario);
    }

    private static int countSurvivors(Scenario scenario) {

        while (!scenario.immuneSystem.isEmpty() && !scenario.infection.isEmpty()) {
            if (LOG) {
                System.out.println("###########");
                System.out.println("Immune System:");
                scenario.immuneSystem.forEach(ug -> {
                    System.out.println(String.format(
                            "Group %s contains %s units, power = %s, init = %s",
                            ug.groupNumber,
                            ug.count,
                            ug.getEffectivePower(),
                            ug.initiative));
                });
                System.out.println("Infection System:");
                scenario.infection.forEach(ug -> {
                    System.out.println(String.format(
                            "Group %s contains %s units, power = %s, init = %s",
                            ug.groupNumber,
                            ug.count,
                            ug.getEffectivePower(),
                            ug.initiative));
                });
                System.out.println();
            }

            // target selection

            // In decreasing order of effective power, groups choose their targets;
            // in a tie, the group with the higher initiative chooses first.

            Set<UnitGroup> alreadyTargeted = new HashSet<>();

            List<TargetChoice> targets = Stream.concat(
                    scenario.immuneSystem.stream(),
                    scenario.infection.stream())
                    .sorted(targetSelectionOrder)
                    .map(ug -> {
                        Optional<UnitGroup> maybeTarget = (ug.isImmuneSystem ? scenario.infection : scenario.immuneSystem)
                                .stream()
                                // Defending groups can only be chosen as a target by one attacking group.
                                .filter(x -> !alreadyTargeted.contains(x))
                                // If it cannot deal any defending groups damage, it does not choose a target.
                                .filter(x -> ug.getDamageTo(x) > 0)
                                .min(ug.targetChoiceOrder());

                        if (maybeTarget.isPresent()) {
                            UnitGroup target = maybeTarget.get();
                            if (LOG) {
                                System.out.println(String.format(
                                        "%s group %s would deal defending group %s %s damage",
                                        (ug.isImmuneSystem ? "Immune System" : "Infection"),
                                        ug.groupNumber,
                                        target.groupNumber,
                                        ug.getDamageTo(target)));
                            }

                            alreadyTargeted.add(target);

                            return new TargetChoice(
                                    ug,
                                    target);
                        } else {
                            if (LOG) {
                                System.out.println(String.format(
                                        "%s group %s has no valid target",
                                        (ug.isImmuneSystem ? "Immune System" : "Infection"),
                                        ug.groupNumber));
                            }

                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(toList());

            if (LOG) {
                System.out.println();
            }

            AtomicBoolean anyUnitsKilled = new AtomicBoolean(false);

            // attacking
            targets.stream()
                    .sorted(Comparator.comparing((TargetChoice x) -> x.attacker.initiative).reversed())
                    .forEach(choice -> {
                        if (choice.attacker.count > 0) {
                            int damage = choice.attacker.getDamageTo(choice.target);
                            int kills = Math.min(choice.target.count, damage / choice.target.hitPointsPerUnit);
                            choice.target.count -= kills;

                            anyUnitsKilled.compareAndSet(false, (kills > 0));

                            if (choice.target.count == 0) {
                                (choice.target.isImmuneSystem ? scenario.immuneSystem : scenario.infection)
                                        .remove(choice.target);
                            }

                            if (LOG) {
                                System.out.println(String.format(
                                        "%s group %s attacks defending group %s, killing %s units",
                                        (choice.attacker.isImmuneSystem ? "Immune System" : "Infection"),
                                        choice.attacker.groupNumber,
                                        choice.target.groupNumber,
                                        kills));
                            }
                        }
                    });

            if (!anyUnitsKilled.get()) {
                if (LOG) {
                    System.out.println("Stalemate");
                }
                return 0;
            }
        }

        return scenario.immuneSystem.stream().mapToInt(x -> x.count).sum()
                - scenario.infection.stream().mapToInt(x -> x.count).sum();
    }

    static Comparator<UnitGroup> targetSelectionOrder =
            Comparator.comparing((UnitGroup u) -> -u.getEffectivePower())
                    .thenComparing(u -> -u.initiative);

    @Value
    static class TargetChoice {
        UnitGroup attacker;
        UnitGroup target;
    }

    @Value
    static class Scenario {
        List<UnitGroup> immuneSystem;
        List<UnitGroup> infection;

        public Scenario clone() {
            return new Scenario(
                    immuneSystem.stream().map(x -> x.clone()).collect(toList()),
                    infection.stream().map(x -> x.clone()).collect(toList()));
        }
    }

    @Data
    @AllArgsConstructor
    static class UnitGroup implements Cloneable {
        int count;
        int hitPointsPerUnit;
        Set<String> weaknesses;
        Set<String> immunities;
        int attackDamage;
        String damageType;
        int initiative;
        boolean isImmuneSystem;
        int groupNumber;

        public int getEffectivePower() {
            return count * attackDamage;
        }

        Comparator<UnitGroup> targetChoiceOrder() {
            return Comparator
                    .comparing((UnitGroup x) -> -this.getDamageTo(x))
                    .thenComparing((UnitGroup x) -> -x.getEffectivePower())
                    .thenComparing((UnitGroup x) -> -x.initiative);
        }

        public int getDamageTo(UnitGroup other) {
            if (other.immunities.contains(damageType)) {
                return 0;
            }
            if (other.weaknesses.contains(damageType)) {
                return 2 * getEffectivePower();
            }
            return getEffectivePower();
        }

        @SneakyThrows
        public UnitGroup clone() {
            return (UnitGroup) super.clone();
        }
    }

    private static Scenario parse(String spec) {

        String[] lines = spec.split("\n");
        checkState(lines[0].equals("Immune System:"));
        List<UnitGroup> immuneSystem = new ArrayList<>();

        int idx = 1;
        int groupNumber = 1;
        for (; ; idx++) {
            if (lines[idx].equals("")) {
                break;
            }
            immuneSystem.add(parseUnitGroup(lines[idx], true, groupNumber++));
        }

        checkState(lines[++idx].equals("Infection:"));
        idx++;
        List<UnitGroup> infection = new ArrayList<>();
        groupNumber = 1;

        for (; idx < lines.length; idx++) {
            infection.add(parseUnitGroup(lines[idx], false, groupNumber++));
        }

        return new Scenario(
                immuneSystem,
                infection);
    }

    private static UnitGroup parseUnitGroup(String spec, boolean isImmuneSystem, int groupNumber) {
        Pattern pattern = Pattern.compile("(\\d+) units each with (\\d+) hit points (?:\\(([\\w ,;]+)\\) )?with an attack that does (\\d+) (\\w+) damage at initiative (\\d+)");

        Matcher matcher = pattern.matcher(spec);
        checkState(matcher.matches());

        Set<String> weaknesses = Collections.emptySet();
        Set<String> immunities = Collections.emptySet();
        if (matcher.group(3) != null) {
            String[] extrasSpecs = matcher.group(3).split("; ");
            for (String extrasSpec : extrasSpecs) {
                if (extrasSpec.startsWith("immune to ")) {
                    immunities = Arrays.stream(extrasSpec.substring("immune to ".length())
                            .split(", ")).collect(Collectors.toSet());
                } else {
                    checkState(extrasSpec.startsWith("weak to "));
                    weaknesses = Arrays.stream(extrasSpec.substring("weak to ".length())
                            .split(", ")).collect(Collectors.toSet());
                }
            }
        }

        return new UnitGroup(
                Integer.parseInt(matcher.group(1)),
                Integer.parseInt(matcher.group(2)),
                weaknesses,
                immunities,
                Integer.parseInt(matcher.group(4)),
                matcher.group(5),
                Integer.parseInt(matcher.group(6)),
                isImmuneSystem,
                groupNumber);
    }

    static Scenario testInput = parse(
            "Immune System:\n" +
                    "17 units each with 5390 hit points (weak to radiation, bludgeoning) with an attack that does 4507 fire damage at initiative 2\n" +
                    "989 units each with 1274 hit points (immune to fire; weak to bludgeoning, slashing) with an attack that does 25 slashing damage at initiative 3\n" +
                    "\n" +
                    "Infection:\n" +
                    "801 units each with 4706 hit points (weak to radiation) with an attack that does 116 bludgeoning damage at initiative 1\n" +
                    "4485 units each with 2961 hit points (immune to radiation; weak to fire, cold) with an attack that does 12 slashing damage at initiative 4");

    static Scenario input = parse(
            "Immune System:\n" +
                    "1514 units each with 8968 hit points (weak to cold) with an attack that does 57 bludgeoning damage at initiative 9\n" +
                    "2721 units each with 6691 hit points (weak to cold) with an attack that does 22 slashing damage at initiative 15\n" +
                    "1214 units each with 10379 hit points (immune to bludgeoning) with an attack that does 69 fire damage at initiative 16\n" +
                    "2870 units each with 4212 hit points with an attack that does 11 radiation damage at initiative 12\n" +
                    "1239 units each with 5405 hit points (weak to cold) with an attack that does 37 cold damage at initiative 18\n" +
                    "4509 units each with 4004 hit points (weak to cold; immune to radiation) with an attack that does 8 slashing damage at initiative 20\n" +
                    "3369 units each with 10672 hit points (weak to slashing) with an attack that does 29 cold damage at initiative 11\n" +
                    "2890 units each with 11418 hit points (weak to fire; immune to bludgeoning) with an attack that does 30 cold damage at initiative 8\n" +
                    "149 units each with 7022 hit points (weak to slashing) with an attack that does 393 radiation damage at initiative 13\n" +
                    "2080 units each with 5786 hit points (weak to fire; immune to slashing, bludgeoning) with an attack that does 20 fire damage at initiative 7\n" +
                    "\n" +
                    "Infection:\n" +
                    "817 units each with 47082 hit points (immune to slashing, radiation, bludgeoning) with an attack that does 115 cold damage at initiative 3\n" +
                    "4183 units each with 35892 hit points with an attack that does 16 bludgeoning damage at initiative 1\n" +
                    "7006 units each with 11084 hit points with an attack that does 2 fire damage at initiative 2\n" +
                    "4804 units each with 25411 hit points with an attack that does 10 cold damage at initiative 14\n" +
                    "6262 units each with 28952 hit points (weak to fire) with an attack that does 7 slashing damage at initiative 10\n" +
                    "628 units each with 32906 hit points (weak to slashing) with an attack that does 99 radiation damage at initiative 4\n" +
                    "5239 units each with 46047 hit points (immune to fire) with an attack that does 14 bludgeoning damage at initiative 6\n" +
                    "1173 units each with 32300 hit points (weak to cold, slashing) with an attack that does 53 bludgeoning damage at initiative 19\n" +
                    "3712 units each with 12148 hit points (immune to cold; weak to slashing) with an attack that does 5 slashing damage at initiative 17\n" +
                    "334 units each with 43582 hit points (weak to cold, fire) with an attack that does 260 cold damage at initiative 5");

}
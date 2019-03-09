package y2015;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import lombok.Value;
import lombok.experimental.Wither;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Collections.emptyList;
import static y2015.Y2015D22.Spells.*;

public class Y2015D22 {

    static boolean LOG = false;
    static StringBuilder logBuffer = new StringBuilder();

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        // 1

        // For example, suppose the player has 10 hit points and 250
        // mana, and that the boss has 13 hit points and 8 damage:
        System.out.println("########################### Example 1");
        GameStateOpen example1 = new GameStateOpen(
                new player(10, 0, 250),
                new boss(13, 8),
                emptyList(),
                0,
                emptyList());
        String log1 = findLeastAmountOfManaToWinAndPrintLog(false, example1);
//        assertThat(log1).isEqualTo(exampleLog1);

        System.out.println("########################### Example 2");

        GameStateOpen example2 = new GameStateOpen(
                new player(10, 0, 250),
                new boss(14, 8),
                emptyList(),
                0,
                emptyList());
        String log2 = findLeastAmountOfManaToWinAndPrintLog(false, example2);
        System.out.println("Price of log2 = " + getManaSpendOfLog(log2));
//        assertThat(log2).isEqualTo(exampleLog2);

        System.out.println("########################### Problem 1");

        // You start with 50 hit points and 500 mana points. The
        // boss's actual stats are in your puzzle input. What is the
        // least amount of mana you can spend and still win the fight?

        System.out.println(findLeastAmountOfManaToWin(
                false,
                new GameStateOpen(
                        new player(50, 0, 500),
                        boss,
                        emptyList(),
                        0,
                        emptyList())));

        // 2
        System.out.println("########################### Problem 2");

        System.out.println(findLeastAmountOfManaToWinAndPrintLog(
                true,
                new GameStateOpen(
                        new player(50, 0, 500),
                        boss,
                        emptyList(),
                        0,
                        emptyList())));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static int getManaSpendOfLog(String battleLog) {
        Pattern pattern = Pattern.compile("Player casts ([\\w ]+)");
        Matcher matcher = pattern.matcher(battleLog);
        int spent = 0;
        while (matcher.find()) {
            Spell spell = Arrays.stream(Spells.ALL).filter(s -> s.getName().equals(matcher.group(1))).findFirst().get();
            spent += spell.getManaCost();
        }
        return spent;
    }

    private static String findLeastAmountOfManaToWinAndPrintLog(boolean hardMode, GameStateOpen initialState) {
        LOG = false;
        Won win = findLeastAmountOfManaToWin(hardMode, initialState);
        System.out.println("##= " + win);
        LOG = true;
        logBuffer = new StringBuilder();
        runTurns(hardMode, initialState, win.spellHistory);
        LOG = false;
        return logBuffer.toString().trim();
    }


    private static Won findLeastAmountOfManaToWin(boolean hardMode, GameStateOpen initialState) {
        // Dijkstra:
        PriorityQueue<GameState> queue = new PriorityQueue<>(Comparator.comparing(GameState::getManaSpent));
        queue.add(initialState);

        while (true) {
            GameState current = queue.poll();

            if (current instanceof Won) {
                return (Won) current;
            }
            GameStateOpen currentState = (GameStateOpen) current;

            for (Spell spell : Spells.ALL) {
                if (spell.getManaCost() <= ((GameStateOpen) current).player.mana) {
                    if (!currentState.activeEffects.stream().anyMatch(e ->
                            // You cannot cast a spell that would start an effect which is already active.
                            e.from == spell
                                    // However, effects can be started on the same turn they end.
                                    && e.timer > 1)) {
                        GameState nextState = runTurn(currentState, spell, hardMode);
                        if (!(nextState instanceof Lost)) {
                            queue.add(nextState);
                        }
                    }
                }
            }
        }
    }

    private static GameState runTurns(boolean hardMode, GameStateOpen state, Iterable<Spell> choices) {
        for (Spell choice : choices) {
            GameState next = runTurn(state, choice, hardMode);
            if (!(next instanceof GameStateOpen)) {
                return next;
            }
            state = (GameStateOpen) next;
        }
        return state;
    }

    private static GameState runTurn(GameStateOpen state, Spell spell, boolean hardMode) {
        log("\n-- Player turn --");

        if (hardMode) {
            if (state.player.hitPoints <= 1) {
                // lose
                return new Lost(state.manaSpent);
            }
            state = state.withPlayer(
                    state.player.withHitPoints(state.player.hitPoints - 1));
        }

        log("- " + state.player);
        log("- " + state.boss);

        GameState beforePlayerAttack = state.applyEffects();
        if (!(beforePlayerAttack instanceof GameStateOpen)) {
            return beforePlayerAttack;
        }
        state = (GameStateOpen) beforePlayerAttack;
        state = state.recordSpendManaFor(spell);
        GameState afterSpell = spell.apply(state);
        if (!(afterSpell instanceof GameStateOpen)) {
            return afterSpell;
        }
        state = (GameStateOpen) afterSpell;

        log("\n-- Boss turn --");
        log("- " + state.player);
        log("- " + state.boss);
        GameState beforeBossAttack = state.applyEffects();
        if (!(beforeBossAttack instanceof GameStateOpen)) {
            return beforeBossAttack;
        }
        state = (GameStateOpen) beforeBossAttack;
        return state.applyBossAttack();
    }

    interface Spells {

        // Magic Missile costs 53 mana. It instantly does 4 damage.
        static Spell MagicMissile =
                new Spell() {
                    @Override
                    public String getName() {
                        return "Magic Missile";
                    }

                    @Override
                    public int getManaCost() {
                        return 53;
                    }

                    @Override
                    public GameState apply(GameStateOpen state) {
                        log("Player casts " + getName() + ", dealing 4 damage.");
                        return GameState.of(
                                state.player,
                                state.boss.withHitPoints(state.boss.hitPoints - 4),
                                state.activeEffects,
                                state.manaSpent,
                                state.spellHistory);
                    }
                };
        // Drain costs 73 mana. It instantly does 2 damage and heals you for 2 hit points.
        static Spell Drain = new Spell() {
            @Override
            public String getName() {
                return "Drain";
            }

            @Override
            public int getManaCost() {
                return 73;
            }

            @Override
            public GameState apply(GameStateOpen state) {
                log("Player casts " + getName() + ".");
                return GameState.of(
                        state.player.withHitPoints(state.player.hitPoints + 2),
                        state.boss.withHitPoints(state.boss.hitPoints - 2),
                        state.activeEffects,
                        state.manaSpent,
                        state.spellHistory);
            }
        };
        // Shield costs 113 mana. It starts an effect that lasts for 6 turns. While it is active, your armor is increased by 7.
        static Spell Shield = new Spell() {
            @Override
            public String getName() {
                return "Shield";
            }

            @Override
            public int getManaCost() {
                return 113;
            }

            @Override
            public GameState apply(GameStateOpen state) {
                log("Player casts " + getName() + ", increasing armor by 7.");
                return state.addEffect(new Effect(this, 6, 7, 0, 0));
            }
        };
        // Poison costs 173 mana. It starts an effect that lasts for 6 turns. At the start of each turn while it is active, it deals the boss 3 damage.
        static Spell Poison = new Spell() {
            @Override
            public String getName() {
                return "Poison";
            }

            @Override
            public int getManaCost() {
                return 173;
            }

            @Override
            public GameState apply(GameStateOpen state) {
                log("Player casts " + getName() + ".");
                return state.addEffect(new Effect(this, 6, 0, 3, 0));
            }
        };
        // Recharge costs 229 mana. It starts an effect that lasts for 5 turns. At the start of each turn while it is active, it gives you 101 new mana.
        static Spell Recharge = new Spell() {
            @Override
            public String getName() {
                return "Recharge";
            }

            @Override
            public int getManaCost() {
                return 229;
            }

            @Override
            public GameState apply(GameStateOpen state) {
                log("Player casts " + getName() + ".");
                return state.addEffect(new Effect(this, 5, 0, 0, 101));
            }
        };

        static Spell[] ALL = new Spell[]{
                MagicMissile,
                Drain,
                Shield,
                Poison,
                Recharge
        };
    }

    abstract static class Spell {
        abstract String getName();

        abstract int getManaCost();

        abstract GameState apply(GameStateOpen state);

        @Override
        public String toString() {
            return getName();
        }
    }

    interface GameState {
        static GameState of(player player, boss boss, List<Effect> effects, int manaSpent, List<Spell> spellHistory) {
            if (boss.hitPoints <= 0) {
                return new Won(manaSpent, spellHistory);
            }
            if (player.hitPoints <= 0) {
                return new Lost(manaSpent);
            }
            return new GameStateOpen(player, boss, effects, manaSpent, spellHistory);
        }

        int getManaSpent();
    }

    @Value
    static class Lost implements GameState {
        int manaSpent;

    }

    @Value
    static class Won implements GameState {
        int manaSpent;
        List<Spell> spellHistory;
    }

    @Value
    @Wither
    static class GameStateOpen implements GameState {
        player player;
        boss boss;
        List<Effect> activeEffects;
        int manaSpent;
        List<Spell> spellHistory;

        public GameState applyEffects() {
            List<Effect> remainingEffects = new ArrayList<>();
            player player = this.player;
            boss boss = this.boss;
            int armor = 0;

            for (Effect activeEffect : activeEffects) {
                if (activeEffect.armorBonus > 0) {
                    armor += activeEffect.armorBonus;
                    log(String.format("Shield's timer is now %s.", activeEffect.timer - 1));
                }
                if (activeEffect.bossDamage > 0) {
                    boss = boss.withHitPoints(boss.hitPoints - activeEffect.bossDamage);
                    if (boss.hitPoints <= 0) {
                        log(String.format("%s deals %s damage. This kills the boss, and the player wins.", activeEffect.from.getName(), activeEffect.bossDamage));
                        return new Won(manaSpent, spellHistory);
                    } else {
                        log(String.format("%s deals %s damage; its timer is now %s.", activeEffect.from.getName(), activeEffect.bossDamage, activeEffect.timer - 1));
                    }
                }
                if (activeEffect.manaIncome > 0) {
                    player = player.withMana(player.mana + activeEffect.manaIncome);
                    log(String.format("%s provides %s mana; its timer is now %s.", activeEffect.from.getName(), activeEffect.manaIncome, activeEffect.timer - 1));
                }
                if (activeEffect.timer > 1) {
                    remainingEffects.add(activeEffect.withTimer(activeEffect.timer - 1));
                } else {
                    log(activeEffect.from.getName() + " wears off.");
                }
            }
            player = player.withArmor(armor);

            return GameState.of(player, boss, remainingEffects, manaSpent, spellHistory);
        }

        public GameState addEffect(Effect effect) {
            return withActiveEffects(append(activeEffects, effect));
        }

        public GameState applyBossAttack() {
            int damage = Math.max(1, boss.attackDamage - player.armor);
            log(String.format("Boss attacks for %s%s damage!",
                    player.armor > 0 ? (boss.attackDamage + " - " + player.armor + " = ") : "",
                    damage));
            return withPlayer(
                    player.withHitPoints(player.hitPoints - damage));
        }

        public GameStateOpen recordSpendManaFor(Spell spell) {
            int manaCost = spell.getManaCost();
            return new GameStateOpen(
                    player.withMana(player.mana - manaCost),
                    boss,
                    activeEffects,
                    manaSpent + manaCost,
                    append(spellHistory, spell));
        }
    }

    static void log(Object msg) {
        if (LOG) {
            System.out.println(msg);
            logBuffer.append(msg).append('\n');
        }
    }

    @Value
    @Wither
    static class player {
        int hitPoints;
        int armor;
        int mana;

        public String toString() {
            return String.format("Player has %s hit points, %s armor, %s mana", hitPoints, armor, mana);
        }
    }

    @Value
    static class Effect {
        Spell from;
        @Wither
        int timer;
        int armorBonus;
        int bossDamage;
        int manaIncome;
    }

    @Value
    static class boss {
        @Wither
        int hitPoints;
        int attackDamage;

        public String toString() {
            return String.format("Boss has %s hit points", hitPoints);
        }
    }

    static boss boss = new boss(55, 8);

    static <T> List<T> append(List<T> list, T item) {
        if (list.isEmpty()) {
            return Collections.singletonList(item);
        }
        ArrayList<T> acc = new ArrayList<>(list);
        acc.add(item);
        return acc;
    }

    private static String exampleLog1 = "-- Player turn --\n" +
            "- Player has 10 hit points, 0 armor, 250 mana\n" +
            "- Boss has 13 hit points\n" +
            "Player casts Poison.\n" +
            "\n" +
            "-- Boss turn --\n" +
            "- Player has 10 hit points, 0 armor, 77 mana\n" +
            "- Boss has 13 hit points\n" +
            "Poison deals 3 damage; its timer is now 5.\n" +
            "Boss attacks for 8 damage!\n" +
            "\n" +
            "-- Player turn --\n" +
            "- Player has 2 hit points, 0 armor, 77 mana\n" +
            "- Boss has 10 hit points\n" +
            "Poison deals 3 damage; its timer is now 4.\n" +
            "Player casts Magic Missile, dealing 4 damage.\n" +
            "\n" +
            "-- Boss turn --\n" +
            "- Player has 2 hit points, 0 armor, 24 mana\n" +
            "- Boss has 3 hit points\n" +
            "Poison deals 3 damage. This kills the boss, and the player wins.";

    private static String exampleLog2 = "-- Player turn --\n" +
            "- Player has 10 hit points, 0 armor, 250 mana\n" +
            "- Boss has 14 hit points\n" +
            "Player casts Recharge.\n" +
            "\n" +
            "-- Boss turn --\n" +
            "- Player has 10 hit points, 0 armor, 21 mana\n" +
            "- Boss has 14 hit points\n" +
            "Recharge provides 101 mana; its timer is now 4.\n" +
            "Boss attacks for 8 damage!\n" +
            "\n" +
            "-- Player turn --\n" +
            "- Player has 2 hit points, 0 armor, 122 mana\n" +
            "- Boss has 14 hit points\n" +
            "Recharge provides 101 mana; its timer is now 3.\n" +
            "Player casts Shield, increasing armor by 7.\n" +
            "\n" +
            "-- Boss turn --\n" +
            "- Player has 2 hit points, 7 armor, 110 mana\n" +
            "- Boss has 14 hit points\n" +
            "Shield's timer is now 5.\n" +
            "Recharge provides 101 mana; its timer is now 2.\n" +
            "Boss attacks for 8 - 7 = 1 damage!\n" +
            "\n" +
            "-- Player turn --\n" +
            "- Player has 1 hit point, 7 armor, 211 mana\n" +
            "- Boss has 14 hit points\n" +
            "Shield's timer is now 4.\n" +
            "Recharge provides 101 mana; its timer is now 1.\n" +
            "Player casts Drain, dealing 2 damage, and healing 2 hit points.\n" +
            "\n" +
            "-- Boss turn --\n" +
            "- Player has 3 hit points, 7 armor, 239 mana\n" +
            "- Boss has 12 hit points\n" +
            "Shield's timer is now 3.\n" +
            "Recharge provides 101 mana; its timer is now 0.\n" +
            "Recharge wears off.\n" +
            "Boss attacks for 8 - 7 = 1 damage!\n" +
            "\n" +
            "-- Player turn --\n" +
            "- Player has 2 hit points, 7 armor, 340 mana\n" +
            "- Boss has 12 hit points\n" +
            "Shield's timer is now 2.\n" +
            "Player casts Poison.\n" +
            "\n" +
            "-- Boss turn --\n" +
            "- Player has 2 hit points, 7 armor, 167 mana\n" +
            "- Boss has 12 hit points\n" +
            "Shield's timer is now 1.\n" +
            "Poison deals 3 damage; its timer is now 5.\n" +
            "Boss attacks for 8 - 7 = 1 damage!\n" +
            "\n" +
            "-- Player turn --\n" +
            "- Player has 1 hit point, 7 armor, 167 mana\n" +
            "- Boss has 9 hit points\n" +
            "Shield's timer is now 0.\n" +
            "Shield wears off, decreasing armor by 7.\n" +
            "Poison deals 3 damage; its timer is now 4.\n" +
            "Player casts Magic Missile, dealing 4 damage.\n" +
            "\n" +
            "-- Boss turn --\n" +
            "- Player has 1 hit point, 0 armor, 114 mana\n" +
            "- Boss has 2 hit points\n" +
            "Poison deals 3 damage. This kills the boss, and the player wins.";


}

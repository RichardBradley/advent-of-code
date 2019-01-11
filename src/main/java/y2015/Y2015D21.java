package y2015;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.Value;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.google.common.truth.Truth.assertThat;

public class Y2015D21 {

    public static void main(String[] args) throws Exception {

        // 1
        assertThat(firstCharacterWinsFight(
                new Character(8, 5, 5),
                new Character(12, 7, 2)))
                .isTrue();

        System.out.println(findLeastAmountOfGoldToWin(boss));

        // 2
        System.out.println(findMostAmountOfGoldToLose(boss));
    }

    private static int findLeastAmountOfGoldToWin(Character boss) {
        return genPossibleShoppingBaskets()
                .filter(b -> firstCharacterWinsFight(b.resultingPlayerStats, boss))
                .map(b -> b.cost)
                .min(Comparator.naturalOrder())
                .get();
    }


    private static int findMostAmountOfGoldToLose(Character boss) {
        return genPossibleShoppingBaskets()
                .filter(b -> !firstCharacterWinsFight(b.resultingPlayerStats, boss))
                .map(b -> b.cost)
                .max(Comparator.naturalOrder())
                .get();
    }

    @Value
    static class Basket {
        int cost;
        Character resultingPlayerStats;
    }

    private static Stream<Basket> genPossibleShoppingBaskets() {
        List<Basket> acc = new ArrayList<>();

        // You must buy exactly one weapon; no dual-wielding.
        for (ShopItem weapon : shopWeapons) {
            // Armor is optional, but you can't use more than one.
            for (ShopItem armor : Lists.asList(null, shopArmor)) {
                // You can buy 0-2 rings (at most one for each hand)
                acc.add(makeBasket(weapon, armor));
                for (int i = 0; i < shopRings.length; i++) {
                    ShopItem ring1 = shopRings[i];
                    acc.add(makeBasket(weapon, armor, ring1));
                    for (int j = i + 1; j < shopRings.length; j++) {
                        ShopItem ring2 = shopRings[j];
                        acc.add(makeBasket(weapon, armor, ring1, ring2));
                    }
                }
            }
        }

        return acc.stream();
    }

    static Basket makeBasket(ShopItem... items) {
        int cost = 0;
        int damage = 0;
        int armor = 0;
        for (ShopItem item : items) {
            if (item != null) {
                cost += item.cost;
                damage += item.damage;
                armor += item.armor;
            }
        }
        return new Basket(
                cost,
                new Character(playerStartingHitPoints, damage, armor));
    }

    private static boolean firstCharacterWinsFight(Character first, Character second) {
        first = first.clone();
        second = second.clone();

        while (true) {
            second.hitPoints -= first.getDamageTo(second);
            if (second.hitPoints <= 0) {
                return true;
            }
            first.hitPoints -= second.getDamageTo(first);
            if (first.hitPoints <= 0) {
                return false;
            }
        }
    }

    @AllArgsConstructor
    static class Character implements Cloneable {
        int hitPoints;
        int damage;
        int armor;

        public int getDamageTo(Character other) {
            return Math.max(1, this.damage - other.armor);
        }

        @SneakyThrows
        public Character clone() {
            return (Character) super.clone();
        }
    }

    @Value
    static class ShopItem {
        int cost;
        int damage;
        int armor;
    }

    static ShopItem[] shopWeapons = new ShopItem[]{
            new ShopItem(8, 4, 0),
            new ShopItem(10, 5, 0),
            new ShopItem(25, 6, 0),
            new ShopItem(40, 7, 0),
            new ShopItem(74, 8, 0)
    };

    static ShopItem[] shopArmor = new ShopItem[]{
            new ShopItem(13, 0, 1),
            new ShopItem(31, 0, 2),
            new ShopItem(53, 0, 3),
            new ShopItem(75, 0, 4),
            new ShopItem(102, 0, 5)
    };

    static ShopItem[] shopRings = new ShopItem[]{
            new ShopItem(25, 1, 0),
            new ShopItem(50, 2, 0),
            new ShopItem(100, 3, 0),
            new ShopItem(20, 0, 1),
            new ShopItem(40, 0, 2),
            new ShopItem(80, 0, 3),
    };

    static int playerStartingHitPoints = 100;

    static Character boss = new Character(109, 8, 2);
}

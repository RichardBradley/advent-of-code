package y2020;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Iterables;
import com.google.common.io.Resources;
import lombok.Value;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;
import static java.util.stream.Collectors.toList;


public class Y2020D21 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        String input = Resources.toString(Resources.getResource("y2020/Y2020D21.txt"), StandardCharsets.UTF_8);

        run(example);
        run(input);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static void run(String input) {

        String[] lines = input.split("\n");
        Pattern outerP = Pattern.compile("(.*) \\(contains (.*)\\)");
        List<IngredientsAllergens> spec = Arrays.stream(lines).map(line -> {
            Matcher outerM = outerP.matcher(line);
            checkState(outerM.matches());
            List<String> ingredients = Splitter.on(" ").splitToList(outerM.group(1));
            List<String> allergens = Splitter.on(", ").splitToList(outerM.group(2));
            return new IngredientsAllergens(ingredients, allergens);
        }).collect(toList());

        Set<String> allIngredients = spec.stream().flatMap(s -> s.ingredients.stream()).collect(Collectors.toSet());
        Set<String> allAllergens = spec.stream().flatMap(s -> s.allergens.stream()).collect(Collectors.toSet());

        Map<String, Set<String>> possibleContainingIngredientByAllergen = new HashMap<>();
        for (String allergen : allAllergens) {
            Set<String> possibleContainingIngredient = new HashSet<>(allIngredients);
            for (IngredientsAllergens specLine : spec) {
                if (specLine.allergens.contains(allergen)) {
                    // intersect possibleContainingIngredient with specLine.ingredients
                    possibleContainingIngredient.removeIf(i -> !specLine.ingredients.contains(i));
                }
            }
            possibleContainingIngredientByAllergen.put(allergen, possibleContainingIngredient);
        }

        // which ingredients can't possibly contain any of the allergens
        // in any food in your list.
        Set<String> allergenFree = allIngredients.stream()
                .filter(i -> possibleContainingIngredientByAllergen.values().stream()
                        .noneMatch(x -> x.contains(i)))
                .collect(Collectors.toSet());

        // How many times do any of those ingredients appear?
        long part1 = spec.stream().flatMap(s -> s.ingredients.stream()).filter(i -> allergenFree.contains(i)).count();
        System.out.println("part1 = " + part1);

        // you should have enough information to figure out which ingredient contains which allergen.
        Set<String> knownContainingIngredients = new HashSet<>();
        while (true) {
            boolean changed = false;
            for (Map.Entry<String, Set<String>> entry : possibleContainingIngredientByAllergen.entrySet()) {
                String allergen = entry.getKey();
                Set<String> ingredients = entry.getValue();
                if (ingredients.size() == 1) {
                    changed |= knownContainingIngredients.add(Iterables.getOnlyElement(ingredients));
                } else if (ingredients.size() > 0) {
                    changed |= ingredients.removeAll(knownContainingIngredients);
                }
            }
            if (!changed) {
                break;
            }
        }

        checkState(possibleContainingIngredientByAllergen.values().stream().allMatch(s -> s.size() == 1));

        // Arrange the ingredients alphabetically by their allergen and separate
        // them by commas to produce your canonical dangerous ingredient list
        String dangerousList = possibleContainingIngredientByAllergen.entrySet().stream()
                .sorted(Comparator.comparing(x -> x.getKey()))
                .map(x -> Iterables.getOnlyElement(x.getValue()))
                .collect(Collectors.joining(","));
        System.out.println("dangerousList = " + dangerousList);
    }

    @Value
    static class IngredientsAllergens {
        List<String> ingredients;
        List<String> allergens;
    }

    static String example = "mxmxvkd kfcds sqjhc nhms (contains dairy, fish)\n" +
            "trh fvjkl sbzzf mxmxvkd (contains dairy)\n" +
            "sqjhc fvjkl (contains soy)\n" +
            "sqjhc mxmxvkd sbzzf (contains fish)";
}

package y2023;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;
import lombok.Value;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

public class Y2023D02 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        List<Game> input = parse(Resources.readLines(Resources.getResource("y2023/Y2023D02.txt"), StandardCharsets.UTF_8));

        // 1
        System.out.println(part1(example));
        System.out.println(part1(input));

        // 2
        System.out.println(part2(input));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    @Value
    private static class Game {
        int gameId;
        List<Turn> turns;
    }

    @Value
    private static class Turn {
        Map<String, Integer> countsByColour;
    }

    private static List<Game> parse(List<String> spec) {
        ArrayList<Game> acc = new ArrayList<>();
        Pattern gamePat = Pattern.compile("Game (\\d+): (.*)");
        for (String line : spec) {
            Matcher m = gamePat.matcher(line);
            assertTrue(m.matches());
            int gameId = Integer.parseInt(m.group(1));
            List<Turn> turns = Splitter.on("; ").splitToList(m.group(2)).stream().map(turnSpec ->
                    new Turn(Splitter.on(", ").splitToList(turnSpec).stream().map(s ->
                            s.split(" ")
                    ).collect(Collectors.toMap(a -> a[1], a -> Integer.parseInt(a[0]))))
            ).collect(Collectors.toList());
            acc.add(new Game(gameId, turns));
        }
        return acc;
    }

    private static long part1(List<Game> input) {
        // which games would have been possible if the bag had been loaded with
        // only 12 red cubes, 13 green cubes, and 14 blue cubes.
        // What is the sum of the IDs of those games?
        long sum = 0;
        nextGame:
        for (Game game : input) {
            for (Turn turn : game.turns) {
                if (turn.countsByColour.getOrDefault("red", 0) > 12
                        || turn.countsByColour.getOrDefault("green", 0) > 13
                        || turn.countsByColour.getOrDefault("blue", 0) > 14) {
                    continue nextGame;
                }
            }
            sum += game.gameId;
        }

        return sum;
    }

    private static int part2(List<Game> input) {
        // For each game, find the minimum set of cubes that must have been present. What is the sum of the power of these sets?
        int sum = 0;
        for (Game game : input) {
            int minRed = 0;
            int minGreen = 0;
            int minBlue = 0;
            for (Turn turn : game.turns) {
                minRed = Math.max(minRed, turn.countsByColour.getOrDefault("red", 0));
                minGreen = Math.max(minGreen, turn.countsByColour.getOrDefault("green", 0));
                minBlue = Math.max(minBlue, turn.countsByColour.getOrDefault("blue", 0));
            }
            sum += (minRed * minGreen * minBlue);
        }

        return sum;
    }

    static List<Game> example = parse(List.of(
            "Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green",
            "Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue",
            "Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red",
            "Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red",
            "Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green"
    ));
}

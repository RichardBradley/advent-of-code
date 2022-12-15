package y2022;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;
import lombok.Value;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2022D15 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        List<String> input = Resources.readLines(Resources.getResource("y2022/Y2022D15.txt"), StandardCharsets.UTF_8);

        // 1
        assertThat(part1(example, 10)).isEqualTo(26);
        System.out.println("part 1 example ok");
        System.out.println(part1(input, 2000000));

        // 2
        assertThat(part2(example, 20)).isEqualTo(56000011);
        System.out.println("part 2 example ok");
        System.out.println(part2(input, 4000000));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    @Value
    private static class Sensor {
        int x;
        int y;
        int distClear; // the distress signal is further than this
    }

    /**
     * Find the only possible position for the distress beacon. What is its tuning frequency?
     */
    private static long part2(List<String> input, int maxCoord) {

        List<Sensor> sensors = new ArrayList<>();

        Pattern p = Pattern.compile("Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)");
        for (int i = 0; i < input.size(); i++) {
            String line = input.get(i);
            Matcher m = p.matcher(line);
            checkState(m.matches());

            Point sensor = new Point(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
            Point closestBeacon = new Point(Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4)));
            int distToBeacon = Math.abs(sensor.x - closestBeacon.x) + Math.abs(sensor.y - closestBeacon.y);

            sensors.add(new Sensor(sensor.x, sensor.y, distToBeacon));
        }

        // given that the answer is known unique, it must be at dist+1 from a sensor
        for (Sensor sensor : sensors) {
            int distToPossible = sensor.distClear + 1;
            for (int dx = -distToPossible; dx <= distToPossible; dx++) {
                int x = sensor.x + dx;
                if (x >= 0 && x <= maxCoord) {
                    int dy = Math.abs(distToPossible - dx);

                    int y = sensor.y - dy;
                    if (y >=0 && y <= maxCoord) {
                        if (couldBeDistress(x, y, sensors)) {
                            return x * 4000000L + y;
                        }
                    }

                    y = sensor.y + dy;
                    if (y >=0 && y <= maxCoord) {
                        if (couldBeDistress(x, y, sensors)) {
                            return x * 4000000L + y;
                        }
                    }
                }
            }
        }
        throw new RuntimeException("no solution");
    }

    private static boolean couldBeDistress(int x, int y, List<Sensor> sensors) {
        for (Sensor sensor : sensors) {
            int dist = Math.abs(x - sensor.x) + Math.abs(y - sensor.y);
            if (dist <= sensor.distClear) {
                return false;
            }
        }
        return true;
    }

    private static int part1(List<String> input, int targetY) {

        Map<Point, Character> world = new HashMap<>();
        Pattern p = Pattern.compile("Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)");
        for (int i = 0; i < input.size(); i++) {
            String line = input.get(i);
            Matcher m = p.matcher(line);
            checkState(m.matches());

            Point sensor = new Point(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
            Point closestBeacon = new Point(Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4)));

            world.put(sensor, 'S');
            world.put(closestBeacon, 'B');

            int distToBeacon = Math.abs(sensor.x - closestBeacon.x) + Math.abs(sensor.y - closestBeacon.y);
            System.out.printf(
                    "Input row %s of %s. distToBeacon=%s world size=%s\n",
                    i,
                    input.size(),
                    distToBeacon,
                    world.size());
            // Only do targetY row
            int dy = Math.abs(targetY - sensor.y);
            int maxDx = distToBeacon - dy;
            if (maxDx >= 0) {
                for (int dx = -maxDx; dx <= maxDx; dx++) {
                    int x = sensor.x + dx;
                    int y = targetY;
                    world.putIfAbsent(new Point(x, y), '#');
                }
            }
        }

        int count = 0;
        for (Map.Entry<Point, Character> entry : world.entrySet()) {
            if (entry.getKey().y == targetY) {
                if (entry.getValue() == '#') {
                    count++;
                }
            }
        }
        return count;
    }

    private static List<String> example = List.of(
            "Sensor at x=2, y=18: closest beacon is at x=-2, y=15",
            "Sensor at x=9, y=16: closest beacon is at x=10, y=16",
            "Sensor at x=13, y=2: closest beacon is at x=15, y=3",
            "Sensor at x=12, y=14: closest beacon is at x=10, y=16",
            "Sensor at x=10, y=20: closest beacon is at x=10, y=16",
            "Sensor at x=14, y=17: closest beacon is at x=10, y=16",
            "Sensor at x=8, y=7: closest beacon is at x=2, y=10",
            "Sensor at x=2, y=0: closest beacon is at x=2, y=10",
            "Sensor at x=0, y=11: closest beacon is at x=2, y=10",
            "Sensor at x=20, y=14: closest beacon is at x=25, y=17",
            "Sensor at x=17, y=20: closest beacon is at x=21, y=22",
            "Sensor at x=16, y=7: closest beacon is at x=15, y=3",
            "Sensor at x=14, y=3: closest beacon is at x=15, y=3",
            "Sensor at x=20, y=1: closest beacon is at x=15, y=3");
}

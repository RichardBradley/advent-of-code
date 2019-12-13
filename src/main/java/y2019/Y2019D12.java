package y2019;

import com.google.common.base.Stopwatch;
import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.truth.Truth.assertThat;
import static org.apache.commons.math3.util.ArithmeticUtils.lcm;

public class Y2019D12 {

    static final boolean LOG = false;

    public static void main(String[] args) {
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(getTotalEnergyAfter(example1, 10)).isEqualTo(179);
        System.out.println(getTotalEnergyAfter(input, 1000));

        // 2
        assertThat(findMinCycleLength2(example1)).isEqualTo(2772);
        System.out.println("example ok");
        System.out.println(findMinCycleLength2(input)); // 478,373,365,921,244

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static long findMinCycleLength2(String input) {
        int x = findMinXCycleLength(input);
        int y = findMinYCycleLength(input);
        int z = findMinZCycleLength(input);

        return lcm((long) x, lcm((long) y, (long) z));
    }

    private static int findMinXCycleLength(String input) {
        List<Moon> moons = parse(input);
        String step0State = toString(moons);
        for (int step = 1; ; step++) {
            advanceSimulationX(moons);
            if (step0State.equals(toString(moons))) {
                return step;
            }
        }
    }

    private static int findMinYCycleLength(String input) {
        List<Moon> moons = parse(input);
        String step0State = toString(moons);
        for (int step = 1; ; step++) {
            advanceSimulationY(moons);
            if (step0State.equals(toString(moons))) {
                return step;
            }
        }
    }

    private static int findMinZCycleLength(String input) {
        List<Moon> moons = parse(input);
        String step0State = toString(moons);
        for (int step = 1; ; step++) {
            advanceSimulationZ(moons);
            if (step0State.equals(toString(moons))) {
                return step;
            }
        }
    }

    private static int getTotalEnergyAfter(String input, int steps) {
        List<Moon> moons = parse(input);
        if (LOG) {
            System.out.println("step = 0");
            System.out.println(toString(moons));
        }
        for (int step = 1; step <= steps; step++) {
            advanceSimulation(moons);

            if (LOG) {
                System.out.println("step = " + step);
                System.out.println(toString(moons));
            }
        }

        return moons.stream().mapToInt(Moon::getTotalEnergy).sum();
    }

    private static String toString(List<Moon> moons) {
        return moons.stream().map(m -> m.toString()).collect(Collectors.joining("\n"));
    }

    private static void advanceSimulation(List<Moon> moons) {
        // apply gravity
        for (int i = 0; i < moons.size(); i++) {
            Moon moon1 = moons.get(i);
            for (int j = i + 1; j < moons.size(); j++) {
                Moon moon2 = moons.get(j);
                if (moon1.posX < moon2.posX) {
                    moon1.velX++;
                    moon2.velX--;
                } else if (moon1.posX > moon2.posX) {
                    moon1.velX--;
                    moon2.velX++;
                }
                if (moon1.posY < moon2.posY) {
                    moon1.velY++;
                    moon2.velY--;
                } else if (moon1.posY > moon2.posY) {
                    moon1.velY--;
                    moon2.velY++;
                }
                if (moon1.posZ < moon2.posZ) {
                    moon1.velZ++;
                    moon2.velZ--;
                } else if (moon1.posZ > moon2.posZ) {
                    moon1.velZ--;
                    moon2.velZ++;
                }
            }
        }
        // apply velocity
        moons.forEach(Moon::applyVelocity);
    }

    private static void advanceSimulationX(List<Moon> moons) {
        // apply gravity
        for (int i = 0; i < moons.size(); i++) {
            Moon moon1 = moons.get(i);
            for (int j = i + 1; j < moons.size(); j++) {
                Moon moon2 = moons.get(j);
                if (moon1.posX < moon2.posX) {
                    moon1.velX++;
                    moon2.velX--;
                } else if (moon1.posX > moon2.posX) {
                    moon1.velX--;
                    moon2.velX++;
                }
            }
        }
        // apply velocity
        moons.forEach(m -> {
            m.posX += m.velX;
        });
    }

    private static void advanceSimulationY(List<Moon> moons) {
        // apply gravity
        for (int i = 0; i < moons.size(); i++) {
            Moon moon1 = moons.get(i);
            for (int j = i + 1; j < moons.size(); j++) {
                Moon moon2 = moons.get(j);
                if (moon1.posY < moon2.posY) {
                    moon1.velY++;
                    moon2.velY--;
                } else if (moon1.posY > moon2.posY) {
                    moon1.velY--;
                    moon2.velY++;
                }
            }
        }
        // apply velocity
        moons.forEach(m -> {
            m.posY += m.velY;
        });
    }

    private static void advanceSimulationZ(List<Moon> moons) {
        // apply gravity
        for (int i = 0; i < moons.size(); i++) {
            Moon moon1 = moons.get(i);
            for (int j = i + 1; j < moons.size(); j++) {
                Moon moon2 = moons.get(j);
                if (moon1.posZ < moon2.posZ) {
                    moon1.velZ++;
                    moon2.velZ--;
                } else if (moon1.posZ > moon2.posZ) {
                    moon1.velZ--;
                    moon2.velZ++;
                }
            }
        }
        // apply velocity
        moons.forEach(m -> {
            m.posZ += m.velZ;
        });
    }

    private static List<Moon> parse(String input) {
        Pattern pattern = Pattern.compile("<x=([\\d-]+), y=([\\d-]+), z=([\\d-]+)>");
        return Arrays.stream(input.split("\n"))
                .map(line -> {
                    Matcher matcher = pattern.matcher(line);
                    checkArgument(matcher.matches());
                    return new Moon(
                            Integer.parseInt(matcher.group(1)),
                            Integer.parseInt(matcher.group(2)),
                            Integer.parseInt(matcher.group(3)));
                })
                .collect(Collectors.toList());
    }

    @Data
    static class Moon {
        int posX;
        int posY;
        int posZ;
        int velX = 0;
        int velY = 0;
        int velZ = 0;

        public Moon(int posX, int posY, int posZ) {
            this.posX = posX;
            this.posY = posY;
            this.posZ = posZ;
        }

        void applyVelocity() {
            posX += velX;
            posY += velY;
            posZ += velZ;
        }

        int getTotalEnergy() {
            return getPotentialEnergy() * getKineticEnergy();
        }

        private int getKineticEnergy() {
            return Math.abs(velX) + Math.abs(velY) + Math.abs(velZ);
        }

        private int getPotentialEnergy() {
            return Math.abs(posX) + Math.abs(posY) + Math.abs(posZ);
        }
    }

    static String example1 = "<x=-1, y=0, z=2>\n" +
            "<x=2, y=-10, z=-7>\n" +
            "<x=4, y=-8, z=8>\n" +
            "<x=3, y=5, z=-1>";

    static String input = "<x=10, y=15, z=7>\n" +
            "<x=15, y=10, z=0>\n" +
            "<x=20, y=12, z=3>\n" +
            "<x=0, y=-3, z=13>";
}

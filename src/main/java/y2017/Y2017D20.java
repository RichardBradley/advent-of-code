package y2017;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.io.Resources;
import lombok.Value;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.truth.Truth.assertThat;

public class Y2017D20 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {

            List<Particle> input = Particle.parse(Resources.readLines(Resources.getResource("y2017/Y2017D20.txt"), StandardCharsets.UTF_8));

            assertThat(closestToOriginLongTerm(input)).isEqualTo(344);
            assertThat(countAfterCollisions(input)).isEqualTo(404);
        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private static int countAfterCollisions(List<Particle> input) {
        ListMultimap<Point3, Particle> world = MultimapBuilder.hashKeys().arrayListValues().build();
        for (Particle particle : input) {
            world.put(particle.pos, particle);
        }

        for (int t = 0; t < 10000; t++) {
            ListMultimap<Point3, Particle> nextTickByPosition = MultimapBuilder.hashKeys().arrayListValues().build();
            for (Map.Entry<Point3, Collection<Particle>> entry : world.asMap().entrySet()) {
                Collection<Particle> particlesAtPoint = entry.getValue();
                if (particlesAtPoint.size() == 1) {
                    Particle next = particlesAtPoint.iterator().next().afterTick();
                    nextTickByPosition.put(next.pos, next);
                }
            }

            world = nextTickByPosition;
        }

        return world.size();
    }

    private static int closestToOriginLongTerm(List<Particle> input) {
        int smallestAbsAccel = Integer.MAX_VALUE;
        List<Particle> bestParticles = null;

        for (Particle particle : input) {
            int absAccel = particle.acc.abs();

            if (absAccel < smallestAbsAccel) {
                bestParticles = new ArrayList<>();
                smallestAbsAccel = absAccel;
            }

            if (absAccel == smallestAbsAccel) {
                bestParticles.add(particle);
            }
        }

        return bestParticles.stream()
                .sorted(Comparator.comparing(p -> p.distFromOriginAtTime(10000)))
                .findFirst()
                .get().id;
    }

    @Value
    private static class Particle {
        int id;
        Point3 pos;
        Point3 vel;
        Point3 acc;

        int distFromOriginAtTime(int t) {
            // pos + t * vel + t * (t + 1) / 2 * acc
            int x = pos.x + t * vel.x + t * (t + 1) * acc.x / 2;
            int y = pos.y + t * vel.y + t * (t + 1) * acc.y / 2;
            int z = pos.z + t * vel.z + t * (t + 1) * acc.z / 2;
            return Math.abs(x) + Math.abs(y) + Math.abs(z);
        }

        static List<Particle> parse(List<String> input) {
            Pattern specPat = Pattern.compile("p=<(-?[\\d]+),(-?[\\d]+),(-?[\\d]+)>, v=<(-?[\\d]+),(-?[\\d]+),(-?[\\d]+)>, a=<(-?[\\d]+),(-?[\\d]+),(-?[\\d]+)>");
            List<Particle> acc = new ArrayList<>(input.size());
            for (int i = 0; i < input.size(); i++) {
                String line = input.get(i);

                Matcher matcher = specPat.matcher(line);
                Preconditions.checkArgument(matcher.matches());
                acc.add(new Particle(
                        i,
                        new Point3(
                                Integer.parseInt(matcher.group(1)),
                                Integer.parseInt(matcher.group(2)),
                                Integer.parseInt(matcher.group(3))),
                        new Point3(
                                Integer.parseInt(matcher.group(4)),
                                Integer.parseInt(matcher.group(5)),
                                Integer.parseInt(matcher.group(6))),
                        new Point3(
                                Integer.parseInt(matcher.group(7)),
                                Integer.parseInt(matcher.group(8)),
                                Integer.parseInt(matcher.group(9)))));
            }
            return acc;
        }

        public Particle afterTick() {
            Point3 newVel = new Point3(
                    vel.x + acc.x,
                    vel.y + acc.y,
                    vel.z + acc.z);
            Point3 newPos = new Point3(
                    pos.x + newVel.x,
                    pos.y + newVel.y,
                    pos.z + newVel.z);
            return new Particle(
                    id,
                    newPos,
                    newVel,
                    acc);
        }
    }

    @Value
    private static class Point3 {
        int x;
        int y;
        int z;

        public int abs() {
            return Math.abs(x) + Math.abs(y) + Math.abs(z);
        }
    }
}

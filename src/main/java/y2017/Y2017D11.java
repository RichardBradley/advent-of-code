package y2017;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import lombok.Value;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

public class Y2017D11 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        String input = Resources.toString(Resources.getResource("y2017/Y2017D11.txt"), StandardCharsets.UTF_8);

        assertThat(distTo("ne,ne,ne")).isEqualTo(3);
        assertThat(distTo("ne,ne,sw,sw")).isEqualTo(0);
        assertThat(distTo("ne,ne,s,s")).isEqualTo(2);
        assertThat(distTo("se,sw,se,sw,sw")).isEqualTo(3);
        assertThat(distTo(input)).isEqualTo(682);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static int distTo(String spec) {
        Iterator<String> instr = Splitter.on(",").split(spec).iterator();

        Point3D pos = new Point3D(0, 0, 0);
        int maxDist = 0;
        while (instr.hasNext()) {
            Point3D delta = dirs.get(instr.next());
            pos = pos.add(delta);
            maxDist = Math.max(maxDist, getDistFromOrigin(pos));
        }
        System.out.println("maxDist = " + maxDist);
        return getDistFromOrigin(pos);
    }

    private static int getDistFromOrigin(Point3D pos) {
        return (int) (Math.abs(pos.getX()) + Math.abs(pos.getY()) + Math.abs(pos.getZ())) / 2;
    }

    // https://www.redblobgames.com/grids/hexagons/#neighbors-cube
    static Map<String, Point3D> dirs = ImmutableMap.<String, Point3D>builder()
            .put("n", new Point3D(1, 0, -1))
            .put("ne", new Point3D(0, 1, -1))
            .put("se", new Point3D(-1, 1, 0))
            .put("s", new Point3D(-1, 0, 1))
            .put("sw", new Point3D(0, -1, 1))
            .put("nw", new Point3D(1, -1, 0))
            .build();

    @Value
    static class Point3D {
        double x, y, z;

        public Point3D add(Point3D other) {
            return new Point3D(this.x + other.x, this.y + other.y, this.z + other.z);
        }
    }
}


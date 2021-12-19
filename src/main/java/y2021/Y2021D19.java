package y2021;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;
import lombok.Value;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2021D19 {

    static int matrixMulCount = 0;

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            List<String> input = Resources.readLines(Resources.getResource("y2021/Y2021D19.txt"), StandardCharsets.UTF_8);

            allScannerOrientations = generateAllScannerOrientations();

            // 1
            assertThat(examine(example)).isEqualTo(new Output(79, 3621));
            assertThat(examine(input)).isEqualTo(new Output(367, 11925));

            System.out.println("matrixMulCount = " + matrixMulCount);

        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private static Collection<Matrix3d> generateAllScannerOrientations() {
        Set<Matrix3d> acc = new HashSet<>();
        Matrix3d xr1 = new Matrix3d();
        xr1.rotX(Math.PI / 2);
        Matrix3d yr1 = new Matrix3d();
        yr1.rotY(Math.PI / 2);
        Matrix3d zr1 = new Matrix3d();
        zr1.rotZ(Math.PI / 2);
        Matrix3d m = new Matrix3d();
        m.setIdentity();
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                for (int z = 0; z < 4; z++) {
                    fixRoundingErrors(m);
                    acc.add((Matrix3d) m.clone());
                    m.mul(zr1);
                }
                m.mul(yr1);
            }
            m.mul(xr1);
        }
        return acc;
    }

    private static void fixRoundingErrors(Matrix3d m) {
        m.m00 = fixRoundingErrors(m.m00);
        m.m01 = fixRoundingErrors(m.m01);
        m.m02 = fixRoundingErrors(m.m02);
        m.m10 = fixRoundingErrors(m.m10);
        m.m11 = fixRoundingErrors(m.m11);
        m.m12 = fixRoundingErrors(m.m12);
        m.m20 = fixRoundingErrors(m.m20);
        m.m21 = fixRoundingErrors(m.m21);
        m.m22 = fixRoundingErrors(m.m22);
    }

    private static double fixRoundingErrors(double d) {
        return Math.round(d);
    }

    static Collection<Matrix3d> allScannerOrientations;

    @Value
    static class Output {
        int beaconCount;
        int maxScannerSeparation;
    }

    private static Output examine(List<String> input) {
        List<List<Point3>> scanners = parse(input);
        Set<Point3> truePoints = new HashSet<>(scanners.get(0));
        List<Point3> scannerLocations = new ArrayList<>();
        scanners.remove(0);

        while (!scanners.isEmpty()) {
            boolean progressMade = false;

            scannerLoop:
            for (int i = scanners.size() - 1; i >= 0; i--) {
                List<Point3> scanner = scanners.get(i);

                // match scanner i to true points so far
                for (Matrix3d scannerOrientation : allScannerOrientations) {
                    for (Point3 referencePoint : truePoints) {
                        for (Point3 scannerIStartPoint : scanner) {

                            Point3 scannerIStartPointTr = transform(scannerOrientation, scannerIStartPoint);
                            Point3 translation = referencePoint.subtract(scannerIStartPointTr);

                            int matchingCount = 0;
                            for (Point3 scannerPoint : scanner) {
                                if (truePoints.contains(
                                        translation.add(transform(scannerOrientation, scannerPoint)))) {
                                    matchingCount++;
                                    if (matchingCount >= 12) {
                                        progressMade = true;
                                        scanners.remove(i);

                                        for (Point3 p : scanner) {
                                            truePoints.add(
                                                    translation.add(transform(scannerOrientation, p)));
                                        }
                                        scannerLocations.add(translation);

                                        continue scannerLoop;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            checkState(progressMade);
        }

        int maxScannerSeparation = 0;
        for (int i = 0; i < scannerLocations.size(); i++) {
            for (int j = i + 1; j < scannerLocations.size(); j++) {
                maxScannerSeparation = Math.max(
                        scannerLocations.get(i).manhattanDist(scannerLocations.get(j)),
                        maxScannerSeparation);
            }
        }

        return new Output(truePoints.size(), maxScannerSeparation);
    }

    private static Point3 transform(Matrix3d m, Point3 p) {
        matrixMulCount++;
        Vector3d v = new Vector3d(p.x, p.y, p.z);
        m.transform(v);

        return new Point3(dtoi(v.x), dtoi(v.y), dtoi(v.z));
    }

    private static int dtoi(double d) {
        return Math.toIntExact(Math.round(d));
    }

    private static List<List<Point3>> parse(List<String> input) {
        List<List<Point3>> scanners = new ArrayList<>();
        List<Point3> curr = null;

        for (String line : input) {
            if (line.startsWith("--- scanner ")) {
                curr = new ArrayList<>();
                scanners.add(curr);
            } else if (!line.isEmpty()) {
                String[] parts = line.split(",");
                checkArgument(parts.length == 3);
                curr.add(new Point3(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2])));
            }
        }
        return scanners;
    }

    @Value
    static class Point3 {
        int x;
        int y;
        int z;

        Point3 add(Point3 other) {
            return new Point3(x + other.x, y + other.y, z + other.z);
        }

        Point3 subtract(Point3 other) {
            return new Point3(x - other.x, y - other.y, z - other.z);
        }

        public int manhattanDist(Point3 other) {
            return Math.abs(x - other.x) + Math.abs(y - other.y) + Math.abs(z - other.z);
        }
    }

    private static List<String> example = List.of(
            "--- scanner 0 ---",
            "404,-588,-901",
            "528,-643,409",
            "-838,591,734",
            "390,-675,-793",
            "-537,-823,-458",
            "-485,-357,347",
            "-345,-311,381",
            "-661,-816,-575",
            "-876,649,763",
            "-618,-824,-621",
            "553,345,-567",
            "474,580,667",
            "-447,-329,318",
            "-584,868,-557",
            "544,-627,-890",
            "564,392,-477",
            "455,729,728",
            "-892,524,684",
            "-689,845,-530",
            "423,-701,434",
            "7,-33,-71",
            "630,319,-379",
            "443,580,662",
            "-789,900,-551",
            "459,-707,401",
            "",
            "--- scanner 1 ---",
            "686,422,578",
            "605,423,415",
            "515,917,-361",
            "-336,658,858",
            "95,138,22",
            "-476,619,847",
            "-340,-569,-846",
            "567,-361,727",
            "-460,603,-452",
            "669,-402,600",
            "729,430,532",
            "-500,-761,534",
            "-322,571,750",
            "-466,-666,-811",
            "-429,-592,574",
            "-355,545,-477",
            "703,-491,-529",
            "-328,-685,520",
            "413,935,-424",
            "-391,539,-444",
            "586,-435,557",
            "-364,-763,-893",
            "807,-499,-711",
            "755,-354,-619",
            "553,889,-390",
            "",
            "--- scanner 2 ---",
            "649,640,665",
            "682,-795,504",
            "-784,533,-524",
            "-644,584,-595",
            "-588,-843,648",
            "-30,6,44",
            "-674,560,763",
            "500,723,-460",
            "609,671,-379",
            "-555,-800,653",
            "-675,-892,-343",
            "697,-426,-610",
            "578,704,681",
            "493,664,-388",
            "-671,-858,530",
            "-667,343,800",
            "571,-461,-707",
            "-138,-166,112",
            "-889,563,-600",
            "646,-828,498",
            "640,759,510",
            "-630,509,768",
            "-681,-892,-333",
            "673,-379,-804",
            "-742,-814,-386",
            "577,-820,562",
            "",
            "--- scanner 3 ---",
            "-589,542,597",
            "605,-692,669",
            "-500,565,-823",
            "-660,373,557",
            "-458,-679,-417",
            "-488,449,543",
            "-626,468,-788",
            "338,-750,-386",
            "528,-832,-391",
            "562,-778,733",
            "-938,-730,414",
            "543,643,-506",
            "-524,371,-870",
            "407,773,750",
            "-104,29,83",
            "378,-903,-323",
            "-778,-728,485",
            "426,699,580",
            "-438,-605,-362",
            "-469,-447,-387",
            "509,732,623",
            "647,635,-688",
            "-868,-804,481",
            "614,-800,639",
            "595,780,-596",
            "",
            "--- scanner 4 ---",
            "727,592,562",
            "-293,-554,779",
            "441,611,-461",
            "-714,465,-776",
            "-743,427,-804",
            "-660,-479,-426",
            "832,-632,460",
            "927,-485,-438",
            "408,393,-506",
            "466,436,-512",
            "110,16,151",
            "-258,-428,682",
            "-393,719,612",
            "-211,-452,876",
            "808,-476,-593",
            "-575,615,604",
            "-485,667,467",
            "-680,325,-822",
            "-627,-443,-432",
            "872,-547,-609",
            "833,512,582",
            "807,604,487",
            "839,-516,451",
            "891,-625,532",
            "-652,-548,-490",
            "30,-46,-14"
    );
}

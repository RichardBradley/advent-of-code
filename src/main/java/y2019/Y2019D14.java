package y2019;

import com.google.common.base.Stopwatch;
import lombok.Value;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.truth.Truth.assertThat;
import static java.lang.System.out;

public class Y2019D14 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(findMinFuelCost(example1, 1)).isEqualTo(31);
        out.println("example ok");
        out.println(findMinFuelCost(input, 1));

        // 2
        System.out.println(findMinFuelCost(input, 998535));
        System.out.println(findMinFuelCost(input, 998536));
        System.out.println(findMinFuelCost(input, 998537));
        System.out.println(findMinFuelCost(input, 998538));

        out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static long findMinFuelCost(String spec, long targetFuelCount) {
        List<Reaction> reactions = parse(spec);

        Map<String, Long> required = new HashMap<>();
        required.put("FUEL", targetFuelCount);
        while (true) {
            // expand one requirement
            Optional<Map.Entry<String, Long>> nextReqO = required.entrySet().stream()
                    .filter(e -> e.getValue() > 0)
                    .filter(e -> !e.getKey().equals("ORE"))
                    .findFirst();
            if (!nextReqO.isPresent()) {
                return required.get("ORE");
            }
            Map.Entry<String, Long> nextReq = nextReqO.get();
            String nextReqName = nextReq.getKey();
            long nextReqCount = nextReq.getValue();

            List<Reaction> matchingReactions = reactions.stream()
                    .filter(reaction -> reaction.to.name.equals(nextReqName))
                    .collect(Collectors.toList());

            if (matchingReactions.isEmpty()) {
                throw new IllegalStateException("No path to: " + required);
            } else if (matchingReactions.size() > 1) {
                throw new IllegalStateException(String.format(
                        "Ambiguous path to: %s\nChoices: %s",
                        required,
                        matchingReactions));
            } else {
                Reaction matchingReaction = matchingReactions.get(0);
                long reactionRunCount = divRoundUp(nextReqCount, matchingReaction.to.count);

                required.compute(nextReqName, (k, oldCount) -> {
                    long newCount = (oldCount == null ? 0 : oldCount) - matchingReaction.to.count * reactionRunCount;
                    return (newCount == 0) ? null : newCount;
                });
                for (ChemQuant from : matchingReaction.from) {
                    required.merge(from.name, from.count * reactionRunCount, (a, b) -> a + b);
                }

//                out.printf("Used x%s %s\nNew reqs: %s\n",
//                        reactionRunCount,
//                        matchingReaction,
//                        required);
            }
        }
    }

    private static long divRoundUp(long a, long b) {
        return (a + b - 1) / b;
    }

    private static List<Reaction> parse(String spec) {
        Pattern.compile("((\\d+) (\\w+),? )+");
        return Arrays.stream(spec.split("\n"))
                .map(line -> {
                    String[] fromTo = line.split(" => ");
                    checkArgument(fromTo.length == 2);
                    ChemQuant to = ChemQuant.fromString(fromTo[1]);
                    String[] fromlist = fromTo[0].split(", ");
                    List<ChemQuant> from = Arrays.stream(fromlist).map(x -> ChemQuant.fromString(x)).collect(Collectors.toList());
                    return new Reaction(from, to);
                })
                .collect(Collectors.toList());
    }

    @Value
    static class Reaction {
        List<ChemQuant> from;
        ChemQuant to;
    }

    @Value
    static class ChemQuant {
        int count;
        String name;

        public static ChemQuant fromString(String s) {
            String[] countName = s.split(" ");
            checkArgument(countName.length == 2);
            return new ChemQuant(
                    Integer.parseInt(countName[0]),
                    countName[1]);
        }
    }

    static String example1 = "10 ORE => 10 A\n" +
            "1 ORE => 1 B\n" +
            "7 A, 1 B => 1 C\n" +
            "7 A, 1 C => 1 D\n" +
            "7 A, 1 D => 1 E\n" +
            "7 A, 1 E => 1 FUEL";

    static String input = "1 JKXFH => 8 KTRZ\n" +
            "11 TQGT, 9 NGFV, 4 QZBXB => 8 MPGLV\n" +
            "8 NPDPH, 1 WMXZJ => 7 VCNSK\n" +
            "1 MPGLV, 6 CWHX => 5 GDRZ\n" +
            "16 JDFQZ => 2 CJTB\n" +
            "1 GQNQF, 4 JDFQZ => 5 WJKDC\n" +
            "2 TXBS, 4 SMGQW, 7 CJTB, 3 NTBQ, 13 CWHX, 25 FLPFX => 1 FUEL\n" +
            "3 WMXZJ, 14 CJTB => 5 FLPFX\n" +
            "7 HDCTQ, 1 MPGLV, 2 VFVC => 1 GSVSD\n" +
            "1 WJKDC => 2 NZSQR\n" +
            "1 RVKLC, 5 CMJSL, 16 DQTHS, 31 VCNSK, 1 RKBMX, 1 GDRZ => 8 SMGQW\n" +
            "2 JDFQZ, 2 LGKHR, 2 NZSQR => 9 TSWN\n" +
            "34 LPXW => 8 PWJFD\n" +
            "2 HDCTQ, 2 VKWN => 8 ZVBRF\n" +
            "2 XCTF => 3 QZBXB\n" +
            "12 NGFV, 3 HTRWR => 5 HDCTQ\n" +
            "1 TSWN, 2 WRSD, 1 ZVBRF, 1 KFRX, 5 BPVMR, 2 CLBG, 22 NPSLQ, 9 GSVSD => 5 NTBQ\n" +
            "10 TSWN => 9 VFVC\n" +
            "141 ORE => 6 MKJDZ\n" +
            "4 NPSLQ, 43 VCNSK, 4 PSJL, 14 KTRZ, 3 KWCDP, 3 HKBS, 11 WRSD, 3 MXWHS => 8 TXBS\n" +
            "8 VCNSK, 1 HDCTQ => 7 MXWHS\n" +
            "3 JDFQZ, 2 GQNQF => 4 XJSQW\n" +
            "18 NGFV, 4 GSWT => 5 KFRX\n" +
            "2 CZSJ => 7 GMTW\n" +
            "5 PHKL, 5 VCNSK, 25 GSVSD => 8 FRWC\n" +
            "30 FRWC, 17 GKDK, 8 NPSLQ => 3 CLBG\n" +
            "8 MXWHS, 3 SCKB, 2 NPSLQ => 1 JKXFH\n" +
            "1 XJSQW, 7 QZBXB => 1 LGKHR\n" +
            "115 ORE => 6 GQNQF\n" +
            "12 HTRWR, 24 HDCTQ => 1 RKBMX\n" +
            "1 DQTHS, 6 XDFWD, 1 MXWHS => 8 VKWN\n" +
            "129 ORE => 3 XCTF\n" +
            "6 GQNQF, 7 WJKDC => 5 PHKL\n" +
            "3 NZSQR => 2 LPXW\n" +
            "2 FLPFX, 1 MKLP, 4 XDFWD => 8 NPSLQ\n" +
            "4 DQTHS, 1 VKWN => 1 BPVMR\n" +
            "7 GMTW => 1 TXMVX\n" +
            "152 ORE => 8 JDFQZ\n" +
            "21 LGKHR => 9 NPDPH\n" +
            "5 CJTB, 1 QZBXB, 3 KFRX => 1 GTPB\n" +
            "1 MXWHS => 3 CWHX\n" +
            "3 PHKL => 1 NGFV\n" +
            "1 WMXZJ => 7 XDFWD\n" +
            "3 TSWN, 1 VKWN => 8 GKDK\n" +
            "1 ZVBRF, 16 PWJFD => 8 CMJSL\n" +
            "3 VCNSK, 7 GDRZ => 4 HKBS\n" +
            "20 XJSQW, 6 HTRWR, 7 CJTB => 5 WMXZJ\n" +
            "12 ZVBRF, 10 FRWC, 12 TSWN => 4 WRSD\n" +
            "16 HDCTQ, 3 GTPB, 10 NGFV => 4 KWCDP\n" +
            "3 TXMVX, 1 NPDPH => 8 HTRWR\n" +
            "9 NPDPH, 6 LPXW => 8 GSWT\n" +
            "4 MKLP => 1 TQGT\n" +
            "34 GTPB => 3 RVKLC\n" +
            "25 VFVC, 5 RVKLC => 8 DQTHS\n" +
            "7 KWCDP => 3 SCKB\n" +
            "6 LGKHR => 8 MKLP\n" +
            "39 MKJDZ => 9 CZSJ\n" +
            "2 TSWN, 1 WMXZJ => 3 PSJL";
}

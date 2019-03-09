package y2015;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.truth.Truth.assertThat;

public class Y2015D19 {
    public static void main(String[] args) throws Exception {

        // 1
        assertThat(countPossibleTargets(testReplacements, "HOH")).isEqualTo(4);
        assertThat(countPossibleTargets(testReplacements, "HOHOHO")).isEqualTo(7);

        System.out.println(countPossibleTargets(replacements, medicineMolecule));

        /// qq too slow -- maybe look for unique paths??

        // 2
        assertThat(countStepsToGenerate(testReplacements, "HOH")).isEqualTo(3);
        assertThat(countStepsToGenerate(testReplacements, "HOHOHO")).isEqualTo(6);

        System.out.println(countStepsToGenerate(replacements, medicineMolecule));
    }

    private static int countPossibleTargets(String[] replacements, String start) {
        Set<String> targets = new HashSet<>();
        for (Replacement replacement : parse(replacements)) {
            Matcher fromMatcher = replacement.fromPat.matcher(start);
            while (fromMatcher.find()) {
                targets.add(
                        start.substring(0, fromMatcher.start()) +
                                replacement.to +
                                start.substring(fromMatcher.end()));
            }
        }

        return targets.size();
    }

    // qq do A* search, heuristic is number of different letters...

    private static int countStepsToGenerate(String[] replacementsSpec, String medicineMolecule) {
        List<Replacement> replacements = parse(replacementsSpec);

        // https://en.wikipedia.org/wiki/A*_search_algorithm

        // The set of nodes already evaluated
        Set<String> closedSet = new HashSet<>();
        // The set of currently discovered nodes that are not evaluated yet.
        PriorityQueue<NodeWithDistance> openSet = new PriorityQueue<>();
        openSet.add(new NodeWithDistance(medicineMolecule, 0));

        while (true) {
            NodeWithDistance current = openSet.poll();
            if ("e".equals(current.value)) {
                return current.stepsFromStart;
            }

            if (!closedSet.add(current.value)) {
                continue;
            }

            for(String neighbor : generateReplacements(replacements, current.value)) {
                if (!closedSet.contains(neighbor)) {
                    NodeWithDistance neighbourNode = new NodeWithDistance(neighbor, 1 + current.stepsFromStart);
                    // DODGY: assume no back-tracking...
                    if (neighbourNode.estTotalDistance <= current.estTotalDistance + 1) {
                        openSet.add(neighbourNode);
                    }
                }
            };
        }

//        Set<String> sources = Collections.singleton(medicineMolecule);
//        Set<String> reachedMols;
//        List<Replacement> replacements = parse(replacementsSpec);
//
//        // Replace backwards for a shorter search
//        for (int step = 1; ; step++) {
//            reachedMols = new HashSet<>();
//            for (String source : sources) {
//                for (Replacement replacement : replacements) {
//                    Matcher toMatcher = replacement.toPat.matcher(source);
//                    while (toMatcher.find()) {
//                        String reached = source.substring(0, toMatcher.start()) +
//                                replacement.from +
//                                source.substring(toMatcher.end());
//                        if ("e".equals(reached)) {
//                            return step;
//                        }
//                        reachedMols.add(reached);
//                    }
//                }
//            }
//
//            sources = reachedMols;
//            System.out.println(String.format(
//                    "Done step %s. Reached %s. Min length %s",
//                    step,
//                    reachedMols.size(),
//                    reachedMols.stream().mapToInt(x -> x.length()).min().getAsInt()));
//        }
    }

    private static Iterable<String> generateReplacements(List<Replacement> replacements, String value) {
        List<String> acc = new ArrayList<>();
        for (Replacement replacement : replacements) {
            Matcher toMatcher = replacement.toPat.matcher(value);
            while (toMatcher.find()) {
                acc.add(value.substring(0, toMatcher.start()) +
                        replacement.from +
                        value.substring(toMatcher.end()));
            }
        }
        return acc;
    }

    private static class NodeWithDistance implements Comparable<NodeWithDistance> {
        String value;
        int stepsFromStart;
        int estTotalDistance;

        NodeWithDistance(String value, int stepsFromStart) {
            this.value = value;
            this.stepsFromStart = stepsFromStart;
            int heuristicDistance = 2 * value.length();
            estTotalDistance = stepsFromStart + heuristicDistance;
        }

        @Override
        public int compareTo(NodeWithDistance o) {
            return Integer.compare(this.estTotalDistance, o.estTotalDistance);
        }
    }

    private static List<Replacement> parse(String[] replacements) {
        List<Replacement> acc = new ArrayList<>();
        Pattern pattern = Pattern.compile("(\\w+) => (\\w+)");
        for (String replacement : replacements) {
            Matcher matcher = pattern.matcher(replacement);
            checkArgument(matcher.matches());
            acc.add(new Replacement(
                    matcher.group(1),
                    matcher.group(2)));
        }
        return acc;
    }

    private static class Replacement {
        String from;
        String to;
        Pattern fromPat;
        Pattern toPat;

        public Replacement(String from, String to) {
            this.from = from;
            this.to = to;
            fromPat = Pattern.compile(Pattern.quote(from));
            toPat = Pattern.compile(Pattern.quote(to));
        }
    }


    private static String[] testReplacements = new String[]{
            "e => H",
            "e => O",
            "H => HO",
            "H => OH",
            "O => HH"
    };

    private static String[] replacements = new String[]{
            "Al => ThF",
            "Al => ThRnFAr",
            "B => BCa",
            "B => TiB",
            "B => TiRnFAr",
            "Ca => CaCa",
            "Ca => PB",
            "Ca => PRnFAr",
            "Ca => SiRnFYFAr",
            "Ca => SiRnMgAr",
            "Ca => SiTh",
            "F => CaF",
            "F => PMg",
            "F => SiAl",
            "H => CRnAlAr",
            "H => CRnFYFYFAr",
            "H => CRnFYMgAr",
            "H => CRnMgYFAr",
            "H => HCa",
            "H => NRnFYFAr",
            "H => NRnMgAr",
            "H => NTh",
            "H => OB",
            "H => ORnFAr",
            "Mg => BF",
            "Mg => TiMg",
            "N => CRnFAr",
            "N => HSi",
            "O => CRnFYFAr",
            "O => CRnMgAr",
            "O => HP",
            "O => NRnFAr",
            "O => OTi",
            "P => CaP",
            "P => PTi",
            "P => SiRnFAr",
            "Si => CaSi",
            "Th => ThCa",
            "Ti => BP",
            "Ti => TiTi",
            "e => HF",
            "e => NAl",
            "e => OMg",
    };

    static String medicineMolecule =
            "ORnPBPMgArCaCaCaSiThCaCaSiThCaCaPBSiRnFArRnFArCaCaSiThCaCaSiThCaCaCaCaCaCaSiRnFYFArSiRnMgArCaSiRnPTiTiBFYPBFArSiRnCaSiRnTiRnFArSiAlArPTiBPTiRnCaSiAlArCaPTiTiBPMgYFArPTiRnFArSiRnCaCaFArRnCaFArCaSiRnSiRnMgArFYCaSiRnMgArCaCaSiThPRnFArPBCaSiRnMgArCaCaSiThCaSiRnTiMgArFArSiThSiThCaCaSiRnMgArCaCaSiRnFArTiBPTiRnCaSiAlArCaPTiRnFArPBPBCaCaSiThCaPBSiThPRnFArSiThCaSiThCaSiThCaPTiBSiRnFYFArCaCaPRnFArPBCaCaPBSiRnTiRnFArCaPRnFArSiRnCaCaCaSiThCaRnCaFArYCaSiRnFArBCaCaCaSiThFArPBFArCaSiRnFArRnCaCaCaFArSiRnFArTiRnPMgArF";

}

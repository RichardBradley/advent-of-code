package y2015;

import com.google.common.base.Stopwatch;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.truth.Truth.assertThat;

public class Y2015D19 {
    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(countPossibleTargets(testReplacements, "HOH")).isEqualTo(4);
        assertThat(countPossibleTargets(testReplacements, "HOHOHO")).isEqualTo(7);

        System.out.println(countPossibleTargets(replacements, medicineMolecule));

        // 2

        assertThat(countStepsToGenerate3(testReplacements, "HOH")).isEqualTo(3);
        assertThat(countStepsToGenerate3(testReplacements, "HOHOHO")).isEqualTo(6);

        System.out.println(countStepsToGenerate3(replacements, medicineMolecule));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");

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

    private static int countStepsToGenerate3(String[] replacementsSpec, String medicineMolecule) {
        List<Replacement> replacements = parse(replacementsSpec);
        return countStepsToGenerate3(replacements, medicineMolecule);
    }

    // Depth first search, with random paths
    private static int countStepsToGenerate3(List<Replacement> replacements, String medicineMolecule) {
        if ("e".equals(medicineMolecule)) {
            return 0;
        }
        Collections.shuffle(replacements);
        for (String generated : generateReverseReplacements(replacements, medicineMolecule)) {
            int steps = countStepsToGenerate3(replacements, generated);
            if (steps == -1) {
                continue; // backtrack
            }
            return 1 + steps;
        }
        return -1;
    }

    private static Iterable<String> generateReverseReplacements(List<Replacement> replacements, String value) {
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

package y2019;

import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import lombok.Value;
import scala.Int;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.truth.Truth.assertThat;
import static java.lang.System.out;

public class Y2019D16 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        // 1
//        testGeneratePattern();
//        assertThat(transform("12345678", 1)).isEqualTo("48226158");
//        assertThat(transform("12345678", 2)).isEqualTo("34040438");
//        assertThat(transform("12345678", 3)).isEqualTo("03415518");
//        assertThat(transform("12345678", 4)).isEqualTo("01029498");
//        assertThat(transform("80871224585914546619083218645595", 100).substring(0, 8)).isEqualTo("24176176");
//        out.println("example ok");
//        out.println(transform(input, 100).substring(0, 8));

        // 2
        assertThat(transformPart2("03036732577212944063491565474664")).isEqualTo("84462026");
        out.println(transformPart2(input));
        // 25131128 too low
        int messageOffset = Integer.parseInt(input.substring(0, 7));
        System.out.println(transform(Strings.repeat(input, 10000), 100)
                .substring(messageOffset, messageOffset + 8));

        out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static String transformPart2(String signalStr) {
        int[] signal = signalStr.chars().map(c -> c - '0').toArray();
        assertThat(signal.length % 4).isIn(ImmutableList.of(0, 2));
        // the

        // op count:
        //   len = 10000 * 650
        //   each digit = len ops
        //   total = 100 * len * len = 10^15
        return "qq";
    }

    private static String transform(String signalStr, int count) {
        assertThat((38) % 10).isEqualTo(8);
        assertThat(Math.abs((-17) % 10)).isEqualTo(7);

        int[] signal = signalStr.chars().map(c -> c - '0').toArray();
        int[] buff = new int[signal.length];
        int[] patt = new int[signal.length];

        for (int phase = 1; phase <= count; phase++) {
            for (int position = 0; position < signal.length; position++) {
                generatePattern(position, patt);
                int sum = 0;
                for (int i = 0; i < signal.length; i++) {
                    sum += signal[i] * patt[i];
                }
                buff[position] = Math.abs(sum % 10);
            }
            int[] tmp = signal;
            signal = buff;
            buff = tmp;

            out.println("Phase " + phase);
        }
        return Arrays.stream(signal).mapToObj(Integer::toString).collect(Collectors.joining());
    }

    private static void testGeneratePattern() {
        int[] buff = new int[15];
        generatePattern(1 - 1, buff);
        assertThat(Arrays.stream(buff).mapToObj(Integer::toString).collect(Collectors.joining(", ")))
                .isEqualTo("1, 0, -1, 0, 1, 0, -1, 0, 1, 0, -1, 0, 1, 0, -1");
        generatePattern(2 - 1, buff);
        assertThat(Arrays.stream(buff).mapToObj(Integer::toString).collect(Collectors.joining(", ")))
                .isEqualTo("0, 1, 1, 0, 0, -1, -1, 0, 0, 1, 1, 0, 0, -1, -1");
        generatePattern(3 - 1, buff);
        assertThat(Arrays.stream(buff).mapToObj(Integer::toString).collect(Collectors.joining(", ")))
                .isEqualTo("0, 0, 1, 1, 1, 0, 0, 0, -1, -1, -1, 0, 0, 0, 1");
    }

    static int[] basePattern = new int[]{0, 1, 0, -1};

    private static void generatePattern(int position, int[] buff) {
        int basePatternIdx = 0;
        int repetitionCount = 0;
        int i = 0;
        while (true) {
            if (repetitionCount++ >= position) {
                repetitionCount = 0;
                basePatternIdx = (basePatternIdx + 1) % basePattern.length;
            }
            buff[i++] = basePattern[basePatternIdx];
            if (i == buff.length) {
                return;
            }
        }
    }

    static String input = "59775675999083203307460316227239534744196788252810996056267313158415747954523514450220630777434694464147859581700598049220155996171361500188470573584309935232530483361639265796594588423475377664322506657596419440442622029687655170723364080344399753761821561397734310612361082481766777063437812858875338922334089288117184890884363091417446200960308625363997089394409607215164553325263177638484872071167142885096660905078567883997320316971939560903959842723210017598426984179521683810628956529638813221927079630736290924180307474765551066444888559156901159193212333302170502387548724998221103376187508278234838899434485116047387731626309521488967864391";

}

package y2020;

import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.io.Resources;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;

public class Y2020D14 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        List<String> input = Resources.readLines(Resources.getResource("y2020/Y2020D14.txt"), StandardCharsets.UTF_8);

        part1(input);
        part2(input);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static void part1(List<String> input) {
        Map<Integer, String> memory = new HashMap<>();

        String mask = null;

        Pattern setMaskPatt = Pattern.compile("mask = ([01X]{36})");
        Pattern setMemPatt = Pattern.compile("mem\\[(\\d+)] = (\\d+)");

        for (String line : input) {
            Matcher setMaskM = setMaskPatt.matcher(line);
            if (setMaskM.matches()) {
                mask = setMaskM.group(1);
            } else {
                Matcher setMemM = setMemPatt.matcher(line);
                checkState(setMemM.matches());

                int address = Integer.parseInt(setMemM.group(1));
                BigInteger val = new BigInteger(setMemM.group(2));
                String valBits = Strings.padStart(val.toString(2), 36, '0');

                StringBuilder modifiedValBits = new StringBuilder();
                for (int i = 0; i < 36; i++) {
                    switch (mask.charAt(i)) {
                        case 'X':
                            modifiedValBits.append(valBits.charAt(i));
                            break;
                        case '0':
                        case '1':
                            modifiedValBits.append(mask.charAt(i));
                            break;
                        default:
                            throw new IllegalStateException();
                    }
                }

                memory.put(address, modifiedValBits.toString());
            }
        }

        long memorySum = memory.values().stream()
                .mapToLong(val -> new BigInteger(val, 2).longValueExact())
                .sum();

        System.out.println("Memory sum part 1 = " + memorySum);
    }

    private static void part2(List<String> input) {
        Map<String, Long> memory = new HashMap<>();

        String mask = null;

        Pattern setMaskPatt = Pattern.compile("mask = ([01X]{36})");
        Pattern setMemPatt = Pattern.compile("mem\\[(\\d+)] = (\\d+)");

        for (String line : input) {
            Matcher setMaskM = setMaskPatt.matcher(line);
            if (setMaskM.matches()) {
                mask = setMaskM.group(1);
            } else {
                Matcher setMemM = setMemPatt.matcher(line);
                checkState(setMemM.matches());

                BigInteger address = new BigInteger(setMemM.group(1));
                String addressBits = Strings.padStart(address.toString(2), 36, '0');
                long val = Long.parseLong(setMemM.group(2));

                setMem(memory, 0, mask, addressBits, new StringBuilder(), val);
            }
        }

        long memorySum = memory.values().stream()
                .mapToLong(x -> x)
                .sum();

        System.out.println("Memory sum = " + memorySum);
    }

    private static void setMem(Map<String, Long> memory, int idx, String mask, String inputAddressBits, StringBuilder currAddressBits, long val) {
        if (idx == 36) {
            memory.put(currAddressBits.toString(), val);
        } else {
            switch (mask.charAt(idx)) {
                case '0':
                    currAddressBits.append(inputAddressBits.charAt(idx));
                    setMem(memory, idx + 1, mask, inputAddressBits, currAddressBits, val);
                    currAddressBits.setLength(currAddressBits.length() - 1);
                    break;
                case '1':
                    currAddressBits.append('1');
                    setMem(memory, idx + 1, mask, inputAddressBits, currAddressBits, val);
                    currAddressBits.setLength(currAddressBits.length() - 1);
                    break;
                case 'X':
                    currAddressBits.append('0');
                    setMem(memory, idx + 1, mask, inputAddressBits, currAddressBits, val);
                    currAddressBits.setCharAt(currAddressBits.length() - 1, '1');
                    setMem(memory, idx + 1, mask, inputAddressBits, currAddressBits, val);
                    currAddressBits.setLength(currAddressBits.length() - 1);
                    break;
                default:
                    throw new IllegalStateException();
            }
        }
    }
}

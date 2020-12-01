import com.google.common.base.Strings;

import java.math.BigInteger;

public class DoubleTest {

    public static void main(String[] args) {

        double n = 23.0;

        printInfo(n);
        System.out.println();
        System.out.println();
        System.out.println("#######");

        printInfo(7);
        System.out.println("#######");

        printInfo(2000001);

    }

    private static void printInfo(double n) {
        long bits = Double.doubleToLongBits(n);


        boolean negative = (bits & 0x8000000000000000L) != 0;
        long mantissa = bits & 0x000fffffffffffffL;

        // (-1)^sign + 1.Mantissa x 2^(Exponent - Bias)
        System.out.println("mantissa bits as long: " + mantissa);
        String mantissaBitsStr = BigInteger.valueOf(mantissa).toString(2);
        // zero-pad up to 52
        mantissaBitsStr = Strings.repeat("0", 52 - mantissaBitsStr.length()) +  mantissaBitsStr;

        System.out.println("mantissa bits: " + mantissaBitsStr);
        System.out.println("1.mantissa bits: 1." + mantissaBitsStr);
        double mantissaVal = 1.0;
        double base = 2.0;
        // ironic using doubles for this?
        for (int i = 0; i < mantissaBitsStr.length(); i++) {
            if (mantissaBitsStr.charAt(i) == '1') {
                mantissaVal += (1.0 / base);
            }
            base *= 2.0;
        }
        System.out.println("1.mantissa bits val = " + mantissaVal);

        int exponent = Math.getExponent(n);
        System.out.println("exponent = " + exponent);
        System.out.println("2^exponent = " + Math.pow(2.0, exponent));
        System.out.println("2^exponent * 1.mantissa = " + Math.pow(2.0, exponent) * mantissaVal);


        //System.out.println("value of 1.mantissa bits: " + Double.longBitsToDouble( ));





    }
}

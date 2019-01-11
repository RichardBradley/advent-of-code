package y2015;

public class Y2015D20 {

    public static void main(String[] args) throws Exception {

        // 1
        System.out.println(lowestHouseNumberWithPresents_v1(36000000));

        // 2
        System.out.println(lowestHouseNumberWithPresents_v2(36000000));
    }

    private static int lowestHouseNumberWithPresents_v1(long target) {
        long[] presentCountByHouseNum = new long[1000000];
        for (int elfNumber = 1; ; elfNumber++) {
            int houseNumber = elfNumber;
            while (true) {
                presentCountByHouseNum[houseNumber] += 10 * elfNumber;
                houseNumber += elfNumber;
                if (houseNumber >= presentCountByHouseNum.length) {
                    break;
                }
            }
            if (presentCountByHouseNum[elfNumber] >= target) {
                return elfNumber;
            }
        }
    }


    private static int lowestHouseNumberWithPresents_v2(long target) {
        long[] presentCountByHouseNum = new long[1000000];
        for (int elfNumber = 1; ; elfNumber++) {
            int houseNumber = elfNumber;
            for (int presentNumber = 1; presentNumber <= 50; presentNumber++) {
                presentCountByHouseNum[houseNumber] += 11 * elfNumber;
                houseNumber += elfNumber;
                if (houseNumber >= presentCountByHouseNum.length) {
                    break;
                }
            }
            if (presentCountByHouseNum[elfNumber] >= target) {
                return elfNumber;
            }
        }
    }
}

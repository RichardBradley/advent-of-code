package day3;

public class Q1 {

    public static void main(String[] args) {

        int[][] claimsByPoint = init2d(1000);
        int countClaimedAtLeastTwice = 0;

        for (Input.Claim claim : Input.Claims) {
            for (int x = claim.left; x < claim.left + claim.width; x++) {
                for (int y = claim.top; y < claim.top + claim.height; y++) {
                    if (claimsByPoint[x][y] == 0) {
                        claimsByPoint[x][y] = claim.id;
                    } else if (claimsByPoint[x][y] > 0) {
                        claimsByPoint[x][y] = -1;
                        countClaimedAtLeastTwice++;
                    }
                }
            }
        }

        System.out.println(countClaimedAtLeastTwice);

        for (Input.Claim claim : Input.Claims) {
            boolean isIntact = true;
            for (int x = claim.left; x < claim.left + claim.width; x++) {
                for (int y = claim.top; y < claim.top + claim.height; y++) {
                    if (claimsByPoint[x][y] != claim.id) {
                        isIntact = false;
                    }
                }
            }
            if (isIntact) {
                System.out.println("intact: " + claim.id);
            }
        }
    }

    private static int[][] init2d(int size) {
        int[][] acc = new int[size][];
        for (int i = 0; i < size; i++) {
            acc[i] = new int[size];
        }
        return acc;
    }
}

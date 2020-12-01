import org.apache.commons.math3.distribution.BinomialDistribution;

/**
 * https://www.maths.manchester.ac.uk/mathsbombe/problem.php?index=4
 */
public class ChipsPuzzle {

    public static void main(String[] args) {

        int N = 10;

        // expected time to ok after a shuffle
        double est_e_shuff = N/2; // initial guess

        // expected time to all OK, given N are currently OK
        double[] e_n = new double[N+1];

        double e_shuff_error = 0;

        do {

            for (int n = 0; n <= N; n++) {
                double e_tongs = N - n;
                double e_with_shuff = 1 + est_e_shuff;
                e_n[n] = Math.min(e_tongs, e_with_shuff);
            }

            // recompute e_shuff for iterative approx
            double computed_e_shuff = 0;
            BinomialDistribution dist = new BinomialDistribution(N, 0.5);
            for (int n = 0; n <= N; n++) {
                double p_n = dist.probability(n);
                computed_e_shuff += p_n * e_n[n];
            }

            System.out.println("computed_e_shuff = " + computed_e_shuff);

            e_shuff_error = computed_e_shuff - est_e_shuff;
            System.out.println("e_shuff_error = " + e_shuff_error);

            est_e_shuff = computed_e_shuff;

        } while (Math.abs(e_shuff_error) > 1e-8);

        System.out.println("Answer = " + e_n[0]);
    }
}

package day2;

public class Q2 {

    public static void main(String[] args) {

        String[] input = Input$.MODULE$.ids();

        for (int i = 0; i < input.length; i++) {
            String left = input[i];
            for (int j = i + 1; j < input.length; j++) {
                String right = input[j];

                int differences = 0;
                for (int x = 0; x < left.length(); x++) {
                    if (left.charAt(x) != right.charAt(x)) {
                        differences++;
                    }
                }

                if (differences == 1) {
                    System.out.println("Match: " + left + " " + right);
                    System.out.println(left.chars().filter(c -> right.indexOf(c) >= 0).boxed()
                            .reduce("", (String x, Integer y) -> x + (char) (int) y, (String x, String y) -> x + y));


                }
            }
        }

    }
}

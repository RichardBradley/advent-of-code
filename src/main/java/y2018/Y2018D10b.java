package y2018;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

public class Y2018D10b {
    public static void main(String[] args) throws Exception {

//        findMinApproach(testInput);
//        findMinApproach(input);
        findMinApproach(inputSamCL);


//        // 1
        runLoop(testInput, 10);
//
//        // look at 10886s
//        runLoop(input, 20000);
//
//        runLoop(inputSamCL, 100000 * 365 * 24 * 24);

        System.exit(0);
    }

    private static void findMinApproach(List<StarInfo> testInput) {
        Random rnd = new Random();
        for (int i = 0; i < 20; i++) {
            StarInfo a = testInput.get(rnd.nextInt(testInput.size()));
            StarInfo b = testInput.get(rnd.nextInt(testInput.size()));

            // For quadratic eq  y = cx^2 + bx + a
            // If c > 0, then min is at
            // x = - b/2c
            //
            // Here, = ()
            //  distance^2 = (a.position.x + t * a.velocity.x - b.position.x - t * b.velocity.x)^2 + ...
            //  distance^2 = (a.position.x + b.position.x)^2 - 2*t*(a.position.x + b.position.x)*(a.velocity.x + b.velocity.x) + t^2*(a.velocity.x + b.velocity.x)^2 + ...
            // so `c` = (a.velocity.x + b.velocity.x)^2 + (a.velocity.y + b.velocity.y)^2
            double C = Math.pow(a.velocity.x + b.velocity.x, 2) + Math.pow(a.velocity.y + b.velocity.y, 2);
            // and `b` = - 2 * ((a.position.x + b.position.x)*(a.velocity.x + b.velocity.x) + (a.position.y + b.position.y)*(a.velocity.y + b.velocity.y))
            double B = -2.0 * ((a.position.x + b.position.x) * (a.velocity.x + b.velocity.x) + (a.position.y + b.position.y) * (a.velocity.y + b.velocity.y));
            // so t for min approach is:
            double minApproachT = -B / (2 * C);

            System.out.println(String.format(
                    "Min approach at %s = %s",
                    (int) minApproachT,
                    dist(a, b, (int) minApproachT)));

        }
    }

    private static double dist(StarInfo a, StarInfo b, int t) {
        double dx = (a.position.x + t * a.velocity.x) - (b.position.x + t * b.velocity.x);
        double dy = (a.position.y + t * a.velocity.y) - (b.position.y + t * b.velocity.y);
        return Math.sqrt(dx * dx + dy * dx);
    }

    private static void runLoop(List<StarInfo> stars, int maxTime) throws Exception {
        JFrame frame = new JFrame();
        frame.setSize(500, 500);
        frame.setVisible(true);

        AtomicInteger currentSecond = new AtomicInteger();
        AtomicInteger speed = new AtomicInteger(1);

        frame.getContentPane().add(new StarPanel(stars), BorderLayout.CENTER);
        JSlider slider = new JSlider();
        frame.getContentPane().add(slider, BorderLayout.SOUTH);
        slider.setMinimum(0);
        slider.setMaximum(maxTime);
        slider.addChangeListener(e -> {
            currentSecond.set(slider.getValue());
            System.out.println("Moving to " + (currentSecond.get()));
            setTime(stars, currentSecond.get());
            frame.repaint();

        });

        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);

                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    System.out.println("Moving to " + (currentSecond.incrementAndGet()));
                    advance(stars, 1);
                    frame.repaint();
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    System.out.println("Moving to " + (currentSecond.decrementAndGet()));
                    advance(stars, -1);
                    frame.repaint();
                }
                if (e.getKeyCode() == KeyEvent.VK_PERIOD) {
                    speed.set(speed.get() * 2);
                    int dt = speed.get();
                    System.out.println("Moving to " + (currentSecond.addAndGet(dt)));
                    advance(stars, dt);
                    frame.repaint();
                }
            }
        });

        while (frame.isVisible()) {
            Thread.sleep(100);
        }
    }

    private static void setTime(List<StarInfo> stars, int t) {
        for (StarInfo star : stars) {
            star.position.x = star.intitialPosition.x + t * star.velocity.x;
            star.position.y = star.intitialPosition.y + t * star.velocity.y;
        }
    }

    private static void advance(List<StarInfo> stars, int dt) {
        for (StarInfo star : stars) {
            star.position.x += dt * star.velocity.x;
            star.position.y += dt * star.velocity.y;
        }
    }

    private static class StarPanel extends JPanel {
        private final List<StarInfo> stars;

        private StarPanel(List<StarInfo> stars) {
            this.stars = stars;
        }

        @Override
        protected void paintComponent(Graphics g) {
            long minX = -1 + stars.stream().mapToLong(x -> x.position.x).min().getAsLong();
            long minY = -1 + stars.stream().mapToLong(x -> x.position.y).min().getAsLong();
            long maxX = 1 + stars.stream().mapToLong(x -> x.position.x).max().getAsLong();
            long maxY = 1 + stars.stream().mapToLong(x -> x.position.y).max().getAsLong();
            long starFieldWidth = (maxX - minX);
            long starFieldHeight = (maxY - minY);

            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());

            double xScale = getWidth() / (double) starFieldWidth;
            double yScale = getHeight() / (double) starFieldHeight;
            g.setColor(Color.WHITE);
            for (StarInfo star : stars) {

                double drawX = (star.position.x - minX) * xScale;
                double drawY = (star.position.y - minY) * yScale;
                g.drawRect((int) drawX, (int) drawY, 1, 1);
            }

        }
    }

    static class StarInfo {
        LongPoint position;
        LongPoint intitialPosition;
        LongPoint velocity;
    }

    static class LongPoint implements Cloneable {
        long x;
        long y;

        public LongPoint(long x, long y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public LongPoint clone() {
            try {
                return (LongPoint) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static List<StarInfo> parse(String... input) {
        Pattern pattern = Pattern.compile("position=< *(-?\\d+), *(-?\\d+)> velocity=< *(-?\\d+), *(-?\\d+)>");

        return Arrays.stream(input)
                .map(x -> {
                    Matcher matcher = pattern.matcher(x);
                    checkArgument(matcher.matches());
                    StarInfo starInfo = new StarInfo();
                    starInfo.position = new LongPoint(
                            Long.parseLong(matcher.group(1)),
                            Long.parseLong(matcher.group(2)));
                    starInfo.intitialPosition = (LongPoint) starInfo.position.clone();
                    starInfo.velocity = new LongPoint(
                            Long.parseLong(matcher.group(3)),
                            Long.parseLong(matcher.group(4)));
                    return starInfo;
                })
                .collect(Collectors.toList());
    }

    static List<StarInfo> testInput = parse(
            "position=< 9,  1> velocity=< 0,  2>",
            "position=< 7,  0> velocity=<-1,  0>",
            "position=< 3, -2> velocity=<-1,  1>",
            "position=< 6, 10> velocity=<-2, -1>",
            "position=< 2, -4> velocity=< 2,  2>",
            "position=<-6, 10> velocity=< 2, -2>",
            "position=< 1,  8> velocity=< 1, -1>",
            "position=< 1,  7> velocity=< 1,  0>",
            "position=<-3, 11> velocity=< 1, -2>",
            "position=< 7,  6> velocity=<-1, -1>",
            "position=<-2,  3> velocity=< 1,  0>",
            "position=<-4,  3> velocity=< 2,  0>",
            "position=<10, -3> velocity=<-1,  1>",
            "position=< 5, 11> velocity=< 1, -2>",
            "position=< 4,  7> velocity=< 0, -1>",
            "position=< 8, -2> velocity=< 0,  1>",
            "position=<15,  0> velocity=<-2,  0>",
            "position=< 1,  6> velocity=< 1,  0>",
            "position=< 8,  9> velocity=< 0, -1>",
            "position=< 3,  3> velocity=<-1,  1>",
            "position=< 0,  5> velocity=< 0, -1>",
            "position=<-2,  2> velocity=< 2,  0>",
            "position=< 5, -2> velocity=< 1,  2>",
            "position=< 1,  4> velocity=< 2,  1>",
            "position=<-2,  7> velocity=< 2, -2>",
            "position=< 3,  6> velocity=<-1, -1>",
            "position=< 5,  0> velocity=< 1,  0>",
            "position=<-6,  0> velocity=< 2,  0>",
            "position=< 5,  9> velocity=< 1, -2>",
            "position=<14,  7> velocity=<-2,  0>",
            "position=<-3,  6> velocity=< 2, -1>"
    );

    static List<StarInfo> input = parse(
            "position=< 21992, -10766> velocity=<-2,  1>",
            "position=<-43366, -21661> velocity=< 4,  2>",
            "position=<-10714, -21661> velocity=< 1,  2>",
            "position=< 21993,  32773> velocity=<-2, -3>",
            "position=< 43745,  32772> velocity=<-4, -3>",
            "position=< 43742,  43664> velocity=<-4, -4>",
            "position=<-43327,  43662> velocity=< 4, -4>",
            "position=<-43373,  43655> velocity=< 4, -4>",
            "position=<-10677,  32773> velocity=< 1, -3>",
            "position=<-43324,  43664> velocity=< 4, -4>",
            "position=<-32447,  43664> velocity=< 3, -4>",
            "position=< 21946, -54317> velocity=<-2,  5>",
            "position=< 11051, -10772> velocity=<-1,  1>",
            "position=< 32824, -21652> velocity=<-3,  2>",
            "position=< 11092, -21655> velocity=<-1,  2>",
            "position=< 32863,  11002> velocity=<-3, -1>",
            "position=<-21562,  10997> velocity=< 2, -1>",
            "position=<-10725,  43662> velocity=< 1, -4>",
            "position=< 11074,  11006> velocity=<-1, -1>",
            "position=<-54209, -10768> velocity=< 5,  1>",
            "position=< 43710, -10774> velocity=<-4,  1>",
            "position=< 54615,  54544> velocity=<-5, -5>",
            "position=< 32859, -21659> velocity=<-3,  2>",
            "position=< 11052,  10997> velocity=<-1, -1>",
            "position=< 21957, -32542> velocity=<-2,  3>",
            "position=<-10714,  10997> velocity=< 1, -1>",
            "position=< 32847, -21652> velocity=<-3,  2>",
            "position=<-21561, -32547> velocity=< 2,  3>",
            "position=< 11074,  21887> velocity=<-1, -2>",
            "position=<-43339, -21656> velocity=< 4,  2>",
            "position=< 54643,  54541> velocity=<-5, -5>",
            "position=<-32477, -32547> velocity=< 3,  3>",
            "position=<-21571,  11005> velocity=< 2, -1>",
            "position=<-10701, -54314> velocity=< 1,  5>",
            "position=<-21584, -43429> velocity=< 2,  4>",
            "position=<-21595, -32542> velocity=< 2,  3>",
            "position=< 54594,  21887> velocity=<-5, -2>",
            "position=< 21970,  32774> velocity=<-2, -3>",
            "position=<-54237, -43425> velocity=< 5,  4>",
            "position=< 43732, -54319> velocity=<-4,  5>",
            "position=<-32488, -10766> velocity=< 3,  1>",
            "position=<-32473, -43433> velocity=< 3,  4>",
            "position=<-43370,  10998> velocity=< 4, -1>",
            "position=<-43347,  32777> velocity=< 4, -3>",
            "position=<-32460,  11002> velocity=< 3, -1>",
            "position=< 21962, -21659> velocity=<-2,  2>",
            "position=< 21981,  21889> velocity=<-2, -2>",
            "position=<-32437, -21656> velocity=< 3,  2>",
            "position=<-43347, -21653> velocity=< 4,  2>",
            "position=< 54652,  54541> velocity=<-5, -5>",
            "position=<-21606, -54319> velocity=< 2,  5>",
            "position=< 11055,  54549> velocity=<-1, -5>",
            "position=<-54259,  21892> velocity=< 5, -2>",
            "position=< 54636,  32777> velocity=<-5, -3>",
            "position=<-54224,  11006> velocity=< 5, -1>",
            "position=<-10723, -54310> velocity=< 1,  5>",
            "position=< 32856,  54547> velocity=<-3, -5>",
            "position=< 11052,  11006> velocity=<-1, -1>",
            "position=<-10674, -54319> velocity=< 1,  5>",
            "position=< 11071, -54317> velocity=<-1,  5>",
            "position=< 11075,  54541> velocity=<-1, -5>",
            "position=<-54226,  21888> velocity=< 5, -2>",
            "position=< 21962,  32774> velocity=<-2, -3>",
            "position=<-54251,  21883> velocity=< 5, -2>",
            "position=< 54628, -32540> velocity=<-5,  3>",
            "position=< 11087,  32770> velocity=<-1, -3>",
            "position=< 54594,  43659> velocity=<-5, -4>",
            "position=< 43748,  32778> velocity=<-4, -3>",
            "position=< 32854,  32778> velocity=<-3, -3>",
            "position=< 54639, -32542> velocity=<-5,  3>",
            "position=<-43367, -43425> velocity=< 4,  4>",
            "position=< 54651, -21660> velocity=<-5,  2>",
            "position=< 11087,  10999> velocity=<-1, -1>",
            "position=<-43379,  32778> velocity=< 4, -3>",
            "position=< 43737, -32543> velocity=<-4,  3>",
            "position=<-43382,  43655> velocity=< 4, -4>",
            "position=<-10684, -43433> velocity=< 1,  4>",
            "position=<-54250,  54550> velocity=< 5, -5>",
            "position=< 11095, -21657> velocity=<-1,  2>",
            "position=< 32836,  10997> velocity=<-3, -1>",
            "position=<-54209, -32540> velocity=< 5,  3>",
            "position=< 11050,  21892> velocity=<-1, -2>",
            "position=<-21587,  10999> velocity=< 2, -1>",
            "position=< 43725,  54550> velocity=<-4, -5>",
            "position=<-54221,  32771> velocity=< 5, -3>",
            "position=< 11087,  54546> velocity=<-1, -5>",
            "position=< 11088, -32538> velocity=<-1,  3>",
            "position=< 21986, -10774> velocity=<-2,  1>",
            "position=< 11071,  10998> velocity=<-1, -1>",
            "position=< 54631,  43661> velocity=<-5, -4>",
            "position=<-32462,  21892> velocity=< 3, -2>",
            "position=<-32464, -43433> velocity=< 3,  4>",
            "position=<-32485, -10772> velocity=< 3,  1>",
            "position=< 43707,  21888> velocity=<-4, -2>",
            "position=< 54602, -32538> velocity=<-5,  3>",
            "position=<-43335, -32546> velocity=< 4,  3>",
            "position=< 54639, -10769> velocity=<-5,  1>",
            "position=<-21551, -21656> velocity=< 2,  2>",
            "position=<-43323, -21658> velocity=< 4,  2>",
            "position=< 43718,  32770> velocity=<-4, -3>",
            "position=<-10709,  43662> velocity=< 1, -4>",
            "position=< 43749,  10997> velocity=<-4, -1>",
            "position=< 32859,  11002> velocity=<-3, -1>",
            "position=< 32839, -43433> velocity=<-3,  4>",
            "position=< 21975, -10775> velocity=<-2,  1>",
            "position=< 11088,  11006> velocity=<-1, -1>",
            "position=<-10716,  21892> velocity=< 1, -2>",
            "position=<-21560, -32538> velocity=< 2,  3>",
            "position=<-21591,  54541> velocity=< 2, -5>",
            "position=< 21968, -21661> velocity=<-2,  2>",
            "position=<-21579, -32545> velocity=< 2,  3>",
            "position=<-10721, -32547> velocity=< 1,  3>",
            "position=<-32468, -43430> velocity=< 3,  4>",
            "position=<-54260, -43427> velocity=< 5,  4>",
            "position=<-43333,  10997> velocity=< 4, -1>",
            "position=<-21579,  21884> velocity=< 2, -2>",
            "position=<-43343,  54545> velocity=< 4, -5>",
            "position=<-10708, -43424> velocity=< 1,  4>",
            "position=< 54601, -21661> velocity=<-5,  2>",
            "position=<-32460, -21654> velocity=< 3,  2>",
            "position=< 32848, -10770> velocity=<-3,  1>",
            "position=<-54209,  21886> velocity=< 5, -2>",
            "position=< 32844,  32773> velocity=<-3, -3>",
            "position=< 32839,  32778> velocity=<-3, -3>",
            "position=<-43374, -43427> velocity=< 4,  4>",
            "position=< 32827,  43662> velocity=<-3, -4>",
            "position=<-21583, -43433> velocity=< 2,  4>",
            "position=< 32819, -54311> velocity=<-3,  5>",
            "position=<-43371,  43655> velocity=< 4, -4>",
            "position=<-32437,  10997> velocity=< 3, -1>",
            "position=<-21566,  43664> velocity=< 2, -4>",
            "position=<-43371, -21658> velocity=< 4,  2>",
            "position=< 43707, -32542> velocity=<-4,  3>",
            "position=<-21551,  11003> velocity=< 2, -1>",
            "position=<-21599,  11006> velocity=< 2, -1>",
            "position=< 11057,  43660> velocity=<-1, -4>",
            "position=<-21571, -10773> velocity=< 2,  1>",
            "position=< 54609, -43424> velocity=<-5,  4>",
            "position=<-32473, -54311> velocity=< 3,  5>",
            "position=<-21587, -54310> velocity=< 2,  5>",
            "position=<-43370, -54310> velocity=< 4,  5>",
            "position=< 54627,  21883> velocity=<-5, -2>",
            "position=< 54620,  32777> velocity=<-5, -3>",
            "position=<-32473, -54316> velocity=< 3,  5>",
            "position=< 54615,  21892> velocity=<-5, -2>",
            "position=<-21579, -54313> velocity=< 2,  5>",
            "position=<-43370, -21652> velocity=< 4,  2>",
            "position=<-10709, -54313> velocity=< 1,  5>",
            "position=< 43737,  32777> velocity=<-4, -3>",
            "position=< 32843,  11000> velocity=<-3, -1>",
            "position=<-43383, -32547> velocity=< 4,  3>",
            "position=<-43359,  43661> velocity=< 4, -4>",
            "position=<-10720, -43433> velocity=< 1,  4>",
            "position=< 54620,  32772> velocity=<-5, -3>",
            "position=< 43739,  21892> velocity=<-4, -2>",
            "position=< 54615,  43656> velocity=<-5, -4>",
            "position=<-32471,  32769> velocity=< 3, -3>",
            "position=<-54232,  21884> velocity=< 5, -2>",
            "position=< 11107, -10769> velocity=<-1,  1>",
            "position=<-43383,  21890> velocity=< 4, -2>",
            "position=< 21951,  54550> velocity=<-2, -5>",
            "position=< 43747, -21661> velocity=<-4,  2>",
            "position=<-32494, -43433> velocity=< 3,  4>",
            "position=<-21563, -54316> velocity=< 2,  5>",
            "position=<-54224,  11002> velocity=< 5, -1>",
            "position=<-43372, -32538> velocity=< 4,  3>",
            "position=<-43342, -21652> velocity=< 4,  2>",
            "position=< 11079,  54548> velocity=<-1, -5>",
            "position=< 11106, -32538> velocity=<-1,  3>",
            "position=<-32488, -54310> velocity=< 3,  5>",
            "position=< 43748,  43664> velocity=<-4, -4>",
            "position=< 11063,  43658> velocity=<-1, -4>",
            "position=<-54240,  54543> velocity=< 5, -5>",
            "position=< 21983,  43655> velocity=<-2, -4>",
            "position=<-21551,  32773> velocity=< 2, -3>",
            "position=<-21607, -32547> velocity=< 2,  3>",
            "position=<-10709, -54315> velocity=< 1,  5>",
            "position=< 21978,  32775> velocity=<-2, -3>",
            "position=< 32864, -21653> velocity=<-3,  2>",
            "position=<-32464,  43655> velocity=< 3, -4>",
            "position=< 32848, -54312> velocity=<-3,  5>",
            "position=< 54639,  32772> velocity=<-5, -3>",
            "position=<-43380, -21657> velocity=< 4,  2>",
            "position=<-43371,  54544> velocity=< 4, -5>",
            "position=< 11079, -43429> velocity=<-1,  4>",
            "position=< 11099,  43664> velocity=<-1, -4>",
            "position=< 54591, -21653> velocity=<-5,  2>",
            "position=<-10715, -54310> velocity=< 1,  5>",
            "position=< 21949, -10768> velocity=<-2,  1>",
            "position=<-21606,  43657> velocity=< 2, -4>",
            "position=<-54256, -21660> velocity=< 5,  2>",
            "position=<-54242, -54319> velocity=< 5,  5>",
            "position=< 21985, -21661> velocity=<-2,  2>",
            "position=<-32465, -32542> velocity=< 3,  3>",
            "position=<-54250, -43433> velocity=< 5,  4>",
            "position=< 43742,  11005> velocity=<-4, -1>",
            "position=<-32494, -10775> velocity=< 3,  1>",
            "position=< 32843,  11004> velocity=<-3, -1>",
            "position=<-21586,  43664> velocity=< 2, -4>",
            "position=<-10723,  21883> velocity=< 1, -2>",
            "position=<-21552, -43433> velocity=< 2,  4>",
            "position=<-43358, -10775> velocity=< 4,  1>",
            "position=< 32872, -10774> velocity=<-3,  1>",
            "position=<-10680, -54312> velocity=< 1,  5>",
            "position=<-54237, -32545> velocity=< 5,  3>",
            "position=< 54651,  10997> velocity=<-5, -1>",
            "position=<-32489, -43425> velocity=< 3,  4>",
            "position=<-54243, -43429> velocity=< 5,  4>",
            "position=< 54626, -54314> velocity=<-5,  5>",
            "position=< 11107, -21656> velocity=<-1,  2>",
            "position=<-21575,  10997> velocity=< 2, -1>",
            "position=< 43716,  11001> velocity=<-4, -1>",
            "position=<-10720,  32771> velocity=< 1, -3>",
            "position=< 11092,  43660> velocity=<-1, -4>",
            "position=< 54631, -21658> velocity=<-5,  2>",
            "position=< 11068,  11005> velocity=<-1, -1>",
            "position=< 43708, -21652> velocity=<-4,  2>",
            "position=<-10674, -32538> velocity=< 1,  3>",
            "position=< 11051,  11006> velocity=<-1, -1>",
            "position=< 11063, -54316> velocity=<-1,  5>",
            "position=< 11056,  32769> velocity=<-1, -3>",
            "position=<-21587,  21884> velocity=< 2, -2>",
            "position=< 11082,  43660> velocity=<-1, -4>",
            "position=< 43726,  43663> velocity=<-4, -4>",
            "position=< 54633,  54550> velocity=<-5, -5>",
            "position=< 11071,  43663> velocity=<-1, -4>",
            "position=<-43367,  43660> velocity=< 4, -4>",
            "position=<-21563,  11004> velocity=< 2, -1>",
            "position=< 54639,  54542> velocity=<-5, -5>",
            "position=< 32848, -32541> velocity=<-3,  3>",
            "position=<-54209,  54543> velocity=< 5, -5>",
            "position=<-43370,  21892> velocity=< 4, -2>",
            "position=< 54627, -21661> velocity=<-5,  2>",
            "position=< 54612,  54542> velocity=<-5, -5>",
            "position=< 11103,  43663> velocity=<-1, -4>",
            "position=< 32876,  21892> velocity=<-3, -2>",
            "position=< 21978, -21660> velocity=<-2,  2>",
            "position=< 54592,  54547> velocity=<-5, -5>",
            "position=<-43354, -43428> velocity=< 4,  4>",
            "position=< 54615,  11001> velocity=<-5, -1>",
            "position=<-10665, -43430> velocity=< 1,  4>",
            "position=<-21566, -10766> velocity=< 2,  1>",
            "position=< 43753, -43431> velocity=<-4,  4>",
            "position=< 43739, -32547> velocity=<-4,  3>",
            "position=<-43354, -43425> velocity=< 4,  4>",
            "position=<-10669, -21654> velocity=< 1,  2>",
            "position=< 54644, -10767> velocity=<-5,  1>",
            "position=< 54612, -43432> velocity=<-5,  4>",
            "position=< 32829,  54546> velocity=<-3, -5>",
            "position=< 54594, -10766> velocity=<-5,  1>",
            "position=< 54626, -32547> velocity=<-5,  3>",
            "position=<-32441,  11004> velocity=< 3, -1>",
            "position=< 43738,  11006> velocity=<-4, -1>",
            "position=<-43351, -32541> velocity=< 4,  3>",
            "position=< 43732, -10766> velocity=<-4,  1>",
            "position=< 43749,  32777> velocity=<-4, -3>",
            "position=<-32460, -21656> velocity=< 3,  2>",
            "position=< 54628,  10998> velocity=<-5, -1>",
            "position=< 21949,  32777> velocity=<-2, -3>",
            "position=<-43370, -21661> velocity=< 4,  2>",
            "position=<-10682, -43433> velocity=< 1,  4>",
            "position=<-32441, -10767> velocity=< 3,  1>",
            "position=< 21973,  21888> velocity=<-2, -2>",
            "position=< 21973,  11000> velocity=<-2, -1>",
            "position=< 11107, -10771> velocity=<-1,  1>",
            "position=<-10680, -10769> velocity=< 1,  1>",
            "position=< 32867,  54542> velocity=<-3, -5>",
            "position=<-10701,  21887> velocity=< 1, -2>",
            "position=<-54250, -10775> velocity=< 5,  1>",
            "position=< 54615, -10768> velocity=<-5,  1>",
            "position=<-21550,  21883> velocity=< 2, -2>",
            "position=<-43346,  21890> velocity=< 4, -2>",
            "position=<-54216, -54318> velocity=< 5,  5>",
            "position=<-43357,  11006> velocity=< 4, -1>",
            "position=<-10692, -10766> velocity=< 1,  1>",
            "position=< 21944,  21892> velocity=<-2, -2>",
            "position=< 54623, -10772> velocity=<-5,  1>",
            "position=< 43749,  43663> velocity=<-4, -4>",
            "position=< 43740,  21888> velocity=<-4, -2>",
            "position=<-54224,  32776> velocity=< 5, -3>",
            "position=<-54240,  43657> velocity=< 5, -4>",
            "position=<-21592, -54310> velocity=< 2,  5>",
            "position=< 54599,  21883> velocity=<-5, -2>",
            "position=< 32830, -10771> velocity=<-3,  1>",
            "position=< 21961,  54550> velocity=<-2, -5>",
            "position=<-54258, -32543> velocity=< 5,  3>",
            "position=< 11047, -10768> velocity=<-1,  1>",
            "position=< 11047, -21653> velocity=<-1,  2>",
            "position=< 43709,  21883> velocity=<-4, -2>",
            "position=< 43729, -10766> velocity=<-4,  1>",
            "position=<-32481, -54317> velocity=< 3,  5>",
            "position=<-32481, -54318> velocity=< 3,  5>",
            "position=< 43750, -54311> velocity=<-4,  5>",
            "position=<-43325,  11006> velocity=< 4, -1>",
            "position=<-43331,  21892> velocity=< 4, -2>",
            "position=< 32867, -54311> velocity=<-3,  5>",
            "position=< 32847, -10771> velocity=<-3,  1>",
            "position=< 43731,  43664> velocity=<-4, -4>",
            "position=< 21965, -10770> velocity=<-2,  1>",
            "position=<-43362,  21884> velocity=< 4, -2>",
            "position=< 43742,  43663> velocity=<-4, -4>",
            "position=< 54634,  54541> velocity=<-5, -5>",
            "position=<-32437,  10999> velocity=< 3, -1>",
            "position=< 54631, -21654> velocity=<-5,  2>",
            "position=<-54220,  11006> velocity=< 5, -1>",
            "position=< 54607,  54546> velocity=<-5, -5>",
            "position=<-43342,  43655> velocity=< 4, -4>",
            "position=< 21973,  32773> velocity=<-2, -3>",
            "position=<-32496, -32538> velocity=< 3,  3>",
            "position=< 54599, -21652> velocity=<-5,  2>",
            "position=<-54267,  54541> velocity=< 5, -5>",
            "position=< 21949, -10768> velocity=<-2,  1>",
            "position=< 11071, -10773> velocity=<-1,  1>",
            "position=< 32879,  54549> velocity=<-3, -5>",
            "position=<-43351, -54316> velocity=< 4,  5>",
            "position=<-54237,  10999> velocity=< 5, -1>",
            "position=<-54245,  32774> velocity=< 5, -3>",
            "position=<-43375,  54549> velocity=< 4, -5>",
            "position=< 54623, -21658> velocity=<-5,  2>",
            "position=<-43375,  32778> velocity=< 4, -3>",
            "position=<-21551, -21660> velocity=< 2,  2>",
            "position=< 43741, -21656> velocity=<-4,  2>",
            "position=<-54240,  32775> velocity=< 5, -3>",
            "position=< 21984, -10766> velocity=<-2,  1>",
            "position=<-10720, -21659> velocity=< 1,  2>",
            "position=< 32824,  32770> velocity=<-3, -3>",
            "position=< 11057,  32774> velocity=<-1, -3>",
            "position=<-21561,  54550> velocity=< 2, -5>",
            "position=< 11071, -21653> velocity=<-1,  2>",
            "position=<-32497, -54310> velocity=< 3,  5>",
            "position=< 32871,  43655> velocity=<-3, -4>",
            "position=<-43371,  32778> velocity=< 4, -3>",
            "position=<-21582, -54318> velocity=< 2,  5>",
            "position=< 11076, -21658> velocity=<-1,  2>",
            "position=< 43749,  43655> velocity=<-4, -4>",
            "position=< 43754, -54319> velocity=<-4,  5>"
    );

    static List<StarInfo> inputSamCL = parse(
            "position=<-643813942218102619, 833050742891738729> velocity=<357482, -462557>",
            "position=<1238175081108090756, -1086999955861015713> velocity=<-687505, 603564>",
            "position=<1467912069015652483, 285059148678986977> velocity=<-815068, -158281>",
            "position=<-623311712964011048, 856007692672299623> velocity=<346098, -475304>",
            "position=<1782813271344110387, -158397010289167887> velocity=<-989919, 87951>",
            "position=<-126031799297351210, -52871042183559899> velocity=<69980, 29357>",
            "position=<1404815125769245419, 1406700740142354375> velocity=<-780033, -781080>",
            "position=<-769658440756305558, 619212707887824259> velocity=<427358, -343822>",
            "position=<-1387376344508885937, 1158165239501640173> velocity=<770350, -643079>",
            "position=<1725713554328915992, 468188864022719145> velocity=<-958214, -259965>",
            "position=<1621720210704617911, -1140839919279064503> velocity=<-900471, 633459>",
            "position=<-345984123506961450, 1296564291810949267> velocity=<192110, -719926>",
            "position=<637135949766099119, -145828048767356129> velocity=<-353774, 80972>",
            "position=<-269125977367852896, 788905394729731305> velocity=<149434, -438045>",
            "position=<-1385242196435616557, 1537877907410473449> velocity=<769165, -853917>",
            "position=<-1372431905089485727, 1356414088242078131> velocity=<762052, -753158>",
            "position=<-989201945039643937, -1256597191354498053> velocity=<549261, 697734>",
            "position=<-1631744403258000688, -1571262466765151493> velocity=<906037, 872454>",
            "position=<1717225588194469989, 997149620522190363> velocity=<-953501, -553674>",
            "position=<-1336929406382009690, 452241284960440435> velocity=<742339, -251110>",
            "position=<263476338122462783, 1030418917870295909> velocity=<-146297, -572147>",
            "position=<-41272802865050444, 87183100488056833> velocity=<22917, -48409>",
            "position=<925217124156052859, -549965456059884929> velocity=<-513733, 305372>",
            "position=<1337757852047632192, 1213148818131947233> velocity=<-742799, -673609>",
            "position=<-1245177247950970589, -989356828360217879> velocity=<691393, 549347>",
            "position=<846428339422391768, -47644630615097695> velocity=<-469985, 26455>",
            "position=<324129366655429753, -228254790554185265> velocity=<-179975, 126740>",
            "position=<-219123878544012536, -1458161623726065893> velocity=<121670, 809654>",
            "position=<811621014686461725, 611065124863798411> velocity=<-450658, -339298>",
            "position=<-1730030476634036612, 1081905015016814685> velocity=<960611, -600735>",
            "position=<698950603199706405, 516532270546575431> velocity=<-388097, -286808>",
            "position=<-618782276335932954, 390251937549527595> velocity=<343583, -216690>",
            "position=<409898706605961811, -240447349588812805> velocity=<-227599, 133510>",
            "position=<1083010809883959312, -137680465743330281> velocity=<-601349, 76448>",
            "position=<-696449057483394199, 1024778283469047245> velocity=<386708, -569015>",
            "position=<1195886531818269666, 1008630796865728113> velocity=<-664024, -560049>",
            "position=<-7578476871129194, 870301982341108897> velocity=<4208, -483241>",
            "position=<1575507350316354647, -1700047947416138311> velocity=<-874811, 943963>",
            "position=<1413200436679914004, 923189233243748829> velocity=<-784689, -512607>",
            "position=<-1507985426634435611, 537898964843003959> velocity=<837319, -298672>",
            "position=<1734702189800382238, -201796757352159683> velocity=<-963205, 112049>",
            "position=<-325921330649391111, 551087459645157205> velocity=<180970, -305995>",
            "position=<894964449611935700, -22672396704588763> velocity=<-496935, 12589>",
            "position=<-679195776013419111, 237763906018964256> velocity=<377128, -132020>",
            "position=<191297109025002991, 1035652533314110922> velocity=<-106219, -575053>",
            "position=<-1474413566521512198, 789254782684342494> velocity=<818678, -438239>",
            "position=<1609936471596262218, 278876422657439512> velocity=<-893928, -154848>",
            "position=<-1527369254240003799, -164500493681834464> velocity=<848082, 91340>",
            "position=<-892012661686073023, 107973484756260722> velocity=<495296, -59953>",
            "position=<1542493790543273734, -1675837523324188824> velocity=<-856480, 930520>",
            "position=<-1720732074522399719, -514228831403442642> velocity=<955448, 285529>",
            "position=<1297052354366590579, -503118654640574504> velocity=<-720197, 279360>",
            "position=<-927064918183998525, 727674255199701508> velocity=<514759, -404046>",
            "position=<-564783827660122405, -60901562233102616> velocity=<313600, 33816>",
            "position=<-1251325755564592218, 1797465953811233326> velocity=<694807, -998055>",
            "position=<1387432174542919760, -947576152282769680> velocity=<-770381, 526148>",
            "position=<-287991125948018816, -1630645812267186038> velocity=<159909, 905427>",
            "position=<1466795468335967301, 1729578433455208936> velocity=<-814448, -960360>",
            "position=<1770393790235869455, 1193547073296956666> velocity=<-983023, -662725>",
            "position=<-1699570690673526182, 1568443950532437794> velocity=<943698, -870889>",
            "position=<409520503149939395, -191515026254864464> velocity=<-227389, 106340>",
            "position=<48826066172519241, 622308573320693498> velocity=<-27111, -345541>",
            "position=<551746614240427744, -1097031352289800852> velocity=<-306361, 609134>",
            "position=<-1587188434200883169, -1210789548954830182> velocity=<881297, 672299>",
            "position=<580709795096392231, 1610676669788274695> velocity=<-322443, -894339>",
            "position=<-1276063863526134933, 1604196783908423899> velocity=<708543, -890741>",
            "position=<-495920182193792568, 810448583972303631> velocity=<275363, -450007>",
            "position=<236245689288848515, 763945767601089789> velocity=<-131177, -424186>",
            "position=<-471392787586319522, -1580090816010017695> velocity=<261744, 877356>",
            "position=<-1650971546574645257, -110514651787891311> velocity=<916713, 61364>",
            "position=<26186086907481854, 290752011176543501> velocity=<-14540, -161442>",
            "position=<-335994149361454949, 1756861310385131035> velocity=<186563, -975509>",
            "position=<-1519124418898715023, -16100661413989663> velocity=<843504, 8940>",
            "position=<-216399012691812921, 1652885976449214969> velocity=<120157, -917776>",
            "position=<-1798892321131528299, -295814534581656889> velocity=<998847, 164253>",
            "position=<-528687009236039718, 836854389078021355> velocity=<293557, -464669>",
            "position=<-198585629913156934, -515370645646862709> velocity=<110266, 286163>",
            "position=<500121842493367393, 231064301940852817> velocity=<-277696, -128300>",
            "position=<763205569409077356, 1527653807316000697> velocity=<-423775, -848240>",
            "position=<-614987634993841343, -1555421144864326699> velocity=<341476, 863658>",
            "position=<1119329147475140844, -1530648818494858189> velocity=<-621515, 849903>",
            "position=<1377292719983842519, -102255408695896939> velocity=<-764751, 56778>",
            "position=<101117196389714311, 219158096951499395> velocity=<-56146, -121689>",
            "position=<1693541047003275522, 83105707038367507> velocity=<-940350, -46145>",
            "position=<-1412080234062502709, 147992813309947365> velocity=<784067, -82174>",
            "position=<1240503733815885941, 1193500248107163416> velocity=<-688798, -662699>",
            "position=<-462809370103448803, -1262410718764214106> velocity=<256978, 700962>",
            "position=<-1655906201191318748, 553158573809089508> velocity=<919453, -307145>",
            "position=<-269954423033425827, 1426165611345641594> velocity=<149894, -791888>",
            "position=<1642110779890740952, -734662014292852834> velocity=<-911793, 407926>",
            "position=<1625993909757671257, 48968342710248598> velocity=<-902844, -27190>",
            "position=<1045752366559236296, 1039204043863045268> velocity=<-580661, -577025>",
            "position=<1430675237316987961, -149213870183175886> velocity=<-794392, 82852>",
            "position=<-1657559490584788157, 634227385091914336> velocity=<920371, -352159>",
            "position=<760974169018545065, 1491511964670962962> velocity=<-422536, -828172>",
            "position=<-381297520486426238, -1465403319424476132> velocity=<211718, 813675>",
            "position=<-951743594173880523, -1237289004440134408> velocity=<528462, 687013>",
            "position=<-1431737808931477558, 764639140603797560> velocity=<794982, -424571>",
            "position=<-63889369535191141, 358198294167208402> velocity=<35475, -198892>",
            "position=<1069942979993965600, 680521289077896746> velocity=<-594093, -377864>",
            "position=<-1092325420715090425, -1316621681762932508> velocity=<606521, 731063>",
            "position=<-748046814697881497, 1710875372070481168> velocity=<415358, -949975>",
            "position=<750764476674777956, -214428752783308508> velocity=<-416867, 119063>",
            "position=<-416537077743524745, -1482516125325071536> velocity=<231285, 823177>",
            "position=<1016978287431282913, 601723499500044641> velocity=<-564684, -334111>",
            "position=<-718456896686222703, 1101177182554414291> velocity=<398928, -611436>",
            "position=<981448774191233866, 55014195100092713> velocity=<-544956, -30547>",
            "position=<1402837661984899625, -1210378928059720123> velocity=<-778935, 672071>",
            "position=<-1410061347994878336, -1681063934892651025> velocity=<782946, 933422>",
            "position=<-634065297896915181, 959041119905836047> velocity=<352069, -532514>",
            "position=<91503624731391974, -1087320528314215665> velocity=<-50808, 603742>",
            "position=<-1000690325258534519, -1762725264923244311> velocity=<555640, 978765>",
            "position=<-769800717294523508, -815087879700439547> velocity=<427437, 452583>",
            "position=<847811483490130875, 1404750290890581613> velocity=<-470753, -779997>",
            "position=<-420407359776820898, 467326199949220391> velocity=<233434, -259486>",
            "position=<-1011868938837254323, 1089782452715110237> velocity=<561847, -605109>",
            "position=<-1477460805795749968, -743567805197761723> velocity=<820370, 412871>",
            "position=<-552699326755787007, -939949049252984207> velocity=<306890, 521913>",
            "position=<-1748641688608016113, 1177032189050644329> velocity=<970945, -653555>",
            "position=<-1081737524915300899, 441882112203102535> velocity=<600642, -245358>",
            "position=<-1544653152180228372, -268911662076595411> velocity=<857679, 149315>",
            "position=<-431326633842839607, 1287771961942847107> velocity=<239497, -715044>",
            "position=<1730777878701940040, -1110805161964369745> velocity=<-961026, 616782>",
            "position=<-712873893287796467, -688441950029236705> velocity=<395828, 382262>",
            "position=<-815624568413735169, 1500405148794004439> velocity=<452881, -833110>",
            "position=<859018912570261950, 295013103447729435> velocity=<-476976, -163808>",
            "position=<-251294584900814865, 867487068046999175> velocity=<139533, -481678>",
            "position=<1609725758242192628, 1542963843409555901> velocity=<-893811, -856741>",
            "position=<-1026770155004537633, -1776852064490100799> velocity=<570121, 986609>",
            "position=<-540054724542770721, 16541898778421589> velocity=<299869, -9185>",
            "position=<1157538502346434481, 1638537657715259637> velocity=<-642731, -909809>",
            "position=<954081251725916342, 1473115067988729533> velocity=<-529760, -817957>",
            "position=<-1675585387686351935, 539258696315846473> velocity=<930380, -299427>",
            "position=<1435581076432250258, 1718891484369318271> velocity=<-797116, -954426>",
            "position=<-462287089140370149, 985978210818823361> velocity=<256688, -547471>",
            "position=<1507990829540999872, 1063054274187354355> velocity=<-837322, -590268>",
            "position=<412371436820813173, -1116762766881141961> velocity=<-228972, 620090>",
            "position=<-188030151552455065, 1239041347118777392> velocity=<104405, -687986>",
            "position=<806997927678797135, 1508767047109776326> velocity=<-448091, -837753>",
            "position=<-895746070087665780, -1012302972327749556> velocity=<497369, 562088>",
            "position=<1484709705369562551, 1623526582448845968> velocity=<-824395, -901474>",
            "position=<-19196526846370356, -80308802433567364> velocity=<10659, 44592>",
            "position=<1000701131071613255, -450546573284619918> velocity=<-555646, 250169>",
            "position=<516523265702872970, 437096937999999822> velocity=<-286803, -242701>",
            "position=<-693454046305464325, 642439802994115458> velocity=<385045, -356719>",
            "position=<1060855291236398260, -940701854227352642> velocity=<-589047, 522331>",
            "position=<-1220082548159463949, 1508430265937032552> velocity=<677459, -837566>",
            "position=<1469039475508366960, 1216205062250376032> velocity=<-815694, -675306>",
            "position=<536627480843721909, 1765095339913390582> velocity=<-297966, -980081>",
            "position=<558631718108873953, -717558213236448438> velocity=<-310184, 398429>",
            "position=<356746713284106165, -1238468639029156716> velocity=<-198086, 687668>",
            "position=<230181827210622418, -714510973962210654> velocity=<-127810, 396737>",
            "position=<-767967331017233837, -1155011743066876036> velocity=<426419, 641328>",
            "position=<517706502229571722, -538088066571942734> velocity=<-287460, 298777>",
            "position=<1754664128403013181, 1566104492011613400> velocity=<-974289, -869590>",
            "position=<1068399549699626476, 769528770999515992> velocity=<-593236, -427286>",
            "position=<326679538530323787, -590777210902380446> velocity=<-181391, 328033>",
            "position=<281603089478965930, -607951249743474718> velocity=<-156362, 337569>",
            "position=<9388450553571835, -421188980253089116> velocity=<-5213, 233868>",
            "position=<152198075547637630, -1118068469288838410> velocity=<-84509, 620815>",
            "position=<-1316457793598167531, -713507834319332140> velocity=<730972, 396180>",
            "position=<1704373874565060544, -994430157577432908> velocity=<-946365, 552164>",
            "position=<141779470818639065, -1325975913908553694> velocity=<-78724, 736257>",
            "position=<-419526686014940070, 1648756354903217786> velocity=<232945, -915483>",
            "position=<-361117664654372804, -99955571489512982> velocity=<200513, 55501>",
            "position=<1586617527079222785, -881644484085032658> velocity=<-880980, 489539>",
            "position=<-1699338365693398193, -1345853206975789167> velocity=<943569, 747294>",
            "position=<968101794131318843, -1638872637920092769> velocity=<-537545, 909995>",
            "position=<1021210564201057626, 446629466060603009> velocity=<-567034, -247994>",
            "position=<-479531365766154365, -1099795839456440917> velocity=<266263, 610669>",
            "position=<69648867879810712, -1188792515565030949> velocity=<-38673, 660085>",
            "position=<-1169540158684163021, 456147586370500579> velocity=<649395, -253279>",
            "position=<979523538503195947, -1766464076231351661> velocity=<-543887, 980841>",
            "position=<638022026434494509, 1235880646807732883> velocity=<-354266, -686231>",
            "position=<-1791542567302825947, 456934609752794853> velocity=<994766, -253716>",
            "position=<-18178979452786195, 917008308191039571> velocity=<10094, -509175>",
            "position=<-1647542501906708620, -1004139180584179889> velocity=<914809, 557555>",
            "position=<981938637715224857, 325215350864376977> velocity=<-545228, -180578>",
            "position=<637568182287267634, 338358821445575173> velocity=<-354014, -187876>",
            "position=<-582492754246162657, -1514535550299464891> velocity=<323433, 840956>",
            "position=<-156430352317362704, -793733792185878431> velocity=<86859, 440726>",
            "position=<1295773666491467191, 1796943672848154751> velocity=<-719487, -997765>",
            "position=<-1777050171061814418, 530468167416582513> velocity=<986719, -294546>",
            "position=<-186863123745300092, 369306669961238341> velocity=<103757, -205060>",
            "position=<-153519986674828261, -58403618454516437> velocity=<85243, 32429>",
            "position=<1275970213146597927, 676334036529077100> velocity=<-708491, -375539>",
            "position=<-550219392665582873, 1208098901509628832> velocity=<305513, -670805>",
            "position=<-21933999480437400, -509521098860382608> velocity=<12179, 282915>",
            "position=<-1468295675378140001, 191590666945141386> velocity=<815281, -106382>",
            "position=<-176979406761247572, 669800121584080244> velocity=<98269, -371911>",
            "position=<-172999265628821149, -747472305638983656> velocity=<96059, 415039>",
            "position=<-687584688861763998, 361494067141118066> velocity=<381786, -200722>",
            "position=<-748016198227632105, 1233413319499396144> velocity=<415341, -684861>",
            "position=<1593403577661567881, 1708415248637497240> velocity=<-884748, -948609>",
            "position=<-187862661450502231, 1661836791575078914> velocity=<104312, -922746>",
            "position=<-1693641901258165214, 1692172310685753402> velocity=<940406, -939590>",
            "position=<1459087321708462737, -1343450714545627698> velocity=<-810168, 745960>",
            "position=<-1725061603609437298, 1237234975374060788> velocity=<957852, -686983>",
            "position=<-678147612149585483, -1472893548822558246> velocity=<376546, 817834>",
            "position=<487216099798811874, 451983746416577556> velocity=<-270530, -250967>",
            "position=<677176889945844231, -1705486873307508344> velocity=<-376007, 946983>",
            "position=<1142104199403043342, -661240116697033694> velocity=<-634161, 367158>",
            "position=<-124302869212677218, 1644502466507384664> velocity=<69020, -913121>",
            "position=<1451409791551207635, 622092457060109264> velocity=<-805905, -345421>",
            "position=<-1593484621259237441, -707132404632097057> velocity=<884793, 392640>",
            "position=<-279929989428226713, 1290907448690156793> velocity=<155433, -716785>",
            "position=<1060145709514146664, -1510013317546739667> velocity=<-588653, 838445>",
            "position=<-1796624901364232015, 1437888117513498417> velocity=<997588, -798397>",
            "position=<824663631012720566, -714224619916936533> velocity=<-457900, 396578>",
            "position=<-475254064775424607, -1154066234426819983> velocity=<263888, 640803>",
            "position=<1447634960866336192, 563357460339827439> velocity=<-803809, -312808>",
            "position=<-1242338921061964257, 328579560654138315> velocity=<689817, -182446>",
            "position=<-346446972498379349, 604709505833783561> velocity=<192367, -335769>",
            "position=<-79512774206593507, 1561092395734897237> velocity=<44150, -866807>",
            "position=<-1426007126088318802, -1142561645488385607> velocity=<791800, 634415>",
            "position=<-1127138148357535101, 1669480103324408203> velocity=<625851, -926990>",
            "position=<-1687450170392426730, 339430397904305365> velocity=<936968, -188471>",
            "position=<-722505474634500743, -935561889163124131> velocity=<401176, 519477>",
            "position=<-747641596709286052, 925743007056319273> velocity=<415133, -514025>",
            "position=<-551928512093036507, 1579265972281193619> velocity=<306462, -876898>",
            "position=<638755020751642766, -1300252675992514525> velocity=<-354673, 721974>",
            "position=<85881000018525394, 99816896888043871> velocity=<-47686, -55424>",
            "position=<1779922716358796253, -464891290080898845> velocity=<-988314, 258134>",
            "position=<-1191135576023043227, 464291567456850026> velocity=<661386, -257801>",
            "position=<-1087956270313612444, 1264065809125594186> velocity=<604095, -701881>",
            "position=<1364145647464967849, -306771628993277850> velocity=<-757451, 170337>",
            "position=<1177842625028323758, 1062841759864446524> velocity=<-654005, -590150>",
            "position=<-1754089619343577191, -1202375422542750430> velocity=<973970, 667627>",
            "position=<-605374063335519126, 558321951468214648> velocity=<336138, -310012>",
            "position=<760307810548410285, 401135192238782290> velocity=<-422166, -222733>",
            "position=<860420066326383066, 628113095886218552> velocity=<-477754, -348764>",
            "position=<282282054730968039, 401682686765595698> velocity=<-156739, -223037>",
            "position=<1310698295253647114, -124437941876030966> velocity=<-727774, 69095>",
            "position=<-341330420029047483, 104765959255422968> velocity=<189526, -58172>",
            "position=<640107548349132422, 1755281860714027888> velocity=<-355424, -974632>",
            "position=<-155628921184362851, -28132934222017218> velocity=<86414, 15621>",
            "position=<-493760820556788350, 486936949628401974> velocity=<274164, -270375>",
            "position=<-1655029129367114353, 828600548892541596> velocity=<918966, -460086>",
            "position=<328024862252460656, 1121423674233481180> velocity=<-182138, -622678>",
            "position=<-236025971090538329, 1615485256586274042> velocity=<131055, -897009>",
            "position=<920781337907561322, -105543977794453784> velocity=<-511270, 58604>",
            "position=<-638753219782754959, -606544693080838952> velocity=<354672, 336788>",
            "position=<1307361099996458828, -465123615061026902> velocity=<-725921, 258263>",
            "position=<-923756538428221457, -1318157908181918808> velocity=<512922, 731916>",
            "position=<-456785129339663078, -588102772177650472> velocity=<253633, 326548>",
            "position=<-318832717302228079, 304275486182602326> velocity=<177034, -168951>",
            "position=<-323282911301425220, -894516008371662348> velocity=<179505, 496686>",
            "position=<61045639739719785, 125808479160975136> velocity=<-33896, -69856>",
            "position=<-739742547384932096, -84469040449813980> velocity=<410747, 46902>",
            "position=<-847310814153061149, -1281861182216795700> velocity=<470475, 711762>",
            "position=<1193482238419269984, 729219486462878832> velocity=<-662689, -404904>",
            "position=<-1109838041697766681, 991575621967955182> velocity=<616245, -550579>",
            "position=<-782384086567040838, -865974254223837052> velocity=<434424, 480838>",
            "position=<497285316573199253, -1354225911104590262> velocity=<-276121, 751943>",
            "position=<-114044550710278636, -291222064044241782> velocity=<63324, 161703>",
            "position=<1329781361063235553, 166976825833434654> velocity=<-738370, -92715>",
            "position=<-763999796666674818, -443470566719324256> velocity=<424216, 246240>",
            "position=<-135476079884882431, -437457131768567778> velocity=<75224, 242901>",
            "position=<1778510756789645881, 701304469470747832> velocity=<-987530, -389404>",
            "position=<315837706124347764, -503651741416682288> velocity=<-175371, 279656>",
            "position=<1382121117439062089, -83215566138425388> velocity=<-767432, 46206>",
            "position=<1346708667173496164, 1664116818124242648> velocity=<-747769, -924012>",
            "position=<-630919005336576217, 297865838087441408> velocity=<350322, -165392>",
            "position=<1124229583683888500, 390986732835514020> velocity=<-624236, -217098>",
            "position=<-1114848337005644625, -1647755016230105030> velocity=<619027, 914927>",
            "position=<-1158037370714566786, -983285762406638928> velocity=<643008, 545976>"
    );
}

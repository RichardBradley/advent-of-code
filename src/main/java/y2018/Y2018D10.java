package y2018;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

public class Y2018D10 {
    public static void main(String[] args) throws Exception {

        // 1
        runLoop(testInput, 10);

        // look at 10886s
        runLoop(input, 20000);

        System.exit(0);
    }

    private static double dist(StarInfo a, StarInfo b, int t) {
        double dx = (a.position.x + t * a.velocity.x) - (b.position.x + t * b.velocity.x);
        double dy = (a.position.y + t * a.velocity.y) - (b.position.y + t * b.velocity.y);
        return Math.sqrt(dx * dx + dy * dx);
    }

    private static void runLoop(List<StarInfo> stars, int maxTime) throws Exception {
        JFrame frame = new JFrame();
        frame.setSize(500, 500);

        AtomicInteger currentSecond = new AtomicInteger();
        AtomicInteger speed = new AtomicInteger(1);

        frame.getContentPane().add(new StarPanel(stars), BorderLayout.CENTER);
        JSlider slider = new JSlider();
        frame.getContentPane().add(slider, BorderLayout.SOUTH);
        slider.setMinimum(0);
        slider.setMaximum(maxTime);
        slider.setValue(0);
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

        frame.setVisible(true);
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
            int minX = -1 + stars.stream().mapToInt(x -> x.position.x).min().getAsInt();
            int minY = -1 + stars.stream().mapToInt(x -> x.position.y).min().getAsInt();
            int maxX = 1 + stars.stream().mapToInt(x -> x.position.x).max().getAsInt();
            int maxY = 1 + stars.stream().mapToInt(x -> x.position.y).max().getAsInt();
            int starFieldWidth = (maxX - minX);
            int starFieldHeight = (maxY - minY);

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
        Point position;
        Point intitialPosition;
        Point velocity;
    }

    private static List<StarInfo> parse(String... input) {
        Pattern pattern = Pattern.compile("position=< *(-?\\d+), *(-?\\d+)> velocity=< *(-?\\d+), *(-?\\d+)>");

        return Arrays.stream(input)
                .map(x -> {
                    Matcher matcher = pattern.matcher(x);
                    checkArgument(matcher.matches());
                    StarInfo starInfo = new StarInfo();
                    starInfo.position = new Point(
                            Integer.parseInt(matcher.group(1)),
                            Integer.parseInt(matcher.group(2)));
                    starInfo.intitialPosition = (Point) starInfo.position.clone();
                    starInfo.velocity = new Point(
                            Integer.parseInt(matcher.group(3)),
                            Integer.parseInt(matcher.group(4)));
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
}

package y2018;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;
import static java.awt.event.KeyEvent.*;

public class Y2018D13 {
    public static void main(String[] args) throws Exception {

        // 1
        assertThat(getLocationOfFirstCrash(testInput, true)).isEqualTo("7,3");

        System.out.println(getLocationOfFirstCrash(input, false));

        // 2
        assertThat(getLocationOfLastCart(testInput2, true)).isEqualTo("6,4");

        System.out.println(getLocationOfLastCart(input, false));
    }

    private static String getLocationOfFirstCrash(String[] input, boolean log) {
        return run(input, log, true);
    }

    private static String getLocationOfLastCart(String[] input, boolean log) {
        return run(input, log, false);
    }

    private static String run(String[] input, boolean log, boolean reportFirstCrashVsLastCart) {
        Object[][] map = new Object[input.length][];
        Object[][] nextMap = new Object[input.length][];
        for (int y = 0; y < input.length; y++) {
            String row = input[y];
            map[y] = new Object[row.length()];
            nextMap[y] = new Object[row.length()];
            for (int x = 0; x < row.length(); x++) {
                char c = row.charAt(x);
                switch (c) {
                    case '^':
                    case '>':
                    case '<':
                    case 'v':
                        map[y][x] = new CartState(c);
                        break;
                    default:
                        map[y][x] = c;
                }
            }
        }

        for (int tick = 0; ; tick++) {
            if (log) {
                for (Object[] line : map) {
                    for (Object o : line) {
                        if (o instanceof Character) {
                            System.out.print((char) o);
                        } else {
                            System.out.print(((CartState) o).cartShape);
                        }
                    }
                    System.out.println();
                }
                System.out.println();
            }

            // Do a two-pass iteration, to avoid races and weirdness
            // 1. Copy the track
            for (int y = 0; y < map.length; y++) {
                Object[] row = map[y];
                for (int x = 0; x < row.length; x++) {
                    Object c = row[x];
                    if (c instanceof CartState) {
                        nextMap[y][x] = ((CartState) c).trackUnderCart;
                    } else {
                        nextMap[y][x] = c;
                    }
                }
            }
            // 2. Move the carts:
            for (int y = 0; y < map.length; y++) {
                Object[] row = map[y];
                for (int x = 0; x < row.length; x++) {
                    Object c = row[x];
                    if (c instanceof CartState) {
                        CartState cart = (CartState) c;
                        Point velocity = cart.getVelocity();
                        int nextX = x + velocity.x;
                        int nextY = y + velocity.y;
                        Object destState = nextMap[nextY][nextX];

                        if (destState instanceof CartState) {
                            if (reportFirstCrashVsLastCart) {
                                return nextX + "," + nextY;
                            } else {
                                // delete this cart and that
                                nextMap[nextY][nextX] = ((CartState) destState).trackUnderCart;
                            }
                        } else if (map[nextY][nextX] instanceof CartState) {
                            if (reportFirstCrashVsLastCart) {
                                return nextX + "," + nextY;
                            } else {
                                // delete this cart and that
                                map[nextY][nextX] = '\0';
                            }
                        } else {
                            char nextChar = (char) destState;
                            cart.moveToTrack(nextChar);
                            nextMap[nextY][nextX] = cart;
                        }
                        row[x] = null;
                    }
                }
            }

            Object[][] tmp = nextMap;
            nextMap = map;
            map = tmp;

            if (!reportFirstCrashVsLastCart) {
                String lastCartPos = null;
                outer:
                for (int y = 0; y < map.length; y++) {
                    Object[] row = map[y];
                    for (int x = 0; x < row.length; x++) {
                        Object c = row[x];
                        if (c instanceof CartState) {
                            if (lastCartPos != null) {
                                // two carts:
                                lastCartPos = null;
                                break outer;
                            } else {
                                lastCartPos = x + "," + y;
                            }
                        }
                    }
                }
                if (lastCartPos != null) {
                    return lastCartPos;
                }
            }
        }
    }

    private static void set(String[] map, int x, int y, char c) {
        StringBuilder s = new StringBuilder(map[y]);
        s.setCharAt(x, c);
        map[y] = s.toString();
    }

    static class CartState {
        int[] moveChoices = new int[]{VK_LEFT, KeyEvent.VK_UP, KeyEvent.VK_RIGHT};
        int nextMoveChoiceId = 0;
        char trackUnderCart;
        char cartShape;

        public CartState(char initalState) {
            cartShape = initalState;
            switch (initalState) {
                case '^':
                    trackUnderCart = '|';
                    break;
                case 'v':
                    trackUnderCart = '|';
                    break;
                case '>':
                    trackUnderCart = '-';
                    break;
                case '<':
                    trackUnderCart = '-';
                    break;
                default:
                    throw new IllegalArgumentException(initalState + "");
            }
        }

        Point getVelocity() {
            switch (cartShape) {
                case '^':
                    return new Point(0, -1);
                case 'v':
                    return new Point(0, 1);
                case '>':
                    return new Point(1, 0);
                case '<':
                    return new Point(-1, 0);
                default:
                    throw new IllegalArgumentException(cartShape + "");
            }
        }

        public void moveToTrack(char nextChar) {
            trackUnderCart = nextChar;
            switch (nextChar) {
                case '|':
                case '-':
                    // no change
                    return;
                case '/':
                    if (cartShape == '>') {
                        cartShape = '^';
                    } else if (cartShape == 'v') {
                        cartShape = '<';
                    } else if (cartShape == '<') {
                        cartShape = 'v';
                    } else if (cartShape == '^') {
                        cartShape = '>';
                    } else {
                        throw new IllegalStateException(cartShape + "");
                    }
                    break;
                case '\\':
                    if (cartShape == '>') {
                        cartShape = 'v';
                    } else if (cartShape == 'v') {
                        cartShape = '>';
                    } else if (cartShape == '<') {
                        cartShape = '^';
                    } else if (cartShape == '^') {
                        cartShape = '<';
                    } else {
                        throw new IllegalStateException(cartShape + "");
                    }
                    break;
                case '+':
                    switch (moveChoices[(nextMoveChoiceId++) % moveChoices.length]) {
                        case VK_LEFT:
                            if (cartShape == '>') {
                                cartShape = '^';
                            } else if (cartShape == '^') {
                                cartShape = '<';
                            } else if (cartShape == 'v') {
                                cartShape = '>';
                            } else if (cartShape == '<') {
                                cartShape = 'v';
                            } else {
                                throw new IllegalStateException(cartShape + "");
                            }
                            break;
                        case VK_RIGHT:
                            if (cartShape == '>') {
                                cartShape = 'v';
                            } else if (cartShape == '^') {
                                cartShape = '>';
                            } else if (cartShape == 'v') {
                                cartShape = '<';
                            } else if (cartShape == '<') {
                                cartShape = '^';
                            } else {
                                throw new IllegalStateException(cartShape + "");
                            }
                            break;
                        case VK_UP:
                            // no change
                            break;
                        default:
                            throw new IllegalStateException();

                    }
                    break;
                default:
                    throw new IllegalStateException(nextChar + "");
            }
        }
    }

    static String[] testInput = new String[]{
            "/->-\\        ",
            "|   |  /----\\",
            "| /-+--+-\\  |",
            "| | |  | v  |",
            "\\-+-/  \\-+--/",
            "  \\------/   "
    };

    static String[] testInput2 = new String[]{
            "/>-<\\  ",
            "|   |  ",
            "| /<+-\\",
            "| | | v",
            "\\>+</ |",
            "  |   ^",
            "  \\<->/"
    };

    static String[] input = new String[]{
            "     /----------------------------------------------------------------------------------------------\\                                                 ",
            "     |                                                          /-----------------------------------+-------------------------------\\                 ",
            "     |      /-------------\\                             /-------+-----------------------------------+----------------\\              |                 ",
            "     |      |        /----+-----------------------------+-------+----------------------------------<+\\               |              |                 ",
            "     |      |        |    | /-----------------------\\   |       |   /-------------------------------++---------------+\\             |                 ",
            "     |      |        |    | |                       |   |      /+---+-----------------\\          /--++---------------++\\            |                 ",
            "     |      |        |    | |                       |   |      ||/--+-----------------+----------+\\ ||               |||            |                 ",
            "     |      |      /-+----+-+-----------------------+---+-----\\|||  |                 |          || ||          /----+++------------+------------\\    ",
            "     |      |      | |    | |                       |   | /---++++--+---------------\\ |          || ||          |    |||            |            |    ",
            "     |     /+------+-+----+-+---------------\\       |   |/+---++++--+---------------+-+----------++-++----------+---\\|||           /+------------+--\\ ",
            "     |     ||      | |    | |  /------------+-------+---+++---++++--+---------------+-+----------++-++----------+---++++-------\\/--++------\\     |  | ",
            "     |     ||      | |    | |  |            |       |   |||   ||||  |               | |          || ||          |   ||||       ||  ||      |     |  | ",
            "     \\-----++------+-+----+-+--+------------+-------+---+++---++++--+---------------+-+----------++-/|          |   ||||       ||  ||      |     |  | ",
            "           ||   /--+-+----+-+--+---------\\  |       |/--+++---++++--+---------------+-+----------++--+----------+---++++-------++--++------+-----+-\\| ",
            "           ||   |  | |    | |  |     /---+--+---\\   ||/-+++---++++--+---------------+-+--------\\ ||  |          |   ||||       ||  ||      |     | || ",
            "           ||  /+--+-+---\\| |  |     |   |  |   |/--+++-+++---++++--+---------------+-+--------+-++--+---->-----+---++++-------++--++------+-\\   | || ",
            "           ||  ||  | |   || |/-+-----+---+--+---++--+++-+++---++++--+---------\\     | |        | ||/-+----------+---++++-------++--++------+-+---+\\|| ",
            "           ||  ||  | |   || || |     | /-+--+---++--+++-+++---++++--+---------+-----+-+--------+-+++-+----------+---++++-------++--++-----\\| |   |||| ",
            "           ||  ||  | |   || || |   /-+-+-+--+---++--+++-+++---++++--+---------+-----+-+----\\   | \\++-+----------+---+++/       ||  \\+-----++-+---+++/ ",
            "  /--------++--++--+-+---++-++-+---+-+-+-+--+---++--+++-+++---++++--+-\\       |     | |    |   |  || |          |   |||        ||   |     || |   |||  ",
            "  |        ||  ||  | |   || || |/--+-+-+-+--+---++--+++-+++---++++--+-+-------+-----+-+----+---+\\ || |          |   |||        ||   |     v| |   |||  ",
            "  |        ||  ||  | |   || || ||  | | | |  |   ||  ||| |||   ||||  | |       |     | |    |   || || |          |   |||        ||   |     || |   |||  ",
            "  |        ||  ||  | |   || || ||  | | | |  |   ||  ||| |\\+---++++--+-+-------+-----+-+----+---++-++-+----------+---/||        ||   |     || |   |||  ",
            "  |     /--++--++--+-+---++-++-++--+-+-+-+--+---++--+++-+-+---++++--+\\|       |     | |    |   || || |          |    ||        ||  /+-----++-+\\  |||  ",
            "  |     |  ||  ||  | |   || || \\+--+-+-+-+--+---++--+++-+-+---++++--+++-------+-----+-+----+---++-++-+----------+----++--------/|  ||     || ||  |||  ",
            "  |     |  ||  ||  | |/--++-++--+--+-+-+-+\\ |   ||  |\\+-+-+---++++--+++-------+-----+-+----+---++-++-+----------+----++---------+--++-----++-++--++/  ",
            "  |     |  ||  ||  | ||  || ||  |  | | | || |   ||  | | | |   ||\\+--+++-------+-----+-+----+---++-++-+----------+----++---------+--+/     || ||  ||   ",
            "  |     |  ||  \\+--+-++--/| ||  |  | | | || |   ||  | | | \\---++-+--+++-------+-----/ |    |   || || |          |    ||         |  |      || ||  ||   ",
            "  |     |  ||/--+--+>++---+-++--+--+-+-+-++-+---++--+-+-+-----++-+--+++-------+-------+-\\  |   || || |/---------+----++-----\\   |  |  /---++-++-\\||   ",
            "  | /---+--+++--+--+-++---+-++--+--+-+-+-++-+-\\ ||  | | |     || |  |||       |       | |  |   || || ||         |    ||     |   |  |  |   || || |||   ",
            "  | |   |  |||  |  | ||   | ||  |  | | | || | | ||  | | | /---++-+--+++-------+-------+-+--+---++-++-++---------+----++-----+---+--+--+---++-++-+++-\\ ",
            "/-+-+---+--+++--+--+-++---+-++--+--+-+-+-++-+-+-++--+-+-+-+---++-+--+++-------+-------+-+\\ |   || || ||         |    ||     |   |  |  |   || || ||| | ",
            "| | |   |/-+++--+--+-++---+-++--+--+-+-+-++-+-+-++--+-+-+-+---++-+--+++-------+-------+-++-+---++\\|| ||         |/---++-----+---+-\\|  |   || || ||| | ",
            "| | |   || |||  |  | ||   | ||  |/-+-+-+-++-+-+-++--+-+-+\\|   || |  |||   /---+-------+-++-+---+++++-++---------++---++-----+---+-++--+---++\\|| ||| | ",
            "| | |   || |||/-+--+-++---+-++--++-+-+-+-++-+-+-++\\ | \\-+++---++-+--+++---+---+-------+-++-+---/|||| ||         ||   ||   /-+---+-++--+---+++++-+++-+\\",
            "| | |   || |||| |  | ||   | ||  || | | | || | | ||| |   |||   || |  |||   |   |       | || |    |||| ||         ||   ||   | |   | ||  |   ||||| ||| ||",
            "| | |   || |||| |  | ||   | ||/-++-+-+-+-++-+-+-+++-+---+++---++-+--+++---+---+-------+-++-+----++++-++---------++---++---+-+\\  | || /+---+++++-+++\\||",
            "|/+-+---++-++++-+--+-++---+-+++-++-+-+-+-++-+-+-+++-+---+++---++-+--+++---+---+-------+-++-+----++++-++-----\\   ||   ||   | ||  | || ||   ||||| ||||||",
            "||| |   || \\+++-+--+-++---+-+++-++-+-+-+-++-/ | ||| |   |||/--++-+--+++---+---+-------+-++-+----++++-++-----+---++---++---+-++-\\| || ||   ||||| ||||||",
            "||| |   ||  ||| |  | ||   | ||| ||/+-+-+-++---+-+++-+---++++--++-+\\ |||   |   |       | || |/---++++-++-----+---++---++---+-++-++-++-++---+++++\\||||||",
            "||| |   ||  ||| |  | ||   | ||| |||| | |/++---+-+++-+---++++--++-++-+++---+---+-------+-++-++---++++-++-----+-\\ ||   ||   | || || || ||   ||||||||||||",
            "||| |   ||  ||| |  | ||   | ||| |||| \\-++++---+-/|| |   ||||  || || |||   |   |       | || ||   |||| ||     | | ||   ||   | || || || ||   ||||||||||||",
            "||| |/--++--+++-+--+-++---+\\||| ||||   ||||   |  || |   ||||  || || |||   |   |       | || ||   |||| ||     | | ||   ||   | || || || ||   ||||||||||||",
            "||| || /++--+++-+--+-++---+++++-++++---++++---+--++-+---++++--++-++-+++\\  |   |       | || ||   |||| ||     | | ||   ||   | || || || ||   ||||||||||||",
            "||| || |||  ||| |  | ||   ||||| |\\++---++++---+--++-+---+/||  |\\-++-++++--+---+-------/ || ||   |||| ||     | | ||   ||   | || || || ||   ||||||||||||",
            "||| || |||  ||| |  | ||   ||||| | ||   ||||   |  || |   | ||  |  || ||||  | /-+---------++-++---++++-++-----+-+-++---++---+-++\\|| || ||   ||||||||||||",
            "||| || |||  ||| |  | ||   ||||| \\-++---++++---+--++-+---+-++--+--++-++++--+-+-+---------++-++---/||| ||     | | ||   ||   | ||||| || ||   ||||||||||||",
            "||| || ||v  ||| \\--+-++---+++++---++---++/| /-+--++-+---+-++--+--++-++++--+-+-+---------++-++----+++-++-\\   | | ||   ||   | ||||| || ||   ||||||||||||",
            "||| || |||  |||    | ||   |||||   ||   || | |/+--++-+---+-++--+--++-++++--+-+-+---------++-++----+++-++-+---+-+-++---++---+-+++++-++-++--\\||||||||||||",
            "||| || |||  |||    | ||   |||||   ||   || | |||  || |   |/++--+--++-++++--+-+-+--\\      || ||    ||| || |   | | ||   ||   | |^||| || ||  |||||||||||||",
            "||| || |||  |\\+----+-++---+++++---++---++-+-+++--++-+---++++--+--++-++++--+-+-+--+------/| ||    ||| || |   | | ||   ||   | ||||| || ||  |||||||||||||",
            "|||/++-+++--+-+----+-++---+++++---++---++-+-+++--++-+---++++--+--++-++++--+-+-+--+-------+\\||    ||| || |   | | ||   ||   \\-+++++-++-++--++++++++++++/",
            "|||||| |||  | |    | ||   |||||   \\+---++-+-+++--++-+---++++--+--+/ ||||  | | |  |       ||||    ||| || |   | | ||   ||     ||||| || ||  |||||||||||| ",
            "|||||| |||  | |    | ||   |||||   /+---++-+-+++--++-+---++++--+--+--++++--+-+-+--+-------++++--\\ ||| |\\-+---+-+-++---++-----/|||| || ||  |||||||||||| ",
            "|||||| |||  | |    | ||   |||||   ||   || | |||  || |   ||||  |  |  ||||  | | |  |       ||||  | ||| |  |   | | ||   ||      |||| || ||  |||||||||||| ",
            "|||||| ||| /+-+----+-++---+++++---++---++-+-+++--++-+---++++--+--+--++++--+-+-+\\ |       ||||  | ||| |  |   | | ||   ||      |||| || ||  |||||||||||| ",
            "|||||| ||| || |    | || /-+++++---++---++-+-+++--++-+---++++--+--+--++++--+-+-++-+-------++++--+-+++-+--+---+-+-++---++--\\   |||| || ||  |||||||||||| ",
            "|||||| ||| || |    | || | |||||   || /-++-+-+++--++-+---++++--+--+-\\||||  | | || |       ||||  | ||| |  |   | | ||   ||  |   |||| || ||  |||||||||||| ",
            "|||||| ||| || |    | || | |||||/--++-+-++-+-+++--++-+---++++--+--+-+++++--+-+-++-+\\      ||||  | ||| |  |   | | ||   ||  |   |||| || ||  |||||||||||| ",
            "|||||| ||| ||/+----+-++-+-++++++--++-+-++-+-+++-\\|| |   |||\\--+--+-+++++--+-+-++-++------++++--+-+++-+--+---+-+-++---++--+---++/| || ||  |||||||||||| ",
            "|||||| ||| ||||    | || |/++++++--++-+-++-+-+++-+++-+---+++---+--+-+++++--+-+-++-++------++++--+-+++-+--+---+-+\\||   ||  |   || | || ||  |||||||||||| ",
            "|||||| ||| |\\++----+-++-++/|||||  || | || | \\++-+++-+---+++---+--+-+++++--+-+-++-++------++++--+-+++-+--/   | ||||   ||  |   || | || ||  |||||||||||| ",
            "|||||| ||| | ||/---+-++-++-+++++--++\\| || |  || ||| |   |||   |  | |||||  | | || ||      ||||  | ||| |  /---+-++++---++--+--\\|| | || ||  |||||||||||| ",
            "|||||| ||| | |\\+---+-++-++-+++++--++++-++-+--++-++/ |   |||   |  | |||||  | | || ||      ||||  | ||| |  |   | ||\\+---++--+--+++-+-++-++--++++++++/||| ",
            "|||||| ||| | | |   | || || |||||  |||| || |/-++-++--+---+++---+--+-+++++--+-+-++-++------++++--+-+++-+--+---+-++-+---++--+-\\||| | || ||  |||||||| ||| ",
            "|||||| ||| | | |   | || ||/+++++--++++-++-++-++-++--+---+++---+--+-+++++--+-+-++-++\\     ||||  | ||| |  |   | || |   ||  | |||| \\-++-++--++/||||| ||| ",
            "|||||| ||| | | |   | || ||||||||/-++++-++-++-++-++--+---+++---+--+-+++++--+-+-++-+++-----++++--+-+++-+--+---+-++-+---++--+-++++---++-++\\ || ||||| ||| ",
            "|||||| ||| | | |   | || ||||||||| |||| || || || ||  |   |||   |  | |||||  | | || |||     ||||  | ||| |  |   | || |   ||  | ||||   || ||| || ||||| ||| ",
            "|||||| ||| | | |   | || |||||\\+++-++++-++-++-++-++--+---+++---+--+-+++++--+-+-/| |||     ||||  | ||| |  |   | || |   ||  | ||||   || ||| || ||||v ||| ",
            "||||\\+-+++-+-+-+---+-++-+++++-+++-++++-++-++-+/ || /+---+++---+--+-+++++--+-+--+-+++-----++++--+-+++-+--+---+-++-+---++--+-++++--\\|| ||| || ||||| ||| ",
            "|||| | ||| | | |   | || ||||| \\++-++++-++-++-+--++-++---+++---+--+-+++++--+-+--+-+++-----++++--+-+++-+--+---+-++-+---++--+-++/|  ||| ||| || ||||| ||| ",
            "|||| |/+++-+-+-+---+-++-+++++--++-++++\\|| || |  || ||   |||   |/-+-+++++--+-+--+-+++-----++++--+-+++-+--+---+-++-+---++--+-++-+--+++-+++-++-+++++\\||| ",
            "|||| ||||| | | | /-+-++-+++++--++-+++++++-++-+--++-++---+++---++-+-+++++--+-+--+-+++-----++++-\\| |||/+--+---+-++-+---++--+\\|| |  ||| ||| || ||||||||| ",
            "|||| ||||| | | | | |/++-+++++--++-+++++++-++-+-\\|| || /-+++---++-+-+++++--+-+--+-+++-----++++-++-+++++--+---+-++-+---++--++++-+--+++-+++-++-+++++++++\\",
            "|||| ||||| | | | | |||| |||||  || |||||||/++-+-+++-++-+-+++---++-+-+++++--+-+--+-+++-----++++-++-+++++--+---+-++-+---++--++++-+--+++\\||| || ||||||||||",
            "|||| ||||| | | | | |||| |||||  || |||||||||| | ||| || | |||   || | |||||  | |  | |||     |||\\-++-+++++--+---+-++-+---++--++++-+--+++++++-++-+++/||||||",
            "|||| ||||| | | ^ | |||| |||||  || |||||||||| | ||| || | |||   || | |||||  | |  | |||     |||  || |||||  |   | || |   ||  |||| |  ||||||| || ||| ||||||",
            "|||| ||||| | | | | |||| |||||  || |||||||||\\-+-+++-++-+-+++---++-+-+++++--+-+--+-+++-----+++--++-+++++--+---+-++-+---++--++/| |  |||||\\+-++-+++-/|||||",
            "|||| ||||| | | | | |||| |||||  || |||||||||  | ||| || | |||   || | |||||  | |  | |||     |||  || |||||  |   | || |   ||  || | |  ||||| | || |||  |||||",
            "|||| ||||| | | | | ||\\+-+++++--++-+++++++++--+-+++-++-+-+++---++-+-+++++--+-+--+-+++-----+++--++-++++/  |   | || |   ||  || | |  ||||| | || |||  |||||",
            "|||| ||||| | | | | || | ||\\++--++-+++++++++--+-+++-++-+-+++---++-+-+++++--+-+--+-++/     |||  || ||||   |   | || |   ||  || | |  ||||| | || |||  |||||",
            "|||| ||||| | | | | || | || ||  || |||||||||  | ||| || | |||   || | |||||  |/+--+-++------+++--++-++++---+-\\ | || |   ||  || | |  ||||| | || |||  |||||",
            "|||| ||||| | | | | || | |\\-++--++-+++++++++--+-+++-++-+-+++---++-+-+++++--+++--+-++------+++--++-++++---+-+-+-+/ |   ||  || | | /+++++-+-++\\|||  |||||",
            "|||| ||||| | | | | || | |  ||  || |||||||||  | ||| || | |||   |\\-+-+++++--+++--+-++------+++--++-++++---+-+-+-+--+---++--++-+-+-++++++-+-++++++--/||||",
            "|||| ||||| | | | | \\+-+-+--++--++-+++++++++--+-+++-++-+-+++---/  | |||||  |||  | ||      |||  || ||||   | | | |  |   ||  || | | |||||| |/++++++--\\||||",
            "|||| |\\+++-+-+-+-+--+-+-+--++--++-++++/||||  | ||| || | |||      | |||||  |||  | ||      |||  || ||||   | | | |  |   ||  || | | |||||| ||||||||  |||||",
            "|||| | ||| | | | |  | | |/-++--++-++++-++++--+-+++-++-+-+++------+-+++++--+++--+-++-\\    |||  ||/++++---+-+-+-+--+---++--++-+-+-++++++\\||||||||  |||||",
            "||||/+-+++-+-+-+-+--+-+-++-++--++-++++-++++--+-+++-++-+-+++------+-+++++--+++--+-++-+----+++--+++++++---+-+-+-+-\\|   ||  v| | | |||||||||||||||  |||||",
            "|||||| ||| | | | |  | | || ||  || |||| ||||  | ||| || | |||      | |||||  |||  | || |    |||  |||||||   | | | | ||   ||  || | | |||||||||||||||  |||||",
            "|||||| ||| | | | |  | | || ||  \\+-++++-++++--+-+++-++-+-+++------+-+++++--+++--+-+/ |    |||  |||||||   | | | | ||   ||  || | | |||||||||||||||  |||||",
            "|||||| ||| | | | |  | | || ||  /+-++++-++++-\\| ||| || |/+++------+-+++++--+++--+-+--+----+++--+++++++---+-+-+-+-++---++\\ || | | |||||||||||||||  |||||",
            "|||||| ||| | | | |  | | || ||  || |||| |||| || ||| || |||\\+------+-+++++--+++--+-/  |    |||  |||||||   | | | | ||   ||| || | | ||||^||||||||||  |||||",
            "|||||| ||| | | | |  | | || ||  || \\+++-++++-++-+++-++-+++-+------+-+++++--+++--+----+----+++--+/|||||   | | | | ||   ||| || | | |||||||||||||||  |||||",
            "|||||| ||| | | | |  | | || ||  ||  ||| |||| || ||| || ||| |      | |||||  |||  |    |    |||  | |||||   | | | | ||   ||| || | | |||||||||||||||  |||||",
            "|||||| |\\+>+-+-+-+--+-+-++<++--++--+++-++++-++-+++-++-+++-+------+-++/||  |||  |    |    |||  | |||||   | | | | ||   ||| || | | |||||||||||||||  |||||",
            "||||||/+-+-+-+-+-+--+-+-++-++--++--+++-++++-++-+++-++-+++-+------+-++-++--+++--+----+----+++--+-+++++---+-+-+-+\\||   ||| || | | |||||||||||||||  |||||",
            "|||||||| | | | | |  | | \\+-++--++--+++-++++-++-+++-++-+++-+------+-++-++--+++--+----+----+++--+-+++++---+-+-+-++++---+++-/| | | |||||||||||||||  |||||",
            "|||||||| | | | | \\--+-+--+-++--++--+++-++++-++-+++-++-+++-+------+-++-++--+++--+----+----+++--/ |||||   | | | ||||   |||  | | | \\++++++++++/|||  |||||",
            "|||||||| | | | |    | |  | ||  ||  ||| |||| || ||| || ||| |      | || ||  |||  |    |    |||    |||||   | | | ||||   |||  | | |  |||||||||| |||  |||||",
            "|||||||| | | | |   /+-+--+-++--++--+++-++++-++-+++-++-+++-+------+-++-++--+++--+----+----+++----+++++--\\| | | ||||   |||  | | |  |||||||||| |||  |||||",
            "|||||||| | | | |   || |  | ||  ||  ||| |||| || ||| ||/+++-+------+-++-++--+++--+----+\\   |||    |||||  || | | ||||   |||  | | |  |||||||||| |||  |||||",
            "|||||||| | | | |   || |  | ||  ||  ||| |||| || ||| |||||| |      | || ||  |||  |    ||   |||    |||||  || | | ||||   |||  | | |  |||||||||| |||  |||||",
            "|||||||| | | | |   || |  | ||  ||  ||| |||| || ||| |||||| |      | |\\-++--+++--+----++---+++----+++++--++-+-+-++++---+/|  | | |  |||||||||| |||  |||||",
            "|||||||| | | | |   || |  | ||  ||  ||| |||| || ||| |||||| |      | |  ||  |||  |    ||   |||    |||||  || | | ||||   | |  | | |  |||||||\\++-+++--/||||",
            "|||||||| | | | |   || |  | ||  ||  ||| |||| || ||| |||||\\-+------+-+--++--+++--+----++---+++----+++++--++-+-+-++++---/ |  | | |  ||\\++++-++-++/   ||||",
            "|||||||| | | | |   || |  | ||  ||  ||| |||| |\\-+++-+++++--+------+-+--++--+++--+----++---+++----+++++--++-+-+-++++-----+--+-+-+--++-++++-/| ||    ||||",
            "||||\\+++-+-+-+-+---++-+--+-++--++--+++-++++-+--+++-+++++--+------+-+--++--+++--+----++---+++----+++++--++-+-+-++/|     |  | | |  || ||||  | ||    ||||",
            "|||| ||| | | | |   || \\--+-++--++-<+++-+++/ |  ||| |||||  |      | |  ||  ||\\--+----++---+++----+++++--++-+-+-++-+-----+--+-+-/  || ||||  | ||    ||||",
            "|||| ||| | | | |   ||    |/++--++--+++\\|||  |  ||\\-+++++--+------+-+--++--++---+----++---+++----+++++--++-+-+-++-+-----+--+-+----++-++++--+-+/    ||||",
            "|||| ||| | | | |   ||    |||\\--++--+++++++--+--++--+/|||  \\------+-+--++--++---+----++---+++----+++++--++-+-+-++-+-----+--+-+----++-++++--+-+-----++/|",
            "|||| ||| | | | |   ||    |||   ||  |||||\\+--+--++--+-+++---------+-+--++--++---+----++---+++----+++++--++-+-+-/| |     |  | |    || ||||  | |     || |",
            "|||| \\++-+-+-+-+---++----++/   ||  ||||| |  |  ||  | |||         | |  ||  ||   |    ||   |||  /-+++++--++-+-+--+-+-----+--+-+----++-++++-\\| |     || |",
            "||||  || | | | |   ||    ||    ||  ||||| |  |  ||  | |||         \\-+--++--++---+----++---+++--+-++/||  || | |/-+-+-----+--+-+--\\ || |||| || |     || |",
            "||||  || | | | |   ||    ||    ||  ||||| |  |  ||  | \\++-----------+--++--++---+----+/   |||  | || \\+--++-+-++-+-+-----+--+-+--+-++-++++-++-+-----/| |",
            "||||  || \\-+-+-+---++----++----++--+++++-+--+--++--+--++-----------+--++--++---+--->+----+++--+-+/  |  || | || | |     |  | |  | || |||| || |      | |",
            "||||  ||   | | |   ||    ||    ||  ||||\\-+--+--++--+--++-----------+--++--++---+----+----+++--+-+---+--++-+-++-+-+-----+--+-+--+-++-++++-+/ |      | |",
            "||||  |\\---+-+-+---++----++----++--++++--+--+--++--+--++-----------+--+/  ||   |    |    |||  | |   |  || | || | |     |  | |  | || |||| |  |      | |",
            "||||  |    | | |   \\+----++----++--++++--+--+--++--+--++-----------+--+---++---+----+----+++--+-+---+--/| | || | |     |  | |  | || |||| |  |      | |",
            "||||  |    | | |    |    ||    ||  ||||  |  |  ||  |  ||           |  |   \\+---+<---+----+++--+-+---+---+-+-++-+-+-----+--+-+--+-++-++++-+--/      | |",
            "||||  |    | | | /--+----++----++\\ ||||  |  |  ||  |  ||           |  |    |   |    |    |||  | |   |   | | || | |     |  | |  | || |||| |         | |",
            "||||  |    | | | |  |    ||    |\\+-++++--+--+--++--+--++-----------+--+----+---+----+----+++--+-+---+---+-+-++-+-+-----+--+-+--+-++-+++/ |         | |",
            "|\\++--+----+-+-+-+--+----++----+-+-++++--+--+--++--+--++-----------+--+----+---+----+----+++--+-+---+---+-+-/| | |     |  | |  | || |||  |         | |",
            "| \\+--+----+-+-+-+--+----++----+-+-++++--+--+--++--+--++-----------+--/    |   |    |    |||  | |   |   | |  | | |     |  | v  | || |||  |         | |",
            "|  |  |    | | | |  |    ||    | | \\+++--+--+--++--+--++-----------+-------+---+----+----++/  | |   |/--+-+--+-+-+---\\ |  | |  | || |||  |         | |",
            "|  |  |    | | | |  |    ||    | |  |||  |  |  ||  |  |\\-----------+-------+---+----+----++---+-+---++--+-+--+-+-+---+-/  | |  | || |||  |         | |",
            "|  |  |    | | | |  |    ||    | |  |||  |  |  ||  |  |            | /-----+---+----+----++-\\ | |   ||  \\-+--+-+-+---+----+-/  | || |||  |         | |",
            "|  |  |    | | | |  |    ||    | |  |||  |  |  ||  |  |            | |    /+---+----+----++-+-+-+---++----+--+-+-+---+----+----+\\|| |||  |         | |",
            "|  |  |    | | | |  |    || /--+-+--+++--+--+--++--+--+\\           | |    ||   |   /+----++-+-+-+-\\ ||    |  | | |   |    |    |||| |||  |         | |",
            "|  |  |    | | | |  |    || |  | |  |||  |  |  ||  |  \\+-----------+-+----++---+---++----++-+-+-+-+-++----+--+-+-+---+----+----++++-+++--+---------+-/",
            "|  |  |    | | | |  |    || |  | |  |||  |  |  ||  |   |           | |    ||   |   ||    ||/+-+-+-+-++----+--+-+-+---+----+----++++-+++--+--------\\|  ",
            "\\--+--+----+-+-+-+--+----++-+--+-+--+++--+--+--++--+---+-----------+-+----++---+---++----/||| | | | ||    |  | | \\---+----+----+++/ |||  |        ||  ",
            "   |  |    | | | \\--+----++-+--+-/  |||  \\--+--++--+---+-----------+-+----++---+---++-----+++-+-+-+-++----+--+-+-----+----+----+++--/||  |        ||  ",
            "   |  |    | | |    |    || |  |    |||     |  ||  |   |           | |    ||   |   ||     ||| | | | ||    |  \\-+-----+----+----/||   ||  |        ||  ",
            "   |  |    | | \\----+----++-+--+----/||     |  ||  \\---+-----------+-+----++---+---++-----+++-+-+-+-++----+----+-----+----+>----+/   ||  |        ||  ",
            "   \\--+----+-+------+----++-+--+-----++-----+--++------+-----------+-+----++---+---++-----/|| | | | ||    |    |     |    |     |    ||  |        ||  ",
            "      |    | |      |    || |  |     ||     |  ||      |           | |    ||   |   ||      || | | | ||    |    |     |    |     |    ||  |        ||  ",
            "      |    \\-+------+----++-+--+-----++-----+--++------+-----------+-+----++---/   \\+------++-+-+-/ \\+----+----+-----+----/     |    ||  |        ||  ",
            "      |      |      |    || |  |     ||     |  ||      |           | |    ||        |      || | |    |    |    |     |          |    ||  |        ||  ",
            "      |      |      |    || |  |     ||     |  ||      |           | |    ||        |      || | \\----+----+----+-----+----------+----+/  |        ||  ",
            "      |      |      |    || |  |     ||     |  ||      |      /----+-+--\\ ||        |      || |      |    |    |     |          |    |   |        ||  ",
            "      |      |      |    || |  \\-----++-----/  ||      |      |    | |  | ||        |      \\+-+------+----+----+-----+----------+----+---+--------/|  ",
            "      |      |      |    || |        ||        ||      |      |    | |  | ||        |       | |      \\----+----+-----/          |    |   |         |  ",
            "      |      |      |    |\\-+--------+/        ||      |      |    | \\--+-++--------+-------/ |           |    |                |    |   |         |  ",
            "      |      |      |    |  \\--------+---------++------/      |    |    | \\+--------+---------+-----------+----+----------------/    |   |         |  ",
            "      |      |      |    |           |         ||             |    |    |  |        |         |           |    |                     \\---+---------/  ",
            "      |      |      |    \\-----------+---------++-------------+----+----+--+--------/         \\-----------+----+-------------------------/            ",
            "      |      \\------+----------------+---------+/             |    |    |  \\------------------------------/    |                                      ",
            "      |             |                \\---------+--------------+----/    |                                      |                                      ",
            "      \\-------------+--------------------------+--------------+---------+--------------------------------------/                                      ",
            "                    \\--------------------------/              \\---------/                                                                             "
    };
}

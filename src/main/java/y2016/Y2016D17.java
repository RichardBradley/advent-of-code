package y2016;

import com.google.common.base.Stopwatch;
import lombok.Value;
import org.apache.commons.codec.binary.Hex;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.truth.Truth.assertThat;
import static java.util.Collections.singletonList;

public class Y2016D17 {

    private static MessageDigest md5;

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        md5 = MessageDigest.getInstance("MD5");

        // 1
        assertThat(new SearchState("", new Point(0, 0)).possibleMoves("hijkl")).isEqualTo(singletonList(new SearchState("D", new Point(0, 1))));
        assertThat(getMinPath("ihgpwlah")).isEqualTo("DDRRRD");
        assertThat(getMinPath("kglvqrro")).isEqualTo("DDUDRLRRUDRD");
        assertThat(getMinPath("ulqzkmiv")).isEqualTo("DRURDRUDDLLDLUURRDULRLDUUDDDRR");
        System.out.println("example ok");

        System.out.println(getMinPath("bwnlcvfs"));

        // 2
        assertThat(getMaxPath("ihgpwlah")).isEqualTo(370);
        assertThat(getMaxPath("ulqzkmiv")).isEqualTo(830);

        System.out.println(getMaxPath("bwnlcvfs"));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    static final int WIDTH = 4;
    static final int HEIGHT = 4;
    static final Point TARGET = new Point(WIDTH - 1, HEIGHT - 1);

    @Value
    static class SearchState implements Comparable<SearchState> {
        String movesToHere;
        Point location;
        private final int searchHeuristicCost;

        public SearchState(String movesToHere, Point location) {
            this.movesToHere = movesToHere;
            this.location = location;

            this.searchHeuristicCost = movesToHere.length() + minMovesToFinish(location, TARGET);
        }

        @Override
        public int compareTo(SearchState that) {
            return Integer.compare(this.searchHeuristicCost, that.searchHeuristicCost);
        }

        private static int minMovesToFinish(Point location, Point target) {
            // To be admissable, must not over-estimate the dist to finish
            return Math.abs(location.x - target.x) + Math.abs(location.y - target.y);
        }

        public List<SearchState> possibleMoves(String mapSeed) {
            String hash = Hex.encodeHexString(md5.digest((mapSeed + movesToHere).getBytes(StandardCharsets.UTF_8)));

            List<SearchState> acc = new ArrayList<>();
            if (location.y > 0) {
                Point next = new Point(location.x, location.y - 1);
                if ('b' <= hash.charAt(0) && 'f' >= hash.charAt(0)) {
                    acc.add(new SearchState(movesToHere + "U", next));
                }
            }
            if (location.y < HEIGHT - 1) {
                Point next = new Point(location.x, location.y + 1);
                if ('b' <= hash.charAt(1) && 'f' >= hash.charAt(1)) {
                    acc.add(new SearchState(movesToHere + "D", next));
                }
            }
            if (location.x > 0) {
                Point next = new Point(location.x - 1, location.y);
                if ('b' <= hash.charAt(2) && 'f' >= hash.charAt(2)) {
                    acc.add(new SearchState(movesToHere + "L", next));
                }
            }
            if (location.x < WIDTH - 1) {
                Point next = new Point(location.x + 1, location.y);
                if ('b' <= hash.charAt(3) && 'f' >= hash.charAt(3)) {
                    acc.add(new SearchState(movesToHere + "R", next));
                }
            }

            return acc;
        }
    }

    private static String getMinPath(String mapSeed) {

        long lastReportTimeMillis = System.currentTimeMillis();

        // A* search:
        // Heuristic is min number of moves to finish (ignoring open/closed doors). This is "admissable"
        // No need to track visited states, as all moves create a new state

        PriorityQueue<SearchState> queue = new PriorityQueue<>();
        queue.add(new SearchState("", new Point(0, 0)));

        while (true) {
            SearchState state = queue.poll();
            checkNotNull(state);

            if (state.location.equals(TARGET)) {
                return state.movesToHere;
            }

            queue.addAll(state.possibleMoves(mapSeed));

            if (System.currentTimeMillis() - lastReportTimeMillis > 10000) {
                lastReportTimeMillis = System.currentTimeMillis();
                System.out.println("Queue len = " + queue.size() + " Current node = " + state);
            }
        }
    }

    private static int getMaxPath(String mapSeed) {
        long lastReportTimeMillis = System.currentTimeMillis();
        int maxLength = 0;
        Queue<SearchState> queue = new ArrayDeque<>();
        queue.add(new SearchState("", new Point(0, 0)));

        SearchState state;
        while (null != (state = queue.poll())) {

            if (state.location.equals(TARGET)) {
                if (maxLength < state.movesToHere.length()) {
                    maxLength = state.movesToHere.length();
                }
            } else {
                queue.addAll(state.possibleMoves(mapSeed));
            }

            if (System.currentTimeMillis() - lastReportTimeMillis > 10000) {
                lastReportTimeMillis = System.currentTimeMillis();
                System.out.println("Queue len = " + queue.size() + " Current node = " + state);
            }
        }

        return maxLength;
    }

}

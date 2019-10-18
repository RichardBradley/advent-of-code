package y2016;

import com.google.common.base.Stopwatch;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2016D13 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(minStepsTo(new Point(1, 1), new Point(7, 4), 10)).isEqualTo(11);
        System.out.println("example ok");

        System.out.println(minStepsTo(new Point(1, 1), new Point(31, 39), 1362));

        // 2
        System.out.println(countReachable(new Point(1, 1), 50, 1362));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static int countReachable(Point start, int maxSteps, int mapSeed) {
        Set<Point> reached = new HashSet<>();
        Maze maze = new Maze(mapSeed);
        reached.add(start);
        Queue<SearchState> openSet = new ArrayDeque<>();
        openSet.add(new SearchState(0, start, start));
        SearchState curr;
        while (null != (curr = openSet.poll())) {
            if (curr.movesToHere < maxSteps) {
                for (SearchState next : curr.possibleMoves(maze)) {
                    if (reached.add(next.location)) {
                        openSet.add(next);
                    }
                }
            }
        }
        return reached.size();
    }

    @Value
    static class SearchState implements Comparable<SearchState> {
        int movesToHere;
        Point location;
        private final int searchHeuristicCost;
        private final Point target;

        public SearchState(int movesToHere, Point location, Point target) {
            this.movesToHere = movesToHere;
            this.location = location;
            this.target = target;

            this.searchHeuristicCost = movesToHere + minMovesToFinish(location, target);
        }

        @Override
        public int compareTo(SearchState that) {
            return Integer.compare(this.searchHeuristicCost, that.searchHeuristicCost);
        }

        private static int minMovesToFinish(Point location, Point target) {
            // To be admissable, must not over-estimate the dist to finish
            return Math.abs(location.x - target.x) + Math.abs(location.y - target.y);
        }

        public List<SearchState> possibleMoves(Maze maze) {
            List<SearchState> acc = new ArrayList<>();
            if (location.x > 0) {
                Point next = new Point(location.x - 1, location.y);
                if (maze.isOpen(next)) {
                    acc.add(new SearchState(movesToHere + 1, next, target));
                }
            }
            if (location.y > 0) {
                Point next = new Point(location.x, location.y - 1);
                if (maze.isOpen(next)) {
                    acc.add(new SearchState(movesToHere + 1, next, target));
                }
            }
            {
                Point next = new Point(location.x, location.y + 1);
                if (maze.isOpen(next)) {
                    acc.add(new SearchState(movesToHere + 1, next, target));
                }
            }
            {
                Point next = new Point(location.x + 1, location.y);
                if (maze.isOpen(next)) {
                    acc.add(new SearchState(movesToHere + 1, next, target));
                }
            }

            return acc;
        }
    }

    @AllArgsConstructor
    static class Maze {
        private int mazeSeed;
        private final Map<Point, Boolean> isOpen = new HashMap<>();

        public boolean isOpen(Point point) {
            return isOpen.computeIfAbsent(point, p -> {

                // Find x*x + 3*x + 2*x*y + y + y*y.
                //Add the office designer's favorite number (your puzzle input).
                //Find the binary representation of that sum; count the number of bits that are 1.
                //If the number of bits that are 1 is even, it's an open space.
                //If the number of bits that are 1 is odd, it's a wall.
                long v = p.x * (long) p.x + 3L * p.x + 2 * p.x * (long) p.y + p.y + p.y * (long) p.y;
                v += mazeSeed;
                long bitCount = Long.toBinaryString(v).chars().filter(c -> c == '1').count();
                return (bitCount % 2) == 0;
            });
        }
    }

    static int minStepsTo(Point start, Point destination, int mapSeed) {

        long lastReportTimeMillis = System.currentTimeMillis();

        // A* search:
        // Heuristic is min number of moves to finish (ignoring open/closed space). This is "admissable"
        PriorityQueue<SearchState> queue = new PriorityQueue<>();
        Map<Point, Integer> minMovesToReachStates = new HashMap<>();
        queue.add(new SearchState(0, start, destination));
        Maze maze = new Maze(mapSeed);

        while (true) {
            SearchState state = queue.poll();

            if (state.location.equals(destination)) {
                Integer prevEntry = minMovesToReachStates.get(state);
                checkState(prevEntry == null || prevEntry >= state.movesToHere);
                return state.movesToHere;
            }

            minMovesToReachStates.compute(state.location, (k, oldDist) -> {

                if (oldDist == null || oldDist > state.movesToHere) {
                    int nextMoveCount = state.movesToHere + 1;
                    for (SearchState nextState : state.possibleMoves(maze)) {
                        Integer nextStatePrevMoves = minMovesToReachStates.get(nextState);
                        if (nextStatePrevMoves == null || nextStatePrevMoves > nextMoveCount) {
                            queue.add(nextState);
                        }
                    }

                    return state.movesToHere;
                } else {
                    // already reached
                    return oldDist;
                }
            });

            if (System.currentTimeMillis() - lastReportTimeMillis > 10000) {
                lastReportTimeMillis = System.currentTimeMillis();
                System.out.println("Queue len = " + queue.size() + " Current node = " + state);
            }
        }
    }
}

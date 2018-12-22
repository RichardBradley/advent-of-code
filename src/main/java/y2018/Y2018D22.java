package y2018;

import com.google.common.base.Stopwatch;
import lombok.Value;
import lombok.experimental.Wither;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

public class Y2018D22 {
    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        // 1

        assertThat(getRisk(510, new Point(10, 10))).isEqualTo(114);

        System.out.println(getRisk(11739, new Point(11, 718)));

        // 2

        assertThat(getFastestRouteDuration(510, new Point(10, 10))).isEqualTo(45);

        System.out.println(getFastestRouteDuration(11739, new Point(11, 718)));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static int getRisk(int depth, Point targetLoc) {
        int[][] erosionLevels = alloc(targetLoc);
        int risk = 0;
        for (int y = 0; y <= targetLoc.y; y++) {
            for (int x = 0; x <= targetLoc.x; x++) {
                risk += (getErosionLevel(depth, targetLoc, x, y, erosionLevels) % 3);
            }
        }
        return risk;
    }

    private static int getErosionLevel(int depth, Point targetLoc, int x, int y, int[][] erosionLevels) {
        int erosionLevel = erosionLevels[y][x];
        if (erosionLevel >= 0) {
            // already computed:
            return erosionLevel;
        }
        int geologicIndex;
        if (x == 0 && y == 0) {
            geologicIndex = 0;
        } else if (x == targetLoc.x && y == targetLoc.y) {
            geologicIndex = 0;
        } else if (y == 0) {
            geologicIndex = x * 16807;
        } else if (x == 0) {
            geologicIndex = y * 48271;
        } else {
            geologicIndex = getErosionLevel(depth, targetLoc, x - 1, y, erosionLevels)
                    * getErosionLevel(depth, targetLoc, x, y - 1, erosionLevels);
        }
        erosionLevel = (geologicIndex + depth) % 20183;
        erosionLevels[y][x] = erosionLevel;
        return erosionLevel;
    }

    private static int getFastestRouteDuration(int depth, Point targetLoc) {
        int[][] erosionLevels = alloc(targetLoc);

        Map<RouteState, Integer> fastestRouteDurations = new HashMap<>();
        RouteState start = new RouteState(ToolChoice.Torch, 0, 0);
        PriorityQueue<TimeAndState> openSet = new PriorityQueue<>();
        openSet.add(new TimeAndState(0, start));

        while (true) {
            TimeAndState nextNodeAndTime = openSet.poll();
            int newTime = nextNodeAndTime.time;
            RouteState nextNode = nextNodeAndTime.state;

            int currentTime = fastestRouteDurations.getOrDefault(nextNode, Integer.MAX_VALUE);
            if (newTime < currentTime) {
                fastestRouteDurations.put(nextNode, newTime);

                if (nextNode.x == targetLoc.x && nextNode.y == targetLoc.y
                        && nextNode.tool == ToolChoice.Torch) {
                    return newTime;
                }

                openSet.addAll(getNeighbours(nextNode, newTime, depth, targetLoc, erosionLevels));
            }
        }
    }

    private static Collection<TimeAndState> getNeighbours(
            RouteState currentState, int currentTime,
            int depth, Point targetLoc, int[][] erosionLevels) {
        List<TimeAndState> acc = new ArrayList<>();
        // You can move to an adjacent region (up, down, left, or right;
        // never diagonally) if your currently equipped tool allows you to
        // enter that region. Moving to an adjacent region takes one minute.
        for (RouteState move : new RouteState[]{
                currentState.withX(currentState.x - 1),
                currentState.withX(currentState.x + 1),
                currentState.withY(currentState.y - 1),
                currentState.withY(currentState.y + 1)
        }) {
            if (toolIsValid(move, depth, targetLoc, erosionLevels)) {
                acc.add(new TimeAndState(
                        currentTime + 1,
                        move));
            }
        }
        for (ToolChoice newTool : ToolChoice.values()) {
            if (newTool != currentState.tool) {
                RouteState toolChange = currentState.withTool(newTool);
                if (toolIsValid(toolChange, depth, targetLoc, erosionLevels)) {
                    acc.add(new TimeAndState(
                            currentTime + 7,
                            toolChange));
                }
            }
        }
        return acc;
    }

    private static boolean toolIsValid(
            RouteState routeState,
            int depth, Point targetLoc, int[][] erosionLevels) {

        if (routeState.x < 0 || routeState.y < 0) {
            return false;
        }

        switch (getErosionLevel(depth, targetLoc, routeState.x, routeState.y, erosionLevels) % 3) {
            case 0:
                return routeState.tool == ToolChoice.ClimbingGear
                        || routeState.tool == ToolChoice.Torch;
            case 1:
                return routeState.tool == ToolChoice.ClimbingGear
                        || routeState.tool == ToolChoice.Neither;
            case 2:
                return routeState.tool == ToolChoice.Torch
                        || routeState.tool == ToolChoice.Neither;
            default:
                throw new IllegalStateException();
        }
    }

    private static int[][] alloc(Point targetLoc) {
        int height = targetLoc.y + 1000;
        int[][] acc = new int[height][];
        for (int y = 0; y < height; y++) {
            acc[y] = new int[targetLoc.x + 1000];
            Arrays.fill(acc[y], -1);
        }
        return acc;
    }

    @Value
    static class RouteState {
        @Wither
        ToolChoice tool;
        @Wither
        int x;
        @Wither
        int y;
    }

    @Value
    static class TimeAndState implements Comparable<TimeAndState> {
        int time;
        RouteState state;

        @Override
        public int compareTo(TimeAndState o) {
            return Integer.compare(this.time, o.time);
        }
    }

    enum ToolChoice {
        Torch,
        ClimbingGear,
        Neither;
    }
}
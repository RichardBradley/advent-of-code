package y2020;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;
import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;


public class Y2020D20 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        String example = Resources.toString(Resources.getResource("y2020/Y2020D20-example.txt"), StandardCharsets.UTF_8);
        String input = Resources.toString(Resources.getResource("y2020/Y2020D20.txt"), StandardCharsets.UTF_8);

        run(example, 20899048083289L, 273);
        run(input, 18262194216271L, 2023);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    static int TILE_SIZE = 10;
    static int NORTH = 0;
    static int EAST = 1;
    static int SOUTH = 2;
    static int WEST = 3;

    private static void run(String input, long part1Answer, long part2Answer) {
        List<Tile> tiles = Splitter.on("\n\n").splitToList(input).stream()
                .map(Y2020D20::parseTile)
                .collect(Collectors.toList());

        int gridSize = (int) Math.sqrt(tiles.size());
        checkState(gridSize * gridSize == tiles.size());

        Tile[][] grid = new Tile[gridSize][gridSize];

        // depth first fill:
        checkState(fill(grid, tiles, 0, 0));
        long part1 = grid[0][0].tileId * grid[gridSize - 1][0].tileId
                * grid[0][gridSize - 1].tileId * grid[gridSize - 1][gridSize - 1].tileId;
        assertThat(part1).isEqualTo(part1Answer);

        String[] pic = buildPic(grid);

        String[] monster = new String[]{
                "                  # ",
                "#    ##    ##    ###",
                " #  #  #  #  #  #   "
        };

        for (int flip = 0; flip < 2; flip++) {
            for (int rotate = 0; rotate < 4; rotate++) {
                int monsterCount = getMonsterCount(pic, monster);
                if (monsterCount != 0) {
                    int pixelsNotInMonsterCount = countHashes(pic) - monsterCount * countHashes(monster);
                    assertThat(pixelsNotInMonsterCount).isEqualTo(part2Answer);
                }

                pic = rotate(pic);
            }
            pic = flip(pic);
        }
    }

    private static int countHashes(String[] pic) {
        int acc = 0;
        for (String row : pic) {
            for (int i = 0; i < row.length(); i++) {
                if ('#' == row.charAt(i)) {
                    acc++;
                }
            }
        }
        return acc;
    }

    private static String[] flip(String[] pic) {
        for (int i = 0; i < pic.length; i++) {
            pic[i] = reverse(pic[i]);
        }
        return pic;
    }

    private static String[] rotate(String[] pic) {
        int len = pic.length;
        String[] rotated = new String[len];
        for (int y = 0; y < len; y++) {
            StringBuilder row = new StringBuilder();
            for (int x = 0; x < len; x++) {
                row.append(pic[x].charAt(len - y - 1));
            }
            rotated[y] = row.toString();
        }
        return rotated;
    }

    private static int getMonsterCount(String[] pic, String[] monster) {
        int monsterCount = 0;
        for (int y = 0; y < 1 + pic.length - monster.length; y++) {
            outer:
            for (int x = 0; x < 1 + pic[0].length() - monster[0].length(); x++) {
                for (int dy = 0; dy < monster.length; dy++) {
                    for (int dx = 0; dx < monster[0].length(); dx++) {
                        if (monster[dy].charAt(dx) == '#' && pic[y + dy].charAt(x + dx) != '#') {
                            continue outer;
                        }
                    }
                }
                monsterCount++;
            }
        }
        return monsterCount;
    }

    private static String[] buildPic(Tile[][] grid) {
        for (Tile[] tiles : grid) {
            for (Tile tile : tiles) {
                if (tile.placedFlipped) {
                    tile.lines = flip(tile.lines);
                }
                for (int i = 0; i < tile.placedOrientation; i++) {
                    tile.lines = rotate(tile.lines);
                }
            }
        }

        List<String> acc = new ArrayList<>();
        for (int gridY = 0; gridY < grid.length; gridY++) {
            Tile[] gridRow = grid[gridY];
            for (int tileY = 1; tileY < TILE_SIZE - 1; tileY++) {
                StringBuilder picRow = new StringBuilder();
                for (int gridX = 0; gridX < gridRow.length; gridX++) {
                    picRow.append(
                            gridRow[gridX].lines[tileY],
                            1,
                            TILE_SIZE - 1);
                }
                acc.add(picRow.toString());
            }
        }
        return acc.toArray(new String[0]);
    }

    private static boolean fill(Tile[][] grid, List<Tile> tiles, int x, int y) {
        boolean isLast = false;
        int nextY = y;
        int nextX = x + 1;
        if (nextX >= grid.length) {
            nextX = 0;
            nextY++;
            if (nextY >= grid.length) {
                isLast = true;
            }
        }

        Collections.shuffle(tiles);

        for (Tile tile : tiles) {
            ArrayList<Tile> remaining = new ArrayList<>(tiles);
            remaining.remove(tile);

            for (boolean flipped : new boolean[]{true, false}) {
                tile.setPlacedFlipped(flipped);
                for (int orientation = 0; orientation < 4; orientation++) {
                    tile.setPlacedOrientation(orientation);
                    if (canFit(tile, grid, x, y)) {
                        grid[y][x] = tile;
                        if (isLast || fill(grid, remaining, nextX, nextY)) {
                            return true;
                        } else {
                            grid[y][x] = null;
                        }
                    }
                }
            }
        }
        return false;
    }

    private static boolean canFit(Tile tile, Tile[][] grid, int x, int y) {
        int gridSize = grid.length;
        if (x > 0) {
            // check W neighbour
            Tile nieghbour = grid[y][x - 1];
            if (nieghbour != null) {
                if (!equalsReversed(nieghbour.getEdge(EAST), tile.getEdge(WEST))) {
                    return false;
                }
            }
        }
        if (x < gridSize - 1) {
            Tile nieghbour = grid[y][x + 1];
            if (nieghbour != null) {
                if (!equalsReversed(nieghbour.getEdge(WEST), tile.getEdge(EAST))) {
                    return false;
                }
            }
        }
        if (y > 0) {
            // check N neighbour
            Tile nieghbour = grid[y - 1][x];
            if (nieghbour != null) {
                if (!equalsReversed(nieghbour.getEdge(SOUTH), tile.getEdge(NORTH))) {
                    return false;
                }
            }
        }
        if (y < gridSize - 1) {
            Tile nieghbour = grid[y + 1][x];
            if (nieghbour != null) {
                if (!equalsReversed(nieghbour.getEdge(NORTH), tile.getEdge(SOUTH))) {
                    return false;
                }
            }
        }
        return true;
    }

    private static Tile parseTile(String spec) {
        String[] headerBody = spec.split(":\n");
        checkState(headerBody.length == 2);
        String header = headerBody[0];
        String[] lines = headerBody[1].split("\n");
        Pattern p = Pattern.compile("Tile (\\d+)");
        Matcher m = p.matcher(header);
        checkState(m.matches());
        long tileId = Long.parseLong(m.group(1));

        checkState(lines[0].length() == lines.length);
        return new Tile(tileId, lines);
    }

    @Data
    private static class Tile {
        final long tileId;
        String[] lines;
        final String[] edges; // NESW indexed, read clockwise
        final String[] flippedEdges;

        int placedOrientation; // rotation anti-clockwise 0-3
        boolean placedFlipped;

        String getEdge(int nesw) {
            int idx = (nesw + placedOrientation) % 4;
            return placedFlipped ? flippedEdges[idx] : edges[idx];
        }

        public Tile(long tileId, String[] lines) {
            this.tileId = tileId;
            this.lines = lines;
            checkState(lines.length == TILE_SIZE);
            for (String line : lines) {
                checkState(line.length() == TILE_SIZE);
            }

            edges = new String[4];
            edges[0] = lines[0];
            edges[2] = reverse(lines[TILE_SIZE - 1]);
            StringBuilder east = new StringBuilder();
            StringBuilder west = new StringBuilder();
            for (int i = 0; i < TILE_SIZE; i++) {
                east.append(lines[i].charAt(TILE_SIZE - 1));
                west.append(lines[TILE_SIZE - i - 1].charAt(0));
            }
            edges[1] = east.toString();
            edges[3] = west.toString();

            flippedEdges = new String[4];
            flippedEdges[0] = reverse(edges[0]);
            flippedEdges[1] = reverse(edges[3]);
            flippedEdges[2] = reverse(edges[2]);
            flippedEdges[3] = reverse(edges[1]);
        }
    }


    private static String reverse(String x) {
        return new StringBuilder(x).reverse().toString();
    }

    private static boolean equalsReversed(String a, String b) {
        int length = a.length();
        for (int i = 0; i < length; i++) {
            if (a.charAt(i) != b.charAt(length - i - 1)) {
                return false;
            }
        }
        return true;
    }
}

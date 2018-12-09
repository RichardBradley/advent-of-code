package y2018;

import com.google.common.base.Stopwatch;

import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

public class Y2018D9 {
    public static void main(String[] args) throws Exception {

        Stopwatch stopwatch = Stopwatch.createStarted();

        // 1
        assertThat(getHighScore(9, 25)).isEqualTo(32);
        assertThat(getHighScore(10, 1618)).isEqualTo(8317);
        assertThat(getHighScore(13, 7999)).isEqualTo(146373);
        assertThat(getHighScore(17, 1104)).isEqualTo(2764);
        assertThat(getHighScore(21, 6111)).isEqualTo(54718);
        assertThat(getHighScore(30, 5807)).isEqualTo(37305);

        System.out.println(getHighScore(404, 71852));

        // 2
        System.out.println(getHighScore(404, 7185200));

        System.out.println("Took " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static long getHighScore(int playerCount, int lastMarbleScore) {
        int nextMarbleScore = 0;

        LinkedListNode currentMarble = new LinkedListNode();
        currentMarble.val = nextMarbleScore++;
        currentMarble.next = currentMarble;
        currentMarble.prev = currentMarble;

        long[] playerScores = new long[playerCount];

        Stopwatch stopwatch = Stopwatch.createStarted();
        long lastProgressReportMs = 0;

        outer:
        while (true) {
            for (int nextPlayerId = 0; nextPlayerId < playerCount; nextPlayerId++) {
                if ((nextMarbleScore % 23) == 0) {
                    // First, the current player keeps the marble they would have placed, adding it to their score.
                    // In addition, the marble 7 marbles counter-clockwise from the current marble is removed from
                    // the circle and also added to the current player's score.
                    // The marble located immediately clockwise of the marble that was removed becomes the new current marble.
                    playerScores[nextPlayerId] += (nextMarbleScore++);
                    for (int i = 0; i < 7; i++) {
                        currentMarble = currentMarble.prev;
                    }
                    playerScores[nextPlayerId] += currentMarble.val;
                    currentMarble = currentMarble.remove();
                } else {
                    // each Elf takes a turn placing the lowest-numbered remaining marble
                    // into the circle between the marbles that are 1 and 2 marbles clockwise of the current marble.
                    currentMarble = currentMarble.next;
                    currentMarble = currentMarble.insertAfter(nextMarbleScore++);
                }

                if (nextMarbleScore > lastMarbleScore) {
                    break outer;
                }
            }

            long elapsedMs = stopwatch.elapsed(TimeUnit.MILLISECONDS);
            if (elapsedMs - lastProgressReportMs > 10000) {
                lastProgressReportMs = elapsedMs;
                double done = nextMarbleScore;
                double total = lastMarbleScore;
                double remainingMs = (elapsedMs) / done * total;
                System.out.println(
                        "Est completion: " + Instant.now().plusMillis((long) remainingMs));
            }
        }

        return Arrays.stream(playerScores).max().getAsLong();
    }

    // I can't find a library for this...
    private static class LinkedListNode {
        int val;
        LinkedListNode prev;
        LinkedListNode next;

        /**
         * @return the new node
         */
        LinkedListNode insertAfter(int val) {
            LinkedListNode newNode = new LinkedListNode();
            newNode.val = val;
            LinkedListNode prevNext = this.next;
            this.next = newNode;
            newNode.next = prevNext;
            newNode.prev = this;
            prevNext.prev = newNode;
            return newNode;
        }

        /**
         * @return node that was after this
         */
        LinkedListNode remove() {
            LinkedListNode next = this.next;
            LinkedListNode prev = this.prev;
            prev.next = next;
            next.prev = this;
            return next;
        }
    }
}

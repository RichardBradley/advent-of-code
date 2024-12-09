package y2024;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Iterables;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static aoc.Common.loadInputFromResources;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2024D09 {

    public static void main(String[] args) throws Exception {
        List<String> input = loadInputFromResources();
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(part1(example)).isEqualTo(1928);
        assertThat(part1(input)).isEqualTo(6340197768906L);

        // 2
        assertThat(part2(example)).isEqualTo(2858);
        assertThat(part2(input)).isEqualTo(6363913128533L);

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static long part1(List<String> input) {
        String inputLine = Iterables.getOnlyElement(input);
        List<Integer> blocks = new ArrayList<>();
        boolean isFile = true;
        int fileId = 0;
        for (int i = 0; i < inputLine.length(); i++) {
            int n = inputLine.charAt(i) - '0';
            for (int j = 0; j < n; j++) {
                if (isFile) {
                    blocks.add(fileId);
                } else {
                    blocks.add(-1);
                }
            }
            if (isFile) {
                fileId++;
            }

            isFile = !isFile;
        }

        // Compact
        for (int nextFreePtr = 0; nextFreePtr < blocks.size(); nextFreePtr++) {
            if (blocks.get(nextFreePtr) == -1) {
                int endBlock;
                do {
                    endBlock = blocks.remove(blocks.size() - 1);
                } while (endBlock == -1);
                blocks.set(nextFreePtr, endBlock);
            } // else look at next space
        }

        long acc = 0;
        for (int i = 0; i < blocks.size(); i++) {
            acc += i * blocks.get(i);
        }

        return acc;
    }

    @Value
    static class FileSpan {
        int fileId; // -1 empty
        int length;
    }

    private static long part2(List<String> input) {
        String inputLine = Iterables.getOnlyElement(input);
        List<FileSpan> spans = new ArrayList<>();
        boolean isFile = true;
        int fileId = 0;
        for (int i = 0; i < inputLine.length(); i++) {
            int n = inputLine.charAt(i) - '0';
            if (isFile) {
                spans.add(new FileSpan(fileId++, n));
            } else {
                spans.add(new FileSpan(-1, n));
            }

            isFile = !isFile;
        }

        // Compact
        nextFile:
        for (int nextFileId = fileId - 1; nextFileId >= 0; nextFileId--) {
            int startingIdx = findFileId(spans, nextFileId);
            FileSpan fileToMove = spans.get(startingIdx);
            int fileSize = fileToMove.length;
            for (int possInsert = 0; possInsert < startingIdx; possInsert++) {
                FileSpan possSpace = spans.get(possInsert);
                int possSpaceLength = possSpace.length;
                if (possSpace.fileId == -1 && possSpaceLength >= fileSize) {
                    // remove file, replace it with space
                    spans.remove(startingIdx);
                    FileSpan spanPreceedingFile = spans.get(startingIdx - 1);
                    if (spanPreceedingFile.fileId == -1) {
                        spans.set(startingIdx - 1, new FileSpan(-1, spanPreceedingFile.length + fileSize));
                        // modified the space recorded above
                        if (startingIdx - 1 == possInsert) {
                            possSpaceLength += fileSize;
                        }
                    } else {
                        spans.add(startingIdx, new FileSpan(-1, fileSize));
                    }

                    // insert file into the found space
                    spans.set(possInsert, fileToMove);
                    if (possSpaceLength > fileSize) {
                        spans.add(possInsert + 1, new FileSpan(-1, possSpaceLength - fileSize));
                    }
                    continue nextFile;
                }
            }
        }

        if (spans.size() < 100) {
            for (FileSpan span : spans) {
                if (span.fileId == -1) {
                    for (int i = 0; i < span.length; i++) {
                        System.out.print(".");
                    }
                } else {
                    for (int i = 0; i < span.length; i++) {
                        System.out.print(Integer.toString(span.fileId));
                    }
                }
            }
            System.out.println();
        }

        int blockPtr = 0;
        long checkSum = 0;
        for (FileSpan span : spans) {
            if (span.fileId == -1) {
                blockPtr += span.length;
            } else {
                for (int i = 0; i < span.length; i++) {
                    checkSum += blockPtr * span.fileId;
                    blockPtr++;
                }
            }
        }

        return checkSum;
    }

    private static int findFileId(List<FileSpan> spans, int nextFileId) {
        for (int i = 0; i < spans.size(); i++) {
            if (spans.get(i).fileId == nextFileId) {
                return i;
            }
        }
        throw new IllegalArgumentException();
    }

    static List<String> example = List.of(
            "2333133121414131402");
}

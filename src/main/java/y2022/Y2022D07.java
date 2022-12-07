package y2022;

import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;
import lombok.Value;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2022D07 {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        List<String> input = Resources.readLines(Resources.getResource("y2022/Y2022D07.txt"), StandardCharsets.UTF_8);

        // 1
        assertThat(part1(example)).isEqualTo(95437);
        System.out.println(part1(input));

        // 2
        assertThat(part2(example)).isEqualTo(24933642);
        System.out.println(part2(input));

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    /**
     * Find all of the directories with a total size of at most
     * 100000. What is the sum of the total sizes of those directories?
     */
    private static long part1(List<String> input) {
        Dir root = parse(input);

        return sumAllDirsAtMost(root, 100000);
    }

    private static Dir parse(List<String> input) {
        Dir root = new Dir(null, null);
        Dir pwd = null;
        Pattern lsOutputPat = Pattern.compile("(\\d+|dir) ([\\w.]+)");
        for (int i = 0; i < input.size(); i++) {
            String line = input.get(i);
            if (line.startsWith("$ cd ")) {
                String target = line.substring("$ cd ".length());
                if (target.equals("/")) {
                    pwd = root;
                } else if (target.equals("..")) {
                    pwd = checkNotNull(pwd.parent);
                } else {
                    pwd = pwd.dirs.stream()
                            .filter(d -> d.name.equals(target))
                            .findFirst()
                            .get();
                }
            } else if (line.equals("$ ls")) {
                while (i < input.size() - 1) {
                    String next = input.get(i + 1);
                    if (next.startsWith("$")) {
                        break;
                    } else {
                        i++;
                        Matcher m = lsOutputPat.matcher(next);
                        checkState(m.matches());
                        if (m.group(1).equals("dir")) {
                            pwd.dirs.add(new Dir(m.group(2), pwd));
                        } else {
                            pwd.files.add(new File(Integer.parseInt(m.group(1))));
                        }
                    }
                }
            } else {
                throw new IllegalArgumentException(line);
            }
        }
        return root;
    }

    private static long sumAllDirsAtMost(Dir d, int maxSize) {
        long sum = d.dirs.stream().mapToLong(ch -> sumAllDirsAtMost(ch, maxSize)).sum();
        long size = d.getTotalSize();
        if (size <= maxSize) {
            return sum + size;
        } else {
            return sum;
        }
    }

    @Value
    private static class Dir {
        String name;
        Dir parent;
        List<File> files = new ArrayList<>();
        List<Dir> dirs = new ArrayList<>();

        long getTotalSize() {
            return files.stream().mapToLong(f -> f.size).sum()
                    + dirs.stream().mapToLong(d -> d.getTotalSize()).sum();
        }
    }

    @Value
    private static class File {
        long size;
    }

    private static long part2(List<String> input) {
        long diskSize = 70000000;
        Dir root = parse(input);
        long currFree = diskSize - root.getTotalSize();
        return part2(root, currFree);
    }

    // smallest dir to give 30000000 free
    private static long part2(Dir d, long currFree) {
        long size = d.getTotalSize();
        long freeAfterDel = currFree + size;
        long bestChild = d.dirs.stream().mapToLong(
                ch -> part2(ch, currFree)).min().orElseGet(() -> Long.MAX_VALUE);
        if (freeAfterDel > 30000000) {
            return Math.min(bestChild, size);
        } else {
            return bestChild;
        }
    }

    private static List<String> example = List.of(
            "$ cd /",
            "$ ls",
            "dir a",
            "14848514 b.txt",
            "8504156 c.dat",
            "dir d",
            "$ cd a",
            "$ ls",
            "dir e",
            "29116 f",
            "2557 g",
            "62596 h.lst",
            "$ cd e",
            "$ ls",
            "584 i",
            "$ cd ..",
            "$ cd ..",
            "$ cd d",
            "$ ls",
            "4060174 j",
            "8033020 d.log",
            "5626152 d.ext",
            "7214296 k");
}

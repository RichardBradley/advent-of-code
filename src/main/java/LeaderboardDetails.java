import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.time.Instant;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class LeaderboardDetails {

    public static void main(String[] args) throws Exception {
        try (FileReader in = new FileReader("2018-11-12 10.53 leaderboard.json")) {
            Gson gson = new GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .create();
            LeaderBoard leaderBoard = gson.fromJson(in, LeaderBoard.class);

            for (int day = 1; ; day++) {
                SortedSet<String> entries = new TreeSet<>();

                for (Member member : leaderBoard.members.values()) {
                    if (member.completionDayLevel != null) {
                        Map<Integer, StarTs> starTime = member.completionDayLevel.get(day);
                        if (starTime != null && starTime.get(2) != null) {
                            Instant star2 = Instant.ofEpochSecond(Long.parseLong(starTime.get(2).getStarTs));
                            entries.add(star2 + " " + member.name);
                        }
                    }
                }

                if (entries.isEmpty()) {
                    break;
                }
                System.out.println("Day " + day);
                for (String entry : entries) {
                    System.out.println(entry);
                }
                System.out.println();
            }
        }
    }

    static class LeaderBoard {
        String event;
        Map<Integer, Member> members;
    }

    static class Member {
        String name;
        Integer localScore;
        Integer globalScore;
        Map<Integer, Map<Integer, StarTs>> completionDayLevel;
    }

    static class StarTs {
        String getStarTs;
    }
}

package day4;

import java.util.List;

public class Q1 {
    public static void main(String[] args) {

        List<Input.DayLog> input = Input.input;


        int maxMinsAsleep = -1;
        int maxMinsAsleepGuardId = -1;

        for (Input.DayLog dayLog : input) {
            if (dayLog.minsAsleep >= maxMinsAsleep) {
                maxMinsAsleep = dayLog.minsAsleep;
                maxMinsAsleepGuardId = dayLog.guardId;
            }
        }

        int[] timesMinWasAsleep = new int[60];
        for (Input.DayLog dayLog : input) {
            if (dayLog.guardId == maxMinsAsleepGuardId) {
                for (int i = 0; i < 60; i++) {
                    if (dayLog.isAsleepByMin[i]) {
                        timesMinWasAsleep[i]++;
                    }
                }
            }
        }

        int maxTimesAsleepInMin = -1;
        int maxTimesAsleepInMinMin = -1;
        for (int i = 0; i < 60; i++) {
            if (timesMinWasAsleep[i] >= maxTimesAsleepInMin) {
                maxTimesAsleepInMin = timesMinWasAsleep[i];
                maxTimesAsleepInMinMin = i;
            }
        }

        System.out.println("guard id " + maxMinsAsleepGuardId);
        System.out.println("maxTimesAsleepInMinMin " + maxTimesAsleepInMinMin);
        System.out.println("mul " + maxMinsAsleepGuardId * maxTimesAsleepInMinMin);


        // Of all guards, which guard is most frequently asleep on the same minute?
        int[][] timesAsleepByGuardIdByMin = new int[10000][];
        for (Input.DayLog dayLog : input) {
            int guardId = dayLog.guardId;
            if (timesAsleepByGuardIdByMin[guardId] == null) {
                timesAsleepByGuardIdByMin[guardId] = new int[60];
            }
            int[] timesAsleepByMin = timesAsleepByGuardIdByMin[guardId];
            for (int i = 0; i < 60; i++) {
                if (dayLog.isAsleepByMin[i]) {
                    timesAsleepByMin[i]++;
                }
            }
        }
        int maxSleepsInSameMin = -1;
        int maxSleepsInSameMinGuardId = -1;
        int maxSleepsInSameMinMin = -1;
        for (int guardId=0; guardId<timesAsleepByGuardIdByMin.length; guardId++) {
            if (timesAsleepByGuardIdByMin[guardId] != null) {
                int[] timesAsleepByMin = timesAsleepByGuardIdByMin[guardId];
                for (int min=0; min<60; min++) {
                    if (timesAsleepByMin[min] >= maxSleepsInSameMin) {
                        maxSleepsInSameMin = timesAsleepByMin[min];
                        maxSleepsInSameMinGuardId = guardId;
                        maxSleepsInSameMinMin = min;
                    }
                }
            }
        }

        System.out.println("maxSleepsInSameMinGuardId " + maxSleepsInSameMinGuardId);
        System.out.println("maxSleepsInSameMinMin " + maxSleepsInSameMinMin);
        System.out.println("mul " + maxSleepsInSameMinGuardId * maxSleepsInSameMinMin);

    }
}

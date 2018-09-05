package com.jaishni;

public class LongestIncreasingSubsequence {

    int list[] = { };

    private int getLISLength() {
        final int l = list.length;
        if (l == 0) return 0;

        int resultCounter = 1;
        int lastValue = list[0];


        for (int i = 1; i < l; i++) {

            if (lastValue < list[i]) {
                lastValue = list[i];
                resultCounter++;
            }

        }

        return resultCounter;
    }

    public static void main(String[] args) {
        LongestIncreasingSubsequence LIS = new LongestIncreasingSubsequence();

        System.out.printf("Length = " + LIS.getLISLength());
    }
}

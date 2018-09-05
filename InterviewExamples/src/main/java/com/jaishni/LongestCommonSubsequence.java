package com.jaishni;

import java.util.HashSet;
import java.util.Set;

public class LongestCommonSubsequence {
    final char[] A = {'A','B','C','D'};
    final char[] B = {'C','D','E','F','F'};


    private String getLcs() {
        // Find the shorter of the two strings
        final int lengthA = A.length;
        final int lengthB = B.length;

        if (lengthA < lengthB) {
            return compute(A, B);
        } else {
            return compute(B, A);
        }
    }

    private String compute(final char[] shorter, final char[] longer) {
        Set<String> resultSet = new HashSet<String>();
        String lcs = "";
        StringBuilder sb = new StringBuilder();
        int runningMax = 0;
        int previousMax = 0;

        for (int l = 0; l < longer.length; l++) {


            for (int s = 0; s < shorter.length; s++) {
                if (longer[l] == shorter[s]) {
                    sb.append(longer[l]);
                    runningMax = sb.length();
                    break;
                } else {

                    // if no match and stringbuffer is not 0 length
                    // check if this is the longest running list so far
                    if (sb.length() > previousMax) {
                        resultSet.add(sb.toString());
                        lcs = sb.toString();
                        previousMax = runningMax;
                        sb = new StringBuilder();
                    }
                }
            }
        }
        return lcs;
    }

    public static void main(String[] args) {
        LongestCommonSubsequence lcs = new LongestCommonSubsequence();
        lcs.getLcs();
    }
}

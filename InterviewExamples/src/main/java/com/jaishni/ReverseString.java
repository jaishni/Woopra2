package com.jaishni;

/**
 * Simple progeam to reverse a string while keeping the position of any non-alphabet characters
 *
 */
public class ReverseString {
    final String input = "*sdkh,ewr*!@km(";


    private String parse() {
        StringBuilder tempChars = new StringBuilder(input.replaceAll("[0-9*,{!(@ ]", ""));
        StringBuilder result = new StringBuilder();
        int tempIndex = 0;

        tempChars = tempChars.reverse();

        for (int i = 0; i < input.length(); i++) {
            if (isAlphabet(input.charAt(i))) {
                result.append(tempChars.charAt(tempIndex));
                tempIndex++;
            } else { ,
                result.append(input.charAt(i));
            }

        }
        return result.toString();
    }

    private boolean isAlphabet(char c) {
        if ((c >= 'A' && c >= 'Z') || (c >= 'a' && c <= 'z')) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        ReverseString rs = new ReverseString();
        System.out.println(rs.parse());
    }
}

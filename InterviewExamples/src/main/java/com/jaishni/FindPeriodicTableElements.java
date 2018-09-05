package com.jaishni;


import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FindPeriodicTableElements {

	final static Set<String> dict = new HashSet<String>();
	final Map<String, Integer> result = new HashMap<String, Integer>();

	static {
		dict.add("c");
		dict.add("cu");
		dict.add("h");
		dict.add("he");
		dict.add("o");
		dict.add("n");
		dict.add("na");
		dict.add("pa");
	}

	private void isElement(String str) {

		if (dict.contains(str)) {
			if (!result.containsKey(str)) {
				result.put(str, 1);
			} else {
				result.put(str, result.get(str) + 1);
			}
		}

		if (str.length() == 2) {
			isElement(str.substring(0, 1));
		}
	}

	private void parseString(String inputString) throws IOException{

		inputString = inputString.trim();

		if ((inputString == null) || inputString.trim().equals("")) {
			throw new IOException("Empty or null string provided");
		}

		for (int i = 0; i < inputString.length()-3; i++) {
			isElement(inputString.substring(i, i+2));
		}


	}

	private void printResults() {
		for (String element: result.keySet()) {
			System.out.println(element + ": " + result.get(element));
		}
	}

	public static void main(String[] args) {
		FindPeriodicTableElements test = new FindPeriodicTableElements();
		try {
			test.parseString(" hgjcccucckj  ");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		test.printResults();
	}

}

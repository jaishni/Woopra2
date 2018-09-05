package com.jaishni;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/*
[l t r e]
[m a n q]
[x a p b]
[a g o s]

m -> a -> n
t -> a -> p
p -> a -> n -> e
s -> o -> b -> s
*/

public class Boggle {
    /* Enter your code here. Read input from STDIN. Print output to STDOUT */

    Map<Integer, Integer> usedPoints = new HashMap<Integer, Integer>();
    final char[][] matrix = {{'l','t','r','e'},{'m','a','n','q'},{'x','a','p','b'},{'a','g','o','s'}};

    final static int N = 4;

    final Set<String> dictSet = new HashSet<String>();
    final Set<String> resultSet = new HashSet<String>();



    private void getValidWords(int Pointx, int Pointy, String currentWord, HashSet<String> stalePoints) {


    	currentWord = currentWord + Character.toString(matrix[Pointx][Pointy]);

        if (dictSet.contains(currentWord)) {
            resultSet.add(currentWord);
        }

        // add current coordinates to listed of already traversed points
        stalePoints.add(Pointx + "," + Pointy);

        // if we have traversed all points available terminate
        if (stalePoints.size() == N * N) return;

        //Otherwise find the next available point
        if (Pointx - 1 >= 0) {
            //  (x-1, y-1)
            if ((Pointy - 1 >= 0) && (!stalePoints.contains((Pointx-1) + "," + (Pointy-1)))) {
                getValidWords(Pointx - 1, Pointy - 1, currentWord, new HashSet<String>(stalePoints));
            }

            //  (x-1, y+1)
            if ((Pointy + 1 < N)  && (!stalePoints.contains((Pointx-1) + "," + (Pointy+1)))) {
                getValidWords(Pointx - 1, Pointy + 1, currentWord, new HashSet<String>(stalePoints));
            }

            //  (x-1, y)
			if (!stalePoints.contains((Pointx-1) + "," + (Pointy))) {
				getValidWords(Pointx - 1, Pointy, currentWord, new HashSet<String>(stalePoints));
			}

        }

        if (Pointx + 1 < N) {
            // (x+1, y)
			if (!stalePoints.contains((Pointx+1) + "," + (Pointy))) {
				getValidWords(Pointx+1, Pointy, currentWord, new HashSet<String>(stalePoints));
			}

            // (x+1, y+1)
            if ((Pointy + 1 < N)  && !stalePoints.contains((Pointx+1) + "," + (Pointy+1))) {
                getValidWords(Pointx + 1, Pointy + 1, currentWord, new HashSet<String>(stalePoints));
            }

            // (x+1, y-1)
            if ((Pointy - 1 >= 0)  && (!stalePoints.contains((Pointx+1) + "," + (Pointy-1)))) {
                getValidWords(Pointx + 1, Pointy - 1, currentWord, new HashSet<String>(stalePoints));
            }

        }


        // (x, y-1)
        if ((Pointy - 1 >= 0)  && (!stalePoints.contains((Pointx) + "," + (Pointy-1)))) {
            getValidWords(Pointx, Pointy - 1, currentWord, new HashSet<String>(stalePoints));
        }
        // (x, y+1)
        if ((Pointy + 1 < N)  && (!stalePoints.contains((Pointx) + "," + (Pointy+1)))) {
            getValidWords(Pointx, Pointy + 1, currentWord, new HashSet<String>(stalePoints));
        }
    }

    public void printResults() {
    	for (String word: resultSet) {
			System.out.println(word);
		}
	}

    private void initializeDictionary() {
        dictSet.add("lap");
        dictSet.add("a");
        dictSet.add("tan");
        dictSet.add("tana");
    }

    public static void main(String args[] ) {

		Boggle boggle = new Boggle();
		boggle.initializeDictionary();

		for (int x = 0; x < N; x++) {
			for (int y = 0; y < N; y++) {
				boggle.getValidWords(x, y, new String(), new HashSet<String>());
			}
		}

		boggle.printResults();

    }
}

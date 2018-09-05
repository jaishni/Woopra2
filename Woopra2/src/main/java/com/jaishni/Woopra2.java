package com.jaishni;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * @author jaishni
 *
 * NOTE:
 * Was a fun exercise! I chose to go the route of using a TreeMap to store the results and a HashMap dictionary to keep track of records for the folling reasons:
 * 1. Decouples the json file size from the amount of memory uased by the class. The memory used only dependant on N (the number of records output in the end result)
 * 2. Using a Treeset will ensure that
 * 	  ~ Entries are always ordered by timestamp
 * 	  ~ Will account for cases when we see more than one pid at the same timestamp (i.e. timestamp collision)
 * 	  ~ Insertion, deletion and retrieval of entries are performed at O(log n)
 * 	  ~ Note a hashmap will preform better on average at O(1) but we still have tp pay the cost of sorting the results and adding complexity to know which is the smallest timestamp at all times.
 * 	  ~ Expect performance to be reasonable as it is only storing a max of N values in all key nodes combined
 * 3. Usings a Hashmap dictionary
 *    ~ Keeps track of which pids have been inserted as values into the Treemap (to help with pid collisions)
 * 	  ~ Keeps track of the timestamp associated with a pid. (to help with timestamp collisions)
 * 	  ~ Ensures that the TreeMap never contains more than N values. In doing so it minimise memory usage to no more than N records and ensuring efficient memory management even as the json file size increases.
 * 	  ~ performs at O(1) on average.
 * 	  ~ Expect performance to be reasonable as it is only storing a max of N elements
 *
 */

public class Woopra2 {

	// can be configurable but hard-coding this for now
	final static int N = 20;
	final static String PID = "pid";
	final static String ACTIONS = "actions";
	final static String TIMESTAMP = "time";


	final TreeMap<Long, HashSet<String>> resultMap = new TreeMap<Long, HashSet<String>>(Collections.reverseOrder());

	// This hashmap will :
	// 1. Keep track of which pids have been inserted as values into the Treemap (to help with pid collisions)
	// 2. Keep track of the timestamp associated with a pid. (to help with timestamp collisions)
	// 3. Ensure that the TreeMap never contains more than N values. In doing so it minimise memory usage to no more than N records and ensuring efficient memory management even as the json file size increases.

	final Map<String, Long> lookupMap = new HashMap<String, Long>();


	/**
	 * Streams through a json file one line at a time. Ensures that only one line is stored within memory at a time.
	 * This will ensure that the application is able to scale efficiently as the input json file size increases.
	 * I attempted to stream the json directly with Google's GSON API but ran into issues. I have commented that code out below just for reference
	 *
	 * @param aFileName
	 * @throws IOException
	 */
	public void streamFile(final String aFileName) throws IOException {
		FileInputStream inputStream = null;
		Scanner scanner = null;
		try {
			inputStream = new FileInputStream(aFileName);
			scanner = new Scanner(inputStream, "UTF-8");

			while (scanner.hasNextLine()) {
				parseJson(scanner.nextLine());
			}

			if (scanner.ioException() != null) {
				throw scanner.ioException();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();

		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
			if (scanner != null) {
				scanner.close();
			}
		}
	}

	/**
	 * Reads in one json record at a time
	 * Calls the validateKeyPair of each json it receives
	 * Note: broke this method out to make code readability easier
	 * @param aJsonLine
	 */
	public void parseJson(final String aJsonLine) {
		String pid;
		long maxTimestamp;

		try {
			final JSONObject jsonRecord = new JSONObject(aJsonLine);
			final JSONArray actionsArray =jsonRecord.getJSONArray(ACTIONS);

			pid = jsonRecord.get(PID).toString();
			maxTimestamp = getMostRecentTimestamp(actionsArray);

			validateKeyPair(maxTimestamp, pid);

		} catch(final JSONException ex) {
			ex.printStackTrace();
		}
	}


	/**
	 * Reads in a Json array of timestamps and returns the most recent one
	 * @param actionsArray
	 * @return maxTimestamp

	 */
	public long getMostRecentTimestamp (final JSONArray actionsArray) {
		long maxTimestamp = 0L;

		if (actionsArray.length() == 0) {
			return 0L;
		}

		for (int i = 0; i < actionsArray.length(); i++) {
			final JSONObject actions = (JSONObject) actionsArray.get(i);
			maxTimestamp = Math.max(actions.getLong(TIMESTAMP), maxTimestamp);
		}

		return maxTimestamp;
	}


	/**
	 * Uses Googles Gson API to parse json in a stream fashion.
	 * Ran into issues reading more than one line from the file.
	 * Suspect it was likely due to file format discrepencies
	 * Moved onto streaming the file directly as fixing the issue was taking too long.
	 * Leaving this code in for reference for future work.
	 */
//	public void streamJson(final String aJsonRecord)	{
//
//			JsonReader reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
//			reader.beginObject();
//
//			while (reader.hasNext()) {
//
//				String name = reader.nextName();
//
//				if (PID.equals(name)) {
//					System.out.println(name);
//					System.out.println(reader.nextString());
//
//				} else if (ACTIONS.equals(name)) {
//					reader.beginArray();
//					reader.beginObject();
//					while (reader.hasNext()) {
//						String objectWeatherName = reader.nextName();
//
//						if (objectWeatherName.equals("time")) {
//							System.out.println(reader.nextString());
//						} else {
//							reader.skipValue();
//						}
//
//					}
//					reader.endObject();
//					reader.endArray();
//
//				} else {
//					reader.skipValue();
//				}
//			}
//			reader.endObject();
//			reader.close();
//
//	}

	/**
	 * Checks if the new key pair should be included in the top N records and if so inserts it into the result set
	 * @param timeStamp
	 * @param pid
	 */
	public void validateKeyPair(final long timeStamp, final String pid) {

		final boolean isDuplicatePid = lookupMap.containsKey(pid);

		if (!resultMap.isEmpty()) {

			// check for duplicates
			if (!isDuplicatePid) {
				insertNewEntry(timeStamp, pid);

			} else {
				// check if the existing entry should be replaced
				if (timeStamp > lookupMap.get(pid)) {

					//if yes then remove it and add the new entry
					removeTreeEntry(lookupMap.get(pid), pid);
					insertNewEntry(timeStamp, pid);
				}

			}
		} else if ((resultMap.isEmpty()) || ((lookupMap.size() <= N)) && (!isDuplicatePid)) {
			// insert the entry into the tree
			insertNewEntry(timeStamp, pid);
		}
	}

	/**
	 * Inserts a new entry into the result set and lookup map.
	 * Also ensures that the result set and lookup map are trimmed down to the expected size
	 * @param timeStamp
	 * @param pid
	 */
	public void insertNewEntry(final long timeStamp, final String pid) {
		// check if the timestamp already exists
		// if so add the pid to the Set of pid values it maps to
		if (resultMap.containsKey(timeStamp)) {

			// insert into the global result set
			HashSet pidValues = resultMap.get(timeStamp);
			pidValues.add(pid);

			resultMap.put(timeStamp, pidValues);
		} else {
			// insert a new node into the tree
			HashSet<String> newPidList = new HashSet<String>();
			newPidList.add(pid);

			resultMap.put(timeStamp, newPidList);
		}

		// update the lookup map
		lookupMap.put(pid, timeStamp);

		// Trim Tree if required
		while (lookupMap.size() > N) {
			// remove lowest timestamp to ensure that the result set is always the right size
			removeLowestTreeEntry();
		}

	}

	/**
	 * Trims the resultTree if the total number of pids exceed RESULT_COUNT
	 */
	public void removeLowestTreeEntry() {
		final long lowestTimestamp = resultMap.lastKey();
		final HashSet<String> pids = resultMap.get(lowestTimestamp);
		final String disposedPid = pids.iterator().next();

		// No preference as to which pid should be removed.
		// So we remove the first pid from the Set since they all map to the same timestamp.
		// We can add conditions for which pid should be deleted here later if required.
		removeTreeEntry(lowestTimestamp, disposedPid);
	}

	/**
	 * Removes a specific pid value from the tree
	 * If a node has a single pid value then that node is removed
	 * @param timeStamp
	 * @param pid
	 */
	public void removeTreeEntry(final long timeStamp, final String pid) {
		final HashSet<String> pids = resultMap.get(timeStamp);

		if (pids.size() == 1) {
			// remove the entire node from the tree
			resultMap.remove(timeStamp);

		} else if (pids.size() > 1)  {
			//remove the pid from the Set of values associated with this timestamp
			pids.remove(pid);
			resultMap.put(timeStamp, pids);
		}

		lookupMap.remove(pid);
	}


	/**
	 * Output the final result set to console in readable format
	 * @return
	 */
	@Override
	public String toString() {
		StringBuilder prettyPrintOutput = new StringBuilder();

		for (final long timestamp : resultMap.keySet()) {
			for (final String pid: resultMap.get(timestamp)) {
				prettyPrintOutput.append("pid: " + pid + ", timestamp: " + timestamp + "\n");
			}
		}
		return prettyPrintOutput.toString();
	}

	public static void main(String args[]) {
		Woopra2 test = new Woopra2();

		try {
			test.streamFile("/Users/jaishni/IdeaProjects/Woopra2/src/main/resources/file.txt");
			System.out.println(test.toString());
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
			System.out.println(ex.getMessage());
		}
	}
}



package edu.nyu.pqs.stopwatch.impl;

import java.util.List;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.Set;
import edu.nyu.pqs.stopwatch.api.IStopwatch;

/**
 * The StopwatchFactory is a thread-safe factory class for IStopwatch objects.
 * It maintains references to all created IStopwatch objects and provides a
 * convenient method for getting a list of those objects.
 * 
 */
public class StopwatchFactory {
    private static List<IStopwatch> watchList = new LinkedList<>();
    // Check if an id has been taken.
    private static Set<String> idSet = new HashSet<>();
    private static Object locker = new Object();
    
	/**
	 * Creates and returns a new IStopwatch object
	 * @param id The identifier of the new object
	 * @return The new IStopwatch object
	 * @throws IllegalArgumentException if <code>id</code> is empty, null, or already
     * taken.
	 */
	public static IStopwatch getStopwatch(String id) {
	  synchronized(locker) {
	    if (id == null || id.length() == 0) {
	        throw new IllegalArgumentException("The id is invalid!");
	      }
	      if (idSet.contains(id)) {
	        throw new IllegalArgumentException("This id is already taken!");
	      }
	      IStopwatch stopWatch = new StopWatch(id);
	      watchList.add(stopWatch);
	      idSet.add(id);
	      return stopWatch;
	  }
	}

	/**
	 * Returns a list of all created stopwatches
	 * @return a List of al creates IStopwatch objects.  Returns an empty
	 * list if no IStopwatches have been created.
	 */
	public static List<IStopwatch> getStopwatches() {
		if (watchList.size() == 0) {
		  return new LinkedList<IStopwatch>();
		}
		return watchList;
	}
}
    
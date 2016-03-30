package edu.nyu.pqs.stopwatch.impl;

import java.util.HashMap;
import java.util.Map;

import java.util.Collections;

import edu.nyu.pqs.stopwatch.api.IStopwatch;

/**
 * The StopwatchFactory is a thread-safe factory class for IStopwatch objects.
 * It maintains references to all created IStopwatch objects and provides a
 * convenient method for getting a list of those objects.
 * 
 */
public class StopwatchFactory {
  // Instead of using two data structures(one List and another HashSet), using one Map
  // mapping id to StopWatch. 
  private static Map<String, IStopwatch> watchMap = new HashMap<>();
  // Check if an id has been taken.
  // private static Set<String> idSet = new HashSet<>();
  private static Object locker = new Object();

  /**
   * Creates and returns a new IStopwatch object
   * 
   * @param id
   *          The identifier of the new object
   * @return The new IStopwatch object
   * @throws IllegalArgumentException
   *           if <code>id</code> is empty, null, or already taken.
   */
  public static IStopwatch getStopwatch(String id) {
    synchronized (locker) {
      if (id == null || id.length() == 0) {
        throw new IllegalArgumentException("The id is invalid!");
      }
      if (watchMap.containsKey(id)) {
        throw new IllegalArgumentException("This id is already taken!");
      }
      IStopwatch stopWatch = new StopWatch(id);
      watchMap.put(id, stopWatch);
      return stopWatch;
    }
  }

  /**
   * This methods should be thread-safe, since size check and return may run in
   * parallel. Returns a list of all created stopwatches
   * 
   * @return a List of al creates IStopwatch objects. Returns an empty list if
   *         no IStopwatches have been created.
   */
  public static Map<String, IStopwatch> getStopwatches() {
    synchronized (locker) {
      if (watchMap.size() == 0) {
        return new HashMap<String, IStopwatch>();
      }
      return Collections.unmodifiableMap(watchMap);
    }
  }
}

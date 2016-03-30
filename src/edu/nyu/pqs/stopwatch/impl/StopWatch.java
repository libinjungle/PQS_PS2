package edu.nyu.pqs.stopwatch.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Collections;


import edu.nyu.pqs.stopwatch.api.IStopwatch;

/**
 * Two states of StopWatch. StopWatch can be in RUNNING state or STOPPED state.
 */
enum State {
  RUNNING, STOPPED;
}

/**
 * Don't list class variables in javadoc unless they are publicly accessible.
 * This class implements the IStopWatch interface. It can be used to calculate the laps
 * between starting point until the stop watch is stopped. All methods should be thread-safe.
 * @author BINLI
 *
 */
public class StopWatch implements IStopwatch  {
  private List<Long> lapTimes;
  private String id;
  private State state;
  // This lock object is used for synchronization of threads.
  private Object lock;
  private long startTime;
  
  /**
   * Don't use javadoc for constructor
   */
  public StopWatch(String id) {
    this.id = id;
    this.lapTimes = new LinkedList<Long>();
    this.state = State.STOPPED;
    this.lock = new Object();
    this.startTime = 0L;
  }
  
  /**
   * "Override annotation should be there. It can avoid mistakes. For example,
   * using public boolean equals(someclass object) in implemented class instead of 
   * public boolean equals(Object object) in the interface.
   * 
   * Returns the Id of this stopwatch
   * @return the Id of this stopwatch.  Will never be empty or null.
   */
  @Override
  public String getId() {
    return this.id;
  }
  
  /**
   * Starts the stopwatch. This method should be thread-safe.
   * @throws IllegalStateException if called when the stopwatch is already running
   */
  @Override
  public void start() {
    synchronized(lock) {
      if (state == State.RUNNING) {
        throw new IllegalStateException("the stopwatch is already running.");
      }
      startTime = System.currentTimeMillis();
      state = State.RUNNING;
    }
  }

  /**
   * Stores the time elapsed since the last time lap() was called
   * or since start() was called if this is the first lap. This method should be thread-safe.
   * @throws IllegalStateException if called when the stopwatch isn't running
   */
  @Override
  public void lap() {
    synchronized(lock) {
      if (state == State.STOPPED) {
        throw new IllegalStateException("The stopwatch already stopped.");
      }
      long curTime = System.currentTimeMillis();
      long diff = curTime - startTime;
      startTime = curTime;
      lapTimes.add(diff);
    }
  }

  /**
   * Stops the stopwatch (and records one final lap). This method should be thread-safe.
   * @throws IllegalStateException if called when the stopwatch isn't running
   */
  @Override
  public void stop() {
    synchronized(lock) {
      if (state == State.STOPPED) {
        throw new IllegalStateException("The stopwatch already stopped.");
      }
      // Should use System.nanoTime() since it is much more accurate. 
      // But it is an expensive call.
      long curTime = System.currentTimeMillis();
      // start -> stop -> start leaves a lap in lapTimes. This should clear the last lap and 
      // continue timing where it left off. This will cause an error for adjacent stop/start, 
      // since the time between stop->start should not be counted.
      // Use a boolean to indicate the last state is not START.
      long diff = curTime - startTime;
      lapTimes.add(diff);
      state = State.STOPPED;
    }  
  }

  /**
   * Resets the stopwatch.  If the stopwatch is running, this method stops the
   * watch and resets it.  This also clears all recorded laps. This method should be thread-safe.
   */
  @Override
  public void reset() {
    synchronized(lock) {
      if (state == State.RUNNING) {
        state = State.STOPPED;
      }
      startTime = 0L;
      lapTimes.clear();
    }
  }
  
  /**
   * This methods should be thread-safe. Otherwise, lapTimes size check and return 
   * may run in parallel.
   * Returns a list of lap times (in milliseconds).  This method can be called at
   * any time and will not throw an exception.
   * @return a list of recorded lap times or an empty list if no times are recorded.
   */
  @Override
  public List<Long> getLapTimes() {
    synchronized (lock) {
      if (lapTimes.size() == 0) {
        return new LinkedList<Long>();
      }
      // getStopwatches() and getLapTimes() return references to private members. 
      // Would be better to return a copy, so that the user can’t alter internal state or make 
      // changes that will affect expected execution 
      // (changing it externally producing ConcurrentModificationExceptions).
      return Collections.unmodifiableList(lapTimes);
    }
  }
  
  /**
   * Thred-safe. The hashcode method in List<StopWatch> iterate the list.
   * No need to compare state and lapTimes. They are all runtime behavior.
   * Compare to StopWatch object.
   */
  @Override
  public boolean equals(Object object) {
    synchronized(lock) {
      if (object == this) {
        return true;
      }
      if (!(object instanceof StopWatch)) {
        return false;
      }
      StopWatch other = (StopWatch) object;
      return other.id.equals(this.id);
    }
  }
  
  /**
   * Thred-safe. The equals methods in List<StopWatch> iterate the list.
   * Return the hashcode for a StopWatch object.
   */
  @Override 
  public int hashCode() {
    synchronized (lock) {
      int result = 17;
      result = 31 * result + this.id.hashCode();
      return result;
    }
  }
  
  /**
   * Used for printing out the StopWatch object.
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    // 
    sb.append("StopWatch: id = ");
    sb.append(this.id);
    if (state == State.RUNNING) {
      sb.append(", The state is RUNNING. ");
    } else {
      sb.append(", The state is Stopped. ");
    }
    sb.append("The laps list is: [");
    synchronized(lock) {
      for (Long laptime : lapTimes) {
        sb.append(laptime);
        sb.append(",");
      }
      sb.deleteCharAt(sb.length()-1);
      sb.append("]");
    }
    return sb.toString();  
  }
}

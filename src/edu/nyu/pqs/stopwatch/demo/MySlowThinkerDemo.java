package edu.nyu.pqs.stopwatch.demo;

import java.util.List;
import java.util.logging.Logger;
import edu.nyu.pqs.stopwatch.api.IStopwatch;
import edu.nyu.pqs.stopwatch.impl.StopwatchFactory;

public class MySlowThinkerDemo {
  /** use a logger instead of System.out.println */
  private static final Logger logger = 
      Logger.getLogger("edu.nyu.pqs.ps4.demo.MySlowThinkerDemo");
  
  /**
   * Spawns multiple threads to run stopwatch.
   * @param args a single argument specifying the number of threads
   */
  public static void main(String[] args) {
    MySlowThinkerDemo thinker = new MySlowThinkerDemo();
    for (int i=0; i<5; i++) {
      thinker.go();
    }    
  }

  /**
   * Creats an StopWatch object. Gets a few laps and print out the laps 
   * list and the StopWatch object.
   */
  private void go() {
      Runnable runnable = new Runnable() {
          public void run() {
            IStopwatch stopwatch;
            synchronized(this) {
              stopwatch = StopwatchFactory.getStopwatch("ID " + Thread.currentThread().getId());
              stopwatch.start();
            }
              for (int i = 0; i < 10; i++) {
                  try {
                      Thread.sleep(1000);
                  } catch (InterruptedException ie) { /* safely ignore this */ }
                  try {
                    stopwatch.lap();
                  } catch (IllegalStateException ignored) { }
              }
              List<Long> times1 = stopwatch.getLapTimes();
              logger.info("The number of laps is: " + times1.size());
              try {
                Thread.sleep(500);
              } catch (InterruptedException ignored) { }
              stopwatch.reset();
              logger.info("Interrupte stopwatch and reset it...");
              List<Long> times = stopwatch.getLapTimes();
              logger.info("The number of laps is: " + times.size());
              
        //      try {
        //        stopwatch.stop();
        //      } catch (IllegalStateException ignored) { }
              
       //       List<Long> times = stopwatch.getLapTimes();
       //       logger.info(times.toString());
       //       logger.info(stopwatch.toString());
       //       logger.info("The number of laps is: " + times.size());

              try {
                stopwatch.start();
              } catch (IllegalStateException ignored) { }
              
              try {
                Thread.sleep(500);
              } catch (InterruptedException ignored) { }
              
              try {
                stopwatch.lap();
              } catch (IllegalStateException ignored) { }
              
              try {
                stopwatch.stop();
              } catch (IllegalStateException ignored) { }
              
              List<Long> timesNow = stopwatch.getLapTimes();
              logger.info(timesNow.toString());
              logger.info(stopwatch.toString());  
              logger.info("The number of laps is: " + timesNow.size());
            }
          
      };
      new Thread(runnable).start();
  }
}



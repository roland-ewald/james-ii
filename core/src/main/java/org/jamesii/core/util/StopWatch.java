/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util;

import org.jamesii.core.util.misc.TimeSpan;

/**
 * This class provides a simple stopwatch for time measurement.
 * 
 * It is based on the system's clock, and thus it measures some real elapsed
 * time between start and stop, respectively between start and a call of one of
 * the elapsedXXX methods. Surely it can be used to measure the time passed in
 * between these calls, but it is hard to draw any conclusion about algorithm
 * performance from this. If used therefore one should keep in mind, that
 * multi-core CPUs can harm the results as well as threads, processes and their
 * management in general.
 * 
 * Written by Petra and Dirk Tyschler, for the old JAMES, adapted therefore by
 * Karsten Gugler, and finally transferred to CoSA by Jan Himmelspach (2003)
 * 
 * @author Jan Himmelspach
 */
public final class StopWatch {

  /**
   * Number of totally elapsed nanoseconds since first start of stopwatch.
   */
  private long elapsedTime = 0;

  /**
   * State this stopwatch is in. Could be running (start button pressed) or not
   * running (stop or reset button pressed). When this watch is instanciated it
   * is not running.
   */
  private boolean running = false;

  /**
   * This VM's time in nanoseconds when the start button was pressed
   * successfully for the last time.
   */
  private long startTime;

  /**
   * Returns the number of elapsed hours if the stop watch is stopped. Otherwise
   * it returns the current meantime in hours since the first start of the stop
   * watch.
   * 
   * @return the elapsed hours as double
   */
  public double elapsedHours() {
    return Math.rint(meanTime() / 36000d / 1000000d) / 100d;

  }

  /**
   * Returns the number of elapsed milliseconds if the stop watch is stopped.
   * Otherwise it returns the current meantime in milliseconds since the first
   * start of the stop watch.
   * 
   * @return the long
   */
  // FIXME change return type to double and return two fractions
  public long elapsedMilliseconds() {
    return (long) Math.rint(meanTime() / 1000000d);
  }

  /**
   * Returns the number of elapsed microseconds if the stop watch is stopped.
   * Otherwise it returns the current meantime in microseconds since the first
   * start of the stop watch.
   * 
   * @return the elapsed microseconds
   */
  public double elapsedMicroseconds() {
    return (long) Math.rint(meanTime() / 10d) / 100d;
  }

  /**
   * Returns the number of elapsed nanoseconds if the stop watch is stopped.
   * Otherwise it returns the current meantime in nanoseconds since the first
   * start of the stop watch.
   * 
   * @return the elapsed nanoseconds
   */
  public long elapsedNanoseconds() {
    return meanTime();
  }

  /**
   * Returns the number of elapsed minutes if the stop watch is stopped.
   * Otherwise it returns the current meantime in minutes since the first start
   * of the stop watch.
   * 
   * @return the double
   */
  public double elapsedMinutes() {
    return Math.rint(meanTime() / 600d / 1000000d) / 100d;

  }

  /**
   * Returns the number of elapsed seconds if the stop watch is stopped.
   * Otherwise it returns the current meantime in seconds since the first start
   * of the stop watch.
   * 
   * @return the double
   */
  public double elapsedSeconds() {
    return Math.rint(meanTime() / 10d / 1000000d) / 100d;
  }

  /**
   * Gets the elapsed time as a {@link TimeSpan} object. The time is stored down
   * to nanosecond precision.
   * 
   * @return A {@link TimeSpan} representing the elapsed time of this
   *         {@link StopWatch}.
   */
  public TimeSpan getElapsedTime() {
    return new TimeSpan(this.elapsedNanoseconds());
  }

  // new:
  /**
   * Indicates if this stop watch is running: returns <CODE>true</CODE> if the
   * stop watch is running and <CODE>false</CODE> otherwise.
   * 
   * @return true, if checks if is running
   */
  public boolean isRunning() {
    return running;
  }

  /**
   * Measures the currently (total) elapsed time without stopping the stop
   * watch, if it is running.
   * 
   * @return the time value in the unit returned by {@link #now()}
   */
  private long meanTime() {
    if (running) {
      long stopTime = now();
      long elapsed = stopTime - startTime;
      if (elapsed < 0) {
        elapsed = 0; // should not happen
      }
      return elapsedTime + elapsed;
    }
    return elapsedTime;
  }

  /**
   * Calculates this virtual machines current time.
   * 
   * @return the current time as nano time
   */
  private long now() {
    return System.nanoTime();
  }

  /**
   * Resets this stopwatch by stopping the watch, if it is running, and setting
   * the totally elapsed and measured time back to zero.
   * 
   * @return true, if reset
   */
  public boolean reset() {
    running = false;
    elapsedTime = 0;
    return true;
  }

  /**
   * Calling this method corresponds with pressing the start button of this
   * stopwatch. If the stopwatch is already running nothing happens and this
   * method returns <CODE>false</CODE>. If the stopwatch is not running, then it
   * is started and <CODE>true</CODE> is returned. <BR>
   * You may restart the stopwatch again and again. As long as you don't reset
   * the stop watch, the totally elapsed time increases.
   * 
   * @return true, if start
   */
  public boolean start() {
    if (!running) {
      running = true;
      startTime = now();
      return true;
    }
    return false;
  }

  /**
   * Temporarily stops this stopwatch. If you stopped an already stopped
   * stopwatch, this method returns <CODE>false</CODE>; otherwise
   * <CODE>true</CODE>.
   * 
   * @return true, if stop
   */
  public boolean stop() {
    if (running) {
      elapsedTime = meanTime();
      running = false;
      return true;
    }
    return false;
  }
}
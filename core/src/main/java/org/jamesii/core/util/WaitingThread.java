/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util;

import org.jamesii.SimSystem;

/**
 * A thread that waits for a specified amount of time and then executes a
 * method.
 * 
 * @author Stefan Leye
 * 
 */

public abstract class WaitingThread extends Thread {

  /**
   * The waiting time.
   */
  private long time;

  /**
   * Flag determining whether the thread should stop.
   */
  private boolean stopping = false;

  /**
   * Default constructor.
   * 
   * @param time
   *          the waiting time
   */
  public WaitingThread(long time) {
    this.time = time;
  }

  @Override
  public void run() {
    while (!stopping) {
      try {
        synchronized (this) {
          sleep(time);
        }
      } catch (InterruptedException e) {
        SimSystem.report(e);
      }
      if (!stopping) {
        execute();
      }
    }
  }

  public void stopThread() {
    stopping = true;
  }

  /**
   * This method is called after each waiting interval.
   */
  protected abstract void execute();

}

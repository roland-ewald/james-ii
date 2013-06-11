/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util;

import java.io.Serializable;

import org.jamesii.SimSystem;

/**
 * The Class Semaphore.
 * 
 * Instances of this class can be used to make sure that only a limited number
 * of concurrent class to a certain piece of code is executed. All others will
 * be blocked until they are informed of a free resource by someone calling the
 * {link v} method.
 * 
 * @author Jan Himmelspach
 */
public class Semaphore implements Serializable {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 7306202415057668120L;

  /** Holds the current state of the semaphore. */
  private int s = 0;

  /**
   * Creates a new Semaphore with the given initial value.
   * 
   * @param s0
   *          the value of the semaphore
   */
  public Semaphore(int s0) {
    if (s0 >= 0) {
      s = s0;
    }
  }

  /**
   * Try to enter the protected area - if that's possible decrement the
   * semaphore by one.
   */
  public synchronized void p() {
    while (s == 0) {
      try {
        wait();
      } catch (InterruptedException ie) {
        SimSystem.report(ie);
      }
    }
    s--;
  }

  /**
   * Leave the protected area and increment the semaphore by one - so that the
   * next one can enter the area.
   */
  public synchronized void v() {
    s++;
    notify();
  }

}

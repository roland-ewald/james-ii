/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.synchronization;

/**
 * Simple class to use simple object as locks.
 * 
 * @author Simon Bartels
 * 
 * @param <T>
 *          the objects type
 */
public class Lock<T> {
  /**
   * The value.
   */
  private T val;

  public Lock(T val) {
    this.val = val;
  }

  /**
   * Gets the value.
   * 
   * @return the value
   */
  public T getVal() {
    return val;
  }

  /**
   * Sets the value.
   * 
   * @param val
   *          the value
   */
  public void setVal(T val) {
    this.val = val;
  }

}

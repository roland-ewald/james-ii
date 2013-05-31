/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.observe;

import java.io.Serializable;

/**
 * The Class IntegerObject.
 * 
 * @author Björn Paul
 */
public class IntegerObject implements Serializable {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 189004767510356508L;

  /** The access counter. */
  private int accessCounter;

  /**
   * Instantiates a new integer object.
   */
  public IntegerObject() {
    accessCounter = 0;
  }

  /**
   * Adds the one.
   */
  public void addOne() {
    ++accessCounter;
  }

  /**
   * Gets the access counter.
   * 
   * @return the access counter
   */
  public int getAccessCounter() {
    return accessCounter;
  }
}

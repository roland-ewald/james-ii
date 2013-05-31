/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.model.variables;

/**
 * Aux class that defines what is needed to define the trajectory of a single
 * integer variable - the value changes (stored in data) that occur at certain
 * points in time (stored in time).
 * 
 * @author Roland Ewald
 */
public class IntegerTrajectory extends Trajectory {

  /** The data. */
  private final int[] data;

  /**
   * Instantiates a new integer trajectory.
   * 
   * @param d
   *          the d
   * @param t
   *          the t
   */
  public IntegerTrajectory(int[] d, double[] t) {
    super(t);
    this.data = d;
  }

  /**
   * Gets the data.
   * 
   * @return the data
   */
  public int[] getData() {
    return data;
  }

}

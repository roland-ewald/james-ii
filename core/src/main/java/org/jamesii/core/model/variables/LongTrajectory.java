/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.model.variables;

/**
 * Aux class that defines what is needed to define the trajectory of a single
 * long variable - the value changes (stored in data) that occur at certain
 * points in time (stored in time).
 * 
 * @author Jan Himmelspach
 */
public class LongTrajectory extends Trajectory {

  /** The data. */
  private final long[] data;

  /**
   * Instantiates a new integer trajectory.
   * 
   * @param d
   *          the d
   * @param t
   *          the t
   */
  public LongTrajectory(long[] d, double[] t) {
    super(t);
    this.data = d;

  }

  /**
   * Gets the data.
   * 
   * @return the data
   */
  public long[] getData() {
    return data;
  }

}

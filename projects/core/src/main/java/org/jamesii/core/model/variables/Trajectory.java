/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.model.variables;

/**
 * The Class Trajectory.
 * 
 * @author Jan Himmelspach
 */
public abstract class Trajectory {

  /** The times. */
  private final double[] times;

  /**
   * Instantiates a new trajectory.
   * 
   * @param t
   *          the t
   */
  public Trajectory(double[] t) {
    super();
    this.times = t;
  }

  /**
   * Gets the times.
   * 
   * @return the times
   */
  public double[] getTimes() {
    return times;
  }

}

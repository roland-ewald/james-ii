/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace;

import org.jamesii.core.util.ITime;

/**
 * Container for simulation time. Used for hybrid processor that is composed of
 * other processors that need to have the same simulation time (which is
 * realized by passing the same timer to their constructors).
 *
 * @author Arne Bittig
 * @date Mar 21, 2012
 */
public class Timer implements ITime<Double> {
  /** Current time of the simulation. */
  private Double currentTime = 0.;

  /**
   * @return Current time
   */
  @Override
  public Double getTime() {
    return currentTime;
  }

  /**
   * @param newTime
   *          Time to set
   */
  public void setTime(Double newTime) {
    this.currentTime = newTime;
  }
}
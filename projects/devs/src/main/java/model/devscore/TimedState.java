/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore;

import org.jamesii.core.model.State;

/**
 * The Class TimedState. This class is a special state which takes automatically
 * care of the time elapsed.
 * 
 * @author Jan Himmelspach
 */
public class TimedState extends State {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 1075121394407522945L;

  /** The last time. */
  private double lastTime = 0;

  /** The time advance. */
  private double timeAdvance = 0;

  /**
   * Returns the time of the next event.
   * 
   * @return the time of the next event which is evaluated through the lastTime
   *         + timeAdvance
   */
  public double getTime() {
    return lastTime + timeAdvance;
  }

  /**
   * Sets the lastTime value to the given value.
   * 
   * @param lastTime
   *          the new value of lastTime
   */
  public void setLastTime(double lastTime) {
    this.lastTime = lastTime;
    changed();
  }

  /**
   * Sets a new timeAdvance value.
   * 
   * @param timeAdvance
   *          the new time advance value
   */
  public void setTimeAdvance(double timeAdvance) {
    this.timeAdvance = timeAdvance;
    changed();
  }

}

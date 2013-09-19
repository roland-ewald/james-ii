/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.variables.modifier;

/**
 * Abstract class for increment modifier.
 * 
 * @author Jan Himmelspach
 */
public abstract class AbstractIncrementModifier<N extends Number> extends VariableModifier<N> {
  /**
   * The serialization uid.
   */
  private static final long serialVersionUID = 4671807372837079024L;

  /** Current value. */
  private N currentValue = null;

  /** Increment. */
  private N incrementBy = null;

  /** Start value. */
  private N startValue = null;

  /** Stop value. */
  private N stopValue = null;

  /**
   * Default constructor.
   * 
   * @param firstValue
   *          the start value
   * @param inc
   *          the increment
   * @param lastValue
   *          the last value (inclusive)
   */
  public AbstractIncrementModifier(N firstValue, N inc,
      N lastValue) {
    startValue = firstValue;
    incrementBy = inc;
    stopValue = lastValue;
    reset();
  }  


  /**
   * Gets the increment by.
   * 
   * @return the increment by
   */
  public N getIncrementBy() {
    return incrementBy;
  }

  /**
   * Sets the increment by.
   * 
   * @param incrementBy
   *          the new increment by
   */
  public void setIncrementBy(N incrementBy) {
    this.incrementBy = incrementBy;
    reset();
  }

  /**
   * Gets the start value.
   * 
   * @return the start value
   */
  public N getStartValue() {
    return startValue;
  }

  /**
   * Sets the start value.
   * 
   * @param startValue
   *          the new start value
   */
  public void setStartValue(N startValue) {
    this.startValue = startValue;
    reset();
  }

  /**
   * Gets the stop value.
   * 
   * @return the stop value
   */
  public N getStopValue() {
    return stopValue;
  }

  /**
   * Sets the stop value.
   * 
   * @param stopValue
   *          the new stop value
   */
  public void setStopValue(N stopValue) {
    this.stopValue = stopValue;
  }

  @Override
  public void reset() {
    if (startValue != null && incrementBy != null) {
      currentValue = getInitValue();
    }
  }  

  /**
   * Get the current value.
   * @return current value
   */
  protected N getCurrentValue() {
    return currentValue;
  }


  /**
   * Set the current value.
   * @param currentValue the new current value
   */
  protected void setCurrentValue(N currentValue) {
    this.currentValue = currentValue;
  }
  
  /**
   * Get the initial value of the current variable.
   * @return
   */
  protected abstract N getInitValue();

}

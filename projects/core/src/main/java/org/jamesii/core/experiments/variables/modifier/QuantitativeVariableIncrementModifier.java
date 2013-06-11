/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.variables.modifier;

import org.jamesii.core.experiments.variables.ExperimentVariables;
import org.jamesii.core.experiments.variables.NoNextVariableException;
import org.jamesii.core.model.variables.IQuantitativeVariable;
import org.jamesii.core.serialization.IConstructorParameterProvider;
import org.jamesii.core.serialization.SerialisationUtils;

/**
 * Modifies quantitative variables incrementally.
 * 
 * @author Roland Ewald
 * @param <X>
 * @param <V>
 *          Date: 24.05.2007
 */
public class QuantitativeVariableIncrementModifier<X extends Comparable<X>, V extends IQuantitativeVariable<X>>
    extends VariableModifier<V> {
  static {
    SerialisationUtils
        .addDelegateForConstructor(
            QuantitativeVariableIncrementModifier.class,
            new IConstructorParameterProvider<QuantitativeVariableIncrementModifier<?, ?>>() {
              @Override
              public Object[] getParameters(
                  QuantitativeVariableIncrementModifier<?, ?> modifier) {
                Object[] params =
                    new Object[] { modifier.getCurrentValue(),
                        modifier.getStartValue(), modifier.getIncrementBy(),
                        modifier.getStopValue() };
                return params;
              }
            });
  }

  /** Serialisation ID. */
  private static final long serialVersionUID = 1L;

  /** Current value. */
  private V currentValue = null;

  /** Increment. */
  private X incrementBy = null;

  /** Start value. */
  private X startValue = null;

  /** Stop value. */
  private X stopValue = null;

  /**
   * Default constructor.
   * 
   * @param currValue
   *          the current value
   * @param startVal
   *          the start value
   * @param inc
   *          the increment
   * @param stopVal
   *          the stop value
   */
  public QuantitativeVariableIncrementModifier(V currValue, X startVal, X inc,
      X stopVal) {
    super();
    currentValue = currValue;
    startValue = startVal;
    incrementBy = inc;
    stopValue = stopVal;
    reset();
  }

  @Override
  public V next(ExperimentVariables variables) throws NoNextVariableException {
    currentValue.modify(1);
    if (currentValue.getValue().compareTo(stopValue) > 0) {
      throw new NoNextVariableException();
    }
    return currentValue;
  }

  @Override
  public void reset() {
    if (currentValue != null && startValue != null && incrementBy != null) {
      currentValue.setValue(startValue);
      currentValue.setStepWidth(incrementBy);
      currentValue.modify(-1);
    }
  }

  /**
   * Gets the current value.
   * 
   * @return the current value
   */
  public V getCurrentValue() {
    return currentValue;
  }

  /**
   * Sets the current value.
   * 
   * @param currentValue
   *          the new current value
   */
  public void setCurrentValue(V currentValue) {
    this.currentValue = currentValue;
    reset();
  }

  /**
   * Gets the increment by.
   * 
   * @return the increment by
   */
  public X getIncrementBy() {
    return incrementBy;
  }

  /**
   * Sets the increment by.
   * 
   * @param incrementBy
   *          the new increment by
   */
  public void setIncrementBy(X incrementBy) {
    this.incrementBy = incrementBy;
    reset();
  }

  /**
   * Gets the start value.
   * 
   * @return the start value
   */
  public X getStartValue() {
    return startValue;
  }

  /**
   * Sets the start value.
   * 
   * @param startValue
   *          the new start value
   */
  public void setStartValue(X startValue) {
    this.startValue = startValue;
    reset();
  }

  /**
   * Gets the stop value.
   * 
   * @return the stop value
   */
  public X getStopValue() {
    return stopValue;
  }

  /**
   * Sets the stop value.
   * 
   * @param stopValue
   *          the new stop value
   */
  public void setStopValue(X stopValue) {
    this.stopValue = stopValue;
  }

}

/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.model.variables;

import org.jamesii.SimSystem;

// TODO: Auto-generated Javadoc
/**
 * Quantitative variables are either based on an interval scale (e.g.
 * temperature) or on a ratio scale (e.g. age).
 * 
 * @param <T>
 *          Type of the variable, has to be comparable
 * 
 * @author Jan Himmelspach
 */
public abstract class QuantitativeBaseVariable<T extends Comparable<T>> extends
    BaseVariable<T> implements IQuantitativeVariable<T> {

  /** Serialisation ID. */
  private static final long serialVersionUID = -421070521905803259L;

  /** Lower bound of this variable. */
  private T lowerBound = null;

  /**
   * Internal flag indicating whether this variable has an absolute base or not.
   */
  private boolean ratio = false;

  /** Stepwidth. */
  private T stepWidth = null;

  /** Upper bound of this variable. */
  private T upperBound = null;

  /**
   * Instantiates a new quantitative variable.
   * 
   * @param name
   *          the name
   * @param ratio
   *          the ratio
   * @param sw
   *          the sw
   */
  public QuantitativeBaseVariable(String name, boolean ratio, T sw) {
    super(name);
    this.ratio = ratio;
    this.stepWidth = sw;
  }

  @Override
  @SuppressWarnings("unchecked")
  public BaseVariable<T> copyVariable() {
    QuantitativeBaseVariable<T> newVar = null;
    try {
      newVar = getClass().newInstance();
      newVar.setName(getName());
      newVar.setValue(getValue());
      newVar.setStepWidth(getStepWidth());
      newVar.setLowerBound(getLowerBound());
      newVar.setUpperBound(getUpperBound());
    } catch (Exception ex) {
      SimSystem.report(ex);
    }
    return newVar;
  }

  /**
   * Gets the lower bound.
   * 
   * @return the lower bound
   * 
   * @see org.jamesii.core.model.variables.IQuantitativeVariable#getLowerBound()
   */
  @Override
  public T getLowerBound() {
    return lowerBound;
  }

  /**
   * Gets the step width.
   * 
   * @return the step width
   * 
   * @see org.jamesii.core.model.variables.IQuantitativeVariable#getStepWidth()
   */
  @Override
  public T getStepWidth() {
    return stepWidth;
  }

  /**
   * Gets the upper bound.
   * 
   * @return the upper bound
   * 
   * @see org.jamesii.core.model.variables.IQuantitativeVariable#getUpperBound()
   */
  @Override
  public T getUpperBound() {
    return upperBound;
  }

  @Override
  public boolean isRatio() {
    return ratio;
  }

  /**
   * Modify.
   * 
   * @param factor
   *          the factor
   * 
   * @return true, if modify
   * 
   * @see org.jamesii.core.model.variables.IQuantitativeVariable#modify(double)
   */
  @Override
  public boolean modify(double factor) {

    if (stepWidth == null) {
      return false;
    }

    T result = modifyVariable(factor);

    if (violatesBounds(result)) {
      return false;
    }

    setValue(result);
    return true;
  }

  /**
   * Modifies variable.
   * 
   * @param factor
   *          factor by which the stepwith should be scaled
   * 
   * @return the T
   */
  protected abstract T modifyVariable(double factor);

  @Override
  public void setLowerBound(T lowBound) {
    lowerBound = lowBound;
  }

  @Override
  public void setStepWidth(T sw) {
    stepWidth = sw;
  }

  @Override
  public void setUpperBound(T upBound) {
    upperBound = upBound;
  }

  /**
   * Violates bounds.
   * 
   * @param val
   *          value this variable should be set to
   * 
   * @return true, if this value is not allowed, ie. if it violates the bounds
   */
  protected boolean violatesBounds(T val) {
    if ((lowerBound != null && lowerBound.compareTo(val) > 0)
        || (upperBound != null && upperBound.compareTo(val) < 0)) {
      return true;
    }
    return false;
  }

  /**
   * Sets the random value.
   * 
   * @see org.jamesii.core.model.variables.IQuantitativeVariable#setRandomValue()
   */
  @Override
  public abstract void setRandomValue();
}

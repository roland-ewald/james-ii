/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.editable.constraints;

import org.jamesii.core.util.IConstraint;

/**
 * Checks constraints concerning numbers within an interval [lowerBound,
 * upperBound].
 * 
 * Created: 23.05.2004
 * 
 * @author Roland Ewald
 */

public class IntervalConstraint implements IConstraint<Number> {

  /** Serialisation ID. */
  private static final long serialVersionUID = 5989852477319294355L;

  /** Lower bound. */
  private Double lowerBound = null;

  /** True if upper bound is exclusive. */
  private boolean maxExclusive = false;

  /** True if lower bound is exclusive. */
  private boolean minExclusive = false;

  /** Upper bound. */
  private Double upperBound = null;

  @Override
  public IConstraint<Number> getCopy() {
    IntervalConstraint iConstr = new IntervalConstraint();
    iConstr.setLowerBound(getLowerBound());
    iConstr.setUpperBound(getUpperBound());
    iConstr.setMaxExclusive(isMaxExclusive());
    iConstr.setMinExclusive(isMinExclusive());
    return iConstr;
  }

  /**
   * Gets the lower bound.
   * 
   * @return the lower bound
   */
  public double getLowerBound() {
    return lowerBound;
  }

  /**
   * Gets the upper bound.
   * 
   * @return the upper bound
   */
  public double getUpperBound() {
    return upperBound;
  }

  /**
   * Checks if is fulfilled.
   * 
   * @param number
   *          the number
   * 
   * @return true, if checks if is fulfilled
   * 
   * @see org.jamesii.core.util.IConstraint#isFulfilled(java.lang.Object)
   */
  @Override
  public boolean isFulfilled(Number number) {

    double tryValue = number.doubleValue();

    // test for all activated rules
    if ((lowerBound != null) && (tryValue <= lowerBound)
        && (minExclusive || tryValue < lowerBound)) {
      return false;
    }

    if ((upperBound != null) && (tryValue >= upperBound)
        && (maxExclusive || tryValue > upperBound)) {
      return false;
    }

    return true;
  }

  /**
   * Checks if is max exclusive.
   * 
   * @return true, if is max exclusive
   */
  public boolean isMaxExclusive() {
    return maxExclusive;
  }

  /**
   * Checks if is min exclusive.
   * 
   * @return true, if is min exclusive
   */
  public boolean isMinExclusive() {
    return minExclusive;
  }

  /**
   * Sets the lower bound.
   * 
   * @param lBound
   *          the new lower bound
   */
  public void setLowerBound(double lBound) {
    lowerBound = lBound;
  }

  /**
   * Sets the max exclusive.
   * 
   * @param maxEx
   *          the new max exclusive
   */
  public void setMaxExclusive(boolean maxEx) {
    maxExclusive = maxEx;
  }

  /**
   * Sets the min exclusive.
   * 
   * @param minEx
   *          the new min exclusive
   */
  public void setMinExclusive(boolean minEx) {
    minExclusive = minEx;
  }

  /**
   * Sets the upper bound.
   * 
   * @param uBound
   *          the new upper bound
   */
  public void setUpperBound(double uBound) {
    upperBound = uBound;
  }

}

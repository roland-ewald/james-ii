/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.model.variables;

/**
 * Interface for quantitative variables. These variables can either be based on
 * intervals or on ratios. If the latter is true the variable has an absolute
 * base.
 * 
 * @author Jan Himmelspach *
 * @param <T>
 */
public interface IQuantitativeVariable<T extends Comparable<T>> extends
    IVariable<T> {

  /**
   * @return lower bound of this variable
   */
  T getLowerBound();

  /**
   * @return step width
   */
  T getStepWidth();

  /**
   * @return upper bound of this variable
   */
  T getUpperBound();

  /**
   * Returns true if the variable has an absolute base (e.g. age, weight).
   * 
   * @return true if the variable has an absolute base
   */
  boolean isRatio();

  /**
   * Modifies the variable
   * 
   * @param factor
   * @return true, if modification was successful, i.e. no bounds prevented the
   *         value from being set and the value did change (ie. step width was
   *         defined)
   */
  boolean modify(double factor);

  /**
   * @param lowBound
   *          lower bound to be set
   */
  void setLowerBound(T lowBound);

  /**
   * Set step width
   * 
   * @param stepWidth
   */
  void setStepWidth(T stepWidth);

  /**
   * @param upBound
   *          upper bound to be set
   */
  void setUpperBound(T upBound);

  /**
   * Sets a Random Value with uniformly distributed probabilities between lower
   * and upper bound.
   */
  void setRandomValue();
}

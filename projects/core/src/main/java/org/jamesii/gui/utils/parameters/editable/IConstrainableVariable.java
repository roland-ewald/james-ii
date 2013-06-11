/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.editable;

import java.util.List;

import org.jamesii.core.model.variables.IVariable;
import org.jamesii.core.util.IConstraint;

/**
 * All functions a constrainable variable has. Immediate verification is nice in
 * a UI, but has sometimes to be switched off (eg., minOccurence constraints
 * should only be checked when the user is ready, not before).
 * 
 * @param <V>
 *          the type of the variable to be constrained
 * 
 * @author Roland Ewald
 */
public interface IConstrainableVariable<V> extends IVariable<V> {

  /**
   * Adds constraint for this parameter.
   * 
   * @param constraint
   *          new constraint
   */
  void addConstraint(IConstraint<V> constraint);

  /**
   * Invokes 'lazy verification' for the parameter.
   * 
   * @return true if all constraints are fulfilled
   */
  boolean check();

  /**
   * Checks value for constraints.
   * 
   * @param value
   *          value to be checked
   * 
   * @return true if value fulfils all constraints
   */
  boolean check(V value);

  /**
   * Returns checking strategy.
   * 
   * @return true, if parameter checks the constraints of his value as soon as a
   *         new value is set, otherwise false
   */
  boolean checkImmediately();

  /**
   * Get constraints for this parameter.
   * 
   * @return list of constraints
   */
  List<IConstraint<V>> getConstraints();

  /**
   * Remove constraint for this parameter.
   * 
   * @param constraint
   *          constraint to be removed
   */
  void removeConstraint(IConstraint<V> constraint);

  /**
   * Sets the checking behaviour of a parameter. If true, the parameter checks
   * every newly assigned value as it is set, otherwise false. If not, check()
   * has to be called later.
   * 
   * @param immediateCheck
   *          flag to set checking strategy
   */
  void setCheckImmediately(boolean immediateCheck);

}

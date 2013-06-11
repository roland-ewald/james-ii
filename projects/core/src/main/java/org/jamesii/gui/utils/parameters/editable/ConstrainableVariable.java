/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.editable;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.base.NamedEntity;
import org.jamesii.core.model.variables.IVariable;
import org.jamesii.core.util.IConstraint;

/**
 * Implementation of a constrained variable.
 * 
 * @param <V>
 *          the type of the variable
 * @author Roland Ewald
 * 
 * 
 */
public class ConstrainableVariable<V> extends NamedEntity implements
    IConstrainableVariable<V> {

  /** Serialisation ID. */
  private static final long serialVersionUID = -7373758537433387065L;

  /** Flag that determines checking strategy. */
  private boolean checkImmediately = false;

  /** List of constraints for variable. */
  private List<IConstraint<V>> constraints = new ArrayList<>();

  /** Reference to constrained variable. */
  private transient IVariable<V> variable = null;

  /**
   * Default constructor.
   * 
   * @param var
   *          variable to be constrained
   */
  public ConstrainableVariable(IVariable<V> var) {
    variable = var;
  }

  /**
   * Adds the constraint.
   * 
   * @param constraint
   *          the constraint
   * 
   * @see org.jamesii.gui.utils.parameters.editable.IConstrainableVariable#addConstraint(org.jamesii.core.util.IConstraint)
   */
  @Override
  public void addConstraint(IConstraint<V> constraint) {
    constraints.add(constraint);
  }

  /**
   * Check.
   * 
   * @return true, if check
   * 
   * @see org.jamesii.gui.utils.parameters.editable.IConstrainableVariable#check()
   */
  @Override
  public boolean check() {
    return check(getValue());
  }

  /**
   * Check.
   * 
   * @param value
   *          the value
   * 
   * @return true, if check
   * 
   * @see org.jamesii.gui.utils.parameters.editable.IConstrainableVariable#check(java.lang.Object)
   */
  @Override
  public boolean check(V value) {
    for (IConstraint<V> constraint : constraints) {
      if (!constraint.isFulfilled(value)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Check immediately.
   * 
   * @return true, if check immediately
   * 
   * @see org.jamesii.gui.utils.parameters.editable.IConstrainableVariable#checkImmediately()
   */
  @Override
  public boolean checkImmediately() {
    return checkImmediately;
  }

  /**
   * Gets the constraints.
   * 
   * @return the constraints
   * 
   * @see org.jamesii.gui.utils.parameters.editable.IConstrainableVariable#getConstraints()
   */
  @Override
  public List<IConstraint<V>> getConstraints() {
    return constraints;
  }

  /**
   * Gets the value.
   * 
   * @return the value
   * 
   * @see org.jamesii.core.model.variables.IVariable#getValue()
   */
  @Override
  public V getValue() {
    return variable.getValue();
  }

  /**
   * Gets the variable.
   * 
   * @return the variable
   */
  public IVariable<V> getVariable() {
    return variable;
  }

  /**
   * Removes the constraint.
   * 
   * @param constraint
   *          the constraint
   * 
   * @see org.jamesii.gui.utils.parameters.editable.IConstrainableVariable#removeConstraint(org.jamesii.core.util.IConstraint)
   */
  @Override
  public void removeConstraint(IConstraint<V> constraint) {
    constraints.remove(constraint);
  }

  /**
   * Sets the check immediately.
   * 
   * @param immediateCheck
   *          the immediate check
   * 
   * @see org.jamesii.gui.utils.parameters.editable.IConstrainableVariable#setCheckImmediately(boolean)
   */
  @Override
  public void setCheckImmediately(boolean immediateCheck) {
    checkImmediately = immediateCheck;
  }

  /**
   * Sets the value.
   * 
   * @param value
   *          the value
   * 
   * @see org.jamesii.core.model.variables.IVariable#setValue(java.lang.Object)
   */
  @Override
  public void setValue(V value) {
    if (checkImmediately && check()) {
      variable.setValue(value);
    }
  }

  /**
   * Sets the variable.
   * 
   * @param var
   *          the new variable
   */
  public void setVariable(IVariable<V> var) {
    this.variable = var;
  }

}

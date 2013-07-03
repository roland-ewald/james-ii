/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.model;

import org.jamesii.SimSystem;
import org.jamesii.core.base.Entity;

/**
 * The Class AbstractState is a simple, and abstract implementation of a state
 * of a model. It just adds convenience methods for update propagation.
 * 
 * @author Jan Himmelspach
 */
public abstract class AbstractState extends Entity implements Cloneable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -168148914597943702L;

  @Override
  public Object clone() throws CloneNotSupportedException {
    Object o = null;
    try {
      o = super.clone();
    } catch (CloneNotSupportedException cnse) {
      SimSystem.report(cnse);
    }
    return o;
  }

  /**
   * Returns true if this state has been changed after the last call to
   * resetChanged or false if not. Please note that a change must be indicated
   * by a call to the changed method (otherwise this method will always return
   * false).
   * 
   * @return true, if checks for changed
   */
  public abstract boolean hasChanged();

  /**
   * Combination of isChanged(), resetChanged and super.changed().
   * 
   * @return true, if the state has been changed
   */
  public abstract boolean isChangedRR();

  /**
   * This method sets the changed flag to false, i.e., the state is said to be
   * unchanged.
   */
  public abstract void clearChanged();
}

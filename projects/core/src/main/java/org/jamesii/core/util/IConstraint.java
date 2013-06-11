/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util;

import java.io.Serializable;

/**
 * Basic interface of a constraint. Can be used in various situations, e.g., for
 * optimization and experiment setup.
 * 
 * @author Roland Ewald
 * @param <V>
 *          created 04.07.2007
 */
public interface IConstraint<V> extends Serializable {

  /**
   * Get copy of this constraint.
   * 
   * @return copy of constraint
   */
  IConstraint<V> getCopy();

  /**
   * Tests whether constraint is fulfilled.
   * 
   * @param value
   *          value to be tested
   * 
   * @return true, if constraint is fulfilled, and false otherwise
   */
  boolean isFulfilled(V value);
}

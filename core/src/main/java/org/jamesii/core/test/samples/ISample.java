/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.test.samples;

import java.io.Serializable;

/**
 * Standard interface for samples produced by a simulation and used for
 * validation.
 * 
 * @author Stefan Leye
 * 
 * @param <V>
 *          the type of the data
 */

public interface ISample<V> extends Serializable {

  /**
   * Returns the list of (unique) variable name associated to this sample.
   * 
   * @return variable name
   */
  String getVariableName();

  /**
   * Returns the data of this sample.
   * 
   * @return data
   */
  V getData();

  /**
   * Get class of variable values. Convenience method, as Generics cannot be
   * queried at runtime.
   * 
   * @return class of variable values
   */
  Class<?> getVariableValueClass();

}

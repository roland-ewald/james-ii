/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.cmdparameters;

import java.util.List;

/**
 * General interface for classed which can be parameterised.
 * 
 * @author Jan Himmelspach
 * 
 */
public interface IParameters {

  /**
   * Get the list of optional parameters (included in the list returned by
   * getParameters).
   * 
   * @return list of optional parameters
   */
  List<Parameter> getOptionalParameters();

  /**
   * Get the complete list of available parameters.
   * 
   * @return complete list of available parameters
   */
  List<Parameter> getParameters();

  /**
   * Get the list of required parameters (included in the list returned by
   * getParameters).
   * 
   * @return list of required parameters
   */
  List<Parameter> getRequiredParameters();

  /**
   * Checks whether there are any parameters or not.
   * 
   * @return true if there are
   */
  boolean hasParameters();

}

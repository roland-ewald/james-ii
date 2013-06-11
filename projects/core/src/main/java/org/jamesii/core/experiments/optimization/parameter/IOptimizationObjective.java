/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization.parameter;

import java.util.Map;

import org.jamesii.core.model.variables.BaseVariable;

/**
 * Base interface to support definition of the objective function.
 * 
 * @author Arvid Schwecke
 */
public interface IOptimizationObjective {

  /**
   * The name of the objective
   * 
   * @return the name
   */
  String getName();

  /**
   * Calculates objectives for given factors and responses.
   * 
   * @param configuration
   *          the configuration
   * @param response
   *          the response
   * 
   * @return the double
   */
  double calcObjective(Configuration configuration,
      Map<String, BaseVariable<?>> response);

}

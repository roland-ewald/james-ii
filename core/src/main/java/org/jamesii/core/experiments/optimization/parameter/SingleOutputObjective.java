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
 * Simple objective function that picks a certain model output as objective.
 * 
 * @author Roland Ewald Date: 18.05.2007
 */
public class SingleOutputObjective implements IOptimizationObjective {

  /** Name of the output variable to be optimised. */
  private String nameOfOutput;

  /**
   * Default constructor.
   * 
   * @param output
   *          name of the output variable
   */
  public SingleOutputObjective(String name) {
    this.nameOfOutput = name;
  }

  @Override
  public String getName() {
    return nameOfOutput;
  }

  /**
   * Calculate objective.
   * 
   * @param configuration
   *          the parameter configuration
   * @param response
   *          the response
   * 
   * @return the objective
   * 
   * @see org.jamesii.core.experiments.optimization.parameter.IOptimizationObjective#calcObjective(org.jamesii.core.experiments.optimization.parameter.Configuration,
   *      java.util.HashMap)
   */
  @Override
  public double calcObjective(Configuration configuration,
      Map<String, BaseVariable<?>> response) {
    BaseVariable<?> variable = response.get(nameOfOutput);
    if (variable == null) {
      throw new RuntimeException("The response parameter '" + nameOfOutput
          + "' cannot be found!");
    }
    return new Double(variable.getValue().toString());
  }
}

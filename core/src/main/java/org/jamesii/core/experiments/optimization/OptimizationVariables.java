/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization;

import org.jamesii.core.experiments.steering.SteeredExperimentVariables;
import org.jamesii.core.experiments.variables.ExperimentVariables;

/**
 * Contains one optimiser, given by {@link OptimizerVariable}, and the factors.
 * On {@link ExperimentVariables#nextThisLevel(ExperimentVariables)}, a new
 * configuration is retrieved from the optimiser.
 * 
 * @author Arvid Schwecke
 * @author Roland Ewald
 */
public class OptimizationVariables extends
    SteeredExperimentVariables<Optimizer> {

  /** Serialisation ID. */
  private static final long serialVersionUID = -6947363027907783961L;

  /**
   * Instantiates a new optimisation variables.
   */
  public OptimizationVariables() {
    super(Optimizer.class);
  }

}

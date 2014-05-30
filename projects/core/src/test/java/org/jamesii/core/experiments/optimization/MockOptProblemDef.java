/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization;

import org.jamesii.core.experiments.optimization.parameter.OptimizationProblemDefinition;
import org.jamesii.core.experiments.optimization.parameter.instrumenter.IResponseObsModelInstrumenter;
import org.jamesii.core.experiments.optimization.parameter.instrumenter.IResponseObsSimInstrumenter;

/**
 * Mock for {@link OptimizationProblemDefinition}.
 * 
 * @author Roland Ewald
 */
public class MockOptProblemDef extends OptimizationProblemDefinition {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -5069515319493028835L;

  @Override
  public IResponseObsModelInstrumenter getModelInstrumenter() {
    return null;
  }

  @Override
  public IResponseObsSimInstrumenter getSimulationInstrumenter() {
    return null;
  }

}

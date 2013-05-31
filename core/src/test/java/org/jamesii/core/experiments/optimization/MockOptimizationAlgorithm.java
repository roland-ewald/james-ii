/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization;

import java.io.Serializable;
import java.util.Map;

import org.jamesii.core.experiments.optimization.algorithm.IOptimizationAlgorithm;
import org.jamesii.core.experiments.optimization.parameter.Configuration;
import org.jamesii.core.experiments.variables.NoNextVariableException;

/**
 * This is a mocked optimizer for testing the steerers.
 * 
 * @author Roland Ewald
 * 
 */
public class MockOptimizationAlgorithm implements IOptimizationAlgorithm,
    Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 6159968862607492358L;

  @Override
  public Configuration getNextConfiguration() throws NoNextVariableException {
    return null;
  }

  @Override
  public void addResults(Configuration config, Map<String, Double> results,
      Map<Configuration, Map<String, Double>> currentParetoFront) {

  }
}

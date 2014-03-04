/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.portfolios.ga.abort;

import org.jamesii.asf.portfolios.ga.GeneticAlgorithmPortfolioSelector;

/**
 * Abort, when a specific fitness is reached.
 * 
 * @author René Schulz, Roland Ewald
 * 
 */
public class MaxFitnessAbort implements IAbortCriterion {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1434229343946344358L;

  /** The maximum fitness. */
  private final double maxFitness;

  /**
   * Instantiates a new max fitness abort.
   * 
   * @param maximalCount
   *          the maximal count
   */
  public MaxFitnessAbort(double maximalFitness) {
    maxFitness = maximalFitness;
  }

  @Override
  public boolean abort(GeneticAlgorithmPortfolioSelector selector) {
    return selector.getBestFitness() >= maxFitness;
  }

}

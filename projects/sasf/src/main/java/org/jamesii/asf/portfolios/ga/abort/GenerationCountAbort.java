/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.portfolios.ga.abort;

import org.jamesii.asf.portfolios.ga.GeneticAlgorithmPortfolioSelector;

/**
 * Counts number of generations.
 * 
 * @author René Schulz, Roland Ewald
 * 
 */
public class GenerationCountAbort implements IAbortCriterion {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1428248479499777488L;

  /** The max count. */
  private final int maxCount;

  /**
   * Instantiates a new generation count abort.
   * 
   * @param maximalCount
   *          the maximal count
   */
  public GenerationCountAbort(int maximalCount) {
    maxCount = maximalCount;
  }

  @Override
  public boolean abort(GeneticAlgorithmPortfolioSelector selector) {
    return selector.getGenerationCount() >= maxCount;
  }

}

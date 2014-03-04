/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.portfolios.ga;

import org.jamesii.asf.portfolios.ga.fitness.IPortfolioFitness;
import org.jamesii.asf.portfolios.plugintype.PortfolioProblemDescription;

/**
 * Factory for {@link GenePool}.
 * 
 * @author René Schulz, Roland Ewald
 * 
 */
public class GenePoolFactory extends AbstractGenePoolFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -1147313327116657418L;

  @Override
  public IGenePool create(PortfolioProblemDescription problem,
      int numIndividuals, IPortfolioFitness fitness, double mutationRate,
      AbstractIndividualFactory individualFactory) {
    return new GenePool(problem, numIndividuals, fitness, mutationRate,
        individualFactory);
  }

}

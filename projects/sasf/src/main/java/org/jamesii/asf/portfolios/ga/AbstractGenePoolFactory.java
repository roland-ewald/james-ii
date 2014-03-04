/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.portfolios.ga;

import java.io.Serializable;

import org.jamesii.asf.portfolios.ga.fitness.IPortfolioFitness;
import org.jamesii.asf.portfolios.plugintype.PortfolioProblemDescription;


/**
 * Abstract factory for {@link IGenePool}.
 * 
 * @author René Schulz, Roland Ewald
 * 
 */
public abstract class AbstractGenePoolFactory implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -4282147860003560104L;

  /**
   * Creates a genepool with the given parameters.
   * 
   * @param problem
   *          the problem
   * @param numIndividuals
   *          the num individuals
   * @param fitness
   *          the fitness
   * @param mutationRate
   *          the mutation rate
   * @param individualFactory
   *          the individual factory
   * @return the i gene pool
   */
  public abstract IGenePool create(PortfolioProblemDescription problem,
      int numIndividuals, IPortfolioFitness fitness, double mutationRate,
      AbstractIndividualFactory individualFactory);

}

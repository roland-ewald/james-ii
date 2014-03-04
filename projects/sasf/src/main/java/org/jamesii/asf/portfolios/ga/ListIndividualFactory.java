/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.portfolios.ga;

import org.jamesii.core.math.random.generators.IRandom;

/**
 * Factory for {@Link ListIndividual}.
 * 
 * @author René Schulz, Roland Ewald
 * 
 */
public class ListIndividualFactory extends AbstractIndividualFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 2555010927864379219L;

  @Override
  public IIndividual createRandomIndividual(IRandom rng, int length,
      int portfolioSize) {
    ListIndividual ind = new ListIndividual(null, length, rng);
    ind.generateRandomGenome(portfolioSize, length);
    return ind;
  }
}

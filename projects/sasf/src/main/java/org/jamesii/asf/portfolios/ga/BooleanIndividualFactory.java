/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.portfolios.ga;

import org.jamesii.core.math.random.generators.IRandom;

/**
 * Factory for {@link BooleanIndividual}.
 * 
 * @author René Schulz, Roland Ewald
 * 
 */
public class BooleanIndividualFactory extends AbstractIndividualFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 8843833375154646660L;

  @Override
  public IIndividual createRandomIndividual(IRandom rng, int length,
      int portfolioSize) {
    BooleanIndividual ind = new BooleanIndividual(null, rng);
    ind.generateRandomGenome(length, portfolioSize);
    return ind;
  }

}

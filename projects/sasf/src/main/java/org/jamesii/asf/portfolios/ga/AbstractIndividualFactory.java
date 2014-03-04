/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.portfolios.ga;


import java.io.Serializable;

import org.jamesii.core.math.random.generators.IRandom;

/**
 * Abstract factory for {@link IIndividual}.
 * 
 * @author René Schulz, Roland Ewald
 * 
 */
public abstract class AbstractIndividualFactory implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 5253608838888406117L;

  /**
   * Creates a random individual.
   * 
   * @param rng
   *          the rng to be used
   * @param length
   *          the length of the individual's genome
   * @param portfolioSize
   *          the portfolio size
   * @return the individual
   */
  public abstract IIndividual createRandomIndividual(IRandom rng, int length,
      int portfolioSize);
}

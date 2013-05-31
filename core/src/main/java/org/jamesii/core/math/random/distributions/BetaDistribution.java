/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.distributions;

import org.jamesii.core.math.random.generators.IRandom;

/**
 * Beta distribution. Integer shape values.
 * 
 * for other shape values the code from "R. C. H. Cheng (1978). Generating beta
 * variates with nonintegral shape parameters. Communications of the ACM 21,
 * 317-322. (Algorithms BB and BC)" has to be implemented!
 * 
 * @author Jan Himmelspach
 */
public class BetaDistribution extends AbstractDistribution {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -6206156315748568528L;

  /** The alpha. */
  private int alpha = 1;

  /** The beta. */
  private int beta = 1;

  /**
   * Instantiates a new beta distribution.
   * 
   * @param seed
   *          the seed
   */
  public BetaDistribution(Long seed) {
    super(seed);
  }

  /**
   * Instantiates a new beta distribution.
   * 
   * @param randomizer
   *          the randomizer
   */
  public BetaDistribution(IRandom randomizer) {
    super(randomizer);
  }

  /**
   * Instantiates a new beta distribution.
   * 
   * @param randomizer
   *          the randomizer
   * @param alpha
   *          the alpha
   * @param beta
   *          the beta
   */
  public BetaDistribution(IRandom randomizer, int alpha, int beta) {
    super(randomizer);
    this.alpha = alpha;
    this.beta = beta;
  }

  @Override
  public double getRandomNumber() {
    double v = GammaDistribution.getGamma(getRandom(), alpha);
    double om = GammaDistribution.getGamma(getRandom(), beta);
    // System.out.println(v / (v + om));
    return v / (v + om);
  }

  @Override
  public AbstractDistribution getSimilar(IRandom newRandomizer) {
    return new BetaDistribution(newRandomizer, alpha, beta);
  }

}

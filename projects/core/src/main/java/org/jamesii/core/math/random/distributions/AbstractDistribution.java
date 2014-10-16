/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.distributions;

import java.io.Serializable;

import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.math.random.generators.java.JavaRandom;

/**
 * Super class to generalise random number distributions.
 * 
 * @author Roland Ewald
 */
public abstract class AbstractDistribution implements IDistribution,
    Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -9021975782781687783L;

  /** Reference to random number generator. */
  private IRandom random;

  /**
   * Constructor which specifies the number generator to be used.
   * 
   * @param seed
   *          the seed for an auto generated random number generator
   */
  public AbstractDistribution(long seed) {
    this.random = new JavaRandom(seed);
  }

  /**
   * Constructor which specifies the number generator to be used.
   * 
   * @param randomizer
   *          the random number generator
   */
  public AbstractDistribution(IRandom randomizer) {
    this.random = randomizer;
  }

  @Override
  public abstract double getRandomNumber();

  /**
   * Gets a new distribution object that uses the same parameters and the same
   * randomiser object (needed to duplicate distributions without having to
   * create new randomisers).
   * <p>
   * <strong>NOTE:</strong> if this method is used for initialising random
   * distributions for stuff which gets computed concurrently by several threads
   * (in one JVM) the repeatability of the random number is not guaranteed and
   * thus a simulation run is not repeatable!
   * 
   * @return a copy of the distribution
   * @see #getSimilar(IRandom)
   * @see #getRandom()
   */
   @Deprecated
  public AbstractDistribution getSimilar() {
    return getSimilar(this.random);
  }

  /**
   * Gets a new distribution object that uses the same parameters and the same
   * randomiser object (needed to duplicate distributions).
   * 
   * @param newRandomizer
   *          the new randomiser
   * 
   * @return the similar
   */
  @Override
  public abstract AbstractDistribution getSimilar(IRandom newRandomizer);

  @Override
  public void setRandom(IRandom random) {
    this.random = random;
  }

  @Override
  public IRandom getRandom() {
    return random;
  }
}

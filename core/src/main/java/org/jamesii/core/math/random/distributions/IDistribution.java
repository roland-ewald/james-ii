/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.distributions;

import org.jamesii.core.math.random.generators.IRandom;

/**
 * This is the general interface all random number distributions have to
 * implement. A distribution relies on an instance of {@link IRandom} that
 * generates uniformly distributed pseudo-random number for the interval [0,1).
 * 
 * @author Jan Himmelspach
 * @author Roland Ewald
 */
public interface IDistribution {

  /**
   * Generate random number.
   * 
   * @return generated random number
   */
  double getRandomNumber();

  /**
   * Set randomiser.
   * 
   * @param random
   *          the random
   */
  void setRandom(IRandom random);

  /**
   * Get randomiser.
   * 
   * @return the randomiser
   */
  IRandom getRandom();

  /**
   * Gets a new distribution object that generates similar pseudo-random
   * numbers, i.e. number are drawn according to the same underlying probability
   * distribution (and the same parameters), but now by using the given
   * randomiser.
   * 
   * @param random
   *          the randomiser to be used by the newly created instance
   * 
   * @return the similar
   */
  IDistribution getSimilar(IRandom random);
}

/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.rnggenerator;

import java.io.Serializable;

import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.math.random.generators.plugintype.RandomGeneratorFactory;
import org.jamesii.core.parameters.ParameterizedFactory;

/**
 * Generator to centralize the initialization of random number generators. It is
 * used as a singleton in the simulation system and will allow to control all
 * mechanisms that use a random number generator.
 * 
 * The idea is that we use a random number generator to generate seeds and
 * parameters for certain new random number generators, so it's support for
 * parallel random number generators. If we use the same seed and parameters
 * everywhere, we may get correlations (e.g., miraculously synchronized models
 * with stochastic events). If we use no centralized scheme, there will be
 * mayhem when we have to find a 'heisenbug' in a stochastic algorithm or model. <br/>
 * WATCH OUT: This won't be a silver bullet for multi-threaded applications, if
 * you create a new Thread you will have to take a new RNG generator from the
 * system with you (which may then be used for generating multiple RNGs again).
 * 
 * @author Roland Ewald
 */
public interface IRNGGenerator extends Serializable {

  /**
   * Parameter for setting a concrete
   * {@link org.jamesii.core.math.random.generators.plugintype.RandomGeneratorFactory}
   * for the generator.
   */
  String RNG_FACTORY = "RNGFactory";

  /** Parameter for setting the initial seed. */
  String RNG_INIT_SEED = "RNGInitialSeed";

  /**
   * Get the next random number generator.
   * 
   * @return the next rng
   */
  IRandom getNextRNG();

  /**
   * Set new seed (which was, e. g. transmitted from the master server's rand
   * seed generator to control the seeds of all simulation servers).
   * 
   * @param seed
   *          the seed
   */
  void setSeed(long seed);

  /**
   * Gets the RNG factory.
   * 
   * @return the RNG factory
   */
  ParameterizedFactory<RandomGeneratorFactory> getRNGFactory();

  /**
   * Sets the RNG factory.
   * 
   * @param rngFactory
   *          the new RNG factory to be used
   */
  void setRNGFactory(ParameterizedFactory<RandomGeneratorFactory> rngFactory);
}

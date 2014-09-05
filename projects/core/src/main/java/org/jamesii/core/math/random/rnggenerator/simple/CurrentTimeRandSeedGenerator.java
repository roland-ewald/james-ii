/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.rnggenerator.simple;

import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.math.random.generators.java.JavaRandomGeneratorFactory;
import org.jamesii.core.math.random.generators.mersennetwister.MersenneTwisterGeneratorFactory;
import org.jamesii.core.math.random.generators.plugintype.AbstractRandomGeneratorFactory;
import org.jamesii.core.math.random.generators.plugintype.RandomGeneratorFactory;
import org.jamesii.core.math.random.rnggenerator.IRNGGenerator;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterizedFactory;

/**
 * RNG seed generator that uses the java random number generator to create seeds
 * which will be used to generate instances of the RNG (see
 * {@link #getRNGFactory()}). The initial seed of the java RNG is the
 * {@link System#currentTimeMillis()} seed.
 * 
 * @author Roland Ewald
 */
public class CurrentTimeRandSeedGenerator implements IRNGGenerator {

  /** Serialization ID. */
  private static final long serialVersionUID = -8074375008537611791L;

  /** Seed generator for random numbers. */
  private IRandom randSeedGenerator;

  /** The {@link RandomGeneratorFactory} to be used. */
  private ParameterizedFactory<RandomGeneratorFactory> rngFactory =
      new ParameterizedFactory<RandomGeneratorFactory>(
          new MersenneTwisterGeneratorFactory());

  /**
   * Initial seed for the
   * {@link org.jamesii.core.math.random.generators.java.JavaRandom} to generate
   * seeds.
   */
  private long seedGeneratorSeed;

  /** Factory to create seed RNG. */
  private final RandomGeneratorFactory seedGeneratorFactory =
      new JavaRandomGeneratorFactory();

  /**
   * Default constructor.
   */
  public CurrentTimeRandSeedGenerator() {
    this(System.currentTimeMillis());
  }

  /**
   * Instantiates a new current time rand seed generator.
   * 
   * @param seed
   *          the seed
   */
  protected CurrentTimeRandSeedGenerator(long seed) {
    setSeed(seed);
  }

  @Override
  public final void setSeed(long seed) {
    seedGeneratorSeed = seed;
    randSeedGenerator = seedGeneratorFactory.create(seedGeneratorSeed);
    SimSystem.report(Level.CONFIG,
        "A random seed generator has been initialized with an initial seed of "
            + seedGeneratorSeed);
  }

  /**
   * @return seed for {@link java.util.Random} instance that generates seeds for
   *         {@link IRandom} instances subsequently returned by
   *         {@link #getNextRNG()}
   */
  public long getSeedGeneratorSeed() {
    return seedGeneratorSeed;
  }

  @Override
  public IRandom getNextRNG() {
    ParameterBlock parameters = rngFactory.getParameters().getCopy();
    parameters.addSubBl(AbstractRandomGeneratorFactory.SEED,
        randSeedGenerator.nextLong());
    return rngFactory.getFactoryInstance().create(parameters, SimSystem.getRegistry().createContext());
  }

  @Override
  public ParameterizedFactory<RandomGeneratorFactory> getRNGFactory() {
    return rngFactory;
  }

  @Override
  public void setRNGFactory(
      ParameterizedFactory<RandomGeneratorFactory> rngFactory) {
    this.rngFactory = rngFactory;
  }

}

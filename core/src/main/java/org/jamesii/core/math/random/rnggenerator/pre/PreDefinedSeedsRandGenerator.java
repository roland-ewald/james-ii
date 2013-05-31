/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.rnggenerator.pre;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.math.random.generators.mersennetwister.MersenneTwisterGeneratorFactory;
import org.jamesii.core.math.random.generators.plugintype.AbstractRandomGeneratorFactory;
import org.jamesii.core.math.random.generators.plugintype.RandomGeneratorFactory;
import org.jamesii.core.math.random.rnggenerator.IRNGGenerator;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterizedFactory;
import org.jamesii.core.util.Hook;
import org.jamesii.core.util.exceptions.OperationNotSupportedException;

/**
 * RNG seed generator that generates random number generators based on the list
 * of seeds passed. Each seed is used once - to create a new instance of the
 * {@link RandomGeneratorFactory}.<br/>
 * A {@link Hook} to generate new seeds should always be provided - otherwise
 * experiments using this generator might fail as soon as we run out of seeds.
 * 
 * @author Jan Himmelspach
 */
public class PreDefinedSeedsRandGenerator implements IRNGGenerator {

  /** Serialization ID. */
  private static final long serialVersionUID = 1968121386322460740L;

  /** the list of seeds to be used. */
  private List<Long> seeds;

  /**
   * A hook which gets executed as soon as the list of seeds is empty. If the
   * list of seeds is empty and no such hook is available an exception will be
   * thrown.
   */
  private Hook<PreDefinedSeedsRandGenerator> outOfSeedsHook;

  /**
   * The random number generator factory to be used.
   */
  private ParameterizedFactory<RandomGeneratorFactory> rngFactory =
      new ParameterizedFactory<RandomGeneratorFactory>(
          new MersenneTwisterGeneratorFactory());

  /**
   * Default constructor.
   */
  public PreDefinedSeedsRandGenerator() {
  }

  /**
   * Instantiates a new current time rand seed generator.
   * 
   * @param seeds
   *          Seeds to be used
   * @param outOfSeedsHook
   *          Hook to create seeds when all seeds were used
   */
  public PreDefinedSeedsRandGenerator(List<Long> seeds,
      Hook<PreDefinedSeedsRandGenerator> outOfSeedsHook) {
    setSeeds(seeds);
    setOutOfSeedsHook(outOfSeedsHook);
  }

  @Override
  public void setSeed(long seed) {
    throw new OperationNotSupportedException();
  }

  /**
   * Set seeds to use for {@link #getNextRNG() next generated RNGs}
   * 
   * @param seeds
   */
  public final void setSeeds(List<Long> seeds) {
    this.seeds = new LinkedList<>(seeds);
  }

  /**
   * @return seeds to use for {@link #getNextRNG() next generated RNGs}
   */
  public final List<Long> getSeeds() {
    return Collections.unmodifiableList(this.seeds);
  }

  private Object lock = new Object();

  @Override
  public IRandom getNextRNG() {
    Long nextSeed;
    synchronized (lock) {
      if (seeds.isEmpty()) {
        if (outOfSeedsHook != null) {
          outOfSeedsHook.execute(this);
        }
        if (seeds.isEmpty()) {
          throw new IllegalStateException(
              "Out of seeds. The random generator generator ran out of"
                  + "(pre-defined) seeds and no hook generated more of them.");
        }
      }
      nextSeed = seeds.remove(0);
    }
    ParameterBlock parameters = rngFactory.getParameters().getCopy();
    parameters.addSubBl(AbstractRandomGeneratorFactory.SEED, nextSeed);
    return rngFactory.getFactoryInstance().create(parameters);
  }

  @Override
  public ParameterizedFactory<RandomGeneratorFactory> getRNGFactory() {
    return rngFactory;
  }

  @Override
  public void setRNGFactory(
      ParameterizedFactory<RandomGeneratorFactory> parameters) {
    this.rngFactory = parameters;
  }

  /**
   * Get the value of the outOfSeedsHook.
   * 
   * @return the outOfSeedsHook
   */
  public Hook<PreDefinedSeedsRandGenerator> getOutOfSeedsHook() {
    return outOfSeedsHook;
  }

  /**
   * Set the outOfSeedsHook to the value passed via the outOfSeedsHook
   * attribute.
   * 
   * @param outOfSeedsHook
   *          the outOfSeedsHook to set
   */
  public final void setOutOfSeedsHook(
      Hook<PreDefinedSeedsRandGenerator> outOfSeedsHook) {
    this.outOfSeedsHook = outOfSeedsHook;
  }
}

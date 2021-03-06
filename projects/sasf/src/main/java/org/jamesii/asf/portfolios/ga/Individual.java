/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.portfolios.ga;

import org.jamesii.core.math.random.generators.IRandom;

/**
 * Super class for easy implementation of {@link IIndividual}.
 * 
 * @author René Schulz, Roland Ewald
 * 
 */
public abstract class Individual implements IIndividual {

  /** The RNG to be used. */
  private IRandom rng;

  /** The current fitness of the individual. */
  private double fitness;

  /**
   * Instantiates a new individual.
   * 
   * @param random
   *          the random number generator to be used
   */
  public Individual(IRandom random) {
    rng = random;
  }

  /**
   * This method is obsolete if (and only if) genomes generated by recombination
   * or mutation meet the size constraint.
   */
  @Override
  public void repairGenome(int minSize, int maxSize) {
  }

  @Override
  public final double getFitness() {
    return fitness;
  }

  @Override
  public final void setFitness(double fit) {
    fitness = fit;
  }

  /**
   * Gets the random number generator.
   * 
   * @return the random number generator
   */
  protected IRandom getRNG() {
    return rng;
  }

}

/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.portfolios.ga;

import org.jamesii.core.util.misc.Pair;

/**
 * 
 * Interface for Individuals for {@Link IGenePool}.
 * 
 * 
 * @author René Schulz, Roland Ewald
 * 
 */
public interface IIndividual {

  /**
   * Gets the fitness of the individual.
   * 
   * @return the fitness
   */
  double getFitness();

  /**
   * Sets the fitness of the individual.
   * 
   * @param fitness
   *          the new fitness
   */
  void setFitness(double fitness);

  /**
   * Generate random genome for an individual.
   * 
   * @param length
   *          the size of the hole genome
   * @param portfolioSize
   *          the size of the portfolio
   * 
   */
  void generateRandomGenome(int length, int portfolioSize);

  /**
   * Mutate the genome of an individual. The new genome may NOT meet the size
   * constrains (if necessary, use repairGenome() to avoid this).
   * 
   * @param mutationRate
   *          the probability of an mutation
   * @param minSize
   *          the minimal possible portfolioSize
   * @param maxSize
   *          the maximal possible portfolioSize
   * 
   */
  void mutate(double mutationRate, int minSize, int maxSize);

  /**
   * Recombines the individual with "partner" and returns two new individuals
   * ("children"). The children may NOT meet the size constrains (if necessary,
   * use repairGenome() to avoid this).
   * 
   * @param partner
   *          another individual to recombine
   * @return two new children
   */
  Pair<IIndividual, IIndividual> recombine(IIndividual partner);

  /**
   * Check if the current genome meets the size constraints and repair if
   * necessary.
   */
  void repairGenome(int minSize, int maxSize);

  /**
   * Return the genome as a boolean array. (chosen algorithm are "true").
   * 
   * @return
   */
  boolean[] getBooleanRepresentation();

  /**
   * Counts the number of chosen algorithms (ones) in a genome.
   * 
   * @return the number of ones
   */
  int numOfChosenAlgo();
}

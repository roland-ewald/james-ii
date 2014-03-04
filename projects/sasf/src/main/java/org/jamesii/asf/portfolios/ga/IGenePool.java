/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.portfolios.ga;

/**
 * 
 * Interface for Gene Pools.
 * 
 * @author René Schulz, Roland Ewald
 * 
 */
public interface IGenePool {

  /**
   * Choose individuals out of an existing pool according to their fitness,
   * recombine them to create new individuals and mutate the generated new
   * individuals.
   * 
   * @return a new generation if individuals.
   */
  GenePool generateNewPool();

  /**
   * Gets the individual with the best fitness.
   * 
   * @return the individual with the best fitness.
   */
  IIndividual getBestIndividual();

  /**
   * Gets the best fitness.
   * 
   * @return the best fitness
   */
  double getBestFitness();

  /**
   * Gets the average fitness.
   * 
   * @return the average fitness
   */
  double getAvgFitness();

  /**
   * Gets all individuals.
   * 
   * @return the individuals
   */
  IIndividual[] getIndividuals();

}

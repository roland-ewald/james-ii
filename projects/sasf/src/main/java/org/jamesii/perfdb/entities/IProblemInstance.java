/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.entities;

/**
 * Interface for a problem instance. This represents a single instance of a
 * {@link IProblemDefinition}, which is currently characterised by an individual
 * RNG seed.
 * 
 * @author Roland Ewald
 * 
 */
public interface IProblemInstance extends IIDEntity {

  /**
   * Sets the simulation problem.
   * 
   * @param problemDefinition
   *          the new simulation problem
   */
  void setProblemDefinition(IProblemDefinition problemDefinition);

  /**
   * Gets the simulation problem.
   * 
   * @return the simulation problem
   */
  IProblemDefinition getProblemDefinition();

  /**
   * Sets the random seed.
   * 
   * @param randomSeed
   *          the new random seed
   */
  void setRandomSeed(long randomSeed);

  /**
   * Gets the random seed.
   * 
   * @return the random seed
   */
  long getRandomSeed();

  /**
   * Sets the RNG factory name.
   * 
   * @param rngFactoryName
   *          the new RNG factory name
   */
  void setRNGFactoryName(String rngFactoryName);

  /**
   * Gets the RNG factory name.
   * 
   * @return the RNG factory name
   */
  String getRNGFactoryName();

}

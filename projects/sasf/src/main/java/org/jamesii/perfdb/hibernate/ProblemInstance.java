/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.hibernate;

import org.jamesii.perfdb.entities.IProblemDefinition;
import org.jamesii.perfdb.entities.IProblemInstance;

/**
 * Hibernate implementation of a problem instance.
 * 
 * @author Roland Ewald
 * 
 */
@SuppressWarnings("unused")
public class ProblemInstance extends IDEntity implements IProblemInstance {

  private static final long serialVersionUID = -2305193644325308750L;

  /** Random seed that was used. */
  private long randomSeed;

  /** Problem definition that is initialized with this seed. */
  private ProblemDefinition problemDefinition;

  /** The RNG factory to be used. */
  private String rngFactoryName;

  /**
   * Empty constructor for beans compliance.
   */
  public ProblemInstance() {
  }

  /**
   * Instantiates a new problem instance.
   * 
   * @param probDefinition
   *          the problem definition
   * @param randSeed
   *          the RNG seed
   * @param rngFacName
   *          the RNG factory name
   */
  public ProblemInstance(ProblemDefinition probDefinition, long randSeed,
      String rngFacName) {
    randomSeed = randSeed;
    problemDefinition = probDefinition;
    rngFactoryName = rngFacName;
  }

  @Override
  public long getRandomSeed() {
    return randomSeed;
  }

  @Override
  public IProblemDefinition getProblemDefinition() {
    return problemDefinition;
  }

  @Override
  public void setRandomSeed(long randSeed) {
    randomSeed = randSeed;
  }

  @Override
  public void setProblemDefinition(IProblemDefinition problemDefinition) {
    if (!(problemDefinition instanceof ProblemDefinition)) {
      throw new IllegalArgumentException();
    }
    this.problemDefinition = (ProblemDefinition) problemDefinition;
  }

  @Override
  public String getRNGFactoryName() {
    return rngFactoryName;
  }

  @Override
  public void setRNGFactoryName(String rngFactoryName) {
    this.rngFactoryName = rngFactoryName;
  }

  // Functions for hibernate

  private ProblemDefinition getProblemDef() {
    return problemDefinition; // NOSONAR:{used_by_hibernate}
  }

  private void setProblemDef(ProblemDefinition probDef) {
    problemDefinition = probDef; // NOSONAR:{used_by_hibernate}
  }

}

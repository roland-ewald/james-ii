/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.database.hibernate;

import org.jamesii.asf.database.ISelector;
import org.jamesii.asf.database.ITrainingProblem;
import org.jamesii.perfdb.entities.IProblemDefinition;
import org.jamesii.perfdb.hibernate.IDEntity;
import org.jamesii.perfdb.hibernate.ProblemDefinition;


/**
 * Hibernate implementation of a training problem.
 * 
 * @author Roland Ewald
 * 
 */
@Deprecated
@SuppressWarnings("unused")
// Hibernate uses private methods
public class TrainingProblem extends IDEntity implements ITrainingProblem {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -7705682728734534685L;

  /** Reference to the simulation problem that was used for training. */
  private ProblemDefinition problemDefinition;

  /** Reference to the selector that was trained. */
  private Selector selector;

  /**
   * Number of available runtime configurations. Used to qualify when this
   * problem was taken into account as a training problem (eg., maybe there were
   * only very few configurations for it).
   */
  private int numOfConfigs;

  /**
   * Number of available feature types. Used to quantify how much information on
   * the problem was available.
   */
  private int numOfFeatures;

  @Override
  public int getNumOfConfigs() {
    return numOfConfigs;
  }

  @Override
  public int getNumOfFeatures() {
    return numOfFeatures;
  }

  @Override
  public ISelector getSelector() {
    return selector;
  }

  @Override
  public IProblemDefinition getSimulationProblem() {
    return problemDefinition;
  }

  @Override
  public void setNumOfConfigs(int numConfigs) {
    numOfConfigs = numConfigs;
  }

  @Override
  public void setNumOfFeatures(int numFeatures) {
    numOfFeatures = numFeatures;
  }

  @Override
  public void setSelector(ISelector sel) {
    if (!(sel instanceof Selector)) {
      throw new IllegalArgumentException();
    }
    selector = (Selector) sel;
  }

  @Override
  public void setSimulationProblem(IProblemDefinition problemDefinition) {
    if (!(problemDefinition instanceof ProblemDefinition)) {
      throw new IllegalArgumentException();
    }
    this.problemDefinition = (ProblemDefinition) problemDefinition;
  }

  // Hibernate functions

  private Selector getSel() {
    return selector; // NOSONAR:{used_by_hibernate}
  }

  private void setSel(Selector sel) {
    selector = sel; // NOSONAR:{used_by_hibernate}
  }

  private ProblemDefinition getSimProblem() {
    return problemDefinition; // NOSONAR:{used_by_hibernate}
  }

  private void setSimProblem(ProblemDefinition simProb) {
    problemDefinition = simProb; // NOSONAR:{used_by_hibernate}
  }

}

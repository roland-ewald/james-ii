/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.replication;

import java.util.List;

import org.jamesii.core.experiments.RunInformation;

/**
 * Replication number criterion, allows to define a constant number of
 * replications.
 * 
 * @author Roland Ewald
 * 
 *         Date: 10.05.2007
 */
public class ReplicationNumberCriterion implements IReplicationCriterion {

  /** Serialisation ID. */
  private static final long serialVersionUID = 8994630008402882328L;

  /** Maximal number of replications. */
  private int numOfReplications;

  /**
   * Constructor for bean compatibility.
   */
  public ReplicationNumberCriterion() {
    numOfReplications = 1;
  }

  /**
   * Default constructor.
   * 
   * @param numOfReps
   *          number of replications
   */
  public ReplicationNumberCriterion(int numOfReps) {
    numOfReplications = numOfReps;
  }

  /**
   * Gets the number of replications.
   * 
   * @return the number of replications
   */
  public int getNumOfReplications() {
    return numOfReplications;
  }

  /**
   * Sets the number of replications.
   * 
   * @param numOfReplications
   *          the new number of replications
   */
  public void setNumOfReplications(int numOfReplications) {
    this.numOfReplications = numOfReplications;
  }

  @Override
  public int sufficientReplications(List<RunInformation> runInformation) {
    int result = numOfReplications - runInformation.size();
    if (result <= 0) {
      return 0;
    }
    return result;
  }

  @Override
  public String toString() {
    return "RepNumberCriterion: Repeat " + numOfReplications + " times.";
  }

  public int getInitialReplications() {
    return numOfReplications;
  }

}

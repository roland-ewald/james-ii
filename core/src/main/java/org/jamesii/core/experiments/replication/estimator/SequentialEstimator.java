/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.replication.estimator;

import java.util.List;

/**
 * Estimates the amount of replications in a sequential procedure. returns a
 * given count of required replications until a given criterion is met.
 * 
 * @see Soren Asmussen and Peter W. Glynn: Stochastic Simulation, Springer, 2008
 *      p. 71-73
 * 
 * @author Stefan Leye
 */
public abstract class SequentialEstimator implements
    IRequiredReplicationsEstimator {

  /**
   * The amount of additional required replications, if the criterion is not
   * met.
   */
  private int additionalReplicationCount;

  /**
   * Basic constructor.
   * 
   * @param additionalReplicationCount
   *          the initial amount of additional replications.
   */
  public SequentialEstimator(int additionalReplicationCount) {
    this.additionalReplicationCount = additionalReplicationCount;
  }

  @Override
  public int estimateAditionalReplications(List<? extends Number> sample) {
    if (metCriterion(sample)) {
      return 0;
    }
    return additionalReplicationCount;
  }

  /**
   * Gets the amount of additionally required replications, if the criterion is
   * not met.
   * 
   * @return additionally required replications
   */
  protected int getAdditionalReplicationCount() {
    return additionalReplicationCount;
  }

  /**
   * Sets the amount of additionally required replications, if the criterion is
   * not met.
   * 
   * @param additionalReplicationCount
   *          additionally required replications
   */
  protected void setAdditionalReplicationCount(int additionalReplicationCount) {
    this.additionalReplicationCount = additionalReplicationCount;
  }

  /**
   * Checks whether the criterion is met and no addional replications are
   * required.
   * 
   * @param sample
   *          the sample to be tested
   * 
   * @return true if the criterion is met
   */
  protected abstract boolean metCriterion(List<? extends Number> sample);
}

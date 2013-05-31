/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.replication.estimator;

import java.util.List;

/**
 * Basic interface for classes which estimate the amount of additionally
 * required replications based on a given sample.
 * 
 * @author Stefan Leye
 */
public interface IRequiredReplicationsEstimator {

  /**
   * Estimates the amount of additionally required replications.
   * 
   * @param sample
   *          the first sample on which the estimation is based
   * @return the estimated amount of additional replications
   */
  int estimateAditionalReplications(List<? extends Number> sample);

}

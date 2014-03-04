/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.spdm.generators;

import java.io.Serializable;

import org.jamesii.asf.spdm.Configuration;
import org.jamesii.asf.spdm.Features;


/**
 * Interface for all performance predictors. These are components that predict
 * the performance of an (algorithmic) configuration for a given problem, which
 * is characterized by certain features.
 * 
 * More formally, the interface represents functions of the form F x A -> R,
 * which assign a certain weight for each algorithm in A to be applied to a
 * problem with features in F. The weight may be interpreted in different ways,
 * generally is should refer to the eligibility and suitability of an algorithm,
 * i.e. a return value of 0 indicates that the algorithm is not applicable to
 * the problem (the higher the weight, the better).
 * 
 * This allows to use performance predictors for a broad variety of algorithm
 * synthesis/selection tasks, i.e. for picking a single algorithm from a set or
 * for defining an algorithm portfolio.
 * 
 * @author Roland Ewald
 * 
 */
public interface IPerformancePredictor extends Serializable {

  /**
   * Predict performance for a given configuration.
   * 
   * @param features
   *          the characteristics of the problem to be solved
   * @param config
   *          the configuration
   * @return predicted performance
   */
  double predictPerformance(Features features, Configuration config);

}

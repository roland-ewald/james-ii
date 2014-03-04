/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.spdm.generators;

import java.io.Serializable;
import java.util.Comparator;

import org.jamesii.asf.spdm.Features;


/**
 * Interface for all performance comparison predictors. These are components
 * that predict the result of a performance comparison of any two configurations
 * for a given set of simulation problem features. Hence, a performance
 * predictor has to decide which of two configurations will perform better under
 * the given circumstances.
 * 
 * @author Roland Ewald
 * 
 */
public interface IPerfComparisonPredictor extends
    Comparator<ConfigurationEntry>, Serializable {

  /**
   * Features of the current simulation problem.
   * 
   * @param features
   *          features of the simulation problem, to be taken into account
   */
  void setFeatures(Features features);

}

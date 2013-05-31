/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization.parameter.representativeValue;

import java.io.Serializable;
import java.util.Map;

import org.jamesii.core.experiments.optimization.ConfigurationInfos;

/**
 * Support to calculate a representative value on repetitive runs.
 * 
 * @author Arvid Schwecke
 * 
 */
public interface IRepresentativeValueOfObjectives extends Serializable {

  /**
   * Calculate representative value.
   * 
   * @param expInfo
   *          the exp info
   * 
   * @return the double
   */
  Map<String, Double> calcRepresentativeValue(ConfigurationInfos expInfo);

}

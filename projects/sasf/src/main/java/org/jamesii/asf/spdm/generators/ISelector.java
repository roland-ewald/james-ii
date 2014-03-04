/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.spdm.generators;

import java.io.Serializable;
import java.util.List;

import org.jamesii.asf.spdm.Configuration;
import org.jamesii.asf.spdm.Features;


/**
 * Interface for all algorithm selectors. These components order lists of
 * configurations by their predicted performance.
 * 
 * The prediction is done by an {@link IPerformancePredictor} instance.
 * 
 * @see IPerformancePredictor
 * 
 * @author Roland Ewald
 * 
 */
public interface ISelector extends Serializable {

  /**
   * Select a sorted list of suitable configurations for a given set of
   * features.
   * 
   * @param features
   *          features of the simulation problem
   * @return list of proposed configurations (best predicted is first element,
   *         second best predicted is second element, and so on)
   */
  List<Configuration> selectConfigurations(Features features);

  /**
   * Returns performance comparison predictor this selector uses.
   * 
   * @return performance predictor
   */
  IPerformancePredictor getPerformancePredictor();

}

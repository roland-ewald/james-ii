/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.spdm.generators;

import org.jamesii.asf.spdm.generators.plugintype.IPerformancePredictorGenerator;

/**
 * Super type of predictor generators.
 * 
 * @author Roland Ewald
 * 
 */
public abstract class AbstractPredictorGenerator implements
    IPerformancePredictorGenerator {

  /** Name of the performance class (for all classifiers). */
  public static final String PERFORMANCE_CLASS = "performance";

}

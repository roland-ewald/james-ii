/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.spdm.generators.plugintype;

import java.util.List;

import org.jamesii.asf.spdm.dataimport.PerfTupleMetaData;
import org.jamesii.asf.spdm.dataimport.PerformanceTuple;
import org.jamesii.asf.spdm.generators.IPerformancePredictor;


/**
 * Basic interface for all performance predictor generators. Such a generator
 * takes some input data on performance and creates a predictor for future
 * performance.
 * 
 * @see IPerformancePredictor
 * 
 * @author Roland Ewald
 * 
 */
public interface IPerformancePredictorGenerator {

  /**
   * Generates a new performance predictor out of training data.
   * 
   * @param trainingData
   *          list of performance tuples (training data)
   * @param metaData
   *          meta-data on data set
   * @return predictor generated from that training data
   */
  <T extends PerformanceTuple> IPerformancePredictor generatePredictor(
      List<T> trainingData, PerfTupleMetaData metaData);

}

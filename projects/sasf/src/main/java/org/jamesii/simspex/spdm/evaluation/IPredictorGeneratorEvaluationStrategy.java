/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.evaluation;


import java.util.List;

import org.jamesii.asf.spdm.dataimport.PerformanceDataSet;
import org.jamesii.asf.spdm.dataimport.PerformanceTuple;
import org.jamesii.asf.spdm.generators.plugintype.PerformancePredictorGeneratorFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.simspex.spdm.evaluation.perfmeasures.PredictorPerformance;


/**
 * Interface for all evaluation strategies for predictor generators. Examples
 * for this are bootstrapping, cross-validation, etc.
 * 
 * Evaluation strategies need to divide the performance tuples based on their
 * *features*, i.e. the split into test and training set has to be done for sets
 * of unique features. Then, all performance tuples are assigned to the set to
 * which its features belong.
 * 
 * @author Roland Ewald
 * 
 */
// TODO distinguish globally between parameters for evaluation strategy, for
// performance measures, and for predictor generators
public interface IPredictorGeneratorEvaluationStrategy {

  /**
   * Lists the performances of the predictors generated with the given predictor
   * generator and data.
   * 
   * @param <T>
   *          the type of performance tuple
   * @param predGenFactory
   *          the corresponding predictor generator factory
   * @param dataSet
   *          the available data set (= list of instances + meta-data)
   * @param parameters
   *          parameters for the evaluation strategy, the evaluators, and the
   *          performance measures
   * @return list of performances (one for each generated predictor)
   */
  <T extends PerformanceTuple> List<PredictorPerformance> evaluatePredictorGenerator(
      PerformancePredictorGeneratorFactory predGenFactory,
      PerformanceDataSet<T> dataSet, ParameterBlock parameters);

}

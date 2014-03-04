/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.evaluation.simple;


import java.util.ArrayList;
import java.util.List;

import org.jamesii.asf.spdm.dataimport.PerformanceDataSet;
import org.jamesii.asf.spdm.dataimport.PerformanceTuple;
import org.jamesii.asf.spdm.generators.IPerformancePredictor;
import org.jamesii.asf.spdm.generators.plugintype.IPerformancePredictorGenerator;
import org.jamesii.asf.spdm.generators.plugintype.PerformancePredictorGeneratorFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.simspex.spdm.evaluation.IDataSelector;
import org.jamesii.simspex.spdm.evaluation.IPredictorGeneratorEvaluationStrategy;
import org.jamesii.simspex.spdm.evaluation.SimpleDataSelector;
import org.jamesii.simspex.spdm.evaluation.evaluator.FullPredictorEvaluator;
import org.jamesii.simspex.spdm.evaluation.evaluator.IPredictorEvaluator;
import org.jamesii.simspex.spdm.evaluation.perfmeasures.PredictorPerformance;


/**
 * Simple evaluation strategy, just cuts the data tuples in half (using the
 * {@link SimpleDataSelector}). Then, a full evaluation using the
 * {@link FullPredictorEvaluator} is applied once to the predictor generated
 * with the first half, and the one-element list of predictor performances for
 * the second half is returned.
 * 
 * @author Roland Ewald
 * 
 */
public class SimpleEvaluationStrategy implements
    IPredictorGeneratorEvaluationStrategy {

  /** Percentage of data to be used for training (in [0,1]). */
  private final double percentage;

  /**
   * Default constructor.
   * 
   * @param trainDataPercent
   *          percentage of data used for training (needs to be in [0,1]).
   */
  public SimpleEvaluationStrategy(double trainDataPercent) {
    percentage =
        (trainDataPercent <= 0 || trainDataPercent >= 1) ? 0.5
            : trainDataPercent;
  }

  @Override
  public List<PredictorPerformance> evaluatePredictorGenerator(
      PerformancePredictorGeneratorFactory predGenFactory,
      PerformanceDataSet dataSet, ParameterBlock parameters) {
    List<PredictorPerformance> result = new ArrayList<>();

    List<PerformanceTuple> data = dataSet.getInstances();
    int trainEndIndex = (int) Math.floor(data.size() * percentage);

    // Generate predictor
    IDataSelector trainingDataSelector =
        new SimpleDataSelector(0, trainEndIndex);
    List<PerformanceTuple> trainingData = trainingDataSelector.selectData(data);
    IPerformancePredictorGenerator predGen =
        predGenFactory.createPredictorGenerator(parameters, data.get(0));
    IPerformancePredictor predictor =
        predGen.generatePredictor(trainingData, dataSet.getMetaData());

    // Evaluate predictor
    IDataSelector testDataSelector =
        new SimpleDataSelector(trainEndIndex, data.size());
    List<PerformanceTuple> testData = testDataSelector.selectData(data);
    IPredictorEvaluator sev = new FullPredictorEvaluator();

    result.add(sev.evaluate(predictor, trainingData, testData, parameters));
    return result;
  }
}

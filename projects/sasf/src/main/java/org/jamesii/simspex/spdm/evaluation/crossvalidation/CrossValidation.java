/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.evaluation.crossvalidation;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.asf.spdm.Features;
import org.jamesii.asf.spdm.dataimport.PerformanceDataSet;
import org.jamesii.asf.spdm.dataimport.PerformanceTuple;
import org.jamesii.asf.spdm.generators.IPerformancePredictor;
import org.jamesii.asf.spdm.generators.plugintype.IPerformancePredictorGenerator;
import org.jamesii.asf.spdm.generators.plugintype.PerformancePredictorGeneratorFactory;
import org.jamesii.asf.spdm.util.PerformanceTuples;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.Strings;
import org.jamesii.simspex.spdm.evaluation.IDataSelector;
import org.jamesii.simspex.spdm.evaluation.IPredictorGeneratorEvaluationStrategy;
import org.jamesii.simspex.spdm.evaluation.PredictorGeneratorEvaluationException;
import org.jamesii.simspex.spdm.evaluation.SimpleDataSelector;
import org.jamesii.simspex.spdm.evaluation.evaluator.FullPredictorEvaluator;
import org.jamesii.simspex.spdm.evaluation.evaluator.IPredictorEvaluator;
import org.jamesii.simspex.spdm.evaluation.perfmeasures.PredictorPerformance;


/**
 * Algorithm for n-fold cross-validation (see Frank and Witten, "Data Mining:
 * Practical Machine Learning Tools and Techniques". Morgan Kaufmann 2005). Can
 * be configured for "leave-one-out" - cross validation.
 * 
 * Will generate (folds x replications) predictor performances.
 * 
 * @author Roland Ewald
 * @author Steffen Torbahn
 * 
 */
public class CrossValidation implements IPredictorGeneratorEvaluationStrategy {

  /** The maximal waiting time until termination. */
  private static final int WAIT_TIME_UNTIL_TERMINATION = 1200;

  /** Number of folds. */
  private int folds;

  /** The number of threads for replication. */
  private int threads = 1;

  /** The number of replications. */
  private final int numOfReplications;

  /** Flag that determines if 'leave-one-out' cross-validation should be done. */
  private final boolean leaveOneOut;

  /** The predictor evaluator. */
  private final IPredictorEvaluator predictorEvaluator =
      new FullPredictorEvaluator();

  /**
   * Default constructor.
   * 
   * @param foldNum
   *          the number of folds to be done
   * @param leaveOut
   *          true if leave-one-out cross-validation should be applied
   * @param replications
   *          the number of replications
   */
  public CrossValidation(int foldNum, boolean leaveOut, int replications) {
    folds = foldNum;
    leaveOneOut = leaveOut;
    numOfReplications = replications;
  }

  @Override
  public <T extends PerformanceTuple> List<PredictorPerformance> evaluatePredictorGenerator(
      final PerformancePredictorGeneratorFactory gef,
      final PerformanceDataSet<T> dataSet, final ParameterBlock parameters) {

    final List<PredictorPerformance> result =
        new ArrayList<>();

    List<T> allData = dataSet.getInstances();
    final Map<Features, List<T>> featureMap =
        PerformanceTuples.sortToFeatureMap(allData);

    final List<Features> allFeatures = new ArrayList<>();
    allFeatures.addAll(featureMap.keySet());

    if (leaveOneOut || folds > allFeatures.size()) {
      folds = allFeatures.size() - 1;
    }

    ExecutorService exec = Executors.newFixedThreadPool(threads);

    for (int i = 0; i < numOfReplications; i++) {
      Runnable replicationJob = new Runnable() {
        @Override
        public void run() {
          try {
            Collections.shuffle(allFeatures);

            List<PredictorPerformance> myResult =
                executeCrossValidation(featureMap, allFeatures, gef, dataSet,
                    parameters);
            synchronized (result) {
              result.addAll(myResult);
            }
          } catch (Throwable t) { // NOSONAR:{robustness_required}
            SimSystem.report(Level.SEVERE, "Cross-validation failed.", t);
          }
        }
      };
      exec.submit(replicationJob);
    }

    exec.shutdown();
    try {
      exec.awaitTermination(WAIT_TIME_UNTIL_TERMINATION, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      throw new PredictorGeneratorEvaluationException(e);
    }
    return result;
  }

  /**
   * Executes a single cross-validation.
   * 
   * * @param featureMap the map feature => corresponding performance tuples
   * 
   * @param <T>
   *          the type of performance tuple
   * @param featureMap
   *          the feature map
   * @param allFeatures
   *          list of all features
   * @param factory
   *          the predictor generator factory
   * @param dataSet
   *          the overall data set
   * @param parameters
   *          the parameters for predictor generation
   * @return the results, equals the number of folds
   */
  private <T extends PerformanceTuple> List<PredictorPerformance> executeCrossValidation(
      Map<Features, List<T>> featureMap, List<Features> allFeatures,
      PerformancePredictorGeneratorFactory factory,
      PerformanceDataSet<T> dataSet, ParameterBlock parameters) {

    List<PredictorPerformance> results = new ArrayList<>();

    for (int i = 0; i < folds; i++) {
      SimSystem.report(Level.INFO, "\tFold: " + i);
      int startIndex = i * allFeatures.size() / folds;
      int endIndex = (i + 1) * allFeatures.size() / folds;

      List<T> trainingData =
          getTrainingData(featureMap, allFeatures, startIndex, endIndex);
      List<T> testData =
          getTestData(featureMap, allFeatures, startIndex, endIndex);
      SimSystem.report(
          Level.INFO,
          "\tCreating predictor with '"
              + Strings.dispClassName(factory.getClass()) + " / "
              + parameters.toString() + "'...");
      IPerformancePredictor predictor =
          createPredictor(factory, dataSet, parameters, trainingData);
      SimSystem.report(Level.INFO, "done.\n\tStarting evaluation...");
      results.add(predictorEvaluator.evaluate(predictor, trainingData,
          testData, parameters));
      SimSystem.report(Level.INFO, "done.");
    }
    return results;
  }

  /**
   * Compiles test data.
   * 
   * @param <T>
   *          the type of the performance tuple
   * @param featureMap
   *          the map feature => corresponding performance tuples
   * @param allFeatures
   *          list of all features
   * @param startIndex
   *          the start index of the test set
   * @param endIndex
   *          the end index of the test set
   * @return the test data
   */
  private <T extends PerformanceTuple> List<T> getTestData(
      Map<Features, List<T>> featureMap, List<Features> allFeatures,
      int startIndex, int endIndex) {
    IDataSelector testDataSelector =
        new SimpleDataSelector(startIndex, endIndex);
    List<Features> testFeatures = testDataSelector.selectData(allFeatures);
    List<T> testData =
        PerformanceTuples.getTuplesForFeatures(featureMap, testFeatures);
    return testData;
  }

  /**
   * Creates a predictor.
   * 
   * @param <T>
   *          the type of performance tuple
   * @param predGenFactory
   *          the predictor generator factory
   * @param dataSet
   *          the overall data set
   * @param parameters
   *          the parameters for predictor generation
   * @param trainingData
   *          the training data
   * @return newly created predictor, based on training data
   */
  private <T extends PerformanceTuple> IPerformancePredictor createPredictor(
      PerformancePredictorGeneratorFactory predGenFactory,
      PerformanceDataSet<T> dataSet, ParameterBlock parameters,
      List<T> trainingData) {
    IPerformancePredictorGenerator predGen =
        predGenFactory.createPredictorGenerator(parameters, dataSet
            .getInstances().get(0));
    return predGen.generatePredictor(trainingData, dataSet.getMetaData());
  }

  /**
   * Compiles training data.
   * 
   * @param featureMap
   *          the map feature => corresponding performance tuples
   * @param allFeatures
   *          list of all features
   * @param startIndex
   *          the start index of the test set
   * @param endIndex
   *          the end index of the test set
   * @return the training data
   */
  private <T extends PerformanceTuple> List<T> getTrainingData(
      Map<Features, List<T>> featureMap, List<Features> allFeatures,
      int startIndex, int endIndex) {
    IDataSelector trainingDataSelector = new SimpleDataSelector(0, startIndex);
    List<Features> trainingFeatures =
        trainingDataSelector.selectData(allFeatures);

    trainingDataSelector = new SimpleDataSelector(endIndex, allFeatures.size());
    trainingFeatures.addAll(trainingDataSelector.selectData(allFeatures));
    List<T> trainingData =
        PerformanceTuples.getTuplesForFeatures(featureMap, trainingFeatures);
    return trainingData;
  }

  public int getFolds() {
    return folds;
  }

  public int getNumOfReplications() {
    return numOfReplications;
  }

  public boolean isLeaveOneOut() {
    return leaveOneOut;
  }

  public IPredictorEvaluator getPredictorEvaluator() {
    return predictorEvaluator;
  }

}

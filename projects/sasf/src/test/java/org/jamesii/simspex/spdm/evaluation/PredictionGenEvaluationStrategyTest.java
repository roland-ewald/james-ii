/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.evaluation;


import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jamesii.ChattyTestCase;
import org.jamesii.asf.spdm.dataimport.IDMDataImportManager;
import org.jamesii.asf.spdm.dataimport.PerformanceDataSet;
import org.jamesii.asf.spdm.generators.random.RandomPredictorGeneratorFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.simspex.spdm.dataimport.FileImportManagerTest;
import org.jamesii.simspex.spdm.evaluation.perfmeasures.IPredictorPerformanceMeasure;
import org.jamesii.simspex.spdm.evaluation.perfmeasures.PredictorPerformance;

/**
 * Tests for predictor generator evaluation strategies.
 * 
 * @author Roland Ewald
 * 
 */
public abstract class PredictionGenEvaluationStrategyTest extends ChattyTestCase {

  /** The evaluation strategy to be tested. */
  IPredictorGeneratorEvaluationStrategy evalStrategy;

  /** Data import manager to be tested. */
  IDMDataImportManager dataManager;

  /** Performance data set to be analysed. */
  PerformanceDataSet performanceData;

  /** Performance results of generated predictors. */
  List<PredictorPerformance> predictorPerformances;

  @Override
  public void setUp() throws Exception {
    evalStrategy = getEvaluationStrategy();
    dataManager = FileImportManagerTest.createTestFileImportManager();
    performanceData = dataManager.getPerformanceData();
    predictorPerformances =
        evalStrategy.evaluatePredictorGenerator(
            new RandomPredictorGeneratorFactory(), performanceData,
            new ParameterBlock());
  }

  /**
   * Basic tests on performance measure existence.
   */
  public void testPerfInfoExistence() {

    assertFalse(predictorPerformances.size() == 0);

    for (PredictorPerformance selPerf : predictorPerformances) {
      Map<Class<? extends IPredictorPerformanceMeasure>, Pair<Double, Integer>> perfMeasures =
          selPerf.getPerformanceMeasures();

      assertNotNull(perfMeasures);
      assertFalse(perfMeasures.size() == 0);

      for (Entry<Class<? extends IPredictorPerformanceMeasure>, Pair<Double, Integer>> perfMeasurement : perfMeasures
          .entrySet()) {
        assertNotNull(perfMeasurement.getKey());
        assertNotNull(perfMeasurement.getValue());
      }

      assertTrue(selPerf.getTestSetSize() > 0);
      assertTrue(selPerf.getTrainingSetSize() > 0);
      assertNotNull(selPerf.getPredictorEvaluator());
    }
  }

  /**
   * Creates the evaluation strategy to be tested.
   * 
   * @return the evaluation strategy to be tested
   */
  protected abstract IPredictorGeneratorEvaluationStrategy getEvaluationStrategy();

}

/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.generators;

import org.jamesii.SimSystem;
import org.jamesii.asf.spdm.Configuration;
import org.jamesii.asf.spdm.Features;
import org.jamesii.asf.spdm.generators.IPerformancePredictor;
import org.jamesii.asf.spdm.generators.ISelector;
import org.jamesii.asf.spdm.generators.SelectorGeneration;
import org.jamesii.asf.spdm.generators.plugintype.IPerformancePredictorGenerator;
import org.jamesii.simspex.spdm.PerformanceDataTest;


/**
 * General test for predictor generators.
 * 
 * @author Roland Ewald
 * 
 */
public abstract class PredictorGeneratorTest extends PerformanceDataTest {

  /** The number of performance classes for this test. */
  protected static final int NUM_PERF_CLASSES_TEST = 3;

  /** The predictor generator to be tested. */
  IPerformancePredictorGenerator predictorGenerator = null;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    predictorGenerator = getPredictorGenerator();
  }

  /**
   * Creates predictor generator to be tested.
   * 
   * @return instance of the predictor generator
   */
  protected abstract IPerformancePredictorGenerator getPredictorGenerator();

  /**
   * Tests predictor generation.
   * 
   * @throws Exception
   *           when predictor generation goes wrong
   */
  public void testPredictorGeneration() throws Exception {

    // Generate predictor
    IPerformancePredictor predictor =
        predictorGenerator.generatePredictor(getDataSet().getInstances(),
            getDataSet().getMetaData());
    assertNotNull(predictor);

    // Generate corresponding selector
    ISelector selector =
        SelectorGeneration.createSelector(getDataSet(), predictor);
    assertNotNull(selector);

    // Simple test run to see if comparison throws Exceptions etc.
    Features features = getDataSet().getInstances().get(0).getFeatures();
    Configuration config1 =
        getDataSet().getInstances().get(0).getConfiguration();
    Configuration config2 =
        getDataSet().getInstances().get(1).getConfiguration();

    try {
      predictor.predictPerformance(features, config1);
      predictor.predictPerformance(features, config2);
    } catch (Exception e) {
      SimSystem.report(e);
      fail("Performance prediction should work.");
    }
  }
}

/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.util;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.jamesii.asf.spdm.Configuration;
import org.jamesii.asf.spdm.Features;
import org.jamesii.asf.spdm.dataimport.PerformanceTuple;
import org.jamesii.asf.spdm.util.PredictorGenerators;
import org.jamesii.perfdb.recording.performance.totaltime.TotalRuntimePerfMeasurerFactory;

/**
 * 
 * Unit tests for auxiliary predictor generation methods.
 * 
 * @author Roland Ewald
 * 
 */
public class TestPredictorGeneratorUtils extends TestCase {

  /** List of test tuples. */
  List<PerformanceTuple> perfTuples;

  /**
   * Generates 100 performance tuples with performance from 1 to 100.
   */
  @Override
  protected void setUp() throws Exception {
    perfTuples = new ArrayList<>(100);
    for (int i = 0; i < 100; i++) {
      perfTuples.add(new PerformanceTuple(new Features(), new Configuration(
          null), TotalRuntimePerfMeasurerFactory.class, i + 1));
    }
  }

  /**
   * Tests methods to support classifiers.
   */
  public void testPerformanceLevelClassification() {

    assertEquals(0, PredictorGenerators.performanceLevels(perfTuples, 1).length);
    Double[] perfBorders =
        PredictorGenerators.performanceLevels(perfTuples, 10);

    assertEquals(9, perfBorders.length);

    assertEquals(0, PredictorGenerators.classifyPerformance(perfBorders, -100.));
    assertEquals(0, PredictorGenerators.classifyPerformance(perfBorders, 0.));
    assertEquals(0, PredictorGenerators.classifyPerformance(perfBorders, 9.));
    assertEquals(1, PredictorGenerators.classifyPerformance(perfBorders, 10.));
    assertEquals(8, PredictorGenerators.classifyPerformance(perfBorders, 89.));
    assertEquals(9, PredictorGenerators.classifyPerformance(perfBorders, 90.));
    assertEquals(9, PredictorGenerators.classifyPerformance(perfBorders, 120.));

  }
}

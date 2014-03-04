/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.spdm.generators.random;

import java.util.List;

import org.jamesii.asf.spdm.dataimport.PerfTupleMetaData;
import org.jamesii.asf.spdm.dataimport.PerformanceTuple;
import org.jamesii.asf.spdm.generators.AbstractPredictorGenerator;
import org.jamesii.asf.spdm.generators.IPerformancePredictor;


/**
 * Creates a random predictor (for testing purposes).
 * 
 * @author Roland Ewald
 * 
 */
public class RandomPredictorGenerator extends AbstractPredictorGenerator {

  @Override
  public <T extends PerformanceTuple> IPerformancePredictor generatePredictor(
      List<T> trainingData, PerfTupleMetaData metaData) {
    return new RandomPredictor();
  }
}
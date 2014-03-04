/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.spdm.generators.random;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jamesii.asf.spdm.Configuration;
import org.jamesii.asf.spdm.dataimport.PerfTupleMetaData;
import org.jamesii.asf.spdm.dataimport.PerformanceTuple;
import org.jamesii.asf.spdm.generators.AbstractPredictorGenerator;
import org.jamesii.asf.spdm.generators.IPerformancePredictor;
import org.jamesii.asf.spdm.util.FixedPerfPredictor;


/**
 * The uses a constant predictor ordering the configurations in the same way
 * each time. The fixed configuration order is random.
 * 
 * @author Roland Ewald
 */
public class RandomFixedPredictorGenerator extends AbstractPredictorGenerator {

  @Override
  public <T extends PerformanceTuple> IPerformancePredictor generatePredictor(
      List<T> trainingData, PerfTupleMetaData metaData) {
    Set<Configuration> configs = new HashSet<>();
    for (PerformanceTuple tuple : trainingData) {
      configs.add(tuple.getConfiguration());
    }
    List<Configuration> configList = new ArrayList<>(configs);
    Collections.shuffle(configList);
    return new FixedPerfPredictor(configList);
  }

}

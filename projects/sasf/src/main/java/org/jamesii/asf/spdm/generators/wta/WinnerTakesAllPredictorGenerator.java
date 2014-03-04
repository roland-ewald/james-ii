/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.spdm.generators.wta;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jamesii.asf.spdm.Configuration;
import org.jamesii.asf.spdm.dataimport.PerfTupleMetaData;
import org.jamesii.asf.spdm.dataimport.PerformanceTuple;
import org.jamesii.asf.spdm.generators.AbstractPredictorGenerator;
import org.jamesii.asf.spdm.generators.IPerformancePredictor;
import org.jamesii.asf.spdm.util.FixedPerfPredictor;
import org.jamesii.core.util.misc.Pair;


/**
 * This generates a benchmark predictor for using non-adaptive statistical means
 * for algorithm selection. It computes the average performance of each
 * configuration and 'predicts' performance according to this measurement. The
 * sorted list will be used to initialise a {@link FixedPerfPredictor}.
 * 
 * @author Roland Ewald
 * 
 */
public class WinnerTakesAllPredictorGenerator extends
    AbstractPredictorGenerator {

  /**
   * A comparator for configurations.
   */
  private static final class ConfigurationComparator implements
      Comparator<Configuration>, Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -964038452626785930L;

    /** The performances of the configurations. */
    private final Map<Configuration, Pair<Double, Integer>> performances;

    /**
     * Instantiates a new configuration comparator.
     * 
     * @param performances
     *          the performances of the configurations
     */
    private ConfigurationComparator(
        Map<Configuration, Pair<Double, Integer>> performances) {
      this.performances = performances;
    }

    @Override
    public int compare(Configuration config1, Configuration config2) {
      Pair<Double, Integer> p1 = performances.get(config1);
      double perf1 = p1.getFirstValue() / p1.getSecondValue();
      Pair<Double, Integer> p2 = performances.get(config2);
      double perf2 = p2.getFirstValue() / p2.getSecondValue();
      return Double.compare(perf1, perf2);
    }
  }

  @Override
  public <T extends PerformanceTuple> IPerformancePredictor generatePredictor(
      List<T> trainingData, PerfTupleMetaData metaData) {

    final Map<Configuration, Pair<Double, Integer>> performances =
        new HashMap<>();

    // Sum up run time for performance data
    for (PerformanceTuple tuple : trainingData) {
      Configuration config = tuple.getConfiguration();
      if (!performances.containsKey(config)) {
        performances.put(config,
            new Pair<>(tuple.getPerformance(), 1));
      } else {
        Pair<Double, Integer> perf = performances.get(config);
        perf.setFirstValue(perf.getFirstValue() + tuple.getPerformance());
        perf.setSecondValue(perf.getSecondValue() + 1);
      }
    }

    // Create configuration list sorted by overall performance
    List<Configuration> configs =
        new ArrayList<>(performances.keySet());
    Collections.sort(configs, new ConfigurationComparator(performances));

    return new FixedPerfPredictor(configs);
  }
}

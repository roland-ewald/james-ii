/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.evaluation.perfmeasures;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.asf.spdm.Configuration;
import org.jamesii.asf.spdm.Features;
import org.jamesii.asf.spdm.dataimport.PerformanceTuple;
import org.jamesii.asf.spdm.generators.ConfigurationEntry;
import org.jamesii.asf.spdm.generators.IPerformancePredictor;
import org.jamesii.asf.spdm.generators.PerformancePredictingComparator;
import org.jamesii.asf.spdm.util.PerformanceTuples;
import org.jamesii.core.util.misc.Pair;

/**
 * This measure is similar to the notion of 'regret' for multi-armed bandit
 * policies.
 * 
 * 
 * @author Roland Ewald
 */
public class FeatureBestDiffMeasure implements IPredictorPerformanceMeasure {

  /**
   * Compares performance tuples by performance.
   */
  private static final class ComparatorByPerformance implements
      Comparator<PerformanceTuple>, Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6260289205179526900L;

    @Override
    public int compare(PerformanceTuple o1, PerformanceTuple o2) {
      return Double.compare(o1.getPerformance(), o2.getPerformance());
    }
  }

  @Override
  public <T extends PerformanceTuple> Pair<Double, Integer> calculatePerformance(
      List<T> testData, IPerformancePredictor predictor) {

    Map<Features, List<T>> allData =
        PerformanceTuples.sortToFeatureMap(testData);

    if (!noDuplicateConfigsForFeature(allData)) {
      throw new IllegalArgumentException(
          "Performance data set contains multiple tuples with same configuration and features - any result would be biased.");
    }

    double errorSum = 0;
    int predictions = 0;
    for (Features features : PerformanceTuples.sortToFeatureMap(testData)
        .keySet()) {

      List<PerformanceTuple> tuplesForFeature =
          new ArrayList<PerformanceTuple>(allData.get(features));
      List<ConfigurationEntry> configEntries =
          createConfigEntries(tuplesForFeature);
      Map<Configuration, PerformanceTuple> configTupleMap =
          createConfigTupleMap(tuplesForFeature);

      errorSum +=
          calculateDifference(predictor, features, tuplesForFeature,
              configEntries, configTupleMap);
      predictions++;
    }

    return new Pair<>(errorSum, predictions);
  }

  /**
   * Calculates the differences between best performance and performance of the
   * selected algorithm.
   * 
   * @param predictor
   *          the predictor to be tested
   * @param features
   *          the current problem's features
   * @param tuplesForFeature
   *          the performance tuples available for this feature
   * @param configEntriesForPrediction
   *          the configuration entries
   * @param configTupleMap
   *          a map configuration => corresponding performance tuple
   * @return
   */
  private double calculateDifference(IPerformancePredictor predictor,
      Features features, List<PerformanceTuple> tuplesForFeature,
      List<ConfigurationEntry> configEntriesForPrediction,
      Map<Configuration, PerformanceTuple> configTupleMap) {

    double realPerfOfPredictedBest =
        getPerformanceOfPredictedBest(predictor, features,
            configEntriesForPrediction, configTupleMap);
    double realBestPerf = getBestPerformance(tuplesForFeature);

    // Return relative performance:
    return (realPerfOfPredictedBest / realBestPerf - 1);
  }

  /**
   * Gets the best performance from the list of tuples.
   * 
   * @param tuplesForFeature
   *          the list of performance tuples characterizing this feature set
   * @return the best performance that was observed
   */
  private double getBestPerformance(List<PerformanceTuple> tuplesForFeature) {
    Collections.sort(tuplesForFeature, new ComparatorByPerformance());
    return tuplesForFeature.get(0).getPerformance();
  }

  /**
   * Retrieves the actual performance of the configuration that was predicted to
   * be the best (and would hence have been chosen).
   * 
   * @param predictor
   *          the predictor
   * @param features
   *          the problem features
   * @param configEntriesForPrediction
   *          the list of configuration entries (to be considered by predictor)
   * @param configTupleMap
   *          the map from configuration to corresponding performance tuple
   * @return the observed (past) performance of the algorithm that was deemed
   *         best
   */
  private double getPerformanceOfPredictedBest(IPerformancePredictor predictor,
      Features features, List<ConfigurationEntry> configEntriesForPrediction,
      Map<Configuration, PerformanceTuple> configTupleMap) {
    Collections.sort(configEntriesForPrediction,
        new PerformancePredictingComparator(features, predictor));
    return configTupleMap.get(configEntriesForPrediction.get(0).getConfig())
        .getPerformance();
  }

  /**
   * Creates a map from a configuration to its performance tuple. There are not
   * two tuples with the same configuration for any feature. This is ensured by
   * {@link FeatureBestDiffMeasure#noDuplicateConfigsForFeature(Map)}.
   * 
   * @param tuples
   *          the list of performance tuples
   * @return a map configuration => corresponding performance tuple
   */
  private Map<Configuration, PerformanceTuple> createConfigTupleMap(
      List<PerformanceTuple> tuples) {
    Map<Configuration, PerformanceTuple> configTupleMap =
        new HashMap<>();
    for (PerformanceTuple tuple : tuples) {
      configTupleMap.put(tuple.getConfiguration(), tuple);
    }
    return configTupleMap;
  }

  /**
   * Creates a list of configuration entries.
   * 
   * @param tuples
   *          the list of performance tuples
   * @return list of corresponding configuration entries
   */
  private List<ConfigurationEntry> createConfigEntries(
      List<PerformanceTuple> tuples) {
    List<ConfigurationEntry> result = new ArrayList<>();
    for (PerformanceTuple tuple : tuples) {
      result.add(new ConfigurationEntry(tuple.getConfiguration()));
    }
    return result;
  }

  /**
   * Checks whether there are multiple performance tuples with the same
   * configuration for a single feature. This is not allowed.
   * 
   * @param featuresTuples
   *          the map features => available performance tuples
   */
  private <T extends PerformanceTuple> boolean noDuplicateConfigsForFeature(
      Map<Features, List<T>> featuresTuples) {
    for (List<T> perfForFeature : featuresTuples.values()) {
      Set<Configuration> configurations = new HashSet<>();
      for (PerformanceTuple tuple : perfForFeature) {
        if (configurations.contains(tuple.getConfiguration())) {
          SimSystem.report(Level.SEVERE,
              "Configuration " + tuple.getConfiguration()
                  + " exists twice for features " + tuple.getFeatures());
          return false;
        }
        configurations.add(tuple.getConfiguration());
      }
    }
    return true;
  }

}

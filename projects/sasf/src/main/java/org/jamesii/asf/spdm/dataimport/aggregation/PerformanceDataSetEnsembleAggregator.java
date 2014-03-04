/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.spdm.dataimport.aggregation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.asf.spdm.Configuration;
import org.jamesii.asf.spdm.Features;
import org.jamesii.asf.spdm.dataimport.PerformanceDataSet;
import org.jamesii.asf.spdm.dataimport.PerformanceTuple;
import org.jamesii.asf.spdm.dataimport.ProblemPerformanceTuple;
import org.jamesii.perfdb.recording.performance.plugintype.PerformanceMeasurerFactory;

/**
 * Aggregator for constructing data that can be used to train predictors for
 * algorithm ensembles.
 * 
 * @author Roland Ewald
 */
public abstract class PerformanceDataSetEnsembleAggregator extends
    PerformanceDataSetAggregator<ProblemPerformanceTuple> {

  /**
   * The factory for the performance measure of interest. Is used to filter out
   * uninteresting performance.
   */
  private final Class<? extends PerformanceMeasurerFactory> perfMeasureFactory;

  /** The configurations over which the data is aggregated. */
  private final Set<Configuration> aggregatedConfigurations = new HashSet<>();

  /**
   * Instantiates a new performance data set ensemble aggregator.
   * 
   * @param performanceDataSet
   *          the performance data set
   * @param featuresToFilter
   *          the features to filter
   * @param maximizePerformance
   *          the maximize performance
   * @param performanceMeasureFactory
   *          the performance measure factory
   */
  public PerformanceDataSetEnsembleAggregator(
      PerformanceDataSet<ProblemPerformanceTuple> performanceDataSet,
      List<String> featuresToFilter, boolean maximizePerformance,
      Class<? extends PerformanceMeasurerFactory> performanceMeasureFactory) {
    super(performanceDataSet, featuresToFilter, maximizePerformance);
    perfMeasureFactory = performanceMeasureFactory;
  }

  /**
   * Adds the given configuration's performance as feature.
   * 
   * @param features
   *          the features to be augmented
   * @param perfTuple
   *          the performance tuple for the given configuration
   */
  public abstract void addConfigurationPerformanceAsFeature(Features features,
      ProblemPerformanceTuple perfTuple);

  /**
   * Calculates the desired outcome for some problem (features) and the results
   * of other configurations, stored in performance tuples. This is, for
   * example, a decision an ensemble <i>ought</i> to make, given the problem
   * features and the results of ensemble members.
   * 
   * @param problemFeatures
   *          the (original) problem features
   * @param set
   *          the set of performance tuples associated with these features
   * @return the desired outcome to be stored
   */
  public abstract double calculateDesiredOutcome(Features problemFeatures,
      List<ProblemPerformanceTuple> set);

  /**
   * Gets the ensemble configuration. Needs to be unique only in the scope where
   * the generated predictor is used (e.g. even using an empty configuration
   * could be fine - if not, just override this method).
   * 
   * @return the ensemble configuration
   */
  public Configuration getEnsembleConfig() {
    return new Configuration();
  }

  @Override
  public List<PerformanceTuple> aggregatePerformanceTuples(
      List<ProblemPerformanceTuple> instances) {

    Map<Long, Features> featuresByProblem = new HashMap<>();
    Map<Long, Features> oldFeaturesByProblem = new HashMap<>();
    Map<Long, List<ProblemPerformanceTuple>> tuplesByProblem = new HashMap<>();

    List<ProblemPerformanceTuple> aggregatedInstances =
        aggregateReplications(instances);

    for (ProblemPerformanceTuple perfTuple : aggregatedInstances) {

      // Filter all unsuitable performance data
      if (!perfMeasureFactory.isAssignableFrom(perfTuple
          .getPerfMeasureFactory())) {
        continue;
      }

      registerTupleInMaps(perfTuple, featuresByProblem, oldFeaturesByProblem,
          tuplesByProblem);

      // Add configuration performance as feature
      addConfigurationPerformanceAsFeature(
          featuresByProblem.get(perfTuple.getProblemDefinitionID()), perfTuple);
      aggregatedConfigurations.add(perfTuple.getConfiguration());
    }

    checkFeaturesAndPerformanceTuples(featuresByProblem, tuplesByProblem);

    return createAggregatedTuples(featuresByProblem, oldFeaturesByProblem,
        tuplesByProblem);

  }

  /**
   * Aggregates the results of different replications for a set of problems and
   * problem solvers, i.e. the result contains each combination of problem and
   * solver only once.
   * 
   * @param instances
   *          the set of instances including replications
   * @return the set of aggregated instances
   */
  private List<ProblemPerformanceTuple> aggregateReplications(
      List<ProblemPerformanceTuple> instances) {
    List<ProblemPerformanceTuple> result = new ArrayList<>();
    Map<Long, Map<Configuration, List<Double>>> map = new HashMap<>();
    Map<Long, Map<Configuration, ProblemPerformanceTuple>> resultTupleMap =
        new HashMap<>();
    for (ProblemPerformanceTuple tuple : instances) {
      Long problemID = tuple.getProblemDefinitionID();
      Configuration config = tuple.getConfiguration();
      Map<Configuration, List<Double>> configPerformanceMap =
          map.get(problemID);
      Map<Configuration, ProblemPerformanceTuple> configTupleMap =
          resultTupleMap.get(problemID);
      if (configTupleMap == null) {
        configPerformanceMap = new HashMap<>();
        configTupleMap = new HashMap<>();
        map.put(problemID, configPerformanceMap);
        resultTupleMap.put(problemID, configTupleMap);
      }
      List<Double> performanceList = configPerformanceMap.get(config);
      if (performanceList == null) {
        performanceList = new ArrayList<>();
        ProblemPerformanceTuple resultTuple =
            new ProblemPerformanceTuple(tuple.getProblemSchemeURI(),
                tuple.getProblemSchemeParameters(),
                tuple.getProblemDefinitionID(),
                tuple.getProblemDefinitionParameters(), tuple.getFeatures(),
                config, perfMeasureFactory, 0.0);
        configTupleMap.put(config, resultTuple);
        configPerformanceMap.put(config, performanceList);
      }
      performanceList.add(tuple.getPerformance());
    }

    for (Map.Entry<Long, Map<Configuration, List<Double>>> entry : map
        .entrySet()) {
      Long problemId = entry.getKey();
      for (Map.Entry<Configuration, List<Double>> entry2 : entry.getValue()
          .entrySet()) {
        Configuration config = entry2.getKey();
        List<Double> performances = entry2.getValue();
        Double performance = getAggregatedPerformance(performances);
        ProblemPerformanceTuple resultTuple =
            resultTupleMap.get(problemId).get(config);
        resultTuple.setPerformance(performance);
        result.add(resultTuple);
      }
    }
    return result;
  }

  /**
   * Aggregates a list of performances (from different replications).
   * 
   * @param performances
   *          the perfromances from the repliactions
   * @return the aggregated performance
   */
  protected abstract Double getAggregatedPerformance(List<Double> performances);

  /**
   * Some sanity checks for the performance data that was analyzed: is the
   * number of new features the same? Is the number of performance tuples per
   * feature the same?.
   * 
   * @param featuresByProblem
   *          the features per problem
   * @param tuplesByProblem
   *          the tuples per problem
   */
  private void checkFeaturesAndPerformanceTuples(
      Map<Long, Features> featuresByProblem,
      Map<Long, List<ProblemPerformanceTuple>> tuplesByProblem) {

    Integer newFeatureSize = null;
    Integer numOfPerformanceTuples = null;

    for (Entry<Long, Features> features : featuresByProblem.entrySet()) {
      Long problemID = features.getKey();
      Features aggregatedFeatures = features.getValue();

      // Initialize size
      if (newFeatureSize == null) {
        newFeatureSize = aggregatedFeatures.size();
        numOfPerformanceTuples = tuplesByProblem.get(problemID).size();
        continue;
      }

      // Check size
      int featureSize = aggregatedFeatures.size();
      int perfTupleSetSize = tuplesByProblem.get(problemID).size();
      if (newFeatureSize != featureSize) {
        SimSystem.report(Level.WARNING, "For some problems there are "
            + newFeatureSize
            + " (new) features, but for the problem with features '"
            + problemID + "' there are " + featureSize + " new features.");
      }
      if (numOfPerformanceTuples != perfTupleSetSize) {
        SimSystem.report(Level.WARNING, "For some problems there are "
            + numOfPerformanceTuples
            + " performance tuples, but for the problem with features '"
            + problemID + "' there are " + perfTupleSetSize + ".");
      }
    }
  }

  /**
   * Creates the aggregated tuples.
   * 
   * @param featuresByProblem
   *          the features per problem
   * @param oldFeaturesByProblem
   *          the old feature per problem
   * @param tuplesByProblem
   *          the tuples per problem
   * @return the list
   */
  private List<PerformanceTuple> createAggregatedTuples(
      Map<Long, Features> featuresByProblem,
      Map<Long, Features> oldFeaturesByProblem,
      Map<Long, List<ProblemPerformanceTuple>> tuplesByProblem) {
    Configuration config = getEnsembleConfig();

    List<PerformanceTuple> aggregatedTuples = new ArrayList<>();
    for (Entry<Long, Features> featuresEntry : featuresByProblem.entrySet()) {

      Long problemID = featuresEntry.getKey();
      PerformanceTuple newPerformanceTuple =
          new PerformanceTuple(featuresEntry.getValue(), config, null,
              calculateDesiredOutcome(oldFeaturesByProblem.get(problemID),
                  tuplesByProblem.get(problemID)));

      aggregatedTuples.add(newPerformanceTuple);
    }

    return aggregatedTuples;
  }

  /**
   * Register problem tuple in auxiliary maps.
   * 
   * @param perfTuple
   *          the performance tuple
   * @param featuresByProblem
   *          the features by problem id
   * @param oldFeaturesByProblem
   *          the old (original) problem features
   * @param tuplesByProblem
   *          the tuples by problem id
   */
  private void registerTupleInMaps(ProblemPerformanceTuple perfTuple,
      Map<Long, Features> featuresByProblem,
      Map<Long, Features> oldFeaturesByProblem,
      Map<Long, List<ProblemPerformanceTuple>> tuplesByProblem) {

    if (newProblemFeaturesRegistered(perfTuple, featuresByProblem,
        oldFeaturesByProblem)) {
      tuplesByProblem.put(perfTuple.getProblemDefinitionID(),
          new ArrayList<ProblemPerformanceTuple>());
    }

    tuplesByProblem.get(perfTuple.getProblemDefinitionID()).add(perfTuple);
  }

  /**
   * Registers new problem features if necessary.
   * 
   * @param perfTuple
   *          the performance tuple to be aggregated
   * @param featuresByProblem
   *          the features by problem map
   * @param oldFeaturesByProblem
   *          the old features by problem map
   * @return true, if new problem features needed to be registered
   */
  private boolean newProblemFeaturesRegistered(
      ProblemPerformanceTuple perfTuple, Map<Long, Features> featuresByProblem,
      Map<Long, Features> oldFeaturesByProblem) {
    if (featuresByProblem.containsKey(perfTuple.getProblemDefinitionID())) {
      return false;
    }

    Features newFeatures = filterFeaturesToAggregate(perfTuple.getFeatures());
    featuresByProblem.put(perfTuple.getProblemDefinitionID(), newFeatures);
    oldFeaturesByProblem.put(perfTuple.getProblemDefinitionID(),
        perfTuple.getFeatures());
    return true;
  }

  /**
   * Filter features to aggregate.
   * 
   * @see PerformanceDataSetAggregator
   * 
   * @param originalFeatures
   *          the original problem features
   * @return the filtered features
   */
  private Features filterFeaturesToAggregate(Features originalFeatures) {
    Features filteredFeatures = new Features();
    for (Entry<String, Serializable> feature : originalFeatures.entrySet()) {
      if (featureIsPermissible(feature.getKey())) {
        filteredFeatures.put(feature.getKey(), feature.getValue());
      }
    }
    return filteredFeatures;
  }

  /**
   * Checks whether feature is permissible.
   * 
   * @param featureName
   *          the feature name
   * @return true, if feature is permissible
   */
  private boolean featureIsPermissible(String featureName) {
    for (String forbiddenSubstring : getFilterFeatures()) {
      if (featureName.contains(forbiddenSubstring)) {
        return false;
      }
    }
    return true;
  }

  public Set<Configuration> getAggregatedConfigurations() {
    return Collections.unmodifiableSet(aggregatedConfigurations);
  }
}

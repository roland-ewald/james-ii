/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.spdm.generators.ensemble;

import java.util.HashMap;
import java.util.Map;

import org.jamesii.asf.spdm.Configuration;
import org.jamesii.asf.spdm.Features;
import org.jamesii.asf.spdm.generators.IPerformancePredictor;


/**
 * Predictor that relies on an ensemble of {@link Configuration}-specific
 * {@link IPerformancePredictor} instances to predict performance for a set of
 * {@link Configuration} instances. Unknown {@link Configuration} instances will
 * be predicted to have a runtime of {@link Double#POSITIVE_INFINITY}.
 * 
 * @author Roland Ewald
 * 
 */
public class EnsemblePerfPredictor implements IPerformancePredictor {

  /** Serialisation ID. */
  private static final long serialVersionUID = -1961496192308816283L;

  /**
   * This is a mapping from configurations to the performance predictor that
   * shall be used to predict the configuration's performance in the feature
   * space.
   */
  private final Map<Configuration, IPerformancePredictor> predictors =
      new HashMap<>();

  /**
   * Constructor for bean compliance. Do not use manually.
   */
  public EnsemblePerfPredictor() {
  }

  /**
   * Default constructor.
   * 
   * @param predictorMap
   *          the map from configurations to their performance predictors
   */
  public EnsemblePerfPredictor(
      Map<Configuration, IPerformancePredictor> predictorMap) {
    predictors.putAll(predictorMap);
  }

  @Override
  public double predictPerformance(Features features, Configuration config) {
    if (!predictors.containsKey(config)) {
      return Double.POSITIVE_INFINITY;
    }
    IPerformancePredictor predictor = predictors.get(config);
    return predictor.predictPerformance(features, config);
  }

  public Map<Configuration, IPerformancePredictor> getPredictors() {
    return new HashMap<>(predictors);
  }

  public void setPredictors(Map<Configuration, IPerformancePredictor> predictors) {
    this.predictors.clear();
    this.predictors.putAll(predictors);
  }

}

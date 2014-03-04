/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.spdm.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jamesii.asf.spdm.Configuration;
import org.jamesii.asf.spdm.Features;
import org.jamesii.asf.spdm.generators.IPerformancePredictor;


/**
 * Performance predictor that assigns fixed weights to configurations.
 * 
 * When used with a selector, the selector will always select the same (fixed)
 * sequence of {@link Configuration} entities.
 * 
 * @author Roland Ewald
 * 
 */
public class FixedPerfPredictor implements IPerformancePredictor {

  /** Serialisation ID. */
  private static final long serialVersionUID = 7996911085579247197L;

  /** The map containing the weights of the configurations. */
  private Map<Configuration, Integer> weights =
      new HashMap<>();

  /**
   * Constructor for bean compliance. Do NOT use manually
   */
  public FixedPerfPredictor() {
  }

  /**
   * Instantiates a new fixed performance predictor with default weights. The
   * first configuration is deemed best (largest weight, set to the number of
   * configurations in the list). Each of the following configurations is
   * weighter one less...
   * 
   * @param configs
   *          the configurations
   */
  public FixedPerfPredictor(List<Configuration> configs) {
    int bestPerformance = configs.size();
    for (int i = 0; i < configs.size(); i++) {
      weights.put(configs.get(i), bestPerformance - i);
    }
  }

  // TODO: Add constructor for the instatiation of portfolios here...

  /**
   * Not every configuration has a weight associated with it, it is possible to
   * define only a subset of the best configurations. Hence, if a configuration
   * is not in the order map its performance is very bad, and 0 is returned.
   */
  @Override
  public double predictPerformance(Features features, Configuration config) {
    Integer configWeight = weights.get(config);
    if (configWeight == null) {
      return 0;
    }
    return configWeight;
  }

  public Map<Configuration, Integer> getOrder() {
    return weights;
  }

  public void setOrder(Map<Configuration, Integer> order) {
    this.weights = order;
  }

}
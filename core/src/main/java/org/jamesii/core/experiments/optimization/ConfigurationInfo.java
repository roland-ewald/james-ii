/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.jamesii.core.experiments.optimization.parameter.Configuration;
import org.jamesii.core.model.variables.BaseVariable;

/**
 * Holds reference to a configuration, a set of responses and the objective
 * calculated for them.
 * 
 * @author Roland Ewald Date: 04.07.2007
 */
public class ConfigurationInfo implements Serializable {

  /** Generated serialVersionUID. */
  private static final long serialVersionUID = 6073139522510015546L;

  /** Configuration that was tested. */
  private Configuration configuration = null;

  /** Objectives of the responses. */
  private Map<String, Double> objectives = new HashMap<>();

  /** Simulated responses. */
  private Map<String, BaseVariable<?>> responses = null;

  /**
   * Default constructor. Objective is set to positive infinity.
   * 
   * @param config
   *          the configuration
   * @param results
   *          the responses
   */
  public ConfigurationInfo(Configuration config,
      Map<String, BaseVariable<?>> results) {
    this.configuration = config;
    this.responses = results;
  }

  /**
   * Full constructor.
   * 
   * @param config
   *          the configuration
   * @param results
   *          the responses
   * @param obj
   *          the objectives
   */
  public ConfigurationInfo(Configuration config,
      Map<String, BaseVariable<?>> results, Map<String, Double> obj) {
    this(config, results);
    this.objectives = obj;
  }

  /**
   * Gets the configuration.
   * 
   * @return the configuration
   */
  public Configuration getConfiguration() {
    return configuration;
  }

  /**
   * Gets the objective.
   * 
   * @return the objectives
   */
  public Map<String, Double> getObjectives() {
    return objectives;
  }

  /**
   * Gets the responses.
   * 
   * @return the responses
   */
  public Map<String, BaseVariable<?>> getResponses() {
    return responses;
  }

  /**
   * Sets the configuration.
   * 
   * @param configuration
   *          the new configuration
   */
  public void setConfiguration(Configuration configuration) {
    this.configuration = configuration;
  }

  /**
   * Sets the objective.
   * 
   * @param objectives
   *          the objectives
   */
  public void setObjectives(Map<String, Double> obj) {
    this.objectives = obj;
  }

  /**
   * Sets the responses.
   * 
   * @param responses
   *          the responses
   */
  public void setResponses(Map<String, BaseVariable<?>> responses) {
    this.responses = responses;
  }

}

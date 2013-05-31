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
 * A simple storage for configurations. Stores a configuration together by using
 * a key generated from the assignment of the factors of the configuration.
 * 
 * @author Jan Himmelspach (based on ideas from Arvid Schwecke)
 */
public class SimpleConfigurationStorage implements IConfigurationStorage,
    Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 5067477310499103124L;

  /** The memory. */
  private Map<String, ConfigurationInfos> data = new HashMap<>();

  @Override
  public void addConfiguration(ConfigurationInfos runInfo) {
    data.put(getIdentifier(runInfo.getConfiguration()), runInfo);
  }

  @Override
  public boolean containsConfiguration(Configuration configuration) {
    return data.containsKey(getIdentifier(configuration));
  }

  @Override
  public ConfigurationInfos getConfigurationInfos(Configuration configuration) {
    return data.get(getIdentifier(configuration));
  }

  @Override
  public boolean isEmpty() {
    return data.isEmpty();
  }

  /**
   * Create an identifier from the given configuration (used to identify the
   * configuration). Thus this method is a hash function. As identifier the
   * values of the the factors are used - and thus this method relies on the
   * repeatability of the toString method of the variables used as factors.
   * 
   * @param configuration
   *          the configuration for which we need the identifier
   * 
   * @return the identifier (a string)
   */
  protected final String getIdentifier(Configuration configuration) {
    StringBuilder result = new StringBuilder();
    for (BaseVariable<?> value : configuration.values()) {
      result.append(value);
      result.append("-");
    }
    return result.toString();
  }

}

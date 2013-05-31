/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization;

import org.jamesii.core.experiments.optimization.parameter.Configuration;

/**
 * The interface IConfigurationStorage. Such a storage can be used to avoid the
 * redundant computation of configurations.
 * 
 * @author Jan Himmelspach (based on ideas of Arvid Schwecke.)
 * 
 *         history<br>
 *         2009 (JH) modified method names, method parameters and comments
 * 
 */
public interface IConfigurationStorage {

  /**
   * Gets the configuration infos associated with the passed configuration.
   * 
   * @param configuration
   *          the current configuration
   * 
   * @return stored objectives to configuration
   */
  ConfigurationInfos getConfigurationInfos(Configuration configuration);

  /**
   * Checks whether there is an entry for the passed configuration in the
   * storage.
   * 
   * @param configuration
   *          the configuration to check for existence
   * 
   * @return true if an entry exists
   */
  boolean containsConfiguration(Configuration configuration);

  /**
   * Add a configuration and its calculated objectives to a storage.
   * 
   * @param configInfos
   *          the configuration infos to be added
   */
  void addConfiguration(ConfigurationInfos configInfos);

  /**
   * Checks if is empty.
   * 
   * @return true, if is empty
   */
  boolean isEmpty();

}

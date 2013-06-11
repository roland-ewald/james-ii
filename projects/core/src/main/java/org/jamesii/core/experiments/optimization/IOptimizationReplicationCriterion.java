/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization;

import java.io.Serializable;
import java.util.List;

/**
 * Interface for objects that want to control the number of replications for
 * each configuration.
 * 
 * @author Peter Sievert Date: 08.08.2008
 * 
 */
public interface IOptimizationReplicationCriterion extends Serializable {

  /**
   * Returns the number of replications that are most possibly enough. This will
   * be determined by the list of replications made so far.
   * 
   * @param configInfo
   *          the configuration information of current configuration
   * @param otherConfigs
   *          configuration informations of former configurations
   * 
   * @return the (estimated) number of sufficient replications
   */
  int sufficientReplications(List<ConfigurationInfo> configInfo,
      List<ConfigurationInfos> otherConfigs);
}

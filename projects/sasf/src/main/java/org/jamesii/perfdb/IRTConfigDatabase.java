/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb;


import java.util.List;

import org.jamesii.perfdb.entities.IApplication;
import org.jamesii.perfdb.entities.IHardwareSetup;
import org.jamesii.perfdb.entities.IProblemDefinition;
import org.jamesii.perfdb.entities.IProblemInstance;
import org.jamesii.perfdb.entities.IResultDataProvider;
import org.jamesii.perfdb.entities.IRuntimeConfiguration;
import org.jamesii.perfdb.recording.selectiontrees.SelectionTree;

/**
 * Interface for runtime configuration data base.
 * 
 * @author Roland Ewald
 * 
 */
public interface IRTConfigDatabase {

  /**
   * Gets up to date runtime configuration for given selection tree.
   * 
   * @param selectionTree
   *          the selection tree defining the configuration
   * @return the runtime configuration for this tree, or null if not found
   */
  IRuntimeConfiguration getRuntimeConfig(SelectionTree selectionTree);

  /**
   * Get all configurations applied to a given problem instance.
   * 
   * @param probInstance
   *          the problem instance
   * @return list of runtime configurations applied to that instance.
   */
  List<IRuntimeConfiguration> getAllRuntimeConfigurations(
      IProblemInstance probInstance);

  /**
   * Gets the all runtime configurations.
   * 
   * @return the all runtime configurations
   */
  List<IRuntimeConfiguration> getAllRuntimeConfigs();

  /**
   * Get all runtime configurations for a simulation problem.
   * 
   * @param simProb
   *          the simulation problem
   * @return list of all runtime configurations that have been applied to this
   *         simulation problem
   */
  List<IRuntimeConfiguration> getAllRuntimeConfigs(IProblemDefinition simProb);

  /**
   * Gets the all current runtime configurations, i.e. those that are up to
   * date.
   * 
   * @param simProb
   *          the simulation problem
   * 
   * @return the all runtime configurations that are up to date
   */
  List<IRuntimeConfiguration> getAllCurrentRTConfigs(IProblemDefinition simProb);

  /**
   * Create a new runtime configuration if one with these features does not
   * exist.
   * 
   * @param selectionTree
   *          the selection tree
   * @param newVersion
   *          flag that signals if this is a new version
   * @return the (maybe newly created) runtime configuration
   */
  IRuntimeConfiguration newRuntimeConfiguration(SelectionTree selectionTree,
      boolean newVersion);

  /**
   * Creates new application for given problem instance, configuration, and
   * setup.
   * 
   * @param probInst
   *          the problem instance to which the runtime configuration shall be
   *          applied
   * @param RUNTIME_CONFIGURATION
   *          the runtime configuration to be applied
   * @param hwSetup
   *          the setup under which the configuration shall be applied to the
   *          problem instance
   * @param sdProvider
   *          an (optional) component to provide simulation data to the
   *          performance analysis (e.g., useful for validation etc.)
   * @return the new application
   */
  IApplication newApplication(IProblemInstance probInst,
      IRuntimeConfiguration rtConfig, IHardwareSetup hwSetup,
      IResultDataProvider<?> sdProvider);

  /**
   * Get all applications for a given problem instance.
   * 
   * @param probInstance
   *          the problem instance
   * @return list of all applications on that instance
   */
  List<IApplication> getAllApplications(IProblemInstance probInstance);

  /**
   * Get all applications for given problem instance and configuration.
   * 
   * @param instance
   *          the problem instance
   * @param RUNTIME_CONFIGURATION
   *          the configuration
   * @return list of all applications
   */
  List<IApplication> getAllApplications(IProblemInstance instance,
      IRuntimeConfiguration rtConfig);

  /**
   * Get all applications on instances of a given problem.
   * 
   * @param simProb
   *          the problem
   * @return list of applications
   */
  List<IApplication> getAllApplications(IProblemDefinition simProb);

  /**
   * Get all applications on instances of a given problem, which have been
   * processed by the same runtime configuration.
   * 
   * @param simProb
   *          the given simulation problem
   * @param rtConfig
   *          the runtime configuration
   * @return list of applications of the given runtime configuration to the
   *         given problem
   */
  List<IApplication> getAllApplications(IProblemDefinition simProb,
      IRuntimeConfiguration rtConfig);

}

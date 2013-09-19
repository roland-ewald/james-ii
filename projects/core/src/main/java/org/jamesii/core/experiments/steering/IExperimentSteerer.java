/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.steering;

import org.jamesii.core.experiments.RunInformation;
import org.jamesii.core.experiments.TaskConfiguration;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Interface for all components that need to steer a
 * {@link org.jamesii.core.experiments.BaseExperiment} in a certain direction by
 * changing experiment variables in a non-trivial way.
 * {@link IExperimentSteerer} components will be registered as observers of the
 * {@link org.jamesii.core.experiments.BaseExperiment}, hence they have to
 * implement the {@link rg.jamesii.core.observe.IObserver} interface. They
 * should in turn be observable entities themselves, so they also have to
 * implement the {@link org.jamesii.core.base.IEntity} interface.
 * 
 * @author Roland Ewald
 */
public interface IExperimentSteerer {

  /**
   * Initialise steerer.
   */
  void init();

  /**
   * Get the parameters for the experiment.
   * 
   * @return experiment parameters
   */
  ParameterBlock getExperimentParameters();

  /**
   * Get next assignment of variables to be executed by the base experiment.
   * 
   * @return next assignment of variables, or null otherwise
   */
  VariablesAssignment getNextVariableAssignment();

  /**
   * Decides on the end of the steering.
   * 
   * @return true, if steerer is finished defining new setups
   */
  boolean isFinished();

  /**
   * Signals if sub-structures are allowed or not.
   * 
   * @return true, if additional sub-levels are allowed
   */
  boolean allowSubStructures();

  /**
   * Notifies steerer that execution of given simulation configuration is
   * finished.
   * 
   * @param simConfig
   *          the sim config
   * @param runInformation
   *          the run information
   */
  void executionFinished(TaskConfiguration simConfig,
      RunInformation runInformation);

}

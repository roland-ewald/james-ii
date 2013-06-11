/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.simulationserver;

import java.util.HashMap;
import java.util.Map;

import org.jamesii.core.base.Entity;
import org.jamesii.core.experiments.SimulationRunConfiguration;
import org.jamesii.core.experiments.taskrunner.IRemoteComputationTaskRunner;
import org.jamesii.core.experiments.tasks.ComputationTaskIDObject;

/**
 * Manages simulation configurations etc.
 * 
 * @author Jan Himmelspach
 * 
 */
public class ServerSimulationManagement extends Entity {

  /** The Constant serialVersionuid. */
  private static final long serialVersionUID = 5809756316184539582L;

  /**
   * Contains a mapping between a simulation and the SimulationRunner which
   * calls initializeSimulation().
   */
  private final Map<ComputationTaskIDObject, IRemoteComputationTaskRunner> simulationRunners =
      new HashMap<>();

  /**
   * Contains a mapping between simulation run IDs and simulation run
   * configurations.
   */
  private final Map<ComputationTaskIDObject, SimulationRunConfiguration> simulationRunConfigs =
      new HashMap<>();

  /**
   * Add the simulation configuration for the given simulation.
   * 
   * @param config
   */
  public void addConfig(SimulationRunConfiguration config) {
    simulationRunConfigs.put(config.getComputationTaskID(), config);
    changed(config);
  }

  /**
   * Remove a simulation configuration.
   * 
   * @param uid
   */
  public void removeConfig(ComputationTaskIDObject uid) {
    simulationRunConfigs.remove(uid);
    changed(uid);
  }

  /**
   * Add the simulation runner of the simulation.
   * 
   * @param uid
   * @param runner
   */
  public void addRunner(ComputationTaskIDObject uid,
      IRemoteComputationTaskRunner runner) {
    simulationRunners.put(uid, runner);
    changed(runner);
  }

  /**
   * Remove a simulation runner.
   * 
   * @param uid
   */
  public void removeRunner(ComputationTaskIDObject uid) {
    simulationRunners.remove(uid);
    changed(uid);
  }

  /**
   * Get a reference to the simulation runner of the given simulation.
   * 
   * @param uid
   *          the uid
   * 
   * @return the runner
   */
  public IRemoteComputationTaskRunner getRunner(ComputationTaskIDObject uid) {
    return simulationRunners.get(uid);
  }

  /**
   * Get a reference to the simulation configuration of the given simulation.
   * 
   * @param id
   *          the unique identification of the simulation run to be retrieved.
   * 
   * @return the config
   */
  public SimulationRunConfiguration getConfig(ComputationTaskIDObject id) {
    return simulationRunConfigs.get(id);
  }

  /**
   * Gets the simulation config.
   * 
   * @return the simulation config
   */
  public Map<ComputationTaskIDObject, SimulationRunConfiguration> getSimulationRunConfigs() {
    return simulationRunConfigs;
  }

}

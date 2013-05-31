/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.simulationserver;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.base.Entity;
import org.jamesii.core.experiments.SimulationRunConfiguration;
import org.jamesii.core.experiments.tasks.ComputationTaskIDObject;
import org.jamesii.core.experiments.tasks.IComputationTask;
import org.jamesii.core.simulationrun.ISimulationRun;

/**
 * The Class ComputationTaskManagement.
 * 
 * @author Jan Himmelspach
 */
public class ComputationTaskManagement extends Entity {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -3366420208378312765L;

  /** The simulations. */
  private List<ISimulationRun> computationTasks = new ArrayList<>();

  /**
   * Number of simulations which have been executed on this host so far.
   */
  private long simCount = 0;

  /**
   * Adds the simulation.
   * 
   * @param simulation
   *          the simulation
   * 
   * @param config
   *          the simulation configuration
   */
  public synchronized void addComputationTask(ISimulationRun simulation,
      SimulationRunConfiguration config) {
    computationTasks.add(simulation);
    simCount++;
    changed(simulation.getUniqueIdentifier());
  }

  /**
   * Removes the simulation.
   * 
   * @param computationTask
   *          the simulation
   */
  public synchronized void removeComputationTask(
      IComputationTask computationTask) {
    if (computationTasks.size() == 0) {
      return;
    }

    ComputationTaskIDObject info = computationTask.getUniqueIdentifier();
    int c = 0;
    for (IComputationTask sim : computationTasks) {
      if (sim.getUniqueIdentifier().compareTo(info) == 0) {
        break;
      }
      c++;
    }
    if (c < computationTasks.size()) {
      computationTasks.remove(c);
      changed(info);
    }

  }

  /**
   * Clean up.
   */
  public synchronized void cleanUp() {
    computationTasks.clear();
  }

  /**
   * Gets the simulation by name.
   * 
   * @param name
   *          the name
   * 
   * @return the simulation by name
   */
  public synchronized ISimulationRun getSimulationByName(String name) {
    for (ISimulationRun sim : computationTasks) {
      // System.out.println(sim.getName());
      if (sim.getName().compareTo(name) == 0) {
        return sim;
      }
    }
    return null;
  }

  /**
   * Gets the computation task by uid.
   * 
   * @param uid
   *          the uid
   * 
   * @return the simulation by uid
   */
  public synchronized IComputationTask getComputationTaskByUid(
      ComputationTaskIDObject uid) {
    for (IComputationTask sim : computationTasks) {
      // System.out.println(sim.getName());
      if (sim.getUniqueIdentifier().equals(uid)) {
        return sim;
      }
    }
    return null;
  }

  /**
   * Get a list of information about running computation tasks.
   * 
   * @return list with simulationInfoObjects
   */
  public synchronized List<ComputationTaskIDObject> getRunningComputationTasks() {
    List<ComputationTaskIDObject> result = new ArrayList<>();
    for (IComputationTask run : computationTasks) {
      result.add(run.getUniqueIdentifier());
    }
    return result;
  }

}

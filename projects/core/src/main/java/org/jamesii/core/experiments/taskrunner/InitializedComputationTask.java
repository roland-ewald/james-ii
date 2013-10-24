/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.taskrunner;

import java.io.Serializable;

import org.jamesii.core.experiments.RunInformation;
import org.jamesii.core.experiments.tasks.IComputationTask;
import org.jamesii.core.experiments.tasks.IInitializedComputationTask;

/**
 * The initialised computation task holds references to a created
 * {@link org.jamesii.core.experiments.tasks.IComputationTask} object and its
 * performance information ( {@link org.jamesii.core.experiments.RunInformation}
 * ).
 * 
 * @author Stefan Leye
 */
public class InitializedComputationTask implements Serializable,
    IInitializedComputationTask {

  /** The serialisation ID. */
  private static final long serialVersionUID = -4770172799931280263L;

  /** Reference to performance information. */
  private final RunInformation runInfo;

  /** The simulation. */
  private IComputationTask computationTask = null;

  /**
   * Instantiates a new initialised simulation.
   * 
   * @param computationTask
   *          the simulation run
   * @param rInfo
   *          the runtime information
   */
  public InitializedComputationTask(IComputationTask computationTask,
      RunInformation rInfo) {
    this.computationTask = computationTask;
    this.runInfo = rInfo;
  }

  @Override
  public IComputationTask getComputationTask() {
    return computationTask;
  }

  // /**
  // * Starts the simulation.
  // */
  // public void startSimulation() {
  // if (simulation != null) {
  // simulation.start();
  // } else {
  // System.out.print("No Simulation has been initialized!!!");
  // }
  // }

  @Override
  public RunInformation getRunInfo() {
    return runInfo;
  }
}

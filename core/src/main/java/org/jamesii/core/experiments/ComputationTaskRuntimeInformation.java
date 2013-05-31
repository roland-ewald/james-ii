/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments;

import java.io.Serializable;
import java.util.List;

import org.jamesii.core.experiments.tasks.ComputationTaskIDObject;
import org.jamesii.core.experiments.tasks.IComputationTask;
import org.jamesii.core.observe.IObservable;
import org.jamesii.core.observe.IObserver;
import org.jamesii.gui.utils.ListenerSupport;

/**
 * This class contains all information that is essential to manage the execution
 * of a computation task at runtime.
 * 
 * @author Roland Ewald
 */
public class ComputationTaskRuntimeInformation implements Serializable {

  /** Serialisation ID. */
  private static final long serialVersionUID = 1L;

  /**
   * Reference to a running processor that can be used to pause/stop/re-start
   * the computation task. Could also be a proxy to the actual processor, that
   * doesn't matter here. TODO replace by reference to the computation task
   * runner, and there should be a method allowing to call the stuff on the sim
   * run, the sim run of a distributed simulation will not be available here
   */
  private IComputationTask computationTask;

  /** The ID of the computation task. */
  private ComputationTaskIDObject computationTaskID;

  /** The computation task configuration concerned. */
  private final IComputationTaskConfiguration computationTaskConfiguration;

  /** The computation task configuration. */
  private final TaskConfiguration taskConfiguration;

  /**
   * Stores performance data, execution IDs, and link to the data storage that
   * was used.
   */
  private final RunInformation runInformation;

  /**
   * The current status of the computation job. Components that are interested
   * in any state change can be registered.
   */
  private ComputationRuntimeState state = ComputationRuntimeState.INITIALIZED;

  /** List of listeners for the execution. */
  private ListenerSupport<IComputationTaskExecutionListener> execListeners =
      new ListenerSupport<>();

  /**
   * Default constructor.
   * 
   * @param compTaskConfig
   *          the executed configuration
   * @param taskConfig
   *          the executed configuration
   * @param computationTask
   *          the computation task
   * @param runInfo
   *          run information
   * @param compuationTaskID
   *          the (unique) simulation id
   */
  public ComputationTaskRuntimeInformation(
      IComputationTaskConfiguration compTaskConfig,
      TaskConfiguration taskConfig, IComputationTask computationTask,
      ComputationTaskIDObject compuationTaskID, RunInformation runInfo) {
    computationTaskConfiguration = compTaskConfig;
    taskConfiguration = taskConfig;
    this.computationTask = computationTask;
    this.computationTaskID = compuationTaskID;
    runInformation = runInfo;
  }

  /**
   * Gets the ID of the computation task.
   * 
   * @return the computationTaskID
   */
  public ComputationTaskIDObject getComputationTaskID() {
    return computationTaskID;
  }

  /**
   * Gets the model observers.
   * 
   * @return the model observers
   */
  public List<IObserver<? extends IObservable>> getModelObservers() {
    return computationTaskConfiguration == null ? null
        : computationTaskConfiguration.getModelObservers();
  }

  /**
   * Gets the simulation configuration.
   * 
   * @return the simulation configuration
   */
  public IComputationTaskConfiguration getSimulationRunConfiguration() {
    return computationTaskConfiguration == null ? null
        : computationTaskConfiguration;
  }

  /**
   * Gets the computation task observers. Reads the value from
   * 
   * @return a list of computation task observers
   */
  public List<IObserver<? extends IObservable>> getComputationTaskObservers() {
    return computationTaskConfiguration == null ? null
        : computationTaskConfiguration.getComputationTaskObservers();
  }

  /**
   * Gets the computation task.
   * 
   * @return the instance of the computation task this instance holds the
   *         runtime information of
   */
  public IComputationTask getComputationTask() {
    return computationTask;
  }

  /**
   * Should be called when the associated computation task has finished, to
   * avoid memory leaks. Removes instantiated observers and the references to
   * the computation task.
   */
  public void computationTaskFinished() {
    computationTask = null;
    if (computationTaskConfiguration != null) {
      computationTaskConfiguration.finished();
    }
    setState(ComputationRuntimeState.FINISHED);
  }

  /**
   * Gets the run information.
   * 
   * @return the run information
   */
  public RunInformation getRunInformation() {
    return runInformation;
  }

  /**
   * Gets the current state.
   * 
   * @return the current state
   */
  public ComputationRuntimeState getState() {
    return state;
  }

  /**
   * Sets the current state.
   * 
   * @param stat
   *          the new state
   */
  public void setState(ComputationRuntimeState stat) {
    this.state = stat;
    for (IComputationTaskExecutionListener execListener : execListeners) {
      execListener.stateChanged(this);
    }
  }

  /**
   * Adds a computation task execution listener.
   * 
   * @param execListener
   *          the execution listener to be added
   */
  public void addComputationTaskExecutionListener(
      IComputationTaskExecutionListener execListener) {
    execListeners.add(execListener);
  }

  /**
   * Removes a computation task execution listener.
   * 
   * @param execListener
   *          the execution listener to be removed
   */
  public void removeComputationTaskExecutionListener(
      IComputationTaskExecutionListener execListener) {
    execListeners.remove(execListener);
  }

  /**
   * Determines if a computation task associated with a given SRTI can be
   * controlled.
   * 
   * @param srti
   *          the simulation runtime information
   * 
   * @return true, if simulation can be controlled
   */
  public static boolean computationTaskControlPossible(
      ComputationTaskRuntimeInformation srti) {
    return srti != null && srti.getComputationTask() != null
        && srti.getComputationTask().isProcessorRunnable();
  }

  /**
   * Gets the computation task configuration.
   * 
   * @return the computation task configuration
   */
  public TaskConfiguration getComputationTaskConfiguration() {
    return taskConfiguration;
  }

}

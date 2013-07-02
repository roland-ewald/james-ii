/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.taskrunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.base.Entity;
import org.jamesii.core.data.model.IModelReader;
import org.jamesii.core.data.storage.IDataStorage;
import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.core.experiments.ComputationTaskRuntimeInformation;
import org.jamesii.core.experiments.ExperimentException;
import org.jamesii.core.experiments.IExperimentExecutionController;
import org.jamesii.core.experiments.TaskConfiguration;
import org.jamesii.core.experiments.tasks.ComputationTaskHook;
import org.jamesii.core.experiments.tasks.ComputationTaskIDObject;
import org.jamesii.core.experiments.tasks.IComputationTask;
import org.jamesii.core.experiments.tasks.IInitializedComputationTask;
import org.jamesii.core.experiments.tasks.InvalidComputationTaskConfiguration;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.simulation.launch.InteractiveConsole;

/**
 * Base class for all task runners. A {@link ITaskRunner} gets
 * {@link TaskConfiguration} instances and initialises/executes
 * {@link IInitializedComputationTask} / {@link IComputationTask} entities
 * according to them.
 * 
 * @author Roland Ewald
 */
public abstract class AbstractTaskRunner implements ITaskRunner {

  /**
   * If cancelOnError is set to true any error during execution will cancel the
   * complete experiment.
   */
  private boolean cancelOnError = false;

  /** This hook can be set for sending on error information to someone. */
  private transient ComputationTaskHook<String> errorHook = null;

  /** Counts the executed configurations. */
  private int configCounter = 0;

  /**
   * Model reader parameters for last model that was computed (to prevent
   * frequent re-selection and re-instantiation of model reader).
   */
  private ParameterBlock absModReaderParams = null;

  /** Map to store the experiment instance that spawned the job. */
  private Map<TaskConfiguration, IExperimentExecutionController> experimentControllers =
      new Hashtable<>();

  /**
   * Mapping from configurations to the data storage of their execution
   * controllers.
   */
  private Map<TaskConfiguration, IDataStorage<?>> compTaskConfigDSs =
      new Hashtable<>();

  /** Support for console mode. */
  private transient InteractiveConsole interactiveConsole;

  /** Model reader to be used. */
  private IModelReader modelReader = null;

  /** List of all task configurations to be done. */
  private List<TaskConfiguration> todoList = Collections
      .synchronizedList(new ArrayList<TaskConfiguration>());

  /**
   * Internal flag which will be set if the stop() has been called This flags
   * indicates that the experiment should be cancelled as soon as possible.
   */
  private boolean stopping = false;

  /** Flag to determine whether the computation task runner is pausing. */
  private boolean pausing = false;

  /**
   * Main function of the runner. Waits until jobs arrive in the job queue (
   * {@link #scheduleConfigurations(IExperimentExecutionController, java.util.List)}
   * , which is called by method runExperiment of class {@link BaseExperiment}).
   */
  @Override
  public void run() {
    while (!isStopping()) {
      // the iteration of the replications happens sequentially!
      // therefore, the waiting happens after the replications all jobs have
      // been initiated.
      while (!isIdle() && !isPausing()) {
        TaskConfiguration task = getTodoList().remove(0);
        try {
          executeConfiguration(task);
        } catch (Throwable t) { // NOSONAR:{robustness_otherwise_deadlock_possible}
          SimSystem
              .report(
                  Level.SEVERE,
                  "Invalid task runner behaviour, experiment execution controller has to be notified, i.e., this exception should be caught and dealt with earlier. Shutting down corresponding experiment controller.",
                  t);
          experimentControllers.get(task).stop(true);
        }
      }

      try {
        // Wait until someone wakes you up with more work to do
        synchronized (this) {
          if (isIdle() && !isStopping()) {
            wait();
          }
        }
      } catch (InterruptedException ex) {
        SimSystem.report(ex);
      }
    }
  }

  /**
   * Execute a single configuration.
   * 
   * @param config
   *          the configuration to be executed once
   */
  protected abstract void executeConfiguration(TaskConfiguration config);

  @Override
  public ComputationTaskHook<String> getErrorHook() {
    return errorHook;
  }

  /**
   * Gets the data storage.
   * 
   * @param config
   *          the config
   * 
   * @return the data storage
   */
  public IDataStorage<?> getDataStorage(TaskConfiguration config) {
    return this.compTaskConfigDSs.get(config);
  }

  @Override
  public boolean isCancelOnError() {
    return cancelOnError;
  }

  @Override
  public boolean isStopping() {
    return stopping;
  }

  /**
   * @param stopping
   *          the stopping to set
   */
  protected void setStopping(boolean stopping) {
    this.stopping = stopping;
  }

  @Override
  public boolean isIdle() {
    return todoList.isEmpty();
  }

  @Override
  public boolean isPausing() {
    return pausing;
  }

  /**
   * Executes error hook, e.g. for sending notification e-mails
   * 
   * @param message
   *          The error message
   */
  @Override
  public void onErrorHook(String message) {
    if (errorHook != null) {
      errorHook.execute(message);
    }
  }

  @Override
  public void setCancelOnError(boolean cancelOnError) {
    this.cancelOnError = cancelOnError;
  }

  @Override
  public void setErrorHook(ComputationTaskHook<String> errorHook) {
    this.errorHook = errorHook;
  }

  @Override
  public synchronized void stop() {
    stopping = true;
    notifyAll();
  }

  /**
   * Prints out the number of the current execution.
   */
  public void reportConfigExecution() {
    SimSystem.report(Level.INFO, "@@@@@@@@@@@@@@@@ executing configuration #"
    + ++configCounter + " @@@@@@@@@@@@@@@@");
  }

  @Override
  public void runTask(ComputationTaskRuntimeInformation taskInfo) {
    processNotification(taskInfo, true);
  }

  @Override
  public synchronized void scheduleConfigurations(
      IExperimentExecutionController execController,
      List<TaskConfiguration> taskConfigurations) {
    for (TaskConfiguration taskConfig : taskConfigurations) {
      if (taskConfig == null) {
        throw new InvalidComputationTaskConfiguration(
            "Invalid computation task configuration (null) was added to the task runner. Execution aborted.");
      }
      addExperimentController(taskConfig, execController);
      todoList.add(taskConfig);
    }
    this.notifyAll();
  }

  /**
   * Returns the parameters for the model reader.
   * 
   * @return ParameterBlock of the model reader
   */
  protected ParameterBlock getAbsModReaderParams() {
    return absModReaderParams;
  }

  /**
   * Sets the abstract model reader parameters.
   * 
   * @param absModReaderParams
   *          the new abstract model reader parameters
   */
  protected void setAbsModReaderParams(ParameterBlock absModReaderParams) {
    this.absModReaderParams = absModReaderParams;
  }

  /**
   * Gets the execution controller for a task configuration.
   * 
   * @param conf
   *          the task configuration
   * 
   * @return execution controller of it
   */
  protected IExperimentExecutionController getExperimentController(
      TaskConfiguration conf) {
    return experimentControllers.get(conf);
  }

  /**
   * Returns an iterator for all task configurations to be processed by the
   * runner.
   * 
   * @return iterator for the task configurations
   */
  protected Iterator<TaskConfiguration> getTaskConfigurationIterator() {
    return this.experimentControllers.keySet().iterator();
  }

  /**
   * Adds an execution controller for a specific task configuration (to the map
   * of controllers and configurations).
   * 
   * @param config
   *          the task configuration
   * @param experimentController
   *          the experiment controller
   */
  protected void addExperimentController(TaskConfiguration config,
      IExperimentExecutionController experimentController) {
    experimentControllers.put(config, experimentController);
  }

  /**
   * Removes the experiment controller.
   * 
   * @param config
   *          the task configuration
   */
  protected void removeExperimentController(TaskConfiguration config) {
    experimentControllers.remove(config);
    IDataStorage<?> storage = compTaskConfigDSs.remove(config);
    try {
      if (storage != null) {
        storage.flushBuffers();
        storage.configurationDone(config.getUniqueID());
      }

    } catch (Exception e) {
      SimSystem.report(e);
    }
  }

  /**
   * Gets the id of the computation task.
   * 
   * @param config
   *          the task configuration
   * 
   * @return the computation task id object (a mapping between the uid and the
   *         long id which might be used by the storages)
   */
  public ComputationTaskIDObject getComputationTaskID(TaskConfiguration config) {
    if (config.hasDataStorage()) {

      IDataStorage<?> storage;
      if (!compTaskConfigDSs.containsKey(config)) {
        synchronized (compTaskConfigDSs) {
          // as any number of threads might be waiting another one might have
          // passed the following and this we should check whether we can skip
          // this
          storage = getDataStorage(config);
          if (storage == null) {
            storage = config.createPlainDataStorage();
            storage.setExperimentID(config.getExperimentID());
            storage.setConfigurationID(config.getExperimentID(),
                config.getUniqueID());
            // if the storage is instantiated the experiment ID is set once; the
            // contract does not allow to change the experiment of a storage;
            // this would mean that the storage is shared among experiments and
            // this breaks all those methods where you do not have to explicitly
            // specify the experiment number
            compTaskConfigDSs.put(config, storage);
          }
        }
      } else {
        storage = getDataStorage(config);
      }

      ComputationTaskIDObject result = new ComputationTaskIDObject(storage);
      return result;
    }
    return new ComputationTaskIDObject();
  }

  /**
   * Gets the interactive console.
   * 
   * @return the interactive console
   */
  public InteractiveConsole getInteractiveConsole() {
    return interactiveConsole;
  }

  /**
   * Sets the interactive console.
   * 
   * @param interactiveConsole
   *          the new interactive console
   */
  protected void setInteractiveConsole(InteractiveConsole interactiveConsole) {
    this.interactiveConsole = interactiveConsole;
  }

  /**
   * Returns the model reader.
   * 
   * @return the model reader
   */
  protected IModelReader getModelReader() {
    return modelReader;
  }

  /**
   * Sets the model reader.
   * 
   * @param modelReader
   *          the model reader
   */
  protected void setModelReader(IModelReader modelReader) {
    this.modelReader = modelReader;
  }

  /**
   * Returns the list of computations tasks to be processed.
   * 
   * @return to-do list of tasks
   */
  protected List<TaskConfiguration> getTodoList() {
    return todoList;
  }

  /**
   * Sets the list of computation tasks to be processed.
   * 
   * @param todoList
   *          the to-do list
   */
  protected void setTodoList(List<TaskConfiguration> todoList) {
    this.todoList = todoList;
  }

  /**
   * Process notification.
   * 
   * @param compTaskRuntimeInformation
   *          the computation task runtime information
   * @param run
   *          true if computation task shall be started, false if it shall be
   *          canceled
   */
  protected abstract void processNotification(
      ComputationTaskRuntimeInformation compTaskRuntimeInformation, boolean run);

  /**
   * Checks for a failure an notifies the error hook.
   * 
   * @param runner
   *          the actual runner
   * @param initCompTask
   *          the initialized computation task
   * @return true if there was a failure
   * @throws ExperimentException
   *           if runner should halt on error
   */
  public static boolean checkForFailure(ITaskRunner runner,
      IInitializedComputationTask initCompTask) {
    String s = initCompTask.getRunInfo().getErrorMsg();
    if (s != null) {
      runner.onErrorHook(s);
      if (runner.isCancelOnError()) {
        throw new ExperimentException(
            "An error occured. The experiment is cancelled:" + s, initCompTask
                .getRunInfo().getErrorCause());
      }
    }
    return s != null;
  }

  /**
   * @param pausing
   *          the pausing to set
   */
  protected void setPausing(boolean pausing) {
    this.pausing = pausing;
  }

}

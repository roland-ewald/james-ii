/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.taskrunner.sequential;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.data.model.read.plugintype.AbstractModelReaderFactory;
import org.jamesii.core.data.model.read.plugintype.ModelReaderFactory;
import org.jamesii.core.data.storage.IDataStorage;
import org.jamesii.core.experiments.ComputationTaskRuntimeInformation;
import org.jamesii.core.experiments.IComputationTaskConfiguration;
import org.jamesii.core.experiments.IExperimentExecutionController;
import org.jamesii.core.experiments.RunInformation;
import org.jamesii.core.experiments.TaskConfiguration;
import org.jamesii.core.experiments.taskrunner.AbstractTaskRunner;
import org.jamesii.core.experiments.taskrunner.ComputationTaskHandler;
import org.jamesii.core.experiments.taskrunner.InitializedComputationTask;
import org.jamesii.core.experiments.tasks.ComputationTaskIDObject;
import org.jamesii.core.experiments.tasks.IComputationTask;
import org.jamesii.core.experiments.tasks.IInitializedComputationTask;
import org.jamesii.core.util.exceptions.OperationNotSupportedException;

/**
 * This class takes {@link TaskConfiguration}s and executes them sequentially.
 * 
 * @author Roland Ewald 05.06.2007
 */
public class SequentialComputationTaskRunner extends AbstractTaskRunner {

  /** If true, the current configuration will be aborted. */
  private boolean abortCurrentConfig = false;

  /** Runtime info on the current computation task to be executed. */
  private ComputationTaskRuntimeInformation currentTRTI = null;

  /**
   * Reference to TRTI that was allowed to be run by the execution control
   * thread.
   */
  private ComputationTaskRuntimeInformation notifiedTRTI = null;

  /**
   * Flag that determines whether the initialised computation task corresponding
   * to currentTRTI should be run (true) or cancelled (false).
   */
  private boolean runComputationTask = true;

  /**
   * Instantiates a new sequential computation task runner.
   * 
   * @throws RemoteException
   *           the remote exception
   */
  protected SequentialComputationTaskRunner() throws RemoteException {
    super();
  }

  @Override
  public boolean cancelAllJobs(IExperimentExecutionController expController) {

    synchronized (this) {
      setPausing(true); // prevent any new replication from being executed
      if (currentTRTI == null) {
        return true;
      }
      IExperimentExecutionController controller =
          getExperimentController(currentTRTI.getComputationTaskConfiguration());
      if (controller != null && controller.equals(expController)) {
        abortCurrentConfig = true;
        IComputationTaskConfiguration config =
            currentTRTI.getSimulationRunConfiguration();
        if (config.useMasterServer()) {
          try {
            config.getMasterServer().stop(currentTRTI.getComputationTaskID());
          } catch (Exception e) {
            SimSystem.report(Level.SEVERE,
                "Couldn't stop the computation task!");
          }
        } else {
          IComputationTask sim = currentTRTI.getComputationTask();
          if (sim != null) {
            sim.stopProcessor();
          }
        }
      }

      // Clear to-do list
      List<TaskConfiguration> filteredList = new ArrayList<>();
      for (TaskConfiguration taskConfig : getTodoList()) {
        if (!getExperimentController(taskConfig).equals(expController)) {
          filteredList.add(taskConfig);
        } else {
          removeExperimentController(taskConfig);
        }
      }

      setTodoList(filteredList);
      this.notifyAll();
    }
    return true;
  }

  @Override
  public void cancelConfiguration(TaskConfiguration taskConfiguration) {
    while (getTodoList().remove(taskConfiguration)) {
      ;
    }
  }

  @Override
  public void cancelTask(
      ComputationTaskRuntimeInformation taskRuntimeInformation) {
    processNotification(taskRuntimeInformation, false);
  }

  /**
   * Executes a computation task configuration. Before execution it waits for
   * notification form the {@link IExperimentExecutionController}.
   * 
   * @param taskConfig
   *          the computation task configuration
   */
  @Override
  protected void executeConfiguration(TaskConfiguration taskConfig) {
    List<List<RunInformation>> runInformation = new ArrayList<>();

    // Create new model reader if necessary

    if (getAbsModReaderParams() == null
        || !getAbsModReaderParams().equals(taskConfig.getModelReaderParams())) {
      ModelReaderFactory modelReaderWriterFactory =
          SimSystem.getRegistry().getFactory(AbstractModelReaderFactory.class,
              taskConfig != null ? taskConfig.getModelReaderParams() : null);

      if (modelReaderWriterFactory == null) {
        SimSystem.report(Level.SEVERE, "Model reader creation failed");
        handleEarlyAbort(taskConfig, null);
        return;
      }

      setModelReader(modelReaderWriterFactory.create(taskConfig
          .getCustomRWParams()));

      setAbsModReaderParams(taskConfig.getModelReaderParams());
    }

    RunInformation currentResult = null;
    List<RunInformation> currentResults = new ArrayList<>();
    runInformation.add(currentResults);

    reportConfigExecution();

    abortCurrentConfig = false;
    int replications = taskConfig.allowedReplications(currentResults);
    int executedReplications = 0;
    while (replications > 0 && executedReplications < replications
        && !abortCurrentConfig) {

      IInitializedComputationTask initComputationTask = null;
      IComputationTaskConfiguration compTaskConfig = null;
      currentTRTI = null;

      // Initialise computation task
      try {
        initComputationTask = init(taskConfig);
        currentResult = initComputationTask.getRunInfo();

        // Check for premature abort
        if (initComputationTask.getComputationTask() != null) {
          compTaskConfig = initComputationTask.getComputationTask().getConfig();
        } else {
          abortCurrentConfig = true;
        }

        // Notify submitting experiment
        IComputationTask run = initComputationTask.getComputationTask();
        currentTRTI =
            new ComputationTaskRuntimeInformation(compTaskConfig, taskConfig,
                run, run != null ? run.getUniqueIdentifier() : null,
                currentResult);
      } catch (Exception t) {
        SimSystem.report(Level.SEVERE,
            "Problem in initializing execution task", t);
        handleAbort(taskConfig, currentResult, compTaskConfig, t);
      } finally {
        getExperimentController(taskConfig).computationTaskInitialized(this,
            currentTRTI);
      }

      waitForNotification();

      if (runComputationTask) {
        executedReplications++;
        try {
          currentResult = run(initComputationTask, compTaskConfig);
          currentResults.add(currentResult);
          // Notify that the execution of a single run is done
          if (executedReplications == replications) {
            replications += taskConfig.allowedReplications(currentResults);
          }
          currentResult.setJobDone(executedReplications == replications);
        } catch (Exception t) {
          SimSystem.report(t);
          abortCurrentConfig = true;
          currentResult = new RunInformation(true);
        } finally {
          getExperimentController(taskConfig).computationTaskExecuted(this,
              currentTRTI, currentResult);

          IDataStorage<?> storage = getDataStorage(taskConfig);
          try {
            if (storage != null) {
              storage
                  .computationTaskDone(compTaskConfig.getComputationTaskID());
            }
          } catch (Exception e) {
            SimSystem.report(e);
          }

        }
      } else {
        if (executedReplications == replications) {
          replications += taskConfig.allowedReplications(currentResults);
        }
      }
    }

    removeExperimentController(taskConfig);

  }

  /**
   * Wait for notification, i.e. the run is either executed or cancelled.
   */
  private void waitForNotification() {
    try {
      synchronized (this) {
        // Compare old and current SRTI - if not equal, the runner does not
        // need to wait, because the controller already notified him to
        // proceed
        if (notifiedTRTI != currentTRTI) {
          wait();
        }
      }
    } catch (InterruptedException ex) {
      SimSystem.report(ex);
    }
  }

  /**
   * Handle early abort, i.e., in a stage where not even the model is loaded
   * properly.
   * 
   * @param taskConfig
   *          the task configuration
   * @param cause
   *          the cause for the failure
   */
  private void handleEarlyAbort(TaskConfiguration taskConfig, Throwable cause) {
    if (cause != null) {
      SimSystem.report(cause);
    }
    RunInformation r = new RunInformation(true);
    currentTRTI =
        new ComputationTaskRuntimeInformation(null, taskConfig, null, null, r);
    getExperimentController(taskConfig).computationTaskInitialized(this,
        currentTRTI);
    waitForNotification();
    getExperimentController(taskConfig).computationTaskExecuted(this,
        currentTRTI, r);
    SimSystem.report(Level.SEVERE, "Finishing experiment");
    removeExperimentController(taskConfig);
  }

  /**
   * Sets all variables so that the runner aborts this configuration gracefully.
   * 
   * @param taskConfig
   *          the task configuration
   * @param currentResult
   *          the current results
   * @param compTaskConfig
   *          the computation task configuration
   * @param cause
   *          the cause of the failure
   */
  private void handleAbort(TaskConfiguration taskConfig,
      RunInformation currentResult,
      IComputationTaskConfiguration compTaskConfig, Throwable cause) {
    SimSystem.report(cause);
    abortCurrentConfig = true;
    currentTRTI =
        new ComputationTaskRuntimeInformation(compTaskConfig, taskConfig, null,
            null, currentResult);
  }

  /**
   * Initialises a single computation task.
   * 
   * @param taskConfig
   *          the task configuration
   * 
   * @return the initialised computation task
   */
  private IInitializedComputationTask init(TaskConfiguration taskConfig) {

    ComputationTaskIDObject id = this.getComputationTaskID(taskConfig);
    IComputationTaskConfiguration compTaskConfig =
        taskConfig.newComputationTaskConfiguration(id);

    if (isStopping()) {
      return new InitializedComputationTask(null, new RunInformation(
          compTaskConfig));
    }

    return ComputationTaskHandler.initRunConfig(compTaskConfig, this,
        getModelReader());
  }

  /**
   * Processes a notification from the experiment execution controller.
   * 
   * @param taskRuntimeInformation
   *          the computation task runtime information identifying the execution
   * @param run
   *          flag that determines whether the computation task shall be
   *          executed or aborted
   */
  @Override
  protected void processNotification(
      ComputationTaskRuntimeInformation taskRuntimeInformation, boolean run) {
    if (taskRuntimeInformation == null
        || !taskRuntimeInformation.equals(currentTRTI)) {
      throw new RuntimeException(
          "Sequential Runner may only execute computation tasks with runtime info:"
              + currentTRTI);
    }
    runComputationTask = run;
    synchronized (this) {
      notifiedTRTI = taskRuntimeInformation;
      notifyAll();
    }
  }

  /**
   * Executes initialised computation tasks.
   * 
   * @param task
   *          the initialised computation task
   * @param compTaskConfig
   *          the computation task configuration
   * 
   * @return runtime information
   */
  private RunInformation run(IInitializedComputationTask task,
      IComputationTaskConfiguration compTaskConfig) {
    return ComputationTaskHandler.runComputationTask(task, compTaskConfig,
        getInteractiveConsole(), null, this);
  }

  @Override
  public void recoverTask(long simUID,
      ComputationTaskRuntimeInformation runtimeInfo) {
    throw new OperationNotSupportedException();
  }

  @Override
  public void restartTask(long taskUID) {
    throw new OperationNotSupportedException();
  }

}

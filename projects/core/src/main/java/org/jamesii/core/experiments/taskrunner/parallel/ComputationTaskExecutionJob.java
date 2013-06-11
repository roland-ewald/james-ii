/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.taskrunner.parallel;

import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.data.model.IModelReader;
import org.jamesii.core.experiments.ComputationSetupException;
import org.jamesii.core.experiments.ComputationTaskRuntimeInformation;
import org.jamesii.core.experiments.IComputationTaskConfiguration;
import org.jamesii.core.experiments.IExperimentExecutionController;
import org.jamesii.core.experiments.RunInformation;
import org.jamesii.core.experiments.TaskConfiguration;
import org.jamesii.core.experiments.taskrunner.ComputationTaskHandler;
import org.jamesii.core.experiments.taskrunner.InitializedComputationTask;
import org.jamesii.core.experiments.tasks.ComputationTaskIDObject;
import org.jamesii.core.experiments.tasks.IComputationTask;
import org.jamesii.core.experiments.tasks.IInitializedComputationTask;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * 
 * The SimulationExecutionJob is created by the
 * {@link ParallelComputationTaskRunner} to create and run a computation task
 * independently.
 * 
 * @author Stefan Leye
 * 
 */
public class ComputationTaskExecutionJob implements Runnable {

  /**
   * The parallel computation task runner, which created (and uses) this thread.
   */
  private ParallelComputationTaskRunner runner;

  /** The task configuration to be executed by this thread. */
  private TaskConfiguration config;

  /** The configuration of the specific computation task to be executed. */
  private IComputationTaskConfiguration runConfig;

  /** Flag determining whether the actual simulation shall be cancelled. */
  private boolean cancel = false;

  /**
   * Flag that determines whether the initialised computationt task
   * corresponding to currentSRTI should be run (true) or cancelled (false).
   */
  private boolean runComputationTask = true;

  /**
   * Model reader to be used.
   */
  private IModelReader modelReader = null;

  /**
   * Model reader parameters for last model that was computed (to prevent
   * frequent re-selection and re-instantiation of model reader).
   */
  private ParameterBlock absModReaderParams = null;

  /**
   * Flag determining whether to wait for notification.
   */
  private boolean notified = false;

  /**
   * Runtime info on the current task to be executed.
   */
  private ComputationTaskRuntimeInformation currentTRI = null;

  /**
   * Execution Controller responsible for the tasks to be executed.
   */
  private IExperimentExecutionController execController;

  /**
   * Instantiates a new computation task execution thread.
   * 
   * @param runner
   *          the runner
   * @param config
   *          the config
   * @param execController
   *          the exec controller
   * @param currentResults
   *          the current results
   */
  public ComputationTaskExecutionJob(ParallelComputationTaskRunner runner,
      TaskConfiguration config, IExperimentExecutionController execController) {
    this.runner = runner;
    this.config = config;
    this.execController = execController;
  }

  /**
   * Cancels the computation task (if created yet), i.e., if it runs in a
   * network tell the master server to stop, if it runs locally simply stop it.
   */
  public void cancel() {
    cancel = true;
    if (currentTRI != null) {
      if (runConfig.useMasterServer()) {
        try {
          runConfig.getMasterServer().stop(currentTRI.getComputationTaskID());
        } catch (Exception e) {
          SimSystem.report(Level.SEVERE, "Couldn't stop computation!");
        }
      } else {
        IComputationTask sim = currentTRI.getComputationTask();
        if (sim != null) {
          sim.stopProcessor();
        }
      }
    }
  }

  /**
   * Initialises a computation task either by a master server, if available or
   * locally. If model or computation task instantiation or run execution fail
   * the experiment will only be cancelled if the cancelOnError attribute is
   * true! This is done by throwing an ExperimentException. Any way some
   * information will be printed about the problem, and if an onErrorHook is set
   * this will be called.
   * 
   * @return general (measurements of required time) information about the
   *         simulation run
   */
  private IInitializedComputationTask init() {

    if (runConfig == null) {
      ComputationTaskIDObject computationTaskID =
          runner.getComputationTaskID(config);
      runConfig = createRunConfiguration(computationTaskID);
    }

    if (cancel) {
      return new InitializedComputationTask(null, new RunInformation(runConfig));
    }

    return ComputationTaskHandler.initRunConfig(runConfig, runner, modelReader);
  }

  /**
   * Gets the run configuration.
   * 
   * @param computationTaskID
   *          the computation task id
   * @return the run configuration
   */
  protected IComputationTaskConfiguration createRunConfiguration(
      ComputationTaskIDObject computationTaskID) {
    return config.newComputationTaskConfiguration(computationTaskID);
  }

  /**
   * Main method. Organizes the computation task execution (calls computation
   * task initialization and run methods). After the computation task is
   * finished it notifies the runner about it.
   */
  @Override
  public void run() {
    RunInformation currentResult = null;
    try {
      if (!cancel) {
        IInitializedComputationTask initCompTask = null;

        try {
          // Initialize simulation run
          initCompTask = init();

          if (initCompTask == null) {
            throw new ComputationSetupException(
                "Computation task init process failed");
          }
          currentResult = initCompTask.getRunInfo();

          // Notify submitting experiment
          currentTRI =
              new ComputationTaskRuntimeInformation(runConfig, config,
                  initCompTask.getComputationTask(),
                  currentResult.getComputationTaskID(), currentResult);
        } catch (Throwable t) {
          SimSystem.report(t);
        } finally {
          if (currentResult == null) {
            currentResult = new RunInformation(false);
          }
          if (currentTRI == null) {
            currentTRI =
                new ComputationTaskRuntimeInformation(runConfig, config, null,
                    null, currentResult);
          }
          runner.notifyExecutionController(this, execController, currentTRI);
        }
        // Wait for notification: this run will either be executed or canceled
        try {
          synchronized (this) {
            if (!notified) {
              wait();
            }
          }
        } catch (InterruptedException ex) {
          SimSystem.report(ex);
        }
        if (runComputationTask) {
          currentResult = run(initCompTask, runConfig);
        }

      }
    } catch (Exception t) {
      SimSystem.report(t);
    } finally {
      // notify the parallel computation task runner, that the simulation is
      // finished
      if (currentResult == null) {
        currentResult = new RunInformation(false);
      }
      runner.runExecuted(this, currentTRI, config, currentResult);
    }

  }

  /**
   * Executes initialized computation task.
   * 
   * @param initComputationTask
   *          the initialized task
   * @param computationTaskConfiguration
   *          the computation task configuration
   * @return RunInformation infos about the computation (time, etc.)
   */
  private RunInformation run(IInitializedComputationTask initComputationTask,
      IComputationTaskConfiguration computationTaskConfiguration) {
    return ComputationTaskHandler.runComputationTask(initComputationTask,
        computationTaskConfiguration, runner.getInteractiveConsole(), null,
        runner);
  }

  /**
   * Sets the model reader.
   * 
   * @param modelReader
   *          the new model reader
   */
  public void setModelReader(IModelReader modelReader) {
    this.modelReader = modelReader;
  }

  /**
   * Sets the parameters for the model reader.
   * 
   * @param absModReaderParams
   *          the parameters for the model reader.
   */
  public void setAbsModReaderParams(ParameterBlock absModReaderParams) {
    this.absModReaderParams = absModReaderParams;
  }

  /**
   * This method is called after the execution controller notified the parallel
   * simulation runner about whether the simulation is started or canceled.
   * 
   * @param runComputationTask
   *          boolean that indicates whether the computation task has to be
   *          started or canceled.
   */
  public void setRunComputationTask(boolean runComputationTask) {
    this.runComputationTask = runComputationTask;
    synchronized (this) {
      this.notified = true;
      notifyAll();
    }
  }

  /**
   * @return the config
   */
  protected final TaskConfiguration getConfig() {
    return config;
  }

  /**
   * @param config
   *          the config to set
   */
  protected final void setConfig(TaskConfiguration config) {
    this.config = config;
  }
}

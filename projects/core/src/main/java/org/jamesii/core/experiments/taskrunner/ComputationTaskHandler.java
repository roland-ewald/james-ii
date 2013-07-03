/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.taskrunner;

import java.rmi.RemoteException;
import java.util.List;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.base.Entity;
import org.jamesii.core.data.model.IModelReader;
import org.jamesii.core.distributed.simulationserver.ISimulationServer;
import org.jamesii.core.experiments.ExecutionMeasurements;
import org.jamesii.core.experiments.ExperimentException;
import org.jamesii.core.experiments.IComputationTaskConfiguration;
import org.jamesii.core.experiments.RunInformation;
import org.jamesii.core.experiments.tasks.IComputationTask;
import org.jamesii.core.experiments.tasks.IInitializedComputationTask;
import org.jamesii.core.simulation.launch.InteractiveConsole;
import org.jamesii.core.simulationrun.SimulationRun;
import org.jamesii.core.util.misc.Strings;

/**
 * The ComputationTaskHandler handler provides the central methods for
 * initialising and running computation tasks in a static manner, these are used
 * by various components implementing different execution schemes.
 * 
 * The actual creation of the instance of the computation task is delegated to
 * another class - this means that this procedure can be replaced easily and
 * that thus any type of pairs of problem instance / computation instance can be
 * used. This opens the experimental layer of the framework to non "simulation"
 * based computations and to computations done in external software. Only need:
 * two phase process (instantiation, execution).
 * 
 * 
 * @author Stefan Leye
 * @author Jan Himmelspach, Roland Ewald
 */
public final class ComputationTaskHandler {

  /**
   * Hidden constructor.
   */
  private ComputationTaskHandler() {
  }

  /**
   * Creates an IInitializedComputationTask object, using a
   * SimulationConfiguration and a ModelReader. Information about the
   * initialisation process are stored in RunInformation and StringBuffer.
   * 
   * @param computationTaskConfiguration
   *          the computation task configuration
   * @param modelReader
   *          the model reader
   * @param info
   *          the result
   * @param out
   *          the out
   * @param resources
   *          the available resources
   * 
   * @return initialised computation task
   * 
   * @throws ExperimentException
   *           the experiment exception
   */
  public static IInitializedComputationTask initComputationTask(
      IComputationTaskConfiguration computationTaskConfiguration,
      IModelReader modelReader, RunInformation info, StringBuffer out,
      List<ISimulationServer> resources) {

    return computationTaskConfiguration.getSetup().initComputationTask(
        computationTaskConfiguration, modelReader, info, out, resources);
  }

  /**
   * Executes an initialised computation task and returns information about the
   * run.
   * 
   * @param task
   *          the initialised computation task
   * @param compTaskConfig
   *          the computation task configuration
   * @param interactiveConsole
   *          the interactive console
   * @param out
   *          the out
   * @param taskRunner
   *          the task runner which handles the task
   * 
   * @return information about the task computed
   */
  public static RunInformation runComputationTask(
      IInitializedComputationTask task,
      IComputationTaskConfiguration compTaskConfig,
      InteractiveConsole interactiveConsole, StringBuffer out,
      ITaskRunner taskRunner) {

    if (task == null) {
      SimSystem
          .report(
              Level.SEVERE,
              "A computation task which is not existent (null) shall be computed. "
                  + "This is not possible and means that the task creation failed. "
                  + "Please check the error log for further details. "
                  + " The task belongs to the following configuration: "
                  + "Exp: " + compTaskConfig.getExperimentID() + "\n" + "Con: "
                  + compTaskConfig.getConfigurationID() + "\n" + "Com: "
                  + compTaskConfig.getComputationTaskID());
      return new RunInformation(false);
    }

    IComputationTask currentComputationTask = task.getComputationTask();
    ExecutionMeasurements execMeasures =
        new ExecutionMeasurements(task.getRunInfo(), out);

    // Execute the computation task
    execMeasures.startComputationTask();
    try {
      // TODO: check whether this works...
      if (compTaskConfig.isInteractive() && (interactiveConsole != null)
          && currentComputationTask instanceof SimulationRun) {
        interactiveConsole.setSimulation(currentComputationTask);
      }
      // If there is a master server defined by the parameters, start the
      // simulation there
      if (compTaskConfig.useMasterServer()) {

        RemoteComputationTaskRunnerRef remoteSimRunnerRef = null;

        try {
          remoteSimRunnerRef = new RemoteComputationTaskRunnerRef(taskRunner);
        } catch (RemoteException t) {
          SimSystem.report(t);
          task.getRunInfo().storeFailure("Remote computation failed.", t);
        }

        compTaskConfig.getMasterServer().execute(
            task.getRunInfo().getComputationTaskID(), remoteSimRunnerRef);
        // Otherwise start the simulation locally
      } else {
        if (currentComputationTask == null) {
          return task.getRunInfo();
        }
        currentComputationTask.start();
      }
    } catch (RemoteException t) {
      String msg =
          "Computation of task " + compTaskConfig.toString() + " FAILED !!! ";
      SimSystem.report(Level.SEVERE, msg, t);
      attemptDetailedErrorReporting(compTaskConfig);
      task.getRunInfo().storeFailure(msg, t);
      return task.getRunInfo();
    } finally {
      execMeasures.stopComputationTask();
    }

    compTaskConfig.getSetup().cleanUp(compTaskConfig, task.getRunInfo());

    return task.getRunInfo();
  }

  /**
   * Attempt to report some more details one the error.
   * 
   * @param compTaskConfig
   *          the computation task configuration that failed
   */
  private static void attemptDetailedErrorReporting(
      IComputationTaskConfiguration compTaskConfig) {

    try {
      SimSystem
          .report(
              Level.SEVERE,
              "Task parameters: "
                  + Strings.dispMap(compTaskConfig.getParameters()));
      SimSystem.report(Level.SEVERE,
          "Execution parameters: " + compTaskConfig.getExecParams());
    } catch (Exception e) {
      SimSystem.report(Level.WARNING, "Detailed information on "
          + compTaskConfig + " can not be displayed. ", e);
    }
  }

  /**
   * Initialises the run configuration.
   * 
   * @param runConfig
   *          the run configuration
   * @param taskRunner
   *          the task runner
   * @param modelReader
   *          the model reader
   * @return the initialized computation task
   */
  public static IInitializedComputationTask initRunConfig(
      IComputationTaskConfiguration runConfig, ITaskRunner taskRunner,
      IModelReader modelReader) {

    IInitializedComputationTask result = null;

    if (runConfig.useMasterServer()) {
      result = initOnMasterServer(runConfig, taskRunner);
    } else {
      result = initLocally(runConfig, taskRunner, modelReader);
    }

    return result;
  }

  /**
   * Initialises the computation task locally.
   * 
   * @param runConfig
   *          the run configuration
   * @param taskRunner
   *          the task runner
   * @param modelReader
   *          the model reader
   * @param result
   *          the result
   * @return the initialized computation task
   */
  private static IInitializedComputationTask initLocally(
      IComputationTaskConfiguration runConfig, ITaskRunner taskRunner,
      IModelReader modelReader) {
    RunInformation runInfo = new RunInformation(runConfig);
    IInitializedComputationTask result = null;
    try {
      result = initComputationTask(runConfig, modelReader, runInfo, null, null);
    } catch (Exception ex) {
      SimSystem.report(Level.SEVERE,
          "Error during initialization of computation task.", ex);
    }

    if ((result != null)
        && (AbstractTaskRunner.checkForFailure(taskRunner, result))) {
      return new InitializedComputationTask(null, result.getRunInfo());
    }
    return result;
  }

  /**
   * Initializes a run configuration the on master server.
   * 
   * @param runConfig
   *          the run configuration
   * @param taskRunner
   *          the task runner
   * @return the initialized computation task
   */
  private static IInitializedComputationTask initOnMasterServer(
      IComputationTaskConfiguration runConfig, ITaskRunner taskRunner) {
    IInitializedComputationTask result = null;
    try {
      SimSystem.report(Level.INFO, "Creating a simulation on a master server");
      result =
          runConfig.getMasterServer().executeSimulationConfiguration(runConfig,
              new RemoteComputationTaskRunnerRef(taskRunner));
    } catch (Exception e) {
      String msg = e.getMessage();
      SimSystem.report(Level.WARNING, "Exception on server:" + e.getMessage(),
          e);
      taskRunner.onErrorHook(msg);
      if (taskRunner.isCancelOnError()) {
        throw new ExperimentException(
            "An error occured. The experiment is cancelled! " + msg, e);
      }
      // cancel this particular run
      return new InitializedComputationTask(null, new RunInformation(runConfig));
    }
    return result;
  }
}

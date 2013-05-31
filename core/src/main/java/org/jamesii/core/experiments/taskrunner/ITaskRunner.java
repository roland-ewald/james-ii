/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.taskrunner;

import java.rmi.RemoteException;
import java.util.List;

import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.core.experiments.ComputationTaskRuntimeInformation;
import org.jamesii.core.experiments.IExperimentExecutionController;
import org.jamesii.core.experiments.TaskConfiguration;
import org.jamesii.core.experiments.tasks.ComputationTaskHook;

/**
 * Interface for all objects that are able to process {@link TaskConfiguration}
 * entities. These are received from (possibly different)
 * {@link IExperimentExecutionController} entities. These have to be notified
 * with corresponding {@link ComputationTaskRuntimeInformation} when a task has
 * been initialised and when its execution was finished. They notify a
 * {@link ITaskRunner} via
 * {@link ITaskRunner#runTask(ComputationTaskRuntimeInformation)},
 * {@link ITaskRunner#cancelTask(ComputationTaskRuntimeInformation)} etc. about
 * the actions to be done, regarding the task computation / run that corresponds
 * to the {@link ComputationTaskRuntimeInformation}.
 * 
 * @author Roland Ewald
 * @author Thomas Nösinger
 * 
 */
public interface ITaskRunner extends Runnable {

  /**
   * Cancels all jobs for a given experiment. Will be called when
   * {@link BaseExperiment#stop(boolean)} is invoked.
   * 
   * @param executionController
   *          the experiment execution controller associated with the experiment
   *          whose jobs have to be cancelled
   * @return true, if cancellation was successful
   */
  boolean cancelAllJobs(IExperimentExecutionController executionController);

  /**
   * Removes configuration from job queue.
   * 
   * @param taskConfiguration
   *          configuration to be cancelled
   */
  void cancelConfiguration(TaskConfiguration taskConfiguration);

  /**
   * Cancels a computation task.
   * 
   * @param taskRuntimeInformation
   *          corresponding task runtime info
   */
  void cancelTask(ComputationTaskRuntimeInformation taskRuntimeInformation);

  /**
   * Tests whether experiment should be cancelled when an error occurs.
   * 
   * @return true, if the experiment should be cancelled when an error occurs.
   */
  boolean isCancelOnError();

  /**
   * Tests whether runner is idle.
   * 
   * @return true, if there is no more work to be done right now
   */
  boolean isIdle();

  /**
   * Tests whether runner is stopping.
   * 
   * @return true, if the runner is stopping
   */
  boolean isStopping();

  /**
   * Tests whether runner is pausing.
   * 
   * @return true, if the runner is pausing
   */
  boolean isPausing();

  /**
   * This lets the task runner start a certain task configuration. The
   * configuration has to be submitted via scheduleConfigurations before. The
   * method may only be called after a notification on the succeeded
   * initialisation (see {@link BaseExperiment} ).
   * 
   * @param taskRuntimeInformation
   *          information necessary to manage the execution of a task
   */
  void runTask(ComputationTaskRuntimeInformation taskRuntimeInformation);

  /**
   * Schedules computation tasks for a certain {@link BaseExperiment}.
   * 
   * @param taskConfigurations
   *          the task configurations
   * @param executionController
   *          the execution controller
   */
  void scheduleConfigurations(
      IExperimentExecutionController executionController,
      List<TaskConfiguration> taskConfigurations);

  /**
   * Sets a flag, denoting, whether the experiment should be cancelled if an
   * error occurs.
   * 
   * @param cancelOnError
   *          flag denoting whether experiment should be cancelled if an error
   *          occurs
   */
  void setCancelOnError(boolean cancelOnError);

  /**
   * Stops the computation task runner.
   */
  void stop();

  /**
   * Executes error hook with given error message.
   * 
   * @param message
   *          contains the error
   */
  void onErrorHook(String message);

  /**
   * Gets the error hook.
   * 
   * @return the error hook
   */
  ComputationTaskHook<String> getErrorHook();

  /**
   * Sets the error hook.
   * 
   * @param errorHook
   *          the error hook
   */
  void setErrorHook(ComputationTaskHook<String> errorHook);

  /**
   * Restarts a task (for resilience if no checkpoint is available).
   * 
   * @param taskUID
   *          the task uid
   * 
   * @throws RemoteException
   *           the remote exception
   */
  void restartTask(long taskUID) throws RemoteException;

  /**
   * Restarts a task configuration (for resilience if a checkpoint is
   * available).
   * 
   * @param taskUID
   *          the task uid
   * @param runtimeInfo
   *          the runtime info
   * 
   * @throws RemoteException
   *           the remote exception
   */
  void recoverTask(long taskUID, ComputationTaskRuntimeInformation runtimeInfo)
      throws RemoteException;

}
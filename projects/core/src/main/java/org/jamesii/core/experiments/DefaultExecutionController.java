/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.experiments.taskrunner.ITaskRunner;
import org.jamesii.core.processor.IRunnable;

/**
 * Abstract class of some process to be done.
 * 
 * @author Roland Ewald
 */
abstract class ControlJob {

  /** Task runtime information. */
  private final ComputationTaskRuntimeInformation taskRTInfo;

  /**
   * Default constructor.
   * 
   * @param trti
   *          simulation runtime information
   */
  public ControlJob(ComputationTaskRuntimeInformation trti) {
    taskRTInfo = trti;
  }

  /**
   * Processes the job.
   */
  abstract void process();

  public ComputationTaskRuntimeInformation getTaskRTInfo() {
    return taskRTInfo;
  }

}

/**
 * Default implementation of experiment execution controller. See
 * {@link IExperimentExecutionController}.
 * 
 * @author Roland Ewald
 */
public class DefaultExecutionController implements
    IExperimentExecutionController {

  /** Reference to experiment. */
  private BaseExperiment experiment = null;

  /** List of listeners for this experiment execution. */
  private List<IExperimentExecutionListener> expExecListeners = Collections
      .synchronizedList(new ArrayList<IExperimentExecutionListener>());

  /** Job queue. */
  private BlockingQueue<ControlJob> jobs = new LinkedBlockingQueue<>();

  /** Flag that stops the thread if set to true. */
  private AtomicBoolean stopping = new AtomicBoolean(false);

  /**
   * The set of RT informations of tasks that are currently being executed. Can
   * be used to retrieve them for overall switching their state, e.g. stopping
   * them all.
   */
  private Set<ComputationTaskRuntimeInformation> currentTasksRTInfos = Collections
      .synchronizedSet(new HashSet<ComputationTaskRuntimeInformation>());

  @Override
  public synchronized void addExecutionListener(
      IExperimentExecutionListener executionListener) {
    if (!expExecListeners.contains(executionListener)) {
      expExecListeners.add(executionListener);
    }
  }

  @Override
  public synchronized BaseExperiment getExperiment() {
    return experiment;
  }

  @Override
  public synchronized boolean removeExecutionListener(
      IExperimentExecutionListener executionListener) {
    return expExecListeners.remove(executionListener);
  }

  @Override
  public void run() {

    for (IExperimentExecutionListener listener : expExecListeners) {
      try {
        listener.experimentExecutionStarted(getExperiment());
      } catch (Exception ex) { // NOSONAR:{needs_to_be_robust}
        reportListenerError(listener, ex);
      }
    }

    try {

      while (!stopping.get()) {
        // the stop method adds a poison pill to the queue so that this run
        // method will terminate in case of a call to stop and an empty job
        // queue
        jobs.take().process();

      }
    } catch (InterruptedException ex) {
      SimSystem.report(ex);
    } finally {
      synchronized (this) {
        experiment.stop(false);
        for (IExperimentExecutionListener listener : expExecListeners) {
          try {
            listener.experimentExecutionStopped(experiment);
          } catch (Exception ex) { // NOSONAR:{needs_to_be_robust}
            reportListenerError(listener, ex);
          }
        }
      }
    }
  }

  /**
   * Sets new experiment (job queue and stopping flag will be re-set).
   * 
   * @param exp
   *          the experiment to be controlled
   */
  @Override
  public synchronized void setExperiment(BaseExperiment exp) {
    jobs.clear();
    this.experiment = exp;
    stopping.set(false);
  }

  @Override
  public void computationTaskExecuted(ITaskRunner taskRunner,
      ComputationTaskRuntimeInformation ctrti, RunInformation runInfo) {

    ctrti.setState(ComputationRuntimeState.FINISHED);
    getCurrentTasksRTInfos().remove(ctrti);
    jobs.add(new ExperimentNotificationJob(getExperiment(), ctrti, runInfo));
    synchronized (this) {
      for (IExperimentExecutionListener listener : expExecListeners) {
        try {
          listener.simulationExecuted(taskRunner, ctrti, runInfo.isJobDone());
        } catch (Exception ex) { // NOSONAR:{needs_to_be_robust}
          reportListenerError(listener, ex);
        }
      }
    }
  }

  /**
   * Add job to queue and wake up.
   * 
   * @param taskRunner
   *          the task runner
   * @param taskRTInfo
   *          the computation task run time information
   */
  @Override
  public void computationTaskInitialized(ITaskRunner taskRunner,
      ComputationTaskRuntimeInformation taskRTInfo) {

    // Has been started in paused mode?
    if (taskRTInfo.getSimulationRunConfiguration() != null
        && taskRTInfo.getSimulationRunConfiguration().isStartPaused()) {
      taskRTInfo.setState(ComputationRuntimeState.PAUSED);
    } else {
      taskRTInfo.setState(ComputationRuntimeState.RUNNING);
    }
    getCurrentTasksRTInfos().add(taskRTInfo);
    jobs.add(new TaskRunnerControlJob(taskRunner, taskRTInfo));
    synchronized (this) {
      for (IExperimentExecutionListener listener : expExecListeners) {
        try {
          listener.simulationInitialized(taskRunner, taskRTInfo);
        } catch (Exception ex) { // NOSONAR:{needs_to_be_robust}
          reportListenerError(listener, ex);
        }
      }
    }
  }

  /**
   * Reports that there was a problem executing a specific listener.
   * 
   * @param listener
   *          the experiment listener causing trouble
   * @param exception
   *          the exception that was caught
   */
  private void reportListenerError(IExperimentExecutionListener listener,
      Exception exception) {
    SimSystem.report(Level.SEVERE, "Call to experiment execution listener '"
        + listener + "' failed. ", exception);
  }

  @Override
  public synchronized void stop(boolean stopComputations) {
    stopping.set(true);
    // let's terminate the run method, in case that it is waiting for a
    // new job
    jobs.add(new StopJob());
    if (stopComputations) {
      for (ComputationTaskRuntimeInformation currTaskRTI : getCurrentTasksRTInfos()) {
        if ((currTaskRTI.getState() == ComputationRuntimeState.RUNNING || currTaskRTI
            .getState() == ComputationRuntimeState.PAUSED)
            && ComputationTaskRuntimeInformation
                .computationTaskControlPossible(currTaskRTI)) {
          ((IRunnable) currTaskRTI.getComputationTask().getProcessorInfo().getLocal())
              .stop();
        }
      }
    }
  }

  /**
   * Gets the experiment execution listeners.
   * 
   * @return the experiment execution listeners
   */
  protected List<IExperimentExecutionListener> getExpExecListeners() {
    return Collections.unmodifiableList(expExecListeners);
  }

  /**
   * @return the set of the current tasks' RT informations
   */
  protected final Set<ComputationTaskRuntimeInformation> getCurrentTasksRTInfos() {
    return currentTasksRTInfos;
  }

  /**
   * @param currentTasksRTInfos
   *          the RT informations of the current tasks to be set
   */
  protected final void setCurrentTaskRTInfos(
      Set<ComputationTaskRuntimeInformation> currentTasksRTInfos) {
    this.currentTasksRTInfos = currentTasksRTInfos;
  }

}

/**
 * Notify experiment about completion of computation task.
 * 
 * @author Roland Ewald
 */
class ExperimentNotificationJob extends ControlJob {

  /**
   * Reference to experiment that shall be notified.
   */
  private final BaseExperiment experiment;

  /**
   * Results.
   */
  private final RunInformation results;

  /**
   * Instantiates a new experiment notification job.
   * 
   * @param exp
   *          the base experiment
   * @param srti
   *          the simulation run time information
   * @param res
   *          the run information
   * @param jobDone
   *          the "is the job done flag"
   */
  ExperimentNotificationJob(BaseExperiment exp,
      ComputationTaskRuntimeInformation srti, RunInformation res) {
    super(srti);
    experiment = exp;
    results = res;
  }

  @Override
  void process() {
    experiment.executionFinished(getTaskRTInfo()
        .getComputationTaskConfiguration(), results);
  }
}

/**
 * The stop job is a poison pill for the run method. It will be added to the
 * {@link DefaultExecutionController#jobs} queue by the
 * {@link DefaultExecutionController#stop(boolean)} method. The
 * {@link #process()} method of this job does nothing.
 * 
 * @author Jan Himmelspach
 * 
 */
class StopJob extends ControlJob {

  public StopJob() {
    super(null);
  }

  @Override
  void process() {
    // the poison pill job does nothing, if executed at all
  }

}

/**
 * Lets the {@link ITaskRunner} proceed.
 * 
 * @author Roland Ewald
 */
class TaskRunnerControlJob extends ControlJob {

  /** Reference to simulation runner that shall proceed. */
  private final ITaskRunner taskRunner;

  /**
   * Instantiates a new task runner control job.
   * 
   * @param taskRunner
   *          the task runner
   * @param trti
   *          the task run time information
   */
  TaskRunnerControlJob(ITaskRunner taskRunner,
      ComputationTaskRuntimeInformation trti) {
    super(trti);
    this.taskRunner = taskRunner;
  }

  @Override
  void process() {
    taskRunner.runTask(getTaskRTInfo());
  }

}
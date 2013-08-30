/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.processor.execontrol;

import java.io.Serializable;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.experiments.tasks.stoppolicy.EmptyStopCondition;
import org.jamesii.core.experiments.tasks.stoppolicy.IComputationTaskStopPolicy;
import org.jamesii.core.model.IModel;
import org.jamesii.core.processor.Processor;
import org.jamesii.core.processor.ProcessorStatus;
import org.jamesii.core.processor.util.IPaceProcessor;
import org.jamesii.core.util.Hook;
import org.jamesii.core.util.Semaphore;

/**
 * This class contains the functionality which must be implemented by a runnable
 * processor (that's a processor implementing the
 * {@link org.jamesii.core.processor.IRunnable} interface. Runnable processors
 * should use this class for this purpose! This does not only make your life
 * easier but allows to easily exchange the implementation later on - e.g., if
 * you require an alternative execution control. <br/>
 * An example on how to use this class can be found in the
 * {@link org.jamesii.core.processor.RunnableProcessor} class.
 * 
 * 
 * Basically there is a method in here which corresponds to a method in the
 * IRunnable interface as can be easily seen in the following table.
 * <table>
 * <tr>
 * <td>IRunnable interface</td>
 * <td>Corresponding method here</td>
 * </tr>
 * <tr>
 * <td>{@link org.jamesii.core.processor.IRunnable#isPausing()}</td>
 * <td>{@link #isPausing()}</td>
 * </tr>
 * <tr>
 * <td>{@link org.jamesii.core.processor.IRunnable#isStopping()}</td>
 * <td>{@link #isStopping()}</td>
 * </tr>
 * <tr>
 * <td>{@link org.jamesii.core.processor.IRunnable#isRunning()}</td>
 * <td>{@link #isRunning()}</td>
 * </tr>
 * <tr>
 * <td>{@link org.jamesii.core.processor.IRunnable#run()}</td>
 * <td>{@link #run()}</td>
 * </tr>
 * <tr>
 * <td>
 * {@link org.jamesii.core.processor.IRunnable#run(IComputationTaskStopPolicy)}</td>
 * <td>{@link #run(IComputationTaskStopPolicy)}</td>
 * </tr>
 * <tr>
 * <td>{@link org.jamesii.core.processor.IRunnable#next(int)}</td>
 * <td>{@link #next(int)}</td>
 * </tr>
 * <tr>
 * <td>{@link org.jamesii.core.processor.IRunnable#pause()}</td>
 * <td>{@link #pause()}</td>
 * </tr>
 * <tr>
 * <td>{@link org.jamesii.core.processor.IRunnable#stop()}</td>
 * <td>{@link #stop()}</td>
 * </tr>
 * </table>
 * 
 * In addition this class provides two additional slots where hooks can be
 * installed. One will be (if one is set) executed as soon as the simulation is
 * no longer running, the other one can be used to observe any status
 * {@link #getStatus()} change of the execution control.
 * 
 * @author Jan Himmelspach
 */
public class ExecutionControl implements Serializable {

  /** Serialisation ID. */
  static final long serialVersionUID = 2432626212153459560L;

  /** The owner. The runnable processor making use of this instance. */
  private Processor owner;

  /** The pause semaphore. Used to realized non busy waiting pausing. */
  private Semaphore semPause = new Semaphore(1);

  /** The status. */
  private ProcessorStatus status;

  /**
   * The pausing flag. If true the execution is currently paused, otherwise it
   * is most likely running or stopping.
   */
  private boolean pausing = false;

  /** Flag that determines whether the processor is running. */
  private boolean running = false;

  /**
   * The stopping flag. Will be true if the execution will stop as soon as
   * possible.
   */
  private boolean stopping = false;

  /** The end policy used to run this processor. */
  private IComputationTaskStopPolicy stoppingPolicy;

  /** The pausetime. */
  private long pauseTime = 0;

  /**
   * The stopped running hook. Will be executed as soon as the processor
   * stopped.
   */
  private Hook<Processor> stoppedRunning = null;

  /**
   * The status changed hook. Executed if the status of this execution control
   * has changed.
   */
  private Hook<ExecutionControl> statusChanged = null;

  /**
   * Create an instance of this object.
   * 
   * @param owner
   *          the owner
   */
  public ExecutionControl(Processor owner) {
    super();
    this.setOwner(owner);
  }

  /**
   * Sets the status.
   * 
   * @param newStatus
   *          the new status
   */
  protected void setStatus(ProcessorStatus newStatus) {
    status = newStatus;
    // execute the stoppedRunning hook, if there is one
    if (statusChanged != null) {
      statusChanged.execute(this);
    }
  }

  /**
   * Gets the status.
   * 
   * @return the status
   */
  public ProcessorStatus getStatus() {
    return status;
  }

  /**
   * Does nothing here, can be used in descendant classes for special jobs.
   */
  protected void cleanUp() {
    setStatus(ProcessorStatus.STOPPED);
    getOwner().cleanUp();
    IModel model = getOwner().getModel();
    if (getOwner().getModel() != null) {
      model.cleanUp();
    }
  }

  /**
   * Checks if is pausing. If this returns true {@link #isRunning()} will return
   * false. Thus to check whether a run has been cancelled or not you have to
   * combine {@link #isRunning()} and {@link #isPausing()}.
   * 
   * @return true, if is pausing
   * 
   * @see org.jamesii.core.processor.IRunnable#isPausing()
   */
  public boolean isPausing() {
    return pausing;
  }

  /**
   * This method is used to check whether this processor is running. If it is
   * running the {@link Processor#executeNextStep()} is executed on and on again
   * - as long as determined by the current setting - and as long as the
   * execution is not paused or stopped.
   * 
   * @return true if the processor is running
   * 
   * @see org.jamesii.core.processor.IRunnable#isRunning()
   */
  public boolean isRunning() {
    return running;
  }

  /**
   * Checks if is stopping. Will return true if the attribute {@link #stopping}
   * is true. A value of true means that the simulation is about to be stopped
   * as soon as possible (but the current simulation step will be finished
   * first).
   * 
   * @return true, if is stopping
   * 
   * @see org.jamesii.core.processor.IRunnable#isStopping()
   */
  public boolean isStopping() {
    return stopping;
  }

  /**
   * This method must be used to indicate whether a simulation run has run to
   * its end or not. <br/>
   * Relies on the {@link Processor#getTime()} method, thus this method will
   * only work if this method returns the current simulation time.
   * 
   * @param stopPolicy
   *          the stop policy the simulation algorithm shall stop at
   * 
   * @return true if the simulation has ended
   */
  public boolean notFinished(IComputationTaskStopPolicy stopPolicy) {
    return !stopPolicy.hasReachedEnd();
  }

  /**
   * Pauses the simulation execution. Can be undone by a second call to this
   * method. The currently processed simulation step will be completed before
   * the simulation will stop.
   * 
   * @see org.jamesii.core.processor.IRunnable#pause()
   */
  public void pause() {
    if (!pausing) {
      pausing = true;
      running = false;
      setStatus(ProcessorStatus.PAUSING);
      getSemPause().p();
    } else {
      running = true;
      pausing = false;
      setStatus(ProcessorStatus.RUNNING);
      getSemPause().v();
    }
  }

  /**
   * Run infinitely (or until the model terminates itself).
   * 
   * Delegates the execution to the {@link #run(IComputationTaskStopPolicy)}
   * method of this class. The end time is automatically set to infinity.
   * 
   * @see org.jamesii.core.processor.IRunnable#run()
   */
  public void run() {
    prepareRun();
    run(new EmptyStopCondition(getOwner().getComputationTask()));
  }

  /**
   * Run until the end time is reached. <br/>
   * In addition this method can be paused, and the execution might be stopped
   * by setting the {@link #stopping} attribute to true, e.g., by using the
   * {@link #stop()} method. Both, pausing and stopping might be delayed because
   * a previous call to {@link Processor#executeNextStep()} has to be finished
   * before this method can react to either of both.
   * 
   * @param end
   *          the stop policy the simulation algorithm shall stop at
   * 
   * @see org.jamesii.core.processor.IRunnable#run(IComputationTaskStopPolicy)
   */
  public void run(IComputationTaskStopPolicy end) {
    prepareRun();
    run(end, 0);
  }

  /**
   * Run until the end time is reached. <br/>
   * In addition this method can be paused, and the execution might be stopped
   * by setting the {@link #stopping} attribute to true, e.g., by using the
   * {@link #stop()} method. Both, pausing and stopping might be delayed because
   * a previous call to {@link Processor#executeNextStep()} has to be finished
   * before this method can react to either of both.
   * 
   * @param end
   *          the stop policy the simulation algorithm shall stop at
   * @param pauseT
   *          the pause time, time to sleep between two steps in ms
   * 
   * @see org.jamesii.core.processor.IRunnable#run(IComputationTaskStopPolicy)
   */
  public void run(IComputationTaskStopPolicy end, long pauseT) {
    run(end, pauseT, false);
  }

  /**
   * Run until the end time is reached. <br/>
   * In addition this method can be paused, and the execution might be stopped
   * by setting the {@link #stopping} attribute to true, e.g., by using the
   * {@link #stop()} method. Both, pausing and stopping might be delayed because
   * a previous call to {@link Processor#executeNextStep()} has to be finished
   * before this method can react to either of both.
   * 
   * @param end
   *          the stop policy the computation algorithm shall stop at
   * @param pauseT
   *          the pause time, time to sleep between two steps in ms
   * @param startPaused
   *          if true, computation of the computation task will start paused
   * 
   * @see org.jamesii.core.processor.IRunnable#run(IComputationTaskStopPolicy)
   */
  public void run(IComputationTaskStopPolicy end, long pauseT,
      boolean startPaused) {
    prepareRun();

    this.setStoppingPolicy(end);

    if ((getOwner() instanceof IPaceProcessor) && (pauseT != 0)) {
      SimSystem.report(Level.WARNING, "This processor is running in paced mode, using an artifical slow down is prohibited because it will harm the simualtion semantics! Will continue without the extra delay.");
      this.pauseTime = 0;
    } else {
      this.pauseTime = pauseT;
    }
    // set the flag to "running"
    running = true;

    // start paused?
    if (startPaused) {
      pause();
    }

    executeRunningLoop(end);

    noLongerRunning();
    if (stopping || !notFinished(end)) {
      cleanUp();
    }

  }

  private void executeRunningLoop(IComputationTaskStopPolicy end) {
    // execute the all steps until we are stopped of the endtime is reached
    while ((!stopping) && (notFinished(end))) {
      if (pausing) {
        // wait to get the semaphore (unpause)
        getSemPause().p();
        // release it so that the pause button can be clicked again ...
        getSemPause().v();
      }
      getOwner().executeNextStep();
      if (this.pauseTime != 0) {
        try {
          // sleep a while
          Thread.sleep(this.pauseTime);
        } catch (InterruptedException e) {
          SimSystem.report(e);
        }
      }
    }
  }

  /**
   * Execute next n steps (if end time not reached). Thus, if the end time is
   * reached by executing less than n steps, the remaining steps will not be
   * executed. <br/>
   * This method cannot be used without a previous call to one of the run
   * methods. The endtime used therein is set by one of these. The execution
   * might be stopped by setting the {@link #stopping} attribute to true, e.g.,
   * by using the {@link #stop()} method. Stopping might be delayed because a
   * previous call to {@link Processor#executeNextStep()} has to be finished
   * before this method can react to either of both.
   * 
   * @param num
   *          the number of steps to be executed
   * 
   * @see org.jamesii.core.processor.IRunnable#next(int)
   */
  public void next(int num) {

    if (!pausing) {
      throw new InvalidExecutionStateException(
          "Not paused! Current state does not allow to proceed stepwise!");
    }

    // set the flag "running"
    // running = true;

    int count = 0;

    setStatus(ProcessorStatus.STEPPING);
    running = true;

    // order of the following expression is important to trigger an analysis
    // even if the run is finished
    while ((notFinished(getStoppingPolicy())) && (!stopping) && (count < num)) {

      // if (pausing) {
      //
      // // wait to get the semaphore (unpause)
      // pause.p();
      // // release it so that the pause button can be clicked again ...
      // pause.v();
      // }

      getOwner().executeNextStep();
      if (pauseTime != 0) {
        try {
          // sleep a while
          Thread.sleep(pauseTime);
        } catch (InterruptedException e) { /* No op */
        }
      }
      count++;
    }

    // return to PAUSING if we are not done with the simulation run
    if (!stopping && (!notFinished(getStoppingPolicy()))) {
      setStatus(ProcessorStatus.PAUSING);
    }

    running = false;

  }

  /**
   * Stops the execution of the simulation. Cannot be undone!! The currently
   * processed computation step will be finished first, thus there might be a
   * delay until the run terminates!
   * 
   * @see org.jamesii.core.processor.IRunnable#stop()
   */
  public void stop() {
    stopping = true;
    setStatus(ProcessorStatus.STOPPING);
    if (isPausing()) {
      pause(); // unpause so that we can stop
    }
  }

  /**
   * No longer running. Will set the {@link #running} attribute to false and
   * execute the {@link #stoppedRunning} hook, if there is one.
   */
  private void noLongerRunning() {
    // set the flag to "not running"
    running = false;
    // execute the stoppedRunning hook, if there is one
    if (stoppedRunning != null) {

      stoppedRunning.execute(getOwner());
    }
  }

  /**
   * Sets the stopped running hook. This hook will be executed from within the
   * {@link #noLongerRunning()} method as soon as the execution is stopped. If
   * an exception occurs during execution the hook might not be executed.
   * 
   * @param hook
   *          the new stopped running hook
   */
  public void setStoppedRunningHook(Hook<Processor> hook) {
    stoppedRunning = hook;
  }

  /**
   * Sets the status changed hook. This hook will be executed whenever the
   * {@link #status} has been changed.
   * 
   * @param hook
   *          the new status changed hook
   */
  public void setStatusChangedHook(Hook<ExecutionControl> hook) {
    statusChanged = hook;
  }

  /**
   * Prepare run.
   * 
   */
  private void prepareRun() {
    if (pausing || running) {
      throw new InvalidExecutionStateException(
          "Already running!!! You cannot execute one processor more than once!");
    }
    if (stopping) {
      throw new InvalidExecutionStateException(
          "Run has been cancelled, cannot restart!");
    }
    setStatus(ProcessorStatus.RUNNING);
  }

  /**
   * Sets the delay to be used in between of two subsequent steps. Will update
   * the internal delay time variable, the changed will be used after the next
   * computation step has been computed. See
   * {@link org.jamesii.core.processor.IRunnable#setDelay(long)}.
   * 
   * @param paTime
   *          the new pause time (delay)
   */
  public void setDelay(long paTime) {
    if ((getOwner() instanceof IPaceProcessor) && (paTime != 0)) {
      SimSystem
          .report(
              Level.WARNING,
              "This processor is running in paced mode, using an artifical slow down is prohibited because it will harm the simualtion semantics! Will continue without the extra delay.");
      this.pauseTime = 0;
    } else {
      this.pauseTime = paTime;
    }
  }

  /**
   * @param running
   *          running flag value to set
   */
  protected void setRunning(boolean running) {
    this.running = running;
  }

  /**
   * @return the stop policy
   */
  public IComputationTaskStopPolicy getStoppingPolicy() {
    return stoppingPolicy;
  }

  protected void setStoppingPolicy(IComputationTaskStopPolicy stoppingPolicy) {
    this.stoppingPolicy = stoppingPolicy;
  }

  protected Semaphore getSemPause() {
    return semPause;
  }

  protected void setSemPause(Semaphore semPause) {
    this.semPause = semPause;
  }

  protected final Processor getOwner() {
    return owner;
  }

  protected final void setOwner(Processor owner) {
    this.owner = owner;
  }

}

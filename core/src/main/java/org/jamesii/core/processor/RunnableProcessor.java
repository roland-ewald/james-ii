/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.processor;

import org.jamesii.core.experiments.tasks.stoppolicy.IComputationTaskStopPolicy;
import org.jamesii.core.model.IModel;
import org.jamesii.core.processor.execontrol.ExecutionControl;

/**
 * Simple {@link Processor} sub-class that also implements {@link IRunnable} by
 * delegating its calls to the {@link ExecutionControl}.<br/>
 * This abstract class can be used as ancestor class for all algorithms which
 * shall be directly executable by the framework. If you cannot use the class
 * directly (because you have already a different class you are inheriting from)
 * you can still copy the code in here into your class.
 * 
 * @author Roland Ewald
 */
public abstract class RunnableProcessor<TimeBase extends Comparable<TimeBase>>
    extends Processor<TimeBase> implements IRunnable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 8616799383391470842L;

  /** The exec control. */
  private ExecutionControl executionControl;

  /**
   * Instantiates a new runnable processor.
   * 
   * @param model
   *          the model
   */
  public RunnableProcessor(IModel model) {
    super(model);
    setExecutionControl(new ExecutionControl(this));
  }

  @Override
  public boolean isPausing() {
    return getExecutionControl().isPausing();
  }

  @Override
  public boolean isRunning() {
    return getExecutionControl().isRunning();
  }

  @Override
  public boolean isStopping() {
    return getExecutionControl().isStopping();
  }

  @Override
  public void pause() {
    getExecutionControl().pause();
  }

  @Override
  public void run() {
    getExecutionControl().run();
  }

  @Override
  public void run(IComputationTaskStopPolicy end) {
    getExecutionControl().run(end);
  }

  @Override
  public void run(IComputationTaskStopPolicy end, long pause) {
    getExecutionControl().run(end, pause);
  }

  @Override
  public void run(IComputationTaskStopPolicy end, long pause, boolean paused) {
    getExecutionControl().run(end, pause, paused);
  }

  @Override
  public void next(int num) {
    getExecutionControl().next(num);
  }

  @Override
  public void stop() {
    getExecutionControl().stop();
  }

  @Override
  public void setDelay(long pause) {
    getExecutionControl().setDelay(pause);
  }

  @Override
  public ProcessorStatus getStatus() {
    return getExecutionControl().getStatus();
  }

  /**
   * @return the executionControl
   */
  protected final ExecutionControl getExecutionControl() {
    return executionControl;
  }

  /**
   * @param executionControl
   *          the executionControl to set
   */
  protected final void setExecutionControl(ExecutionControl executionControl) {
    this.executionControl = executionControl;
  }

}

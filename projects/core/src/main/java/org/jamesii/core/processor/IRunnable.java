/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.processor;

import org.jamesii.core.experiments.tasks.stoppolicy.IComputationTaskStopPolicy;

/**
 * The IRunnable interface has to be implemented by all computation algorithms
 * (see {@link org.jamesii.core.processor.IProcessor}) interface which can be
 * used (standalone) to compute a trajetory of a model. The system is only able
 * to execute a simulation if there is at least one algorithm available which
 * can execute the model at hand, and which implements this interface (as this
 * interface is used by the framework to control the computation). This
 * interface provides basic methods to control simulation execution (like
 * {@link #run()}, {@link #pause()}, and {@link #stop()}). The isX methods (
 * {@link #isPausing()}, {@link #isRunning()}, {@link #isStopping()}) allow to
 * retrieve the state of the current run.<br/>
 * By using delegation the implementation of these methods can be easily
 * forwarded to another object, e.g., the
 * {@link org.jamesii.core.processor.execontrol.ExecutionControl} class. <br/>
 * The execution has to be started by one of the run methods, each execution can
 * be stopped by using the {@link #stop()} method, can be paused by the
 * {@link #pause()} method (and unpaused by using the same one), if paused the
 * {@link #next(int)} method can be used to continue stepwise. <br/>
 * If you use the {@link #run(IComputationTaskStopPolicy, long, boolean)} method
 * you can start in paused mode directly (e.g., to start stepwise from the
 * beginning on). You can only use one of the alternatives run methods for
 * starting a run. The {@link #setDelay(long)} method can be used to set an
 * extra pause between two subsequent steps.<br/>
 * If you use the {@link org.jamesii.core.processor.RunnableProcessor} as
 * ancestor for your own "processor" you get a default implementation of this
 * interface.
 * 
 * @author Jan Himmelspach
 */
public interface IRunnable {

  /**
   * This method is used to check whether this processor is pausing. If this
   * method returns true {@link #isRunning()} should return false, however, if
   * the {@link #next(int)} method is used {@link #isPausing()} and
   * {@link #isRunning()} return true. If it returns false {@link #isRunning()}
   * or {@link #isStopping()} should return true if the sim is running or has
   * been running, otherwise it is in the initial state.
   * 
   * @return true, if the processor is pausing
   */
  boolean isPausing();

  /**
   * This method is used to check whether this processor is running. A run can
   * be paused which makes this method returning false, but the
   * {@link #isPausing()} will return true.
   * 
   * @return true if the processor is running
   */
  boolean isRunning();

  /**
   * This method is used to check whether the processor is stopping.
   * 
   * @return true, if the processor is stopping
   */
  boolean isStopping();

  /**
   * Pause the simulation execution. For un-pausing recall this method, or any
   * of the run methods.
   */
  void pause();

  /**
   * Run infinitely (or until the model terminates itself). You can only use one
   * of the run methods per call, so please make your decision which to use
   * before calling on of these. However, each run method can be paused, and
   * then you can continue stepwise by using the {@link #next(int)} method.
   */
  void run();

  /**
   * Run until end time. You can only use one of the run methods per call, so
   * please make your decision which to use before calling on of these. However,
   * each run method can be paused, and then you can continue stepwise by using
   * the {@link #next(int)} method.
   * 
   * @param end
   *          the stop policy the simulation algorithm shall stop at
   */
  void run(IComputationTaskStopPolicy end);

  /**
   * Run until end time. You can only use one of the run methods per call, so
   * please make your decision which to use before calling on of these. However,
   * each run method can be paused, and then you can continue stepwise by using
   * the {@link #next(int)} method.
   * 
   * @param end
   *          the stop policy the simulation algorithm shall stop at
   * @param pause
   *          the pause between two steps in ms
   */
  void run(IComputationTaskStopPolicy end, long pause);

  /**
   * Run until end time. You can only use one of the run methods per call, so
   * please make your decision which to use before calling on of these. However,
   * each run method can be paused, and then you can continue stepwise by using
   * the {@link #next(int)} method.
   * 
   * @param end
   *          the stop policy the simulation algorithm shall stop at
   * @param pause
   *          the pause between two steps in ms
   * @param paused
   *          if true the sim run will be started in paused mode
   */
  void run(IComputationTaskStopPolicy end, long pause, boolean paused);

  /**
   * Set a new delay value. Can be used to remove a prev. set delay (by using 0
   * as passed value), or to increase / decrease a currently used delay. Please
   * note that the delay of paced processors cannot be modified by this method.
   * 
   * @param pause
   */
  void setDelay(long pause);

  /**
   * Next number of steps (or until end time which has been set by a prev call
   * to run). Thus to use this method you have to have started the execution by
   * one of the run methods of this interface. This method cannot be paused,
   * thus all steps are executed on after the other. However, any pause time
   * will be taken into account as well.
   * 
   * @param num
   *          number of steps
   */
  void next(int num);

  /**
   * Stops the simulation. Once stopped the simulation cannot be restarted.
   */
  void stop();

  /**
   * Gets the processor status as an enum type. Status can be derived as well by
   * using the {@link #isPausing()}, {@link #isRunning()}, and
   * {@link #isStopping()} methods. However, this status here can be more
   * detailed.
   * 
   * @return the status of the simulation algoritm
   */
  ProcessorStatus getStatus();

}

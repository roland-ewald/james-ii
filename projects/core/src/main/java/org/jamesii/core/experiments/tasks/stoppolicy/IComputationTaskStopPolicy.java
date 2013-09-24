/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.tasks.stoppolicy;

import org.jamesii.core.experiments.tasks.IComputationTask;

/**
 * The Interface IComputationTaskStopPolicy. There are plenty of possibilities
 * which can be used to determine the proper end of a computation. Classical
 * examples are
 * <table>
 * <tr>
 * <td>Infinity</td>
 * <td>Run until there are no more events/no more advances</td>
 * </tr>
 * <tr>
 * <td>End time</td>
 * <td>Run until a fixed end time has been reached</td>
 * </tr>
 * <tr>
 * <td>Wallclock time</td>
 * <td>Run for n seconds of wallclock time (e.g., for benchmarking purposes)</td>
 * </tr>
 * <tr>
 * <td>Quality</td>
 * <td>Run until a quality measure is okay</td>
 * </tr>
 * </table>
 * 
 * <b>Please note:</b> A not efficient implementation of this interface can
 * significantly hamper an efficient execution of the computation task.
 * 
 * @param <T>
 *          the IComputationTask or descendant interface / class this stop
 *          policy can work on.
 * 
 * @author Jan Himmelspach
 */
public interface IComputationTaskStopPolicy<T extends IComputationTask> {

  /**
   * Checks whether the computation has reached the "end" or not. A computation
   * task end can be determined by different factors which have to be evaluated
   * by this method after each step of the simulation algorithm it is used in
   * combination with. This means that a not efficient implementation of this
   * method can significantly slow down the overall computation execution speed.
   * I.e., if extensive data has to be accessed it might be advisable to trade
   * off execution time delay due to this method and an early computation task
   * abort. This can mean that it might be helpful to validate the quality of
   * the simulation runs only after each n-th step, or to use a concurrent
   * thread to compute the maturity of the results while the simulation run is
   * still being executed in a different thread. <br>
   * <b>Please note:</b> This method has to return <i>true</i> for all calls to
   * it after it returned true for the first time. (And before that it has to
   * return false). Each call of this method might affect the run condition -
   * thus it should be called only once per simulation step.
   * 
   * 
   * @param task
   *          the task to check for ending
   * 
   * @return true, if the end condition is true, thus if the computation task
   *         shall be stopped
   */
  boolean hasReachedEnd(T task);

}

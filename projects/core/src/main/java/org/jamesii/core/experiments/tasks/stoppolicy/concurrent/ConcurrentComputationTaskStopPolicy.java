/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.tasks.stoppolicy.concurrent;

import org.jamesii.core.experiments.tasks.IComputationTask;
import org.jamesii.core.experiments.tasks.stoppolicy.IComputationTaskStopPolicy;
import org.jamesii.core.util.Semaphore;

/**
 * The Class ConcurrentComputationTaskStopPolicy. The stop policy will be
 * executed in parallel to the computation. Thus to continue the calculation the
 * hasReachEnd will return control immediately. This means that a simulation run
 * might compute too many steps, as the value of end might be changed too late.
 * 
 * The threaded policy will be executed as often as it is executed in the
 * sequential case - until it returns true. This means that in relation to the
 * number of simulation steps executed the policy might be executed less times
 * as some of the simulation steps would not have been computed in the
 * sequential case. Consequently this does only work if the policies do not
 * crash if they are executed on data which contains "future" data from their
 * point of view (as they are executed per simulation step).
 * 
 * @author Jan Himmelspach
 */
public class ConcurrentComputationTaskStopPolicy extends Thread implements
    IComputationTaskStopPolicy<IComputationTask> {

  /** The end flag. Will be set by the thread if the end has reached. */
  private boolean end = false;

  /** The (counting) semaphore. */
  private final Semaphore sem = new Semaphore(0);

  /** The criteria which shall be executed in a thread. */
  private final IComputationTaskStopPolicy<IComputationTask> threadedPolicy;

  private IComputationTask task;

  /**
   * Create an instance of the concurrent computation task stop policy. The
   * passed stop policy will be executed in concurrency.
   * 
   * @param threadedPolicy
   */
  public ConcurrentComputationTaskStopPolicy(
      IComputationTaskStopPolicy<IComputationTask> threadedPolicy) {
    super();
    this.threadedPolicy = threadedPolicy;
  }

  @Override
  public boolean hasReachedEnd(IComputationTask t) {
    // policy needs to be queried again
    synchronized (task) {
      task = t;
    }
    sem.v();
    return end;
  }

  @Override
  public void run() {
    while (!end) {
      sem.p();
      IComputationTask t;
      synchronized (task) {
        t = task;
      }
      end = threadedPolicy.hasReachedEnd(t);
    }
  }

  @Override
  public String toString() {
    return "Concurrent " + threadedPolicy;
  }

}

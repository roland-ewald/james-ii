/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.computationserver;

import java.io.Serializable;
import java.util.concurrent.Callable;

/**
 * Class to run {@link IJob} objects on a {@link ComputationServer}.
 * 
 * @author Stefan Leye
 * 
 * @param <V>
 *          the type of the jobs result
 */
public class JobRunner<V> implements Callable<V> {

  /**
   * Enumeration, representing the information, whether a job shall be
   * initialized or finished. This information should be given via the data
   * attribute.
   * 
   * @author Stefan Leye
   */
  public static enum Duty {

    INITIALIZE, FINISH
  }

  /**
   * The job to be executed
   */
  private IJob<V> job;

  /**
   * The data, the job requires for execution
   */
  private Serializable data;

  /**
   * Default constructor.
   * 
   * @param job
   *          the job to be executed
   * @param data
   *          the data the job requires for execution
   */
  public JobRunner(IJob<V> job, Serializable data) {
    this.job = job;
    this.data = data;
  }

  /**
   * Get the priority of the job. Does not work on the computation server so
   * far!
   * 
   * @return the priority
   */
  @Deprecated
  public Integer getPriority() {
    return job.getPriority();
  }

  /**
   * Returns the job runner with the higher priority. If both have an equal
   * priority, the first runner is returned.
   * 
   * @param runner1
   *          the first runner
   * @param runner2
   *          the second runner
   * @return the runner with higher priority.
   */
  @Deprecated
  public static JobRunner<?> getPriorityRunner(JobRunner<?> runner1,
      JobRunner<?> runner2) {
    if (runner1.getPriority().compareTo(runner2.getPriority()) <= 0) {
      return runner1;
    }
    return runner2;
  }

  /**
   * Executes the job and return the result. If data is equal to
   * Duty.INITIALIZE, the job's initialization method will be called. If data is
   * equal to Duty.FINISH, the job's finishing method will be called.
   */
  @Override
  public V call() {
    if (data == Duty.INITIALIZE) {
      job.initialize();
      return null;
    }
    if (data == Duty.FINISH) {
      job.finish();
      return null;
    }
    return job.execute(data);
  }

}

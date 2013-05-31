/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.computationserver;

import java.io.Serializable;

/**
 * Interface for jobs which can be executed on on a
 * {@link org.jamesii.core.distributed.computationserver.ComputationSever}.
 * 
 * @author Stefan Leye
 * 
 * @param <V>
 *          type of the return value of the execution.
 */
public interface IJob<V> extends Serializable {

  /**
   * Get the priority of the job. Does not work on the computation server so
   * far!
   * 
   * @return the priority
   */
  @Deprecated
  Integer getPriority();

  /**
   * Initializes the job.
   */
  void initialize();

  /**
   * Executes the job with the given data. Can be executed multiple times after
   * the job has been initialized and before it has been finished.
   * 
   * @param data
   *          required to execute the job
   * @return return value
   */
  V execute(Serializable data);

  /**
   * Finishes the job.
   */
  void finish();

}

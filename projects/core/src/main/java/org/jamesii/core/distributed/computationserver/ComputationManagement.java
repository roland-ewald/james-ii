/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.computationserver;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.jamesii.SimSystem;
import org.jamesii.core.base.Entity;
import org.jamesii.core.util.id.IUniqueID;

/**
 * 
 * @author Stefan Leye
 * 
 */
public class ComputationManagement extends Entity {

  /**
   * The serialization ID.
   */
  private static final long serialVersionUID = -5579431882619474323L;

  /**
   * The thread pool used to execute the jobs.
   */
  private transient ExecutorService threadPool;

  /**
   * The capacity.
   */
  private int capacity;

  private transient Map<IUniqueID, IJob<?>> jobIDs = new Hashtable<>();

  public ComputationManagement(int capacity) {
    this.capacity = capacity;
    threadPool =
        new ThreadPoolExecutor(1, capacity, Integer.MAX_VALUE,
            TimeUnit.NANOSECONDS, new LinkedBlockingQueue<Runnable>());
  }

  public int getParallelCapacity() {
    return capacity;
  }

  public <V> void initializeJob(IJob<V> job, IUniqueID id)
      throws RemoteException {
    jobIDs.put(id, job);
    JobRunner<V> runner = new JobRunner<>(job, JobRunner.Duty.INITIALIZE);
    try {
      threadPool.submit(runner).get();
    } catch (Exception e) {
      SimSystem.report(e);
    }
  }

  @SuppressWarnings("unchecked")
  // if the return type of the job does not fit to the message call
  // someone mixed up the ID's or the jobs
  public <V> V executeJob(IUniqueID id, Serializable data) {
    IJob<V> job = (IJob<V>) jobIDs.get(id);
    JobRunner<V> runner = new JobRunner<>(job, data);
    try {
      return threadPool.submit(runner).get();
    } catch (Exception e) {
      SimSystem.report(e);
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  // if the return type of the job does not fit to the message call
  // someone mixed up the ID's or the jobs
  public <V extends Serializable> void finalizeJob(IUniqueID id)
      throws RemoteException {
    IJob<V> job = (IJob<V>) jobIDs.remove(id);
    JobRunner<V> runner = new JobRunner<>(job, JobRunner.Duty.FINISH);
    try {
      threadPool.submit(runner).get();
    } catch (Exception e) {
      SimSystem.report(e);
    }
  }
}

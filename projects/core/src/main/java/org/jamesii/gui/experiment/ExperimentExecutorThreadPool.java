/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.experiment;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Singleton class to handle multiple experiment execution threads in the GUI.
 * 
 * @author Roland Ewald
 */
public final class ExperimentExecutorThreadPool {

  /** Initial size of thread pool. */
  private int poolSize = 5;

  /** Maximum size of thread pool. */
  private int maxPoolSize = 5;

  /** Time a process waits within the queue until it will be executed. */
  private long keepAliveTime = 60;

  /** The queue containing all waiting jobs. */
  private final ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(
      maxPoolSize);

  /** Singleton. */
  private static ExperimentExecutorThreadPool instance;

  /** The thread pool that is used. */
  private final ThreadPoolExecutor executor;

  /**
   * Default constructor.
   */
  private ExperimentExecutorThreadPool() {
    executor =
        new ThreadPoolExecutor(poolSize, maxPoolSize, keepAliveTime,
            TimeUnit.SECONDS, queue);
  }

  /**
   * Gets the executor.
   * 
   * @return the executor
   */
  public Executor getExecutor() {
    return executor;
  }

  /**
   * Gets the single instance of ExperimentExecutorThreadPool.
   * 
   * @return single instance of ExperimentExecutorThreadPool
   */
  public static synchronized ExperimentExecutorThreadPool getInstance() {
    if (instance == null) {
      instance = new ExperimentExecutorThreadPool();
    }

    return instance;
  }

}

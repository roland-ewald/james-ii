/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.taskrunner.parallel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.jamesii.core.distributed.masterserver.IMasterServer;
import org.jamesii.core.experiments.TaskConfiguration;

/**
 * Class to organize the thread pools for the
 * {@link ParallelComputationTaskRunner}. The class maintains a local thread
 * pool ({@link #threadPool}) which will be used to compute and computation task
 * (and all of its replications) on the local machine. In addition it maintains
 * a list of thread pools used to control the computations does on different
 * master servers ({@link #masterServerPools}).
 * 
 * @author Stefan Leye
 * 
 */
public class ThreadPoolHandler {

  /**
   * The default thread pool for handling local computations. A call to the
   * {@link #getAccordingThreadPool(TaskConfiguration)} method will always
   * return this thread pool if the computation is to be done on the local
   * machine.
   */
  private ExecutorService threadPool;

  /**
   * Map of thread pools for computations running on remote hosts.
   */
  private Map<IMasterServer, ExecutorService> masterServerPools =
      new HashMap<>();

  /**
   * Map of master servers and the configurations using them.
   */
  private Map<IMasterServer, Set<TaskConfiguration>> runningConfigurations =
      new HashMap<>();

  /**
   * Flag determining whether the thread pools have been shut down.
   */
  private boolean shutDown = false;

  /**
   * The number of threads to be used in pools created by this handler. Will be
   * set via the constructor.
   */
  private int threadCount = -1;

  /**
   * Default constructor.
   * 
   * @param threadCount
   *          count of maximum threads executing local computations.
   */
  public ThreadPoolHandler(int threadCount) {
    this.threadCount = threadCount;
    threadPool =
        new ThreadPoolExecutor(threadCount, threadCount, Long.MAX_VALUE,
            TimeUnit.NANOSECONDS, new LinkedBlockingQueue<Runnable>(),
            new DefaultThreadFactory());
  }

  /**
   * Submits a {@link ComputationTaskExecutionJob} to the according thread pool.
   * 
   * @param config
   *          the computation task configuration for the thread
   * @param job
   *          the {@link ComputationTaskExecutionJob}
   */
  public void submitJob(TaskConfiguration config,
      ComputationTaskExecutionJob job) {
    if (shutDown) {
      return;
    }
    ExecutorService pool = getAccordingThreadPool(config);
    pool.submit(job);
  }

  /**
   * Retrieves the thread pool for the computation task configuration passed.
   * The result will for all task configurations the same thread pool if and
   * only if they are to be executed on the local machine. Otherwise the thread
   * pool is retrieved from the {@link #masterServerPools} list (execution by
   * using a master server).
   * 
   * @param compTaskConfig
   *          the computation task configuration
   * @return the thread pool
   */
  private synchronized ExecutorService getAccordingThreadPool(
      TaskConfiguration compTaskConfig) {
    ExecutorService result = threadPool;
    if (compTaskConfig.useMasterServer()) {

      IMasterServer server = compTaskConfig.getMasterServer();
      result = masterServerPools.get(server);
      Set<TaskConfiguration> runningConfigs = runningConfigurations.get(server);
      if (result == null) {

        // int threadCount =
        // server.getNumberOfRegisteredServices(ISimulationServer.class);
        // removed, now using the same number of max threads per server,
        // independent from the resources, resources do not work well as we
        // might use more than one per replication
        result =
            new ThreadPoolExecutor(threadCount, threadCount, Long.MAX_VALUE,
                TimeUnit.NANOSECONDS, new LinkedBlockingQueue<Runnable>());
        masterServerPools.put(server, result);
        runningConfigs = new HashSet<>();
        runningConfigurations.put(server, runningConfigs);
      }
      runningConfigs.add(compTaskConfig);

    }
    return result;
  }

  /**
   * Shuts all thread pools down.
   */
  public void cleanUpConfig(TaskConfiguration taskConfig) {
    if (shutDown) {
      return;
    }
    if (taskConfig.useMasterServer()) {
      IMasterServer server = taskConfig.getMasterServer();
      synchronized (server) {
        Set<TaskConfiguration> runningConfigurationsOnServer =
            runningConfigurations.get(server);
        runningConfigurationsOnServer.remove(taskConfig);
        if (runningConfigurationsOnServer.isEmpty()) {
          runningConfigurations.remove(server);
          masterServerPools.remove(server).shutdown();
        }
      }
    }
  }

  /**
   * Shuts all thread pools down.
   */
  public void shutDown() {
    shutDown = true;
    for (Map.Entry<IMasterServer, ExecutorService> entry : masterServerPools
        .entrySet()) {
      entry.getValue().shutdown();
    }
    masterServerPools.clear();
    runningConfigurations.clear();
    threadPool.shutdown();
  }

  /**
   * The default thread factory
   */
  static class DefaultThreadFactory implements ThreadFactory {
    private static final AtomicInteger poolNumber = new AtomicInteger(1);

    private final ThreadGroup group;

    private final AtomicInteger threadNumber = new AtomicInteger(1);

    private final String namePrefix;

    DefaultThreadFactory() {
      SecurityManager s = System.getSecurityManager();
      group =
          (s != null) ? s.getThreadGroup() : Thread.currentThread()
              .getThreadGroup();
      namePrefix = "pool-" + poolNumber.getAndIncrement() + "-thread-";
    }

    @Override
    public Thread newThread(Runnable r) {
      Thread t =
          new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
      if (t.isDaemon()) {
        t.setDaemon(false);
      }
      if (t.getPriority() != Thread.MAX_PRIORITY) {
        t.setPriority(Thread.MAX_PRIORITY);
      }
      return t;
    }
  }

}

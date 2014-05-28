/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * LICENCE: JAMESLIC
 * 
 * @author Stefan Rybacki
 * 
 */
public final class ForLoop {
  public static final int POOL_SIZE = Math.max(1, Runtime.getRuntime()
      .availableProcessors());

  private static final ExecutorService THREADPOOL = new ThreadPoolExecutor(
      POOL_SIZE, POOL_SIZE, 60L, TimeUnit.MINUTES,
      new SynchronousQueue<Runnable>(), new ThreadFactory() {
        private final AtomicInteger counter = new AtomicInteger();

        @Override
        public Thread newThread(Runnable r) {
          return new Thread(r, "MT-For-Loop-Thread-"
              + counter.incrementAndGet());
        }
      }, new CallerRunsPolicy());

  private ForLoop() {
  }

  /**
   * Executes the {@link ForKernel} using indexes starting at {@value from}
   * ending at {@value to} using all available cores.
   * 
   * @param from
   *          from index
   * @param to
   *          to index
   * @param run
   *          kernel to execute to-from times
   */
  // NOSONAR: for is reserved hence FOR
  public static void FOR(int from, int to, ForKernel run) {
    FOR(from, to, run, POOL_SIZE);
  }

  /**
   * Executes the {@link ForKernel} using indexes starting at {@value from}
   * ending at {@value to} using a maximum number of cores. Beware that {@value
   * numCores} can not be greater (or has no effect) than the physical number of
   * cores available on the system.
   * 
   * @param from
   *          from index
   * @param to
   *          to index
   * @param run
   *          kernel to execute to-from times
   * @param numberOfCores
   *          maximum number of cores to use (if it is greater than actual cores
   *          available the actual number of cores is used instead)
   */
  // NOSONAR: for is reserved hence FOR
  public static void FOR(int from, int to, final ForKernel run,
      int numberOfCores) {
    int width = to - from;
    int numCores = Math.min(POOL_SIZE, numberOfCores);

    if (width > 0) {
      // partitioning
      final int chunkSize = width / numCores + 1;
      final int jobCount = (int) Math.ceil((double) width / chunkSize);
      final int lastChunk = width - ((jobCount - 1) * chunkSize);

      final Semaphore s = new Semaphore(0);
      for (int i = 0; i < jobCount; i++) {
        final int start = i * chunkSize + from;
        if (i < jobCount - 1) {
          THREADPOOL.execute(new Runnable() {

            @Override
            public void run() {
              for (int j = 0; j < chunkSize; j++) {
                run.run(j + start);
              }
              s.release();
            }
          });
        } else {
          THREADPOOL.execute(new Runnable() {

            @Override
            public void run() {
              for (int j = 0; j < lastChunk; j++) {
                run.run(j + start);
              }
              s.release();
            }
          });
        }
      }

      try {
        s.acquire(jobCount);
      } catch (InterruptedException e) {
      }

    }
  }

}

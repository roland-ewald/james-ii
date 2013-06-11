/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.services.availability;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.jamesii.SimSystem;
import org.jamesii.core.services.IService;
import org.jamesii.core.services.management.IServiceManagement;

/**
 * The class tests if services managed by the service management class passed
 * are available.
 * 
 * @author Thomas Nösinger
 * @author Jan Himmelspach
 */
public final class Availability {

  /**
   * Default waiting time between two trials.
   */
  public static final int DISTANCE = 60000 * 5;

  /**
   * Default number of trials.
   */
  public static final int TRIALS = 3;

  /**
   * Default number of hosts to be checked concurrently.
   */
  public static final int CONCURRENT_TESTS = 4;

  /** Indicates how many services are tested concurrently. */
  private int concurrentTests = CONCURRENT_TESTS;

  /** Indicates how many milliseconds the time between testing is. */
  private long distance = DISTANCE;

  /** Index of the host which was recently checked. */
  private int recentServiceIndex = -1;

  /** Controls the creation of new threads. */
  private boolean stop = false;

  /** Indicates how often a host is tested. */
  private int trials = TRIALS;

  /** The management class to be used. */
  private IServiceManagement management;

  /**
   * The availability prospect to be informed, whenever a service is detected as
   * unreachable.
   */
  private IAvailabilityProspect<IService> availabilityProspect;

  /**
   * List of checkers created for this availability class.
   */
  private ExecutorService availabilityCheckerService;

  private Thread availabilityCheckerScheduler;

  /**
   * The constructor of the class. If no value for the count, trials or distance
   * is given, default values are used.
   * 
   * @param management
   *          the list of services to be managed
   * @param count
   *          indicates how much hosts are tested concurrently (default is 4)
   * @param trials
   *          indicates how often a host is tested (default is 3)
   * @param distance
   *          indicates how many millisecond the time between testing is
   *          (default is 5 minutes)
   * @param availabilityProspect
   *          the availability prospect
   * 
   */
  @SuppressWarnings("unchecked")
  public Availability(IServiceManagement management,
      IAvailabilityProspect<? extends IService> availabilityProspect,
      int count, int trials, long distance) {

    // set the management class to be used for retrieving the list of services
    this.management = management;

    // sets the availability prospect to be informed, whenever a service is
    // detected as unreachable
    this.availabilityProspect =
        (IAvailabilityProspect<IService>) availabilityProspect;

    if (count > 0) {
      this.concurrentTests = count;
    }
    if (trials > 0) {
      this.trials = trials;
    }
    if (distance > 0) {
      this.distance = distance;
    }
    // create the number of threads required
    availabilityCheckerService =
        new ThreadPoolExecutor(concurrentTests, concurrentTests,
            Long.MAX_VALUE, TimeUnit.NANOSECONDS,
            new LinkedBlockingQueue<Runnable>(), new DefaultThreadFactory());

    availabilityCheckerScheduler = new Thread(new AvailabilityChecker());
    availabilityCheckerScheduler.start();
  }

  /**
   * The method returns index of the next service, which shall be tested for
   * availability.
   * 
   * @return the next service number
   */
  private synchronized int getNextServiceIndex() {
    recentServiceIndex++;

    // check if index of recent service exceeds number of available services
    if (recentServiceIndex >= management.size()) {
      recentServiceIndex = 0;
    }

    return recentServiceIndex;
  }

  /**
   * The method returns the next service, which shall be tested for
   * availability.
   * 
   * @return the next service number
   */
  private synchronized IService getNextService() {
    IService result = null;
    try {
      result = management.get(getNextServiceIndex());
    } catch (ArrayIndexOutOfBoundsException e) {
      SimSystem.report(e);
    }
    return result;
  }

  /**
   * The method stops the availability checker threads. If stopped the threads
   * cannot be started again - you have to create a new instance of this class
   * to restart the availability checking.
   */
  public void setStop() {
    stop = true;
  }

  /**
   * The availability checker runnable tries to communicate with the services
   * registered.
   * 
   * @author Jan Himmelspach
   * 
   */
  private class AvailabilityChecker implements Runnable {

    @Override
    public void run() {

      int counter = 0;

      while (!stop) {

        availabilityCheckerService.submit(new AvailabilityCheck(
            getNextService()));

        counter++;

        // if a test for each of the services has been scheduled we'll sleep for
        // a while
        if (counter >= management.size()) {
          // controls the frequency of the tests
          try {
            Thread.sleep(distance);
          } catch (InterruptedException e1) {
          }
          counter = 0;
        }
      }
    }
  }

  /**
   * The runnable doing the actual test.
   * 
   * @author Jan Himmelspach
   * 
   */
  private class AvailabilityCheck implements Runnable {

    private IService service;

    public AvailabilityCheck(IService serviceToBeChecked) {
      service = serviceToBeChecked;
    }

    @Override
    public void run() {
      // check to make sure that we got a service to test
      if (service != null) {
        // repeat the test for "trials" times
        boolean again = true;
        int trial = trials;

        while (again) {
          try {
            // if stop has been set get out
            if (stop) {
              return;
            }
            // test by trying to read the name of the service (by doing a
            // remote method invocation)
            service.getName();
            // if we get here everything is fine -> get out
            again = false;
          } catch (Exception e) {
            // if we get here the service did not respond
            trial--;

            // if we tried the number of trials set we have to notify that the
            // service is not reachable
            if (trial <= 0) {

              if (availabilityProspect != null) {
                availabilityProspect.serviceUnreachable(service);
              }

              // get out here
              again = false;
            }
          }

        }
      }
    }
  }

  /**
   * The default thread factory
   */
  static class DefaultThreadFactory implements ThreadFactory {
    private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);

    private final ThreadGroup group;

    private final AtomicInteger threadNumber = new AtomicInteger(1);

    private final String namePrefix;

    DefaultThreadFactory() {
      SecurityManager s = System.getSecurityManager();
      group =
          (s != null) ? s.getThreadGroup() : Thread.currentThread()
              .getThreadGroup();
      namePrefix = "availability-" + POOL_NUMBER.getAndIncrement() + "-thread-";
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

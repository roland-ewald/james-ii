/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.misc;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

/**
 * Simple helper class to generate synthetic load. Stores some bogus computation
 * result to a member just to prevent the JIT compiler to remove the senseless
 * for loops.
 * 
 * @author Roland Ewald
 * 
 */
public class LoadGenerator {

  /**
   * A bogus value. Stores the results of pseudo-calculations to generate some
   * load.
   */
  private double bogusValue;

  /** Reference to thread monitor. */
  private transient ThreadMXBean threads;

  /**
   * Standard constructor.
   */
  public LoadGenerator() {
    threads = ManagementFactory.getThreadMXBean();
    threads.setThreadContentionMonitoringEnabled(true);
    threads.setThreadCpuTimeEnabled(true);
  }

  /**
   * Blocks the current thread for given time span. This function is only
   * accurate from 0.3 - 2s
   * 
   * @param timeSpan
   *          the time span (in microseconds)
   */
  public synchronized void generateLoadInMS(int timeSpan) {
    if (threads == null) {
      threads = ManagementFactory.getThreadMXBean();
      threads.setThreadContentionMonitoringEnabled(true);
      threads.setThreadCpuTimeEnabled(true);
    }
    long start = threads.getCurrentThreadUserTime();
    long additionalTime = timeSpan * 1000L;
    long actualTime = threads.getCurrentThreadUserTime();
    while (start + additionalTime > actualTime) {
      actualTime = threads.getCurrentThreadUserTime();
    }
  }

  /**
   * Calls {@link LoadGenerator#generateLoadInMS(timeSpan)}. This function has
   * an accuracy of ~ 0.1 seconds
   * 
   * @param numOfSeconds
   */
  public synchronized void generateLoadInS(double numOfSeconds) {
    int numOfS = (int) numOfSeconds;
    for (int i = 0; i < numOfS; i++) {
      generateLoadInMS(1000000);
    }
    int numOfMS = (int) ((numOfSeconds - numOfS) * 1000000);
    generateLoadInMS(numOfMS);
  }

  /**
   * Generates synthetic load.
   * 
   * @param load
   *          a load parameter; the higher the more load
   */
  public void generateLoad(int load) {
    int x = 0;
    for (int i = 0; i < load; i++) {
      for (int j = 0; j < 1000000; j++) {
        x += (i % 2345 - 0.35 * (j % 6789));
      }
    }
    bogusValue += x;
  }

}

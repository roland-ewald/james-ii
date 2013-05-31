/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.processor;

/**
 * The IThreadedProcessor interface must be implemented by all processors which
 * require an own thread. The thread will be started by a call of startThread
 * which has to be automatically done by the parent processor. The startThread
 * method must start the thread and the threads of all children of the processor
 * implementing this interface (if the processor has children).
 * 
 * @author Jan Himmelspach
 * @author Björn Paul
 */
public interface IThreadedProcessor {

  /**
   * Returns true if the thread is currently running.
   * 
   * @return true, if thread is running
   */
  boolean isThreadRunning();

  /**
   * Starts the thread of an processor (and of all children!!).
   */
  void startThread();

  /**
   * Stop the thread as soon as possible (and of all children).
   */
  void stopThread();

}

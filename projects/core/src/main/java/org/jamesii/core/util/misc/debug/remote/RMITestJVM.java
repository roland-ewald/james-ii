/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.misc.debug.remote;

import org.jamesii.SimSystem;
import org.jamesii.core.util.misc.exec.JavaAppExecutionThread;

/**
 * This class is used by {@link SerializableTest} to create a new thread
 * executed in a new JVM. The thread will execute the main method of
 * {@link SerializableTest}.
 * 
 * @author Simon Bartels
 * 
 */
public class RMITestJVM extends JavaAppExecutionThread {

  /**
   * Return value of exit(...) when the RMI_test_thread has been killed.
   */
  private static final int ERR_KILL = -2;

  /**
   * Constructs a new {@link JavaAppExecutionThread} to execute
   * {@link SerializableTest} in a separate process.
   * 
   * @param args
   *          the arguments
   */
  public RMITestJVM(String... args) {
    super(SerializableTest.class, args);
  }

  /**
   * Executes System.exit(-2).
   */
  public void kill() {
    SimSystem.shutDown(ERR_KILL);
  }

}

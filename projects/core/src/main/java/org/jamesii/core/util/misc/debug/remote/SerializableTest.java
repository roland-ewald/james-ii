/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.misc.debug.remote;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.util.logging.ApplicationLogger;
import org.jamesii.core.util.logging.FileLog;
import org.jamesii.core.util.misc.Strings;

/**
 * Class for testing whether an object is Serializable or not. Usage: 1) Start
 * main() method. (Only one time) 2) Create an instance of SerializableTest and
 * call isSerializable(...) with the Object of interest.
 * 
 * @author Simon Bartels
 * 
 */
public class SerializableTest extends UnicastRemoteObject implements
    IRemoteSerializableTest, ISerializableTest {

  /** The serial version ID. */
  private static final long serialVersionUID = 5478210231281105226L;

  /** The port the registry will be bound to. */
  private static final int REGISTRY_PORT = 42235;

  /** The name under which the remote object will be reachable. */
  private static final String NAME = "REMOTE_SERIALIZABLE_TEST";

  /**
   * Return value of exit(...) when the RMI_test_thread coudn't been started.
   */
  private static final int ERR_COULD_NOT_START = -1;

  /** The maximal number of connection re-attempts. */
  private static final int MAX_CONNECTION_REATTEMPTS = 7;

  /** The time to sleep in-between connection attempts. */
  private static final int MILLIS_BETWEEN_CONNECTION_ATTEMPT = 1000;

  /**
   * Instantiates a new Object.
   * 
   * @throws RemoteException
   *           Should not happen.
   */
  public SerializableTest() throws RemoteException {
  }

  /**
   * Creates a registry and binds an object to it which is used to do the
   * serializable test.
   * 
   * @param args
   *          will be part of the log file name
   */
  public static void main(String[] args) {
    try {
      configureLogging(args);
      SerializableTest t = new SerializableTest();
      Registry r = LocateRegistry.createRegistry(REGISTRY_PORT);
      r.bind(NAME, t);
      SimSystem.report(Level.FINE, "waiting for connections ...");
    } catch (Exception e) {
      SimSystem.report(e);
      System.exit(ERR_COULD_NOT_START);
    }
  }

  /**
   * Configures logging when running as stand-alone application. As there is not
   * console, the output will be written into a file, the name of which is
   * constructed from the command line arguments
   * 
   * @param args
   *          the command line arguments
   */
  private static void configureLogging(String[] args) {
    ApplicationLogger.disableConsoleLog();
    ApplicationLogger.setLogLevel(Level.FINEST);
    ApplicationLogger.addLogListener(new FileLog("Output-"
        + SerializableTest.class.getName() + "-" + Strings.dispArray(args)
        + ".log"));
  }

  @Override
  public synchronized boolean isSerializable(Serializable testObject) {
    RMITestJVM rmiTestJVM =
        new RMITestJVM(testObject.getClass().getCanonicalName());
    rmiTestJVM.start();

    Registry rmiRegistry = connectToRMIRegistry();

    if (rmiRegistry == null) {
      SimSystem.report(Level.SEVERE,
          "Connection to RMI registry could not be established.");
      rmiTestJVM.kill();
      return false;
    }

    SimSystem.report(Level.INFO, "Connection to RMI registry established.");

    IRemoteSerializableTest remoteTest = getRemoteTest(rmiRegistry);

    if (remoteTest == null) {
      SimSystem.report(Level.SEVERE,
          "Connection to remote test could not be established.");
      rmiTestJVM.kill();
      return false;
    }

    SimSystem.report(Level.INFO,
        "Connection to remotely running test established.");

    try {
      SimSystem.report(Level.INFO,
          "Testing remote method invocation with object...");
      remoteTest.serializableTestMethod(testObject);
      SimSystem.report(Level.INFO, "Remote method invocation done.");
    } catch (RemoteException e) {
      SimSystem.report(e);
      return false;
    } finally {
      try {
        remoteTest.shutDown();
      } catch (RemoteException e) {
        SimSystem.report(e);
        rmiTestJVM.kill();
      }
    }
    return true;
  }

  /**
   * Gets the remote test.
   * 
   * @param rmiRegistry
   *          the rmi registry
   * @return the remote test
   */
  public IRemoteSerializableTest getRemoteTest(Registry rmiRegistry) {
    IRemoteSerializableTest remoteTest = null;
    int i = 0;
    while (i < MAX_CONNECTION_REATTEMPTS && remoteTest == null) {
      try {
        remoteTest = (IRemoteSerializableTest) rmiRegistry.lookup(NAME);
      } catch (Exception e) {
        SimSystem.report(Level.SEVERE, "Connection problem - reattempt #"
            + (i + 1) + " of " + MAX_CONNECTION_REATTEMPTS, e);
      }
      try {
        Thread.sleep(MILLIS_BETWEEN_CONNECTION_ATTEMPT);
      } catch (InterruptedException e) {
        SimSystem.report(e);
      }
      i++;
    }
    return remoteTest;
  }

  /**
   * Connects to an RMI registry. Attempts connection for
   * {@link SerializableTest#MAX_CONNECTION_REATTEMPTS} with a
   * 
   * @return the registry
   */
  private Registry connectToRMIRegistry() {
    Registry rmiRegistry = null;
    int i = 0;
    while (i < MAX_CONNECTION_REATTEMPTS && rmiRegistry == null) {
      try {
        rmiRegistry = LocateRegistry.getRegistry(REGISTRY_PORT);
      } catch (Exception e) {
        SimSystem.report(Level.SEVERE, "Connection problem - reattempt #"
            + (i + 1) + " of " + MAX_CONNECTION_REATTEMPTS, e);
      }
      try {
        Thread.sleep(MILLIS_BETWEEN_CONNECTION_ATTEMPT);
      } catch (InterruptedException e) {
        SimSystem.report(e);
      }
      i++;
    }
    return rmiRegistry;
  }

  @Override
  public void serializableTestMethod(Serializable o) throws RemoteException {
    SimSystem.report(Level.FINE, this.getClass().getCanonicalName() + ":"
        + o.getClass().getCanonicalName() + " is serializable");
  }

  @Override
  public synchronized void shutDown() throws RemoteException {
    UnicastRemoteObject.unexportObject(this, false);

    final SerializableTest thisTestObject = this;

    // we start a new thread which will exit the process after this method
    // returned its value
    Thread t = new Thread() {
      @Override
      public void run() {
        synchronized (thisTestObject) {
          System.exit(0);
        }
      }
    };
    t.start();
  }

  @Override
  public boolean isSerializable2(Serializable o) {
    Registry r;
    try {
      r = LocateRegistry.getRegistry(REGISTRY_PORT);
    } catch (RemoteException e) {
      SimSystem.report(e);
      return false;
    }

    IRemoteSerializableTest t;
    try {
      t = (IRemoteSerializableTest) r.lookup(NAME);
    } catch (Exception e) {
      SimSystem.report(e);
      return false;
    }

    try {
      t.serializableTestMethod(o);
    } catch (RemoteException e) {
      return false;
    }

    return true;
  }

}

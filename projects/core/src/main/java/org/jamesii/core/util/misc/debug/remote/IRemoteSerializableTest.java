/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.misc.debug.remote;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface for {@link SerializableTest}.
 * 
 * @author Simon Bartels
 * 
 */
public interface IRemoteSerializableTest extends Remote {
  /**
   * A dummy method to check if an object is serializable.
   * 
   * @param testObject
   *          object to be tested.
   * @throws RemoteException
   *           raised when o is not serializable.
   */
  void serializableTestMethod(Serializable testObject) throws RemoteException;

  /**
   * Closes the RMI connection so the thread can terminate.
   * 
   * @throws RemoteException
   *           may be caused by an RMI error.
   */
  void shutDown() throws RemoteException;

}

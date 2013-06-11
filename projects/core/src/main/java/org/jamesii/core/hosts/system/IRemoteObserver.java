/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.hosts.system;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface for a remote service observer.
 * 
 * @author Stefan Leye
 */

public interface IRemoteObserver extends Remote {

  /**
   * Get a the registerer, which to register the observer.
   * 
   * @return the registerer
   * @throws RemoteException
   */
  IObserverRegisterer getRegisterer() throws RemoteException;

  /**
   * Get a the registerer, which to register the observer.
   * 
   * @return the class of the registerer
   * @throws RemoteException
   */
  Class<?> getRegistererClass() throws RemoteException;

  /**
   * This function is called when an entity changes its state. In most cases we
   * do not want to send the entity over the network, but only a hint.
   * 
   * @param hint
   *          the hint
   * 
   * @throws RemoteException
   *           the remote exception
   */
  void update(Object hint) throws RemoteException;

}

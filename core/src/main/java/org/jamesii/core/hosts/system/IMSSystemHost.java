/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.hosts.system;

import java.rmi.RemoteException;

import org.jamesii.core.hosts.IHost;
import org.jamesii.core.observe.IMediator;

/**
 * The Interface ISimulationSystemHost.
 * 
 * @author Jan Himmelspach
 */
public interface IMSSystemHost extends IHost {

  /**
   * Register a remote observer, i.e. an observer residing on a different host.
   * 
   * @param observer
   *          the observer
   * 
   * @return true, if register remote observer
   * 
   * @throws RemoteException
   *           the remote exception
   */
  boolean registerRemoteObserver(IRemoteObserver observer)
      throws RemoteException;

  /**
   * Set a mediator for the components managing the server's jobs.
   * 
   * @param mediator
   *          the mediator
   * @throws RemoteException
   *           the remote exception
   */
  void setManagementMediator(IMediator mediator) throws RemoteException;

}

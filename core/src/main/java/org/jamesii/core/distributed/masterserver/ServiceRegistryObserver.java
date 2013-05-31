/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.masterserver;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.jamesii.core.hosts.system.IObserverRegisterer;
import org.jamesii.core.hosts.system.IRemoteObserver;
import org.jamesii.core.services.ServiceInfo;
import org.jamesii.gui.server.view.master.MasterServerView;

/**
 * Remote observer for the serviceRegistry of a master server.
 * 
 * @author Jan Himmelspach
 * @author Stefan Leye
 */
public class ServiceRegistryObserver extends UnicastRemoteObject implements
    IRemoteObserver {

  /** The serialization ID. */
  private static final long serialVersionUID = 5309787127872047272L;

  /** The window. */
  private transient MasterServerView window;

  /**
   * The registerer, which registers this observer at the service.
   */
  private IObserverRegisterer registerer =
      new ServiceRegistryObserverRegisterer();

  /**
   * This method is called when information about an HostManagement which was
   * previously requested using an asynchronous interface becomes available.
   * 
   * @param window
   *          the window
   * 
   * @throws RemoteException
   *           the remote exception
   */
  public ServiceRegistryObserver(MasterServerView window)
      throws RemoteException {
    super();
    this.window = window;
  }

  @Override
  public IObserverRegisterer getRegisterer() throws RemoteException {
    return registerer;
  }

  @Override
  public void update(Object hint) throws RemoteException {
    if (hint instanceof ServiceInfo) {
      window.registerEvent((ServiceInfo) hint);
    } // else
    // Entity.report(Level.WARNING,
    // "Warning! Update of remote service registry observer, " +
    // "without service info!");
  }

  @Override
  public Class<?> getRegistererClass() throws RemoteException {
    return ServiceRegistryObserverRegisterer.class;
  }

}

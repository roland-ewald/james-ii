/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.services.observer;

import java.rmi.RemoteException;

import org.jamesii.SimSystem;
import org.jamesii.core.hosts.system.IRemoteObserver;
import org.jamesii.core.observe.IObservable;
import org.jamesii.core.observe.Observer;

/**
 * Proxy for a remote
 * {@link org.jamesii.gui.server.view.master.ServiceRegistryObserver}.
 * 
 * @author Stefan Leye
 */
public class ServiceObserverProxy extends Observer {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 2774462056156877452L;

  /** The remote. */
  private IRemoteObserver remote;

  /**
   * Instantiates a new service observer proxy.
   * 
   * @param remote
   *          the remote
   */
  public ServiceObserverProxy(IRemoteObserver remote) {
    this.remote = remote;
  }

  @Override
  public void update(IObservable entity) {
    try {
      remote.update(null);
    } catch (RemoteException e) {
      SimSystem.report(e);
    }
  }

  @Override
  public void update(IObservable entity, Object hint) {
    try {
      remote.update(hint);
    } catch (RemoteException e) {
      SimSystem.report(e);
    }
  }
}

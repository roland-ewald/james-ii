/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.simulationserver;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.jamesii.core.experiments.tasks.ComputationTaskIDObject;
import org.jamesii.core.hosts.system.IObserverRegisterer;
import org.jamesii.core.hosts.system.IRemoteObserver;
import org.jamesii.gui.server.view.simulation.SimulationServerView;

/**
 * Remote observer for the simulation management of a simulation host
 * 
 * @author Stefan Leye
 * 
 */
public class SimulationManagementObserver extends UnicastRemoteObject implements
    IRemoteObserver {

  /** The serialisation ID. */
  private static final long serialVersionUID = 526374482255160617L;

  /** The window. */
  private transient SimulationServerView window;

  /** The registerer, which registers this observer at the service. */
  private IObserverRegisterer registerer =
      new SimManagementObserverRegisterer();

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
  public SimulationManagementObserver(SimulationServerView window)
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
    // TODO handle simulation info
    if (hint instanceof ComputationTaskIDObject) {
      window.simulationEvent((ComputationTaskIDObject) hint);
    } // else
    // Entity.report(Level.WARNING,
    // "Warning! Update of remote service registry observer, " +
    // "without service info!");
  }

  @Override
  public Class<?> getRegistererClass() throws RemoteException {
    return SimManagementObserverRegisterer.class;
  }

}

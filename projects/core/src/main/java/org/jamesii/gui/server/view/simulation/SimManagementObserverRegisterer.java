/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.server.view.simulation;

import java.rmi.RemoteException;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.distributed.simulationserver.SimulationHost;
import org.jamesii.core.distributed.simulationserver.SimulationServer;
import org.jamesii.core.hosts.system.IMSSystemHost;
import org.jamesii.core.hosts.system.IObserverRegisterer;
import org.jamesii.core.hosts.system.IRemoteObserver;
import org.jamesii.core.observe.IObserver;
import org.jamesii.core.services.observer.ServiceObserverProxy;

/**
 * Class to register a {@link SimulationManagementObserver} at a
 * {@link SimulationServer}.
 * 
 * @author Stefan Leye
 * 
 */
public class SimManagementObserverRegisterer implements IObserverRegisterer {

  /** The serialization ID. */
  private static final long serialVersionUID = -6293831458882554598L;

  @Override
  public boolean register(IMSSystemHost host, IRemoteObserver remote) {

    IObserver<?> proxy = new ServiceObserverProxy(remote);
    if (host instanceof SimulationHost) {
      ((SimulationHost) host).getTaskManager().registerObserver(proxy);
      return true;
    }
    try {
      SimSystem
          .report(
              Level.SEVERE,
              "The host "
                  + host.getName()
                  + " is not an instance of the SimulationHost class and thus does not provide the functionality required.");

    } catch (RemoteException e) {
      SimSystem.report(e);

    }
    return false;
  }

}

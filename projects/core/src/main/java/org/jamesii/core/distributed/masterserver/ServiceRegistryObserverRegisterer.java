/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.masterserver;

import java.rmi.RemoteException;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.hosts.system.IMSSystemHost;
import org.jamesii.core.hosts.system.IObserverRegisterer;
import org.jamesii.core.hosts.system.IRemoteObserver;
import org.jamesii.core.observe.IObserver;
import org.jamesii.core.services.observer.ServiceObserverProxy;

/**
 * Class to register a
 * {@link rg.jamesii.core.distributed.simulationserver.SimulationManagementObserver}
 * at a {@link rg.jamesii.core.distributed.simulationserver.SimulationServer}.
 * 
 * @author Stefan Leye
 * 
 */
public class ServiceRegistryObserverRegisterer implements IObserverRegisterer {

  /** The serialization ID. */
  private static final long serialVersionUID = -6293831458882554598L;

  @Override
  public boolean register(IMSSystemHost host, IRemoteObserver remote) {

    IObserver<?> proxy = new ServiceObserverProxy(remote);
    if (host instanceof MasterServer) {
      ((MasterServer) host).getServiceRegistry().registerObserver(proxy);
      return true;
    }
    try {
      SimSystem
          .report(
              Level.SEVERE,
              "The host "
                  + host.getName()
                  + " is not a MasterServer instance and thus does not provide the service registration required.");
    } catch (RemoteException e) {
      SimSystem.report(e);
    }
    return false;
  }

}

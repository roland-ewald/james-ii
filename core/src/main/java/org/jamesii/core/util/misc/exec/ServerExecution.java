/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.misc.exec;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.distributed.masterserver.IMasterServer;
import org.jamesii.core.distributed.masterserver.MasterServer;
import org.jamesii.core.distributed.simulationserver.ISimulationServer;
import org.jamesii.core.distributed.simulationserver.SimulationClientThread;
import org.jamesii.core.hosts.Host;

/**
 * Simple utility class to execute servers.
 * 
 * @author Simon Bartels
 * @author Roland Ewald
 * 
 */
public class ServerExecution {

  /** The ms to wait for the simulation servers havving registered. */
  private static final long WAIT_FOR_SIMSERVERS = 1000;

  /** The number of servers. */
  private final int numberOfServers;

  /** The master serv name. */
  private final String masterServName;

  /** The ms address. */
  private final String msAddress;

  /**
   * Instantiates a new server execution.
   * 
   * @param numOfServers
   *          the num of servers
   * @param masterServerName
   *          the master server name
   * @param masterServerAdress
   *          the master server adress
   */
  public ServerExecution(int numOfServers, String masterServerName,
      String masterServerAdress) {
    numberOfServers = numOfServers;
    masterServName = masterServerName;
    msAddress = masterServerAdress;
  }

  public IMasterServer executeSetup() throws RemoteException,
      InterruptedException {
    IMasterServer server = setUpServers();
    waitForServerThreads(server);
    return server;
  }

  /**
   * Wait for server threads.
   * 
   * @param server
   *          the server
   * 
   * @throws RemoteException
   *           the remote exception
   * @throws InterruptedException
   *           the interrupted exception
   */
  private void waitForServerThreads(IMasterServer server)
      throws RemoteException, InterruptedException {
    while (server.getNumberOfRegisteredServices(ISimulationServer.class) < numberOfServers) {
      SimSystem.report(Level.INFO, "Waiting for servers...");
      Thread.sleep(WAIT_FOR_SIMSERVERS);
    }
  }

  /**
   * Sets the up servers.
   * 
   * @return the i master server
   * 
   * @throws RemoteException
   *           the remote exception
   */
  private IMasterServer setUpServers() throws RemoteException {
    SimSystem.report(Level.CONFIG, "Master server runs at " + msAddress);
    LocateRegistry.createRegistry(MasterServer.DEFAULT_PORT);
    IMasterServer server = new MasterServer(masterServName);
    Host.publish(server, MasterServer.DEFAULT_PORT);

    for (int i = 0; i < numberOfServers; i++) {
      SimulationClientThread sct =
          new SimulationClientThread("-server=" + msAddress, "Client" + (i + 1));
      sct.start();
      SimSystem.report(Level.INFO, "Starting simulation server #" + (i + 1));
    }
    return server;
  }

}

/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.simulationserver;

import java.rmi.RemoteException;

import org.jamesii.SimSystem;
import org.jamesii.core.distributed.masterserver.MasterServer;
import org.jamesii.core.hosts.Host;

/**
 * The Class SimulationClientThread.
 * 
 * @author Gabriel Blum
 */
public class SimulationClientThread extends Thread {

  /** The ccmw. */
  // ClientControlMasterWindow ccmw;

  /** The sim server data. */
  private String[] simServerData = new String[3];

  /**
   * Instantiates a new simulation client thread.
   * 
   * @param masterServerName
   *          the master server name
   * @param serverName
   *          the server name
   */
  public SimulationClientThread(String masterServerName, String serverName) {
    // this.ccmw = ccmw;
    simServerData[1] = serverName;
    simServerData[0] = masterServerName;
    // simServerData[2] = "false";
  }

  @Override
  public void run() {
    try {
      SimulationServer server = new SimulationServer(simServerData);

      // TODO: This method will provoke an exception, when no registry has been
      // created before
      // Testing whether it has been done or not is difficult.
      Host.publish(server, MasterServer.DEFAULT_PORT);

      // SimulationClientControlWindow sccw = new
      // SimulationClientControlWindow(ssv);
      // sccw.setMasterWindow(ccmw);
      // ccmw.addWindow(sccw);

    } catch (RemoteException rex) {
      SimSystem.report(rex);
    }

  }

}

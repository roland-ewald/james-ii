/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.misc.exec;

import org.jamesii.core.distributed.simulationserver.SimulationServer;

/**
 * The Class SimulationClientVMThread.
 * 
 * Executes a simulation computation server
 * {@link org.jamesii.core.distributed.simulationserver.SimulationServer} in an
 * own virtual machine.
 * 
 * Note: if you do not terminate this server after its usage it might remain in
 * the memory of the host it is executed on.
 * 
 * @author Gabriel Blum
 */
public class SimulationServerVMThread extends JavaAppExecutionThread {

  /**
   * Instantiates a new simulation client vm thread.
   * 
   * @param masterServerName
   *          the master server name
   * @param serverName
   *          the server name
   */
  public SimulationServerVMThread(String masterServerName, String serverName) {
    super(SimulationServer.class, masterServerName, serverName, "true");
  }
}

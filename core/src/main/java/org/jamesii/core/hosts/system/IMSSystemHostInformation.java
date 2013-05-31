/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.hosts.system;

import java.rmi.RemoteException;
import java.util.List;

import org.jamesii.core.hosts.IHostInformation;
import org.jamesii.core.plugins.IPluginData;
import org.jamesii.core.util.info.JavaInfo;

/**
 * The Interface ISimulationHostInformation. Summarizes methods for host
 * information getting. Among these the possibility to query the m&s framework's
 * version {@link #getSimSystemVersion()}, get the list of plug-ins
 * {@link #getPluginInfo()}, get the start time of the system
 * {@link #getStartUpTime()}, and getting information about the virtual machine
 * the host is running in {@link #getVMinfo()}.
 * 
 * @author Jan Himmelspach
 */
public interface IMSSystemHostInformation extends IHostInformation {

  /**
   * Returns the name and version information of the simulation system.
   * 
   * @return the sim system version
   * 
   * @throws RemoteException
   *           the remote exception
   */
  String getSimSystemVersion() throws RemoteException;

  /**
   * Gets the virtual machine info of this sever.
   * 
   * @return the virtual machine information of the server as string
   *         representation.
   * 
   * @throws RemoteException
   *           the remote exception
   */
  JavaInfo getVMinfo() throws RemoteException;

  /**
   * Gets the plug-in information from the server. A list of plug-ins available
   * at the server is returned. This list can be used, e.g., for determining
   * whether a server supports the execution of a simulation run with a model in
   * some modeling formalism, or not.
   * 
   * @return the plug-in info as a list of plug-ins
   * 
   * @throws RemoteException
   *           the remote exception
   */
  List<IPluginData> getPluginInfo() throws RemoteException;

  /**
   * Gets the start up time of the simulation system instance. This allows to
   * print something like: host X running for, or since, ...
   * 
   * @return the start up time
   * 
   * @throws RemoteException
   *           the remote exception
   */
  long getStartUpTime() throws RemoteException;

}

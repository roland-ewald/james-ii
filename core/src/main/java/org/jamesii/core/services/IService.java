/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.services;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

/**
 * The Interface IService. A service in JAMES II is by default just something
 * with a name. Concrete services should extend this interface.
 * 
 * @author Jan Himmelspach
 */
public interface IService extends Remote {

  /**
   * Return the name of the service.
   * 
   * @return the name
   * 
   * @throws RemoteException
   *           the remote exception
   */
  String getName() throws RemoteException;

  /**
   * Get the type of the service, i.e., what's the functionality provided by the
   * service.
   * 
   * @return the service type, typically an interface, or a class
   * 
   * @throws RemoteException
   *           the remote exception
   */
  Class<?> getServiceType() throws RemoteException;

  /**
   * Gets the descriptive name of the service.
   * 
   * While {@link #getName()} returns the name of the instance of a service this
   * method should return a descriptive name (e.g., simulation computation
   * server).
   * 
   * @return the service name
   * 
   * @throws RemoteException
   *           the remote exception
   */
  String getServiceName() throws RemoteException;

  /**
   * Gets the maximum number of concurrent jobs which can be executed by this
   * service.<br/>
   * <br/>
   * -1 - unlimited number of jobs <br/>
   * 0 - only one job at a time<br/>
   * n - at most n jobs<br/>
   * 
   * @return the max number of concurrent jobs
   * 
   * @throws RemoteException
   *           the remote exception
   */
  int getMaxNumberOfConcurrentJobs() throws RemoteException;

  /**
   * Gets the address of the host the service resides on.
   * 
   * @return the host address
   * 
   * @throws RemoteException
   *           the remote exception
   */
  java.net.InetAddress getHostAddress() throws RemoteException;

  /**
   * Calls the method with a given name and parameters.
   * 
   * @param method
   *          the method name
   * @param params
   *          list of parameter types (0) and values (1).
   * @throws RemoteException
   *           the remote exception
   */
  void callMethodByName(String method, List<String[]> params)
      throws RemoteException;

  /**
   * Returns all names of methods (and their parameter types and descriptions)
   * having the {@link TriggerableByName} annotation.
   * 
   * @return map of names of the methods and their parameters (class name and
   *         description)
   * @throws RemoteException
   *           the remote exception
   */
  Map<String, List<String[]>> getTriggerableMethods() throws RemoteException;
}

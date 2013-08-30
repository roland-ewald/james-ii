/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.hosts.database;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import org.jamesii.SimSystem;
import org.jamesii.core.services.IService;
import org.jamesii.core.services.TriggerableByNameFilter;

/**
 * Database server information.
 * 
 * @author Roland Ewald
 */
public class DBServerInfo implements Serializable, IService {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 5799697241508070735L;

  /** IP address of server. */
  private String ipAddress;

  /** Name of server. */
  private String name;

  @Override
  public String getName() {
    return name;
  }

  /**
   * Sets the name.
   * 
   * @param name
   *          the new name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Instantiates a new dB server info.
   * 
   * @param ip
   *          the ip
   * @param serverName
   *          the server name
   */
  public DBServerInfo(String ip, String serverName) {
    ipAddress = ip;
    name = serverName;
  }

  /**
   * Gets the ip address.
   * 
   * @return the ip address
   */
  public String getIpAddress() {
    return ipAddress;
  }

  /**
   * Sets the ip address.
   * 
   * @param ipAddress
   *          the new ip address
   */
  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }

  @Override
  public String toString() {
    return "db server info [ip:" + ipAddress + ",name:" + name + "]";
  }

  @Override
  public boolean equals(Object o) {

    if (!(o instanceof DBServerInfo)) {
      return false;
    }
    DBServerInfo inf = (DBServerInfo) o;

    return (ipAddress.compareTo(inf.getIpAddress()) == 0)
        && (name.compareTo(inf.getName()) == 0);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;

    result = prime * result + ((ipAddress == null) ? 0 : ipAddress.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());

    return result;
  }

  /**
   * Get URL for connection to database server.
   * 
   * @return the URL under which the database server is accessible
   */
  public String getURL() {
    return "//" + ipAddress + "/" + name;
  }

  @Override
  public Class<?> getServiceType() throws RemoteException {
    return DBServerInfo.class;
  }

  @Override
  public int getMaxNumberOfConcurrentJobs() throws RemoteException {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public String getServiceName() throws RemoteException {

    return "DB server";
  }

  @Override
  public InetAddress getHostAddress() throws RemoteException {
    try {
      return java.net.InetAddress.getLocalHost();
    } catch (UnknownHostException e) {
      SimSystem.report(e);
    }
    return null;
  }

  @Override
  public void callMethodByName(String method, List<String[]> params)
      throws RemoteException {
    try {
      // List of parameters for the call
      Object[] finalParameters = new Object[params.size()];
      Class<?>[] paramClasses = new Class<?>[params.size()];
      // iterate the parameters
      for (int i = 0; i < params.size(); i++) {
        // get the class of the parameter
        Class<?> paramClass = Class.forName(params.get(i)[0]);
        paramClasses[i] = paramClass;
        // get the constructor
        Constructor<?> constr = paramClass.getConstructor(String.class);
        // create the parameter object
        finalParameters[i] = constr.newInstance(params.get(i)[1]);
      }
      // method class to be called
      Method methodClass = this.getClass().getMethod(method, paramClasses);
      methodClass.invoke(this, finalParameters);
    } catch (Exception e) {
      SimSystem.report(e);
    }
  }

  @Override
  public Map<String, List<String[]>> getTriggerableMethods()
      throws RemoteException {
    return TriggerableByNameFilter.filter(this.getClass());
  }

}

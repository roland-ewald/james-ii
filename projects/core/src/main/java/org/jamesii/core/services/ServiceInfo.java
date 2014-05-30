/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.services;

import java.io.Serializable;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import org.jamesii.SimSystem;

/**
 * The Class ServiceInfo.
 */
public class ServiceInfo implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 3831195287579944697L;

  /** The type. */
  private Class<?> type;

  /** The name. */
  private String name;

  /** The description. */
  private String description;

  /** The concurrent. */
  private int concurrent;

  /** The host address. */
  private InetAddress hostAddress;

  /** the ID at the service registry */
  private int localID;

  /** Type of the last event occured on the server . */
  private String lastEvent;

  /**
   * Map of possible methods that can be invoked on the service and their
   * parameters (classes and descriptions).
   */
  private Map<String, List<String[]>> possibleCommands;

  /**
   * Instantiates a new service info.
   */
  public ServiceInfo() {

  }

  /**
   * Instantiates a new service info.
   * 
   * @param service
   *          the service
   */
  public ServiceInfo(IService service) {
    this();
    try {
      type = service.getServiceType();

      name = service.getName();
      description = service.getServiceName();
      concurrent = service.getMaxNumberOfConcurrentJobs();
      hostAddress = service.getHostAddress();
      possibleCommands = service.getTriggerableMethods();
    } catch (RemoteException e1) {
      SimSystem.report(e1);
    }
  }

  /**
   * @return the type
   */
  public final Class<?> getType() {
    return type;
  }

  /**
   * @param type
   *          the type to set
   */
  public final void setType(Class<?> type) {
    this.type = type;
  }

  /**
   * @return the name
   */
  public final String getName() {
    return name;
  }

  /**
   * @param name
   *          the name to set
   */
  public final void setName(String name) {
    this.name = name;
  }

  /**
   * @return the description
   */
  public final String getDescription() {
    return description;
  }

  /**
   * @param description
   *          the description to set
   */
  public final void setDescription(String description) {
    this.description = description;
  }

  /**
   * @return the concurrent
   */
  public final int getConcurrent() {
    return concurrent;
  }

  /**
   * @param concurrent
   *          the concurrent to set
   */
  public final void setConcurrent(int concurrent) {
    this.concurrent = concurrent;
  }

  /**
   * @return the hostAddress
   */
  public final InetAddress getHostAddress() {
    return hostAddress;
  }

  /**
   * @param hostAddress
   *          the hostAddress to set
   */
  public final void setHostAddress(InetAddress hostAddress) {
    this.hostAddress = hostAddress;
  }

  /**
   * @return the localID
   */
  public final int getLocalID() {
    return localID;
  }

  /**
   * @param localID
   *          the localID to set
   */
  public final void setLocalID(int localID) {
    this.localID = localID;
  }

  /**
   * @return the lastEvent
   */
  public final String getLastEvent() {
    return lastEvent;
  }

  /**
   * @param lastEvent
   *          the lastEvent to set
   */
  public final void setLastEvent(String lastEvent) {
    this.lastEvent = lastEvent;
  }

  /**
   * @return the possibleCommands
   */
  public final Map<String, List<String[]>> getPossibleCommands() {
    return possibleCommands;
  }

  /**
   * @param possibleCommands
   *          the possibleCommands to set
   */
  public final void setPossibleCommands(
      Map<String, List<String[]>> possibleCommands) {
    this.possibleCommands = possibleCommands;
  }

}

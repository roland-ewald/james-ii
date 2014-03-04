/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.entities;

import java.util.Set;

/**
 * Interface for a hardware setup object.
 * 
 * @author Roland Ewald
 * 
 */
public interface IHardwareSetup extends INamedDBEntity {

  /**
   * Gets the network topology.
   * 
   * @return the network topology
   */
  String getNetworkTopology();

  /**
   * Sets the network topology.
   * 
   * @param networkTopology
   *          the new network topology
   */
  void setNetworkTopology(String networkTopology);

  /**
   * Gets the network speed.
   * 
   * @return the network speed
   */
  long getNetworkSpeed();

  /**
   * Sets the network speed.
   * 
   * @param networkSpeed
   *          the new network speed
   */
  void setNetworkSpeed(long networkSpeed);

  /**
   * Gets the machines.
   * 
   * @return the machines
   */
  Set<IMachine> getMachines();

  void setMachines(Set<IMachine> machines);

}

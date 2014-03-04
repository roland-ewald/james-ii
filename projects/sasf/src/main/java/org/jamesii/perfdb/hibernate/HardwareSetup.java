/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.hibernate;


import java.util.Set;

import org.jamesii.perfdb.entities.IHardwareSetup;
import org.jamesii.perfdb.entities.IMachine;

/**
 * Hibernate implementation for hardware setups.
 * 
 * @author Roland Ewald
 * 
 */
public class HardwareSetup extends NamedDBEntity implements IHardwareSetup {

  private static final long serialVersionUID = 4925672774805427616L;

  /** Network topology that was used. */
  private String networkTopology;

  /** Network speed in KBit/s. */
  private long networkSpeed = 0;

  /** Machines that are involved in this setup. */
  private Set<IMachine> machines = null;

  /**
   * Empty constructor for beans compliance.
   */
  public HardwareSetup() {
  }

  /**
   * Instantiates a new hardware setup.
   * 
   * @param name
   *          the name
   * @param desc
   *          the description
   * @param nwTopology
   *          the network topology
   * @param nwSpeed
   *          the network speed
   * @param resources
   *          the machines used
   */
  public HardwareSetup(String name, String desc, String nwTopology,
      long nwSpeed, Set<IMachine> resources) {
    super(name, desc);
    networkTopology = nwTopology;
    networkSpeed = nwSpeed;
    machines = resources;
  }

  @Override
  public Set<IMachine> getMachines() {
    return machines;
  }

  @Override
  public long getNetworkSpeed() {
    return networkSpeed;
  }

  @Override
  public String getNetworkTopology() {
    return networkTopology;
  }

  @Override
  public void setMachines(Set<IMachine> m) {
    machines = m;
  }

  @Override
  public void setNetworkSpeed(long nwSpeed) {
    networkSpeed = nwSpeed;
  }

  @Override
  public void setNetworkTopology(String nwTopology) {
    networkTopology = nwTopology;
  }

}

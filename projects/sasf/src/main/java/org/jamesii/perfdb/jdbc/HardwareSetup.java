/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.jdbc;


import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

import org.jamesii.perfdb.entities.IHardwareSetup;
import org.jamesii.perfdb.entities.IMachine;

/**
 * Class that represents the available hardware configuration for a given
 * simulation problem.
 * 
 * 
 * @author Roland Ewald
 * 
 */
@Deprecated
public class HardwareSetup extends NamedDBEntity<HardwareSetup> implements
    IHardwareSetup {

  private static final long serialVersionUID = -8667696581962274173L;

  /** Network topology that was used. */
  private String networkTopology;

  /** Network speed in KBit/s. */
  private long networkSpeed = 0;

  /** Machines that are involved in this setup. */
  private final Set<IMachine> machines = new HashSet<>();

  @Override
  public String getNetworkTopology() {
    return networkTopology;
  }

  @Override
  public void setNetworkTopology(String networkTopology) {
    this.networkTopology = networkTopology;
  }

  @Override
  public long getNetworkSpeed() {
    return networkSpeed;
  }

  @Override
  public void setNetworkSpeed(long networkSpeed) {
    this.networkSpeed = networkSpeed;
  }

  @Override
  public Set<IMachine> getMachines() {
    return new HashSet<>(machines);
  }

  @Override
  public void setMachines(Set<IMachine> machines) {
    this.machines.clear();
    this.machines.addAll(machines);
  }

  @Override
  public long getID() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setID(long id) {
    throw new UnsupportedOperationException();
  }

  @Override
  protected String[] getColumnDataTypes() {
    throw new UnsupportedOperationException();
  }

  @Override
  protected String[] getColumnNames() {
    throw new UnsupportedOperationException();
  }

  @Override
  protected String[] getColumnValues() {
    throw new UnsupportedOperationException();
  }

  @Override
  protected HardwareSetup getCopy() {
    throw new UnsupportedOperationException();
  }

  @Override
  protected HardwareSetup getEntityByResultSet(ResultSet resultSet) {
    throw new UnsupportedOperationException();
  }

  @Override
  protected String getTableName() {
    throw new UnsupportedOperationException();
  }

}

/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb;


import java.util.List;
import java.util.Set;

import org.jamesii.perfdb.entities.IHardwareSetup;
import org.jamesii.perfdb.entities.IMachine;

/**
 * Interface for management of hardware data.
 * 
 * @author Roland Ewald
 * 
 */
public interface IHardwareDatabase {

  /**
   * Get list of all machines in the database.
   * 
   * @return list of all machines in the database
   */
  List<IMachine> getAllMachines();

  /**
   * Get list of all setups stored in the database.
   * 
   * @return list of all setups stored in the database
   */
  List<IHardwareSetup> getAllHardwareSetups();

  /**
   * Deletes given hardware setup.
   * 
   * @param setup
   *          setup to be deleted
   * @return true if deletion was successful, otherwise false
   */
  boolean deleteHardwareSetup(IHardwareSetup setup);

  /**
   * Delete a machine from the database.
   * 
   * @param machine
   *          the machine to be deleted
   * @return true id deletion was successful, false if machine could not be
   *         found or is used by an existing setup
   */
  boolean deleteMachine(IMachine machine);

  /**
   * Creates new hardware setup if such a setup does not exists, otherwise
   * retrieves existing setup from database.
   * 
   * @param name
   *          the name of the setup
   * @param desc
   *          the description of the setup
   * @param nwTopology
   *          the network topology
   * @param nwSpeed
   *          the network speed
   * @param machines
   *          set of machines
   * @return new hardware setup
   */
  IHardwareSetup newHardwareSetup(String name, String desc, String nwTopology,
      long nwSpeed, Set<IMachine> machines);

  /**
   * Creates new machine.
   * 
   * @param name
   *          name of the machine
   * @param desc
   *          description of the machine
   * @param macAddress
   *          MAC address of the machine (Base64 Encoded, using {@link org.jamesii.core.util.misc.Strings})
   * @param sciMark
   *          sciMark score of the machine
   * @return newly created machine
   */
  IMachine newMachine(String name, String desc, String macAddress,
      double sciMark);

  /**
   * Get all machines by MAC address. Several machine entries are possible,
   * since old performance measurements need to be distinguished from new ones
   * when a computer had been upgraded.
   * 
   * @param macAddress
   *          MAC address that is looked for (Base64 Encoded, using
   *          {@link org.jamesii.core.util.misc.Strings})
   * @return list of machines with that MAC address
   */
  List<IMachine> getAllMachines(String macAddress);

  /**
   * Counts the number of setups this machine has bee used in.
   * 
   * @param machine
   *          the machine for which the setups shall be counted
   * @return number of setups
   */
  int getSetupCount(IMachine machine);
}

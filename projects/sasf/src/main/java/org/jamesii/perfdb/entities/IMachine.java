/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.entities;

/**
 * Interface for representing a specific machine. For example, a PC, a notebook,
 * or a node in a cluster.
 * 
 * @author Roland Ewald
 * 
 */
public interface IMachine extends INamedDBEntity {

  /**
   * Get MAC address of the machine (Base64 Encoded, using
   * {@link org.jamesii.core.util.misc.Strings}).
   * 
   * @return MAC address of the machine
   */
  String getMacAddress();

  /**
   * Sets the MAC address of the machine.
   * 
   * @param macAddress
   *          the new mac address
   */
  void setMacAddress(String macAddress);

  /**
   * Gets the java SciMark score.
   * 
   * @return the java SciMark score
   */
  double getJavaSciMark();

  /**
   * Sets the java SciMark score.
   * 
   * @param javaSciMark
   *          the new java SciMark score
   */
  void setJavaSciMark(double javaSciMark);

}

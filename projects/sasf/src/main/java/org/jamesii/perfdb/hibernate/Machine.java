/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.hibernate;

import org.jamesii.perfdb.entities.IMachine;

/**
 * Hibernate implementation of a machine.
 * 
 * @author Roland Ewald
 * 
 */
public class Machine extends NamedDBEntity implements IMachine {

  private static final long serialVersionUID = -2383807366683761845L;

  /** MAC address of the machine. */
  private String macAddress;

  /**
   * Java SciMark benchmark score of the machine
   * (http://math.nist.gov/scimark2/).
   */
  private double javaSciMark;

  /**
   * Empty constructor for beans compliance.
   */
  public Machine() {
  }

  /**
   * Instantiates a new machine entity.
   * 
   * @param name
   *          the name
   * @param desc
   *          the description
   * @param macAddr
   *          the MAC address
   * @param sciMark
   *          the java sci-mark value
   */
  public Machine(String name, String desc, String macAddr, double sciMark) {
    super(name, desc);
    macAddress = macAddr;
    javaSciMark = sciMark;
  }

  @Override
  public double getJavaSciMark() {
    return javaSciMark;
  }

  @Override
  public String getMacAddress() {
    return macAddress;
  }

  @Override
  public void setJavaSciMark(double sciMark) {
    javaSciMark = sciMark;
  }

  @Override
  public void setMacAddress(String mAddress) {
    this.macAddress = mAddress;
  }

}

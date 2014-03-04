/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.jdbc;

import java.util.Arrays;

/**
 * A single machine in the hardware setup.
 * 
 * @author Roland Ewald
 * 
 */
@Deprecated
public class Machine {

  /** Name of the machine. */
  private String name;

  /** MAC address of the machine. */
  private byte[] macAddress;

  /** Java SciMark benchmark score of the machine. */
  private double javaSciMark;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public byte[] getMacAddress() {
    return Arrays.copyOf(macAddress, macAddress.length);
  }

  public void setMacAddress(byte[] macAddress) {
    this.macAddress = Arrays.copyOf(macAddress, macAddress.length);
  }

  public double getJavaSciMark() {
    return javaSciMark;
  }

  public void setJavaSciMark(double javaSciMark) {
    this.javaSciMark = javaSciMark;
  }

}

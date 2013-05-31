/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.runtime;

import java.io.Serializable;

import org.jamesii.core.util.info.JavaInfo;

/**
 * The Class SystemInformation summarizes various information about the
 * simulation system, the platform it is executed on, ....
 * 
 * @author Jan Himmelspach
 */
public class SystemInformation implements Serializable {

  /**
   * The constant serial version uid.
   */
  private static final long serialVersionUID = -352893646355044514L;

  /** The org.jamesii version. */
  private String jamesVersion;

  /** The java info. */
  private JavaInfo javaInfo;

  /** The machine id. */
  private long machineId;

  /**
   * Instantiates a new system information.
   * 
   * @param machineId
   *          the machine id
   * @param jamesVersion
   *          the org.jamesii version
   * @param javaInfo
   *          the java info
   */
  public SystemInformation(long machineId, String jamesVersion,
      JavaInfo javaInfo) {
    this.machineId = machineId;
    this.jamesVersion = jamesVersion;
    this.javaInfo = javaInfo;
  }

  /**
   * Gets the org.jamesii version.
   * 
   * @return the org.jamesii version
   */
  public String getJamesVersion() {
    return jamesVersion;
  }

  /**
   * Gets the java info.
   * 
   * @return the java info
   */
  public JavaInfo getJavaInfo() {
    return javaInfo;
  }

  /**
   * Gets the machine id.
   * 
   * @return the machine id
   */
  public long getMachineId() {
    return machineId;
  }

}

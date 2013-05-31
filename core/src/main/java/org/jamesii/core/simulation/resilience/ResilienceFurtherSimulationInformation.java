/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.simulation.resilience;

import java.io.Serializable;

/**
 * Container of data, which collects further information for
 * ResilienceSimulationInformation..
 * 
 * @author Thomas Noesinger
 * 
 */
public class ResilienceFurtherSimulationInformation implements Serializable {

  /**
   * Serialisation ID.
   */
  private static final long serialVersionUID = -2801314627854486253L;

  /**
   * The modelname of the child or parent.
   */
  private String modelname = null;

  /**
   * The modelname of the partition.
   */
  private String modelnameother = null;

  /**
   * The hostname of the child or parent.
   */
  private String hostname = null;

  /**
   * The hostname of the partition.
   */
  private String hostnameother = null;

  /**
   * The constructor of the class.
   * 
   * @param modelMameOwn
   *          : The modelname of the child or parent.
   * @param conModelNameOther
   *          : The modelname of the partition.
   * @param hostnameOwn
   *          : The hostname of the child or parent.
   * @param conhostnameOther
   *          : The hostname of the partition.
   */
  public ResilienceFurtherSimulationInformation(String modelMameOwn,
      String conModelNameOther, String hostnameOwn, String conhostnameOther) {
    modelname = modelMameOwn;
    modelnameother = conModelNameOther;
    hostname = hostnameOwn;
    hostnameother = conhostnameOther;
  }

  /**
   * Set the modelname of the child or parent.
   * 
   * @param value
   *          modelname of the child or parent.
   */
  public void setModelName(String value) {
    modelname = value;
  }

  /**
   * Get the modelname of the child or parent.
   * 
   * @return the modelname of the child or parent.
   */
  public String getModelName() {
    return modelname;
  }

  /**
   * @param value
   *          : The modelname of the partition.
   */
  public void setModelNameOfOther(String value) {
    modelnameother = value;
  }

  /**
   * @return The modelname of the partition.
   */
  public String getModelNameOfOther() {
    return modelnameother;
  }

  /**
   * @param value
   *          : The hostname of the child or parent.
   */
  public void setHostName(String value) {
    hostname = value;
  }

  /**
   * @return The hostname of the child or parent.
   */
  public String getHostName() {
    return hostname;
  }

  /**
   * @param value
   *          : The hostname of the partition.
   */
  public void setHostNameOfOther(String value) {
    hostnameother = value;
  }

  /**
   * @return The hostname of the partition.
   */
  public String getHostNameOfOther() {
    return hostnameother;
  }
}

/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.hosts.database;

import java.io.Serializable;

/**
 * The query class is a "query packet" which allows to define simulation data
 * queries. By using this type the data used is lower as if a string would be
 * used.
 * 
 * @author Sebastian Lieske
 * @author Jan Himmelspach
 */
public class SimulationResultDataQuery implements Serializable {

  /** The serialisation ID. */
  private static final long serialVersionUID = -4522965394017955033L;

  /** The experiment ID. */
  private long expid;

  /** The simulation ID. */
  private long simid;

  /** The data ID. */
  private long dataid;

  /** The attribute. */
  private String attrib;

  /** -1 for latest, 0 for all. */
  private double time;

  /**
   * Instantiates a new simulation result data query.
   */
  public SimulationResultDataQuery() {
    super();
  }

  /**
   * Instantiates a new simulation result data query.
   * 
   * @param expId
   *          the exp id
   * @param simId
   *          the sim id
   * @param dataId
   *          the data id
   * @param attribute
   *          the attribute
   * @param t
   *          the t
   */
  public SimulationResultDataQuery(long expId, long simId, long dataId,
      String attribute, double t) {
    super();
    this.expid = expId;
    this.simid = simId;
    this.dataid = dataId;
    this.attrib = attribute;
    this.time = t;
  }

  /**
   * Gets the attribute.
   * 
   * @return the attrib
   */
  public String getAttrib() {
    return attrib;
  }

  /**
   * Gets the data ID.
   * 
   * @return the data ID
   */
  public long getDataid() {
    return dataid;
  }

  /**
   * Gets the experiment ID.
   * 
   * @return the experiment ID
   */
  public long getExpid() {
    return expid;
  }

  /**
   * Gets the simulationID.
   * 
   * @return the simulation ID
   */
  public long getSimid() {
    return simid;
  }

  /**
   * Gets the time.
   * 
   * @return the time
   */
  public double getTime() {
    return time;
  }

  /**
   * Sets the attribute.
   * 
   * @param attrib
   *          the new attribute
   */
  public void setAttrib(String attrib) {
    this.attrib = attrib;
  }

  /**
   * Sets the data ID.
   * 
   * @param dataid
   *          the new data ID
   */
  public void setDataid(long dataid) {
    this.dataid = dataid;
  }

  /**
   * Sets the experiment id.
   * 
   * @param expid
   *          the new experiment ID
   */
  public void setExpid(long expid) {
    this.expid = expid;
  }

  /**
   * Sets the simulation ID.
   * 
   * @param simid
   *          the new simulation ID.
   */
  public void setSimid(long simid) {
    this.simid = simid;
  }

  /**
   * Sets the time.
   * 
   * @param time
   *          the new time
   */
  public void setTime(double time) {
    this.time = time;
  }

  /**
   * Sets the parameters of the query (all at once).
   * 
   * @param expId
   *          the exp id
   * @param simId
   *          the sim id
   * @param dataId
   *          the data id
   * @param attribute
   *          the attribute
   * @param t
   *          the t
   */
  public void setParameters(long expId, long simId, long dataId,
      String attribute, double t) {
    this.expid = expId;
    this.simid = simId;
    this.dataid = dataId;
    this.attrib = attribute;
    this.time = t;
  }

  @Override
  public String toString() {
    String sRet =
        "Q=(" + expid + "," + simid + "," + dataid + "," + attrib + "," + time
            + ")";
    return sRet;
  }

}

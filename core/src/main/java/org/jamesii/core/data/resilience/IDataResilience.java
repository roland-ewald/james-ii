/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.resilience;

import java.util.List;

/**
 * General data storage interface for the resilience.
 * 
 * @author Thomas Noesinger
 */
public interface IDataResilience {

  /** The key of database url. */
  String DATABASEURL = "url";

  /** The key of driver. */
  String DRIVER = "driver";

  /** The key of password. */
  String PASSWORD = "pass";

  /** The key of user. */
  String USER = "user";

  /**
   * Gets the checkpoint.
   * 
   * @param <D>
   *          the type of the data
   * 
   * @param server
   *          the server
   * @param dataid
   *          the ID of the data
   * @param time
   *          the time when the data has been taken
   * 
   * @return the checkpoint
   */
  <D> List<D> getCheckpoint(String server, long dataid, double time);

  /**
   * Gets the last checkpoint.
   * 
   * @param <D>
   *          the type of the data
   * 
   * @param server
   *          the server
   * @param dataid
   *          the ID of the data
   * 
   * @return the last checkpoint
   */
  <D> List<D> getLastCheckpoint(String server, long dataid);

  /**
   * Write the data .
   * 
   * @param <D>
   *          the type of the data
   * 
   * @param dataid
   *          the ID of the data
   * @param time
   *          the time when the data has been taken
   * @param data
   *          the data
   * 
   * @return true, if successful
   */
  <D> boolean writeData(long dataid, double time, D data);

  /**
   * Sets the status of checkpoint.
   * 
   * @param server
   *          the name of the server
   * @param dataid
   *          the ID of the data
   * @param time
   *          the time when the data has been taken
   * 
   * @return true, if successful
   */
  boolean setStatusOfCheckpoint(String server, long dataid, double time);

}

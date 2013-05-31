/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.runtime;

import java.sql.Connection;

/**
 * Provides different methods for the handling with source connections
 * (database, file etc.).
 * 
 * @author Thomas Noesinger
 */
public class ConnectionHandler {

  /**
   * Gets the connection.
   * 
   * @param params
   *          the params
   * 
   * @return the connection
   */
  public Connection getConnection(Object[] params) {
    return null;
  }

  /**
   * Checks for connection.
   * 
   * @param params
   *          the params
   * 
   * @return true, if successful
   */
  public boolean hasConnection(Object[] params) {
    return false;
  }

}

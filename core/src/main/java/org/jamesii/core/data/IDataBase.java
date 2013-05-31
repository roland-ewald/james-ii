/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data;

import java.sql.SQLException;

/**
 * General data storage base creation and final data storage interface. All data
 * storage types must implement this interface.
 * 
 * @author Thomas Noesinger
 * @author Roland Ewald
 * 
 */
public interface IDataBase {

  /**
   * Close a previously created database (connection).
   * 
   * @throws Exception
   *           if base could not be closed
   */
  void closeBase() throws SQLException;

  /**
   * Opens a database, ie. the connection will be established and can then be
   * used.
   * 
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  void openBase() throws ClassNotFoundException, SQLException;

  /**
   * The method should build the necessary base for the storage (create a file
   * etc.).
   * 
   * @param params
   *          parameters
   * @return true if successful
   * @throws Exception
   *           if problems were encountered
   */
  boolean createBase(Object[] params);

}

/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Interface for SQL data bases.
 * 
 * @author Roland Ewald
 * 
 */
public interface ISQLDataBase extends IDataBase {

  /**
   * Get database connection.
   * 
   * @return database connection
   * 
   * @throws Exception
   *           the exception
   */
  Connection getConnection() throws ClassNotFoundException, SQLException;

  /**
   * (Re-)Initializes database connection.
   * 
   * @param url
   *          URL to database server
   * @param user
   *          database user
   * @param pwd
   *          database user's password
   * @param driver
   *          JDBC driver
   * @param keepAliveTime
   *          the time the connection should be kept alive (null, if this should
   *          not be done)
   * @param defaultStatement
   *          the statement used by the keep-alive thread to query the database,
   *          can be kept empty if keepAliveTime is null, otherwise e.g.
   *          "Select 1"
   * @return the initialized database connection
   * @throws ClassNotFoundException
   *           in case the database driver cannot be found
   * @throws SQLException
   *           in case database access fails
   */
  Connection initConnection(String url, String user, String pwd, String driver,
      Long keepAliveTime, String defaultStatement)
      throws ClassNotFoundException, SQLException;

  /**
   * (Re-)Initializes database connection.
   * 
   * @param url
   *          URL to database server
   * @param user
   *          database user
   * @param pwd
   *          database user's password
   * @param driver
   *          JDBC driver
   * @return the initialized database connection
   * @throws ClassNotFoundException
   *           in case the database driver cannot be found
   * @throws SQLException
   *           in case database access fails
   */
  Connection initConnection(String url, String user, String pwd, String driver)
      throws ClassNotFoundException, SQLException;

}

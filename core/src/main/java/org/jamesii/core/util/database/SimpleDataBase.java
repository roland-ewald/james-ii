/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.data.DBConnectionData;
import org.jamesii.core.data.ISQLDataBase;

/**
 * Utility class for data-base classes.
 * 
 * @author Roland Ewald
 */
public abstract class SimpleDataBase implements ISQLDataBase {

  /** Connection to a database. */
  private Connection connection = null;

  /** Data to get connection. */
  private DBConnectionData connectionData = null;

  /** List of dummy database entity objects. */
  private List<SimpleDataBaseEntity<?>> entities = new ArrayList<>();

  /**
   * Thread which checks the connection after a given interval recreates it if
   * necessary.
   */
  private KeepConnectionAliveThread thread;

  /**
   * Empty constructor.
   */
  public SimpleDataBase() {
  }

  /**
   * Default constructor. Opens database connection. See
   * {@link SimpleDataBase#initConnection(String, String, String, String)}.
   * 
   * @param dbcd
   *          database connection data
   * 
   * @throws SQLException
   *           if connection could not be established
   * @throws ClassNotFoundException
   *           if driver could not be loaded
   */
  public SimpleDataBase(DBConnectionData dbcd) throws SQLException,
      ClassNotFoundException {
    connectionData = dbcd;
    openBase();
  }

  @Override
  public void closeBase() throws SQLException {
    if (thread != null) {
      thread.stopThread();
    }
    if (connection != null) {
      connection.close();
    }
  }

  @Override
  public void openBase() throws ClassNotFoundException, SQLException {
    connection = connectionData.createNewConnection();
  }

  @Override
  public Connection getConnection() throws ClassNotFoundException, SQLException {
    if (connection == null) {
      openBase();
    }
    return connection;
  }

  @Override
  public Connection initConnection(String url, String user, String pwd,
      String driver, Long keepAliveTime, String defaultStatement)
      throws ClassNotFoundException, SQLException {
    if (connection != null) {
      connection.close();
    }

    connectionData = new DBConnectionData(url, user, pwd, driver);
    openBase();
    if (keepAliveTime != null) {
      thread =
          new KeepConnectionAliveThread(keepAliveTime, this, defaultStatement);
      thread.start();
    }
    return getConnection();
  }

  @Override
  public Connection initConnection(String url, String user, String pwd,
      String driver) throws ClassNotFoundException, SQLException {
    return initConnection(url, user, pwd, driver, null, "");
  }

  /**
   * Initialises all database entities in the list.
   * 
   * @throws SQLException
   *           if table creation or connection failed
   */
  protected void initializeDBEntities() throws SQLException {
    for (SimpleDataBaseEntity<?> entity : entities) {
      entity.connect(connection);
    }
  }

  /**
   * Gets the connection data.
   * 
   * @return the connection data
   */
  public DBConnectionData getConnectionData() {
    return connectionData;
  }

  /**
   * Sets the connection data and initializes a new connection.
   * 
   * @param connectionData
   *          the new db connection data
   */
  public void setDbConnectionData(DBConnectionData connectionData) {
    this.connectionData = connectionData;
  }

  /**
   * Adds the entity.
   * 
   * @param entity
   *          the entity
   */
  public void addEntity(SimpleDataBaseEntity<?> entity) {
    entities.add(entity);
  }
}

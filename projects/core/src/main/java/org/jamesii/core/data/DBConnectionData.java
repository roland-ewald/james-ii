/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Class that represents the data needed for a database connection (via JDBC).
 * 
 * TODO(general/security): If we want this to be secure, we will have to encrypt
 * PWD and user name in the RAM. The current implementation is NOT secure.
 * 
 * @author Roland Ewald
 */
public class DBConnectionData implements Serializable {

  /** Serialisation ID. */
  private static final long serialVersionUID = -6344406734519133711L;

  /** The URL of the database. */
  private String url = "";

  /** The user name of the database. */
  private String user = "";

  /** Password of the user. Currently not encrypted! */
  private String password = "";

  /** The JDBC driver to be used. */
  private String driver = "";

  /**
   * Default constructor.
   * 
   * @param dbURL
   *          connection URL
   * @param usr
   *          user
   * @param pwd
   *          password
   * @param drv
   *          FQCN of the JDBC driver
   */
  public DBConnectionData(String dbURL, String usr, String pwd, String drv) {
    url = dbURL;
    user = usr;
    password = pwd;
    driver = drv;
  }

  /**
   * Instantiates a new dB connection data.
   */
  public DBConnectionData() {
  }

  /**
   * Creates database connection.
   * 
   * @return newly established connection to database
   * 
   * @throws SQLException
   *           if connection could not be established
   * @throws ClassNotFoundException
   *           if driver could not be loaded
   */
  public Connection createNewConnection() throws SQLException,
      ClassNotFoundException {
    Class.forName(driver);
    return DriverManager.getConnection(url, user, password);
  }

  /**
   * Gets the url of the database.
   * 
   * @return the url
   */
  public String getURL() {
    return url;
  }

  /**
   * Sets the url of the database.
   * 
   * @param url
   *          the new url
   */
  public void setURL(String url) {
    this.url = url;
  }

  /**
   * Gets the user name.
   * 
   * @return the user
   */
  public String getUser() {
    return user;
  }

  /**
   * Sets the user name.
   * 
   * @param user
   *          the new user
   */
  public void setUser(String user) {
    this.user = user;
  }

  /**
   * Gets the password of the user.
   * 
   * NOTE: the returned pwd is not encrypted.
   * 
   * @return the password
   */
  public String getPassword() {
    return password;
  }

  /**
   * Sets the password of the user.
   * 
   * @param password
   *          the new password
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Gets the driver.
   * 
   * @return the driver
   */
  public String getDriver() {
    return driver;
  }

  /**
   * Sets the driver.
   * 
   * @param driver
   *          the new driver
   */
  public void setDriver(String driver) {
    this.driver = driver;
  }

}

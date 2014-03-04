/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.util;

import org.jamesii.core.data.DBConnectionData;

/**
 * Represents all connection information as required by Hibernate.
 * 
 * @author Roland Ewald
 */
public class HibernateConnectionData extends DBConnectionData {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -3596537889614440832L;

  /** The dialect to be used. */
  private String dialect = "";

  /**
   * Instantiates new hibernate connection data.
   */
  public HibernateConnectionData() {
  }

  /**
   * Instantiates new hibernate connection data.
   * 
   * @param location
   *          the location
   * @param user
   *          the user
   * @param password
   *          the password
   * @param driver
   *          the driver
   * @param dialect
   *          the dialect
   */
  public HibernateConnectionData(String location, String user, String password,
      String driver, String dialect) {
    super(location, user, password, driver);
    this.dialect = dialect;
  }

  /**
   * Gets the dialect.
   * 
   * @return the dialect
   */
  public String getDialect() {
    return dialect;
  }

  /**
   * Sets the dialect.
   * 
   * @param dialect
   *          the new dialect
   */
  public void setDialect(String dialect) {
    this.dialect = dialect;
  }

}

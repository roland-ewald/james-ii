/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.util;

import org.jamesii.core.data.DBConnectionData;
import org.jamesii.perfdb.util.HibernateConnectionData;


/**
 * Class to encapsulate some standard database configurations.
 * 
 * @author Roland Ewald
 * 
 */
public final class DBConfiguration {

  /**
   * Should not be instantiated.
   */
  private DBConfiguration() {
  }

  /**
   * Suffix for the test schema name (ie, the database to be used for testing).
   * This is databasse-independent.
   */
  static final String TEST_SCHEMA_SUFFIX = "_test";

  /** The command line property to set the performance database type. */
  static final String PERFDB_TYPE_PROPERTY = "perfDBType";

  /** The performance type MySQL. */
  static final String PERFDB_TYPE_MYSQL = "mysql";

  /** The performance type for HSQL. */
  static final String PERFDB_TYPE_HSQL = "hsql";

  /** The default type of the performance database. */
  static final String PERFDB_TYPE_DEFAULT = PERFDB_TYPE_HSQL;

  /** The default prefix for MySQL locations. */
  public static final String MYSQL_DEFAULT_LOCATION_PREFIX = "jdbc:mysql://localhost/";

  /** Location of MySQL database. */
  public static final String MYSQL_DEFAULT_LOCATION = MYSQL_DEFAULT_LOCATION_PREFIX
      + "j2_perf_sel_db";

  /** User name for MySQL database. */
  public static final String MYSQL_DEFAULT_USER = "root";

  /** Password for MySQL database. */
  public static final String MYSQL_DEFAULT_PWD = "root";

  /** JDBC driver for MySQL. */
  public static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";

  /** The dialect to be used. */
  public static final String MYSQL_DIALECT = "org.hibernate.dialect.MySQL5Dialect";

  /** The default prefix for HSQL locations. */
  public static final String HSQL_DEFAULT_LOCATION_PREFIX = "jdbc:hsqldb:file:";

  /** Location of HSQL database. */
  public static final String HSQL_DEFAULT_LOCATION = HSQL_DEFAULT_LOCATION_PREFIX
      + "j2_perf_sel_db";

  /** User name for HSQL database. */
  public static final String HSQL_DEFAULT_USER = "sa";

  /** Password for HSQL database. */
  public static final String HSQL_DEFAULT_PWD = "";

  /** Driver for HSQL database. */
  public static final String HSQL_DRIVER = "org.hsqldb.jdbcDriver";

  /** Dialect for HSQL database. */
  public static final String HSQL_DIALECT = "org.hibernate.dialect.HSQLDialect";

  /**
   * Get connection data for tests with MySQL.
   * 
   * @return connection data for tests
   */
  public static DBConnectionData getTestConnectionDataMySQL() {
    return new HibernateConnectionData(MYSQL_DEFAULT_LOCATION
        + TEST_SCHEMA_SUFFIX, MYSQL_DEFAULT_USER, MYSQL_DEFAULT_PWD,
        MYSQL_DRIVER, MYSQL_DIALECT);
  }

  /**
   * Get connection data for tests with HSQL.
   * 
   * @return connection data for tests
   */
  public static DBConnectionData getTestConnectionDataHSQL() {
    return new HibernateConnectionData(HSQL_DEFAULT_LOCATION
        + TEST_SCHEMA_SUFFIX, HSQL_DEFAULT_USER, HSQL_DEFAULT_PWD, HSQL_DRIVER,
        HSQL_DIALECT);
  }

  /**
   * Gets the test connection data to be used.
   * 
   * @return the test connection data
   */
  public static DBConnectionData getTestConnectionData() {
    return checkPerfDBPropertyForMySQL() ? getTestConnectionDataMySQL()
        : getTestConnectionDataHSQL();
  }

  /**
   * Checks the state of the performance database type property.
   * 
   * @return true, if type is set to {@link DBConfiguration#PERFDB_TYPE_MYSQL}
   */
  public static boolean checkPerfDBPropertyForMySQL() {
    return System.getProperty(PERFDB_TYPE_PROPERTY, PERFDB_TYPE_DEFAULT)
        .toLowerCase().trim().equals(PERFDB_TYPE_MYSQL);
  }

  public static DBConnectionData getConnectionData(String schemaName) {
    return checkPerfDBPropertyForMySQL() ? getConnectionDataMySQL(schemaName)
        : getConnectionDataHSQL(schemaName);
  }

  /**
   * Gets the connection data for MySQL.
   * 
   * @param schemaName
   *          the schema name
   * @return the connection data
   */
  private static DBConnectionData getConnectionDataMySQL(String schemaName) {
    return new HibernateConnectionData(MYSQL_DEFAULT_LOCATION_PREFIX
        + schemaName, MYSQL_DEFAULT_USER, MYSQL_DEFAULT_PWD, MYSQL_DRIVER,
        MYSQL_DIALECT);
  }

  /**
   * Gets the connection data for HSQL database.
   * 
   * @param schemaName
   *          the schema name
   * @return the connection data
   */
  private static DBConnectionData getConnectionDataHSQL(String schemaName) {
    return new HibernateConnectionData(HSQL_DEFAULT_LOCATION_PREFIX
        + schemaName, HSQL_DEFAULT_USER, HSQL_DEFAULT_PWD, HSQL_DRIVER,
        HSQL_DIALECT);
  }
}

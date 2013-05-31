/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.data.DBConnectionData;

/**
 * This class shall help to test data storages that rely on a MySQL database.
 * Before each test case, an empty schema is provided and will be deleted after
 * a test function is done (i.e., it is assumed that there is no cross-talk
 * between different test methods - if this is the case, call the test methods
 * in question within a new test method).
 * 
 * Additionally, the class considers Java properties for to set the connection
 * details. Use the properties to configure all test cases based on this class
 * to your system. For example, like this:
 * 
 * <pre>
 * -Djames.core.data.MySQLDataStorageTest.password=42
 * </pre>
 * 
 * Note that these are VM arguments (in case you use Eclipse).
 * 
 * @author Roland Ewald
 */
public abstract class MySQLDataStorageTest extends DataStorageTest {

  /** The MySQL driver that is used. */
  public static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";

  /**
   * The prefix used for looking up properties. There might be tests using
   * *other* databases and also defining user, host, etc. - so we are specific
   * here.
   */
  public static final String PROPERTY_PREFIX = MySQLDataStorageTest.class
      .getName() + '.';

  /** The name of the property to set the schema. */
  public static final String SCHEMA_PROPERTY = "schema";

  /** The name of the property to set the host. */
  public static final String HOST_PROPERTY = "host";

  /** The name of the property to set the user. */
  public static final String USER_PROPERTY = "user";

  /** The name of the property to set the password. */
  public static final String PASSWORD_PROPERTY = "password";

  /** The default host name. */
  public static final String DEFAULT_DB_HOST = "localhost";

  /** The default schema prefix (a randomly generated suffix will be added). */
  public static final String DEFAULT_SCHEMA_PREFIX = "test_";

  /** The default user name. */
  public static final String DEFAULT_USER = "root";

  /** The schema to be created/deleted. */
  private final String schema;

  /** The connection data to be used. */
  private final DBConnectionData connectionData;

  /** The host name to be used. */
  private final String host;

  /** The user name to be used. */
  private final String user;

  /** The password to be used. */
  private final String password;

  /**
   * Instantiates a new MySQL data storage test.
   */
  public MySQLDataStorageTest() {
    schema =
        getProperty(SCHEMA_PROPERTY, DEFAULT_SCHEMA_PREFIX
            + UUID.randomUUID().toString().replace('-', '_'));
    host = getProperty(HOST_PROPERTY, DEFAULT_DB_HOST);
    user = getProperty(USER_PROPERTY, DEFAULT_USER);
    password = getProperty(PASSWORD_PROPERTY, "");
    connectionData =
        new DBConnectionData("jdbc:mysql://" + host, user, password,
            MYSQL_DRIVER);
  }

  /**
   * Gets the property.
   * 
   * @param suffix
   *          the suffix (name of the actual property)
   * @param defaultValue
   *          the default value
   * @return the property value
   */
  public String getProperty(String suffix, String defaultValue) {
    String propertyName = PROPERTY_PREFIX + suffix;
    SimSystem.report(Level.INFO, "Checking property '" + propertyName + "':"
        + System.getProperty(propertyName));
    return System.getProperty(propertyName, defaultValue);
  }

  @Override
  public void setUp() throws Exception {
    createDBSchema();
    super.setUp();
  }

  @Override
  public void tearDown() throws Exception {
    super.tearDown();
    deleteDBSchema();
  }

  /**
   * Creates database schema.
   * 
   * @throws SQLException
   *           the SQL exception
   * @throws ClassNotFoundException
   *           the class not found exception
   */
  private void createDBSchema() throws SQLException, ClassNotFoundException {
    SimSystem.report(Level.INFO, "Creating database schema '" + schema + "'");
    executeSQL("CREATE DATABASE IF NOT EXISTS " + schema + ';');
  }

  /**
   * Deletes database schema.
   * 
   * @throws SQLException
   *           the SQL exception
   * @throws ClassNotFoundException
   *           the class not found exception
   */
  private void deleteDBSchema() throws SQLException, ClassNotFoundException {
    SimSystem.report(Level.INFO, "Dropping database schema '" + schema + "'");
    executeSQL("DROP DATABASE IF EXISTS " + schema + ';');
  }

  /**
   * Executes a piece of SQL.
   * 
   * @param sql
   *          the sql
   * @throws SQLException
   *           the SQL exception
   * @throws ClassNotFoundException
   *           the class not found exception
   */
  public void executeSQL(String sql) throws SQLException,
      ClassNotFoundException {
    Connection connection = null;
    Statement statement = null;
    try {
      connection = connectionData.createNewConnection();
      statement = connection.createStatement();
      statement.execute(sql);
    } catch (SQLException ex) {
      ex.printStackTrace();
    } finally {
      if (statement != null) {
        try {
          statement.close();
        } catch (SQLException e) {
          SimSystem.report(Level.WARNING, "Could not close statement.", e);
        }
      }
      if (connection != null) {
        try {
          connection.close();
        } catch (Exception e) {
          SimSystem.report(Level.WARNING, "Could not close connection.", e);
        }
      }
    }
  }

  /**
   * Gets the password.
   * 
   * @return the password
   */
  protected String getPassword() {
    return connectionData.getPassword();
  }

  /**
   * Gets the user.
   * 
   * @return the user
   */
  protected String getUser() {
    return connectionData.getUser();
  }

  /**
   * Gets the schema.
   * 
   * @return the schema
   */
  protected String getSchema() {
    return schema;
  }

  /**
   * Gets the host.
   * 
   * @return the host
   */
  protected String getHost() {
    return host;
  }

}

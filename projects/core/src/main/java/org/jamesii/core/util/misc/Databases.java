/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.misc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.jamesii.SimSystem;

/**
 * Database helper functions.
 * 
 * @author Roland Ewald
 */
public final class Databases {

  /** Flag to turn debugging on or off. */
  public static final boolean DEBUG_SQL = false;

  /**
   * Defines the policy for insertions.
   */
  public static enum DatabaseInsertPolicy {

    /**
     * Normal insertion. User has to make sure that inserted primary keys do not
     * already exist. Otherwise, an exception will be thrown.
     */
    NORMAL,

    /**
     * Replacing insertion: rows with the same primary key will be replaced with
     * the new data.
     */
    REPLACE,

    /** Data with the same primary key will not be inserted. */
    IGNORE;

    @Override
    public String toString() {
      switch (this) {
      case REPLACE:
        return "REPLACE";
      case IGNORE:
        return "INSERT IGNORE";
      }
      return "INSERT";
    }
  }

  /**
   * The Enum DatabaseEntityType.
   */
  public static enum DatabaseEntityType {

    /** A table. */
    TABLE,

    /** A view. */
    VIEW,

    /** A stored procedure. */
    STORED_PROCEDURE,

    /** A trigger. */
    TRIGGER;
  }

  /**
   * Hidden constructor.
   */
  private Databases() {
  }

  /**
   * Creates database table.
   * 
   * @param connection
   *          connection to the database
   * @param name
   *          name of the table
   * @param columns
   *          column definitions of the table, array of two-element arrays
   *          containing name (position 0) and data type + modifiers (position
   *          1) of the column
   * @param additionalSQL
   *          the additional sql
   * 
   * @throws SQLException
   *           if table creation fails
   */
  public static void createTable(Connection connection, String name,
      String[][] columns, String additionalSQL) throws SQLException {
    String sql = createTableSQL(name, columns, additionalSQL);
    executeSQL(connection, sql);
  }

  /**
   * Creates a view.
   * 
   * @param connection
   *          the connection to the database
   * @param name
   *          the name of the view
   * @param selectSQL
   *          the SELECT statement that defines the view
   * 
   * @throws SQLException
   *           the SQL exception
   */
  public static void createView(Connection connection, String name,
      String selectSQL) throws SQLException {
    if (tableExists(connection, name)) {
      return;
    }
    String sql = createViewSQL(name, selectSQL);
    executeSQL(connection, sql);
  }

  /**
   * Execute the sql command. Closes the statement in any case.
   * 
   * @param connection
   * @param sql
   * @throws SQLException
   */
  private static final void executeSQL(Connection connection, String sql)
      throws SQLException {
    try (Statement statement = connection.createStatement()) {
      if (DEBUG_SQL) {
        SimSystem.report(Level.FINEST, sql);
      }
      statement.executeUpdate(sql);
    }
  }

  /**
   * Stored procedures will be dropped and re-created (to facilitate DB
   * management).
   * 
   * @param connection
   *          the connection to the DB
   * @param name
   *          the stored procedure's name
   * @param definition
   *          the definition of the stored procedure
   * 
   * @throws SQLException
   *           the SQL exception
   */
  public static void createStoredProcedure(Connection connection, String name,
      String definition) throws SQLException {
    try (Statement st = connection.createStatement()) {
      st.addBatch("DROP PROCEDURE IF EXISTS " + name + ";");
      st.addBatch(definition);
      st.executeBatch();
    }
  }

  /**
   * Creates the SQL syntax for a table creation.
   * 
   * @param name
   *          name of the table.
   * @param columns
   *          column definitions, see
   *          {@link #createTable(Connection, String, String[][], String)}
   * @param additionalSQL
   *          additional SQL, e.g., to define foreign keys
   * 
   * @return SQL string to create a table as defined
   */
  public static String createTableSQL(String name, String[][] columns,
      String additionalSQL) {
    StringBuffer sql =
        new StringBuffer("CREATE TABLE IF NOT EXISTS " + name + " (");
    for (int i = 0; i < columns.length; i++) {
      sql.append(columns[i][0] + " " + columns[i][1]);
      if (i < columns.length - 1) {
        sql.append(",");
      }
    }
    sql.append(additionalSQL + ")");
    return sql.toString();
  }

  /**
   * Creates the SQL syntax for a view creation.
   * 
   * @param name
   *          name of the view
   * @param selectStatement
   *          the statement defining the view
   * 
   * @return the SQL statement to create it
   */
  public static String createViewSQL(String name, String selectStatement) {
    return "CREATE VIEW " + name + " AS " + selectStatement;
  }

  /**
   * Delete data from the data base.
   * 
   * @param connection
   *          connection to the database
   * @param tableName
   *          name of the table form which should be deleted
   * @param whereClause
   *          where clause to narrow down the results, set to null if
   *          unnecessary
   * 
   * @throws SQLException
   *           if deletion goes wrong
   */
  public static void deleteData(Connection connection, String tableName,
      String whereClause) throws SQLException {

    String sql = deleteSQL(tableName, whereClause);

    executeSQL(connection, sql);
  }

  /**
   * Generates SQL for simple deletion.
   * 
   * @param tableName
   *          the name of the table
   * @param whereClause
   *          the where clause to narrow the entries to be deleted down, set
   *          null if unnecessary
   * 
   * @return the SQL statement for the deletion
   */
  public static String deleteSQL(String tableName, String whereClause) {
    return "DELETE FROM " + tableName + getWhereClause(whereClause);
  }

  /**
   * Returns where clause.
   * 
   * @param whereClause
   *          where clause string given by the user
   * 
   * @return either an empty string or a string with WHERE + the user's input
   */
  static String getWhereClause(String whereClause) {
    return (whereClause == null || whereClause.length() == 0 ? "" : " WHERE "
        + whereClause);
  }

  /**
   * Insert some data into the database via the given connection.
   * 
   * @param connection
   *          the connection to used
   * @param tableName
   *          the table to which the data should be added
   * @param colNames
   *          columns names to be filled with values
   * @param values
   *          the values
   * 
   * @return id of inserted data (if any, otherwise -1)
   * 
   * @throws SQLException
   *           if data insertion fails
   */
  public static long insertData(Connection connection, String tableName,
      String[] colNames, String[] values) throws SQLException {
    Statement s = connection.createStatement();
    ResultSet keys = null;
    try {
      String sql = insertDataSQL(tableName, colNames, values);
      if (DEBUG_SQL) {
        SimSystem.report(Level.FINEST, sql);
      }
      s.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
      keys = s.getGeneratedKeys();
      s.close();
      if (keys == null) {
        return -1;
      }
      keys.next();
      return keys.getLong(1);
    } finally {
      try {
        if (keys != null) {
          keys.close();
        }
      } finally {
        s.close();
      }
    }

  }

  /**
   * Wrapper for
   * {@link Databases#insertDataBatch(Connection, String, String[], String[][],DatabaseInsertPolicy)}
   * with default insertion policy.
   * 
   * @param connection
   *          the connection
   * @param tableName
   *          the table name
   * @param colNames
   *          the col names
   * @param values
   *          the values
   * 
   * @return list of IDs
   * 
   * @throws SQLException
   *           the SQL exception
   */
  public static List<Long> insertDataBatch(Connection connection,
      String tableName, String[] colNames, String[][] values)
      throws SQLException {
    return insertDataBatch(connection, tableName, colNames, values,
        DatabaseInsertPolicy.NORMAL);
  }

  /**
   * Inserts multiple data items at once.
   * 
   * @param connection
   *          connection to the database
   * @param tableName
   *          name of the table
   * @param colNames
   *          column names
   * @param values
   *          values
   * @param policy
   *          insertion policy (normal, ignore, replace)
   * 
   * @return list of IDs
   * 
   * @throws SQLException
   *           if anything goes wrong
   */
  public static List<Long> insertDataBatch(Connection connection,
      String tableName, String[] colNames, String[][] values,
      DatabaseInsertPolicy policy) throws SQLException {

    List<Long> keyList = new ArrayList<>();
    Statement s = connection.createStatement();
    ResultSet keys = null;
    try {
      String sql = insertMultipleDataSQL(tableName, colNames, values, policy);
      if (DEBUG_SQL) {
        SimSystem.report(Level.FINEST, sql);
      }
      s.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
      keys = s.getGeneratedKeys();

      if (keys != null) {
        while (keys.next()) {
          keyList.add(keys.getLong(1));
        }
      }

    } finally {
      try {
        if (keys != null) {
          keys.close();
        }
      } finally {
        s.close();
      }
    }
    return keyList;

  }

  /**
   * Creates the SQL syntax for inserting data into a table.
   * 
   * @param tableName
   *          name of the table
   * @param colNames
   *          array of column names that will be set
   * @param values
   *          values for the columns
   * 
   * @return SQL-String to insert the data
   */
  public static String insertDataSQL(String tableName, String[] colNames,
      Object[] values) {

    if (colNames == null || values == null || colNames.length == 0
        || colNames.length != values.length) {
      throw new RuntimeException(
          "Columns names and values do not match. ColNames:"
              + Strings.dispArray(colNames) + ", values:"
              + Strings.dispArray(values));
    }

    String sql =
        "INSERT INTO " + tableName + " ("
            + Strings.getSeparatedList(colNames, ",") + ") VALUES ("
            + Strings.getSeparatedList(values, ",") + ")";

    return sql;
  }

  /**
   * Create SQL statement to insert multiple data sets.
   * 
   * @param tableName
   *          name of the table
   * @param colNames
   *          names of the columns
   * @param values
   *          values to be inserted
   * @param policy
   *          the insertion policy (ignore, replace, or normal)
   * 
   * @return SQL statement to be executed
   */
  private static String insertMultipleDataSQL(String tableName,
      String[] colNames, String[][] values, DatabaseInsertPolicy policy) {

    if (colNames == null || values == null || values.length == 0
        || values[0].length != colNames.length) {
      throw new RuntimeException(
          "Columns names and values do not match. ColNames:"
              + Strings.dispArray(colNames) + ", values:"
              + Strings.displayMatrix(values, ' '));
    }

    List<Object> dataChunks = new ArrayList<>();

    String s =
        policy + " INTO " + tableName + " ("
            + Strings.getSeparatedList(colNames, ",") + ") VALUES ";

    byte[] b = s.getBytes();
    int size = b.length; // number of bytes of the insert statement
    dataChunks.add(b);

    for (int i = 0; i < values.length; i++) {
      s =
          "(" + Strings.getSeparatedList(values[i], ",") + ")"
              + (i == values.length - 1 ? "" : ",");
      b = s.getBytes();
      size += b.length;
      dataChunks.add(b);
    }

    return new String(Arrays.dataChunksToArray(dataChunks, size));
  }

  /**
   * Generates SQL query for select statements.
   * 
   * @param tableName
   *          table from which shall be selected
   * @param colNames
   *          the columns that shall be selected
   * @param whereClause
   *          clause to narrow down results (set null if unnecessary)
   * 
   * @return the generated SQL statement for selection
   */
  public static String querySQL(String tableName, String[] colNames,
      String whereClause) {
    return "SELECT " + Strings.getSeparatedList(colNames, ",") + " FROM "
        + tableName + getWhereClause(whereClause);
  }

  /**
   * This is the same as
   * {@link Databases#selectData(Connection, String, String[], String)}, but
   * selects all.
   * 
   * @param connection
   *          the DB connection
   * @param tableName
   *          the table name
   * @param whereClause
   *          the where clause
   * 
   * @return the selection results
   * 
   * @throws SQLException
   *           if DB query goes wrong
   */
  public static ResultSet selectAllData(Connection connection,
      String tableName, String whereClause) throws SQLException {
    return selectData(connection, tableName, new String[] { "*" }, whereClause);
  }

  /**
   * Execute a select query.
   * 
   * @param connection
   *          the connection object
   * @param tableName
   *          the table name
   * @param colNames
   *          the name of the columns to be selected
   * @param whereClause
   *          the where clause (set null if unnecessary)
   * 
   * @return result set
   * 
   * @throws SQLException
   *           if something goes wrong
   */
  public static ResultSet selectData(Connection connection, String tableName,
      String[] colNames, String whereClause) throws SQLException {
    try (Statement s = connection.createStatement()) {
      String sql = querySQL(tableName, colNames, whereClause);
      if (DEBUG_SQL) {
        SimSystem.report(Level.FINEST, sql);
      }
      return s.executeQuery(sql);
    }
  }

  /**
   * Checks whether a certain table or view already exists in the database or
   * not.
   * 
   * @param connection
   *          connection to database
   * @param tableName
   *          name of the table or view you are looking for
   * 
   * @return True if table already exists. False if the table does not exist or
   *         if the database connection fails.
   */
  public static boolean tableExists(Connection connection, String tableName) {
    boolean bRet = false;
    DatabaseMetaData dbmd;
    String[] tabTypes = { "TABLE", "VIEW" };
    ResultSet rs = null;
    try {
      dbmd = connection.getMetaData();
      rs = dbmd.getTables(null, null, "%", tabTypes);
      while (rs.next()) {
        if (rs.getString("TABLE_NAME").equalsIgnoreCase(tableName)) {
          bRet = true;
        }
      }
    } catch (SQLException e) {
      SimSystem.report(Level.SEVERE,
          "Fetching metadata from the database failed.", e);
    } finally {
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException e) {
          SimSystem.report(e);
        }
      }
    }
    return bRet;
  }

  /**
   * To string.
   * 
   * @param o
   *          the o
   * 
   * @return the string
   */
  public static String toString(Object o) {
    if (o == null) {
      return "''";
    }
    return o.toString();
  }

  /**
   * To string.
   * 
   * @param data
   *          the data
   * 
   * @return the string
   */
  public static String toString(Double data) {
    return data.toString();
  }

  /**
   * To string.
   * 
   * @param data
   *          the data
   * 
   * @return the string
   */
  public static String toString(Long data) {
    return data.toString();
  }

  /**
   * To string.
   * 
   * @param data
   *          the data
   * 
   * @return the string
   */
  public static String toString(Integer data) {
    return data.toString();
  }

  /**
   * To string.
   * 
   * @param data
   *          the data
   * 
   * @return the string
   */
  public static String toString(String data) {
    return "'" + data + "'";
  }

  /**
   * To string.
   * 
   * @param uri
   *          the uri
   * 
   * @return the string
   */
  public static String toString(URI uri) {
    if (uri == null) {
      return "''";
    }
    return "'" + uri.toString() + "'";
  }

  /**
   * Update the database via the given connection.
   * 
   * @param connection
   *          the connection to the DB
   * @param tableName
   *          name of the table to be updated
   * @param colNames
   *          name of columns
   * @param values
   *          values to be updated, one for each column (otherwise a RTE is
   *          thrown)
   * @param whereClause
   *          clause to narrow down what to update
   * 
   * @return result set of IDs for changed rows
   * 
   * @throws SQLException
   *           if update fails
   */
  public static ResultSet updateData(Connection connection, String tableName,
      String[] colNames, Object[] values, String whereClause)
      throws SQLException {
    try (Statement s = connection.createStatement()) {
      String sql = updateDataSQL(tableName, colNames, values, whereClause);
      if (DEBUG_SQL) {
        System.err.println(sql);
      }
      s.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
      return s.getGeneratedKeys();
    }

  }

  /**
   * Generates SQL string for update queries.
   * 
   * @param tableName
   *          name of the table to be updated
   * @param colNames
   *          name of columns
   * @param values
   *          values to be updated, one for each column (otherwise a RTE is
   *          thrown)
   * @param whereClause
   *          clause to narrow down what to update
   * 
   * @return SQL update query
   */
  public static String updateDataSQL(String tableName, String[] colNames,
      Object[] values, String whereClause) {

    if (values.length != colNames.length) {
      throw new RuntimeException("Update Query failed: there are "
          + colNames.length + " variables, but " + values.length + " values.");
    }

    String[] assignments = new String[colNames.length];
    for (int i = 0; i < colNames.length; i++) {
      assignments[i] = colNames[i] + "=" + values[i];
    }

    String sql =
        "UPDATE " + tableName + " SET "
            + Strings.getSeparatedList(assignments, ",");

    return sql + getWhereClause(whereClause);
  }

  /**
   * Creates database entities from SQL in files - identified by a given file
   * ending - from a certain directory (non-recursively!). Views and stored
   * procedures will be named after their file names (without the ending), the
   * view definition files must only contain the SELECT statement that defines
   * the view.
   * 
   * @param connection
   *          the connection to the database
   * @param dirPath
   *          the path to the directory containing the SQL files
   * @param fileEnding
   *          the file ending that identifies files containing the SELECT
   *          statements for views
   * @param dbEntityType
   *          the type of the entities to be created
   * 
   * @throws FileNotFoundException
   *           the file not found exception
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public static void createEntities(Connection connection, String dirPath,
      String fileEnding, DatabaseEntityType dbEntityType) throws IOException {

    File dir = new File(dirPath);
    if (!dir.isDirectory()) {
      return;
    }

    String[] files = dir.list(Files.getFilenameFilter(fileEnding, false));
    for (String file : files) {
      File viewFile = new File(dir.getAbsolutePath() + File.separator + file);
      if (viewFile.isDirectory()) {
        continue;
      }
      try {
        switch (dbEntityType) {
        case VIEW:
          Databases.createView(connection,
              Files.getFileNameWithoutEnding(viewFile, fileEnding),
              Files.getFileAsString(viewFile));
          break;
        case STORED_PROCEDURE:
          Databases.createStoredProcedure(connection,
              Files.getFileNameWithoutEnding(viewFile, fileEnding),
              Files.getFileAsString(viewFile));
        }
      } catch (Exception ex) {
        SimSystem.report(ex);
      }
    }
  }
}

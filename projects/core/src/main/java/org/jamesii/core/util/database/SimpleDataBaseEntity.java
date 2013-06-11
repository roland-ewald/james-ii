/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jamesii.SimSystem;
import org.jamesii.core.util.misc.Databases;

/**
 * Super class for simple data base entities, implements some standard function
 * for managing entities in the database. Since Java does not allow certain
 * static generic functions, many of the functions that may be used in a static
 * way (eg., to get all entities, etc.) have to be done on some kind of dummy
 * instance.
 * 
 * @author Roland Ewald
 * @param <X>
 *          the type of the database entity
 */
public abstract class SimpleDataBaseEntity<X extends SimpleDataBaseEntity<X>> {

  /** The database connection. */
  private static Connection connection = null;

  /** Type of foreign key. */
  public static final String FOREIGN_KEY_TYPE = "BIGINT NOT NULL";

  /** Type of primary key. */
  public static final String PRIMARY_KEY_TYPE =
      "BIGINT PRIMARY KEY AUTO_INCREMENT";

  /**
   * Tests whether instances of this kind are already connected to the database.
   * 
   * @return true, if a connection is set and it has not been closed yet
   * @throws SQLException
   *           if testing for the connection status failed
   */
  public static boolean isConnected() throws SQLException {
    return connection != null && !connection.isClosed();
  }

  /** ID of the model. */
  private long id = -1;

  /**
   * Sets connection that will be used for all other entities of that kind.
   * Attempts to create the data structures necessary to store the entities, if
   * these do not exist.
   * 
   * @param conn
   *          the connection to be used
   * @throws SQLException
   *           if connection or table creation failed
   */
  public void connect(Connection conn) throws SQLException {
    if (connection != null && !connection.equals(conn)) {
      connection.close();
    }

    connection = conn;

    String pkName = getPrimaryKeyName();
    boolean pkExists = pkName != null;
    int pkOffset = pkExists ? 1 : 0;

    String[] columnNames = getColumnNames();
    String[] dataTypes = getColumnDataTypes();
    String[][] creationString = new String[columnNames.length + pkOffset][2];

    if (pkExists) {
      creationString[0][0] = pkName;
      creationString[0][1] = PRIMARY_KEY_TYPE;
    }

    for (int i = 0; i < columnNames.length; i++) {
      creationString[i + pkOffset][0] = columnNames[i];
      creationString[i + pkOffset][1] = dataTypes[i];
    }

    Databases.createTable(connection, getTableName(), creationString,
        getAdditionalCreationSQL());
  }

  /**
   * Creates additional SQL syntax for modification of created table, eg. to
   * define foreign keys.
   * 
   * @return additional SQL syntax
   */
  protected String getAdditionalCreationSQL() {
    return "";
  }

  /**
   * Get a copy of the entity.
   * 
   * @return copy of the entity
   */
  public X copy() {
    X copy = getCopy();
    copy.setID(id);
    return copy;
  }

  /**
   * Creates the entity in the database. The new ID is set automatically.
   * 
   * @throws SQLException
   *           if creation went wrong
   */
  public void create() throws SQLException {
    setID(Databases.insertData(connection, getTableName(), getColumnNames(),
        getColumnValues()));
    wasWritten();
  }

  /**
   * Creates a list of entities in the database.
   * 
   * @param entities
   *          list of entities
   * @throws SQLException
   *           if insert failed
   */
  public void create(List<X> entities) throws SQLException {

    String[][] values = new String[entities.size()][getColumnNames().length];

    for (int i = 0; i < entities.size(); i++) {
      values[i] = entities.get(i).getColumnValues();
    }

    List<Long> ids =
        Databases.insertDataBatch(connection, getTableName(), getColumnNames(),
            values);

    for (int i = 0; i < entities.size(); i++) {
      entities.get(i).setID(ids.get(i));
      entities.get(i).wasWritten();
    }
  }

  /**
   * Get the data types for the column names. Use same order as in
   * {@link SimpleDataBaseEntity#getColumnNames()}.
   * 
   * @return list of SQL data types for the column names
   */
  protected abstract String[] getColumnDataTypes();

  /**
   * Get the column names of the table in which these entities are stored.
   * 
   * @return the column names, in the desired order
   */
  protected abstract String[] getColumnNames();

  /**
   * Get the representation of the entity as a row (except the ID field). Wrap
   * VARCHARS and other strings with "'", etc.
   * 
   * @return string of assignments
   * @throws SQLException
   *           the exception
   */
  protected abstract String[] getColumnValues() throws SQLException;

  /**
   * Get copy of this entity.
   * 
   * @return copy of the entity
   */
  protected abstract X getCopy();

  /**
   * Get list of all entities.
   * 
   * @param whereClause
   *          the clause to be used for narrowing the results, use null if
   *          everything should be selected
   * @return list of all entries that fulfil the whereClause
   * @throws SQLException
   *           if selection goes wrong
   */
  public List<X> getEntities(String whereClause) throws SQLException {
    List<X> entities = new ArrayList<>();
    try (ResultSet rs =
        Databases.selectAllData(connection, getTableName(), whereClause)) {
      while (rs.next()) {
        try {
          X entity = getEntityByResultSet(rs);
          // ID is always in the first column, see connect(...)
          entity.setID(rs.getLong(1));
          entity.wasRead();
          entities.add(entity);
        } catch (Exception ex) {
          SimSystem.report(ex);
        }
      }
    }
    return entities;
  }

  /**
   * Retrieves an entity from the database.
   * 
   * @param searchID
   *          the ID of the entity
   * @return the entity
   * @throws SQLException
   *           if retrieval went wrong
   * @throws SQLException
   *           if database look-up went wrong
   */
  public X getEntity(long searchID) throws SQLException {
    X entity;
    boolean hasNext;
    try (ResultSet rs =
        Databases.selectAllData(connection, getTableName(), getPrimaryKeyName()
            + "=" + searchID)) {
      if (!rs.next()) {
        return null;
      }
      entity = getEntityByResultSet(rs);
      entity.setID(rs.getLong(1));
      entity.wasRead();
      hasNext = rs.next();
    }

    if (hasNext) {
      throw new RuntimeException(
          "There are more than one entity with primary key " + searchID
              + " in table " + getTableName() + ".");
    }
    return entity;
  }

  /**
   * Create an entity (without the ID, this will be set automatically) from a
   * given result set.
   * 
   * @param resultSet
   *          the result set from which to create the entity
   * @return the entity
   */
  protected abstract X getEntityByResultSet(ResultSet resultSet);

  /**
   * Get ID.
   * 
   * @return id of the entity
   */
  public long getID() {
    return id;
  }

  /**
   * Get name of ID field.
   * 
   * @return name of the ID field
   */
  protected String getPrimaryKeyName() {
    return "id";
  }

  /**
   * Get name of the table in which entities of that type are stored.
   * 
   * @return name of the table
   */
  protected abstract String getTableName();

  /**
   * Removes entity from database.
   * 
   * @throws SQLException
   *           if removal failed
   */
  public void remove() throws SQLException {
    Databases.deleteData(connection, getTableName(), getPrimaryKeyName() + "="
        + getID());
    wasDeleted();
  }

  /**
   * Set the ID of this entity.
   * 
   * @param newID
   *          new ID of this entity
   */
  public void setID(long newID) {
    id = newID;
  }

  /**
   * Synchronises entity values with the data base.
   * 
   * @throws SQLException
   *           if synchronisation with database failed
   */
  public void update() throws SQLException {
    Databases.updateData(connection, getTableName(), getColumnNames(),
        getColumnValues(), getPrimaryKeyName() + "=" + getID());
    wasWritten();
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof SimpleDataBaseEntity<?>)) {
      return false;
    }

    SimpleDataBaseEntity<?> entity = (SimpleDataBaseEntity<?>) obj;
    if (entity.getTableName().compareTo(getTableName()) != 0) {
      return false;
    }

    return entity.getID() == getID();
  }

  @Override
  public int hashCode() {
    return getTableName().hashCode() + Long.valueOf(getID()).hashCode();
  }

  /**
   * Tests results set for queries that are supposed to retrieve a single
   * (unique) entity: if it is empty, null is returned, if there is one element,
   * the element will be returned. If there is more than one element, a
   * {@link RuntimeException} will be thrown.
   * 
   * @param values
   *          the list of values
   * @return null (no entity found) or the unique entity
   */
  protected X getUniqueEntity(List<X> values) {
    if (values.size() == 0) {
      return null;
    }
    if (values.size() > 1) {
      throw new RuntimeException("Duplicate value of type:"
          + values.get(0).getClass().getCanonicalName());
    }
    return values.get(0);
  }

  /**
   * This method might be overridden to implement additional behaviour to be
   * executed after the entity has been written to the DB. Can be used to store
   * additional data in other tables, etc.
   * 
   * @throws SQLException
   *           if writing actions failed
   */
  protected void wasWritten() throws SQLException {
  }

  /**
   * This method might be overridden to implement additional behaviour to be
   * executed after the entity has been read from the DB. Can be used to
   * retrieved additionally stored data from other tables, etc.
   * 
   * @throws SQLException
   *           if reading actions failed
   */
  protected void wasRead() throws SQLException {
  }

  /**
   * This method might be overridden to implement additional behaviour to be
   * executed after the entity has been deleted from the DB. Can be used to
   * remove additionally stored data from other tables, etc.
   * 
   * @throws SQLException
   *           if deletion actions failed
   */
  protected void wasDeleted() throws SQLException {
  }

  /**
   * Gets the connection.
   * 
   * @return the connection
   */
  public static Connection getConnection() {
    return connection;
  }
}

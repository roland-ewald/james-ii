/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.source;

import java.sql.ResultSet;
import java.util.List;

/**
 * The interface for data sources that need to be integrated, e.g. to import
 * real-world data and compare that to simulation outcomes. Should be used in
 * analogy to {@link org.jamesii.core.data.storage.IDataStorage}.
 * 
 * @author Roland Ewald
 * 
 *         10.05.2007
 */
@Deprecated
public interface IDataSource {

  /**
   * Gets the attribute names.
   * 
   * @param tableName
   *          the table name
   * 
   * @return the attribute names
   */
  List<String> getAttributeNames(String tableName);

  /**
   * Gets the table.
   * 
   * @param tableName
   *          the table name
   * @param attributeNames
   *          the attribute names
   * 
   * @return the table
   */
  ResultSet getTable(String tableName, List<String> attributeNames);

  /**
   * Gets the table names.
   * 
   * @return the table names
   */
  List<String> getTableNames();

  // or something like this, select x from table where filter ...
  // ResultSet getFilteredTable (String tableName, ArrayList<String>
  // attributeNames, ArrayList<ColumnFilter> attributeFilters);

  // ArrayList<AttributeInfo> getAttributes (String tableName);
}

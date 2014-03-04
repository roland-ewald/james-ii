/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.jdbc;


import java.net.URI;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.jamesii.core.util.misc.Databases;
import org.jamesii.perfdb.ConstraintException;
import org.jamesii.perfdb.DatabaseAccessException;
import org.jamesii.perfdb.entities.IProblemScheme;

/**
 * This class holds all relevant information regarding a (benchmark) model in
 * the performance database.
 * 
 * @author Roland Ewald
 * 
 */
@Deprecated
public class BenchmarkModel extends NamedDBEntity<BenchmarkModel> implements
    IProblemScheme {

  /** Serialisation ID. */
  private static final long serialVersionUID = -1473044513266694910L;

  /** The column index for the model URI. */
  private static final int COLUMN_INDEX_URI = 2;

  /** The column index for the model name. */
  private static final int COLUMN_INDEX_NAME = 3;

  /** The column index for the model type. */
  private static final int COLUMN_INDEX_TYPE = 4;

  /** The column index for the index description. */
  private static final int COLUMN_INDEX_DESC = 5;

  /** URI of the model. */
  private URI uri = null;

  /** Benchmark model type. */
  private String type = "";

  protected BenchmarkModel() {
  }

  public BenchmarkModel(long id) throws SQLException {
    BenchmarkModel bmModel = this.getEntity(id);
    uri = bmModel.getUri();
    setName(bmModel.getName());
    setDescription(bmModel.getDescription());
    setID(id);
  }

  public BenchmarkModel(URI uri, String name, String bmType, String desc) {
    this.uri = uri;
    setName(name);
    setDescription(desc);
    type = bmType;
  }

  @Override
  public URI getUri() {
    return uri;
  }

  @Override
  public void setUri(URI uri) {
    this.uri = uri;
  }

  @Override
  protected String[] getColumnDataTypes() {
    return new String[] { "VARCHAR(255)", "VARCHAR(64)", "VARCHAR(6)", "TEXT" };
  }

  @Override
  protected String[] getColumnNames() {
    return new String[] { "URI", "name", "type", "description" };
  }

  @Override
  protected String[] getColumnValues() {
    return new String[] { Databases.toString(uri),
        Databases.toString(getName()), Databases.toString(type),
        Databases.toString(getName()) };
  }

  @Override
  protected BenchmarkModel getEntityByResultSet(ResultSet rs) {
    try {
      return new BenchmarkModel(new URI(rs.getString(COLUMN_INDEX_URI)),
          rs.getString(COLUMN_INDEX_NAME), rs.getString(COLUMN_INDEX_TYPE),
          rs.getString(COLUMN_INDEX_DESC));
    } catch (Exception e) {
      throw new DatabaseAccessException(e);
    }
  }

  @Override
  protected String getTableName() {
    return "models";
  }

  @Override
  protected BenchmarkModel getCopy() {
    return new BenchmarkModel(uri, getName(), type, getDescription());
  }

  /**
   * Looks up a benchmark model from the performance database.
   * 
   * @param u
   *          the URI of the benchmark model
   * @return benchmark model, null if not found
   * @throws SQLException
   *           if there is a database error
   * @throws ConstraintException
   *           if there are multiple benchmark models with the same URI
   */
  public BenchmarkModel lookUp(URI u) throws SQLException {
    List<BenchmarkModel> bModels = getEntities("URI=" + Databases.toString(u));
    if (bModels.size() > 1) {
      throw new ConstraintException(
          "There are multiple benchmark models with the URI" + u);
    }

    return (bModels.size() == 0 ? null : bModels.get(0));
  }

  @Override
  public String getType() {
    return type;
  }

  @Override
  public void setType(String type) {
    this.type = type;
  }
}
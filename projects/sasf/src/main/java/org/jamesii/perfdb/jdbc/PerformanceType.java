/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.jdbc;


import java.sql.ResultSet;
import java.sql.SQLException;

import org.jamesii.core.util.misc.Databases;
import org.jamesii.perfdb.DatabaseAccessException;
import org.jamesii.perfdb.entities.IPerformanceType;
import org.jamesii.perfdb.recording.performance.plugintype.PerformanceMeasurerFactory;

/**
 * Represents a performance measure. This could be execution time, approximation
 * error, or even CO_2 generation / energy consumption.
 * 
 * @author Roland Ewald
 * 
 */
@Deprecated
public class PerformanceType extends NamedDBEntity<PerformanceType> implements
    IPerformanceType {

  /** Serialisation ID. */
  private static final long serialVersionUID = -4083284490938956672L;

  /** The column index for the name. */
  private static final int COLUMN_INDEX_NAME = 2;

  /** The column index for the description. */
  private static final int COLUMN_INDEX_DESC = 3;

  /** The column index for the performance measure factory. */
  private static final int COLUMN_INDEX_PERF_MEASURE_FAC = 4;

  /** Reference to performance measurer factory. */
  private Class<? extends PerformanceMeasurerFactory> performanceMeasurerFactory;

  public PerformanceType(long id) throws SQLException {
    PerformanceType pMeasure = this.getEntity(id);
    setName(pMeasure.getName());
    setDescription(pMeasure.getDescription());
    performanceMeasurerFactory = pMeasure.getPerformanceMeasurerFactory();
    setID(id);
  }

  public PerformanceType(String name, String desc,
      Class<? extends PerformanceMeasurerFactory> pmFactory) {
    setName(name);
    setDescription(desc);
    performanceMeasurerFactory = pmFactory;
  }

  protected PerformanceType() {
  }

  @Override
  public Class<? extends PerformanceMeasurerFactory> getPerformanceMeasurerFactory() {
    return performanceMeasurerFactory;
  }

  @Override
  public void setPerformanceMeasurerFactory(
      Class<? extends PerformanceMeasurerFactory> performanceMeasurerFactory) {
    this.performanceMeasurerFactory = performanceMeasurerFactory;
  }

  @Override
  protected String[] getColumnDataTypes() {
    return new String[] { "VARCHAR(127)", "TEXT", "VARCHAR(255)" };
  }

  @Override
  protected String[] getColumnNames() {
    return new String[] { "name", "description", "performance_measurer" };
  }

  @Override
  protected String[] getColumnValues() {
    return new String[] { Databases.toString(getName()),
        Databases.toString(getDescription()),
        Databases.toString(performanceMeasurerFactory.getCanonicalName()) };
  }

  @Override
  protected PerformanceType getCopy() {
    return new PerformanceType(getName(), getDescription(),
        performanceMeasurerFactory);
  }

  @Override
  @SuppressWarnings("unchecked")
  protected PerformanceType getEntityByResultSet(ResultSet resultSet) {
    try {
      return new PerformanceType(resultSet.getString(COLUMN_INDEX_NAME),
          resultSet.getString(COLUMN_INDEX_DESC),
          (Class<? extends PerformanceMeasurerFactory>) Class.forName(resultSet
              .getString(COLUMN_INDEX_PERF_MEASURE_FAC)));
    } catch (SQLException | ClassNotFoundException e) {
      throw new DatabaseAccessException(e);
    }
  }

  @Override
  protected String getTableName() {
    return "performance_types";
  }
}

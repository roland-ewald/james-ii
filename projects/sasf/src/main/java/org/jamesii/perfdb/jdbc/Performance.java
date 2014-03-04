/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.jdbc;


import java.sql.ResultSet;
import java.sql.SQLException;

import org.jamesii.core.util.database.SimpleDataBaseEntity;
import org.jamesii.core.util.misc.Databases;
import org.jamesii.perfdb.DatabaseAccessException;
import org.jamesii.perfdb.entities.IApplication;
import org.jamesii.perfdb.entities.IPerformance;
import org.jamesii.perfdb.entities.IPerformanceType;

/**
 * Represents the performance of a runtime configuration with respect to a
 * certain performance measure.
 * 
 * @author Roland Ewald
 * 
 */
@Deprecated
public class Performance extends SimpleDataBaseEntity<Performance> implements
    IPerformance {

  /** Serialisation ID. */
  private static final long serialVersionUID = -5770945895862526441L;

  /** The column index for the performance type id. */
  private static final int COLUMN_INDEX_PERF_TYPE_ID = 3;

  /** The column index for the performance value. */
  private static final int COLUMN_INDEX_PERFORMANCE = 4;

  /** Reference to the application that was evaluated. */
  private IApplication application = null;

  /** The performance measure used for this measurement. */
  private IPerformanceType performanceType = null;

  /** The measured performance. */
  private double performance = 0.0;

  public Performance(IApplication app, IPerformanceType perfMeasure, double perf) {
    application = app;
    performanceType = perfMeasure;
    performance = perf;
  }

  public Performance(long id) throws SQLException {
    IPerformance pm = this.getEntity(id);
    application = pm.getApplication();
    performanceType = pm.getPerformanceType();
    performance = pm.getPerformance();
    setID(id);
  }

  protected Performance() {
  }

  @Override
  public IApplication getApplication() {
    return application;
  }

  @Override
  public void setApplication(IApplication app) {
    this.application = app;
  }

  @Override
  public IPerformanceType getPerformanceType() {
    return performanceType;
  }

  @Override
  public void setPerformanceType(IPerformanceType perfType) {
    this.performanceType = perfType;
  }

  @Override
  public double getPerformance() {
    return performance;
  }

  @Override
  public void setPerformance(double performance) {
    this.performance = performance;
  }

  @Override
  protected String[] getColumnNames() {
    return new String[] { "config_id", "performance_type_id", "performance" };
  }

  @Override
  protected String[] getColumnDataTypes() {
    return new String[] { SimpleDataBaseEntity.FOREIGN_KEY_TYPE,
        SimpleDataBaseEntity.FOREIGN_KEY_TYPE, "DOUBLE" };
  }

  @Override
  protected String[] getColumnValues() {
    return new String[] { Databases.toString(application.getID()),
        Databases.toString(performanceType.getID()),
        Databases.toString(performance) };
  }

  @Override
  protected Performance getCopy() {
    return new Performance(application, performanceType, performance);
  }

  @Override
  protected Performance getEntityByResultSet(ResultSet resultSet) {
    try {
      return new Performance(null, new PerformanceType(
          resultSet.getLong(COLUMN_INDEX_PERF_TYPE_ID)),
          resultSet.getDouble(COLUMN_INDEX_PERFORMANCE));
    } catch (Exception e) {
      throw new DatabaseAccessException(e);
    }
  }

  @Override
  protected String getTableName() {
    return "performance_measurements";
  }

  @Override
  protected String getAdditionalCreationSQL() {
    return ", FOREIGN KEY (config_id) REFERENCES runtime_configurations(id) ON DELETE CASCADE"
        + ", FOREIGN KEY (performance_type_id) REFERENCES performance_types(id) ON DELETE CASCADE";
  }

}

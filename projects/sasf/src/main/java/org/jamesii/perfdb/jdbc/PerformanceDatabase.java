/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.jdbc;


import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.jamesii.core.data.DBConnectionData;
import org.jamesii.core.util.database.SimpleDataBase;
import org.jamesii.core.util.misc.Databases;
import org.jamesii.core.util.misc.Databases.DatabaseEntityType;
import org.jamesii.perfdb.DatabaseAccessException;
import org.jamesii.perfdb.entities.IPerformance;
import org.jamesii.perfdb.entities.IPerformanceType;
import org.jamesii.perfdb.entities.IProblemDefinition;
import org.jamesii.perfdb.entities.IRuntimeConfiguration;

/**
 * This class serves as an interface to a performance database, which is some
 * database that supports JDBC.
 * 
 * @author Roland Ewald
 */
@Deprecated
public class PerformanceDatabase extends SimpleDataBase {

  private static final int COLUMN_INDEX_PERF_ID = 1;

  private static final int COLUMN_INDEX_PERFORMANCE = 3;

  /**
   * Benchmark model dummy.
   */
  private final BenchmarkModel benchmarkModel;

  /**
   * Feature type dummy.
   */
  private final FeatureType feature;

  /**
   * Feature value dummy.
   */
  private final Feature featureValue;

  /**
   * Performance measure dummy.
   */
  private final PerformanceType performanceMeasure;

  /**
   * Performance measurement dummy.
   */
  private final Performance performanceMeasurement;

  /**
   * Performance profile dummy.
   */
  private final IPerformaneProfile performanceProfile;

  /**
   * Runtime configuration dummy.
   */
  private final RuntimeConfiguration runtimeConfiguration;

  /**
   * Simulation problem dummy.
   */
  private final SimulationProblem simulationProblem;

  /**
   * Default constructor.
   * 
   * @param dbcd
   *          database connection data
   * @throws SQLException
   *           if connection could not be established
   * @throws ClassNotFoundException
   *           if driver could not be loaded
   * 
   */
  public PerformanceDatabase(DBConnectionData dbcd) throws SQLException,
      ClassNotFoundException {
    super(dbcd);
    benchmarkModel = new BenchmarkModel();
    feature = new FeatureType();
    featureValue = new Feature();
    performanceMeasure = new PerformanceType();
    performanceMeasurement = new Performance();
    performanceProfile = new PerformanceProfile();
    runtimeConfiguration = new RuntimeConfiguration();
    simulationProblem = new SimulationProblem();

    addEntity(benchmarkModel);
    addEntity(simulationProblem);
    addEntity(runtimeConfiguration);

    addEntity(performanceMeasure);
    addEntity(performanceMeasurement);

    addEntity(feature);
    addEntity(featureValue);
  }

  @Override
  public boolean createBase(Object[] params) {
    try {
      this.initializeDBEntities();
      Databases.createEntities(getConnection(), "org/jamesii/perfdb/jdbc/sql",
          "view.sql", DatabaseEntityType.VIEW);
      Databases.createEntities(getConnection(), "org/jamesii/perfdb/jdbc/sql",
          "sp.sql", DatabaseEntityType.STORED_PROCEDURE);

    } catch (SQLException | IOException | ClassNotFoundException e) {
      throw new DatabaseAccessException(e);
    }
    return true;
  }

  @Override
  public void openBase() throws ClassNotFoundException, SQLException {
    super.openBase();
    this.initializeDBEntities();
  }

  public BenchmarkModel getBenchmarkModel() {
    return benchmarkModel;
  }

  public FeatureType getFeature() {
    return feature;
  }

  public Feature getFeatureValue() {
    return featureValue;
  }

  public PerformanceType getPerformanceMeasure() {
    return performanceMeasure;
  }

  public IPerformance getPerformanceMeasurement() {
    return performanceMeasurement;
  }

  public IPerformaneProfile getPerformanceProfile() {
    return performanceProfile;
  }

  public RuntimeConfiguration getRuntimeConfiguration() {
    return runtimeConfiguration;
  }

  public SimulationProblem getSimulationProblem() {
    return simulationProblem;
  }

  /**
   * Get the best runtime configurations for all available kinds of performance
   * measures, given a certain simulation problem.
   * 
   * @param database
   *          the database to be used
   * @param simProblem
   *          the simulation problem for which the best configurations shall be
   *          found
   * @return map of form performance measure => best runtime config
   * @throws SQLException
   *           if deserialization or database querying goes wrong
   */
  public Map<IPerformanceType, IRuntimeConfiguration> getBestConfigurations(
      IProblemDefinition simProblem) throws SQLException {

    // TODO: Use a stored procedure to generate pairs of (config_id,
    // feature_id), that should be faster
    // TODO: Catch case when there are more than one best config (look at the
    // average, prompt the user, whatever...)

    HashMap<IPerformanceType, IRuntimeConfiguration> bestConfigs =
        new HashMap<>();

    Connection connection = null;
    try {
      connection = getConnection();
    } catch (ClassNotFoundException e) {
      throw new DatabaseAccessException(e);
    } finally {
      if (connection != null) {
        connection.close();
      }
    }

    ResultSet problems = null;

    try {
      problems =
          Databases.selectAllData(connection, "perf_profiles_problems",
              "problem_id=" + simProblem.getID());

      while (problems.next()) {
        Statement statement = null;
        ResultSet rs = null;
        try {
          statement = connection.createStatement();
          String sql =
              "SELECT PM.performance_type_id as perf_type_id, RT.id as config_id FROM runtime_configurations as RT LEFT JOIN (performance_measurements as PM) ON (PM.config_id=RT.id) WHERE PM.performance <="
                  + problems.getDouble(COLUMN_INDEX_PERFORMANCE)
                  + " AND RT.problem_id="
                  + problems.getLong(COLUMN_INDEX_PERF_ID)
                  + " GROUP BY PM.config_id";
          rs = statement.executeQuery(sql);
          while (rs.next()) {
            bestConfigs.put(performanceMeasure.getEntity(rs.getLong(1)),
                runtimeConfiguration.getEntity(rs.getLong(2)));
          }
        } finally {
          try {
            rs.close();
          } finally {
            statement.close();
          }
        }
      }
    } finally {
      if (problems != null) {
        problems.close();
      }
    }

    return bestConfigs;
  }
}

/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.jdbc;


import java.io.IOException;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.serialization.SerialisationUtils;
import org.jamesii.core.util.database.SimpleDataBaseEntity;
import org.jamesii.core.util.graph.EqualsCheck;
import org.jamesii.core.util.misc.Databases;
import org.jamesii.core.util.misc.Generics;
import org.jamesii.perfdb.ConstraintException;
import org.jamesii.perfdb.DatabaseAccessException;
import org.jamesii.perfdb.entities.IProblemDefinition;
import org.jamesii.perfdb.entities.IRuntimeConfiguration;
import org.jamesii.perfdb.recording.selectiontrees.SelectionTree;

/**
 * This class represents the configuration of the simulation system. The mode of
 * execution should be reproducible from what is stored inside this object, ie.
 * the selection information and parameter settings of all algorithms that can
 * be tuned should be stored here.
 * 
 * @author Roland Ewald
 * 
 */
@Deprecated
public class RuntimeConfiguration extends
    SimpleDataBaseEntity<RuntimeConfiguration> implements Serializable,
    IRuntimeConfiguration {

  /** Serialisation ID. */
  private static final long serialVersionUID = -5754224532787473347L;

  /** The column index for the standarddeviation. */
  private static final int COLUMN_INDEX_PERF_PROFILE_STD_DEV = 7;

  /** The column index for the sample size. */
  private static final int COLUMN_INDEX_PERF_PROFILE_SAMPLE_SIZE = 6;

  /** The column index for the maximum performance. */
  private static final int COLUMN_INDEX_PERF_PROFILE_MAX_PERF = 5;

  /** The column index for the average performance. */
  private static final int COLUMN_INDEX_PERF_PROFILE_AVG_PERF = 4;

  /** The column index for the minimum performance. */
  private static final int COLUMN_INDEX_PERF_PROFILE_MIN_PERF = 3;

  /** The column index for the performance id. */
  private static final int COLUMN_INDEX_PERF_PROFILE_PERF_ID = 2;

  /** The column index for the configuration id. */
  private static final int COLUMN_INDEX_PERF_PROFILE_CONF_ID = 1;

  /** The column index for the selection tree hash-code. */
  private static final int COLUMN_INDEX_SELECTION_TREE_HASH = 3;

  /** The column index for the selection tree itself. */
  private static final int COLUMN_INDEX_SELECTION_TREE = 4;

  /** Hash sum of the current selection tree. */
  private long selectionTreeHash;

  /** The selection tree that defines the configuration data. */
  private SelectionTree selectionTree;

  public RuntimeConfiguration(SelectionTree selTree) {
    setSelectionTree(selTree);
  }

  /**
   * Instantiates a new runtime configuration.
   * 
   * @param id
   *          the id
   * @throws SQLException
   *           the SQL exception
   */
  public RuntimeConfiguration(long id) throws SQLException {
    IRuntimeConfiguration rtc = this.getEntity(id);
    setSelectionTree(rtc.getSelectionTree());
    setID(id);
  }

  protected RuntimeConfiguration() {
  }

  @Override
  public SelectionTree getSelectionTree() {
    return selectionTree;
  }

  @Override
  public final void setSelectionTree(SelectionTree selectionTree) {
    this.selectionTree = selectionTree;
    selectionTreeHash = selectionTree.getHash();
  }

  @Override
  protected String[] getColumnNames() {
    return new String[] { "selection_hash", "selection_tree" };
  }

  @Override
  protected String[] getColumnDataTypes() {
    return new String[] { SimpleDataBaseEntity.FOREIGN_KEY_TYPE, "LONG", "TEXT" };
  }

  @Override
  protected String[] getColumnValues() {
    String[] values = null;
    try {
      values =
          new String[] {
              Databases.toString(selectionTree.getHash()),
              Databases.toString(SerialisationUtils
                  .serializeToB64String(selectionTree)) };
    } catch (Exception ex) {
      throw new DatabaseAccessException(ex);
    }

    return values;
  }

  @Override
  protected RuntimeConfiguration getCopy() {
    return new RuntimeConfiguration(selectionTree);
  }

  @Override
  protected RuntimeConfiguration getEntityByResultSet(ResultSet resultSet) {
    RuntimeConfiguration newConfig = null;

    try {
      newConfig =
          new RuntimeConfiguration(
              (SelectionTree) SerialisationUtils
                  .deserializeFromB64String(resultSet
                      .getString(COLUMN_INDEX_SELECTION_TREE)));
      if (newConfig.getSelectionTreeHash() != resultSet
          .getLong(COLUMN_INDEX_SELECTION_TREE_HASH)) {
        throw new IllegalArgumentException(
            "Selection tree has invalid hash code for runtime configuration with id: "
                + newConfig.getID());
      }
    } catch (IOException | ClassNotFoundException | SQLException e) {
      reportReadFailure(e);
    }

    return newConfig;
  }

  /**
   * Report read failure.
   * 
   * @param e
   *          the exception
   */
  private void reportReadFailure(Exception e) {
    SimSystem.report(Level.SEVERE, "Reading runtime config failed.", e);
  }

  @Override
  protected String getTableName() {
    return "runtime_configurations";
  }

  @Override
  public long getSelectionTreeHash() {
    return selectionTreeHash;
  }

  /**
   * Looks up existing runtime configurations for a given selection tree.
   * 
   * @param selTree
   *          the selection tree
   * @return the matching runtime configuration, or null
   * @throws SQLException
   *           if look-up failed
   */
  public IRuntimeConfiguration lookUp(SelectionTree selTree)
      throws SQLException {

    IRuntimeConfiguration returnConfig = null;
    List<IRuntimeConfiguration> rtConfigs =
        Generics.changeListType(getEntities("selection_hash='"
            + selTree.getHash() + "'"));

    // Check back: equivalent hash code is not sufficient!
    for (IRuntimeConfiguration rtConfig : rtConfigs) {
      if (EqualsCheck.equals(rtConfig.getSelectionTree(), selTree)) {
        if (returnConfig == null) {
          returnConfig = rtConfig;
        } else {
          throw new ConstraintException(
              "There are multiple runtime configurations with identical selection trees!");
        }
      }
    }

    return returnConfig;
  }

  public List<IRuntimeConfiguration> lookUp(IProblemDefinition simProblem)
      throws SQLException {
    return Generics.changeListType(getEntities("problem_id="
        + simProblem.getID()));
  }

  public List<IRTConfigPerfProfile> lookUpPerformance() throws SQLException {
    ArrayList<IRTConfigPerfProfile> rtcpProfiles =
        new ArrayList<>();
    try (ResultSet rs = Databases.selectAllData(getConnection(), "perf_profiles_configs",
                            "config_id=" + getID())) {
      while (rs.next()) {
        rtcpProfiles.add(new RTConfigPerfProfile(rs
            .getLong(COLUMN_INDEX_PERF_PROFILE_CONF_ID), rs
            .getLong(COLUMN_INDEX_PERF_PROFILE_PERF_ID), rs
            .getDouble(COLUMN_INDEX_PERF_PROFILE_MIN_PERF), rs
            .getDouble(COLUMN_INDEX_PERF_PROFILE_AVG_PERF), rs
            .getDouble(COLUMN_INDEX_PERF_PROFILE_MAX_PERF), rs
            .getInt(COLUMN_INDEX_PERF_PROFILE_SAMPLE_SIZE), rs
            .getDouble(COLUMN_INDEX_PERF_PROFILE_STD_DEV)));
      }
    }
    return rtcpProfiles;
  }

  @Override
  protected String getAdditionalCreationSQL() {
    return ", FOREIGN KEY (problem_id) REFERENCES problems(id) ON DELETE CASCADE";
  }

  @Override
  public Date getIntroductionDate() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getVersion() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isUpToDate() {
    throw new UnsupportedOperationException();
  }

}

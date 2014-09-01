/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.jdbc;


import java.sql.Connection;

import org.jamesii.core.util.SimpleDataBaseEntityTest;
import org.jamesii.core.util.database.SimpleDataBaseEntity;
import org.jamesii.perfdb.jdbc.PerformanceDatabase;
import org.jamesii.simspex.util.DBConfiguration;


/**
 * Super class for all tests on the performance database.
 * 
 * @author Roland Ewald
 * 
 */
@Deprecated
public abstract class PerfDBTest<X extends SimpleDataBaseEntity<X>> extends
    SimpleDataBaseEntityTest<X> {

  /**
   * Reference to performance database.
   */
  PerformanceDatabase perfDB;

  public static PerformanceDatabase getPerfDB() throws Exception {
    return new PerformanceDatabase(DBConfiguration.getTestConnectionData());
  }

  public PerfDBTest() throws Exception {
    perfDB = getPerfDB();
  }

  @Override
  public Connection getConnection() throws Exception {
    return perfDB.getConnection();
  }

  @Override
  protected boolean isDeactivated() {
    return true;
  }
}

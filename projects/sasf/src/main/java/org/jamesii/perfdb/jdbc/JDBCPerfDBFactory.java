/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.jdbc;

import org.jamesii.core.data.DBConnectionData;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.perfdb.IPerformanceDatabase;
import org.jamesii.perfdb.plugintype.PerfDBFactory;


/**
 * Factory to create JDBC-based implementation.
 * 
 * @author Roland Ewald
 */
@Deprecated
// Use hibernate implementation instead, this is old code!
public class JDBCPerfDBFactory extends PerfDBFactory {

  private static final long serialVersionUID = 4735314009307116963L;

  @Override
  public IPerformanceDatabase create(ParameterBlock params) {
    PerfDBImplementation perfDB = new PerfDBImplementation();
    perfDB.init((DBConnectionData) params
        .getSubBlockValue(PerfDBFactory.CONNECTION_DATA));
    return perfDB;
  }

  @Override
  public int supportsParameters(ParameterBlock params) {
    return 0;
  }

}

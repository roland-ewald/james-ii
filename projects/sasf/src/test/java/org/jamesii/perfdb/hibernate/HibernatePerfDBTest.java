/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.hibernate;

import org.jamesii.core.data.DBConnectionData;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.perfdb.IPerformanceDatabase;
import org.jamesii.perfdb.PerfDBTest;
import org.jamesii.perfdb.hibernate.HibernatePerfDBFactory;
import org.jamesii.perfdb.hibernate.Performance;
import org.jamesii.perfdb.plugintype.PerfDBFactory;


/**
 * Overall unit test for {@link Performance}.
 * 
 * @author Roland Ewald
 * 
 */
public class HibernatePerfDBTest extends PerfDBTest {

  @Override
  public IPerformanceDatabase getDataBase(DBConnectionData dbConn)
      throws Exception {
    IPerformanceDatabase perfDB = (new HibernatePerfDBFactory())
        .create(new ParameterBlock(dbConn,
            PerfDBFactory.CONNECTION_DATA));
    perfDB.open();
    return perfDB;
  }

}

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
import org.jamesii.perfdb.plugintype.PerfDBFactory;


/**
 * Performance data base based on hibernate.
 * 
 * @author Roland Ewald
 * 
 */
public class HibernatePerfDBFactory extends PerfDBFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -6793171050687865687L;

  @Override
  public IPerformanceDatabase create(ParameterBlock params) {
    PerformanceDatabase perfDB =
        new PerformanceDatabase(
            (DBConnectionData) params
                .getSubBlockValue(PerfDBFactory.CONNECTION_DATA));
    perfDB.create();
    return perfDB;
  }

  @Override
  public int supportsParameters(ParameterBlock params) {
    return 1;
  }

}

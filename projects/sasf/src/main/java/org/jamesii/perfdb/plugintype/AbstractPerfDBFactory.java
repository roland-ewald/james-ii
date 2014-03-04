/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.plugintype;


import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.data.DBConnectionData;
import org.jamesii.core.factories.AbstractFilteringFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.logging.ApplicationLogger;
import org.jamesii.perfdb.IPerformanceDatabase;

/**
 * Abstract factory for performance database factories.
 * 
 * @author Roland Ewald
 */
public class AbstractPerfDBFactory extends
    AbstractFilteringFactory<PerfDBFactory> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 4401711199767724404L;

  /**
   * Creates a performance database instance with the default implementation.
   * 
   * @param connectionData
   *          the connection data
   * @return the performance database, or null if there is a problem
   */
  public static IPerformanceDatabase createDefaultPerformanceDatabase(
      DBConnectionData connectionData) {
    IPerformanceDatabase performanceDatabase = null;
    try {
      performanceDatabase =
          SimSystem
              .getRegistry()
              .getFactory(AbstractPerfDBFactory.class, null)
              .create(
                  new ParameterBlock(connectionData,
                      PerfDBFactory.CONNECTION_DATA));

      performanceDatabase.open();

      // This is necessary in case the Hibernate driver removes all
      // 'competing' loggers, as the one from HSQLDB does
      ApplicationLogger.reattachLogger();
    } catch (Exception ex) {
      SimSystem.report(Level.SEVERE,
          "Performance DB error: Error while trying to access database.", ex);
    }
    return performanceDatabase;
  }

}

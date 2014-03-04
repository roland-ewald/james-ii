/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.plugintype;

import org.jamesii.core.factories.Factory;
import org.jamesii.core.factories.IParameterFilterFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.perfdb.IPerformanceDatabase;


/**
 * Super class for all factories that provide implementations of a performance
 * database.
 * 
 * @see org.jamesii.core.data.DBConnectionData
 * 
 * @author Roland Ewald
 */
public abstract class PerfDBFactory extends Factory<IPerformanceDatabase> implements
IParameterFilterFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -4795942444489633744L;

  /**
   * Parameter name that holds connection data, type:
   * {@link org.jamesii.core.data.DBConnectionData}.
   */
  public static final String CONNECTION_DATA = "connData";

  /**
   * Create performance database.
   * 
   * @param absParameters
   *          database parameters, e.g. connection data
   * @return performance database
   * @throws Exception
   *           if creation of base failed (i.e., due to connection errors, etc.)
   */
  public abstract IPerformanceDatabase create(
      ParameterBlock params);

}

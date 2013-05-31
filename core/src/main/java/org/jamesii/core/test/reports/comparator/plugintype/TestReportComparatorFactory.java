/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.test.reports.comparator.plugintype;

import org.jamesii.core.factories.Factory;
import org.jamesii.core.factories.IParameterFilterFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.test.reports.comparator.ITestReportComparator;

/**
 * Base class for all factories, creating test report comparators.
 * 
 * @author Stefan Leye
 * 
 */
public abstract class TestReportComparatorFactory extends
    Factory<ITestReportComparator> implements IParameterFilterFactory {

  /**
   * The serialization ID.
   */
  private static final long serialVersionUID = -5013461390099441195L;

  /**
   * Creates a test comparator.
   * 
   * @param params
   *          the parameters
   * @return the comparator
   */
  @Override
  public abstract ITestReportComparator create(ParameterBlock params);
}
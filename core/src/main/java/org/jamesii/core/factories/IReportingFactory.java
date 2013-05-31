/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.factories;

import org.jamesii.core.data.report.IReport;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * An interface for creating objects of type I. In contrast to the inherited
 * factory method from the {@link org.jamesii.core.factories.IFactory} the
 * create method defined in here provides an extra attribute which allows to
 * pass an IReport instance.
 */
public interface IReportingFactory<I> extends IFactory<I> {

  /**
   * Creates an object of the type this factory has been created for (type
   * {@value I}). And reports that to the report passed.
   * 
   * @param report
   *          the report
   * @param parameters
   *          the parameters
   * 
   * @return the i
   */
  I create(IReport report, ParameterBlock parameters);

}

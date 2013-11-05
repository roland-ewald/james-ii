/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.resultreport.renderer.plugintype;

import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.resultreport.dataview.ResultDataView;


/**
 * The base factory for creating {@link IResultReportRenderer} objects.
 * 
 * @author Roland Ewald
 */
public abstract class ResultReportRendererFactory extends
    Factory<IResultReportRenderer> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -33124153455620908L;

  /**
   * Creates the corresponding result report renderer.
   * 
   * @param params
   *          the parameters
   * @return the result report renderer
   */
  @Override
  public abstract IResultReportRenderer create(ParameterBlock params);

  /**
   * Checks whether the renderer supports a certain data view.
   * 
   * @param dataView
   *          the data view in question
   * @return true, if it is supported
   */
  public abstract boolean supportsDataView(ResultDataView<?> dataView);

}

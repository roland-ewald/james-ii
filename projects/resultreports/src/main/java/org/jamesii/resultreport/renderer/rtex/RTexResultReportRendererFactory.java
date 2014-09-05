/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.resultreport.renderer.rtex;


import java.util.HashSet;
import java.util.Set;

import org.jamesii.core.factories.Context;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.resultreport.dataview.BoxPlotDataView;
import org.jamesii.resultreport.dataview.HistogramDataView;
import org.jamesii.resultreport.dataview.LineChartDataView;
import org.jamesii.resultreport.dataview.ResultDataView;
import org.jamesii.resultreport.dataview.ScatterPlotDataView;
import org.jamesii.resultreport.dataview.StatisticalTestDataView;
import org.jamesii.resultreport.dataview.TableDataView;
import org.jamesii.resultreport.renderer.plugintype.IResultReportRenderer;
import org.jamesii.resultreport.renderer.plugintype.ResultReportRendererFactory;

/**
 * Provides a renderer that generates RTex files.
 * 
 * @author Roland Ewald
 * 
 */
public class RTexResultReportRendererFactory extends
    ResultReportRendererFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -4695776782380298075L;

  /** The set of supported views. */
  private static final Set<Class<? extends ResultDataView<?>>> SUPPORTED_VIEWS =
      new HashSet<>();

  static {
    SUPPORTED_VIEWS.add(ScatterPlotDataView.class);
    SUPPORTED_VIEWS.add(BoxPlotDataView.class);
    SUPPORTED_VIEWS.add(HistogramDataView.class);
    SUPPORTED_VIEWS.add(LineChartDataView.class);
    SUPPORTED_VIEWS.add(TableDataView.class);
    SUPPORTED_VIEWS.add(StatisticalTestDataView.class);
  }

  @Override
  public IResultReportRenderer create(ParameterBlock params, Context context) {
    return new RTexResultReportRenderer();
  }

  @Override
  public boolean supportsDataView(ResultDataView<?> dataView) {
    return SUPPORTED_VIEWS.contains(dataView.getClass());
  }

  /**
   * Gets the set of supported view classes.
   * 
   * @return the supported view classes
   */
  protected static Set<Class<? extends ResultDataView<?>>> getSupportedViews() {
    return SUPPORTED_VIEWS;
  }

}

/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.resultreport.renderer.plugintype;

import org.jamesii.resultreport.ResultReportSection;
import org.jamesii.resultreport.dataview.ResultDataView;

/**
 * Base class for {@link IResultReportRenderer} implementations.
 * 
 * @author Roland Ewald
 */
public abstract class ResultReportRenderer implements IResultReportRenderer {

  @Override
  public void addDataViews(ResultReportSection section) {
    for (ResultDataView<?> view : section.getDataViews()) {
      displayData(view);
    }
  }
}

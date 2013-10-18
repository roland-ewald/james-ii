/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.resultreport.dataview;

/**
 * Super class for views that plot two-dimensional data.
 * 
 * @author Roland Ewald
 * 
 */
public abstract class ResultPlot2DDataView extends
    ResultPlotDataView<Double[][]> {

  /**
   * Instantiates a new result plot2 d data view.
   * 
   * @param theData
   *          the data
   * @param theCaption
   *          the caption
   * @param theTitle
   *          the title
   * @param theLabels
   *          the labels
   */
  public ResultPlot2DDataView(Double[][] theData, String theCaption,
      String theTitle, String[] theLabels) {
    super(theData, theCaption, theTitle, theLabels);
  }

}

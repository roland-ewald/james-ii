/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.resultreport.dataview;

/**
 * The super class for all data views that plot one-dimensional data.
 * 
 * @author Roland Ewald
 */
public class ResultPlot1DDataView extends ResultPlotDataView<Double[]> {

  /**
   * Instantiates a new result plot1 d data view.
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
  public ResultPlot1DDataView(Double[] theData, String theCaption,
      String theTitle, String[] theLabels) {
    super(theData, theCaption, theTitle, theLabels);
  }

}

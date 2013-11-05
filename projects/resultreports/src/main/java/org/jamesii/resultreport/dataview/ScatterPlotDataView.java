/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.resultreport.dataview;

/**
 * Data view for scatter plots.
 * 
 * @author Roland Ewald
 * 
 */
public class ScatterPlotDataView extends ResultPlot2DDataView {

  /**
   * Instantiates a new scatter plot data view.
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
  public ScatterPlotDataView(Double[][] theData, String theCaption,
      String theTitle, String[] theLabels) {
    super(theData, theCaption, theTitle, theLabels);
    if (theData.length != 2) {
      throw new IllegalArgumentException(
          "Scatter plot point pairs are not two-dimensional for plot '"
              + theTitle + "'.");
    }
  }
}

/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.resultreport.dataview;

import java.util.Arrays;

/**
 * Represents data to be plotted in a line chart.
 * 
 * @author Roland Ewald
 */
public class LineChartDataView extends ResultPlot2DDataView implements
    IProvideVariableNames {

  /** Contains the variable names for each row (theData[0], ..., theData[n]). */
  private final String[] varNames;

  /**
   * Instantiates a new line chart data view.
   * 
   * @param theData
   *          the data
   * @param theCaption
   *          the caption
   * @param theTitle
   *          the title
   * @param theLabels
   *          the labels
   * @param variableNames
   *          the variable names
   */
  public LineChartDataView(Double[][] theData, String theCaption,
      String theTitle, String[] theLabels, String[] variableNames) {
    super(theData, theCaption, theTitle, theLabels);
    varNames = Arrays.copyOf(variableNames, variableNames.length);
  }

  @Override
  public String[] getVariableNames() {
    return Arrays.copyOf(varNames, varNames.length);
  }

}

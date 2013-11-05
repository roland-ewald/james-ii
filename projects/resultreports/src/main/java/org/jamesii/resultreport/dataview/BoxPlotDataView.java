/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.resultreport.dataview;

import java.util.Arrays;

/**
 * Represents data for a box plot. the first dimension of the array contains the
 * arrays with the data per-variable, i.e. all values in the array data[0]
 * belong to a single variable.
 * 
 * @author Roland Ewald
 * 
 */
public class BoxPlotDataView extends ResultPlot2DDataView implements
    IProvideVariableNames {

  /** Contains the variable names for each row (theData[0], ..., theData[n]). */
  private final String[] varNames;

  /**
   * Instantiates a new box plot data view.
   * 
   * @param theData
   *          the data
   * @param theCaption
   *          the caption
   * @param theTitle
   *          the title
   * @param theLabels
   *          the array of labels
   * @param variableNames
   *          the variable names (list needs to be at least as long as the
   *          number of variables)
   */
  public BoxPlotDataView(Double[][] theData, String theCaption,
      String theTitle, String[] theLabels, String[] variableNames) {
    super(theData, theCaption, theTitle, theLabels);
    varNames = Arrays.copyOf(variableNames, variableNames.length);
    if (varNames.length < theData.length) {
      throw new IllegalArgumentException("Box plots contains data for "
          + theData.length + " variables, but only " + varNames.length
          + " variable names are given.");
    }
  }

  @Override
  public String[] getVariableNames() {
    return Arrays.copyOf(varNames, varNames.length);
  }

}

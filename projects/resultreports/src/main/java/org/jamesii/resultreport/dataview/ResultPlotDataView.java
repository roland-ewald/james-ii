/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.resultreport.dataview;

import java.util.Arrays;

/**
 * The Super class for all data views that plot the values.
 * 
 * @param <D>
 *          the type of data
 * 
 * 
 * @author Roland Ewald
 */
public class ResultPlotDataView<D> extends ResultDataView<D> {

  /** The title of the plot. */
  private final String title;

  /** The labels for the x-axes. */
  private final String[] labels;

  /**
   * Instantiates a new result plot 2D data view.
   * 
   * @param theData
   *          the data
   * @param theCaption
   *          the caption
   * @param theTitle
   *          the title
   * @param theLabels
   *          the axis labels
   */
  public ResultPlotDataView(D theData, String theCaption, String theTitle,
      String[] theLabels) {
    super(theData, theCaption);
    title = theTitle;
    labels = Arrays.copyOf(theLabels, theLabels.length);
  }

  public String getTitle() {
    return title;
  }

  public String[] getLabels() {
    return Arrays.copyOf(labels, labels.length);
  }

}

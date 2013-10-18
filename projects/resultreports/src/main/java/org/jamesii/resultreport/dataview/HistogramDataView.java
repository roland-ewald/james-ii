/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.resultreport.dataview;

/**
 * Represents data to be plotted in a histogram.
 * 
 * @author Roland Ewald
 * 
 */
public class HistogramDataView extends ResultPlot1DDataView {

  /**
   * The variable specifying the bins. Its potential settings depend on the
   * report renderer.
   */
  private final String breakSpec;

  /**
   * Instantiates a new histogram data view.
   * 
   * @param theData
   *          the the data
   * @param theCaption
   *          the the caption
   * @param theTitle
   *          the the title
   * @param theXLabel
   *          the the x label
   * @param theYLabel
   *          the the y label
   * @param breakSpecification
   *          the break specification (see R reference manual for 'hist',
   *          parameter 'break')
   */
  public HistogramDataView(Double[] theData, String theCaption,
      String theTitle, String theXLabel, String theYLabel,
      String breakSpecification) {
    super(theData, theCaption, theTitle, new String[] { theXLabel, theYLabel });
    breakSpec = breakSpecification;
  }

  /**
   * Simplifying constructor (default histogram break behavior).
   * 
   * @param theData
   *          the the data
   * @param theCaption
   *          the the caption
   * @param theTitle
   *          the the title
   * @param theXLabel
   *          the the x label
   * @param theYLabel
   *          the the y label
   */
  public HistogramDataView(Double[] theData, String theCaption,
      String theTitle, String theXLabel, String theYLabel) {
    this(theData, theCaption, theTitle, theXLabel, theYLabel, "");
  }

  public String getBreakSpec() {
    return breakSpec;
  }

}

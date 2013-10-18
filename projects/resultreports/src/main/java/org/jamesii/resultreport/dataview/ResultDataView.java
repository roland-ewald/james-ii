/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.resultreport.dataview;

/**
 * The atomic component of a result report.
 * 
 * @author Roland Ewald
 * 
 * @param <D>
 *          the type of the data to be displayed
 */
public abstract class ResultDataView<D> {

  /** The data to be displayed. */
  private final D data;

  /** The caption explaining the data displayed. */
  private final String caption;

  public ResultDataView(D theData, String theCaption) {
    data = theData;
    caption = theCaption;
  }

  /**
   * Gets the data.
   * 
   * @return the data
   */
  public D getData() {
    return data;
  }

  public String getCaption() {
    return caption;
  }

}

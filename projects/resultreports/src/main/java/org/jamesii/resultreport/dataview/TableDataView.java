/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.resultreport.dataview;

/**
 * Displays a table of data.
 * 
 * @author Roland Ewald
 * 
 */
public class TableDataView extends ResultDataView<String[][]> {

  /**
   * Instantiates a new table data view.
   * 
   * @param theData
   *          the table data
   * @param theCaption
   *          the caption
   */
  public TableDataView(String[][] theData, String theCaption) {
    super(theData, theCaption);
  }

}

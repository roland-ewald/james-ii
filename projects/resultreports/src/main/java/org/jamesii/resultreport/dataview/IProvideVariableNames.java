/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.resultreport.dataview;

/**
 * Interface for all data views that may hold data of multiple variables.
 * 
 * @author Roland Ewald
 * 
 */
public interface IProvideVariableNames {

  /**
   * Gets the names of the variables (in appropriate order, as defined by the
   * data view).
   * 
   * @return the array of variable names
   */
  String[] getVariableNames();

}

/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.resultreport.dataview;

/**
 * Allows to specify which kind of statistical test shall be carried out for the
 * given data. A specific test may or may not be supported by a certain
 * renderer. In the latter case, the renderer has to throw an exception.
 * 
 * @author Roland Ewald
 * 
 */
public enum StatisticalTestDefinition {

  /** The (two-sample) Kolmogorov-Smirnov test. */
  KOLMOGOROV_SMIRNOV;

  public String getID() {
    switch (this) {

    case KOLMOGOROV_SMIRNOV:
      return "ks";

    default:
      return toString();
    }

  }
}

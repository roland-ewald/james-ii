/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.resultreport.renderer.rtex;

/**
 * Type of the {@link RTexReportElement}.
 * 
 * @author Roland Ewald
 */
public enum RTexElementType {

  /** This is a data view. */
  DATA_VIEW,
  
  /** This is a chapter. */
  CHAPTER,

  /** This is the beginning of a section. */
  SECTION,

  /** This is the beginning of a sub-section. */
  SUB_SECTION,

  /** This is the beginning of a sub-sub-section. */
  SUB_SUB_SECTION
}

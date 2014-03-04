/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.util;

import org.jamesii.core.util.exceptions.OperationNotSupportedException;

/**
 * The types of benchmark models currently distinguished. Is used by
 * {@link DatabaseUtils} to en/decode type of
 * {@link org.jamesii.perfdb.entities.IProblemScheme}.
 * 
 * @see org.jamesii.perfdb.entities.IProblemScheme
 * 
 * @author Roland Ewald
 */
public enum BenchmarkModelType {

  /** This is a synthetic benchmark. It focuses on one aspect. */
  SYNTHETIC,

  /** This is a common benchmark. It is an often-used scenario. */
  COMMON,

  /** This is an application benchmark. It is a real-world application. */
  APPLICATION;

  /** String representation for synthetic benchmark models in the database. */
  public static final String STRING_REP_SYNTHETIC = "S";

  /** String representation for application benchmark models in the database. */
  public static final String STRING_REP_APPLICATION = "A";

  /** String representation for common benchmark models in the database. */
  public static final String STRING_REP_COMMON = "C";

  @Override
  public String toString() {
    switch (this) {
    case SYNTHETIC:
      return STRING_REP_SYNTHETIC;
    case APPLICATION:
      return STRING_REP_APPLICATION;
    case COMMON:
      return STRING_REP_COMMON;
    }
    throw new OperationNotSupportedException();
  }

  /**
   * Get benchmark model type from string representation.
   * 
   * @param stringRep
   *          string representation
   * @return appropriate benchmark model type, null if none can be found.
   */
  protected static BenchmarkModelType parseBMT(String stringRep) {
    if (stringRep.equalsIgnoreCase(STRING_REP_APPLICATION)) {
      return BenchmarkModelType.APPLICATION;
    }
    if (stringRep.equalsIgnoreCase(STRING_REP_SYNTHETIC)) {
      return BenchmarkModelType.SYNTHETIC;
    }
    if (stringRep.equalsIgnoreCase(STRING_REP_COMMON)) {
      return BenchmarkModelType.COMMON;
    }
    return null;
  }

}
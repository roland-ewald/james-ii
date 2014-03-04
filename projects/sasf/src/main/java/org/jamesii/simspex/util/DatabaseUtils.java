/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.util;


/**
 * Database utility functions.
 * 
 * @author Roland Ewald
 * 
 */
public final class DatabaseUtils {

  /**
   * Should not be instantiated.
   */
  private DatabaseUtils() {
  }

  /**
   * The default prefix for problem schemes as used for problems with simulation
   * algorithms.
   */
  private static final String DEFAULT_PREFIX = DatabaseUtils.class.getName()
      + ":";

  /**
   * Gets the benchmark model type.
   * 
   * @param benchmarkModelType
   *          the benchmark model type
   * @return the string presentation of the type as a problem scheme type (see
   * @see org.jamesii.perfdb.entities.IProblemScheme
   */
  public static String convertModelTypeToSchemeType(
      BenchmarkModelType benchmarkModelType) {
    return DEFAULT_PREFIX + benchmarkModelType.toString();
  }

  /**
   * Get benchmark model type from a scheme type.
   * 
   * @param stringRepresentation
   *          string representation
   * @return appropriate benchmark model type, null if none can be found.
   */
  public static BenchmarkModelType convertSchemeTypeToModelType(
      String stringRepresentation) {
    String[] elements = stringRepresentation.split(":");
    if (elements.length != 2) {
      return null;
    }
    return BenchmarkModelType.parseBMT(elements[1]);
  }

}

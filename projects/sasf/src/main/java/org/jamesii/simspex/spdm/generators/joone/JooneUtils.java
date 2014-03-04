/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.generators.joone;

import java.util.Map;

/**
 * Auxiliary function for using Joone.
 * 
 * @author Roland Ewald
 * 
 */
final class JooneUtils {

  /**
   * Should not be instantiated.
   */
  private JooneUtils() {
  }

  /**
   * Calculates attribute, set -1 if attribute is missing in map.
   * 
   * @param attribName
   *          name of the attribute
   * @param valueMap
   *          mapping of the current tuple's values
   * @return double value
   */
  static double calculateAttribute(String attribName,
      Map<String, Object> valueMap, Map<String, Double> valEncoding) {

    // Missing values are encoded with -1
    if (!valueMap.containsKey(attribName)) {
      return -1;
    }

    Object value = valueMap.get(attribName);
    if (value instanceof String) {
      if (!valEncoding.containsKey(value)) {
        throw new IllegalArgumentException("'" + value
            + "' could not be decoded for attribute '" + attribName + "'.");
      }
      return valEncoding.get(value);
    } else if (value instanceof Number) {
      return ((Number) value).doubleValue();
    }

    throw new IllegalArgumentException("Attribute '" + attribName
        + "' is neither String nor Numerical.");
  }

}

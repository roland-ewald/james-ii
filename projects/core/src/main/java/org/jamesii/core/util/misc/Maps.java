/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.misc;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Some auxiliary methods to work with {@link Map} instance.
 * 
 * @author Roland Ewald
 */
public class Maps {

  /**
   * Should not be instantiated.
   */
  private Maps() {
  }

  /**
   * Extract (key,value) pairs from an existing map into a new map for all pairs
   * where the key have a given prefix. The prefix will be removed during
   * extraction. This may be useful for propagating parameters to certain
   * sub-components etc.
   * 
   * @param <V>
   *          the value type
   * @param parameters
   *          the parameters
   * @param prefix
   *          the prefix
   * @return the map containing the extracted (key,value) pairs
   */
  public static <V> Map<String, V> extractKeysByPrefix(
      Map<String, V> parameters, String prefix) {
    Map<String, V> extractedParams = new HashMap<>();
    for (Entry<String, V> parameter : parameters.entrySet()) {
      if (parameter.getKey().startsWith(prefix)) {
        extractedParams.put(parameter.getKey().substring(prefix.length()),
            parameter.getValue());
      }
    }
    return extractedParams;
  }

  /**
   * Gets a parameter value from a map if exists, otherwise falls back to a
   * default value.
   * 
   * @param paramMap
   *          the parameter map
   * @param key
   *          the parameter name
   * @param defaultValue
   *          the default value
   * 
   * @return the value that shall be used
   */
  @SuppressWarnings("unchecked")
  public static <O> O getValue(Map<String, ?> paramMap, String key,
      O defaultValue) {
    return paramMap.containsKey(key) ? (O) paramMap.get(key) : defaultValue;
  }

}

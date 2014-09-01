/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.exploration.utils;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jamesii.core.model.variables.BaseVariable;

/**
 * Randomly samples model setups. Repeated calls will yield *different*
 * parameter settings.
 * 
 * @author Roland Ewald
 */
public class RandomModelSetupSampler {

  /** The set containing all past model setups. */
  private Set<String> pastModelSetups = new HashSet<>();

  /**
   * Sample a new model setup.
   * 
   * @param modelVariables
   *          the model variables
   * @return the map containing
   */
  public Map<String, Serializable> sampleSetup(
      Collection<BaseVariable<?>> modelVariables) {

    boolean newSetupFound = false;
    Map<String, Serializable> result = null;

    if (modelVariables.isEmpty()) {
      throw new IllegalArgumentException(
          "At least one model variable has to be defined.");
    }

    while (!newSetupFound) {
      result = new HashMap<>();
      for (BaseVariable<?> variable : modelVariables) {
        variable.setRandomValue();
        result.put(variable.getName(), (Serializable) variable.getValue());
      }
      String setupDescription = getDescription(result);
      if (!pastModelSetups.contains(setupDescription)) {
        newSetupFound = true;
        pastModelSetups.add(setupDescription);
      }
    }
    return result;
  }

  /**
   * Gets the textual description of a result.
   * 
   * @param result
   *          the result
   * @return the description
   */
  public static String getDescription(Map<String, Serializable> result) {
    List<String> variableNames = new ArrayList<>(result.keySet());
    Collections.sort(variableNames);
    StringBuilder strBuf = new StringBuilder();
    for (String varName : variableNames) {
      strBuf.append(varName);
      strBuf.append('=');
      strBuf.append(result.get(varName));
      strBuf.append(';');
    }
    return strBuf.toString();
  }

}

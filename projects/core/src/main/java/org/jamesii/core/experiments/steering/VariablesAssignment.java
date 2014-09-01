/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.steering;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.jamesii.core.model.variables.IQuantitativeVariable;
import org.jamesii.core.util.misc.Clone;

/**
 * Mapping (Experiment Variable Name) -> value to be set. Objects of this type
 * are used by the {@link IExperimentSteerer} implementations to.
 * 
 * TODO make sure hashCode computation stays the same over lifetime.
 * 
 * @author Roland Ewald
 */
public class VariablesAssignment extends LinkedHashMap<String, Serializable> {

  /** Serialisation ID. */
  private static final long serialVersionUID = -230384210880251393L;

  /**
   * Gets the variable names.
   * 
   * @return a set containing the variable names
   */
  public Set<String> getVariableNames() {
    return new HashSet<>(keySet());
  }

  /**
   * Check if all there are only quantitative variables.
   * 
   * @return true if all variables are quantitative
   */
  public boolean onlyQuantitativeVars() {
    for (String name : getVariableNames()) {
      if (!(get(name) instanceof IQuantitativeVariable<?>)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder("variable assignment:\t");
    for (Entry<String, Serializable> variable : entrySet()) {
      result.append(variable.getKey() + "= " + variable.toString() + "\t");
    }
    return result.toString();
  }

  @Override
  public VariablesAssignment clone() {
    VariablesAssignment result = new VariablesAssignment();
    for (Entry<String, Serializable> variable : entrySet()) {
      try {
        result.put(variable.getKey(),
            Clone.cloneSerializable(variable.getValue()));
      } catch (Exception ex) {
      }
    }
    return result;
  }
}

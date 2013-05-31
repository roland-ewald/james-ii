/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization.parameter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jamesii.core.model.variables.BaseVariable;

/**
 * Base class for model variables. All variables in this map have to have unique
 * names, but they might of arbitrary "type".
 * 
 * This is mostly a convenience wrapper for a normal {@link HashMap}, but it
 * provides some methods in addition which may make life easier.
 * 
 * 
 * @author Jan Himmelspach (based on ideas from Arvid Schwecke)
 */
public class Configuration implements Serializable, Cloneable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 4029598839262942414L;

  /** The names list. */
  private List<String> namesList = new ArrayList<>();

  /** Map storing the actual base variable. */
  private Map<String, BaseVariable<?>> content = new HashMap<>();

  /**
   * Instantiates a new configuration.
   */
  public Configuration() {
  }

  /**
   * Instantiates a new configuration with given initial content.
   * 
   * @param initialContent
   *          the initial content
   */
  public Configuration(Map<String, BaseVariable<?>> initialContent) {
    content.putAll(initialContent);
    namesList.addAll(initialContent.keySet());
  }

  @Override
  public Configuration clone() throws CloneNotSupportedException {
    Configuration result = new Configuration();
    for (BaseVariable<?> variable : content.values()) {
      result.put(variable.getName(), variable.copyVariable());
    }
    return result;
  }

  /**
   * Check if all factors are variables of the given "super type", e.g., of
   * IQuantitativeVariable.
   * 
   * @param superType
   *          - the super type to check all factors for
   * 
   * @return true if all factors are quantitative variables
   */
  public boolean factorsInstanceof(Class<?> superType) {
    for (BaseVariable<?> variable : content.values()) {
      if (!superType.isAssignableFrom(variable.getClass())) {
        return false;
      }
    }
    return true;
  }

  @Override
  public String toString() {
    StringBuffer result = new StringBuffer("Configuration:\t");
    for (BaseVariable<?> bv : content.values()) {
      result.append(bv.getName() + "= " + bv.getValue() + " -- ");
    }
    return result.toString();
  }

  @Override
  public boolean equals(Object compared) {

    if (!(compared instanceof Configuration)) {
      return false;
    }

    return this.toString().matches(((Configuration) compared).toString());

  }

  @Override
  public int hashCode() {
    return this.toString().hashCode();
  }

  /**
   * Removes the.
   * 
   * @param key
   *          the key
   * 
   * @return the base variable<?>
   */
  public BaseVariable<?> remove(Object key) {
    BaseVariable<?> result = content.remove(key);

    // don't remove the entry from the namesList, because otherwise the indices
    // will be wrong later on (in using classes)
    // namesList.remove(key.toString());

    return result;
  }

  /**
   * Put.
   * 
   * @param key
   *          the key
   * @param value
   *          the value
   * 
   * @return the base variable<?>
   */
  public BaseVariable<?> put(String key, BaseVariable<?> value) {
    BaseVariable<?> old = content.put(key, value);

    namesList.add(key);

    return old;
  }

  /**
   * Get the set of variable values.
   * 
   * @return the set of variable values
   */
  public Collection<BaseVariable<?>> values() {
    return content.values();
  }

  /**
   * Get the set of variable names.
   * 
   * @return the set of variable names
   */
  public Set<String> keySet() {
    return content.keySet();
  }

  /**
   * The size of the configuration.
   * 
   * @return the number of factors
   */
  public int size() {
    return content.size();
  }

  /**
   * Gets the variable with the corresponding key.
   * 
   * @param key
   *          the key
   * 
   * @return the base variable<?>
   */
  public BaseVariable<?> get(String key) {
    return content.get(key);
  }

  /**
   * Gets the variable with the given index.
   * 
   * @param index
   *          the index
   * 
   * @return the base variable<?>
   */
  public BaseVariable<?> get(Integer index) {
    return content.get(namesList.get(index));
  }

}

/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.parsetree.variables;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * An environment is a level on a function "stack". I.e., as soon as at least
 * one identifier (variable) is used we need an environment from which the value
 * of the identifier/variable can be retrieved.
 * 
 * 
 * @author Jan Himmelspach
 * @param <K>
 *          the type of the keys (usually "names", i.e., strings will be used
 *          here) used to identify variables
 * 
 */
public class Environment<K extends Serializable> implements IEnvironment<K> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 2350493934011134914L;

  /**
   * The list of values stored in this environment.
   */
  private Map<K, Object> values = new HashMap<>();

  /**
   * Instantiates a new environment. To change or set values later use
   * {@link #setValue(Object, Object)}
   */
  public Environment() {
    this(null);
  }

  /**
   * Instantiates a new environment and populates it with the given values. To
   * change or set more values later use {@link #setValue(Object, Object)}
   * 
   * @param values
   *          the values to populate
   */
  public Environment(Map<K, Object> values) {
    super();
    if (values != null) {
      this.getValues().putAll(values);
    }
  }

  @Override
  public Object getValue(K ident) {
    return getValues().get(ident);
  }

  @Override
  public void setValue(K ident, Object value) {
    getValues().put(ident, value);
  }

  @Override
  public boolean containsIdent(K ident) {
    return getValues().containsKey(ident);
  }

  @Override
  public void removeValue(K ident) {
    getValues().remove(ident);
  }

  @Override
  public void removeAll() {
    getValues().clear();
  }

  /**
   * @return the values
   */
  protected final Map<K, Object> getValues() {
    return values;
  }

  /**
   * @param values
   *          the values to set
   */
  protected final void setValues(Map<K, Object> values) {
    this.values = values;
  }

}

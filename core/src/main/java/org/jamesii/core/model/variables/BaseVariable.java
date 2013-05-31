/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.model.variables;

import org.jamesii.SimSystem;
import org.jamesii.core.base.NamedEntity;

/**
 * An observable variable.
 * 
 * @param <T>
 *          the type of the internal variable
 * 
 * @author Jan Himmelspach
 */
public class BaseVariable<T> extends NamedEntity implements IVariable<T> {

  /** Serialisation ID. */
  private static final long serialVersionUID = 5804775075878830711L;

  /**
   * The internal variable (the variable the content of the variable type is
   * stored in).
   */
  private T value;

  /**
   * Instantiates a new base variable.
   */
  public BaseVariable() {
    super();
  }

  /**
   * The Constructor.
   * 
   * @param name
   *          the name
   */
  public BaseVariable(String name) {
    super(name);
  }

  /**
   * Copy variable.
   * 
   * @return the base variable< t>
   */
  @SuppressWarnings("unchecked")
  public BaseVariable<T> copyVariable() {
    BaseVariable<T> newVar = null;
    try {
      newVar = this.getClass().newInstance();
      newVar.setName(this.getName());
      newVar.setValue(this.getValue());
    } catch (Exception ex) {
      SimSystem.report(ex);
    }
    return newVar;
  }

  /**
   * Read the internal variable and return the value.
   * 
   * @return variable value
   */
  @Override
  public T getValue() {
    return value;
  }

  /**
   * Set the internal variable to the given value.
   * 
   * @param value
   *          the value
   */
  @Override
  public void setValue(T value) {
    this.value = value;
    changed();
  }

  @Override
  public String toString() {
    return getName() + " = " + value;
  }

  /**
   * Sets the random value.
   */
  public void setRandomValue() {
    report("WARNING, setRandomValue was called for a Variable, that is neither defined as Quantative nor Qualatative BaseVariable.");
    report("This should not be, all Optimization Variables have to be of one of these two types, if random values are necessary.");
    report("Variable was " + getName());
  }
}

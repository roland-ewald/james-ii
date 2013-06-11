/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.editable;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jamesii.core.model.variables.IVariable;
import org.jamesii.core.util.IConstraint;

/**
 * Basic class for editable variables.
 * 
 * Created: 23.05.2004
 * 
 * @param <V>
 *          the type of the variable to be edited
 * 
 * @author Roland Ewald
 */
public class Editable<V> extends ConstrainableVariable<V> implements
    IEditable<V> {

  /** Serialisation ID. */
  private static final long serialVersionUID = -3509438105927926271L;

  /** Structure with all attribute - variables of this variable. */
  private Map<String, IEditable<?>> attributes = new Hashtable<>();

  /** Default value of the parameter. */
  private V defaultValue = null;

  /** Saves documentation for this variable. */
  private String documentation = "";

  /** Structure with all sub-elements of this parameter. */
  private IEditable<?> subVariable = null;

  /**
   * Default constructor.
   * 
   * @param var
   *          variable to be edited
   */
  public Editable(IVariable<V> var) {
    super(var);
  }

  /**
   * Gets the all attributes.
   * 
   * @return the all attributes
   * 
   * @see org.jamesii.gui.utils.parameters.editable.IEditable#getAllAttributes()
   */
  @Override
  public Map<String, IEditable<?>> getAllAttributes() {
    return attributes;
  }

  /**
   * Gets the attribute.
   * 
   * @param n
   *          the n
   * 
   * @return the attribute
   * 
   * @see org.jamesii.gui.utils.parameters.editable.IEditable#getAttribute(java.lang.String)
   */
  @Override
  public IEditable<?> getAttribute(String n) {
    return attributes.get(n);
  }

  /**
   * Get copy of variable.
   * 
   * @return copy of variable
   * 
   * @throws CopyNotSupportedException
   *           if copying goes wrong
   */
  @Override
  public IEditable<V> getCopy() {

    // TODO: Copy variable
    Editable<V> newParameter = new Editable<>(getVariable());
    newParameter.setDefaultValue(getDefaultValue());
    newParameter.setName(getName());
    newParameter.setValue(getValue());

    // Clone constraints
    List<IConstraint<V>> checkers = getConstraints();
    for (IConstraint<V> checker : checkers) {
      newParameter.addConstraint(checker.getCopy());
    }

    // clone content
    if (subVariable != null) {
      newParameter.setSubVariable(subVariable.getCopy());
    }

    // clone attributes
    Map<String, IEditable<?>> attribs = getAllAttributes();
    for (Entry<String, IEditable<?>> entry : attribs.entrySet()) {
      String key = entry.getKey();
      newParameter.setAttribute(key, entry.getValue().getCopy());
    }

    newParameter.setDocumentation(getDocumentation());

    return newParameter;
  }

  /**
   * Gets the default value.
   * 
   * @return the default value
   * 
   * @see org.jamesii.gui.utils.parameters.editable.IEditable#getDefaultValue()
   */
  @Override
  public V getDefaultValue() {
    return defaultValue;
  }

  /**
   * Gets the documentation.
   * 
   * @return the documentation
   * 
   * @see org.jamesii.gui.utils.parameters.editable.IEditable#getDocumentation()
   */
  @Override
  public String getDocumentation() {
    return documentation;
  }

  /**
   * Gets the string value.
   * 
   * @return the string value
   * 
   * @see org.jamesii.gui.utils.parameters.editable.IEditable#getStringValue()
   */
  @Override
  public String getStringValue() {
    V value = getValue();
    if (value != null) {
      return value.toString();
    } else if (subVariable != null) {
      return subVariable.getStringValue();
    } else {
      return "";
    }
  }

  @Override
  public IEditable<?> getSubVariable() {
    return subVariable;
  }

  /**
   * Checks for sub variable.
   * 
   * @return true, if checks for sub variable
   * 
   * @see org.jamesii.gui.utils.parameters.editable.IEditable#hasSubVariable()
   */
  @Override
  public boolean hasSubVariable() {
    return subVariable != null;
  }

  /**
   * Checks if is complex.
   * 
   * @return true, if checks if is complex
   * 
   * @see org.jamesii.gui.utils.parameters.editable.IEditable#isComplex()
   */
  @Override
  public boolean isComplex() {
    return (hasSubVariable() || attributes.size() > 0);
  }

  @Override
  public void setAttribute(String name, IEditable<?> attribute) {
    attributes.put(name, attribute);
  }

  /**
   * Sets the default value.
   * 
   * @param newDefaultValue
   *          the new default value
   * 
   * @see org.jamesii.gui.utils.parameters.editable.IEditable#setDefaultValue(java.lang.Object)
   */
  @Override
  public void setDefaultValue(V newDefaultValue) {
    if (check(newDefaultValue)) {
      defaultValue = newDefaultValue;
      if (getValue() == null) {
        setValue(defaultValue);
      }
    }
  }

  /**
   * Sets the documentation.
   * 
   * @param doc
   *          the doc
   * 
   * @see org.jamesii.gui.utils.parameters.editable.IEditable#setDocumentation(java.lang.String)
   */
  @Override
  public void setDocumentation(String doc) {
    this.documentation = doc;
  }

  /**
   * Sets the sub variable.
   * 
   * @param subVar
   *          the sub var
   * 
   * @see org.jamesii.gui.utils.parameters.editable.IEditable#setSubVariable(org.jamesii.gui.utils.parameters.editable.IEditable)
   */
  @Override
  public void setSubVariable(IEditable<?> subVar) {
    subVariable = subVar;
  }

  /**
   * Sets the value.
   * 
   * @param newValue
   *          the new value
   * 
   * @see org.jamesii.gui.utils.parameters.editable.IEditable#setValue(java.lang.Object)
   */
  @Override
  public void setValue(V newValue) {
    if (this.check(newValue)) {
      getVariable().setValue(newValue);
    }
  }

  @Override
  public boolean isDeletable() {
    return false;
  }

}

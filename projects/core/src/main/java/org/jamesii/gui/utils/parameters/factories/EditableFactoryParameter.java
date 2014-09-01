/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.factories;

import org.jamesii.SimSystem;
import org.jamesii.core.plugins.IParameter;
import org.jamesii.core.util.IConstraint;
import org.jamesii.core.util.misc.Strings;
import org.jamesii.gui.utils.parameters.editable.CopyNotSupportedException;
import org.jamesii.gui.utils.parameters.editable.IEditable;
import org.jamesii.gui.utils.parameters.editor.plugintype.AbstractParamEditorFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * This is a generic object that integrates the {@link IParameter} objects from
 * the factories framework to the parameter editing framework of the GUI.
 * 
 * @param <V>
 *          the type of the object to be edited
 * 
 * @author Roland Ewald
 * 
 */
public class EditableFactoryParameter<V> implements IEditable<V> {

  /**
   * Parameter type to be edited.
   */
  private Class<V> paramterType;

  /**
   * The factories' parameter.
   */
  private IParameter parameter;

  /**
   * The default value (null if not available).
   */
  private V defaultVal = null;

  /**
   * The value.
   */
  private V value = null;

  /**
   * Empty map for non-existing attributes.
   */
  private Map<String, IEditable<?>> attributes = new HashMap<>();

  /**
   * Constraints for that variable.
   */
  private List<IConstraint<V>> constraints = new ArrayList<>();

  /**
   * Default constructor.
   * 
   * @param paramType
   *          the type of the parameter to be edited
   * @param param
   *          the parameter
   * @param defaultValue
   *          its default value
   * @throws InstantiationException
   *           if default value is null, the empty constructor will be used to
   *           create one. if none exists, this exception will be thrown
   * @throws IllegalAccessException
   *           if empty constructor cannot be accessed, e.g., because it is
   *           private
   */
  public EditableFactoryParameter(Class<V> paramType, IParameter param,
      V defaultValue) throws InstantiationException, IllegalAccessException {
    paramterType = paramType;
    parameter = param;
    defaultVal = defaultValue;

    if (defaultVal == null) {
      if (param.getDefaultValue() != null) {
        defaultVal = Strings.stringToValue(paramType, param.getDefaultValue());
      }
    }

    value = defaultVal;

    // If default is null: create new instance
    if (value == null) {
      if (AbstractParamEditorFactory.isSupportedPrimitive(paramType)) {
        defaultValue =
            AbstractParamEditorFactory.getDefaultForPrimitive(paramType);
      } else if (!paramType.isInterface()) {
        try {
          defaultValue = paramType.newInstance();
        } catch (Exception ex) {
          SimSystem.report(Level.WARNING, "Could not instantiate type '"
              + paramType + "' with empty constructor.", ex);
        }
      }
      value = defaultValue;
    }
  }

  @Override
  public Map<String, IEditable<?>> getAllAttributes() {
    return attributes;
  }

  @Override
  public IEditable<?> getAttribute(String name) {
    return null;
  }

  @Override
  public IEditable<V> getCopy() {
    throw new CopyNotSupportedException();
  }

  @Override
  public V getDefaultValue() {
    return defaultVal;
  }

  @Override
  public String getDocumentation() {
    return parameter.getDescription();
  }

  @Override
  public String getStringValue() {
    return value.toString();
  }

  @Override
  public IEditable<?> getSubVariable() {
    return null;
  }

  @Override
  public boolean hasSubVariable() {
    return false;
  }

  @Override
  public boolean isComplex() {
    return false;
  }

  @Override
  public void setAttribute(String name, IEditable<?> attribute) {
    // Don't do anything, there are no attributes
  }

  @Override
  public void setDefaultValue(V defaultValue) {
    defaultVal = defaultValue;
  }

  @Override
  public void setDocumentation(String documentation) {
    // The documentation must not be changed
  }

  @Override
  public void setSubVariable(IEditable<?> variable) {
  }

  @Override
  public void addConstraint(IConstraint<V> constraint) {
    constraints.add(constraint);

  }

  @Override
  public boolean check() {
    return check(value);
  }

  @Override
  public boolean check(V val) {
    for (IConstraint<V> constraint : constraints) {
      if (!constraint.isFulfilled(val)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean checkImmediately() {
    return true;
  }

  @Override
  public List<IConstraint<V>> getConstraints() {
    return constraints;
  }

  @Override
  public void removeConstraint(IConstraint<V> constraint) {
    constraints.remove(constraint);
  }

  @Override
  public void setCheckImmediately(boolean immediateCheck) {
    // TODO Auto-generated method stub
  }

  @Override
  public V getValue() {
    return value;
  }

  @Override
  public void setValue(V val) {
    value = val;
  }

  @Override
  public String getName() {
    return parameter.getName();
  }

  @Override
  public void setName(String name) {
    // Don't allow setting another name - the parameter's name is the key in the
    // parameter block and must *not* be changed.
  }

  @Override
  public boolean isDeletable() {
    return !parameter.isRequired();
  }

}

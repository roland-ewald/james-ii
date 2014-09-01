/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.factories.propertyprovider;

import org.jamesii.gui.utils.objecteditor.property.AbstractProperty;
import org.jamesii.gui.utils.parameters.factories.FactoryParameters;

/**
 * Internally used class representing a factory parameter property used by
 * {@link FactoryParameters}.
 * 
 * @author Stefan Rybacki
 */
class FactoryParameterProperty extends AbstractProperty {
  /**
   * Instantiates a new factory parameter property.
   * 
   * @param name
   *          the properties' name
   * @param type
   *          the properties' type
   */
  public FactoryParameterProperty(String name, Class<?> type) {
    super(name, type);
  }

  @Override
  public Object getValue(Object parent) {
    if (!(parent instanceof FactoryParameters)) {
      return null;
    }
    return ((FactoryParameters) parent).getParameter(getName());
  }

  @Override
  public boolean isWritable() {
    return true;
  }

  @Override
  public void setValue(Object parent, Object value) {
    if (!(parent instanceof FactoryParameters)) {
      return;
    }

    ((FactoryParameters) parent).setParameter(getName(), value);
  }

}

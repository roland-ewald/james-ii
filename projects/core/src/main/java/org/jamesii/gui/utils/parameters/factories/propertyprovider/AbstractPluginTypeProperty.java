/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.factories.propertyprovider;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.utils.objecteditor.property.AbstractProperty;
import org.jamesii.gui.utils.parameters.factories.FactoryParameters;
import org.jamesii.gui.utils.parameters.factories.PluginTypeParameters;

/**
 * Abstract class that represents a plugin type parameter concrete plugin type
 * parameter implementations (List, Map, Direct) should extend this.
 * 
 * @author Stefan Rybacki
 */
public abstract class AbstractPluginTypeProperty extends AbstractProperty {

  /** The type. */
  private Class<?> type;

  /** The base factory. */
  private String baseFactory;

  /**
   * Instantiates a new abstract plugin type property.
   * 
   * @param name
   *          the name
   * @param plugintype
   *          the plugin type
   * @param type
   *          the type
   */
  public AbstractPluginTypeProperty(String name, Class<?> plugintype,
      Class<?> type) {
    super(name, type);
    this.type = type;
    this.baseFactory = plugintype.getName();
  }

  /**
   * Sets the type.
   * 
   * @param type
   *          the new type
   */
  protected final void setType(Class<?> type) {
    this.type = type;
  }

  protected final String getBaseFactory() {
    return baseFactory;
  }

  @Override
  public final Class<?> getType() {
    return type;
  }

  @Override
  public Object getValue(Object parent) {
    if (!(parent instanceof FactoryParameters)) {
      return null;
    }

    // read parameterblock used to set up the plugintype
    ParameterBlock block =
        ((FactoryParameters) parent).getPluginTypeParameter(getName());

    if (block == null) {
      block = new ParameterBlock();
    }

    PluginTypeParameters pluginTypeParameters = getPluginTypeParameters(block);

    ((FactoryParameters) parent).setPluginTypeParameter(getName(),
        pluginTypeParameters.getAsParameterBlock());

    return pluginTypeParameters;
  }

  protected abstract PluginTypeParameters getPluginTypeParameters(
      ParameterBlock block);

  @Override
  public boolean isWritable() {
    return true;
  }

  @Override
  public void setValue(Object parent, Object value) {
    if (!(parent instanceof FactoryParameters)) {
      return;
    }

    if (value == null) {
      ((FactoryParameters) parent).setPluginTypeParameter(getName(), null);
      return;
    }

    // checks whether the given value fits the plugintype (given from an
    // implementation provider)
    if (getType().isAssignableFrom(value.getClass())) {
      ParameterBlock block = new ParameterBlock(value.getClass().getName());
      ((FactoryParameters) parent).setPluginTypeParameter(getName(), block);
    }

    // check whether the value is a PluginTypeParameters object (when coming
    // form an editor)
    if (value instanceof PluginTypeParameters) {
      ((FactoryParameters) parent).setPluginTypeParameter(getName(),
          ((PluginTypeParameters) value).getAsParameterBlock());
    }

  }

}

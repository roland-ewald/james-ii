/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.factories.propertyprovider;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.utils.parameters.factories.PluginTypeParameters;

/**
 * Internally used class that represents a plugin type parameter in
 * {@link org.jamesii.gui.utils.parameters.factories.FactoryParameters}.
 * 
 * @author Stefan Rybacki
 */
class PluginTypeProperty extends AbstractPluginTypeProperty {

  /**
   * The base factory.
   */
  private String baseFactory;

  /**
   * Instantiates a new plugin type property.
   * 
   * @param name
   *          the name
   * @param plugintype
   *          the type
   */
  public PluginTypeProperty(String name, Class<?> plugintype) {
    super(name, plugintype, plugintype);
    this.baseFactory = plugintype.getName();
  }

  @Override
  protected PluginTypeParameters getPluginTypeParameters(ParameterBlock block) {
    return new PluginTypeParameters(baseFactory, (String) block.getValue(),
        block);
  }

}

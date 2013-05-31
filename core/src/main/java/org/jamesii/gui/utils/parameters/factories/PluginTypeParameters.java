/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.factories;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.utils.BasicUtilities;

/**
 * A wrapper class representing plugin type parameters in
 * {@link FactoryParameters}.
 * 
 * @author Stefan Rybacki
 */
public class PluginTypeParameters extends FactoryParameters {

  /**
   * The base factory for the represented plugin type.
   */
  private String baseFactory;

  /**
   * Instantiates a new plugin type parameters.
   * 
   * @param baseFactory
   *          the base factory for the plugin type
   * @param factoryName
   *          the factory name of an already set factory if any
   * @param block
   *          the predefined parameter block of the factory
   */
  public PluginTypeParameters(String baseFactory, String factoryName,
      ParameterBlock block) {
    super(factoryName, block);
    this.baseFactory = baseFactory;
  }

  @Override
  public String toString() {
    return BasicUtilities.makeFactoryClassNameReadable(getFactory());
  }

  /**
   * Gets the base factory.
   * 
   * @return the base factory
   */
  public String getBaseFactory() {
    return baseFactory;
  }

  @Override
  public ParameterBlock getAsParameterBlock() {
    ParameterBlock block = super.getAsParameterBlock();
    block.setValue(getFactory());
    return block;
  }
}

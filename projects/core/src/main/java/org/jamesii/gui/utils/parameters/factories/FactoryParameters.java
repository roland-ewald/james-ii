/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.factories;

import java.util.List;
import java.util.Map.Entry;

import org.jamesii.SimSystem;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.plugins.IFactoryInfo;
import org.jamesii.core.plugins.IParameter;
import org.jamesii.gui.utils.parameters.factories.converter.IStringConverter;
import org.jamesii.gui.utils.parameters.factories.converter.plugintype.AbstractValueConverterFactory;
import org.jamesii.gui.utils.parameters.factories.converter.plugintype.ValueConverterFactory;

/**
 * This is the main class that can be used to accumulate parameters for any
 * given factory by using the
 * {@link org.jamesii.gui.utils.objecteditor.ObjectEditorComponent}. This is
 * achieved by combining an instance of this class with
 * {@link org.jamesii.gui.utils.objecteditor.property.provider.IPropertyProvider}
 * s
 * {@link org.jamesii.gui.utils.parameters.factories.propertyprovider.FactoryParameterPropertyProvider}
 * and
 * {@link org.jamesii.gui.utils.parameters.factories.propertyprovider.PluginTypeParameterPropertyProvider}
 * . Also an implementation provider is given
 * {@link org.jamesii.gui.utils.parameters.factories.implementationprovider.PluginTypeImplementationProvider}
 * . To make usage easy try to use {@link FactoryParameterPanel} or
 * {@link org.jamesii.gui.utils.factories.ConfigureFactoryPanel} where
 * internally this class is used.
 * 
 * @author Stefan Rybacki
 */
public class FactoryParameters {

  /**
   * The factory name to collect parameters for.
   */
  private String factory;

  /**
   * The resulting parameter block.
   */
  private final ParameterBlock block = new ParameterBlock();

  /**
   * Instantiates a new factory parameters.
   * 
   * @param factoryName
   *          the factory name
   */
  public FactoryParameters(String factoryName) {
    super();
    factory = factoryName;
    if (factory == null) {
      return;
    }

    // set default values for parameters
    IFactoryInfo factoryInfo = SimSystem.getRegistry().getFactoryInfo(factory);

    if (factoryInfo == null) {
      throw new IllegalArgumentException("Factory " + factoryName
          + " not found!");
    }

    List<IParameter> fParams = factoryInfo.getParameters();
    if (fParams == null || fParams.size() == 0) {
      return;
    }

    for (IParameter p : fParams) {
      try {
        if (p.getDefaultValue() == null || p.getDefaultValue().length() == 0) {
          continue;
        }

        // ommit default values for already present values
        if (block.hasSubBlock(p.getName())) {
          continue;
        }

        ParameterBlock dBlock =
            new ParameterBlock(p.getType(), AbstractValueConverterFactory.TYPE);

        ValueConverterFactory<?> cf =
            SimSystem.getRegistry().getFactory(
                AbstractValueConverterFactory.class, dBlock);

        IStringConverter<?> converter = cf.create(dBlock, SimSystem.getRegistry().createContext());
        setParameter(p.getName(), converter.convert(p.getDefaultValue()));
      } catch (Exception e) {
        SimSystem.report(e);
      }
    }

  }

  /**
   * Instantiates a new factory parameters.
   * 
   * @param factoryName
   *          the factory name
   * @param b
   *          a predefined parameter block already containing some set parameter
   *          values
   */
  public FactoryParameters(String factoryName, ParameterBlock b) {
    this(factoryName);

    if (b != null) {
      // also set values set in parameter block
      for (Entry<String, ParameterBlock> e : b.getSubBlocks().entrySet()) {
        block.addSubBlock(e.getKey(), e.getValue());
      }
    }
  }

  /**
   * Sets a parameterblock for plugin type parameter.
   * 
   * @param id
   *          the id
   * @param b
   *          the b
   */
  public final void setPluginTypeParameter(String id, ParameterBlock b) {
    block.addSubBlock(id, b);
  }

  /**
   * Sets a parameter value.
   * 
   * @param id
   *          the id of the parameter
   * @param value
   *          the value
   */
  public final void setParameter(String id, Object value) {
    block.addSubBlock(id, value);
  }

  /**
   * Gets a the parameter.
   * 
   * @param <T>
   *          the parameter type
   * @param id
   *          the id of the parameter
   * @return the parameter
   */
  @SuppressWarnings("unchecked")
  public final <T> T getParameter(String id) {
    return (T) block.getSubBlockValue(id);
  }

  /**
   * Gets the parameter block for a plugin type parameter.
   * 
   * @param name
   *          the name
   * @return the plugin type parameter
   */
  public ParameterBlock getPluginTypeParameter(String name) {
    return block.getSubBlock(name);
  }

  /**
   * Gets the resulting parameter block.
   * 
   * @return the parameter block
   */
  public ParameterBlock getAsParameterBlock() {
    return block;
  }

  /**
   * Gets the factory.
   * 
   * @return the factory
   */
  public String getFactory() {
    return factory;
  }

}

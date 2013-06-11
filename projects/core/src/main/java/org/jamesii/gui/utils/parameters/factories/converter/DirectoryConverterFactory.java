/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.factories.converter;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.IDirectory;
import org.jamesii.gui.utils.parameters.factories.converter.plugintype.AbstractValueConverterFactory;
import org.jamesii.gui.utils.parameters.factories.converter.plugintype.ValueConverterFactory;

/**
 * @author Stefan Rybacki
 */
public class DirectoryConverterFactory extends
    ValueConverterFactory<IDirectory> {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 7745033071401967377L;

  @Override
  public IStringConverter<IDirectory> create(ParameterBlock paramBlock) {
    return new DirectoryConverter();
  }

  @Override
  public int supportsParameters(ParameterBlock parameters) {
    if (parameters.getSubBlockValue(AbstractValueConverterFactory.TYPE).equals(
        IDirectory.class)
        || parameters.getSubBlockValue(AbstractValueConverterFactory.TYPE)
            .equals(IDirectory.class.getName())) {
      return 1;
    }
    return 0;
  }
}

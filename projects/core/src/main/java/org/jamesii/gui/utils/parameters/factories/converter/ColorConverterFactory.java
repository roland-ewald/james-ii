/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.factories.converter;

import java.awt.Color;

import org.jamesii.core.factories.Context;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.utils.parameters.factories.converter.plugintype.AbstractValueConverterFactory;
import org.jamesii.gui.utils.parameters.factories.converter.plugintype.ValueConverterFactory;

/**
 * @author Stefan Rybacki
 */
public class ColorConverterFactory extends ValueConverterFactory<Color> {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 7745033071401967377L;

  @Override
  public IStringConverter<Color> create(ParameterBlock paramBlock, Context context) {
    return new ColorConverter();
  }

  @Override
  public int supportsParameters(ParameterBlock parameters) {
    if (parameters.getSubBlockValue(AbstractValueConverterFactory.TYPE).equals(
        Color.class)
        || parameters.getSubBlockValue(AbstractValueConverterFactory.TYPE)
            .equals(Color.class.getName())) {
      return 1;
    }
    return 0;
  }
}

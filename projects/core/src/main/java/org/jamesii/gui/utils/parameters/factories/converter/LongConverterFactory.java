/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.factories.converter;

import org.jamesii.core.factories.Context;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.utils.parameters.factories.converter.plugintype.AbstractValueConverterFactory;
import org.jamesii.gui.utils.parameters.factories.converter.plugintype.ValueConverterFactory;

/**
 * @author Stefan Rybacki
 */
public class LongConverterFactory extends ValueConverterFactory<Long> {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 7745033071401967378L;

  @Override
  public IStringConverter<Long> create(ParameterBlock paramBlock, Context context) {
    return new LongConverter();
  }

  @Override
  public int supportsParameters(ParameterBlock parameters) {
    if (parameters.getSubBlockValue(AbstractValueConverterFactory.TYPE).equals(
        Long.class)
        || parameters.getSubBlockValue(AbstractValueConverterFactory.TYPE)
            .equals(Long.class.getName())) {
      return 1;
    }
    return 0;
  }
}

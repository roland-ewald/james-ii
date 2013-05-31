/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.factories.converter;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.utils.parameters.factories.converter.plugintype.AbstractValueConverterFactory;
import org.jamesii.gui.utils.parameters.factories.converter.plugintype.ValueConverterFactory;

/**
 * @author Stefan Rybacki
 */
public class StringConverterFactory extends ValueConverterFactory<String> {

  private static final long serialVersionUID = -534984865346179006L;

  @Override
  public IStringConverter<String> create(ParameterBlock paramBlock) {
    return new StringConverter();
  }

  @Override
  public int supportsParameters(ParameterBlock parameters) {
    if (parameters.getSubBlockValue(AbstractValueConverterFactory.TYPE).equals(
        String.class)
        || parameters.getSubBlockValue(AbstractValueConverterFactory.TYPE)
            .equals(String.class.getName())) {
      return 1;
    }
    return 0;
  }
}

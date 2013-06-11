/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.factories.converter;

/**
 * Converter for integer string values.
 * 
 * @author Stefan Rybacki
 */
public class IntegerConverter implements IStringConverter<Integer> {

  @Override
  public Integer convert(String encodedObject) {
    try {
      return Integer.valueOf(encodedObject);
    } catch (Exception e) {
      return 0;
    }
  }

}

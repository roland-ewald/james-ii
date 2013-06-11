/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.factories.converter;

/**
 * Converter for double string values.
 * 
 * @author Stefan Rybacki
 */
public class DoubleConverter implements IStringConverter<Double> {

  @Override
  public Double convert(String encodedObject) {
    try {
      return Double.valueOf(encodedObject);
    } catch (Exception e) {
      return 0d;
    }

  }

}

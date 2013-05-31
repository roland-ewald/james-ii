/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.factories.converter;

/**
 * Converter for Boolean string values.
 * 
 * @author Stefan Rybacki
 */
public class BooleanConverter implements IStringConverter<Boolean> {

  @Override
  public Boolean convert(String encodedObject) {
    try {
      return Boolean.valueOf(encodedObject);
    } catch (Exception e) {
      return false;
    }

  }

}

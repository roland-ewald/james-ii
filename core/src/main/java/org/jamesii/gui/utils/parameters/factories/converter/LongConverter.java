/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.factories.converter;

/**
 * Converter for long string values.
 * 
 * @author Stefan Rybacki
 */
public class LongConverter implements IStringConverter<Long> {

  @Override
  public Long convert(String encodedObject) {
    try {
      return Long.valueOf(encodedObject);
    } catch (Exception e) {
      return 0L;
    }
  }

}

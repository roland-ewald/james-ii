/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.factories.converter;

/**
 * Converter for string values.
 * 
 * @author Stefan Rybacki
 */
public class StringConverter implements IStringConverter<String> {

  @Override
  public String convert(String encodedObject) {
    return encodedObject;
  }

}

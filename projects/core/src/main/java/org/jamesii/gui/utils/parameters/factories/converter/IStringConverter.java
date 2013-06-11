/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.factories.converter;

/**
 * Interface for implementations that are able to convert a given String value
 * into an object of the given type
 * 
 * @author Stefan Rybacki
 * @param <D>
 *          the returned java object type
 */
public interface IStringConverter<D> {

  /**
   * Converts the given String into a Java object.
   * 
   * @param encodedObject
   *          the encoded object
   * @return the object
   */
  D convert(String encodedObject);
}

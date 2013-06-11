/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.objecteditor.property.provider;

import java.util.List;

import org.jamesii.gui.utils.objecteditor.property.IProperty;

/**
 * Interface for property provider implementations. Use this interface to
 * provide custom properties, that are e.g., not bean properties.
 * 
 * @author Stefan Rybacki
 * @see DefaultPropertyProvider
 * @see ListPropertyProvider
 */
public interface IPropertyProvider {

  /**
   * Gets the properties for the specified class.
   * 
   * @param parentClass
   *          the parent class
   * @param parent
   *          the actual object the properties are to be provided for
   * @return the properties
   */
  List<IProperty> getPropertiesFor(Class<?> parentClass, Object parent);

  /**
   * Checks whether the provided class is supported by this provider.
   * 
   * @param parentClass
   *          the class properties are requested for
   * @return true, if supported
   */
  boolean supportsClass(Class<?> parentClass);
}

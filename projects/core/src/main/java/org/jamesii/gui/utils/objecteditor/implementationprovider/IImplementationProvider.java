/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.objecteditor.implementationprovider;

import java.util.Map;

import org.jamesii.gui.utils.objecteditor.ObjectEditorComponent;

/**
 * Interface for implementation provider implementations that can be used in
 * {@link ObjectEditorComponent} to provide predefined values for certain
 * properties.
 * 
 * @author Stefan Rybacki
 * @param <V>
 *          the type of implementations
 * @see DefaultImplementationProvider
 */
public interface IImplementationProvider<V> {

  /**
   * Gets the implementations assignable to the specified property if supported.
   * 
   * @param parentProperty
   *          the object the property belongs to
   * @param propertyName
   *          the property name
   * @param propertyType
   *          the property type
   * @return a map of implementations and names that are displayed in the
   *         implementations chooser
   */
  Map<String, V> getImplementations(Object parentProperty, String propertyName,
      Class<?> propertyType);
}

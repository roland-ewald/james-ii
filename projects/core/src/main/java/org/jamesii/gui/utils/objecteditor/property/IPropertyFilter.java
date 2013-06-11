/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.objecteditor.property;

import org.jamesii.gui.utils.objecteditor.ObjectEditorComponent;

/**
 * Interface for property filter implementations usable in
 * {@link ObjectEditorComponent}.
 * 
 * @author Stefan Rybacki
 */
public interface IPropertyFilter {

  /**
   * Checks if the given property is visible.
   * 
   * @param parentPropertyClass
   *          the type of the object the property belongs to
   * @param propertyName
   *          the property name
   * @param propertyType
   *          the actual property type
   * @return true, if is property visible
   */
  boolean isPropertyVisible(Class<?> parentPropertyClass, String propertyName,
      Class<?> propertyType);

}

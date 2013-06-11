/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.objecteditor;

/**
 * Interface for property changed listener implementations that can be used with
 * {@link ObjectEditorComponent} to be notified whenever a property changed
 * through {@link ObjectEditorComponent}.
 * 
 * @author Stefan Rybacki
 */
public interface IPropertyChangedListener {

  /**
   * Is called whenever a property was changed by the object this listener is
   * registered at, most likely an {@link ObjectEditorComponent} instance or
   * derivate.
   * 
   * @param propertyParent
   *          the parent object who's property was changed
   * @param propertyName
   *          the property name
   * @param value
   *          the new property value
   */
  void propertyChanged(Object propertyParent, String propertyName, Object value);
}

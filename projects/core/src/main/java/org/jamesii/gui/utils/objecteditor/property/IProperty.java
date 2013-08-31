/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.objecteditor.property;

/**
 * This is one of the main interfaces to be used in combination with
 * {@link org.jamesii.gui.utils.objecteditor.ObjectEditorComponent} when
 * providing custom
 * {@link org.jamesii.gui.utils.objecteditor.property.provider.IPropertyProvider}
 * implementations. Implementations are responsible for reading and writing
 * properties for that matter as well as providing information about the
 * property itself. This interface is used to expose properties of objects that
 * are not properties in the usual meaning. So it is possible to have list items
 * to be exposed and therefore made editable as properties.
 * <p/>
 * To work properly with
 * {@link org.jamesii.gui.utils.objecteditor.ObjectEditorComponent} it is
 * crucial to provide a appropriate {@link #hashCode()} and
 * {@link #equals(Object)} implementation. Have also a look at
 * {@link AbstractProperty} where such an implementation already is provided.
 * 
 * @author Stefan Rybacki
 * @see org.jamesii.gui.utils.objecteditor.property.provider.ListPropertyProvider
 * @see org.jamesii.gui.utils.objecteditor.property.provider.ListItemProperty
 * @see AbstractProperty
 */
public interface IProperty {

  /**
   * Gets the property name.
   * 
   * @return the name of the property
   */
  String getName();

  /**
   * Provides the actual value of the property of the specified parent object
   * 
   * @param parent
   *          the parent the property belongs to
   * @return the value
   */
  Object getValue(Object parent);

  /**
   * Sets the value of this property to the specified. The property value is to
   * be set for the given property parent object.
   * 
   * @param parent
   *          the parent object
   * @param value
   *          the value to set
   */
  void setValue(Object parent, Object value);

  /**
   * Specifies the property type
   * 
   * @return the type
   */
  Class<?> getType();

  /**
   * Gets the property description if any.
   * 
   * @return the description
   */
  String getDescription();

  /**
   * Checks if the property is writable. This can be used to disable writing to
   * a property. This is useful if a property should not be changeable but its
   * properties are.
   * 
   * @return true, if is writable
   */
  boolean isWritable();
}

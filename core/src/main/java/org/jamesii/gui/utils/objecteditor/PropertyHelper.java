/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.objecteditor;

import org.jamesii.gui.utils.objecteditor.property.IProperty;

/**
 * Internal class that wraps {@link IProperty} and value plus parent for
 * management in {@link ObjectEditorComponent} more concrete in
 * {@link ObjectEditorModel}.
 * 
 * @author Stefan Rybacki
 */
final class PropertyHelper {

  /**
   * The property value.
   */
  private Object value;

  /**
   * The property descriptor.
   */
  private IProperty descriptor;

  /**
   * The parent property or the main object to edit in case its the root object
   * (the object that is edited by {@link ObjectEditorComponent}).
   */
  private Object parent;

  /**
   * Flag indicating whether this represented property is to be ommited (for
   * cycle reasons for instance).
   */
  private boolean ommited = false;

  /**
   * Instantiates a new property helper.
   * 
   * @param value
   *          the value
   * @param descriptor
   *          the descriptor
   * @param parent
   *          the parent
   */
  public PropertyHelper(Object value, IProperty descriptor, Object parent) {
    this.value = value;
    this.descriptor = descriptor;
    this.parent = parent;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof PropertyHelper) {
      // TODO use
      // property path instead
      // VALUE/PROPERTY/SUBPROPERTY/SUBSUBPROPERTY
      // or use another property object as parent (should work too)
      // makes it even possible to
      // traverse the hierarchy

      // Right now this relies on correct hashCode and equals
      // implementations of parent
      return ((PropertyHelper) obj).getParent().equals(getParent())
          && ((PropertyHelper) obj).descriptor.equals(descriptor);
    }
    return super.equals(obj);
  }

  @Override
  public int hashCode() {
    return getParent().hashCode() * 31 + descriptor.hashCode();
  }

  /**
   * @return the descriptor
   */
  public IProperty getDescriptor() {
    return descriptor;
  }

  /**
   * @param descriptor
   *          the descriptor to set
   */
  public void setDescriptor(IProperty descriptor) {
    this.descriptor = descriptor;
  }

  /**
   * @return the ommited
   */
  protected final boolean isOmmited() {
    return ommited;
  }

  /**
   * @param ommited
   *          the ommited to set
   */
  protected final void setOmmited(boolean ommited) {
    this.ommited = ommited;
  }

  /**
   * @return the value
   */
  protected final Object getValue() {
    return value;
  }

  /**
   * @param value
   *          the value to set
   */
  protected final void setValue(Object value) {
    this.value = value;
  }

  /**
   * @return the parent
   */
  protected final Object getParent() {
    return parent;
  }

  /**
   * @param parent
   *          the parent to set
   */
  protected final void setParent(Object parent) {
    this.parent = parent;
  }
}

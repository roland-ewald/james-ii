/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.objecteditor;

import org.jamesii.gui.validation.AbstractValidator;

/**
 * Internally used class that implements
 * {@link org.jamesii.gui.validation.IValidator} to provide error icon display
 * on top of
 * {@link org.jamesii.gui.utils.objecteditor.property.editor.IPropertyEditor}s
 * in {@link ObjectEditorComponent}.
 * 
 * @author Stefan Rybacki
 */
final class PropertyEditorTableCellEditorRendererValidator extends
    AbstractValidator {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 2503988703169964652L;

  /**
   * The valid flag.
   */
  private boolean valid = true;

  @Override
  public boolean isValid() {
    return valid;
  }

  /**
   * Sets whether the validator should return valid or not
   * 
   * @param v
   *          the new valid state
   */
  public void setValid(boolean v) {
    valid = v;
    fireValidityChanged();
  }

}

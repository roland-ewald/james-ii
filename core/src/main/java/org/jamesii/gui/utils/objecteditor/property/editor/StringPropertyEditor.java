/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.objecteditor.property.editor;

import javax.swing.JComponent;
import javax.swing.JTextField;

/**
 * Editor for {@link String} values.
 * 
 * @author Stefan Rybacki
 */
public class StringPropertyEditor extends AbstractPropertyEditor<String> {

  /**
   * The value to edit.
   */
  private String value;

  /**
   * The in place editing component.
   */
  private JTextField field = new JTextField();
  {
    field.setBorder(null);
  }

  @Override
  public void cancelEditing(EditingMode mode) {
    // value stays the same
  }

  @Override
  public boolean finishEditing(EditingMode mode) {
    value = field.getText();
    return true;
  }

  @Override
  public JComponent getExternalComponent() {
    return null;
  }

  @Override
  public JComponent getInPlaceComponent() {
    field.setText(value);
    return field;
  }

  @Override
  public String getValue() {
    return value;
  }

  @Override
  public boolean isMasterEditor() {
    return true;
  }

  @Override
  public void setValue(String value) {
    field.setText(value);
    this.value = value;
  }

  @Override
  public boolean supportsExternalEditing() {
    return false;
  }

  @Override
  public boolean supportsInPlaceEditing() {
    return true;
  }

}

/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.objecteditor.property.editor;

import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;

/**
 * Editor for {@link Integer} values.
 * 
 * @author Stefan Rybacki
 */
public class IntegerPropertyEditor extends AbstractPropertyEditor<Integer> {

  /**
   * The value to edit.
   */
  private Integer value;

  /**
   * The in place editing component.
   */
  private JFormattedTextField field = new JFormattedTextField(
      NumberFormat.getIntegerInstance());
  {
    field.setBorder(null);
  }

  @Override
  public void cancelEditing(EditingMode mode) {
    // value stays the same
  }

  @Override
  public boolean finishEditing(EditingMode mode) {
    try {
      field.commitEdit();
      value = ((Number) field.getValue()).intValue();
    } catch (Exception e) {
      return false;
    }

    return true;
  }

  @Override
  public JComponent getExternalComponent() {
    return null;
  }

  @Override
  public JComponent getInPlaceComponent() {
    field.setValue(value);
    return field;
  }

  @Override
  public Integer getValue() {
    return value;
  }

  @Override
  public boolean isMasterEditor() {
    return true;
  }

  @Override
  public void setValue(Integer value) {
    this.value = value;
    field.setValue(value);
  }

  @Override
  public boolean supportsExternalEditing() {
    return false;
  }

  @Override
  public boolean supportsInPlaceEditing() {
    return true;
  }

  @Override
  protected String valueToPaint(Integer v) {
    try {
      return field.getFormatter().valueToString(v);
    } catch (ParseException e) {
      return v.toString();
    }
  }

}

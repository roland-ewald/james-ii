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
 * Editor for {@link Long} values
 * 
 * @author Stefan Rybacki
 */
public class LongPropertyEditor extends AbstractPropertyEditor<Long> {

  /**
   * The value to edit.
   */
  private Long value;

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
      value = ((Number) field.getValue()).longValue();
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
  public Long getValue() {
    return value;
  }

  @Override
  public boolean isMasterEditor() {
    return true;
  }

  @Override
  public void setValue(Long value) {
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
  protected String valueToPaint(Long v) {
    try {
      return field.getFormatter().valueToString(v);
    } catch (ParseException e) {
      return v.toString();
    }
  }

}

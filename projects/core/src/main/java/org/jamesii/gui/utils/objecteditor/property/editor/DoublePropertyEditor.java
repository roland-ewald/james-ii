/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.objecteditor.property.editor;

import javax.swing.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Editor for {@link Double} properties.
 *
 * @author Stefan Rybacki
 */
public class DoublePropertyEditor extends AbstractPropertyEditor<Double> {

  /**
   * The value to edit.
   */
  private Double value;

  /**
   * The in place editing component.
   */
  private JFormattedTextField field;

  {
    NumberFormat format = DecimalFormat.getInstance();
    format.setMaximumFractionDigits(20);
    field = new JFormattedTextField(
        format);
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
      value = ((Number) field.getValue()).doubleValue();
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
  public Double getValue() {
    return value;
  }

  @Override
  public boolean isMasterEditor() {
    return true;
  }

  @Override
  public void setValue(Double value) {
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
  protected String valueToPaint(Double v) {
    try {
      return field.getFormatter().valueToString(v);
    } catch (ParseException e) {
      return v.toString();
    }
  }

}

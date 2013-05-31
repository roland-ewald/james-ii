/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.objecteditor.property.editor;

import java.awt.Dimension;
import java.awt.Font;
import java.text.NumberFormat;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;

/**
 * Editor for {@link Dimension} properties.
 * 
 * @author Stefan Rybacki
 */
public class DimensionPropertyEditor extends AbstractPropertyEditor<Dimension> {

  /**
   * The value to edit.
   */
  private Dimension value;

  /**
   * The dimension width.
   */
  private JFormattedTextField width = new JFormattedTextField(
      NumberFormat.getIntegerInstance());

  /**
   * The dimension height.
   */
  private JFormattedTextField height = new JFormattedTextField(
      NumberFormat.getIntegerInstance());
  {
    width.setBorder(null);
    height.setBorder(null);
  }

  @Override
  public void cancelEditing(EditingMode mode) {
  }

  @Override
  public boolean finishEditing(EditingMode mode) {
    try {
      width.commitEdit();
      height.commitEdit();
      value =
          new Dimension(Integer.valueOf(width.getText()),
              Integer.valueOf(height.getText()));
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
    Box hBox = Box.createHorizontalBox();
    JLabel label;
    hBox.add(label = new JLabel("Width:"));
    label.setFont(label.getFont().deriveFont(Font.PLAIN));
    hBox.add(width);
    hBox.add(label = new JLabel("Height:"));
    label.setFont(label.getFont().deriveFont(Font.PLAIN));
    hBox.add(height);
    return hBox;
  }

  @Override
  public Dimension getValue() {
    return value;
  }

  @Override
  public boolean isMasterEditor() {
    return true;
  }

  @Override
  public void setValue(Dimension value) {
    if (value == null) {
      value = new Dimension(0, 0);
    }
    this.value = value;
    width.setText(String.valueOf(value.width));
    height.setText(String.valueOf(value.height));
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
  protected String valueToPaint(Dimension v) {
    return String.format("Width:%d Height:%d", v.width, v.height);
  }
}

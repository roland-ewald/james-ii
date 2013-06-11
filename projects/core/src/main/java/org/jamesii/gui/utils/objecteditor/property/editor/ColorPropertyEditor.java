/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.objecteditor.property.editor;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

/**
 * Editor for {@link Color} values. It provides an external editor for Colors.
 * 
 * @author Stefan Rybacki
 */
public class ColorPropertyEditor extends AbstractPropertyEditor<Color> {

  /**
   * The value.
   */
  private Color value;

  /**
   * The chooser.
   */
  private ColorChooserComponent chooser;

  @Override
  public void cancelEditing(EditingMode mode) {
  }

  @Override
  public boolean finishEditing(EditingMode mode) {
    switch (mode) {
    case EXTERNAL:
      value = chooser.getSelectedColor();
      break;
    }
    return true;
  }

  @Override
  public JComponent getPaintComponent(Color v) {
    JPanel l = new JPanel(new BorderLayout());
    l.setOpaque(false);
    if (v != null) {
      JPanel colorPanel = new JPanel();
      colorPanel.setBackground(v);
      colorPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
      l.add(colorPanel, BorderLayout.LINE_START);
      l.add(new JLabel(v.toString()), BorderLayout.CENTER);
    }
    return l;
  }

  @Override
  public JComponent getExternalComponent() {
    if (chooser == null) {
      chooser = new ColorChooserComponent(value);
    }
    return chooser;
  }

  @Override
  public JComponent getInPlaceComponent() {
    return null;
  }

  @Override
  public Color getValue() {
    return value;
  }

  @Override
  public boolean isMasterEditor() {
    return true;
  }

  @Override
  public void setValue(Color value) {
    this.value = value;
    if (chooser != null) {
      chooser.setSelectedColor(value);
    }
  }

  @Override
  public boolean supportsExternalEditing() {
    return true;
  }

  @Override
  public boolean supportsInPlaceEditing() {
    return false;
  }

}

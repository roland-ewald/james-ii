/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.objecteditor.property.editor;

import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JLabel;

/**
 * Editor for {@link Font} values. It provides an external editor for fonts.
 * 
 * @author Stefan Rybacki
 */
public class FontPropertyEditor extends AbstractPropertyEditor<Font> {

  /**
   * The value.
   */
  private Font value;

  /**
   * The chooser.
   */
  private FontChooserComponent chooser;

  @Override
  public void cancelEditing(EditingMode mode) {
  }

  @Override
  public boolean finishEditing(EditingMode mode) {
    switch (mode) {
    case EXTERNAL:
      value = chooser.getSelectedFont();
      break;
    }
    return true;
  }

  @Override
  public JComponent getPaintComponent(Font v) {
    if (v == null) {
      return null;
    }
    JLabel l = new JLabel(v.getFontName());
    l.setFont(v.deriveFont(l.getFont().getSize2D()));
    return l;
  }

  @Override
  public JComponent getExternalComponent() {
    if (chooser == null) {
      chooser = new FontChooserComponent(value);
    }
    return chooser;
  }

  @Override
  public JComponent getInPlaceComponent() {
    return null;
  }

  @Override
  public Font getValue() {
    return value;
  }

  @Override
  public boolean isMasterEditor() {
    return true;
  }

  @Override
  public void setValue(Font value) {
    this.value = value;
    if (chooser != null) {
      chooser.setSelectedFont(value);
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

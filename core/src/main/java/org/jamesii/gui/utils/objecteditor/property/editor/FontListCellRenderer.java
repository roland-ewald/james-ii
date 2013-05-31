/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.objecteditor.property.editor;

import java.awt.Component;
import java.awt.Font;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * Renderer for {@link Font}s.
 * 
 * @author Stefan Rybacki
 */
public class FontListCellRenderer extends DefaultListCellRenderer {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = -195756140974680969L;

  @Override
  public Component getListCellRendererComponent(JList list, Object value,
      int index, boolean isSelected, boolean cellHasFocus) {

    Component c =
        super.getListCellRendererComponent(list, value, index, isSelected,
            cellHasFocus);
    if (value instanceof Font) {
      c.setFont(((Font) value).deriveFont(c.getFont().getSize2D()));
      if (c instanceof JLabel) {
        ((JLabel) c).setText(((Font) value).getFontName());
      }
    }

    return c;
  }

}

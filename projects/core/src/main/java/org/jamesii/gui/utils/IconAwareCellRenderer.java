/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Renderer for table that is able to render text or {@link Icon}s
 * 
 * @author Stefan Rybacki
 */
public class IconAwareCellRenderer extends DefaultTableCellRenderer {
  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 8699087310060735694L;

  @Override
  public Component getTableCellRendererComponent(JTable table1, Object value,
      boolean isSelected, boolean hasFocus, int row, int column) {

    Component c =
        super.getTableCellRendererComponent(table1, value, isSelected,
            hasFocus, row, column);

    if (c instanceof JLabel) {
      if (value instanceof Icon) {
        ((JLabel) c).setIcon((Icon) value);
        ((JLabel) c).setText(null);
      } else {
        ((JLabel) c).setIcon(null);
      }
    }

    return c;
  }

}

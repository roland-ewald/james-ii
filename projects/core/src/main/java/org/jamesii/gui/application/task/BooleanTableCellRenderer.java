/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.task;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 * Basic table cell renderer for boolean values. In contrast to the default
 * boolean table cell renderer provided by the JDK this one displays with a
 * {@link DefaultTableCellRenderer} when value is not a boolean.
 * 
 * @author Stefan Rybacki
 * 
 */
class BooleanTableCellRenderer extends JCheckBox implements TableCellRenderer {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = -4539459825273986596L;

  /**
   * fallback renderer
   */
  private transient TableCellRenderer defaultRenderer =
      new DefaultTableCellRenderer();

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column) {

    setBorderPainted(true);

    if (isSelected) {
      setOpaque(true);
      setBackground(table.getSelectionBackground());
      setForeground(table.getSelectionForeground());
    } else {
      setOpaque(false);
      setForeground(table.getForeground());
    }

    if (hasFocus) {
      setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
    } else {
      setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
    }

    this.setHorizontalAlignment(SwingConstants.CENTER);

    if (value instanceof Boolean) {
      this.setSelected(((Boolean) value).booleanValue());
      return this;
    }

    return defaultRenderer.getTableCellRendererComponent(table, value,
        isSelected, hasFocus, row, column);
  }

  @Override
  public void updateUI() {
    super.updateUI();
    defaultRenderer = new DefaultTableCellRenderer();
  }
}

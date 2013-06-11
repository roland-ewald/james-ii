/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.task;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 * Basic table cell renderer used to display progress information given via
 * either {@link IProgress} interface or as {@link Number} between 0 and 1. If
 * neither of those two is provided a {@link DefaultTableCellRenderer} is used
 * to render the value.
 * 
 * @author Stefan Rybacki
 * 
 */
class ProgressTableCellRenderer extends JProgressBar implements
    TableCellRenderer {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = -58238043035839457L;

  /**
   * fallback renderer
   */
  private transient TableCellRenderer defaultRenderer =
      new DefaultTableCellRenderer();

  /**
   * helper panel used for focus border
   */
  private transient JPanel panel = new JPanel(new BorderLayout());

  /**
   * Instantiates a new progress table cell renderer.
   */
  public ProgressTableCellRenderer() {
    super(0, 1000);
    panel.add(this, BorderLayout.CENTER);
  }

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column) {

    if (isSelected) {
      if (!table.getSelectionBackground().equals(getForeground())) {
        setBackground(table.getSelectionBackground());
      }
    } else {
      Component c =
          defaultRenderer.getTableCellRendererComponent(table, value,
              isSelected, hasFocus, row, column);
      setBackground(c.getBackground());
    }

    if (hasFocus) {
      panel.setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
    } else {
      panel.setBorder(BorderFactory.createEmptyBorder());
    }

    if (value instanceof Number) {
      setValue((int) (((Number) value).floatValue() * getMaximum()));
      return panel;
    }

    if (value instanceof IProgress) {
      setValue((int) (((IProgress) value).getProgress() * getMaximum()));
      return panel;
    }

    return defaultRenderer.getTableCellRendererComponent(table, value,
        isSelected, hasFocus, row, column);
  }

  @Override
  public void updateUI() {
    super.updateUI();
    defaultRenderer = new DefaultTableCellRenderer();
    panel = new JPanel(new BorderLayout());
    panel.add(this, BorderLayout.CENTER);
  }

}

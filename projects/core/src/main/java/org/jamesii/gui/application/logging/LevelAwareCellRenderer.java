/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.logging;

import java.awt.Component;
import java.util.logging.Level;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Renderer for table that is able to render {@link Level}s as icons given
 * through constructor.
 * 
 * @author Stefan Rybacki
 */
class LevelAwareCellRenderer extends DefaultTableCellRenderer {
  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 8699087310060735694L;

  /**
   * The warning icon.
   */
  private Icon warning;

  /**
   * The error icon.
   */
  private Icon error;

  /**
   * The info icon.
   */
  private Icon info;

  /**
   * The config icon.
   */
  private Icon config;

  /**
   * The others icon.
   */
  private Icon other;

  @Override
  public Component getTableCellRendererComponent(JTable table1, Object value,
      boolean isSelected, boolean hasFocus, int row, int column) {

    Component c =
        super.getTableCellRendererComponent(table1, value, isSelected,
            hasFocus, row, column);

    if (c instanceof JLabel) {
      if (value instanceof Level) {
        Icon icon = other;

        if (value.equals(Level.CONFIG)) {
          icon = config;
        }
        if (value.equals(Level.SEVERE)) {
          icon = error;
        }
        if (value.equals(Level.WARNING)) {
          icon = warning;
        }
        if (value.equals(Level.INFO)) {
          icon = info;
        }

        ((JLabel) c).setIcon(icon);
        ((JLabel) c).setText(null);
      } else {
        ((JLabel) c).setIcon(null);
      }
    }

    return c;
  }

  /**
   * Instantiates a new level aware cell renderer.
   * 
   * @param error
   *          the error icon
   * @param warning
   *          the warning icon
   * @param info
   *          the info icon
   * @param config
   *          the config icon
   * @param other
   *          the other icon
   */
  public LevelAwareCellRenderer(Icon error, Icon warning, Icon info,
      Icon config, Icon other) {
    this.info = info;
    this.error = error;
    this.warning = warning;
    this.config = config;
    this.other = other;
  }

}

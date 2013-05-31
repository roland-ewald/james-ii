/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * Renders a list entry of {@link JList}.
 * 
 * @param <E>
 *          the type of the items contained in the list
 * 
 * @author Roland Ewald
 */
@Deprecated
public abstract class SimpleListCellRenderer<E> implements ListCellRenderer {

  /** Background colour. */
  private Color bg;

  /** Foreground colour. */
  private Color fg;

  /** Label to be displayed. */
  private JLabel label = new JLabel();

  /**
   * Default constructor.
   */
  public SimpleListCellRenderer() {
    this.label.setOpaque(true);
    fg = label.getForeground();
    bg = label.getBackground();
  }

  @Override
  @SuppressWarnings("unchecked")
  // Let's hope the programmer knows what's inside the list
  public Component getListCellRendererComponent(JList list, Object value,
      int index, boolean selected, boolean cellHasFocus) {
    label.setText(getItemName((E) value));
    label.setOpaque(true);
    label.setForeground(selected ? bg : fg);
    label.setBackground(selected ? fg : bg);
    return label;
  }

  /**
   * Gets the item name.
   * 
   * @param value
   *          the value
   * 
   * @return the item name
   */
  public abstract String getItemName(E value);
}
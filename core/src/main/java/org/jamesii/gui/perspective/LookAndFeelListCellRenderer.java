/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.perspective;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.LookAndFeel;
import javax.swing.UIManager.LookAndFeelInfo;

/**
 * Renderer class that shows the look and feel name instead of the standard
 * {@link #toString()} string.
 * 
 * @author Stefan Rybacki
 * 
 */
public final class LookAndFeelListCellRenderer extends DefaultListCellRenderer {
  /**
   * Serialization ID
   */
  private static final long serialVersionUID = -1351367106130454399L;

  /**
   * the singleton instance
   */
  private static final LookAndFeelListCellRenderer instance =
      new LookAndFeelListCellRenderer();

  /**
   * Omitted constructor
   */
  private LookAndFeelListCellRenderer() {
    // nothing to do here
  }

  /**
   * @return the singleton instance of this renderer
   */
  public static ListCellRenderer getInstance() {
    return instance;
  }

  @Override
  public Component getListCellRendererComponent(JList list, Object value,
      int index, boolean isSelected, boolean cellHasFocus) {

    // change the value to be used for display
    if (value instanceof LookAndFeelInfo) {
      value = ((LookAndFeelInfo) value).getName();
    } else

    if (value instanceof LookAndFeel) {
      value = ((LookAndFeel) value).getName();
    }

    return super.getListCellRendererComponent(list, value, index, isSelected,
        cellHasFocus);
  }
}

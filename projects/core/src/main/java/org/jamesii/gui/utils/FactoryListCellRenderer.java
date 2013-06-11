/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.jamesii.core.factories.Factory;

/**
 * Simple renderer that uses {@link Factory#getReadableName()} for displaying
 * the factory name.
 * 
 * @author Stefan Rybacki
 */
public class FactoryListCellRenderer extends DefaultListCellRenderer {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 8359929028861499654L;

  @Override
  public Component getListCellRendererComponent(JList list, Object value,
      int index, boolean isSelected, boolean cellHasFocus) {
    if (value instanceof Factory) {
      value = ((Factory) value).getReadableName();
    }

    return super.getListCellRendererComponent(list, value, index, isSelected,
        cellHasFocus);
  }

}

/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.editor;

import java.awt.Color;
import java.awt.Component;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.jamesii.gui.utils.parameters.editable.IEditable;

/**
 * Cell renderer for the parameter selection combobox in the parameter dialog.
 * 
 * Created on June 23, 2004
 * 
 * @author Roland Ewald
 */
public class ParameterComboBoxCellRenderer extends JLabel implements
    ListCellRenderer {

  /** Serialisation ID. */
  static final long serialVersionUID = 2366061729840660894L;

  /** The indentations. */
  private Map<IEditable<?>, Integer> indentions = new Hashtable<>();

  /**
   * Standard constructor.
   */
  public ParameterComboBoxCellRenderer() {
    this.setOpaque(true);
  }

  /**
   * Gets the list cell renderer component.
   * 
   * @param list
   *          the list
   * @param value
   *          the value
   * @param index
   *          the index
   * @param isSelected
   *          the is selected
   * @param cellHasFocus
   *          the cell has focus
   * 
   * @return the list cell renderer component
   * 
   * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList,
   *      java.lang.Object, int, boolean, boolean)
   */
  @Override
  public Component getListCellRendererComponent(JList list, Object value,
      int index, boolean isSelected, boolean cellHasFocus) {

    if (value instanceof IEditable<?>) {

      int indention = indentions.get(value).intValue();

      StringBuilder indent = new StringBuilder();

      for (int i = 0; i < indention; i++) {
        indent.append("     ");
      }

      this.setText(indent.toString() + ((IEditable<?>) value).getName());

    } else {
      this.setText(value.toString());
    }

    if (isSelected) {
      this.setBackground(Color.DARK_GRAY);
      this.setForeground(Color.WHITE);

    } else {
      this.setBackground(Color.LIGHT_GRAY);
      this.setForeground(Color.BLACK);
    }

    return this;
  }

  /**
   * @return the indentions
   */
  protected final Map<IEditable<?>, Integer> getIndentions() {
    return indentions;
  }

  /**
   * @param indentions
   *          the indentions to set
   */
  protected final void setIndentions(Map<IEditable<?>, Integer> indentions) {
    this.indentions = indentions;
  }

}

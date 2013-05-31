/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.jamesii.gui.utils.parameters.editable.IEditable;
import org.jamesii.gui.utils.parameters.editable.IEditableSet;

/**
 * Renders the title column of the dialog editor.
 * 
 * Created on June 8, 2004
 * 
 * @author Roland Ewald
 */
public class NameCellRenderer extends JPanel implements TableCellRenderer {

  /** Serialisation ID. */
  static final long serialVersionUID = -2878255218037305301L;

  /** Panel to add new parameters. */
  private JPanel addPanel = new JPanel();

  /** Icon string (replacement for a real icon). */
  private String iconString = "";

  /** Main label. */
  private JLabel label = new JLabel();

  /** Reference to parameter table model. */
  private ParameterTableModel paramTM = null;

  /** Label that contains control icon for expanding/collapsing parameters. */
  private JLabel testIcon = new JLabel();

  /**
   * Instantiates a new name cell renderer.
   * 
   * @param ptm
   *          the parameter table model
   */
  public NameCellRenderer(ParameterTableModel ptm) {

    paramTM = ptm;

    setOpaque(true);
    setLayout(new BorderLayout());
    addPanel.add(testIcon);
    addPanel.add(label);
    add(addPanel, BorderLayout.WEST);
  }

  @Override
  public Component getTableCellRendererComponent(JTable table, Object param,
      boolean isSelected, boolean hasFocus, int row, int column) {
    IEditable<?> p = (IEditable<?>) param;

    label.setText(p.getName());

    if (isSelected) {
      label.setFont(new Font("Helvetica", Font.BOLD, 12));
    } else {
      label.setFont(new Font("Helvetica", Font.PLAIN, 12));
    }

    if (p.isComplex() || p instanceof IEditableSet) {
      if (paramTM.isParamOpened(p)) {
        iconString = "[-]";
      } else {
        iconString = "[+]";
      }
    } else {
      iconString = "    ";
    }

    int level = paramTM.getParamLevel(p);

    for (int i = 0; i < level; i++) {
      iconString = "    " + iconString;
    }

    testIcon.setText(iconString);

    setToolTipText(p.getDocumentation());
    return this;
  }
}

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
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.core.experiments.variables.ExperimentVariable;
import org.jamesii.core.experiments.variables.ExperimentVariables;

/**
 * Renders a cell of a {@link JTree}.
 * 
 * @author Roland Ewald
 * 
 *         Date: 25.05.2007
 */
@Deprecated
public class SimpleTreeCellRenderer implements TreeCellRenderer {

  /** Background colour. */
  private Color bg;

  /** Foreground colour. */
  private Color fg;

  /** Label to be displayed. */
  private JLabel label = new JLabel();

  /**
   * Default constructor.
   */
  public SimpleTreeCellRenderer() {
    this.label.setOpaque(true);
    fg = label.getForeground();
    bg = label.getBackground();
  }

  @Override
  public Component getTreeCellRendererComponent(JTree tree, Object value,
      boolean selected, boolean expanded, boolean leaf, int row,
      boolean hasFocus) {
    Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
    if (userObject instanceof ExperimentVariable<?>) {
      label.setText(((ExperimentVariable<?>) userObject).getName());
    } else if (userObject instanceof ExperimentVariables) {
      label.setText("ExperimentVariables: "
          + ((ExperimentVariables) userObject).getName());
    } else if (userObject instanceof BaseExperiment) {
      label.setText("BaseExperiment");
    } else {
      label.setText("Unknown entity");
    }

    label.setOpaque(true);

    label.setForeground(selected ? bg : fg);
    label.setBackground(selected ? fg : bg);

    return label;
  }
}

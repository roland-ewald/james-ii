/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.editor.ca.parametersetup;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.jamesii.gui.model.editor.ca.cellrenderer.ICACellRenderer;

/**
 * A {@link ListCellRenderer} for displaying state names with their associated
 * representation in the grid. A {@link ICACellRenderer} is used to draw the
 * grid representation of the state.
 * 
 * @author Johannes Rössel
 */
public class StateListCellRenderer extends DefaultListCellRenderer {

  /** Serial version UID. */
  private static final long serialVersionUID = -4876824288030115810L;

  /** The renderer used for drawing the state previews next to their names. */
  private ICACellRenderer gridCellRenderer;

  /**
   * Instantiates this ListCellRenderer with default options. Not recommended
   * for this specific renderer.
   */
  public StateListCellRenderer() {
    super();
  }

  /**
   * Initializes a new instance of the {@link ListCellRenderer} class with a CA
   * cell renderer which is in turn used for displaying state previews next to
   * the state names.
   * 
   * @param gridCellRenderer
   *          The CA cell renderer to use for previews. Should be the same as in
   *          the accompanying grid.
   */
  public StateListCellRenderer(ICACellRenderer gridCellRenderer) {
    this();
    this.gridCellRenderer = gridCellRenderer;
  }

  /**
   * Gets the currently used renderer for CA state previews.
   * 
   * @return The {@link ICACellRenderer} used for drawing the state previews.
   */
  public ICACellRenderer getCACellRenderer() {
    return gridCellRenderer;
  }

  /**
   * Sets the renderer for drawing CA state previews.
   * 
   * @param gridCellRenderer
   *          The {@link ICACellRenderer} that should be used for drawing the
   *          state previews.
   */
  public void setGridCellRenderer(ICACellRenderer gridCellRenderer) {
    this.gridCellRenderer = gridCellRenderer;
  }

  @Override
  public Component getListCellRendererComponent(final JList list,
      final Object value, final int index, final boolean isSelected,
      boolean cellHasFocus) {

    final JLabel l =
        (JLabel) super.getListCellRendererComponent(list, value, index,
            isSelected, cellHasFocus);

    l.setIcon(new Icon() {
      private static final long serialVersionUID = -2845443861677797764L;

      @Override
      public int getIconHeight() {
        return list.getFixedCellHeight() != -1 ? list.getFixedCellHeight() : 15;
      }

      @Override
      public int getIconWidth() {
        // The icon is quadratic in this case
        return getIconHeight();
      }

      @Override
      public void paintIcon(Component c, Graphics g, int x, int y) {
        gridCellRenderer.draw(null, g, x + 3, y + 3, getIconWidth() - 6,
            getIconHeight() - 6, new Rectangle(x + 3, y + 3,
                getIconWidth() - 6, getIconHeight() - 6), 0, 0, index, false,
            false);
        g.setColor(l.getBackground());
        g.draw3DRect(x + 2, y + 2, getIconWidth() - 5, getIconHeight() - 5,
            false);
        if (isSelected) {
          Color col = list.getSelectionBackground();
          g.setColor(new Color(col.getRed(), col.getGreen(), col.getBlue(), 64));
          g.fillRect(x, y, getIconWidth(), getIconHeight());
        }
      }
    });

    return l;
  }
}

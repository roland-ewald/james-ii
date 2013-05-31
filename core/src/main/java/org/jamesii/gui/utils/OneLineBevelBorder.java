/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

/**
 * Simple beveled border that uses only one line as width rather than two like
 * {@link BevelBorder} does.
 * 
 * @author Stefan Rybacki
 * 
 */
public class OneLineBevelBorder implements Border {
  /**
   * flag specifying the type (RAISED, LOWERED)
   */
  private final int type;

  /**
   * @see BevelBorder#LOWERED
   */
  public static final int LOWERED = BevelBorder.LOWERED;

  /**
   * @see BevelBorder#RAISED
   */
  public static final int RAISED = BevelBorder.RAISED;

  /**
   * Creates a new beveled border.
   * 
   * @param type
   *          either {@link #RAISED} or {@link #LOWERED}
   */
  public OneLineBevelBorder(int type) {
    this.type = type;
  }

  @Override
  public Insets getBorderInsets(Component c) {
    return new Insets(1, 1, 1, 1);
  }

  @Override
  public boolean isBorderOpaque() {
    return true;
  }

  @Override
  public void paintBorder(Component c, Graphics g, int x, int y, int width,
      int height) {
    g.setColor(c.getBackground());
    g.draw3DRect(x, y, width - 1, height - 1, type == RAISED);
  }

}

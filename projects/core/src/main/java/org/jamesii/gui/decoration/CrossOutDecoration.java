/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.decoration;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Example decoration that paints a red cross on top of the component
 * 
 * @author Stefan Rybacki
 * 
 */
public class CrossOutDecoration extends DefaultDecoration {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 1949692667321111014L;

  @Override
  protected void paintDecoration(Graphics2D g, Decorator d) {
    super.paintDecoration(g, d);
    // paint cross on top
    Graphics2D g2d = g;

    g2d.setStroke(new BasicStroke(3));
    g2d.setColor(Color.red);
    g2d.drawLine(0, 0, d.getWidth() - 1, d.getHeight() - 1);
    g2d.drawLine(d.getWidth() - 1, 0, 0, d.getHeight() - 1);
  }
}

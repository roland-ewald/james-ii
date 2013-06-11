/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.axes.utilities;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Draws square markers at given positions.
 * 
 * @author Stefan Rybacki
 * 
 */
public class SquareMarkerRenderer implements IMarkerRenderer {

  @Override
  public void draw(Graphics2D g, int x, int y, int width, int height,
      Color color) {
    g.setColor(color);
    int wh = Math.min(width, height);
    g.fillRect(x + (width - wh) / 2, y + (height - wh) / 2, wh, wh);
  }

}

/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.axes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Collections;
import java.util.List;

/**
 * Simple {@link IAxisRenderer} implementation that only draws the axes itself
 * but no labels or any ticks on the axes.
 * 
 * @author Stefan Rybacki
 * 
 */
public class SimpleAxisRenderer implements IAxisRenderer {

  @Override
  public void drawAxis(Graphics2D g, IAxis axis, int x, int y, int width,
      int height, boolean horizontal, boolean leftLabels) {
    g.setColor(Color.black);
    if (horizontal) {
      g.drawLine(x, y, x + width, y);
    }
    if (!horizontal) {
      g.drawLine(x, y, x, y + height);
    }
  }

  @Override
  public int getHeight(Graphics2D g, IAxis axis, boolean labelsTopside) {
    return 1;
  }

  @Override
  public int getWidth(Graphics2D g, IAxis axis, boolean labelsLeft) {
    return 1;
  }

  @Override
  public List<Double> getTicksForAxis(Graphics2D g, IAxis axis, int x, int y,
      int width, int height, boolean horizontal, boolean leftLabels) {
    return Collections.emptyList();
  }

}

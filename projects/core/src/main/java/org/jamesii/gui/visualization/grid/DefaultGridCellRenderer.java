/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.grid;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Shape;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.jamesii.core.util.misc.Pair;
import org.jamesii.gui.utils.MultiGradient;
import org.jamesii.gui.utils.MultiGradient.GradientColor;

/**
 * A default grid cell renderer implementation. Different values from the model
 * are displayed as colored squares. This renderer does not know how many
 * different values may appear and thus implements a simple bisection across a
 * multi-color gradient so each possible value gets a different color.
 * 
 * @author Johannes Rössel
 */
public class DefaultGridCellRenderer extends AbstractGridCellRenderer {

  /** The gradient colors. */
  private static final GradientColor[] gradientColors = new GradientColor[] {
      new GradientColor(0, Color.BLACK),
      new GradientColor(1, new Color(0x68ACF9)),
      new GradientColor(2, new Color(0xE05E5C)),
      new GradientColor(3, new Color(0xB3D6D8)),
      new GradientColor(4, new Color(0xA783D3)),
      new GradientColor(5, new Color(0x57C9E5)),
      new GradientColor(6, new Color(0xF48224)) };

  /** Stores the values already known to the renderer as a simple lookup table. */
  private Map<Object, Color> colors;

  /**
   * Stores the intervals between the currently occupied colors on the gradient.
   * Whenever a new value is encountered the next interval from this queue is
   * taken, the color right in the middle of the interval gets assigned to the
   * new value and the newly created two intervals are added to the queue. Thus
   * it is ensured that new values don't get a color too close to other colors
   * currently used.
   */
  private Queue<Pair<Float, Float>> intervals;

  /** Initializes a new instance of this class. */
  public DefaultGridCellRenderer() {
    colors = new HashMap<>();
    intervals = new LinkedList<>();
    intervals.add(new Pair<>(0f, 1f));
  }

  /**
   * Returns the color of the given value. If it is not already present in the
   * list of known values it will be added to that list. For details on how new
   * color values are calculated see {@link #intervals}
   * 
   * @param x
   *          The value to get the color for.
   * @return A color for the given value.
   */
  private Color getColor(Object x) {
    if (!colors.containsKey(x)) {
      Pair<Float, Float> i = intervals.poll();
      float center = (i.getFirstValue() + i.getSecondValue()) / 2;
      Color c = MultiGradient.getColorFor(center, gradientColors);
      colors.put(x, c);
      // put new half intervals into the queue
      intervals.add(new Pair<>(i.getFirstValue(), center));
      intervals.add(new Pair<>(center, i.getSecondValue()));
      return c;
    }
    return colors.get(x);
  }

  @Override
  public void draw(Grid2D sender, Graphics g, int x, int y, int width,
      int height, Shape shape, int cellX, int cellY, Object value,
      boolean isSelected, boolean hasFocus) {
    g.setColor(getColor(value));
    g.fillRect(x, y, width, height);

    if (isSelected) {
      g.setColor(new Color(sender.getSelectionColor().getRed(), sender
          .getSelectionColor().getGreen(),
          sender.getSelectionColor().getBlue(), 128));
      g.fillRect(x, y, width - 1, height - 1);
    }
    if (hasFocus) {
      g.setColor(sender.getFocusColor());
      g.drawRect(x, y, width - 1, height - 1);
    }
  }

}

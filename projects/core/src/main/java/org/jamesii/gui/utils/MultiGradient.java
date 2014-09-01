/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils;

import java.awt.Color;

/**
 * MultiGradient that can be used to interpolate between an virtually arbitrary
 * number of colors. Use {@link #getColorFor(float, GradientColor...)} to
 * retrieve the desired color.
 * 
 * @author Stefan Rybacki
 */
public final class MultiGradient {
  /**
   * Helper class that stores a color and additionally a position within the
   * gradient grid.
   * 
   * @author Stefan Rybacki
   */
  public static class GradientColor implements Comparable<GradientColor> {
    /**
     * position of color on gradient
     */
    private float position;

    /**
     * the color at position
     */
    private Color color;

    /**
     * Creates a new color at the given position
     * 
     * @param position
     *          the position within the gradient
     * @param color
     *          the color at that position
     */
    public GradientColor(float position, Color color) {
      this.position = position;
      this.color = color;
    }

    @Override
    public int compareTo(GradientColor o) {
      if (position - o.position < 0) {
        return -1;
      }
      if (position - o.position > 0) {
        return 1;
      }
      return 0;
    }

  }

  /**
   * omitted constructor
   */
  private MultiGradient() {
    // nothing to do here
  }

  /**
   * Calculates an interpolated color using the specified {@code colors} array.
   * 
   * @param pos
   *          the position the color is calculated for
   * @param colors
   *          <em>sorted</em> array of colors with applied positions
   * @return the interpolated color
   */
  public static Color getColorFor(float pos, GradientColor... colors) {
    if (colors.length > 0) {
      // assuming colors is sorted
      float min = colors[0].position;
      float max = colors[colors.length - 1].position;

      // transform pos into color array position space
      float position = (min + pos * (max - min));

      Color result = colors[colors.length - 1].color;

      // find interval the color is in
      GradientColor last = null;
      for (GradientColor c : colors) {
        if (c.position > position) {
          // interval found
          if (last == null) {
            return c.color;
          }

          float mix = (position - last.position) / (c.position - last.position);

          return mixColors(c.color, last.color, mix);
        }
        last = c;
      }

      return result;
    }
    return null;
  }

  /**
   * Mixes two colors according to provided {@code mix} value. The resulting
   * color is calculated like this:
   * <p/>
   * result = color * mix + (1-mix) * color2
   * 
   * @param color
   *          color 1
   * @param color2
   *          color 2
   * @param mix
   *          the mix value
   * @return mixed color
   */
  private static Color mixColors(Color color, Color color2, float mix) {
    if (color == null || color2 == null) {
      return null;
    }

    int r, g, b, a;

    r =
        Math.min(255,
            (int) (color.getRed() * mix + color2.getRed() * (1 - mix)));
    g =
        Math.min(255, (int) (color.getGreen() * mix + color2.getGreen()
            * (1 - mix)));
    b =
        Math.min(255, (int) (color.getBlue() * mix + color2.getBlue()
            * (1 - mix)));
    a =
        Math.min(255, (int) (color.getAlpha() * mix + color2.getAlpha()
            * (1 - mix)));

    return new Color(r, g, b, a);
  }
}

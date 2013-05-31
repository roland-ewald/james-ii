/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.axes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.List;

import javax.swing.SwingUtilities;

/**
 * Basic implementation of an {@link IAxisRenderer}. It supports auto ticks with
 * non overlapping labels as well as auto resolution adaption of label values
 * according to min and max.
 * <p/>
 * Beware: this renderer is very likely to change in future releases!
 * 
 * @author Stefan Rybacki
 */
public class DefaultAxisRenderer implements IAxisRenderer {

  @Override
  public void drawAxis(Graphics2D g, IAxis axis, int x, int y, int width,
      int height, boolean horizontal, boolean leftLabels) {
    if (!horizontal && leftLabels) {
      drawLeftAxis(g, axis, x, y, width, height);
    }
    if (!horizontal && !leftLabels) {
      drawRightAxis(g, axis, x, y, width, height);
    }
    if (horizontal) {
      drawHorizontalAxis(g, axis, x, y, width, height, leftLabels);
    }

  }

  /**
   * Gets the format string to format axis values. It determines the resolution
   * of the values that are to be displayed so that there are no to ticks samely
   * labeled.
   * 
   * @param min
   *          the min
   * @param max
   *          the max
   * @param maxTicks
   * @return the resolution
   */
  private int getResolution(double min, double max, int maxTicks) {
    // TODO sr137: this works well for linear axis but does not so
    // well for logarithmic axes for instance (so it might be
    // necessary to actually calculate all tick values and then to
    // determine the right stepping)
    int res = 0;
    if (max - min > 0 && max - min != Double.POSITIVE_INFINITY
        && !Double.isNaN(max - min)) {
      res = (int) Math.max(0, Math.ceil(Math.log10(maxTicks / (max - min))));
    }
    return res;
  }

  /**
   * Draw left axis.
   * 
   * @param g
   *          the g
   * @param axis
   *          the axis
   * @param x
   *          the x
   * @param y
   *          the y
   * @param width
   *          the width
   * @param height
   *          the height
   */
  private void drawLeftAxis(Graphics2D g, IAxis axis, int x, int y, int width,
      int height) {
    g.setColor(Color.black);
    g.drawLine(x + width, y, x + width, y + height);

    int maxTicks =
        calcMaxTicksWithoutIntersec(axis, width, height, 20, 10, 1, g);

    List<Double> tickMarks = axis.getTickMarks(maxTicks);

    int res = getResolution(axis.getMinimum(), axis.getMaximum(), maxTicks);
    String f = String.format("%%.%df", res);

    for (Double value : tickMarks) {
      double posY = axis.transform(value);
      double realPosY = axis.transform(roundToNumberOfDigits(value, res));

      String v = String.format(f, value);

      int vw = SwingUtilities.computeStringWidth(g.getFontMetrics(), v);

      g.drawString(v, x + width - 10 - vw, (y + height - (float) posY
          * (y + height - y) + g.getFontMetrics().getAscent() / 2f));

      g.drawLine(x + width - 5, (int) (y + height - posY * (y + height - y)), x
          + width - 3, (int) (y + height - posY * (y + height - y)));

      g.drawLine(x + width - 3,
          (int) (y + height - realPosY * (y + height - y)), x + width - 3,
          (int) (y + height - posY * (y + height - y)));

      g.drawLine(x + width - 3,
          (int) (y + height - realPosY * (y + height - y)), x + width - 1,
          (int) (y + height - realPosY * (y + height - y)));
    }
  }

  /**
   * Draw right axis.
   * 
   * @param g
   *          the g
   * @param axis
   *          the axis
   * @param x
   *          the x
   * @param y
   *          the y
   * @param width
   *          the width
   * @param height
   *          the height
   */
  private void drawRightAxis(Graphics2D g, IAxis axis, int x, int y, int width,
      int height) {
    g.setColor(Color.black);
    g.drawLine(x, y, x, y + height);

    int maxTicks =
        calcMaxTicksWithoutIntersec(axis, width, height, 20, 10, 1, g);
    List<Double> tickMarks = axis.getTickMarks(maxTicks);

    int res = getResolution(axis.getMinimum(), axis.getMaximum(), maxTicks);
    String f = String.format("%%.%df", res);
    for (Double value : tickMarks) {
      double posY = axis.transform(value);
      double realPosY = axis.transform(roundToNumberOfDigits(value, res));

      String v = String.format(f, value);

      g.drawString(v, x + 10, (float) (y + height - posY * (y + height - y) + g
          .getFontMetrics().getAscent() / 2f));

      g.drawLine(x, (int) (y + height - realPosY * (y + height - y)), x + 3,
          (int) (y + height - realPosY * (y + height - y)));

      g.drawLine(x + 3, (int) (y + height - realPosY * (y + height - y)),
          x + 3, (int) (y + height - posY * (y + height - y)));

      g.drawLine(x + 3, (int) (y + height - posY * (y + height - y)), x + 5,
          (int) (y + height - posY * (y + height - y)));
    }

  }

  /**
   * Draw horizontal axis.
   * 
   * @param g
   *          the g
   * @param axis
   *          the axis
   * @param x
   *          the x
   * @param y
   *          the y
   * @param width
   *          the width
   * @param height
   *          the height
   * @param labelsTopside
   *          the labels topside
   */
  private void drawHorizontalAxis(Graphics2D g, IAxis axis, int x, int y,
      int width, int height, boolean labelsTopside) {
    g.setColor(Color.black);
    g.drawLine(x, y, x + width, y);

    int maxTicks =
        calcMaxTicksWithoutIntersec(axis, width, height, 20, 15, 0, g);

    List<Double> tickMarks = axis.getTickMarks(maxTicks);
    int res = getResolution(axis.getMinimum(), axis.getMaximum(), maxTicks);
    String f = String.format("%%.%df", res);
    for (Double value : tickMarks) {
      double posX = axis.transform(value) * width;
      double realPosX =
          axis.transform(roundToNumberOfDigits(value, res)) * width;

      String v = String.format(f, value);

      g.drawLine((int) (x + realPosX), y, (int) (x + realPosX), y + 3);
      g.drawLine((int) (x + realPosX), y + 3, (int) (x + posX), y + 3);
      g.drawLine((int) (x + posX), y + 3, (int) (x + posX), y + 5);

      g.drawString(
          v,
          ((float) posX + x - SwingUtilities.computeStringWidth(
              g.getFontMetrics(), v) / 2f), y + g.getFontMetrics().getHeight()
              + 5);
    }
  }

  @Override
  public int getHeight(Graphics2D g, IAxis axis, boolean labelsTopside) {
    return g.getFontMetrics().getHeight() + 5;
  }

  @Override
  public int getWidth(Graphics2D g, IAxis axis, boolean labelsLeft) {
    List<Double> tickMarks = axis.getTickMarks(10);
    int maxWidth = 0;
    int res =
        getResolution(axis.getMinimum(), axis.getMaximum(), tickMarks.size());
    String f = String.format("%%.%df", res);
    for (Double value : tickMarks) {
      String v = String.format(f, value);
      int w = SwingUtilities.computeStringWidth(g.getFontMetrics(), v);

      // 10 is the margin between label and axis
      maxWidth = Math.max(maxWidth, w + 10);
    }

    return maxWidth;
  }

  /**
   * Returns sum of ticks which can be presented on the axis without
   * intersection
   * 
   * @param axis
   * @param width
   * @param height
   * @param maxTicks
   *          number of ticks which should be presented
   * @param minDistance
   *          minimum distance between each tickMark
   * @param alignment
   *          alignment of the axis; 0 --> horizontal axis, 1 --> vertical axis
   * @param g
   *          the graphics context to use
   * @return number of ticks
   */
  private int calcMaxTicksWithoutIntersec(IAxis axis, int width, int height,
      int maxTicks, int minDistance, int alignment, Graphics2D g) {
    // TODO sr137: this is a bootstrapping problem here, since the
    // resolution is dependent on the tick count which is about to be
    // calculated here (so an iteration might be necessary)
    int res = getResolution(axis.getMinimum(), axis.getMaximum(), maxTicks);
    String f = String.format("%%.%df", res);

    // axis is horizontal
    if (alignment == 0) {
      boolean intersect = true;

      while (intersect && maxTicks > 1) {
        List<Double> tickMarks = axis.getTickMarks(maxTicks);
        for (int i = 0; i < maxTicks - 1; i++) {
          intersect = false;
          Double value1 = tickMarks.get(i);
          Double value2 = tickMarks.get(i + 1);

          double posX0 = axis.transform(value1) * width;
          double posX1 = axis.transform(value2) * width;

          String v0 = String.format(f, value1);
          String v1 = String.format(f, value2);
          Dimension box0 = calcBoundingBox(v0, g);
          Dimension box1 = calcBoundingBox(v1, g);

          // boxes may intersect
          if ((posX0 + box0.getWidth() / 2) + minDistance > posX1
              - box1.getWidth() / 2) {
            maxTicks--;
            intersect = true;
            break;
          }
        }
      }
    }

    if (alignment == 1) {
      boolean intersect = true;

      while (intersect && maxTicks > 1) {
        List<Double> tickMarks = axis.getTickMarks(maxTicks);
        for (int i = 0; i < maxTicks - 1; i++) {
          intersect = false;
          Double value1 = tickMarks.get(i);
          Double value2 = tickMarks.get(i + 1);

          double posY0 = axis.transform(value1);
          double posY1 = axis.transform(value2);

          String v0 = String.format(f, value1);
          String v1 = String.format(f, value2);
          Dimension box0 = calcBoundingBox(v0, g);
          Dimension box1 = calcBoundingBox(v1, g);

          // boxes may intersect
          if ((posY0 * height + box0.getHeight() / 2) + minDistance > (posY1
              * height - box1.getHeight() / 2)) {
            maxTicks--;
            intersect = true;
            break;
          }
        }
      }
    }
    return maxTicks;
  }

  /**
   * Returns bounding box for given String
   * 
   * @param s
   * @param g
   * @return Dimension of the bounding box for given string
   */
  private Dimension calcBoundingBox(String s, Graphics2D g) {
    Dimension d;
    // compute width of string s
    int width = SwingUtilities.computeStringWidth(g.getFontMetrics(), s);
    // compute height of string s
    int height = g.getFontMetrics().getHeight();
    d = new Dimension(width, height);

    return d;
  }

  private double roundToNumberOfDigits(double n, int digits) {
    if (digits < 0) {
      return n;
    }
    double prec = Math.pow(10, digits);
    return Math.floor(n * prec + .5) / prec;
  }

  @Override
  public List<Double> getTicksForAxis(Graphics2D g, IAxis axis, int x, int y,
      int width, int height, boolean horizontal, boolean leftLabels) {
    if (!horizontal) {
      int maxTicks =
          calcMaxTicksWithoutIntersec(axis, width, height, 20, 10, 1, g);

      int res = getResolution(axis.getMinimum(), axis.getMaximum(), maxTicks);

      List<Double> tickMarks = axis.getTickMarks(maxTicks);

      for (int i = 0; i < tickMarks.size(); i++) {
        Double value = tickMarks.get(i);
        value = roundToNumberOfDigits(value, res);
        tickMarks.set(i, value);
      }

      return tickMarks;
    } else {
      int maxTicks =
          calcMaxTicksWithoutIntersec(axis, width, height, 20, 15, 0, g);

      int res = getResolution(axis.getMinimum(), axis.getMaximum(), maxTicks);

      List<Double> tickMarks = axis.getTickMarks(maxTicks);

      for (int i = 0; i < tickMarks.size(); i++) {
        Double value = tickMarks.get(i);
        value = roundToNumberOfDigits(value, res);
        tickMarks.set(i, value);
      }

      return tickMarks;
    }
  }
}

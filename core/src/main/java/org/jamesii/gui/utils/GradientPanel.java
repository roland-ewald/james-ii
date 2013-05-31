/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Paint;

import javax.swing.JPanel;

/**
 * Basic panel with the ability to draw its background using a gradient.
 * 
 * @author Stefan Rybacki
 * 
 */
public class GradientPanel extends JPanel {
  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 5483770460164303131L;

  /**
   * gradient color from
   */
  private Color from;

  /**
   * gradient color to
   */
  private Color to;

  /**
   * Creates a gradient panel using the specified layout manager and the
   * specified gradient colors
   * 
   * @param layout
   *          the layout manager for the panel
   * @param from
   *          the gradient's from color
   * @param to
   *          the gradient's to color
   */
  public GradientPanel(LayoutManager layout, Color from, Color to) {
    super(layout);
    this.from = from;
    this.to = to;
  }

  /**
   * Creates a gradient panel using the specified gradient colors
   * 
   * @param from
   *          the gradient's from color
   * @param to
   *          the gradient's to color
   */
  public GradientPanel(Color from, Color to) {
    this(null, from, to);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;

    Paint paint = g2d.getPaint();
    g2d.setPaint(new GradientPaint(0, 0, from, 0, getHeight() - 1, to));
    g.fillRect(0, 0, getWidth(), getHeight());
    g2d.setPaint(paint);
  }

  /**
   * sets the gradient's from color
   * 
   * @param from
   *          the color to set from to
   */
  public void setGradientFrom(Color from) {
    setGradient(from, this.to);
  }

  /**
   * sets the gradient's to color
   * 
   * @param to
   *          the color to set to to
   */
  public void setGradientTo(Color to) {
    setGradient(this.from, to);
  }

  /**
   * sets the gradient's from and to color
   * 
   * @param from
   *          the color to set from to
   * @param to
   *          the color to set to to
   */

  public void setGradient(Color from, Color to) {
    this.from = from;
    this.to = to;
    repaint();
  }

  /**
   * @return the gradient's from color
   */
  public Color getGradientFrom() {
    return from;
  }

  /**
   * @return the gradient's to color
   */
  public Color getGradientTo() {
    return to;
  }

}

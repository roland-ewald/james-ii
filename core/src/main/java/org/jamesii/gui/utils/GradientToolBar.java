/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JToolBar;

/**
 * Analog to {@link GradientPanel} supports this tool bar gradient filled
 * backgrounds. Plus it also makes buttons added through {@link Action}s
 * transparent by default.
 * 
 * @author Stefan Rybacki
 * 
 */
public class GradientToolBar extends JToolBar {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = -4110728752023655443L;

  /**
   * the gradient's from color
   */
  private Color fromColor;

  /**
   * the gradient's to color
   */
  private Color toColor;

  /**
   * flag indicating whether to draw the gradient or not
   */
  private boolean gradient = true;

  /**
   * Convenience constructor to support all standard constructors of JToolBar
   */
  public GradientToolBar() {
    this(null, HORIZONTAL, false, null, null);
  }

  /**
   * Convenience constructor to support all standard constructors of JToolBar
   * 
   * @see JToolBar#JToolBar(int)
   */
  public GradientToolBar(int orientation) {
    this(null, orientation, false, null, null);
  }

  /**
   * Convenience constructor to support all standard constructors of JToolBar
   * 
   * @see JToolBar#JToolBar(String)
   */
  public GradientToolBar(String name) {
    this(name, HORIZONTAL, false, null, null);
  }

  /**
   * Convenience constructor to support all standard constructors of JToolBar
   * 
   * @see JToolBar#JToolBar(String, int)
   */
  public GradientToolBar(String name, int orientation) {
    this(name, orientation, false, null, null);
  }

  /**
   * Real constructor that also sets up the gradient colors
   * 
   * @param name
   *          the toolbar name
   * @param orientation
   *          the toolbar orientation
   * @param gradient
   *          flag indicating whether to draw gradient or not
   * @param fromColor
   *          sets the gradient's from color
   * @param toColor
   *          sets the gradient's to color
   * 
   * @see JToolBar#JToolBar(String, int)
   */
  public GradientToolBar(String name, int orientation, boolean gradient,
      Color fromColor, Color toColor) {
    super(name, orientation);
    setGradient(gradient);
    setFromColor(fromColor);
    setToColor(toColor);
  }

  /**
   * Convenience constructor analog to the default constructors of JToolBar that
   * also sets up the gradient colors
   * 
   * @param name
   *          the toolbar name
   * @param gradient
   *          flag indicating whether to draw gradient or not
   * @param fromColor
   *          sets the gradient's from color
   * @param toColor
   *          sets the gradient's to color
   * 
   * @see JToolBar#JToolBar(String)
   */
  public GradientToolBar(String name, boolean gradient, Color fromColor,
      Color toColor) {
    this(name, HORIZONTAL, gradient, fromColor, toColor);
  }

  /**
   * Convenience constructor analog to the default constructors of JToolBar that
   * also sets up the gradient colors
   * 
   * @param orientation
   *          the toolbar orientation
   * @param gradient
   *          flag indicating whether to draw gradient or not
   * @param fromColor
   *          sets the gradient's from color
   * @param toColor
   *          sets the gradient's to color
   * 
   * @see JToolBar#JToolBar(int)
   */
  public GradientToolBar(int orientation, boolean gradient, Color fromColor,
      Color toColor) {
    this(null, orientation, gradient, fromColor, toColor);
  }

  /**
   * Sets the gradient's to color
   * 
   * @param toColor
   *          the color
   */
  public final void setToColor(Color toColor) {
    Color old = this.toColor;

    this.toColor = toColor == null ? getBackground() : toColor;

    firePropertyChange("toColor", old, toColor);

    if (toColor != old) {
      repaint();
    }
  }

  /**
   * Sets the gradient's from color
   * 
   * @param fromColor
   *          the color
   */
  public final void setFromColor(Color fromColor) {
    Color old = this.fromColor;

    this.fromColor = fromColor == null ? getBackground() : fromColor;

    firePropertyChange("fromColor", old, fromColor);

    if (fromColor != old) {
      repaint();
    }
  }

  /**
   * @param on
   *          true if gradient should be drawn
   */
  public final void setGradient(boolean on) {
    boolean old = gradient;

    gradient = on;
    firePropertyChange("gradient", old, on);

    if (old != on) {
      repaint();
    }
  }

  @Override
  protected final void paintComponent(Graphics g) {
    super.paintComponent(g);

    if (gradient && isOpaque()) {
      GradientPaint paint =
          new GradientPaint(0, 0, fromColor, 0, getHeight() - 1, toColor);
      ((Graphics2D) g).setPaint(paint);
      g.fillRect(0, 0, getWidth(), getHeight());
    }
  }

  @Override
  protected JButton createActionComponent(Action a) {
    JButton button = super.createActionComponent(a);

    button.setOpaque(false);

    return button;
  }

}

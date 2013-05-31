/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.decoration;

import java.awt.Graphics2D;
import java.awt.Image;
import java.io.Serializable;

/**
 * Implement this interface to provide custom transition effects that can be
 * used with the {@link TransitionDecoration}.
 * 
 * @author Stefan Rybacki
 */
public interface ITransition extends Serializable {
  /**
   * combines {@code fromImage} and {@code toImage} according to {@code aniStep}
   * (which defines the current animation position usually between 0 and 1) and
   * draws the result on {@code g}
   * 
   * @param g
   *          the graphics context to draw on
   * @param fromImage
   *          the transition start image
   * @param toImage
   *          the final image of the transition
   * @param aniStep
   *          the current position of the transition 0 means start 1 means end
   * @param width
   *          the width of the drawing area
   * @param height
   *          the height of the drawing area
   */
  void drawTransition(Graphics2D g, Image fromImage, Image toImage,
      double aniStep, int width, int height);
}

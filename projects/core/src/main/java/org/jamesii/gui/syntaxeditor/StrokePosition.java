/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.syntaxeditor;

/**
 * Defines stroke positions for info token rendering. UNDERLINE would underline
 * text using specified stroke CROSSOUT would cross out the text using specified
 * stroke RECTANGLE would draw a rectangle around the text using the specified
 * stroke NONE would draw nothing at all
 * 
 * @author Stefan Rybacki
 */
public enum StrokePosition {
  /**
   * the stroke will underline the token
   */
  UNDERLINE, /**
   * the token will be stroked out
   */
  CROSSOUT, /**
   * /** the token will be enclosed in a rectangle shape having
   * background color fill (because info tokens are drawn after text the alpha
   * of the returned background color is halfed to see the actual text through
   * the rectangle)
   */
  RECTANGLE,
  /**
   * no stroking at all
   */
  NONE
}

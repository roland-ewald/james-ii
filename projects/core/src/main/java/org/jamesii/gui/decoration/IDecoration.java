/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.decoration;

import java.awt.Graphics2D;
import java.io.Serializable;

/**
 * Implement this interface if you want to add custom decorations that can be
 * used by the {@link Decorator}. There is also a class
 * {@link DefaultDecoration} you can subclass that handles some stuff for you
 * already.
 * 
 * @author Stefan Rybacki
 * 
 * @see DefaultDecoration
 */
public interface IDecoration extends Serializable {

  /**
   * Override paint method to paint entire decorator. It is discouraged to
   * inherit from this class. Subclass {@link DefaultDecoration} instead.
   * 
   * @param g
   *          the graphics context to paint on
   * @param d
   *          the decorator responsible for the decoration
   */
  void paint(Graphics2D g, Decorator d);

  /**
   * Gives the decoration the ability to set up the decorator as it needs it it
   * to be. So it might change insets or other properties.
   * 
   * @param d
   *          the decorator to be installed on
   */
  void setup(Decorator d);

  /**
   * Called if the decoration is replaced by another decoration within the
   * decorator
   * 
   * @param d
   *          the decorator the decoration was removed from
   */
  void removeFrom(Decorator d);
}

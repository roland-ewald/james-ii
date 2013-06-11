/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.decoration;

import java.awt.Graphics2D;

/**
 * Subclass this class to define your own decorations. Override
 * {@link #paintDecoration(Graphics2D, Decorator)} to implement your own
 * paintings.
 * 
 * @author Stefan Rybacki
 */
public class DefaultDecoration implements IDecoration {
  /**
   * Serialization ID
   */
  private static final long serialVersionUID = -1269896140489556188L;

  /**
   * the decorator holding the decoration
   */
  private transient Decorator decorator;

  @Override
  public final void paint(Graphics2D g, Decorator d) {
    paintDecoration(g, d);
  }

  /**
   * Override this method to paint custom decorations on top of underlying
   * components
   * 
   * @param g
   *          graphics context to paint on
   * @param d
   *          decorator the decoration should be painted for
   */
  protected void paintDecoration(Graphics2D g, Decorator d) {
    d.paint(g);
  }

  @Override
  public void setup(Decorator d) {
    if (decorator != null) {
      throw new IllegalArgumentException(
          "Decoration already belongs to another decorator!");
    }
    if (d == null) {
      throw new IllegalArgumentException("Decorator can't be null");
    }
    decorator = d;
  }

  /**
   * @return the decorator the decoration is used in
   */
  public final Decorator getDecorator() {
    return decorator;
  }

  @Override
  public void removeFrom(Decorator d) {
    decorator = null;
  }

}

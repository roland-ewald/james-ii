/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.decoration;

import java.awt.Component;
import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 * Helper class that is used to place additional components on top of decorated
 * components.
 * 
 * @author Stefan Rybacki
 * 
 */
class DecoratorPanel extends JComponent {
  /**
   * Serialization ID
   */
  private static final long serialVersionUID = -3516262148151656958L;

  /**
   * Creates a new DecoratorPanel
   */
  public DecoratorPanel() {
    // make it translucent
    setOpaque(false);
  }

  @Override
  public boolean contains(int x, int y) {
    // run thru all components on decorator panel and look for components that
    // occupy the point x,y
    for (int i = 0; i < getComponentCount(); i++) {
      Component c = getComponent(i);
      Point point = SwingUtilities.convertPoint(this, new Point(x, y), c);
      if (c.isVisible() && c.contains(point)) {
        return true;
      }
    }

    // if no component on decorator panels was found return default value
    return false;
  }

}

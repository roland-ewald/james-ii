/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.list.view.item;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.Icon;

/**
 * 
 * @author Jan Himmelspach
 * 
 */
public class ImageIcon implements Icon {

  /**
   * The dimension of the icon.
   */
  private Dimension dim;

  /**
   * The content / image.
   */
  private Image image;

  public ImageIcon(Dimension d, Image i) {
    dim = d;
    image = i;
  }

  @Override
  public int getIconHeight() {
    return dim.height;
  }

  @Override
  public int getIconWidth() {
    return dim.width;
  }

  @Override
  public void paintIcon(Component c, Graphics g, int x, int y) {

    g.drawImage(image.getScaledInstance(getIconWidth(), getIconHeight(),
        java.awt.Image.SCALE_SMOOTH), x, y, null);
  }
}
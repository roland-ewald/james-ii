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
import java.awt.Image;

import javax.swing.JPanel;

/**
 * Panel that is able to display an image as background using a specified alpha
 * value
 * 
 * @author Stefan Rybacki
 * 
 */
public class ImagePanel extends JPanel {
  /**
   * Serialization ID
   */
  private static final long serialVersionUID = -2100396690754079612L;

  /**
   * image to display on panel
   */
  private Image image;

  /**
   * indicates the alpha value of the displayed image
   */
  private double alpha = 0.0;

  /**
   * Creates a new {@link ImagePanel}
   * 
   * @param image
   */
  public ImagePanel(Image image) {
    super();
    this.image = image;
  }

  @Override
  public boolean imageUpdate(Image img, int infoflags, int x, int y, int w,
      int h) {
    if (img == image) {
      revalidate();
    }
    return super.imageUpdate(img, infoflags, x, y, w, h);
  }

  @Override
  protected final void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (alpha < 1.0) {
      if (image != null) {
        g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
      } else {
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint paint =
            new GradientPaint(0, 0, getBackground().darker(), getWidth(),
                getHeight(), getBackground().brighter());
        g2d.setPaint(paint);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.setPaint(null);
      }
    }
    g.setColor(new Color(1f, 1f, 1f, (float) alpha));
    g.fillRect(0, 0, getWidth(), getHeight());
  }

  /**
   * Sets the displayed image's alpha value
   * 
   * @param d
   *          the alpha value between 0 and 1
   */
  public final void setImageAlpha(double d) {
    alpha = 1.0 - d;
    repaint();
  }

  /**
   * @return the set image
   */
  public Image getImage() {
    return image;
  }
}

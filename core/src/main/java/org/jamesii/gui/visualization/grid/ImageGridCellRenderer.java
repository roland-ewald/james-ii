/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.grid;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Shape;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple image grid cell renderer which is able to draw previously registered
 * images for specified object values in a grid cell. Use
 * {@link #setKeepAspectRatio(boolean)} if you want to have the image centered
 * and scaled according to its aspect ratio.
 * <p/>
 * Rendering might be slow if large images are used.
 * 
 * @author Stefan Rybacki
 */
public class ImageGridCellRenderer extends AbstractGridCellRenderer {
  // TODO sr137: for each image create a mipmap like structure to
  // accelerate rendering of large images
  // by using appropriate mipmap levels
  /**
   * The image to value mapping.
   */
  private final Map<Object, Image> imageMapping = new ConcurrentHashMap<>();

  /**
   * The keep aspect ratio flag.
   */
  private boolean keepAspectRatio = false;

  @Override
  public void draw(Grid2D sender, Graphics g, int x, int y, int width,
      int height, Shape shape, int cellX, int cellY, Object value,
      boolean isSelected, boolean hasFocus) {

    Image image = imageMapping.get(value);
    if (image != null) {
      int nx = x;
      int ny = y;
      int nwidth = width - 1;
      int nheight = height - 1;

      // if keep aspect ratio is enabled recalculate x, y, width and
      // height
      if (isKeepAspectRatio()) {
        int iwidth = image.getWidth(null);
        int iheight = image.getHeight(null);

        // only calculate new position and size if iwidth and iheight
        // are actually set
        if (iwidth > 0 && iheight > 0) {
          double sx = (double) nwidth / iwidth;
          double sy = (double) nheight / iheight;
          double scalex = Math.min(sx, sy);
          double scaley = scalex;

          // center the image
          nx = x + (int) ((nwidth - (iwidth * scalex)) / 2);
          ny = y + (int) ((nheight - (iheight * scaley)) / 2);
          nwidth = (int) (iwidth * scalex);
          nheight = (int) (iheight * scaley);
        }
      }

      g.drawImage(image, nx, ny, nwidth, nheight, sender);
    }

    if (isSelected) {
      g.setColor(new Color(sender.getSelectionColor().getRed(), sender
          .getSelectionColor().getGreen(),
          sender.getSelectionColor().getBlue(), 128));
      g.fillRect(x, y, width - 1, height - 1);
    }
    if (hasFocus) {
      g.setColor(sender.getFocusColor());
      g.drawRect(x, y, width - 1, height - 1);
    }
  }

  /**
   * Set image mapping.
   * 
   * @param value
   *          the value
   * @param image
   *          the image
   */
  public synchronized void setImageMapping(Object value, Image image) {
    imageMapping.put(value, image);
    fireRenderingChanged();
  }

  /**
   * Remove image mapping.
   * 
   * @param value
   *          the value
   */
  public synchronized void removeImageMapping(Object value) {
    imageMapping.remove(value);
    fireRenderingChanged();
  }

  /**
   * Sets the keep aspect ratio.
   * 
   * @param keepAspectRatio
   *          the new keep aspect ratio
   */
  public void setKeepAspectRatio(boolean keepAspectRatio) {
    this.keepAspectRatio = keepAspectRatio;
    fireRenderingChanged();
  }

  /**
   * Checks if is keep aspect ratio.
   * 
   * @return true, if is keep aspect ratio
   */
  public boolean isKeepAspectRatio() {
    return keepAspectRatio;
  }

}

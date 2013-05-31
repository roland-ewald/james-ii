/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.decoration;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;

/**
 * This transition is used to zoom the from image out by zooming and by
 * increasing alpha value at the same time
 * 
 * @author Stefan Rybacki
 */
public class ZoomTransition implements ITransition {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 2182546356530259091L;

  /**
   * zoom factor
   */
  private final float factor;

  /**
   * Constructs a standard zoom transition using a zoom factor of 3
   */
  public ZoomTransition() {
    factor = 3f;
  }

  /**
   * Constructs a zoom transition using a custom zoom factor
   * 
   * @param factor
   *          the custom zoom factor
   */
  public ZoomTransition(float factor) {
    this.factor = factor;
  }

  @Override
  public final void drawTransition(Graphics2D g, Image fromImage,
      Image toImage, double aniStep, int width, int height) {
    // first draw old image
    // draw target image
    g.drawImage(toImage, 0, 0, null);

    // zoom size
    int w = (int) (getFactor() * width * aniStep) + width;
    int h = (int) (getFactor() * height * aniStep) + height;

    // now use alpha composite to draw target image on top
    AlphaComposite composite =
        AlphaComposite
            .getInstance(AlphaComposite.SRC_OVER, 1 - (float) aniStep);
    g.setComposite(composite);

    g.drawImage(fromImage, (width - w) / 2, (height - h) / 2, w, h, null);

  }

  /**
   * @return the zoom factor
   */
  public float getFactor() {
    return factor;
  }

}

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
 * This transition is used to blend from old image to new image.
 * 
 * @author Stefan Rybacki
 */
public class BlendTransition implements ITransition {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 2182546356530259091L;

  @Override
  public final void drawTransition(Graphics2D g, Image fromImage,
      Image toImage, double aniStep, int width, int height) {
    g.drawImage(fromImage, 0, 0, null);

    // now use alpha composite to draw target image on top
    AlphaComposite composite =
        AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) aniStep);
    g.setComposite(composite);

    // draw target image
    g.drawImage(toImage, 0, 0, null);
  }

}

/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.decoration;

import java.awt.Graphics2D;
import java.awt.Image;

/**
 * Simply flips from one image to the other by doing a simple flip which is
 * basically just shrinking the one image to width 0 and expanding the other
 * from width 0 to original width afterwards.
 * 
 * @author Stefan Rybacki
 * 
 */
public class FlipTransition implements ITransition {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 4965988795844133555L;

  @Override
  public final void drawTransition(Graphics2D g, Image fromImage,
      Image toImage, double aniStep, int width, int height) {
    if (aniStep <= 0.5) {
      int w = (int) (width * (0.5 - aniStep) * 2);
      g.drawImage(fromImage, width / 2 - w / 2, 0, w, height, null);
    } else {
      int w = (int) (width * (aniStep - 0.5) * 2);
      g.drawImage(toImage, width / 2 - w / 2, 0, w, height, null);
    }

  }

}

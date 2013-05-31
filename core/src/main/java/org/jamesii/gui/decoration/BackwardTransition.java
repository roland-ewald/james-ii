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
 * Simple backward transition that pushes the old image out to the right where
 * at the same time it pushes the new image in from the left.
 * 
 * @author Stefan Rybacki
 * 
 */
public class BackwardTransition implements ITransition {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 4797880765064061865L;

  @Override
  public final void drawTransition(Graphics2D g, Image fromImage,
      Image toImage, double aniStep, int width, int height) {
    // simply push the fromImage out to the right and at the same time push
    // toImage in from the left
    g.drawImage(fromImage, (int) (width * aniStep), 0, null);
    g.drawImage(toImage, (int) (width * aniStep) - width, 0, null);
  }

}

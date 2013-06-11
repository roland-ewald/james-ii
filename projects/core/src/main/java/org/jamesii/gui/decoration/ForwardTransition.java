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
 * Simple forward transition that pushes the old image out to the left where at
 * the same time it pushes the new image in from the right.
 * 
 * @author Stefan Rybacki
 * 
 */
public class ForwardTransition implements ITransition {

  /**
   * Serializaiton ID
   */
  private static final long serialVersionUID = -1376418669379343608L;

  @Override
  public final void drawTransition(Graphics2D g, Image fromImage,
      Image toImage, double aniStep, int width, int height) {
    // simply push the fromImage out to the left and at the same time push
    // toImage in from the right
    g.drawImage(fromImage, (int) (-width * aniStep), 0, null);
    g.drawImage(toImage, (int) (-width * aniStep) + width, 0, null);
  }

}

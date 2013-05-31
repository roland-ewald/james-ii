/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.graph;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.Serializable;

/**
 * The Interface IRenderer.
 * 
 * @author Jan Himmelspach
 */
public interface IRenderer extends Serializable {

  /**
   * Paint the element to be rendered to the passed graphics context.
   * 
   * @param g
   *          the graphic context the object shall be drawn on
   */
  void paint(Graphics2D g);

  /**
   * Gets the bounding box.
   * 
   * @return the bounding box
   */
  Rectangle getBoundingBox();

}

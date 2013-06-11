/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.graph.mapper;

import java.io.Serializable;

import org.jamesii.gui.visualization.graph.IRenderer;

/**
 * The Interface IRendererMapper.
 * 
 * @author Jan Himmelspach
 */
public interface IRendererMapper extends Serializable {

  /**
   * Gets the renderer to be used for the object passed.
   * 
   * @param object
   *          the object
   * 
   * @return the renderer to be used
   */
  IRenderer getRenderer(Object object);

}

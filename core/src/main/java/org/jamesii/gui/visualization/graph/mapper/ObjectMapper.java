/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.graph.mapper;

import java.util.HashMap;
import java.util.Map;

import org.jamesii.gui.visualization.graph.IRenderer;

/**
 * The Class ObjectMapper.
 */
public class ObjectMapper implements IRendererMapper {

  /** The mappings. */
  private Map<Object, IRenderer> mappings = new HashMap<>();

  @Override
  public IRenderer getRenderer(Object object) {
    return mappings.get(object);
  }

  /**
   * Adds a mapping.
   * 
   * @param o
   *          the o
   * @param r
   *          the r
   */
  public void addMapping(Object o, IRenderer r) {
    mappings.put(o, r);
  }

}

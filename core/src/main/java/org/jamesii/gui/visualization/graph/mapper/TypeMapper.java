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
 * The Class TypeMapper.
 */
public class TypeMapper implements IRendererMapper {

  /** The mappings. */
  private Map<Class<?>, IRenderer> mappings = new HashMap<>();

  @Override
  public IRenderer getRenderer(Object object) {
    return mappings.get(object.getClass());
  }

  /**
   * Adds a mapping.
   * 
   * @param c
   *          the c
   * @param r
   *          the r
   */
  public void addMapping(Class<?> c, IRenderer r) {
    mappings.put(c, r);
  }

}

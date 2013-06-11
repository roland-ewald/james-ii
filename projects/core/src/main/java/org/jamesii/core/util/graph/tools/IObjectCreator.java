/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph.tools;

import org.jamesii.core.util.graph.ISimpleGraph;

/**
 * Interface to allow the annotation of a generated graph.
 * 
 * @author Roland Ewald
 */
public interface IObjectCreator {

  /**
   * Creates the objects.
   * 
   * @param graph
   *          the graph
   */
  void createObjects(ISimpleGraph graph);

}

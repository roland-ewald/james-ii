/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.graph;

import java.io.Serializable;
import java.util.List;

/**
 * The Interface IGraphModel. The data model of a graph.
 * 
 * @author Jan Himmelspach
 */
public interface IGraphModel extends Serializable {

  /**
   * Gets the list of nodes in the graph.
   * 
   * @return the nodes
   */
  List<?> getNodes();

  /**
   * Gets the edges.
   * 
   * @return the edges
   */
  List<?> getEdges();
}

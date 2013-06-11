/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph.paths;

import java.util.Map;

import org.jamesii.core.util.graph.IGraph;
import org.jamesii.core.util.graph.LabeledEdge;

/**
 * Abstract class for algorithms that compute the shortest paths for a given
 * node and a given {@link org.jamesii.core.util.graph.ILabeledGraph}.
 * 
 * @param <V>
 *          the type of vertices
 * 
 * @author Jan Himmelspach
 * @version 1.0
 */

public abstract class ShortestPath<V> {

  /**
   * Compute the shortest paths to the given node. Returns a list of all paths
   * from all nodes of the graph to the given node. INFINITY as path length
   * value means that there is no path between two nodes
   * 
   * @param graph
   *          the graph
   * @param node
   *          the node
   * 
   * @return length of paths from each node in the graph to the given node
   */
  public abstract Map<V, Double> compute(
      IGraph<V, ? extends LabeledEdge<V, Double>> graph, V node);

}

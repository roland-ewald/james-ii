/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph.paths;

import java.util.List;

import org.jamesii.core.util.graph.IGraph;
import org.jamesii.core.util.graph.LabeledEdge;

/**
 * Search for the shortest path between two nodes.
 * 
 * @param <V>
 *          type of vertices
 * @param <E>
 *          type of edges
 * 
 * @author Jan Himmelspach
 */
public abstract class SingleShortestPath<V, E extends LabeledEdge<V, Double>> {

  /**
   * Compute the shortest path.
   * 
   * @param graph
   *          the graph
   * @param startNode
   *          the start node
   * @param endNode
   *          the end node
   * 
   * @return the array list< e>
   */
  public abstract List<E> compute(IGraph<V, E> graph, V startNode, V endNode);

}

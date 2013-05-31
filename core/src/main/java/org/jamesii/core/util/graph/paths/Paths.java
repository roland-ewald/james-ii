/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph.paths;

import org.jamesii.core.util.graph.IGraph;
import org.jamesii.core.util.graph.NodeMatrix;

/**
 * Class to compute shortest paths between all nodes of a {@link IGraph}.
 * 
 * @author Jan Himmelspach
 * 
 * @param <R>
 *          type of the label
 */
public abstract class Paths<R> {

  /**
   * Compute all paths. Returns a matrix containing link information The result
   * maps onto the adjacency matrix
   * 
   * @param graph
   *          the graph for which the paths shall be computed
   * @param <V>
   *          the type of vertices
   * @return length of paths from each node in the graph to the given node
   */
  public abstract <V> NodeMatrix<V, R> compute(IGraph<V, ?> graph);

}

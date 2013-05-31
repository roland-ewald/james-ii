/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph.paths;

import java.util.List;

import org.jamesii.core.util.graph.Edge;
import org.jamesii.core.util.graph.IGraph;
import org.jamesii.core.util.graph.NodeMatrix;

/**
 * This class computes all shortest paths between the nodes of a graph and
 * remembers the resulting edge lists internally.
 * 
 * TODO: We need to integrate a differentiation here: directed/undirected
 * graph!.
 * 
 * @param <V>
 *          the type of vertices
 * @param <E>
 *          the type of edges
 * @author Jan Himmelspach
 */
public abstract class ShortestPaths<V, E extends Edge<V>> {

  /** Slot for storing the edge lists connecting two nodes. */
  private NodeMatrix<V, List<E>> connections = null;

  /**
   * Create an instance, but do not initialise the list of edges.
   */
  public ShortestPaths() {
    super();
  }

  /**
   * Create an instance and directly compute the edge lists.
   * 
   * @param graph
   *          the graph
   */
  public ShortestPaths(IGraph<V, E> graph) {
    super();
    connections = new NodeMatrix<>(graph.getVertices(), null);
    compute(graph);
  }

  /**
   * Compute all shortest paths in the graph. The result will be stored in the
   * internal connections attribute. The results can be accessed through the
   * getEdgeList method
   * 
   * @param graph
   *          the graph
   */
  public abstract void compute(IGraph<V, E> graph);

  /**
   * Returns the list of edges on the shortest path between the given startNode
   * and endNode.
   * 
   * @param startNode
   *          the starting node
   * @param endNode
   *          the end node
   * 
   * @return the list of edges on the shortest path between startNode and
   *         endNode
   */
  public List<E> getEdgesList(V startNode, V endNode) {
    return connections.getValue(startNode, endNode);
  }

}

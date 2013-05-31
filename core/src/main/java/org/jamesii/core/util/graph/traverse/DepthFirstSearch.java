/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph.traverse;

import java.util.List;

import org.jamesii.core.util.ICallBack;
import org.jamesii.core.util.graph.Edge;
import org.jamesii.core.util.graph.IGraph;

/**
 * Implements depth-first search.
 * 
 * @param <N>
 *          the type of the nodes to be traversed by DFS
 */
public class DepthFirstSearch<N> extends Traverse<N> {

  /** The unvisited vertices. */
  private List<N> unvisited;

  /**
   * Traverses the given graph using DFS. Returns vertices one by one.
   * 
   * @param callBack
   *          the call back
   * @param graph
   *          the graph
   * @param node
   *          the node
   */
  private void processNeighbours(ICallBack<N> callBack,
      IGraph<N, ? extends Edge<?>> graph, N node) {

    // if unvisited is not empty do if vertex has not been visited process(node)
    // mark as visited
    if (!unvisited.contains(node)) {
      return;
    }

    callBack.process(node);
    unvisited.remove(node);

    // for all neighbours i do: processNeighbours(i)
    List<N> neighbours = graph.getNeighboursOfNode(node);
    for (N n : neighbours) {
      processNeighbours(callBack, graph, n);
    }
  }

  /**
   * Calls processNeigbours(...) with the first node of the give graph.
   * 
   * @param graph
   *          the graph
   * @param callBack
   *          the call back
   */
  @Override
  public void traverse(IGraph<N, ? extends Edge<?>> graph, ICallBack<N> callBack) {
    unvisited = graph.getVertices();
    processNeighbours(callBack, graph, graph.getVertices().get(0));

  }

}
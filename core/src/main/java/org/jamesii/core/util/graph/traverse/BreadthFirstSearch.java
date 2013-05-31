/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph.traverse;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.util.ICallBack;
import org.jamesii.core.util.graph.Edge;
import org.jamesii.core.util.graph.IGraph;

/**
 * Breadth-first search to search a graph.
 * 
 * @author Nico Eggert
 * 
 * @param <N>
 *          type of the node
 */
public class BreadthFirstSearch<N> extends Traverse<N> {

  /** List of nodes which have not been searched so far. */
  private List<N> unvisited = new ArrayList<>();

  /** List of nodes which have been searched so far. */
  private List<N> visited = new ArrayList<>();

  /**
   * Traverses the given graph using BFS. Returns vertices one by one.
   * 
   * @param callBack
   *          the call back method
   * 
   * @param graph
   *          the graph to be searched
   * 
   * @param node
   *          the actual node
   */
  private void processNeighbours(ICallBack<N> callBack,
      IGraph<N, ? extends Edge<?>> graph, N node) {
    process(callBack, node);
    unvisited.addAll(graph.getNeighboursOfNode(node));
    while (!unvisited.isEmpty()) {
      node = unvisited.get(0);
      process(callBack, node);
      for (N neighbour : graph.getNeighboursOfNode(node)) {
        if (!visited.contains(neighbour)) {
          unvisited.add(neighbour);
        }
      }
    }
  }

  /**
   * Puts the actual node from unvisited to visited and triggers the call back
   * method.
   * 
   * @param callBack
   *          call back method
   * @param node
   *          the actual node
   */
  protected void process(ICallBack<N> callBack, N node) {
    callBack.process(node);
    unvisited.remove(node);
    visited.add(node);
  }

  @Override
  public void traverse(IGraph<N, ? extends Edge<?>> graph, ICallBack<N> callBack) {
    unvisited.clear();
    visited.clear();
    if (graph.getVertexCount() > 0) {
      processNeighbours(callBack, graph, graph.getVertices().get(0));
    }
  }

}

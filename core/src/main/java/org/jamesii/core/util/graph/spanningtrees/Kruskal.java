/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph.spanningtrees;

import java.util.ArrayList;
import java.util.PriorityQueue;

import org.jamesii.core.util.graph.IGraph;
import org.jamesii.core.util.graph.LabeledEdge;
import org.jamesii.core.util.graph.trees.ITree;
import org.jamesii.core.util.graph.trees.Tree;

/**
 * Kruskal's Algorithm creates the minimum spanning tree of a given graph.
 * 
 * Create a forest F (a set of trees), where each vertex in the graph is a
 * separate tree create a set S containing all the edges in the graph while S is
 * nonempty o remove an edge with minimum weight from S o if that edge connects
 * two different trees, then add it to the forest, combining two trees into a
 * single tree o otherwise discard that edge
 * 
 * @author Nico Eggert
 * 
 * @param <V>
 *          type of vertices
 * @param <E>
 *          type of edges
 */
public class Kruskal<V, E extends LabeledEdge<V, ? extends Comparable<?>>>
    extends SpanningTree<V, E> {

  /**
   * Returns (one of) the minimum spanning tree(s) of a graph.
   */
  @Override
  public ITree<V, E> find(IGraph<V, E> graph) {
    int i, n = graph.getVertexCount();

    ITree<V, E> minSpanTree = new Tree<>(new ArrayList<V>());

    PriorityQueue<E> queue = new PriorityQueue<>(0);

    // copy all vertices
    for (i = 0; i < n; i++) {
      minSpanTree.addVertex(graph.getVertices().get(i));
    }

    // Kruskal's Algorithm
    while (!queue.isEmpty()) {
      if (!(minSpanTree.getVertices().contains(queue.peek().getFirstVertex()) && minSpanTree
          .getVertices().contains(queue.peek().getSecondVertex()))) {
        if (minSpanTree.getVertices().contains(queue.peek().getFirstVertex())
            || minSpanTree.getVertices().contains(
                queue.peek().getSecondVertex())) {
          minSpanTree.addEdge(queue.peek());
        }
      }

      queue.remove();
    }

    return minSpanTree;
  }

}

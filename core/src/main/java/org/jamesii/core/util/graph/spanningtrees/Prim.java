/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph.spanningtrees;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import org.jamesii.core.util.graph.IGraph;
import org.jamesii.core.util.graph.LabeledEdge;
import org.jamesii.core.util.graph.trees.ITree;
import org.jamesii.core.util.graph.trees.Tree;

/**
 * Implementation of Prim's algorithm.
 * 
 * Prim's Algorithm
 * 
 * 1. Choose a vertex of the graph G as initial tree E
 * 
 * 2. Create a set of all edges in G connecting a vertex of E to a vertex
 * outside of E
 * 
 * 3. Out of this set, choose an edge with the lowest weight (which will not
 * form a cycle) and add it with the respective vertex to E
 * 
 * 4. Repeat 2-3 until all vertices of G are in E
 * 
 * @param <V>
 *          type of vertices
 * @param <E>
 *          type of edges
 * 
 * @author Nico Eggert
 */
public class Prim<V, E extends LabeledEdge<V, ? extends Comparable<?>>> extends
    SpanningTree<V, E> {

  /**
   * Find minimum spanning tree.
   * 
   * @param graph
   *          the graph
   * 
   * @return Returns one of the minimum spanning trees of a graph.
   */
  @Override
  public ITree<V, E> find(IGraph<V, E> graph) {
    List<V> outside = graph.getVertices();
    ITree<V, E> minSpanTree = new Tree<>(new ArrayList<V>());

    // TODO make sure the edges are sorted according to their weight
    PriorityQueue<E> edges = new PriorityQueue<>(0);
    int i;

    // pick an arbitrary vertex
    minSpanTree.addVertex(outside.get(0));
    outside.remove(0);

    while (!outside.isEmpty()) {
      // create the set of all outgoing edges and incoming edges for all
      // vertices in the tree
      for (i = 0; i < minSpanTree.getVertices().size(); i++) {
        edges.addAll(graph.getEdges(minSpanTree.getVertices().get(i)));
      }

      // get the first edge (minimum) and check whether both of the edges are
      // already in the tree
      if (minSpanTree.getVertices().contains(edges.peek().getFirstVertex())
          && minSpanTree.getVertices().contains(edges.peek().getSecondVertex())) {
        edges.remove();
      } else {
        if (minSpanTree.getVertices().contains(edges.peek().getFirstVertex())) {
          minSpanTree.addVertex(edges.peek().getSecondVertex());
        } else {
          minSpanTree.addVertex(edges.peek().getFirstVertex());
        }

        minSpanTree.addEdge(edges.peek());
        edges.remove();

      }

    }
    return minSpanTree;
  }

}

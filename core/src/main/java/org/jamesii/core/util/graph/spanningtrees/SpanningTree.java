/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph.spanningtrees;

import org.jamesii.core.util.graph.IGraph;
import org.jamesii.core.util.graph.LabeledEdge;
import org.jamesii.core.util.graph.trees.ITree;

/**
 * Super class for algorithms to find a spanning tree.
 * 
 * @param <V>
 *          type of vertices
 * @param <E>
 *          type of edges
 * @author Nico Eggert
 */

public abstract class SpanningTree<V, E extends LabeledEdge<V, ? extends Comparable<?>>> {

  /**
   * Find spanning tree.
   * 
   * @param graph
   *          the graph
   * 
   * @return Returns the minimum spanning tree of a graph.
   */
  public abstract ITree<V, E> find(IGraph<V, E> graph);
}

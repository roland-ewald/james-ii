/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Base class for directed graphs.
 * 
 * @param <V>
 *          Type of vertices
 * @param <E>
 *          Type of edges
 * 
 * @author Roland Ewald
 */
public abstract class BasicDirectedGraph<V, E extends Edge<V>> extends
    BasicGraph<V, E> implements IDirectedGraph<V, E> {

  /** Serialisation ID. */
  private static final long serialVersionUID = 1167683623202521251L;

  @Override
  public abstract Collection<E> getIncomingEdges(V vertex);

  @Override
  public abstract Collection<E> getOutgoingEdges(V vertex);

  @Override
  public boolean isDirected() {
    return true;
  }

  @Override
  public List<V> getNeighboursOfNode(V vertex) {
    List<V> result = new ArrayList<>(getSources(vertex));
    result.addAll(getTargets(vertex));
    return result;
  }

  @Override
  public Collection<V> getSources(V vertex) {
    Collection<E> incomingEdges = getIncomingEdges(vertex);
    Collection<V> result = new ArrayList<>(incomingEdges.size());
    for (E edge : incomingEdges) {
      result.add(edge.getFirstVertex());
    }
    return result;
  }

  @Override
  public Collection<V> getTargets(V vertex) {
    Collection<E> outgoingEdges = getOutgoingEdges(vertex);
    Collection<V> result = new ArrayList<>(outgoingEdges.size());
    for (E edge : outgoingEdges) {
      result.add(edge.getSecondVertex());
    }
    return result;
  }

}

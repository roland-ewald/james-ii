/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph;

import java.util.Collection;

/**
 * Interface for all directed graphs.
 * 
 * @param <V>
 *          vertex type
 * @param <E>
 *          edge type
 * 
 * @author Roland Ewald
 */
public interface IDirectedGraph<V, E extends Edge<V>> extends IGraph<V, E> {

  /**
   * Get the incoming edges of the given vertex.
   * 
   * @param vertex
   *          the vertex
   * @return incoming edges of the given vertex
   */
  Collection<E> getIncomingEdges(V vertex);

  /**
   * Get the outgoing edges of the given vertex.
   * 
   * @param vertex
   *          the vertex
   * @return outgoing edges of the given vertex
   */
  Collection<E> getOutgoingEdges(V vertex);

  /**
   * Get nodes with edges directed to the given vertex.
   * 
   * @param vertex
   *          the vertex
   * @return source nodes of the vertex' incoming edges
   */
  Collection<V> getSources(V vertex);

  /**
   * Get nodes at the end of edges directed from the given vertex.
   * 
   * @param vertex
   *          the vertex
   * @return target nodes of the vertex' outgoing edges
   */
  Collection<V> getTargets(V vertex);

}

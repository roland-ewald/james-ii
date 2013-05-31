/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph;

import java.io.Serializable;

import org.jamesii.core.serialization.IConstructorParameterProvider;
import org.jamesii.core.serialization.SerialisationUtils;

/**
 * Basic edge class.
 * 
 * @param <V>
 *          Type of vertices this edge consists of
 * 
 * @author Jan Himmelspach
 * @version 1.0
 */
public class Edge<V> implements Serializable {
  static {
    SerialisationUtils.addDelegateForConstructor(Edge.class,
        new IConstructorParameterProvider<Edge<?>>() {
          @Override
          public Object[] getParameters(Edge<?> edge) {
            Object[] params =
                new Object[] { edge.getFirstVertex(), edge.getSecondVertex() };
            return params;
          }
        });
  }

  /** Serialisation ID. */
  private static final long serialVersionUID = 4789357055382168674L;

  /** First vertex. */
  private final V firstVertex;

  /** Second vertex. */
  private final V secondVertex;

  /**
   * Create a new edge between the first and the second given vertex. Be
   * careful. The order of the vertices MAY matter! The V objects are arranged
   * in some ordered set within the graph classes, so be sure that first <
   * second holds.
   * 
   * @param first
   *          the first
   * @param second
   *          the second
   */
  public Edge(V first, V second) {
    super();
    this.firstVertex = first;
    this.secondVertex = second;
  }

  /**
   * Gets the first vertex.
   * 
   * @return the first vertex
   */
  public V getFirstVertex() {
    return firstVertex;
  }

  /**
   * Gets the second vertex.
   * 
   * @return the second vertex
   */
  public V getSecondVertex() {
    return secondVertex;
  }

}

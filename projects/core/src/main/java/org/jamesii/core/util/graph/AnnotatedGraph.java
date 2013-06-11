/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple implementation of a graph that supports annotations.
 * 
 * Date: 12.12.2006
 * 
 * @author Roland Ewald
 * 
 * @param <V>
 *          the type of the vertices
 * @param <E>
 *          the type of the edges (here they need to extend (@link
 *          AnnotatedEdge}
 * @param <LV>
 *          the type of the vertex label
 * @param <LE>
 *          the type of the edge label
 * @param <NV>
 *          the type of the annotation of the vertex
 * @param <NE>
 *          the type of the annotation of the edge
 */
public class AnnotatedGraph<V extends Comparable<V>, E extends AnnotatedEdge<V, LE, NE>, LV, LE, NV, NE>
    extends LabeledGraph<V, E, LV, LE> implements
    IAnnotatedGraph<V, E, LV, LE, NV, NE> {

  /** Serialisation ID. */
  private static final long serialVersionUID = 6794840433683983552L;

  /** Mapping annotation => edge. */
  private Map<NE, E> annotationToEdge = new HashMap<>();

  /** Mapping annotation => vertex. */
  private Map<NV, V> annotationToVertex = new HashMap<>();

  /** Mapping edge => annotation. */
  private Map<E, NE> edgeToAnnotation = new HashMap<>();

  /** Mapping vertex => annotation. */
  private Map<V, NV> vertexToAnnotation = new HashMap<>();

  /**
   * Default constructor.
   * 
   * @param vertices
   *          the vertices
   */
  public AnnotatedGraph(V[] vertices) {
    super(vertices);
  }

  @Override
  public E getEdgeByObject(NE annotation) {
    return annotationToEdge.get(annotation);
  }

  @Override
  public NE getObjectByEdge(E edge) {
    return edgeToAnnotation.get(edge);
  }

  @Override
  public NV getObjectByVertex(V vertex) {
    return vertexToAnnotation.get(vertex);
  }

  @Override
  public V getVertexByObject(NV annotation) {
    return annotationToVertex.get(annotation);
  }

  @Override
  public boolean removeEdge(E edge) {
    boolean removalSuccess = super.removeEdge(edge);
    if (removalSuccess) {
      removeObjectOfEdge(edge);
    }
    return removalSuccess;

  }

  @Override
  public void removeObjectOfEdge(E edge) {
    NE annotation = edgeToAnnotation.remove(edge);
    if (annotation != null) {
      annotationToEdge.remove(annotation);
    }
  }

  @Override
  public void removeObjectOfVertex(V vertex) {
    NV annotation = vertexToAnnotation.remove(vertex);
    if (annotation != null) {
      annotationToVertex.remove(annotation);
    }
  }

  @Override
  public boolean removeVertex(V vertex) {
    boolean removalSuccess = super.removeVertex(vertex);
    if (removalSuccess) {
      removeObjectOfVertex(vertex);
    }
    return removalSuccess;
  }

  @Override
  public void setObjectByEdge(E edge, NE annotation) {
    edgeToAnnotation.put(edge, annotation);
    annotationToEdge.put(annotation, edge);
  }

  @Override
  public void setObjectByVertex(V vertex, NV annotation) {
    vertexToAnnotation.put(vertex, annotation);
    annotationToVertex.put(annotation, vertex);
  }

}

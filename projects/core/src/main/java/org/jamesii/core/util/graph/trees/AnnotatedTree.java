/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph.trees;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jamesii.core.util.graph.AnnotatedEdge;
import org.jamesii.core.util.graph.IAnnotatedGraph;

/**
 * Annotatable tree. Annotations should be distinct objects w.r.t.
 * {@link Object#hashCode()} and {@link Object#equals(Object)}. Ie., internally
 * mapping with annotation as keys are constructed).
 * 
 * @param <V>
 *          Type of vertices
 * @param <E>
 *          Type of edges
 * @param <LV>
 *          Label of vertices
 * @param <LE>
 *          Label of edges
 * @param <NV>
 *          Annotation of vertices
 * @param <NE>
 *          Annotation of edges
 * 
 * @author Roland Ewald
 */
public class AnnotatedTree<V, E extends AnnotatedEdge<V, LE, NE>, LV, LE, NV, NE>
    extends LabeledTree<V, E, LV, LE> implements
    IAnnotatedGraph<V, E, LV, LE, NV, NE> {

  /** Serialisation ID. */
  private static final long serialVersionUID = 1L;

  /** Mapping annotation => vertex. */
  private Map<NV, V> annotationToVertex = new HashMap<>();

  /** Mapping vertex => annotation. */
  private Map<V, NV> vertexToAnnotation = new HashMap<>();

  /** Mapping edge => annotation. */
  private Map<E, NE> edgeToAnnotation = new HashMap<>();

  /** Mapping annotation to edge. */
  private Map<NE, E> annotationToEdge = new HashMap<>();

  /**
   * Default constructor.
   * 
   * @param vertices
   *          the vertices
   */
  public AnnotatedTree(List<V> vertices) {
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

  /**
   * Retrieves root object.
   * 
   * @return object for the root node
   */
  public NV getRootObject() {
    return this.getObjectByVertex(this.getRoot());
  }

  @Override
  public V getVertexByObject(NV annotation) {
    return annotationToVertex.get(annotation);
  }

  @Override
  public void removeObjectOfEdge(E edge) {
    NE annotation = edgeToAnnotation.get(edge);
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

  /**
   * Removes the vertex.
   * 
   * @param vertex
   *          the vertex
   * 
   * @return true, if removes the vertex
   * 
   * @see org.jamesii.core.util.graph.trees.Tree#removeVertex(java.lang.Object)
   */
  @Override
  public boolean removeVertex(V vertex) {
    boolean removalSuccess = super.removeVertex(vertex);

    if (removalSuccess) {
      NV annotation = vertexToAnnotation.remove(vertex);
      if (annotation != null) {
        annotationToVertex.remove(annotation);
      }
    }

    return removalSuccess;
  }

  @Override
  public boolean removeEdge(E edge) {
    boolean removalSuccess = super.removeEdge(edge);
    if (removalSuccess) {
      NE annotation = edgeToAnnotation.remove(edge);
      if (annotation != null) {
        annotationToEdge.remove(annotation);
      }
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

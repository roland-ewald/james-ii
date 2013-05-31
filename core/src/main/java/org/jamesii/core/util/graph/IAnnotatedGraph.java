/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph;

// TODO: Auto-generated Javadoc
/**
 * Interface for graphs that can be annotated with a certain kind of object.
 * 
 * @param <V>
 *          Type of vertices
 * @param <E>
 *          Type of edge class, e.g. Edge<V> or LabeledEdge<V>
 * @param <LV>
 *          Type of vertex labels
 * @param <LE>
 *          Type of edge labels
 * @param <NV>
 *          Type of annotation objects for vertices
 * @param <NE>
 *          Type of annotation objects for edges
 * 
 * @author Roland Ewald
 */
public interface IAnnotatedGraph<V, E extends AnnotatedEdge<V, LE, NE>, LV, LE, NV, NE>
    extends ILabeledGraph<V, E, LV, LE> {

  /**
   * Get edge object by annotated object.
   * 
   * @param annotation
   *          annotated object
   * 
   * @return edge object
   */
  E getEdgeByObject(NE annotation);

  /**
   * Get annotated object by edge.
   * 
   * @param edge
   *          edge object
   * 
   * @return annotated object
   */
  NE getObjectByEdge(E edge);

  /**
   * Get annotated object by vertex.
   * 
   * @param vertex
   *          vertex object
   * 
   * @return annotated object
   */
  NV getObjectByVertex(V vertex);

  /**
   * Get vertex object by annotated object.
   * 
   * @param annotation
   *          annotated object
   * 
   * @return vertex object
   */
  V getVertexByObject(NV annotation);

  /**
   * Removes annotation from a given edge.
   * 
   * @param edge
   *          edge to be cleaned up
   */
  void removeObjectOfEdge(E edge);

  /**
   * Removes annotation from a given vertex.
   * 
   * @param vertex
   *          vertex to be cleaned up
   */
  void removeObjectOfVertex(V vertex);

  /**
   * Assigns an object to an edge.
   * 
   * @param edge
   *          edge object
   * @param annotation
   *          annotated object
   */
  void setObjectByEdge(E edge, NE annotation);

  /**
   * Assigns an object to a vertex.
   * 
   * @param vertex
   *          vertex object
   * @param annotation
   *          annotated object
   */
  void setObjectByVertex(V vertex, NV annotation);

}

/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph;

import java.util.List;
import java.util.Map;

/**
 * Interface for all graphs with labels.
 * 
 * @param <V>
 *          Type of vertices
 * @param <E>
 *          Type of edges
 * @param <LV>
 *          Type of vertex labels
 * @param <LE>
 *          Type of edge labels
 * 
 * @author Roland Ewald
 */
public interface ILabeledGraph<V, E extends LabeledEdge<V, LE>, LV, LE> extends
    IGraph<V, E> {

  /**
   * Get edge labels.
   * 
   * @return the edge labels
   */
  Map<V, Map<V, LE>> getEdgeLabels();

  /**
   * Get label for vertex.
   * 
   * @param vertex
   *          the vertex
   * 
   * @return label for vertex
   */
  LV getLabel(V vertex);

  /**
   * Get vertex by label.
   * 
   * @param vertexLabel
   *          the vertex label
   * 
   * @return the vertex by label
   */
  List<V> getVertexByLabel(LV vertexLabel);

  /**
   * Get vertex labels.
   * 
   * @return the vertex labels
   */
  Map<V, LV> getVertexLabels();

  /**
   * Sets the label of a vertex.
   * 
   * @param vertex
   *          vertex to be labelled
   * @param label
   *          Label for the vertex
   * 
   * @return true, if labelling was successful (ie, vertex existed)
   */
  boolean setLabel(V vertex, LV label);

}

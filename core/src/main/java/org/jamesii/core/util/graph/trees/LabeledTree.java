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

import org.jamesii.core.util.graph.ILabeledGraph;
import org.jamesii.core.util.graph.LabeledEdge;

/**
 * Simple implementation of a labelled tree. Vertex labels are kept in a mapping
 * vertex -> vertex label ({@link LabeledTree#vertexLabels}), edge labels are
 * stored in a nested map ({@link LabeledTree#edgeLabels}).
 * 
 * 
 * Date: 11.12.2006
 * 
 * @author Roland Ewald
 * @param <V>
 *          the type of the vertices
 * @param <E>
 *          the type of the edges
 * @param <LV>
 *          the label of the vertices
 * @param <LE>
 *          the label of the edges
 */
public class LabeledTree<V, E extends LabeledEdge<V, LE>, LV, LE> extends
    Tree<V, E> implements ILabeledGraph<V, E, LV, LE> {

  /** Serialisation ID. */
  private static final long serialVersionUID = 2617717296419639515L;

  /** The edge labels. */
  private Map<V, Map<V, LE>> edgeLabels = new HashMap<>();

  /** Storage for vertex labels. */
  private Map<V, LV> vertexLabels = new HashMap<>();

  /**
   * Default constructor.
   * 
   * @param vertices
   *          the vertices
   */
  public LabeledTree(List<V> vertices) {
    super(vertices);
    for (V v : vertices) {
      edgeLabels.put(v, new HashMap<V, LE>());
    }
  }

  @Override
  public boolean addEdge(E edge) {

    boolean success = super.addEdge(edge);

    if (success) {
      edgeLabels.get(edge.getFirstVertex()).put(edge.getSecondVertex(),
          edge.getLabel());
      edgeLabels.get(edge.getSecondVertex()).put(edge.getFirstVertex(),
          edge.getLabel());
    }

    return success;
  }

  @Override
  public void addVertex(V vertex) {
    super.addVertex(vertex);
    edgeLabels.put(vertex, new HashMap<V, LE>());
  }

  @Override
  public void addVertices(V[] vertexList) {
    for (V v : vertexList) {
      addVertex(v);
    }
  }

  @Override
  public Map<V, Map<V, LE>> getEdgeLabels() {
    return edgeLabels;
  }

  /**
   * Gets the label.
   * 
   * @param vertex
   *          the vertex
   * 
   * @return the label
   * 
   * @see org.jamesii.core.util.graph.ILabeledGraph#getLabel(java.lang.Object)
   */
  @Override
  public LV getLabel(V vertex) {
    return vertexLabels.get(vertex);
  }

  @Override
  public List<V> getVertexByLabel(LV vertexLabel) {
    throw new UnsupportedOperationException();
  }

  /**
   * Gets the vertex labels.
   * 
   * @return the vertex labels
   * 
   * @see org.jamesii.core.util.graph.ILabeledGraph#getVertexLabels()
   */
  @Override
  public Map<V, LV> getVertexLabels() {
    return vertexLabels;
  }

  @Override
  public boolean removeEdge(E edge) {

    boolean success = super.removeEdge(edge);

    if (success) {
      edgeLabels.get(edge.getFirstVertex()).remove(edge.getSecondVertex());
      edgeLabels.get(edge.getSecondVertex()).remove(edge.getFirstVertex());
    }

    return success;
  }

  @Override
  public boolean removeVertex(V vertex) {

    if (!super.removeVertex(vertex)) {
      return false;
    }

    vertexLabels.remove(vertex);
    edgeLabels.remove(vertex);

    return true;
  }

  @Override
  public boolean setLabel(V vertex, LV label) {

    boolean result = this.getVertices().contains(vertex);

    if (result) {
      vertexLabels.put(vertex, label);
    }

    return result;
  }

}

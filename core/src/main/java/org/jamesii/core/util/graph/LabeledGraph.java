/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Implements a labelled graph (with labels for both edges and vertices) for
 * general purposes.
 * 
 * Date: November 9, 2004
 * 
 * @param <V>
 *          the type of the vertex
 * @param <E>
 *          the type of the edge
 * @param <LV>
 *          the type of the vertex labels
 * @param <LE>
 *          the type of the edge labels
 * 
 * @author Roland Ewald
 */
public class LabeledGraph<V extends Comparable<V>, E extends LabeledEdge<V, LE>, LV, LE>
    extends Graph<V, E> implements ILabeledGraph<V, E, LV, LE> {

  /** Serialisation ID. */
  private static final long serialVersionUID = 1259991264845866979L;

  /** Array with the labels for the vertices. */
  private Map<V, LV> vertexLabels = new HashMap<>();

  /**
   * Default constructor.
   * 
   * @param vertices
   *          the vertices
   */
  public LabeledGraph(V[] vertices) {
    super(vertices);
  }

  /**
   * Complete Constructor.
   * 
   * @param vertices
   *          the vertices
   * @param isSimple
   *          flag that determines whether this graph is simple
   */
  public LabeledGraph(V[] vertices, boolean isSimple) {
    super(vertices);
    this.setSimple(isSimple);
  }

  /**
   * Adds the label.
   * 
   * @param returnMap
   *          the return map
   * @param vertex1
   *          the first vertex
   * @param vertex2
   *          the second vertex
   * @param label
   *          the label
   */
  void addLabel(Map<V, Map<V, LE>> returnMap, V vertex1, V vertex2, LE label) {

    Map<V, LE> map = returnMap.get(vertex1);
    if (map == null) {
      map = new HashMap<>();
      returnMap.put(vertex1, map);
    }
    map.put(vertex2, label);

    map = returnMap.get(vertex2);
    if (map == null) {
      map = new HashMap<>();
      returnMap.put(vertex2, map);
    }
    map.put(vertex1, label);
  }

  /**
   * Gets the edge labels.
   * 
   * @return the edge labels (ATTENTION: for each pair of vertices just *one*
   *         edge label, but this is stored in both combinations)
   */
  @Override
  public Map<V, Map<V, LE>> getEdgeLabels() {

    Map<V, Map<V, LE>> returnMap = new HashMap<>();

    for (Entry<V, Map<V, Collection<E>>> entry1 : getAdjacencyMap().entrySet()) {

      V key1 = entry1.getKey();

      Map<V, LE> map1 = returnMap.get(key1);

      if (map1 == null) {
        map1 = new HashMap<>();
        returnMap.put(key1, map1);
      }

      for (Entry<V, Collection<E>> entry2 : entry1.getValue().entrySet()) {
        E edgeWithLabel = entry2.getValue().iterator().next(); // .get(0);

        V key2 = entry2.getKey();

        Map<V, LE> map2 = returnMap.get(key2);
        if (map2 == null) {
          map2 = new HashMap<>();
          returnMap.put(key2, map2);
        }

        if (edgeWithLabel == null) {
          continue;
        }

        map1.put(key2, edgeWithLabel.getLabel());
        map2.put(key1, edgeWithLabel.getLabel());

      }
    }

    return returnMap;
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

  /**
   * Gets the vertex by label.
   * 
   * @param vertexLabel
   *          the vertex label
   * 
   * @return the vertex by label
   * 
   * @see org.jamesii.core.util.graph.ILabeledGraph#getVertexByLabel(java.lang.Object)
   */
  @Override
  public List<V> getVertexByLabel(LV vertexLabel) {

    List<V> verticesWithLabel = new ArrayList<>();

    for (Entry<V, LV> entry : vertexLabels.entrySet()) {
      if (entry.getValue().equals(vertexLabel)) {
        verticesWithLabel.add(entry.getKey());
      }
    }

    return verticesWithLabel;
  }

  /**
   * Gets the vertex labels.
   * 
   * @return Returns the vertexLabels.
   */
  @Override
  public Map<V, LV> getVertexLabels() {
    return vertexLabels;
  }

  @Override
  public boolean removeVertex(V vertex) {

    if (!super.removeVertex(vertex)) {
      return false;
    }

    // Removing vertex label
    return vertexLabels.remove(vertex) != null;
  }

  /**
   * Sets the label.
   * 
   * @param vertex
   *          the vertex
   * @param label
   *          the label
   * 
   * @return true, if sets the label
   * 
   * @see org.jamesii.core.util.graph.ILabeledGraph#setLabel(java.lang.Object,
   *      java.lang.Object)
   */
  @Override
  public boolean setLabel(V vertex, LV label) {
    if (!getAdjacencyMap().containsKey(vertex)) {
      return false;
    }
    vertexLabels.put(vertex, label);
    return true;
  }

}

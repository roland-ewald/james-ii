/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements an abstract basic graph (for general purposes, e. g.,
 * partitioning).
 * 
 * Date: August 12, 2005
 * 
 * @param <V>
 *          Type of the vertices
 * @param <E>
 *          Type of the edges
 * 
 * @author Jan Himmelspach
 * @author Roland Ewald
 */
public abstract class BasicGraph<V, E extends Edge<V>> implements IGraph<V, E>,
    Serializable {

  /** Serialisation ID. */
  private static final long serialVersionUID = 2390130388082636888L;

  /** Flag whether the graph is simple. */
  private boolean simple = false;

  @Override
  public void addVertices(V[] vertices) {
    for (V v : vertices) {
      this.addVertex(v);
    }
    return;
  }

  /**
   * Gets the adjacency lists.
   * 
   * @return Returns the adjacency matrix as list of adjacency lists.
   */
  @Override
  public List<List<V>> getAdjacencyLists() {
    List<V> vertices = getVertices();
    ArrayList<List<V>> adjacencyLists = new ArrayList<>();
    for (V v : vertices) {
      adjacencyLists.add(getNeighboursOfNode(v));
    }
    return adjacencyLists;
  }

  /**
   * Gets the adjacency matrix.
   * 
   * @return Returns the adjacency node matrix as integers
   */
  @Override
  public NodeMatrix<V, Integer> getAdjacencyMatrix() {
    NodeMatrix<V, Integer> result = new NodeMatrix<>(getVertices(), 0);
    List<V> vertices = getVertices();
    for (V v1 : vertices) {
      for (V v2 : vertices) {
        result.setValue(v1, v2, hasEdge(v1, v2) ? 1 : 0);
      }
    }
    return result;
  }

  @Override
  public boolean isSimple() {
    return simple;
  }

  @Override
  public boolean removeVertices(V[] vertices) {
    boolean success = true;

    for (V v : vertices) {
      if (!this.removeVertex(v)) {
        success = false;
      }
    }

    return success;
  }

  /**
   * Sets flag simple whether graph is simple.
   * 
   * @param simple
   *          flag to mark the graph as simple
   */
  public void setSimple(boolean simple) {
    this.simple = simple;
  }

}
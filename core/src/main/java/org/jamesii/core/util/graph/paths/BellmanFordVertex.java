/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph.paths;

/**
 * This type of vertex is used in the Bellman-Ford algorithm. It adds a
 * predecessor "pred" and a distance "dist" to a vertex.
 * 
 * @param <V>
 *          type of vertices
 * @author Nico Eggert
 */
public class BellmanFordVertex<V> {

  /** The predecessor. */
  private double pred;

  /** The distance. */
  private double dist;

  /** The vertex. */
  private V vertex = null;

  /**
   * The Constructor.
   * 
   * @param v
   *          the vertex
   */
  public BellmanFordVertex(V v) {
    vertex = v;
  }

  /**
   * Gets the distance.
   * 
   * @return the distance
   */
  public double getDist() {
    return dist;
  }

  /**
   * Gets the predecessor.
   * 
   * @return the predecessor
   */
  public double getPred() {
    return pred;
  }

  /**
   * Gets the v.
   * 
   * @return the vertex
   */
  public V getV() {
    return vertex;
  }

  /**
   * Sets the distance.
   * 
   * @param d
   *          the distance
   */
  public void setDist(double d) {
    dist = d;
  }

  /**
   * Sets the predecessor.
   * 
   * @param p
   *          the predecessor
   */
  public void setPred(double p) {
    pred = p;
  }

  /**
   * Sets the vertex.
   * 
   * @param v
   *          the vertex
   */
  public void setV(V v) {
    vertex = v;
  }

}
